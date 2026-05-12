package com.tfg.futbolstats.model;

/**
 * Jugador con datos precargados para el minijuego.
 * Evita peticiones a la API en tiempo real.
 */
public class GamePlayer {
    private final String name;
    private final String team;
    private final String nationality;
    private final int goals;
    private final int assists;
    private final int appearances;
    private final int minutesPlayed;
    private final int yellowCards;
    private final int redCards;
    private final double rating;
    private final int shotsOnTarget;
    private final String season;
    private final String photoUrl;

    public GamePlayer(String name, String team, String nationality,
                      int goals, int assists, int appearances, int minutesPlayed,
                      int yellowCards, int redCards, double rating, int shotsOnTarget,
                      String season, String photoUrl) {
        this.name = name; this.team = team; this.nationality = nationality;
        this.goals = goals; this.assists = assists; this.appearances = appearances;
        this.minutesPlayed = minutesPlayed; this.yellowCards = yellowCards;
        this.redCards = redCards; this.rating = rating; this.shotsOnTarget = shotsOnTarget;
        this.season = season; this.photoUrl = photoUrl;
    }

    public String getName()        { return name; }
    public String getTeam()        { return team; }
    public String getNationality() { return nationality; }
    public int    getGoals()       { return goals; }
    public int    getAssists()     { return assists; }
    public int    getAppearances() { return appearances; }
    public int    getMinutesPlayed(){ return minutesPlayed; }
    public int    getYellowCards() { return yellowCards; }
    public int    getRedCards()    { return redCards; }
    public double getRating()      { return rating; }
    public int    getShotsOnTarget(){ return shotsOnTarget; }
    public String getSeason()      { return season; }
    public String getPhotoUrl()    { return photoUrl; }

    public int getValueForCategory(String category) {
        return switch (category) {
            case "GOLES"       -> goals;
            case "ASISTENCIAS" -> assists;
            case "PARTIDOS"    -> appearances;
            case "MINUTOS"     -> minutesPlayed;
            case "TIROS"       -> shotsOnTarget;
            case "AMARILLAS"   -> yellowCards;
            default            -> goals;
        };
    }

    public String getFormattedValue(String category) {
        return switch (category) {
            case "GOLES"       -> goals + " goles";
            case "ASISTENCIAS" -> assists + " asist.";
            case "PARTIDOS"    -> appearances + " partidos";
            case "MINUTOS"     -> minutesPlayed + " min.";
            case "TIROS"       -> shotsOnTarget + " tiros";
            case "AMARILLAS"   -> yellowCards + " amarillas";
            default            -> String.valueOf(goals);
        };
    }

    @Override public String toString() { return name; }
}