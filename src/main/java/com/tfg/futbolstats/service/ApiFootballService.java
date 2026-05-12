package com.tfg.futbolstats.service;

import com.google.gson.*;
import com.tfg.futbolstats.config.AppConfig;
import com.tfg.futbolstats.model.Player;
import com.tfg.futbolstats.model.PlayerStats;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para consumir api-football (api-sports.io).
 * Plan gratuito: temporadas 2022-2024, 100 peticiones/día.
 * Búsqueda por nombre requiere también league/team — usamos ID directo
 */
public class ApiFootballService {

    private final HttpClient httpClient;

    public ApiFootballService() {
        this.httpClient = HttpClient.newHttpClient();
    }

    /**
     * Busca jugadores por nombre usando el PlayerIdMap para obtener el ID,
     * y luego carga sus stats directamente por ID.
     * Solo funciona con temporadas 2022-2024 en plan gratuito.
     */
    public List<Player> searchPlayersByName(String name, String season) throws Exception {
        int apiId = PlayerIdMap.getApiFootballId(name);
        if (apiId == -1) {
            // Intentar búsqueda directa (puede fallar en plan gratuito sin league param)
            throw new Exception("Jugador '" + name + "' no encontrado. Prueba con el nombre completo " +
                    "(ej: 'Lionel Messi', 'Cristiano Ronaldo', 'Kylian Mbappe').");
        }
        Player p = getPlayerById(apiId, season);
        List<Player> result = new ArrayList<>();
        if (p != null) result.add(p);
        return result;
    }

    /**
     * Obtiene estadísticas de un jugador por su ID numérico.
     */
    public Player getPlayerStats(int playerId, String season) throws Exception {
        return getPlayerById(playerId, season);
    }

    /**
     * Carga un jugador por ID directamente — método principal.
     */
    public Player getPlayerById(int playerId, String season) throws Exception {
        String url = AppConfig.getBaseUrl() + "/players?id=" + playerId + "&season=" + season;
        JsonObject response = makeRequest(url);
        if (response == null) return null;

        JsonArray arr = response.getAsJsonArray("response");
        if (arr == null || arr.size() == 0) return null;

        JsonObject entry = arr.get(0).getAsJsonObject();
        JsonObject pj = entry.getAsJsonObject("player");
        if (pj == null) return null;

        Player player = new Player();
        player.setId(getInt(pj, "id"));
        player.setName(getString(pj, "name"));
        player.setFirstname(getString(pj, "firstname"));
        player.setLastname(getString(pj, "lastname"));
        player.setAge(getInt(pj, "age"));
        player.setNationality(getString(pj, "nationality"));
        player.setPhoto(getString(pj, "photo"));

        JsonArray statsArray = entry.getAsJsonArray("statistics");
        if (statsArray != null && statsArray.size() > 0) {
            PlayerStats stats = parseStats(statsArray.get(0).getAsJsonObject());
            player.setStats(stats);
            player.setPosition(stats.getPosition() != null ? stats.getPosition() : "");
        }
        return player;
    }

    public JsonObject makeRequest(String url) throws Exception {
        String apiKey = AppConfig.getApiKey();
        if (apiKey == null || apiKey.isEmpty() || apiKey.startsWith("AQUI")) {
            throw new Exception("API key no configurada en config.properties");
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("x-apisports-key", apiKey)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 429)
            throw new Exception("Límite diario de 100 peticiones alcanzado. Vuelve mañana.");
        if (response.statusCode() != 200)
            throw new Exception("Error API: código " + response.statusCode());

        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();

        // Verificar errores dentro del JSON
        JsonElement errors = json.get("errors");
        if (errors != null && !errors.isJsonNull() && errors.isJsonObject()) {
            JsonObject errObj = errors.getAsJsonObject();
            if (errObj.has("plan")) {
                throw new Exception("Plan gratuito: usa solo temporadas 2022, 2023 o 2024.");
            }
            if (errObj.has("token") || errObj.has("requests")) {
                throw new Exception("API key inválida o sin peticiones disponibles.");
            }
        }
        return json;
    }

    public PlayerStats parseStats(JsonObject statsJson) {
        PlayerStats stats = new PlayerStats();

        JsonObject team = statsJson.getAsJsonObject("team");
        if (team != null) stats.setTeamName(getString(team, "name"));

        JsonObject league = statsJson.getAsJsonObject("league");
        if (league != null) {
            stats.setLeagueName(getString(league, "name"));
            stats.setSeason(getString(league, "season"));
        }

        JsonObject games = statsJson.getAsJsonObject("games");
        if (games != null) {
            stats.setAppearances(getInt(games, "appearences"));
            stats.setLineups(getInt(games, "lineups"));
            stats.setMinutesPlayed(getInt(games, "minutes"));
            stats.setPosition(getString(games, "position"));
            String r = getString(games, "rating");
            if (!r.isEmpty()) try { stats.setRating(Double.parseDouble(r)); } catch (Exception ignored) {}
        }

        JsonObject goals = statsJson.getAsJsonObject("goals");
        if (goals != null) { stats.setGoals(getInt(goals, "total")); stats.setAssists(getInt(goals, "assists")); }

        JsonObject shots = statsJson.getAsJsonObject("shots");
        if (shots != null) { stats.setShots(getInt(shots, "total")); stats.setShotsOnTarget(getInt(shots, "on")); }

        JsonObject passes = statsJson.getAsJsonObject("passes");
        if (passes != null) {
            stats.setTotalPasses(getInt(passes, "total"));
            stats.setKeyPasses(getInt(passes, "key"));
            stats.setPassAccuracy(getDouble(passes, "accuracy"));
        }

        JsonObject tackles = statsJson.getAsJsonObject("tackles");
        if (tackles != null) { stats.setTackles(getInt(tackles, "total")); stats.setInterceptions(getInt(tackles, "interceptions")); }

        JsonObject duels = statsJson.getAsJsonObject("duels");
        if (duels != null) { stats.setDuelsTotal(getInt(duels, "total")); stats.setDuelsWon(getInt(duels, "won")); }

        JsonObject cards = statsJson.getAsJsonObject("cards");
        if (cards != null) { stats.setYellowCards(getInt(cards, "yellow")); stats.setRedCards(getInt(cards, "red")); }

        return stats;
    }

    public String getString(JsonObject obj, String key) {
        if (obj == null || !obj.has(key) || obj.get(key).isJsonNull()) return "";
        return obj.get(key).getAsString();
    }
    public int getInt(JsonObject obj, String key) {
        if (obj == null || !obj.has(key) || obj.get(key).isJsonNull()) return 0;
        try { return obj.get(key).getAsInt(); } catch (Exception e) { return 0; }
    }
    public double getDouble(JsonObject obj, String key) {
        if (obj == null || !obj.has(key) || obj.get(key).isJsonNull()) return 0.0;
        try { return obj.get(key).getAsDouble(); } catch (Exception e) { return 0.0; }
    }
}