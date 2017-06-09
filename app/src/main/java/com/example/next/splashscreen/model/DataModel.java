package com.example.next.splashscreen.model;

/**
 * Created by next on 16/2/17.
 */
public class DataModel {
    private int code;
    private int sno;
    private String message;
    private String data;
    private String userid;
    private String title;
    private String time;


    public DataModel() {
    }

    public DataModel(int code, int sno, String message, String data, String userid, String title, String time) {
        this.code = code;
        this.sno = sno;
        this.message = message;
        this.data = data;
        this.userid = userid;
        this.title = title;
        this.time = time;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getSno() {
        return sno;
    }

    public void setSno(int sno) {
        this.sno = sno;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
