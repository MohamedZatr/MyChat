package com.example.mohamedramadan.mychat;

public class Users {
    private String user_name;
    private String user_image;
    private String user_statue;
    private String user_thumb_image;

    public Users()
    {

    }
    public Users(String user_name, String user_image, String user_statue, String user_thumb_image) {
        this.user_name = user_name;
        this.user_image = user_image;
        this.user_statue = user_statue;
        this.user_thumb_image = user_thumb_image;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getUser_statue() {
        return user_statue;
    }

    public void setUser_statue(String user_statue) {
        this.user_statue = user_statue;
    }

    public String getUser_thumb_image() {
        return user_thumb_image;
    }

    public void setUser_thumb_image(String user_thumb_image) {
        this.user_thumb_image = user_thumb_image;
    }
}
