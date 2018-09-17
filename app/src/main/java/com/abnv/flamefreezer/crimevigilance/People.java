package com.abnv.flamefreezer.crimevigilance;

/**
 * Created by Abhishek Abhinav on 15-07-2017.
 */

public class People {
    String name, age, height, last_seen, image, appearance, gender,contact_no;

    public People() {
    }

    public People(String name, String age, String height, String last_seen,
                  String image, String appearance, String gender, String contact_no) {
        this.image = image;
        this.age = age;
        this.name = name;
        this.appearance = appearance;
        this.gender = gender;
        this.height = height;
        this.last_seen = last_seen;
        this.contact_no= contact_no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getLast_seen() {
        return last_seen;
    }

    public void setLast_seen(String last_seen) {
        this.last_seen = last_seen;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAppearance() {
        return appearance;
    }

    public void setAppearance(String appearance) {
        this.appearance = appearance;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getContact_no() {
        return contact_no;
    }

    public void setContact_no(String contact_no) {
        this.contact_no= contact_no;
    }
}