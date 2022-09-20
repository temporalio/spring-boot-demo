package com.temporal.demos.temporalspringbootdemo.model;

import java.util.ArrayList;
import java.util.List;

public class Customer {
    private String id;
    private String name;
    private String role;
    private List<String> milestones = new ArrayList<>();
    private String onboarded;

    public Customer() {}

    public Customer(String id, String name, String role) {
        this.id = id;
        this.name = name;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<String> getMilestones() {
        return milestones;
    }

    public void setMilestones(List<String> milestones) {
        this.milestones = milestones;
    }

    public String getOnboarded() {
        return onboarded;
    }

    public void setOnboarded(String onboarded) {
        this.onboarded = onboarded;
    }
}
