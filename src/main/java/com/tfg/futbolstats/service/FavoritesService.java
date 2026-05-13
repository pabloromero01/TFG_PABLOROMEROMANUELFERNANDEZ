package com.tfg.futbolstats.service;

import com.tfg.futbolstats.model.Player;
import com.tfg.futbolstats.model.PlayerStats;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestión de jugadores favoritos en SQLite.
 * Tabla: favorites (id, name, team, nationality, position, season, goals, assists, apps, mins, rating, shots, yellow, red)
 */
public class FavoritesService {

    private static FavoritesService instance;
    private final Connection connection;

    private FavoritesService() throws SQLException {
        connection = DatabaseService.getInstance().getConnection();
        createTable();
    }

    public static FavoritesService getInstance() {
        if (instance == null) try { instance = new FavoritesService(); } catch (SQLException e) { throw new RuntimeException(e); }
        return instance;
    }

    private void createTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS favorites (
                id           INTEGER PRIMARY KEY AUTOINCREMENT,
                player_id    INTEGER,
                name         TEXT NOT NULL,
                team         TEXT,
                nationality  TEXT,
                position     TEXT,
                season       TEXT,
                goals        INTEGER DEFAULT 0,
                assists      INTEGER DEFAULT 0,
                appearances  INTEGER DEFAULT 0,
                minutes      INTEGER DEFAULT 0,
                rating       REAL    DEFAULT 0,
                shots        INTEGER DEFAULT 0,
                yellow_cards INTEGER DEFAULT 0,
                red_cards    INTEGER DEFAULT 0,
                added_at     TEXT    DEFAULT (datetime('now')),
                UNIQUE(player_id, season)
            )
        """;
        try (Statement s = connection.createStatement()) { s.execute(sql); }
    }

    public boolean addFavorite(Player player, String season) throws SQLException {
        String sql = """
            INSERT OR IGNORE INTO favorites
            (player_id, name, team, nationality, position, season,
             goals, assists, appearances, minutes, rating, shots, yellow_cards, red_cards)
            VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)
        """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            PlayerStats s = player.getStats();
            ps.setInt(1, player.getId());
            ps.setString(2, player.getName());
            ps.setString(3, s != null ? s.getTeamName() : "");
            ps.setString(4, player.getNationality());
            ps.setString(5, player.getPosition());
            ps.setString(6, season);
            ps.setInt(7,    s != null ? s.getGoals() : 0);
            ps.setInt(8,    s != null ? s.getAssists() : 0);
            ps.setInt(9,    s != null ? s.getAppearances() : 0);
            ps.setInt(10,   s != null ? s.getMinutesPlayed() : 0);
            ps.setDouble(11,s != null ? s.getRating() : 0);
            ps.setInt(12,   s != null ? s.getShotsOnTarget() : 0);
            ps.setInt(13,   s != null ? s.getYellowCards() : 0);
            ps.setInt(14,   s != null ? s.getRedCards() : 0);
            return ps.executeUpdate() > 0;
        }
    }

    public void removeFavorite(int playerId, String season) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM favorites WHERE player_id=? AND season=?")) {
            ps.setInt(1, playerId); ps.setString(2, season); ps.executeUpdate();
        }
    }

    public boolean isFavorite(int playerId, String season) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT 1 FROM favorites WHERE player_id=? AND season=?")) {
            ps.setInt(1, playerId); ps.setString(2, season);
            return ps.executeQuery().next();
        }
    }

    /** Devuelve todos los favoritos ordenados por el campo indicado */
    public List<Player> getFavorites(String orderBy) throws SQLException {
        String col = switch (orderBy) {
            case "GOLES"       -> "goals DESC";
            case "ASISTENCIAS" -> "assists DESC";
            case "PARTIDOS"    -> "appearances DESC";
            case "MINUTOS"     -> "minutes DESC";
            case "VALORACIÓN"  -> "rating DESC";
            case "NOMBRE"      -> "name ASC";
            default            -> "added_at DESC";
        };
        String sql = "SELECT * FROM favorites ORDER BY " + col;
        List<Player> list = new ArrayList<>();
        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Player p = new Player();
                p.setId(rs.getInt("player_id"));
                p.setName(rs.getString("name"));
                p.setNationality(rs.getString("nationality"));
                p.setPosition(rs.getString("position"));

                PlayerStats stats = new PlayerStats();
                stats.setTeamName(rs.getString("team"));
                stats.setGoals(rs.getInt("goals"));
                stats.setAssists(rs.getInt("assists"));
                stats.setAppearances(rs.getInt("appearances"));
                stats.setMinutesPlayed(rs.getInt("minutes"));
                stats.setRating(rs.getDouble("rating"));
                stats.setShotsOnTarget(rs.getInt("shots"));
                stats.setYellowCards(rs.getInt("yellow_cards"));
                stats.setRedCards(rs.getInt("red_cards"));
                stats.setSeason(rs.getString("season"));
                p.setStats(stats);

                list.add(p);
            }
        }
        return list;
    }

    public int getCount() throws SQLException {
        try (Statement st = connection.createStatement()) {
            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM favorites");
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
}
