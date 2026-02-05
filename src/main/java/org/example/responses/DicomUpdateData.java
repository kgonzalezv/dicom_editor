package org.example.responses;

import javax.swing.*;

public record DicomUpdateData(String accession,
                              String patientId,
                              String patientName,
                              String studyInstanceUID,
                              String randomTagValue,
                              String randomTagName) {


    public static DicomUpdateData fromFields(
            JTextField accessionField,
            JTextField patientIdField,
            JTextField patientNameField,
            JTextField studyInstanceUIDField,
            JTextField randomTagValueField,
            JTextField randomTagNameField){

        return new DicomUpdateData(
                accessionField.getText().trim(),
                patientIdField.getText().trim(),
                patientNameField.getText().trim(),
                studyInstanceUIDField.getText().trim(),
                randomTagValueField.getText().trim(),
                randomTagNameField.getText().trim());
    }
}

