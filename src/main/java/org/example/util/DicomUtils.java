package org.example.util;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.data.VR;
import org.dcm4che3.io.DicomInputStream;
import org.dcm4che3.io.DicomOutputStream;
import org.example.model.DicomMetadata;
import org.example.service.BackupService;

import java.io.File;
import java.util.Objects;

public class DicomUtils {


    public static DicomMetadata readMetadata(File dicomFile) throws Exception {

        Attributes attrs;
        try (DicomInputStream dis = new DicomInputStream(dicomFile)) {
            attrs = dis.readDataset(-1, -1);
        }

        DicomMetadata meta = new DicomMetadata();
        meta.setAccessionNumber(attrs.getString(Tag.AccessionNumber));
        meta.setPatientId(attrs.getString(Tag.PatientID));

        return meta;
    }

    public static boolean isDicom(File file) {
        try (DicomInputStream dis = new DicomInputStream(file)) {
            dis.readFileMetaInformation();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void writeMetadata(File dicomFile, DicomMetadata meta) throws Exception {
        Attributes fmi;
        Attributes attrs;

        try (DicomInputStream dis = new DicomInputStream(dicomFile)) {
            fmi = dis.readFileMetaInformation();
            attrs = dis.readDataset(-1, -1);
        }

        byte[] pixelBytes = attrs.getSafeBytes(Tag.PixelData);

        if (Objects.nonNull(meta.getAccessionNumber())) {
            attrs.setString(Tag.AccessionNumber, VR.SH, meta.getAccessionNumber());
        }

        if (Objects.nonNull(meta.getPatientId())) {
            attrs.setString(Tag.PatientID, VR.LO, meta.getPatientId());
        }

        if (Objects.nonNull(pixelBytes)) {
            attrs.setBytes(Tag.PixelData, VR.OB, pixelBytes);
        }

        try (DicomOutputStream dos = new DicomOutputStream(dicomFile)) {
            String tsuid = fmi != null ? fmi.getString(Tag.TransferSyntaxUID) : UID.ImplicitVRLittleEndian;
            Attributes safeFMI = attrs.createFileMetaInformation(tsuid);

            dos.writeDataset(safeFMI, attrs);
        }
    }

}

