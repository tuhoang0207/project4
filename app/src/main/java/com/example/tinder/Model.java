package com.example.tinder;

public class Model {
    private String userId;
    private String name;
    private String phone;
    private String imageUrl;

    public Model() {

    }

    public Model(String imageUrl) {
        this.imageUrl = imageUrl;
    }

//    public Model(String userId, String name, String phone, String imageUrl) {
//        this.userId = userId;
//        this.name = name;
//        this.phone = phone;
//        this.imageUrl = imageUrl;
//    }

    public Model(String userId,String name, String phone, String imageUrl) {
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.imageUrl = imageUrl;
    }

    public Model(String name, String phone, String imageUrl) {
        this.name = name;
        this.phone = phone;
        this.imageUrl = imageUrl;
    }

//    public Model(String userId, String name) {
//        this.userId = userId;
//        this.name = name;
//    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
