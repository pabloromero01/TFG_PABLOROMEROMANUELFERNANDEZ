package com.tfg.futbolstats.service;

import com.tfg.futbolstats.model.GamePlayer;

import java.util.*;

/**
 * Servicio del minijuego "¿Quién tiene más?".
 * Datos precargados de temporada 2023 para no gastar peticiones API.
 */
public class GameService {

    public static final String[] CATEGORIES = {
            "GOLES", "ASISTENCIAS", "PARTIDOS", "MINUTOS", "TIROS", "AMARILLAS"
    };

    public static final Map<String, String> CATEGORY_ICONS = Map.of(
            "GOLES",       "⚽",
            "ASISTENCIAS", "🎯",
            "PARTIDOS",    "📋",
            "MINUTOS",     "⏱",
            "TIROS",       "🥅",
            "AMARILLAS",   "🟨"
    );

    public static final Map<String, String> CATEGORY_DESC = Map.of(
            "GOLES",       "¿Quién marcó más goles?",
            "ASISTENCIAS", "¿Quién dio más asistencias?",
            "PARTIDOS",    "¿Quién jugó más partidos?",
            "MINUTOS",     "¿Quién acumuló más minutos?",
            "TIROS",       "¿Quién metió más tiros a puerta?",
            "AMARILLAS",   "¿Quién recibió más amarillas?"
    );

    // Base de datos de jugadores con stats reales de temporada 2023
    private static final List<GamePlayer> PLAYERS = List.of(
            new GamePlayer("Lionel Messi",       "Inter Miami",    "Argentina", 11, 14, 14,  1020, 1, 0, 7.92, 28,  "2023", ""),
            new GamePlayer("Cristiano Ronaldo",  "Al-Nassr",       "Portugal",  35,  5, 31,  2608, 5, 1, 7.15, 77,  "2023", ""),
            new GamePlayer("Kylian Mbappé",      "PSG",            "Francia",   29, 10, 33,  2767, 3, 0, 7.85, 94,  "2023", ""),
            new GamePlayer("Erling Haaland",     "Man. City",      "Noruega",   36,  8, 35,  2789, 3, 0, 7.98, 108, "2023", ""),
            new GamePlayer("Neymar Jr",          "Al-Hilal",       "Brasil",     5,  4,  7,   497, 1, 0, 7.24, 18,  "2023", ""),
            new GamePlayer("Robert Lewandowski", "Barcelona",      "Polonia",   26,  7, 34,  2790, 3, 1, 7.45, 85,  "2023", ""),
            new GamePlayer("Luka Modrić",        "Real Madrid",    "Croacia",    4,  7, 32,  2176, 5, 0, 7.38, 20,  "2023", ""),
            new GamePlayer("Karim Benzema",      "Al-Ittihad",     "Francia",   14,  9, 21,  1714, 2, 0, 7.12, 42,  "2023", ""),
            new GamePlayer("Kevin De Bruyne",    "Man. City",      "Bélgica",    7, 16, 18,  1428, 2, 0, 7.88, 28,  "2023", ""),
            new GamePlayer("Vinicius Jr",        "Real Madrid",    "Brasil",    24, 11, 33,  2675, 9, 1, 7.82, 75,  "2023", ""),
            new GamePlayer("Jude Bellingham",    "Real Madrid",    "Inglaterra",19,  6, 35,  3082, 8, 1, 7.94, 52,  "2023", ""),
            new GamePlayer("Pedri",              "Barcelona",      "España",     4,  7, 30,  2477, 5, 0, 7.45, 28,  "2023", ""),
            new GamePlayer("Gavi",               "Barcelona",      "España",     5,  9, 28,  2133, 9, 0, 7.48, 18,  "2023", ""),
            new GamePlayer("Harry Kane",         "Bayern Munich",  "Inglaterra",44, 12, 45,  3847, 8, 1, 8.12, 138, "2023", ""),
            new GamePlayer("Mohamed Salah",      "Liverpool",      "Egipto",    19, 12, 37,  3118, 2, 0, 7.72, 72,  "2023", ""),
            new GamePlayer("Phil Foden",         "Man. City",      "Inglaterra",19, 10, 35,  2544, 4, 0, 7.78, 64,  "2023", ""),
            new GamePlayer("Bukayo Saka",        "Arsenal",        "Inglaterra",20, 14, 38,  3240, 5, 0, 7.85, 62,  "2023", ""),
            new GamePlayer("Jamal Musiala",      "Bayern Munich",  "Alemania",  12, 11, 32,  2458, 4, 0, 7.68, 48,  "2023", ""),
            new GamePlayer("Toni Kroos",         "Real Madrid",    "Alemania",   4, 11, 30,  2542, 3, 0, 7.72, 20,  "2023", ""),
            new GamePlayer("Antoine Griezmann",  "Atlético Madrid","Francia",   14, 10, 38,  3124, 7, 0, 7.55, 48,  "2023", ""),
            new GamePlayer("Alexis Mac Allister","Liverpool",      "Argentina",  8,  5, 37,  3034, 7, 0, 7.42, 28,  "2023", ""),
            new GamePlayer("Rodri",              "Man. City",      "España",     8,  8, 37,  3200, 9, 1, 7.88, 28,  "2023", ""),
            new GamePlayer("Marcus Rashford",    "Man. United",    "Inglaterra",17,  5, 42,  3412, 8, 0, 7.28, 66,  "2023", ""),
            new GamePlayer("Lautaro Martínez",   "Inter Milan",    "Argentina", 24,  7, 33,  2714, 5, 0, 7.72, 82,  "2023", ""),
            new GamePlayer("Victor Osimhen",     "Napoli",         "Nigeria",   26,  5, 32,  2548, 4, 1, 7.82, 88,  "2023", ""),
            new GamePlayer("Rúben Dias",         "Man. City",      "Portugal",   1,  2, 34,  2988, 4, 0, 7.48,  4,  "2023", ""),
            new GamePlayer("Virgil van Dijk",    "Liverpool",      "Holanda",    3,  2, 35,  3108, 4, 0, 7.38,  8,  "2023", ""),
            new GamePlayer("Alisson Becker",     "Liverpool",      "Brasil",     0,  0, 32,  2880, 1, 0, 7.12,  0,  "2023", ""),
            new GamePlayer("Thibaut Courtois",   "Real Madrid",    "Bélgica",    0,  0,  9,   810, 0, 0, 7.05,  0,  "2023", ""),
            new GamePlayer("Raphinha",           "Barcelona",      "Brasil",    16,  8, 38,  3016, 5, 0, 7.45, 62,  "2023", "")
    );

