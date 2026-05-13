package com.tfg.futbolstats.service;

import java.util.HashMap;
import java.util.Map;

/**
 * Mapa de IDs de api-football para jugadores conocidos.
 * Los IDs son fijos y permanentes en la API.
 * Se puede ampliar con más jugadores.
 */
public class PlayerIdMap {

    // Mapa: nombre normalizado (minúsculas, sin acentos) -> ID en api-football
    private static final Map<String, Integer> IDS = new HashMap<>();

    static {
        // Estrellas mundiales
        IDS.put("lionel messi",        154);
        IDS.put("leo messi",           154);
        IDS.put("messi",               154);
        IDS.put("cristiano ronaldo",   874);
        IDS.put("cristiano",           874);
        IDS.put("ronaldo",             874);
        IDS.put("kylian mbappe",       278);
        IDS.put("mbappe",              278);
        IDS.put("erling haaland",      1100);
        IDS.put("haaland",             1100);
        IDS.put("neymar",              276);
        IDS.put("neymar jr",           276);
        IDS.put("robert lewandowski",  521);
        IDS.put("lewandowski",         521);
        IDS.put("luka modric",         184);
        IDS.put("modric",              184);
        IDS.put("karim benzema",       148);
        IDS.put("benzema",             148);
        IDS.put("kevin de bruyne",     627);
        IDS.put("de bruyne",           627);
        IDS.put("virgil van dijk",     306);
        IDS.put("van dijk",            306);
        IDS.put("Mohamed Salah",       257);
        IDS.put("salah",               257);
        IDS.put("sadio mane",          236);
        IDS.put("mane",                236);
        IDS.put("harry kane",          184614);
        IDS.put("kane",                184614);
        IDS.put("vinicius junior",     1485767);
        IDS.put("vinicius",            1485767);
        IDS.put("vinicius jr",         1485767);
        IDS.put("pedri",               1485656);
        IDS.put("fermin lopez",        306688);
        IDS.put("fermin",              306688);
        IDS.put("gavi",                1485762);
        IDS.put("jude bellingham",     1485745);
        IDS.put("bellingham",          1485745);
        IDS.put("phil foden",          627949);
        IDS.put("foden",               627949);
        IDS.put("bukayo saka",         1100439);
        IDS.put("saka",                1100439);
        IDS.put("jamal musiala",       1485741);
        IDS.put("musiala",             1485741);
        IDS.put("kaka",                8087);
        IDS.put("zinedine zidane",     2295);
        IDS.put("zidane",              2295);
        IDS.put("ronaldinho",          2305);
        IDS.put("thierry henry",       2413);
        IDS.put("henry",               2413);
        IDS.put("zlatan ibrahimovic",  123);
        IDS.put("ibrahimovic",         123);
        IDS.put("ibra",                123);
        IDS.put("antoine griezmann",   2295);
        IDS.put("griezmann",           1485);
        IDS.put("raheem sterling",     627950);
        IDS.put("sterling",            627950);
        IDS.put("toni kroos",          183);
        IDS.put("kroos",               183);
        IDS.put("sergio ramos",        186);
        IDS.put("ramos",               186);
        IDS.put("gerard pique",        185);
        IDS.put("pique",               185);
        IDS.put("iker casillas",       503);
        IDS.put("casillas",            503);
        IDS.put("xavi",                10320);
        IDS.put("andres iniesta",      2299);
        IDS.put("iniesta",             2299);
        IDS.put("wayne rooney",        1160);
        IDS.put("rooney",              1160);
        IDS.put("didier drogba",       4087);
        IDS.put("drogba",              4087);
        IDS.put("david beckham",       1569);
        IDS.put("beckham",             1569);
    }

    /**
     * Busca el ID de api-football para un nombre de jugador.
     * @return ID numérico, o -1 si no está en el mapa.
     */
    public static int getApiFootballId(String playerName) {
        if (playerName == null) return -1;
        String normalized = playerName.toLowerCase()
                .replace("á","a").replace("é","e").replace("í","i")
                .replace("ó","o").replace("ú","u").replace("ñ","n")
                .trim();
        return IDS.getOrDefault(normalized, -1);
    }
}
