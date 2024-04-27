package com.ashcollege.entities;

import java.util.List;

public class InitialPageDetails {
    private List<Match> currentRound;
    private List<Team> teams;
    private boolean betEnable;

    public InitialPageDetails(List<Match> currentRound, List<Team> teams, boolean betEnable) {
        this.currentRound = currentRound;
        this.teams = teams;
        this.betEnable = betEnable;
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
}
