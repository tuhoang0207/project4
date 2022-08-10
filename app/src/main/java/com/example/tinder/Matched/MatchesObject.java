package com.example.tinder.Matched;

public class MatchesObject {
    private String userId;
    private String name;
    private String imageUrl;
//    private String sex;

    //    public MatchesObject(String userId, String name, String imageUrl,String sex) {
//        this.userId = userId;
//        this.name = name;
//        this.imageUrl = imageUrl;
//        this.sex = sex;
//    }
    public MatchesObject(String userId, String name, String imageUrl) {
        this.userId = userId;
        this.name = name;
        this.imageUrl = imageUrl;
    }

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

//    public String getSex() {
//        return sex;
//    }
//
//    public void setSex(String sex) {
//        this.sex = sex;
//    }
}
