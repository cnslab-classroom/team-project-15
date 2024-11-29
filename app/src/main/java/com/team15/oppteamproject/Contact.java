package com.team15.oppteamproject;

public class Contact {
    private String name;
    private String phone;

    public Contact() {
        // Firebase에서 자동으로 객체를 생성할 때 사용
    }

    public Contact(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
}