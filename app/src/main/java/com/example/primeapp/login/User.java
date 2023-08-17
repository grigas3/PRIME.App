package com.example.primeapp.login;

/**
 * This is very simple class and it only contains the user attributes and JWToken
 * a constructor and the getters
 */
public class User {


    private String username, expiresAt, token, patientID, refreshtoken, apikey, insoleID;

    public User(String username, String expiresAt, String token, String patientID) {

        this.username = username;
        this.expiresAt = expiresAt;
        this.token = token;
        this.patientID = patientID;


    }




    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setInsoleID(String insoleID) {
        this.insoleID = insoleID;
    }

    public String getPatientID() {
        return patientID;
    }

    public void setPatientID(String patientID) {
        this.patientID = patientID;
    }

    public String getRefreshtoken() {
        return refreshtoken;
    }

    public void setRefreshtoken(String refreshtoken) {
        this.refreshtoken = refreshtoken;
    }

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }


    public String getInsoleID() { return insoleID;
    }
}