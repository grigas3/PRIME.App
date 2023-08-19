package com.example.primeapp.models;

import java.io.Serializable;
import java.util.Date;

public class Observation implements Serializable {

    public Observation(String code, double value) {

        this.code = code;
        this.value = value;
        this.date=new Date();
    }
    public Observation(String code, double value,String units) {
        this.units=units;
        this.code = code;
        this.value = value;
        this.date=new Date();
    }

    public Observation(String code, double value,String units,String patientId) {
        this.code = code;
        this.units=units;
        this.value = value;
        this.patientId=patientId;
        this.date=new Date();

    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date date;

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String patientId;
    public String code;
    public String units;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    public double value;

    public double getValue() {
        return value;
    }
    public String getUnits() {
        return units;
    }
    public void setValue(double value) {
        this.value = value;
    }
}