    private final Random random = new Random();

    /** Devuelve una categoría aleatoria */
    public String randomCategory() {
        return CATEGORIES[random.nextInt(CATEGORIES.length)];
    }

    /** Devuelve dos jugadores aleatorios distintos, garantizando que no empaten en la categoría */
    public GamePlayer[] randomPair(String category) {
        List<GamePlayer> pool = new ArrayList<>(PLAYERS);
        Collections.shuffle(pool);

        for (int i = 0; i < pool.size() - 1; i++) {
            for (int j = i + 1; j < pool.size(); j++) {
                GamePlayer a = pool.get(i), b = pool.get(j);
                // Evitar empates
                if (a.getValueForCategory(category) != b.getValueForCategory(category)) {
                    return new GamePlayer[]{a, b};
                }
            }
        }
        // Fallback si no se encuentra par sin empate
        return new GamePlayer[]{pool.get(0), pool.get(1)};
    }

    /**
     * Comprueba si el jugador elegido tiene un valor MAYOR en la categoría.
     * @param chosen  jugador que eligió el usuario
     * @param other   el otro jugador
     */
    public boolean isCorrect(GamePlayer chosen, GamePlayer other, String category) {
        // En AMARILLAS, el correcto es quien tiene MÁS (más amarillas = peor, pero el juego pregunta "quién tiene más")
        return chosen.getValueForCategory(category) > other.getValueForCategory(category);
    }

    public List<GamePlayer> getAllPlayers() { return PLAYERS; }
}
