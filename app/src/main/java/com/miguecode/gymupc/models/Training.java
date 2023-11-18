package com.miguecode.gymupc.models;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class Training implements Serializable {
    private String id;
    private String coach;
    private String code;
    private Date dateInit;
    private Date dateEnd;
    private boolean finished;

    public Training() {
    }

    public Training(String id, String coach, Date dateInit, Date dateEnd, boolean finished, String code) {
        this.id = id;
        this.coach = coach;
        this.dateInit = dateInit;
        this.dateEnd = dateEnd;
        this.finished = finished;
        this.code = code;
    }

    public Training(String coach, Date dateInit, Date dateEnd, boolean finished, String code) {
        UUID uuid = UUID.randomUUID();
        this.id = uuid.toString();
        this.coach = coach;
        this.dateInit = dateInit;
        this.dateEnd = dateEnd;
        this.finished = finished;
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCoach() {
        return coach;
    }

    public void setCoach(String coach) {
        this.coach = coach;
    }

    public Date getDateInit() {
        return dateInit;
    }

    public void setDateInit(Date dateInit) {
        this.dateInit = dateInit;
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(Date dateEnd) {
        this.dateEnd = dateEnd;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
