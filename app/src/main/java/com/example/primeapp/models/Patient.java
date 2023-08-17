package com.example.primeapp.models;

import java.io.Serializable;
import java.util.Date;

public class Patient implements Serializable {

    public Patient(String id, String familyName, String givenName,String birthDate, String mrn) {
        this.patientId = id;
        this.mrn =mrn;
        this.date=birthDate;
        this.familyName=familyName;
        this.givenName=givenName;

    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String date;

    public String getPatientId() {
        return patientId;
    }


    public String patientId;
    public String familyName;
    public String givenName;
    public String mrn;

    public String getName() {
        return familyName+ " "+givenName;
    }

    public String getMRN() {
        return mrn;
    }



}
