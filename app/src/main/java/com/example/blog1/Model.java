package com.example.blog1;

public class Model {
    private String title;
    private String desc;
    private String image;
    private String username;
    private String phonenumber;
    private String excPrice;

    public Model() {
    }


    public Model(String title, String desc, String image, String username, String phonenumber,String excPrice) {
        this.title = title;
        this.desc = desc;
        this.image = image;
        this.username = username;
        this.phonenumber = phonenumber;
        this.excPrice=excPrice;
    }

    public String getExcPrice() {
        return excPrice;
    }

    public void setExcPrice(String excPrice) {
        this.excPrice = excPrice;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}