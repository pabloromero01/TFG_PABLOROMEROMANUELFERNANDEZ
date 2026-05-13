package com.tfg.futbolstats.service;

import com.tfg.futbolstats.model.Player;
import com.tfg.futbolstats.model.PlayerStats;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Servicio para exportar fichas de jugadores y comparativas a archivos externos.
 * Los archivos se guardan en ~/FutbolStats/exports/
 *
 * @author Pablo Romero
 */
public class ExportService {

    private static ExportService instance;
    private final String exportDir;
    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FORMATO_ARCHIVO =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private ExportService() {
        String home = System.getProperty("user.home");
        exportDir = home + "/FutbolStats/exports/";
        // Crear directorio si no existe
        new java.io.File(exportDir).mkdirs();
    }

    public static ExportService getInstance() {
        if (instance == null) instance = new ExportService();
        return instance;
    }

    /**
     * Exporta la ficha completa de un jugador a un archivo TXT.
     * Devuelve la ruta del archivo generado.
     */
    public String exportarFichaJugador(Player jugador) throws IOException {
        String timestamp = LocalDateTime.now().format(FORMATO_ARCHIVO);
        String nombreArchivo = exportDir + jugador.getName().replace(" ", "_") + "_" + timestamp + ".txt";

        try (PrintWriter pw = new PrintWriter(new FileWriter(nombreArchivo))) {
            pw.println("========================================");
            pw.println("  FICHA DE JUGADOR — FútbolStats");
            pw.println("  Exportado: " + LocalDateTime.now().format(FORMATO_FECHA));
            pw.println("========================================");
            pw.println();
            pw.println("DATOS PERSONALES");
            pw.println("----------------");
            pw.println("Nombre      : " + jugador.getName());
            pw.println("Edad        : " + jugador.getAge());
            pw.println("Nacionalidad: " + safe(jugador.getNationality()));
            pw.println("Posición    : " + safe(jugador.getPosition()));
            pw.println("Equipo      : " + safe(jugador.getTeamName()));

            PlayerStats stats = jugador.getStats();
            if (stats != null) {
                pw.println();
                pw.println("ESTADÍSTICAS — Temporada " + safe(stats.getSeason()));
                pw.println("------------------------------------------");
                pw.println("Liga        : " + safe(stats.getLeagueName()));
                pw.println("Partidos    : " + stats.getAppearances());
                pw.println("Minutos     : " + stats.getMinutesPlayed());
                pw.println("Goles       : " + stats.getGoals());
                pw.println("Asistencias : " + stats.getAssists());
                pw.println("Valoración  : " + String.format("%.2f", stats.getRating()));
                pw.println();
                pw.println("ATAQUE");
                pw.println("Tiros totales   : " + stats.getShots());
                pw.println("Tiros a puerta  : " + stats.getShotsOnTarget());
                pw.println("Goles/partido   : " + String.format("%.2f", stats.getGoalsPerGame()));
                pw.println();
                pw.println("PASES");
                pw.println("Pases totales   : " + stats.getTotalPasses());
                pw.println("Pases clave     : " + stats.getKeyPasses());
                pw.println("Precisión pases : " + String.format("%.1f%%", stats.getPassAccuracy()));
                pw.println();
                pw.println("DEFENSA");
                pw.println("Entradas        : " + stats.getTackles());
                pw.println("Intercepciones  : " + stats.getInterceptions());
                pw.println("Duelos ganados  : " + String.format("%.1f%%", stats.getDuelsWonPercent()));
                pw.println();
                pw.println("DISCIPLINA");
                pw.println("Tarjetas amarillas: " + stats.getYellowCards());
                pw.println("Tarjetas rojas    : " + stats.getRedCards());
            }
            pw.println();
            pw.println("========================================");
            pw.println("  Generado por FútbolStats — TFG DAM");
            pw.println("========================================");
        }
        return nombreArchivo;
    }

    /**
     * Exporta una comparativa entre dos jugadores a CSV.
     * Devuelve la ruta del archivo generado.
     */
    public String exportarComparativa(Player j1, Player j2) throws IOException {
        String timestamp = LocalDateTime.now().format(FORMATO_ARCHIVO);
        String nombreArchivo = exportDir + "comparativa_" + timestamp + ".csv";

        try (PrintWriter pw = new PrintWriter(new FileWriter(nombreArchivo))) {
            pw.println("Estadística," + j1.getName() + "," + j2.getName());
            pw.println("Equipo," + safe(j1.getTeamName()) + "," + safe(j2.getTeamName()));
            pw.println("Posición," + safe(j1.getPosition()) + "," + safe(j2.getPosition()));
            pw.println("Edad," + j1.getAge() + "," + j2.getAge());

            PlayerStats s1 = j1.getStats();
            PlayerStats s2 = j2.getStats();
            if (s1 != null && s2 != null) {
                pw.println("Temporada," + safe(s1.getSeason()) + "," + safe(s2.getSeason()));
                pw.println("Liga," + safe(s1.getLeagueName()) + "," + safe(s2.getLeagueName()));
                pw.println("Partidos," + s1.getAppearances() + "," + s2.getAppearances());
                pw.println("Minutos," + s1.getMinutesPlayed() + "," + s2.getMinutesPlayed());
                pw.println("Goles," + s1.getGoals() + "," + s2.getGoals());
                pw.println("Asistencias," + s1.getAssists() + "," + s2.getAssists());
                pw.println("Valoración," + s1.getRating() + "," + s2.getRating());
                pw.println("Tiros totales," + s1.getShots() + "," + s2.getShots());
                pw.println("Tiros a puerta," + s1.getShotsOnTarget() + "," + s2.getShotsOnTarget());
                pw.println("Pases clave," + s1.getKeyPasses() + "," + s2.getKeyPasses());
                pw.println("Entradas," + s1.getTackles() + "," + s2.getTackles());
                pw.println("Tarjetas amarillas," + s1.getYellowCards() + "," + s2.getYellowCards());
                pw.println("Tarjetas rojas," + s1.getRedCards() + "," + s2.getRedCards());
            }
        }
        return nombreArchivo;
    }

    private String safe(String val) {
        return val != null ? val : "—";
    }
}
