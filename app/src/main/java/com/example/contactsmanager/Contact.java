package com.example.contactsmanager;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Contact {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private String phoneNumber;
    private String picture;
    private String emailAdr;
    private String homeAdr;
    private String websiteAdr;
    private String birthDate;
    private String callTime;
    private String callDays;

    public Contact(String name, String phoneNumber, String picture, String emailAdr, String homeAdr,String websiteAdr, String birthDate, String callTime, String callDays) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.picture = picture;
        this.emailAdr = emailAdr;
        this.homeAdr = homeAdr;
        this.websiteAdr = websiteAdr;
        this.birthDate = birthDate;
        this.callTime = callTime;
        this.callDays = callDays;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPicture() {
        return picture;
    }
    public String getEmailAdr() {
        return emailAdr;
    }

    public String getHomeAdr() {
        return homeAdr;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getCallTime() {
        return callTime;
    }

    public String getCallDays() {
        return callDays;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setEmailAdr(String emailAdr) {
        this.emailAdr = emailAdr;
    }

    public void setHomeAdr(String homeAdr) {
        this.homeAdr = homeAdr;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public void setCallTime(String callTime) {
        this.callTime = callTime;
    }

    public void setCallDays(String callDays) {
        this.callDays = callDays;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWebsiteAdr() {
        return websiteAdr;
    }
    public void setWebsiteAdr(String websiteAdr) {
        this.websiteAdr = websiteAdr;
    }
}
