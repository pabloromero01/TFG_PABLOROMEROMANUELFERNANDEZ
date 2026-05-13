package com.tfg.futbolstats.service;

import java.io.File;
import java.sql.*;

/**
 * Servicio de base de datos SQLite.
 * El archivo se guarda en: <directorio_usuario>/FutbolStats/users.db
 * Se crea automáticamente si no existe.
 */
public class DatabaseService {

    private static DatabaseService instance;
    private Connection connection;

    // Ruta del archivo de base de datos en el equipo del usuario
    private static final String DB_DIR  = System.getProperty("user.home") + File.separator + "FutbolStats";
    private static final String DB_PATH = DB_DIR + File.separator + "users.db";

    private DatabaseService() {
        try {
            // Crear directorio si no existe
            new File(DB_DIR).mkdirs();

            // Conectar (crea el archivo si no existe)
            connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);

            // Crear tabla si no existe
            createTables();

            // Insertar usuario demo por defecto
            insertDemoUser();

            System.out.println("✔ Base de datos SQLite iniciada en: " + DB_PATH);
        } catch (SQLException e) {
            throw new RuntimeException("Error al iniciar la base de datos: " + e.getMessage(), e);
        }
    }

    /** Singleton — una sola instancia en toda la app */
    public static DatabaseService getInstance() {
        if (instance == null) instance = new DatabaseService();
        return instance;
    }

    private void createTables() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS users (
                id       INTEGER PRIMARY KEY AUTOINCREMENT,
                name     TEXT    NOT NULL,
                email    TEXT    NOT NULL UNIQUE,
                password TEXT    NOT NULL,
                created  TEXT    DEFAULT (datetime('now'))
            )
        """;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    private void insertDemoUser() {
        try {
            // Solo lo inserta si no existe
            String sql = "INSERT OR IGNORE INTO users (name, email, password) VALUES (?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, "Usuario Demo");
                ps.setString(2, "demo@tfg.com");
                ps.setString(3, "1234");
                ps.executeUpdate();
            }
        } catch (SQLException ignored) {}
    }

    /**
     * Registra un nuevo usuario.
     * @return true si se creó correctamente, false si el email ya existe.
     */
    public boolean register(String name, String email, String password) throws SQLException {
        String sql = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, email.toLowerCase());
            ps.setString(3, password);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            // SQLITE_CONSTRAINT = email duplicado
            if (e.getMessage().contains("UNIQUE constraint failed")) return false;
            throw e;
        }
    }

    /**
     * Intenta hacer login.
     * @return el nombre del usuario si las credenciales son correctas, null si no.
     */
    public String login(String email, String password) throws SQLException {
        String sql = "SELECT name FROM users WHERE email = ? AND password = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email.toLowerCase().trim());
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getString("name") : null;
        }
    }

    /**
     * Comprueba si un email ya está registrado.
     */
    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE email = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email.toLowerCase().trim());
            return ps.executeQuery().next();
        }
    }

    /**
     * Devuelve cuántos usuarios hay registrados.
     */
    public int getUserCount() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    /** Expone la conexión para otros servicios (FavoritesService) */
    public Connection getConnection() { return connection; }

    public void close() {
        try { if (connection != null) connection.close(); }
        catch (SQLException ignored) {}
    }
}
