package com.tfg.futbolstats.service;

import com.google.gson.*;
import com.tfg.futbolstats.model.Player;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para consumir TheSportsDB (completamente gratuito, sin API key).
 * Documentación: https://www.thesportsdb.com/api.php
 *
 * Usamos este servicio para:
 *  - Buscar jugadores por nombre
 *  - Obtener foto, nacionalidad, descripción y datos de carrera
 */
public class SportsDbService {

    private static final String BASE_URL = "https://www.thesportsdb.com/api/v1/json/3";
    private final HttpClient httpClient;
    private final Gson gson;

    public SportsDbService() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    /**
     * Busca jugadores por nombre en TheSportsDB.
     * Devuelve lista de Player con datos básicos y foto.
     */
    public List<Player> searchPlayers(String name) throws Exception {
        String url = BASE_URL + "/searchplayers.php?p=" + name.replace(" ", "%20");
        JsonObject response = makeRequest(url);

        List<Player> players = new ArrayList<>();
        if (response == null) return players;

        JsonElement playersEl = response.get("player");
        if (playersEl == null || playersEl.isJsonNull()) return players;

        JsonArray arr = playersEl.getAsJsonArray();
        for (JsonElement el : arr) {
            JsonObject obj = el.getAsJsonObject();

            // Solo fútbol
            String sport = getString(obj, "strSport");
            if (!sport.equalsIgnoreCase("Soccer") && !sport.equalsIgnoreCase("Football")) continue;

            Player p = new Player();
            p.setId(getInt(obj, "idPlayer"));
            p.setName(getString(obj, "strPlayer"));
            p.setNationality(getString(obj, "strNationality"));
            p.setPosition(getString(obj, "strPosition"));
            p.setPhoto(getString(obj, "strThumb"));

            // Datos extra que guardaremos en campos del jugador
            p.setTeamName(getString(obj, "strTeam"));
            p.setBirthDate(getString(obj, "dateBorn"));
            p.setDescription(getString(obj, "strDescriptionES") .isEmpty()
                    ? getString(obj, "strDescriptionEN")
                    : getString(obj, "strDescriptionES"));

            players.add(p);
        }
        return players;
    }

    /**
     * Obtiene los honores/trofeos de un jugador por su ID de TheSportsDB.
     */
    public String getHonours(int playerId) throws Exception {
        String url = BASE_URL + "/lookuphonours.php?id=" + playerId;
        JsonObject response = makeRequest(url);
        if (response == null) return "";

        JsonElement honoursEl = response.get("honours");
        if (honoursEl == null || honoursEl.isJsonNull()) return "Sin datos de trofeos";

        JsonArray arr = honoursEl.getAsJsonArray();
        if (arr.size() == 0) return "Sin trofeos registrados";

        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (JsonElement el : arr) {
            if (count >= 10) { sb.append("...y más"); break; }
            JsonObject obj = el.getAsJsonObject();
            String honour = getString(obj, "strHonour");
            String season = getString(obj, "strSeason");
            if (!honour.isEmpty()) {
                sb.append("• ").append(honour);
                if (!season.isEmpty()) sb.append(" (").append(season).append(")");
                sb.append("\n");
                count++;
            }
        }
        return sb.toString().trim();
    }

    private JsonObject makeRequest(String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", "FutbolStats-TFG/1.0")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("Error TheSportsDB: código " + response.statusCode());
        }

        String body = response.body();
        if (body == null || body.trim().equals("null") || body.trim().isEmpty()) return null;

        return JsonParser.parseString(body).getAsJsonObject();
    }

    private String getString(JsonObject obj, String key) {
        if (obj == null || !obj.has(key) || obj.get(key).isJsonNull()) return "";
        return obj.get(key).getAsString();
    }

    private int getInt(JsonObject obj, String key) {
        if (obj == null || !obj.has(key) || obj.get(key).isJsonNull()) return 0;
        try { return obj.get(key).getAsInt(); } catch (Exception e) { return 0; }
    }
}
