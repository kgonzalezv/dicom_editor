package org.example.model;

public class DicomMetadata {

    private String accessionNumber;
    private String patientId;
    private String patientName;
    private String studyInstanceUID;
    private Object randomTagValue;

    public Object getRandomTagValue() {
        return randomTagValue;
    }

    public void setRandomTagValue(Object randomTag) {
        this.randomTagValue = randomTag;
    }

    public String getStudyInstanceUID() {
        return studyInstanceUID;
    }

    public void setStudyInstanceUID(String studyInstanceUID) {
        this.studyInstanceUID = studyInstanceUID;
    }

    public String getAccessionNumber() {
        return accessionNumber;
    }

    public void setAccessionNumber(String accessionNumber) {
        this.accessionNumber = accessionNumber;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }
}
