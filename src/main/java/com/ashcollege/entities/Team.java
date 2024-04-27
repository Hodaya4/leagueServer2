package com.ashcollege.entities;

import com.github.javafaker.Faker;

import java.util.Arrays;

public class Team {
    private String name;
    private int id;
    private int points;
    private int goalsFor;
    private int goalsAgainst;
    private float skills;
    private int numInjuredPlayers;
    private int numRedCards;
    private int isWinLastMatch;
    private final String[] lastGames = {"-", "-", "-"};

    // Constructor
    public Team(int id) {
        this.name = generateRandomName();
        this.points = 0;
        this.goalsFor = 0;
        this.goalsAgainst = 0;
        this.skills = generateRandomSkills();
        this.id = id;
        this.numInjuredPlayers = 0;
        this.numRedCards = 0;
        this.isWinLastMatch = 0;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumInjuredPlayers() {
        return numInjuredPlayers;
    }

    public void setNumInjuredPlayers(int numInjuredPlayers) {
        if ((this.numInjuredPlayers + numInjuredPlayers) < 0) {
            this.numInjuredPlayers = 0;
        } else {
            this.numInjuredPlayers += numInjuredPlayers;
        }
    }

    public int getNumRedCards() {
        return numRedCards;
    }

    public void setNumRedCards(int numRedCards) {
        if ((this.numRedCards + numRedCards) < 0) {
            this.numRedCards = 0;
        } else {
            this.numRedCards += numRedCards;
        }
    }

    public int getIsWinLastMatch() {
        return isWinLastMatch;
    }

    public void setIsWinLastMatch(int isWinLastMatch) {
        this.isWinLastMatch = isWinLastMatch;
    }

    public void setWinLastMatch(int winLastMatch) {
        isWinLastMatch += winLastMatch;
    }

    public String[] getLastGames() {
        return lastGames;
    }

    public void setLastGames(String result) {
        for (int i = lastGames.length - 1; i > 0; i--) {
            lastGames[i] = lastGames[i - 1];
        }
        lastGames[0] = result;
    }

    @Override
    public String toString() {
        return "Team{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", points=" + points +
                ", goalsFor=" + goalsFor +
                ", goalsAgainst=" + goalsAgainst +
                ", skills=" + skills +
                ", numInjuredPlayers=" + numInjuredPlayers +
                ", numRedCards=" + numRedCards +
                ", isWinLastMatch=" + isWinLastMatch +
                ", lastGames=" + Arrays.toString(lastGames) +
                '}';
    }
}
