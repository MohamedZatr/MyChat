package com.example.mohamedramadan.mychat;

public class Request {
  private   String user_name,user_statue, user_thumb_image;

  public  Request(){}
    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
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
