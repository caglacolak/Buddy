package com.example.asus.buddy;

/**
 * Created by ASUS on 19.12.2017.
 */

public class UserEdit {

    String Address;
    String Birthdate;
    String Country;
    String Email;
    String Id;
    String ImageUrlPath;
    String IsActive;
    String Name;
    String PhoneNumber;
    String RegisteredTime;
    String Surname;

    public UserEdit() {
    }

    public UserEdit(String address, String birthdate, String country, String email, String id, String imageUrlPath, String isActive, String name, String phoneNumber, String registeredTime, String surname) {
        Address = address;
        Birthdate = birthdate;
        Country = country;
        Email = email;
        Id = id;
        ImageUrlPath = imageUrlPath;
        IsActive = isActive;
        Name = name;
        PhoneNumber = phoneNumber;
        RegisteredTime = registeredTime;
        Surname = surname;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public void setBirthdate(String birthdate) {
        Birthdate = birthdate;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setId(String id) {
        Id = id;
    }

    public void setImageUrlPath(String imageUrlPath) {
        ImageUrlPath = imageUrlPath;
    }

    public void setIsActive(String isActive) {
        IsActive = isActive;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public void setRegisteredTime(String registeredTime) {
        RegisteredTime = registeredTime;
    }

    public void setSurname(String surname) {
        Surname = surname;
    }

    public String getAddress() {
        return Address;
    }

    public String getBirthdate() {
        return Birthdate;
    }

    public String getCountry() {
        return Country;
    }

    public String getEmail() {
        return Email;
    }

    public String getId() {
        return Id;
    }

    public String getImageUrlPath() {
        return ImageUrlPath;
    }

    public String getIsActive() {
        return IsActive;
    }

    public String getName() {
        return Name;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public String getRegisteredTime() {
        return RegisteredTime;
    }

    public String getSurname() {
        return Surname;
    }
}
