package com.tfg.futbolstats.service;

import java.sql.*;

/**
 * Servicio para gestionar notas personales sobre jugadores favoritos.
 * Permite al usuario añadir un comentario corto a cada jugador guardado.
 *
 * @author Pablo Romero
 */
public class NotasService {

    private static NotasService instance;
    private final String dbPath;

    private NotasService() {
        String home = System.getProperty("user.home");
        dbPath = "jdbc:sqlite:" + home + "/FutbolStats/users.db";
        crearTabla();
    }

    public static NotasService getInstance() {
        if (instance == null) instance = new NotasService();
        return instance;
    }

    private void crearTabla() {
        String sql = """
            CREATE TABLE IF NOT EXISTS notas (
                id            INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre_jugador TEXT   NOT NULL UNIQUE,
                nota           TEXT   NOT NULL,
                fecha_mod      DATETIME DEFAULT CURRENT_TIMESTAMP
            )
            """;
        try (Connection conn = DriverManager.getConnection(dbPath);
             Statement st = conn.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error creando tabla notas: " + e.getMessage());
        }
    }

    /**
     * Guarda o actualiza la nota de un jugador.
     * Si ya tiene nota, la sobreescribe.
     */
    public void guardarNota(String nombreJugador, String nota) {
        String sql = """
            INSERT INTO notas (nombre_jugador, nota, fecha_mod)
            VALUES (?, ?, CURRENT_TIMESTAMP)
            ON CONFLICT(nombre_jugador) DO UPDATE
            SET nota = excluded.nota, fecha_mod = CURRENT_TIMESTAMP
            """;
        try (Connection conn = DriverManager.getConnection(dbPath);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombreJugador);
            ps.setString(2, nota);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error guardando nota: " + e.getMessage());
        }
    }

    /**
     * Devuelve la nota de un jugador, o cadena vacía si no tiene.
     */
    public String getNota(String nombreJugador) {
        String sql = "SELECT nota FROM notas WHERE nombre_jugador = ?";
        try (Connection conn = DriverManager.getConnection(dbPath);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombreJugador);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("nota");
        } catch (SQLException e) {
            System.err.println("Error obteniendo nota: " + e.getMessage());
        }
        return "";
    }

    /**
     * Elimina la nota de un jugador.
     */
    public void borrarNota(String nombreJugador) {
        String sql = "DELETE FROM notas WHERE nombre_jugador = ?";
        try (Connection conn = DriverManager.getConnection(dbPath);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombreJugador);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error borrando nota: " + e.getMessage());
        }
    }

    /**
     * Comprueba si un jugador tiene nota guardada.
     */
    public boolean tieneNota(String nombreJugador) {
        return !getNota(nombreJugador).isEmpty();
    }
}
