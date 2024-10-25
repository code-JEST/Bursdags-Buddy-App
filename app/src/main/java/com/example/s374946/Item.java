package com.example.s374946;

public class Item {
    private int id;
    private String name;
    private String phone;
    private String birthdate;

    public Item(int id, String name, String phone, String birthdate) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.birthdate = birthdate;
    }
    public int getId() {
        return id;
    }
    public String getName() {
        return this.name;
    }
    public String getPhone() {
        return phone;
    }

    public String getBirthdate() {
        return birthdate;
    }
}
