package com.example.primeapp.login;

/**
 * This is very simple class and it only contains the user attributes and JWToken
 * a constructor and the getters
 */
public class Session {

    private int id;
    private String sessionid;

    public Session(int id, String sessionid ) {
        this.id = id;
        this.sessionid = sessionid;


    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSessionid() {
        return sessionid;
    }

    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }
}