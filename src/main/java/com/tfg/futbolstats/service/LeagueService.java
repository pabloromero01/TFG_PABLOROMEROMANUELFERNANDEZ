package com.tfg.futbolstats.service;

import com.google.gson.*;
import com.tfg.futbolstats.config.AppConfig;
import com.tfg.futbolstats.model.Player;
import com.tfg.futbolstats.model.PlayerStats;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class LeagueService {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ApiFootballService helper = new ApiFootballService();

    private static final Map<String, Integer> LEAGUE_IDS = Map.of(
            "La Liga (España)",            140,
            "Premier League (Inglaterra)",  39,
            "Serie A (Italia)",            135,
            "Bundesliga (Alemania)",        78,
            "Ligue 1 (Francia)",            61,
            "Champions League",              2
    );

    public int getLeagueId(String leagueName) {
        return LEAGUE_IDS.getOrDefault(leagueName, 140);
    }

    public List<Player> getTopPlayers(int leagueId, String season, String tab) throws Exception {
        String endpoint = switch (tab) {
            case "assists" -> "/players/topassists";
            case "yellow"  -> "/players/topyellowcards";
            default        -> "/players/topscorers";
        };

        String url = AppConfig.getBaseUrl() + endpoint + "?league=" + leagueId + "&season=" + season;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("x-apisports-key", AppConfig.getApiKey())
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 429) throw new Exception("Límite diario de 100 peticiones alcanzado.");
        if (response.statusCode() != 200) throw new Exception("Error API: " + response.statusCode());

        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
        JsonElement errors = json.get("errors");
        if (errors != null && !errors.isJsonNull() && errors.isJsonObject()) {
            JsonObject errObj = errors.getAsJsonObject();
            if (errObj.has("plan")) throw new Exception("Plan gratuito: usa temporadas 2022, 2023 o 2024.");
            if (errObj.size() > 0)  throw new Exception("Error API: " + errObj.toString());
        }
        return parsePlayers(json);
    }

    // Ordena de mejor a peor (para Rankings)
    public List<Player> sortByTab(List<Player> players, String tab) {
        Comparator<Player> comp = switch (tab) {
            case "goals"   -> Comparator.comparingInt(p -> -safe(p).getGoals());
            case "assists" -> Comparator.comparingInt(p -> -safe(p).getAssists());
            case "shots"   -> Comparator.comparingInt(p -> -safe(p).getShotsOnTarget());
            case "passes"  -> Comparator.comparingInt(p -> -safe(p).getKeyPasses());
            case "rating"  -> Comparator.comparingDouble(p -> -safe(p).getRating());
            case "yellow"  -> Comparator.comparingInt(p -> -safe(p).getYellowCards());
            default        -> Comparator.comparingInt(p -> -safe(p).getGoals());
        };
        return players.stream().sorted(comp).limit(5).toList();
    }

    // Ordena de PEOR a mejor (para sección Peores)
    public List<Player> sortByTabWorst(List<Player> players, String tab) {
        Comparator<Player> comp = switch (tab) {
            case "red"         -> Comparator.comparingInt(p -> -safe(p).getRedCards());
            case "yellow"      -> Comparator.comparingInt(p -> -safe(p).getYellowCards());
            case "worstRating" -> Comparator.comparingDouble(p -> safe(p).getRating()); // menor = peor
            case "worstGoals"  -> Comparator.comparingInt(p -> safe(p).getGoals());    // menos goles
            default            -> Comparator.comparingInt(p -> -safe(p).getRedCards());
        };
        return players.stream().sorted(comp).limit(5).toList();
    }

    public String getTabValue(Player p, String tab) {
        if (p.getStats() == null) return "-";
        return switch (tab) {
            case "goals","worstGoals"   -> String.valueOf(p.getStats().getGoals());
            case "assists"              -> String.valueOf(p.getStats().getAssists());
            case "shots"                -> String.valueOf(p.getStats().getShotsOnTarget());
            case "passes"               -> String.valueOf(p.getStats().getKeyPasses());
            case "rating","worstRating" -> String.format("%.2f", p.getStats().getRating());
            case "yellow","worstYellow" -> String.valueOf(p.getStats().getYellowCards());
            case "red"                  -> String.valueOf(p.getStats().getRedCards());
            default                     -> "-";
        };
    }

    private List<Player> parsePlayers(JsonObject response) {
        List<Player> players = new ArrayList<>();
        JsonArray arr = response.getAsJsonArray("response");
        if (arr == null) return players;

        for (JsonElement el : arr) {
            JsonObject entry = el.getAsJsonObject();
            JsonObject pj = entry.getAsJsonObject("player");
            if (pj == null) continue;

            Player p = new Player();
            p.setId(helper.getInt(pj, "id"));
            p.setName(helper.getString(pj, "name"));
            p.setAge(helper.getInt(pj, "age"));
            p.setNationality(helper.getString(pj, "nationality"));
            p.setPhoto(helper.getString(pj, "photo"));

            JsonArray statsArr = entry.getAsJsonArray("statistics");
            if (statsArr != null && statsArr.size() > 0) {
                PlayerStats stats = helper.parseStats(statsArr.get(0).getAsJsonObject());
                p.setStats(stats);
                p.setPosition(stats.getPosition() != null ? stats.getPosition() : "");
            }
            players.add(p);
        }
        return players;
    }

    private PlayerStats safe(Player p) {
        return p.getStats() != null ? p.getStats() : new PlayerStats();
    }
}
