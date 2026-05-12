# ⚽ FútbolStats — Comparador de Estadísticas de Jugadores de Fútbol

Aplicación de escritorio desarrollada en **Java + JavaFX** como Trabajo de Fin de Grado (TFG) del Ciclo Formativo de Grado Superior DAM/DAW.

**Autores:** Pablo Romero & Manuel Fernández  
**Curso:** 2025-2026

---

## 📋 Descripción

FútbolStats permite buscar, consultar y comparar estadísticas reales de jugadores de fútbol profesional de las principales ligas europeas. Integra APIs REST externas y una base de datos local SQLite para la gestión de cuentas y favoritos.

---

## ✨ Funcionalidades

- 🔐 **Login y registro** de usuarios con persistencia en SQLite
- 👤 **Búsqueda de jugadores** con estadísticas completas por temporada
- ⚖️ **Comparador** de dos jugadores cara a cara con indicador de ganador
- 🏆 **Rankings por liga** — Top 5 goleadores, asistentes, valorados, etc.
- 📉 **Peores de la temporada** — más tarjetas, menos goles, peor valoración
- ⭐ **Favoritos** con persistencia y ordenación por múltiples criterios
- 🎮 **Minijuego** "¿Quién tiene más?" con 30 jugadores y 6 categorías
- ⚙️ **Ajustes** — color de acento personalizable y temporada por defecto

---

## 🛠️ Tecnologías

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

---

## 🚀 Instalación y ejecución

### Requisitos previos

- JDK 17 o superior
- Maven 3.6+
- IntelliJ IDEA (recomendado)
- Cuenta gratuita en [api-sports.io](https://dashboard.api-football.com/register)

### Pasos

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
mvn javafx:run
```

### Base de datos

La base de datos SQLite se crea automáticamente en el primer arranque:
- **Windows:** `C:\Users\[usuario]\FutbolStats\users.db`
- **macOS/Linux:** `~/FutbolStats/users.db`

**Usuario demo:** `demo@tfg.com` / `1234`

---

## 📁 Estructura del proyecto

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
│   │       ├── ApiFootballService.java
│   │       ├── SportsDbService.java
│   │       ├── DatabaseService.java
│   │       ├── FavoritesService.java
│   │       ├── PlayerService.java
│   │       ├── LeagueService.java
│   │       ├── GameService.java
│   │       └── PlayerIdMap.java
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

## 🌿 Ramas de desarrollo

| Rama | Descripción |
|---|---|
| `main` | Código estable y funcional |
| `develop` | Integración del trabajo de ambos |
| `PABLO` | Rama de trabajo de Pablo Romero |
| `MANUEL` | Rama de trabajo de Manuel Fernández |

---

## ⚠️ Limitaciones de la API gratuita

- Solo disponible para temporadas **2022, 2023 y 2024**
- Máximo **100 peticiones diarias**
- La búsqueda por nombre requiere ID numérico del jugador (incluido en `PlayerIdMap.java`)

---

## 📚 Bibliografía

- [api-football Documentation](https://www.api-football.com/documentation-v3)
- [TheSportsDB API](https://www.thesportsdb.com/api.php)
- [JavaFX 21 Documentation](https://openjfx.io/javadoc/21/)
- [SQLite JDBC Driver](https://github.com/xerial/sqlite-jdbc)
- [Gson User Guide](https://github.com/google/gson/blob/main/UserGuide.md)

---

## 📄 Licencia

Proyecto académico — TFG DAM/DAW 2025-2026