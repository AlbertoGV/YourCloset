package com.example.albertogv.yourcloset.model;

import android.net.Uri;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class Mensaje {

    public String uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPhotoUser() {
        return photoUser;
    }

    public void setPhotoUser(String photoUser) {
        this.photoUser = photoUser;
    }

    public String getNameBuyer() {
        return nameBuyer;
    }

    public void setNameBuyer(String nameBuyer) {
        this.nameBuyer = nameBuyer;
    }

    public String getPhotoSeller() {
        return photoSeller;
    }

    public void setPhotoSeller(String photoSeller) {
        this.photoSeller = photoSeller;
    }

    public Object getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Object createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public String message;
    public String photoUser;
    public String nameBuyer;
    public String photoSeller;
    public Object createdTimestamp;


    public Mensaje(){

    }

    public Mensaje(String uid, String message, String photoUser, String nameBuyer, String photoSeller, Object createdTimestamp) {
        this.uid = uid;
        this.message = message;
        this.photoUser = photoUser;
        this.nameBuyer = nameBuyer;
        this.photoSeller = photoSeller;
        this.createdTimestamp = ServerValue.TIMESTAMP;


    }
    @Exclude
    public long getCreatedTimestampLong() {
        return (long) createdTimestamp;
    }

}