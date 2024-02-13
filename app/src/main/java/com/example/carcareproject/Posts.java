package com.example.carcareproject;



import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Posts {
    private long timestamp;
    public String uid, time, date, company_na, description, post_img, profile_img;

    public Posts(){

    }

    public Posts(String uid, String time, String date, String company_na, String description, String post_img, String profile_img) {
        this.uid = uid;
        this.time = time;
        this.date = date;
        this.company_na = company_na;
        this.description = description;
        this.post_img = post_img;
        this.profile_img = profile_img;

        // Convertendo time e date em um timestamp
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date postDate = dateFormat.parse(date + " " + time);
            this.timestamp = postDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCompany_na() {
        return company_na;
    }

    public void setCompany_na(String company_na) {
        this.company_na = company_na;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPost_img() {
        return post_img;
    }

    public void setPost_img(String post_img) {
        this.post_img = post_img;
    }

    public String getProfile_img() {
        return profile_img;
    }

    public void setProfile_img(String profile_img) {
        this.profile_img = profile_img;
    }
}
