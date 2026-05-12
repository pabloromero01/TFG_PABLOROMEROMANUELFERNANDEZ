package com.tfg.futbolstats.model;

/**
 * Estadísticas de un jugador en una temporada/liga.
 *
 * @author Persona A
 */
public class PlayerStats {

    private String teamName;
    private String leagueName;
    private String season;
    private String position;

    private int appearances;
    private int minutesPlayed;
    private int lineups;

    private int goals;
    private int assists;
    private int shots;
    private int shotsOnTarget;

    private int totalPasses;
    private int keyPasses;
    private double passAccuracy;

    private int tackles;
    private int interceptions;
    private int duelsTotal;
    private int duelsWon;

    private int yellowCards;
    private int redCards;

    private double rating;

    public PlayerStats() {}

    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }

    public String getLeagueName() { return leagueName; }
    public void setLeagueName(String leagueName) { this.leagueName = leagueName; }

    public String getSeason() { return season; }
    public void setSeason(String season) { this.season = season; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public int getAppearances() { return appearances; }
    public void setAppearances(int appearances) { this.appearances = appearances; }

    public int getMinutesPlayed() { return minutesPlayed; }
    public void setMinutesPlayed(int minutesPlayed) { this.minutesPlayed = minutesPlayed; }

    public int getLineups() { return lineups; }
    public void setLineups(int lineups) { this.lineups = lineups; }

    public int getGoals() { return goals; }
    public void setGoals(int goals) { this.goals = goals; }

    public int getAssists() { return assists; }
    public void setAssists(int assists) { this.assists = assists; }

    public int getShots() { return shots; }
    public void setShots(int shots) { this.shots = shots; }

    public int getShotsOnTarget() { return shotsOnTarget; }
    public void setShotsOnTarget(int shotsOnTarget) { this.shotsOnTarget = shotsOnTarget; }

    public int getTotalPasses() { return totalPasses; }
    public void setTotalPasses(int totalPasses) { this.totalPasses = totalPasses; }

    public int getKeyPasses() { return keyPasses; }
    public void setKeyPasses(int keyPasses) { this.keyPasses = keyPasses; }

    public double getPassAccuracy() { return passAccuracy; }
    public void setPassAccuracy(double passAccuracy) { this.passAccuracy = passAccuracy; }

    public int getTackles() { return tackles; }
    public void setTackles(int tackles) { this.tackles = tackles; }

    public int getInterceptions() { return interceptions; }
    public void setInterceptions(int interceptions) { this.interceptions = interceptions; }

    public int getDuelsTotal() { return duelsTotal; }
    public void setDuelsTotal(int duelsTotal) { this.duelsTotal = duelsTotal; }

    public int getDuelsWon() { return duelsWon; }
    public void setDuelsWon(int duelsWon) { this.duelsWon = duelsWon; }

    public int getYellowCards() { return yellowCards; }
    public void setYellowCards(int yellowCards) { this.yellowCards = yellowCards; }

    public int getRedCards() { return redCards; }
    public void setRedCards(int redCards) { this.redCards = redCards; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public double getGoalsPerGame() {
        return appearances > 0 ? (double) goals / appearances : 0;
    }

    public double getDuelsWonPercent() {
        return duelsTotal > 0 ? (double) duelsWon / duelsTotal * 100 : 0;
    }
}

