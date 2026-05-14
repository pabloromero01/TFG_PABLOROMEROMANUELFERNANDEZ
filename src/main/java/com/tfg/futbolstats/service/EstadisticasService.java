package com.tfg.futbolstats.service;

import com.tfg.futbolstats.model.Player;
import com.tfg.futbolstats.model.PlayerStats;

import java.util.List;

/**
 * Servicio que calcula estadísticas agregadas sobre los jugadores favoritos.
 * Muestra medias, totales y el mejor jugador por categoría.
 *
 * @author Pablo Romero
 */
public class EstadisticasService {

    private static EstadisticasService instance;

    private EstadisticasService() {}

    public static EstadisticasService getInstance() {
        if (instance == null) instance = new EstadisticasService();
        return instance;
    }

    /**
     * Clase que contiene el resumen estadístico de una lista de jugadores.
     */
    public static class Resumen {
        public double mediaGoles;
        public double mediaAsistencias;
        public double mediaPartidos;
        public double mediaMinutos;
        public double mediaValoracion;

        public int totalGoles;
        public int totalAsistencias;
        public int totalPartidos;

        public String mejorGoleador;
        public String mejorAsistente;
        public String mejorValorado;

        public int totalJugadores;
    }

    /**
     * Calcula el resumen estadístico de una lista de jugadores favoritos.
     * Ignora jugadores sin estadísticas.
     */
    public Resumen calcularResumen(List<Player> jugadores) {
        Resumen r = new Resumen();
        if (jugadores == null || jugadores.isEmpty()) return r;

        int count = 0;
        double sumGoles = 0, sumAsist = 0, sumPart = 0, sumMins = 0, sumVal = 0;

        String mejorGoleadorNombre = "—";
        String mejorAsistenteNombre = "—";
        String mejorValoradoNombre = "—";
        int maxGoles = -1, maxAsist = -1;
        double maxVal = -1;

        for (Player p : jugadores) {
            PlayerStats s = p.getStats();
            if (s == null) continue;
            count++;

            sumGoles += s.getGoals();
            sumAsist += s.getAssists();
            sumPart  += s.getAppearances();
            sumMins  += s.getMinutesPlayed();
            sumVal   += s.getRating();

            r.totalGoles       += s.getGoals();
            r.totalAsistencias += s.getAssists();
            r.totalPartidos    += s.getAppearances();

            if (s.getGoals() > maxGoles) {
                maxGoles = s.getGoals();
                mejorGoleadorNombre = p.getName();
            }
            if (s.getAssists() > maxAsist) {
                maxAsist = s.getAssists();
                mejorAsistenteNombre = p.getName();
            }
            if (s.getRating() > maxVal) {
                maxVal = s.getRating();
                mejorValoradoNombre = p.getName();
            }
        }

        if (count > 0) {
            r.mediaGoles       = sumGoles / count;
            r.mediaAsistencias = sumAsist / count;
            r.mediaPartidos    = sumPart  / count;
            r.mediaMinutos     = sumMins  / count;
            r.mediaValoracion  = sumVal   / count;
        }

        r.mejorGoleador   = mejorGoleadorNombre;
        r.mejorAsistente  = mejorAsistenteNombre;
        r.mejorValorado   = mejorValoradoNombre;
        r.totalJugadores  = count;

        return r;
    }

    /**
     * Devuelve el jugador con más goles de la lista.
     */
    public Player getMejorGoleador(List<Player> jugadores) {
        return jugadores.stream()
                .filter(p -> p.getStats() != null)
                .max((a, b) -> Integer.compare(
                        a.getStats().getGoals(),
                        b.getStats().getGoals()))
                .orElse(null);
    }

    /**
     * Devuelve el jugador con mejor valoración de la lista.
     */
    public Player getMejorValorado(List<Player> jugadores) {
        return jugadores.stream()
                .filter(p -> p.getStats() != null)
                .max((a, b) -> Double.compare(
                        a.getStats().getRating(),
                        b.getStats().getRating()))
                .orElse(null);
    }

    /**
     * Genera un texto resumen legible para mostrar en la UI o exportar.
     */
    public String resumenTexto(List<Player> jugadores) {
        Resumen r = calcularResumen(jugadores);
        if (r.totalJugadores == 0) return "No hay favoritos con estadísticas.";

        return String.format("""
                ===== RESUMEN DE FAVORITOS =====
                Jugadores analizados : %d
                
                MEDIAS POR JUGADOR
                  Goles        : %.1f
                  Asistencias  : %.1f
                  Partidos     : %.1f
                  Minutos      : %.0f
                  Valoración   : %.2f
                
                TOTALES
                  Goles        : %d
                  Asistencias  : %d
                  Partidos     : %d
                
                MEJORES
                  Goleador     : %s (%d goles)
                  Asistente    : %s
                  Valorado     : %s (%.2f)
                ================================
                """,
                r.totalJugadores,
                r.mediaGoles, r.mediaAsistencias, r.mediaPartidos,
                r.mediaMinutos, r.mediaValoracion,
                r.totalGoles, r.totalAsistencias, r.totalPartidos,
                r.mejorGoleador, getMejorGoleador(jugadores) != null
                        ? getMejorGoleador(jugadores).getStats().getGoals() : 0,
                r.mejorAsistente,
                r.mejorValorado, getMejorValorado(jugadores) != null
                        ? getMejorValorado(jugadores).getStats().getRating() : 0
        );
    }
}