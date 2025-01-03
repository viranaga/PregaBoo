package com.example.pregaboo.models;

// Rename the class to avoid conflict with Android's DatePicker widget
public class DateModel {
    private int day;
    private int month;
    private int year;

    // Constructor
    public DateModel() {}

    // Setters
    public void setDate(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    // Getters
    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    // Method to get a formatted date string
    public String getFormattedDate() {
        return day + "/" + (month + 1) + "/" + year; // Month is 0-indexed
    }
}
