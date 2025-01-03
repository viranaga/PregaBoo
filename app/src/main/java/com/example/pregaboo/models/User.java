package com.example.pregaboo.models;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    private String id;
    private String name;
    private String email;
    private String location;
    private String contact;

    // Required empty constructor for Firestore
    public User() {}

    public User(String id, String name, String email, String location, String contact) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.location = location;
        this.contact = contact;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
} 