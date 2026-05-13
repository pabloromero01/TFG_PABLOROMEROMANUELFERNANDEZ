package com.tfg.futbolstats.service;

import com.tfg.futbolstats.model.Player;
import com.tfg.futbolstats.model.PlayerStats;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PlayerService {

    private final ApiFootballService apiService;

    public PlayerService() {
        this.apiService = new ApiFootballService();
    }

    /**
     * Busca jugadores por nombre usando PlayerIdMap + api-football por ID.
     * Solo temporadas 2022-2024 en plan gratuito.
     */
    public List<Player> search(String name, String season) throws Exception {
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("El nombre no puede estar vacío");

        int apiId = PlayerIdMap.getApiFootballId(name.trim());
        if (apiId == -1) {
            throw new Exception("Jugador no encontrado. Prueba con nombre completo: " +
                    "'Lionel Messi', 'Cristiano Ronaldo', 'Kylian Mbappe', 'Erling Haaland'...");
        }

        Player p = apiService.getPlayerById(apiId, season);
        if (p == null) throw new Exception("Sin datos para '" + name + "' en temporada " + season);
        return List.of(p);
    }

    public Player loadPlayer(int playerId, String season) throws Exception {
        Player p = apiService.getPlayerStats(playerId, season);
        if (p == null) throw new Exception("Jugador no encontrado: ID " + playerId);
        return p;
    }

    public Map<String, Integer> compare(Player p1, Player p2) {
        Map<String, Integer> result = new LinkedHashMap<>();
        if (p1.getStats() == null || p2.getStats() == null) return result;
        PlayerStats s1 = p1.getStats(), s2 = p2.getStats();

        result.put("Goles",           winner(s1.getGoals(),           s2.getGoals()));
        result.put("Asistencias",     winner(s1.getAssists(),         s2.getAssists()));
        result.put("Partidos",        winner(s1.getAppearances(),     s2.getAppearances()));
        result.put("Minutos jugados", winner(s1.getMinutesPlayed(),   s2.getMinutesPlayed()));
        result.put("Tiros a puerta",  winner(s1.getShotsOnTarget(),   s2.getShotsOnTarget()));
        result.put("Pases clave",     winner(s1.getKeyPasses(),       s2.getKeyPasses()));
        result.put("Precisión pase",  winnerD(s1.getPassAccuracy(),   s2.getPassAccuracy()));
        result.put("Entradas",        winner(s1.getTackles(),         s2.getTackles()));
        result.put("Intercepciones",  winner(s1.getInterceptions(),   s2.getInterceptions()));
        result.put("Duelos ganados%", winnerD(s1.getDuelsWonPercent(),s2.getDuelsWonPercent()));
        result.put("Valoración",      winnerD(s1.getRating(),         s2.getRating()));
        return result;
    }

    public int[] getScore(Map<String, Integer> comparison) {
        int[] score = {0, 0, 0};
        for (int v : comparison.values()) {
            if (v == 1) score[0]++; else if (v == 2) score[1]++; else score[2]++;
        }
        return score;
    }

    private int  winner(int a, int b)      { return a > b ? 1 : b > a ? 2 : 0; }
    private int  winnerD(double a, double b){ return a > b ? 1 : b > a ? 2 : 0; }
}
