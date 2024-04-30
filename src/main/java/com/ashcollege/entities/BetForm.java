package com.ashcollege.entities;

public class BetForm {
    private int roundNumber;
    private int matchNumber;
    private int betAmount;
    private double odd;
    private int bet;
    private String name;
    private boolean checked;
    private boolean won;

    // Constructor
    public BetForm(int roundNumber, int matchNumber, int betAmount, double odd, int bet, String name) {
        this.roundNumber = roundNumber;
        this.matchNumber = matchNumber;
        this.betAmount = betAmount;
        this.odd = odd;
        this.bet = bet;
        this.name = name;
        this.checked = false; // Initialize checked to false
        this.won = false; // Initialize won to false
    }

    // Getters and setters
    public int getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(int roundNumber) {
        this.roundNumber = roundNumber;
    }

    public int getMatchNumber() {
        return matchNumber;
    }

    public void setMatchNumber(int matchNumber) {
        this.matchNumber = matchNumber;
    }

    public int getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(int betAmount) {
        this.betAmount = betAmount;
    }

    public double getOdd() {
        return odd;
    }

    public void setOdd(double odd) {
        this.odd = odd;
    }

    public int getBet() {
        return bet;
    }

    public void setBet(int bet) {
        this.bet = bet;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isWon() {
        return won;
    }

    public void setWon(boolean won) {
        this.won = won;
    }
}
