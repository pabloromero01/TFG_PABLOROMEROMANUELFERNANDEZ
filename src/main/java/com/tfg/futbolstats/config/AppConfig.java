package com.tfg.futbolstats.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Carga la configuración desde config.properties.
 * config.properties NO se sube a GitHub (.gitignore).
 *
 * @author Persona A
 */
public class AppConfig {

    private static final Properties props = new Properties();

    static {
        try (InputStream is = AppConfig.class.getResourceAsStream("/config.properties")) {
            if (is != null) {
                props.load(is);
            } else {
                System.err.println("AVISO: config.properties no encontrado.");
            }
        } catch (IOException e) {
            System.err.println("Error cargando config.properties: " + e.getMessage());
        }
    }

    public static String getApiKey() {
        return props.getProperty("api.key", "");
    }

    public static String getBaseUrl() {
        return "https://v3.football.api-sports.io";
    }
}
