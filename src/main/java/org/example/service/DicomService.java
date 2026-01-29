package org.example.service;

import org.example.model.DicomField;
import org.example.model.DicomMetadata;
import org.example.util.DicomUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DicomService {


    public List<String> updateAccessionNumber(File folder, String value) {
        return updateField(folder, DicomField.ACCESSION_NUMBER, value);
    }

    public List<String> updatePatientId(File folder, String value) {
        return updateField(folder, DicomField.PATIENT_ID, value);
    }

//    Actualizamos la imagen con el valor que queremos y el tag
    public List<String> updateField(File folder, DicomField field, String value) {

        List<String> logs = new ArrayList<>();

        if (folder == null || !folder.isDirectory()) {
            logs.add("Esta carpeta es invalida");
            return logs;
        }

        File[] files = folder.listFiles();
        if (files == null) {
            logs.add("La carpeta esta vacia");
            return logs;
        }

        for (File file : files) {
            if (!file.isFile()) continue;
            try {
                if (!DicomUtils.isDicom(file)) continue;
//                Hacemos backeup del archivo original antes de manipularlo
                BackupService.backup(file);
                DicomMetadata metadata = DicomUtils.readMetadata(file);

                if (!needsUpdate(metadata, field, value)) {
                    logs.add("No se ejecuto ningun cambio " + file.getName());
                    continue;
                }

                applyChange(metadata, field, value);
                DicomUtils.writeMetadata(file, metadata);

                logs.add(field + " actualizado en " + file.getName());

            } catch (Exception e) {
                logs.add(" Ha ocurrido un error " + file.getName() + ": " + e.getMessage());
            }
        }

        return logs;
    }

    private boolean needsUpdate(DicomMetadata metadata, DicomField field, String newValue) {

        String currentValue = "";

        switch (field) {
            case ACCESSION_NUMBER:
                currentValue = metadata.getAccessionNumber();
                break;
            case PATIENT_ID:
                currentValue = metadata.getPatientId();
                break;
        }

        return Objects.isNull(currentValue) || !currentValue.equals(newValue);
    }

    private void applyChange(DicomMetadata metadata, DicomField field, String value) {

        switch (field) {
            case ACCESSION_NUMBER:
                metadata.setAccessionNumber(value);
                break;
            case PATIENT_ID:
                metadata.setPatientId(value);
                break;
        }

    }
}
