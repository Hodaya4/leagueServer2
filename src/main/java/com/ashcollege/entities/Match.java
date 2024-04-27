package com.ashcollege.entities;

public class Match {
    private int round;
    private int match;
    private String homeTeam;
    private int homeTeamId;
    private int homeGoals;
    private float homeSkills;
    private double homeOdd;
    private String awayTeam;
    private int awayTeamId;
    private int awayGoals;
    private float awaySkills;
    private double awayOdd;
    private double drawOdd;
    private int result;
    private float weatherFactor;
    public Match(int round, int match, String homeTeam, int homeId, int awayId, String awayTeam, float homeSkill, float awaySkill) {
        this.round = round;
        this.match = match;
        this.homeTeam = homeTeam;
        this.homeGoals = 0;
        this.homeOdd = 0;
        this.awayTeam = awayTeam;
        this.awayGoals = 0;
        this.awayOdd = 0;
        this.drawOdd = 0;
        this.result = 0;
        this.homeSkills = homeSkill;
        this.awaySkills = awaySkill;
        this.homeTeamId = homeId;
        this.awayTeamId = awayId;
        this.weatherFactor = (float) Math.random() * 15 - 7;
        calculateOdds();
    }

    @Override
    public String toString() {
        return "Match{" +
                "round=" + round +
                ", match=" + match +
                ", homeTeam='" + homeTeam + '\'' +
                ", homeTeamId=" + homeTeamId +
                ", homeGoals=" + homeGoals +
                ", homeSkills=" + homeSkills +
                ", homeOdd=" + homeOdd +
                ", awayTeam='" + awayTeam + '\'' +
                ", awayTeamId=" + awayTeamId +
                ", awayGoals=" + awayGoals +
                ", awaySkills=" + awaySkills +
                ", awayOdd=" + awayOdd +
                ", drawOdd=" + drawOdd +
                ", result=" + result +
                '}';
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public int getMatch() {
        return match;
    }

    public void setMatch(int match) {
        this.match = match;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(String homeTeam) {
        this.homeTeam = homeTeam;
    }

    public int getHomeGoals() {
        return homeGoals;
    }

    public void setHomeGoals(int homeGoals) {
        this.homeGoals = homeGoals;
    }

    public double getHomeOdd() {
        return homeOdd;
    }

    public void setHomeOdd(double homeOdd) {
        this.homeOdd = homeOdd;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public int getHomeTeamId() {
        return homeTeamId;
    }

    public void setHomeTeamId(int homeTeamId) {
        this.homeTeamId = homeTeamId;
    }

    public float getHomeSkills() {
        return homeSkills;
    }

    public void setHomeSkills(float homeSkills) {
        this.homeSkills = homeSkills;
    }

    public int getAwayTeamId() {
        return awayTeamId;
    }

    public void setAwayTeamId(int awayTeamId) {
        this.awayTeamId = awayTeamId;
    }

    public float getAwaySkills() {
        return awaySkills;
    }

    public void setAwaySkills(float awaySkills) {
        this.awaySkills = awaySkills;
    }

    public void setAwayTeam(String awayTeam) {
        this.awayTeam = awayTeam;
    }

    public int getAwayGoals() {
        return awayGoals;
    }

    public void setAwayGoals(int awayGoals) {
        this.awayGoals = awayGoals;
    }

    public double getAwayOdd() {
        return awayOdd;
    }

    public void setAwayOdd(double awayOdd) {
        this.awayOdd = awayOdd;
    }

    public double getDrawOdd() {
        return drawOdd;
    }

    public void setDrawOdd(double drawOdd) {
        this.drawOdd = drawOdd;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public void odds (float homeSkill, float awaySkill){

    }

    public float getWeatherFactor() {
        return weatherFactor;
    }

    public void setWeatherFactor(float weatherFactor) {
        this.weatherFactor = weatherFactor;
    }

    public void calculateOdds() {
        // Calculate the odds based on the skills of the home and away teams
        float homeSkill = this.homeSkills;
        float awaySkill = this.awaySkills;
        double skillDifference = Math.abs(homeSkill - awaySkill);

        double hOdd = 1.05f+((100 - homeSkill)/10);
        double aOdd = 1.05f+((100 - awaySkill)/10);
        // Update the odds using the skillDifference
        if (homeSkill > awaySkill){
            hOdd -= (skillDifference/100);
            aOdd += (skillDifference/100);
        } else if (homeSkill < awaySkill) {
            hOdd += (skillDifference/100);
            aOdd -= (skillDifference/100);
        }
        if(hOdd >= 3 && aOdd >= 3){
            if (hOdd < aOdd){
                int intPart = (int) hOdd;
                hOdd = (hOdd - intPart) + 2;
                aOdd = (aOdd - intPart) + 2;
            }else if (hOdd > aOdd){
                int intPart = (int) aOdd;
                aOdd = (aOdd - intPart) + 2;
                hOdd = (hOdd - intPart) + 2;
            }
        }
        if (aOdd < 2 && hOdd < 2) {
            aOdd += 1;
            hOdd += 1;
        }
        hOdd = Math.max(hOdd, 1.10);
        aOdd = Math.max(aOdd, 1.10);
        double dOdd = (hOdd + aOdd)/2;
        // Round the odds to the nearest multiple of 0.05
        hOdd = Math.round(hOdd * 20) / 20.0;
        aOdd = Math.round(aOdd * 20) / 20.0;
        dOdd = Math.round(dOdd * 20) / 20.0;
        setHomeOdd(hOdd);
        setAwayOdd(aOdd);
        setDrawOdd(dOdd);
    }
}
