package com.miguecode.gymupc.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class Schedule implements Serializable {
    private String id;
    private String date;
    private String hour;
    private String shortDate;
    private String idUser;
    private ArrayList<String> exercises;

    private boolean went;

    public Schedule() {
    }

    public Schedule(String id, String date, String hour, String idUser, ArrayList<String> exercises, boolean went, String shortDate) {
        this.id = id;
        this.date = date;
        this.hour = hour;
        this.idUser = idUser;
        this.exercises = exercises;
        this.went = went;
        this.shortDate = shortDate;
    }

    public Schedule(String date, String hour, String idUser, ArrayList<String> exercises, boolean went, String shortDate) {
        UUID uuid = UUID.randomUUID();
        this.id = uuid.toString();
        this.date = date;
        this.hour = hour;
        this.idUser = idUser;
        this.exercises = exercises;
        this.went = went;
        this.shortDate = shortDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public ArrayList<String> getExercises() {
        return exercises;
    }

    public void setExercises(ArrayList<String> exercises) {
        this.exercises = exercises;
    }

    public boolean isWent() {
        return went;
    }

    public void setWent(boolean went) {
        this.went = went;
    }

    public String getShortDate() {
        return shortDate;
    }

    public void setShortDate(String shortDate) {
        this.shortDate = shortDate;
    }
}
