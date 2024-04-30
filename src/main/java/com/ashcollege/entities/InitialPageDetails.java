package com.ashcollege.entities;

import java.util.ArrayList;
import java.util.List;

public class InitialPageDetails {
    private List<Match> currentRound;
    private List<Team> teams;
    private boolean betEnable;
    private List<List<BetForm>> submittedForms = new ArrayList<>();
    private int current;


    public InitialPageDetails(List<Match> currentRound, List<Team> teams, boolean betEnable, List<List<BetForm>> submittedForms, int current) {
        this.currentRound = currentRound;
        this.teams = teams;
        this.betEnable = betEnable;
        this.submittedForms = submittedForms;
        this.current = current;
    }

    public List<Match> getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(List<Match> currentRound) {
        this.currentRound = currentRound;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    public boolean isBetEnable() {
        return betEnable;
    }

    public void setBetEnable(boolean betEnable) {
        this.betEnable = betEnable;
    }

    public List<List<BetForm>> getSubmittedForms() {
        return submittedForms;
    }

    public void setSubmittedForms(List<List<BetForm>> submittedForms) {
        this.submittedForms = submittedForms;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }
}
