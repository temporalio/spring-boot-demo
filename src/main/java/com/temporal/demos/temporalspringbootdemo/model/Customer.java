package com.temporal.demos.temporalspringbootdemo.model;

public class Customer {
    private String name;
    private String role;
    private String milestone;

    public Customer() {}

    public Customer(String name, String role, String milestone) {
        this.name = name;
        this.role = role;
        this.milestone = milestone;
    }

    public String getName() {
        return this.name;
    }

    public String getRole() {
        return this.role;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getMilestone() {
        return milestone;
    }

    public void setMilestone(String milestone) {
        this.milestone = milestone;
    }
}
