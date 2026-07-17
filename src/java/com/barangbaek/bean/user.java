package com.barangbaek.bean;

public class user {

    private int userID;
    private String username;
    private String fullName;
    private String birthday;
    private String gender;
    private String userPhoto;
    private String email;
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String postcode;
    private String phone;
    private String university;
    private String password;

    public user() {
    }

    // Normal Constructor
    public user(int userID, String username, String fullName, String birthday, String gender, String userPhoto, String email,
            String address1, String address2, String city, String state, String postcode, String phone, String university, String password) {

        this.userID = userID;
        this.username = username;
        this.fullName = fullName;
        this.birthday = birthday;
        this.gender = gender;
        this.userPhoto = userPhoto;
        this.email = email;
        this.address1 = address1;
        this.address2 = address2;
        this.city = city;
        this.state = state;
        this.postcode = postcode;
        this.phone = phone;
        this.university = university;
        this.password = password;
    }

    // Getters
    public int getUserID() {
        return userID;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getGender() {
        return gender;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress1() {
        return address1;
    }

    public String getAddress2() {
        return address2;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getPostcode() {
        return postcode;
    }

    public String getPhone() {
        return phone;
    }

    public String getUniversity() {
        return university;
    }

    public String getPassword() {
        return password;
    }

    // Setters
    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
