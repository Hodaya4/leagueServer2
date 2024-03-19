package com.ashcollege.entities;

import com.github.javafaker.Faker;

import java.util.Random;

public class Team {
    private String name;
    private int points;
    private int goalsFor;
    private int goalsAgainst;
    private float skills;

    // Constructor
    public Team() {
        this.name = generateRandomName();
        this.points = 0;
        this.goalsFor = 0;
        this.goalsAgainst = 0;
        this.skills = generateRandomSkills();
    }

    // Generate random name
    private String generateRandomName() {
        Faker faker = new Faker();
        return faker.address().cityName();
    }

    private float generateRandomSkills() {
        return (float) (Math.random() * 40 + 60); // Generates a random float between 60 and 100
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getGoalsFor() {
        return goalsFor;
    }

    public void setGoalsFor(int goalsFor) {
        this.goalsFor = goalsFor;
    }

    public int getGoalsAgainst() {
        return goalsAgainst;
    }

    public void setGoalsAgainst(int goalsAgainst) {
        this.goalsAgainst = goalsAgainst;
    }

    public float getSkills() {
        return skills;
    }

    public void setSkills(float skills) {
        this.skills = skills;
    }
}

