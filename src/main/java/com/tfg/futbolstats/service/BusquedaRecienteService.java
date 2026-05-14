package com.tfg.futbolstats.service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para gestionar el historial de búsquedas recientes.
 * Guarda las últimas 8 búsquedas en SQLite y las muestra como
 * sugerencias debajo del campo de búsqueda.
 *
 * @author Pablo Romero
 */
public class BusquedaRecienteService {

    private static BusquedaRecienteService instance;
    private final String dbPath;
    private static final int MAX_BUSQUEDAS = 8;

    private BusquedaRecienteService() {
        String home = System.getProperty("user.home");
        dbPath = "jdbc:sqlite:" + home + "/FutbolStats/users.db";
        crearTabla();
    }

    public static BusquedaRecienteService getInstance() {
        if (instance == null) instance = new BusquedaRecienteService();
        return instance;
    }

    private void crearTabla() {
        String sql = """
            CREATE TABLE IF NOT EXISTS busquedas_recientes (
                id        INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre    TEXT    NOT NULL UNIQUE,
                temporada TEXT    NOT NULL,
                veces     INTEGER DEFAULT 1,
                fecha     DATETIME DEFAULT CURRENT_TIMESTAMP
            )
            """;
        try (Connection conn = DriverManager.getConnection(dbPath);
             Statement st = conn.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error creando tabla búsquedas: " + e.getMessage());
        }
    }

    /**
     * Registra una búsqueda. Si ya existe, incrementa el contador
     * y actualiza la fecha para que aparezca primero.
     */
    public void registrar(String nombre, String temporada) {
        if (nombre == null || nombre.trim().isEmpty()) return;
        String nombre2 = nombre.trim();

        String existe = "SELECT id FROM busquedas_recientes WHERE nombre = ?";
        String actualizar = """
            UPDATE busquedas_recientes
            SET veces = veces + 1, fecha = CURRENT_TIMESTAMP, temporada = ?
            WHERE nombre = ?
            """;
        String insertar = """
            INSERT INTO busquedas_recientes (nombre, temporada)
            VALUES (?, ?)
            """;
        String limpiar = """
            DELETE FROM busquedas_recientes WHERE id NOT IN (
                SELECT id FROM busquedas_recientes ORDER BY fecha DESC LIMIT ?
            )
            """;

        try (Connection conn = DriverManager.getConnection(dbPath)) {
            boolean existe2 = false;
            try (PreparedStatement ps = conn.prepareStatement(existe)) {
                ps.setString(1, nombre2);
                existe2 = ps.executeQuery().next();
            }
            if (existe2) {
                try (PreparedStatement ps = conn.prepareStatement(actualizar)) {
                    ps.setString(1, temporada);
                    ps.setString(2, nombre2);
                    ps.executeUpdate();
                }
            } else {
                try (PreparedStatement ps = conn.prepareStatement(insertar)) {
                    ps.setString(1, nombre2);
                    ps.setString(2, temporada);
                    ps.executeUpdate();
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(limpiar)) {
                ps.setInt(1, MAX_BUSQUEDAS);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error registrando búsqueda: " + e.getMessage());
        }
    }

    /**
     * Devuelve la lista de búsquedas recientes ordenadas de más reciente a más antigua.
     * Solo devuelve los nombres para mostrar como sugerencias.
     */
    public List<String> getRecientes() {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT nombre FROM busquedas_recientes ORDER BY fecha DESC LIMIT ?";
        try (Connection conn = DriverManager.getConnection(dbPath);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, MAX_BUSQUEDAS);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(rs.getString("nombre"));
        } catch (SQLException e) {
            System.err.println("Error obteniendo búsquedas recientes: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Devuelve búsquedas que coincidan con el prefijo escrito.
     * Útil para autocompletar mientras el usuario escribe.
     */
    public List<String> getSugerencias(String prefijo) {
        List<String> lista = new ArrayList<>();
        if (prefijo == null || prefijo.trim().isEmpty()) return getRecientes();
        String sql = """
            SELECT nombre FROM busquedas_recientes
            WHERE LOWER(nombre) LIKE LOWER(?)
            ORDER BY fecha DESC LIMIT ?
            """;
        try (Connection conn = DriverManager.getConnection(dbPath);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, prefijo.trim() + "%");
            ps.setInt(2, MAX_BUSQUEDAS);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(rs.getString("nombre"));
        } catch (SQLException e) {
            System.err.println("Error obteniendo sugerencias: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Elimina una búsqueda concreta del historial.
     */
    public void eliminar(String nombre) {
        String sql = "DELETE FROM busquedas_recientes WHERE nombre = ?";
        try (Connection conn = DriverManager.getConnection(dbPath);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error eliminando búsqueda: " + e.getMessage());
        }
    }

    /**
     * Borra todo el historial de búsquedas.
     */
    public void limpiar() {
        try (Connection conn = DriverManager.getConnection(dbPath);
             Statement st = conn.createStatement()) {
            st.execute("DELETE FROM busquedas_recientes");
        } catch (SQLException e) {
            System.err.println("Error limpiando búsquedas: " + e.getMessage());
        }
    }
}