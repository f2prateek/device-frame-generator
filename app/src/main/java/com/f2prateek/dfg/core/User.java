package com.f2prateek.dfg.core;

import java.io.Serializable;

public class User implements Serializable {


    private static final long serialVersionUID = -7495897652017488896L;

    protected String firstName;
    protected String lastName;
    protected String username;
    protected String phone;
    protected String objectId;
    protected String sessionToken;
    protected String gravatarId;
    protected String avatarUrl;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGravatarId() {
        return gravatarId;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
}
