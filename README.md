# FútbolStats — Comparador de Estadísticas de Jugadores de Fútbol

Aplicación de escritorio desarrollada en **Java + JavaFX** como Trabajo de Fin de Grado (TFG) del Ciclo Formativo de Grado Superior DAM en el **ISEN (Instituto Superior de Enseñanzas Profesionales)**.

**Autores:** Pablo Romero Fernández & Manuel Fernández Marín  
**Curso:** 2025-2026  
**Tutor:** Jairo Paul Moreno Villarroel

---

## Descripción

FútbolStats permite buscar, consultar y comparar estadísticas reales de jugadores de fútbol profesional de las principales ligas europeas. Integra APIs REST externas y una base de datos local SQLite para la gestión de cuentas, favoritos, notas personales e historial de búsquedas.

---

## Funcionalidades

- 🔐 **Login y registro** de usuarios con persistencia en SQLite y opción de recordar sesión
- 👤 **Búsqueda de jugadores** con estadísticas completas por temporada y autocompletado de búsquedas recientes
- 📝 **Notas personales** por jugador, guardadas en SQLite
- 📤 **Exportación** de fichas de jugador a TXT y comparativas a CSV
- ⚖️ **Comparador** de dos jugadores cara a cara con indicador de ganador y exportación a CSV
- 🏆 **Rankings por liga** — Top 5 goleadores, asistentes, valorados, etc.
- 📉 **Peores de la temporada** — más tarjetas, menos goles, peor valoración
- ⭐ **Favoritos** con persistencia, ordenación por múltiples criterios y resumen estadístico de medias
- 🎮 **Minijuego** "¿Quién tiene más?" con 30 jugadores y 6 categorías estadísticas
- ⚙️ **Ajustes** — color de acento personalizable y temporada por defecto

---

##  Tecnologías

| Tecnología | Versión | Uso |
|---|---|---|
| Java | 17 | Lenguaje principal |
| JavaFX | 21.0.2 | Interfaz gráfica |
| api-football (api-sports.io) | v3 | Estadísticas de jugadores |
| TheSportsDB | v1 | Búsqueda y trofeos |
| SQLite (sqlite-jdbc) | 3.45.1 | Base de datos local |
| Gson | 2.10.1 | Deserialización JSON |
| Maven | 3.6+ | Gestión de dependencias |
| JUnit | 5 | Pruebas unitarias |
| Launch4j | — | Empaquetado JAR → EXE |
| Inno Setup | — | Generación del instalador Windows |

---

##  Instalación y ejecución

### Opción A — Instalador Windows (recomendado, no requiere Java)

1. Descarga el archivo `FutbolStats_Setup.exe` desde la sección [Releases](../../releases)
2. Ejecuta el instalador con doble clic y sigue el asistente
3. La aplicación se instala en `C:\Program Files\FutbolStats\` y crea accesos directos en el escritorio y el menú Inicio
4. En el primer arranque se crea automáticamente la base de datos en `C:\Users\[usuario]\FutbolStats\users.db`

>  El instalador incluye el entorno Java embebido — no es necesario instalar nada más.

---

### Opción B — Ejecución desde el código fuente

#### Requisitos previos

- JDK 17 o superior
- Maven 3.6+
- IntelliJ IDEA (recomendado)
- Cuenta gratuita en [api-sports.io](https://dashboard.api-football.com/register)

#### Pasos

**1. Clona el repositorio**
```bash
git clone https://github.com/pabloromero01/TFG_PABLOROMEROMANUELFERNANDEZ.git
cd TFG_PABLOROMEROMANUELFERNANDEZ
```

**2. Configura la API key**
```bash
cp src/main/resources/config.properties.example src/main/resources/config.properties
```
Abre `config.properties` y añade tu API key:
```properties
api.key=TU_API_KEY_AQUI
api.host=v3.football.api-sports.io
```

**3. Ejecuta la aplicación**

Desde IntelliJ: **Maven > Plugins > javafx > javafx:run**

O desde terminal:
```bash
mvn clean javafx:run
```

---

### Base de datos y archivos generados

La base de datos SQLite y los directorios de trabajo se crean automáticamente en el primer arranque:

| Archivo / Directorio | Descripción |
|---|---|
| `~/FutbolStats/users.db` | Base de datos de usuarios, favoritos y notas |
| `~/FutbolStats/exports/` | Fichas TXT y comparativas CSV exportadas |
| `~/FutbolStats/recordar.properties` | Credenciales guardadas (si se activa "Recordar sesión") |

**Usuario demo:** `demo@tfg.com` / `1234`

---

## Estructura del proyecto

```
src/
├── main/
│   ├── java/com/tfg/futbolstats/
│   │   ├── Main.java
│   │   ├── config/
│   │   │   └── AppConfig.java
│   │   ├── controller/
│   │   │   ├── LoginController.java
│   │   │   ├── DashboardController.java
│   │   │   └── GameController.java
│   │   ├── model/
│   │   │   ├── Player.java
│   │   │   ├── PlayerStats.java
│   │   │   └── GamePlayer.java
│   │   └── service/
│   │       ├── ApiFootballService.java    — API api-football
│   │       ├── SportsDbService.java       — API TheSportsDB
│   │       ├── DatabaseService.java       — SQLite usuarios
│   │       ├── FavoritesService.java      — Favoritos
│   │       ├── PlayerService.java         — Búsqueda y comparación
│   │       ├── LeagueService.java         — Rankings por liga
│   │       ├── GameService.java           — Minijuego
│   │       ├── PlayerIdMap.java           — Mapa de IDs de jugadores
│   │       ├── NotasService.java          — Notas personales por jugador
│   │       ├── ExportService.java         — Exportación TXT y CSV
│   │       ├── BusquedaRecienteService.java — Historial y autocompletado
│   │       └── EstadisticasService.java   — Resumen estadístico de favoritos
│   └── resources/
│       ├── fxml/
│       │   ├── login.fxml
│       │   ├── dashboard.fxml
│       │   ├── game.fxml
│       │   └── style.css
│       ├── config.properties
│       └── config.properties.example
└── test/
    └── java/com/tfg/futbolstats/
        └── PlayerServiceTest.java
```

---

##  Ramas de desarrollo

| Rama | Descripción |
|---|---|
| `main` | Código estable y funcional |
| `develop` | Integración del trabajo de ambos |
| `PABLO` | Rama de trabajo de Pablo Romero |
| `MANUEL` | Rama de trabajo de Manuel Fernández |

---

##  Limitaciones de la API gratuita

- Solo disponible para temporadas **2022, 2023 y 2024**
- Máximo **100 peticiones diarias**
- La búsqueda por nombre requiere ID numérico del jugador (incluido en `PlayerIdMap.java`)

---

## Bibliografía

- [api-football Documentation](https://www.api-football.com/documentation-v3)
- [TheSportsDB API](https://www.thesportsdb.com/api.php)
- [JavaFX 21 Documentation](https://openjfx.io/javadoc/21/)
- [SQLite JDBC Driver](https://github.com/xerial/sqlite-jdbc)
- [Gson User Guide](https://github.com/google/gson/blob/main/UserGuide.md)
- [Launch4j](https://launch4j.sourceforge.net/)
- [Inno Setup](https://jrsoftware.org/isinfo.php)

---

## Licencia

Proyecto académico — TFG DAM 2025-2026 — ISEN