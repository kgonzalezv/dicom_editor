package org.example.util;

import org.dcm4che3.data.*;
import org.dcm4che3.io.DicomInputStream;
import org.dcm4che3.io.DicomOutputStream;
import org.example.model.DicomMetadata;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

public class DicomUtils {

    private static Attributes attrs;
    private static Attributes fmi;
    private static int tagRandom;
    private static VR vrRandom;
    private static Object tagRandomValue;


    // Leemos los metadatos DICOM
    public static DicomMetadata readMetadata(File dicomFile) throws Exception {

        try (DicomInputStream dis = new DicomInputStream(dicomFile)) {
            dis.setIncludeBulkData(DicomInputStream.IncludeBulkData.URI);
            dis.readFileMetaInformation();
            attrs = dis.readDataset(-1, -1);
        }

        DicomMetadata meta = new DicomMetadata();
        meta.setAccessionNumber(attrs.getString(Tag.AccessionNumber));
        meta.setPatientId(attrs.getString(Tag.PatientID));
        meta.setPatientName(attrs.getString(Tag.PatientName));
        meta.setStudyInstanceUID(attrs.getString(Tag.StudyInstanceUID));
        meta.setRandomTagValue(attrs.getString(tagRandom));

        return meta;
    }

    // Configuramos el tag y valor random a modificar
    public static void setTagRandom(String tagName, Object tagValue) {

        if (!tagName.isEmpty()) {
            int tag = ElementDictionary.tagForKeyword(tagName, null);

            if (tag == -1) {
                throw new IllegalArgumentException("Tag no reconocido: " + tagName);
            }

            VR vr = ElementDictionary.getStandardElementDictionary().vrOf(tag);
            tagRandom = tag;
            vrRandom = vr;
            tagRandomValue = tagValue;
        }

    }

    // Verificamos si el archivo es DICOM
    public static boolean isDicom(File file) {
        try (DicomInputStream dis = new DicomInputStream(file)) {
            dis.readFileMetaInformation();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void writeMetadata(File dicomFile, DicomMetadata meta) throws Exception {

        // Leemos el DICOM original
        try (DicomInputStream dis = new DicomInputStream(dicomFile)) {
            dis.setIncludeBulkData(DicomInputStream.IncludeBulkData.URI);
            fmi = dis.readFileMetaInformation();
            attrs = dis.readDataset(-1, -1);
        }

        // Actualizamos los campos
        setTag(Tag.AccessionNumber, VR.SH, meta.getAccessionNumber());
        setTag(Tag.PatientID, VR.LO, meta.getPatientId());
        setTag(Tag.PatientName, VR.PN, meta.getPatientName());
        setTag(Tag.StudyInstanceUID, VR.UI, meta.getStudyInstanceUID());
        setTag(tagRandom, vrRandom, tagRandomValue);


        // Archivo temporal
        File tmp = new File(dicomFile.getParent(), dicomFile.getName() + ".tmp");

            // Escribimos en un archivo temporal
        try (DicomOutputStream dos = new DicomOutputStream(tmp)) {
            String tsuid = fmi != null ? fmi.getString(Tag.TransferSyntaxUID) : UID.ImplicitVRLittleEndian;
            Attributes safeFMI = attrs.createFileMetaInformation(tsuid);

            dos.writeDataset(safeFMI, attrs);
        }

        // Reemplazo final
        Files.move(
                tmp.toPath(),
                dicomFile.toPath(),
                StandardCopyOption.REPLACE_EXISTING
        );
    }

    private static void setTag(int tag, VR vr, Object value) {
        if (value == null) return;

        if (value instanceof String s) {
            attrs.setString(tag, vr, s);
        } else {
            attrs.setValue(tag, vr, value);
        }
    }
}

