package com.mrbell.photoblog;


import java.util.Date;

public class BlogPost {

    public String user_id,image, desp;

    public Date timestamp;





    public BlogPost() {
    }

    public BlogPost(String user_id, String image, String desp,Date timestamp) {
        this.user_id = user_id;
        this.image = image;
        this.desp = desp;
        this.timestamp = timestamp;
    }


    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDesp() {
        return desp;
    }

    public void setDesp(String desp) {
        this.desp = desp;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
