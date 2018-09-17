package com.abnv.flamefreezer.crimevigilance;

/**
 * Created by Abhishek Abhinav on 15-07-2017.
 */

public class Blog {
    String image,title,desc,zipCode,city,contact,status;

    public Blog() {
    }

    public Blog(String image, String title, String desc, String zipCode, String city, String contact, String status) {
        this.image = image;
        this.title = title;     this.status= status;

        this.desc = desc;       this.contact= contact;
        this.zipCode= zipCode;  this.city= city;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
