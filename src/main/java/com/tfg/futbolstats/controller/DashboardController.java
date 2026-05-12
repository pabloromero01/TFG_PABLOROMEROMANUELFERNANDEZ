package com.tfg.futbolstats.controller;

import com.tfg.futbolstats.model.Player;
import com.tfg.futbolstats.model.PlayerStats;
import com.tfg.futbolstats.service.FavoritesService;
import com.tfg.futbolstats.service.LeagueService;
import com.tfg.futbolstats.service.PlayerService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    // Sidebar
    @FXML private Label  sidebarUsername;
    @FXML private Button btnDashboard, btnPlayer, btnCompare, btnLeagues, btnWorst, btnSettings, btnGame, btnFavorites;

    // Páginas
    @FXML private VBox pageDashboard, pagePlayer, pageCompare, pageLeagues, pageWorst, pageSettings, pageGame, pageFavorites;
    @FXML private Label welcomeLabel, statusLabel;

    // --- Jugadores ---
    @FXML private TextField        playerSearchField;
    @FXML private ComboBox<String> playerSeasonCombo;
    @FXML private ListView<Player> playerResultList;
    @FXML private VBox playerDetailPanel, playerEmptyState;
    @FXML private Label playerNameLabel, playerNatLabel, playerAgeLabel, playerPosLabel, playerTeamLabel;
    @FXML private Label statGoals, statAssists, statApps, statMins, statRating;
    @FXML private GridPane attackGrid, defenseGrid;

    // --- Comparar ---
    @FXML private ComboBox<String> compareSeasonCombo;
    @FXML private TextField        compareSearch1, compareSearch2;
    @FXML private ListView<Player> compareList1, compareList2;
    @FXML private VBox             compareInfo1, compareInfo2;
    @FXML private VBox             compareSearchBox1, compareSearchBox2;
    @FXML private Label            compareName1, compareName2, compareTeam1, compareTeam2;
    @FXML private Button           compareBtn;
    @FXML private Label            scoreLabel;
    @FXML private ScrollPane       compareScrollPane;
    @FXML private VBox             compareStatsContainer;

    // --- Ligas ---
    @FXML private ComboBox<String> leagueCombo, leagueSeasonCombo;
    @FXML private HBox  leagueTabsBox;
    @FXML private Button tabGoals, tabAssists, tabShots, tabPasses, tabRating, tabYellow;
    @FXML private VBox   topPlayersContainer, leagueEmptyState;

    // --- Peores ---
    @FXML private ComboBox<String> worstLeagueCombo, worstSeasonCombo;
    @FXML private HBox  worstTabsBox;
    @FXML private Button worstTabRed, worstTabYellow, worstTabRating, worstTabGoals;
    @FXML private VBox   worstPlayersContainer, worstEmptyState;

    // --- Favoritos ---
    @FXML private VBox             favContainer, favEmptyState;
    @FXML private ComboBox<String> favSortCombo;
    @FXML private Label            favCountLabel;
    @FXML private Button           favBtn;
    @FXML private Label            favStatusLabel;
    @FXML private Label            compareSeasonHint;

    // --- Ajustes ---
    @FXML private ComboBox<String> defaultSeasonCombo;
    @FXML private Label settingsUsername, settingsApiLabel, seasonSavedLabel, colorSavedLabel;
    @FXML private Button btnColorGreen, btnColorBlue, btnColorOrange, btnColorPurple, btnColorRed;
    private String pendingAccent = "#00c97a"; // color seleccionado pero no guardado aún

    private final PlayerService   playerService   = new PlayerService();
    private final LeagueService   leagueService   = new LeagueService();
    private final FavoritesService favoritesService = FavoritesService.getInstance();
    private Player currentPlayerDetail; // jugador actualmente visible en detalle

    private Player selectedCompare1, selectedCompare2;
    private List<Player> currentTopPlayers;
    private List<Player> currentWorstPlayers;
    private String currentTab   = "goals";
    private String currentWorstTab = "red";
    private String currentAccent  = "#00c97a";
    private String loggedUsername = "";

    private static final String HINT =
            "Jugadores disponibles: Messi, Cristiano Ronaldo, Mbappe, Haaland, " +
                    "Neymar, Lewandowski, Modric, Benzema, De Bruyne, Vinicius, Bellingham, " +
                    "Pedri, Gavi, Kane, Salah, Foden, Saka, Musiala, Kroos, Ramos, Casillas...";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupSeasonCombos();
        setupLeagueCombos();
        setupPlayerList();
        setupCompareLists();
        setupFavSortCombo();
        showPage(pageDashboard);
    }

    public void setUsername(String username) {
        loggedUsername = username;
        sidebarUsername.setText(username);
        welcomeLabel.setText(getGreeting() + ", " + username.split(" ")[0] + " 👋");
        settingsUsername.setText(username);
    }

    // =============================================
    // NAVEGACIÓN
    // =============================================
    @FXML private void onNavDashboard() { showPage(pageDashboard); setActive(btnDashboard); }
    @FXML private void onNavPlayer()    { showPage(pagePlayer);    setActive(btnPlayer); }
    @FXML private void onNavCompare()   { showPage(pageCompare);   setActive(btnCompare); }
    @FXML private void onNavLeagues()   { showPage(pageLeagues);   setActive(btnLeagues); }
    @FXML private void onNavWorst()     { showPage(pageWorst);     setActive(btnWorst); }
    @FXML private void onNavSettings()  { showPage(pageSettings);  setActive(btnSettings); }
    @FXML private void onNavGame()     { showPage(pageGame);     setActive(btnGame); }
    @FXML private void onNavFavorites(){ showPage(pageFavorites); setActive(btnFavorites); loadFavorites(); }

    private void showPage(VBox page) {
        for (VBox p : List.of(pageDashboard, pagePlayer, pageCompare, pageLeagues, pageWorst, pageSettings, pageGame, pageFavorites)) {
            p.setVisible(false); p.setManaged(false);
        }
        page.setVisible(true); page.setManaged(true);
    }

    private void setActive(Button active) {
        for (Button b : List.of(btnDashboard, btnPlayer, btnCompare, btnLeagues, btnWorst, btnSettings, btnGame, btnFavorites))
            b.getStyleClass().remove("nav-btn-active");
        active.getStyleClass().add("nav-btn-active");
    }

    @FXML private void onLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Scene scene = new Scene(loader.load(), 900, 600);
            scene.getStylesheets().add(getClass().getResource("/fxml/style.css").toExternalForm());
            Stage stage = (Stage) sidebarUsername.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("FútbolStats — Iniciar sesión");
        } catch (Exception e) { e.printStackTrace(); }
    }

    // =============================================
    // JUGADORES
    // =============================================
    @FXML private void onSearchPlayer() {
        String name   = playerSearchField.getText().trim();
        String season = playerSeasonCombo.getValue();
        if (name.isEmpty()) { setStatus(HINT); return; }
        setStatus("Buscando '" + name + "'...");
        playerResultList.setItems(FXCollections.observableArrayList());
        playerDetailPanel.setVisible(false); playerDetailPanel.setManaged(false);
        playerEmptyState.setVisible(true);   playerEmptyState.setManaged(true);
        new Thread(() -> {
            try {
                List<Player> results = playerService.search(name, season);
                Platform.runLater(() -> {
                    playerResultList.setItems(FXCollections.observableArrayList(results));
                    setStatus(results.size() + " jugador(es) encontrado(s)");
                });
            } catch (Exception e) {
                Platform.runLater(() -> setStatus("⚠ " + e.getMessage() + "  |  " + HINT));
            }
        }).start();
    }

    private void setupPlayerList() {
        playerResultList.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, p) -> { if (p != null) showPlayerDetail(p); });
        playerResultList.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Player p, boolean empty) {
                super.updateItem(p, empty);
                if (empty || p == null) { setText(null); setGraphic(null); return; }
                String team = p.getStats() != null ? p.getStats().getTeamName() : "";
                VBox box = new VBox(2);
                Label name = new Label(p.getName());
                name.setStyle("-fx-font-size:13px;-fx-font-weight:700;");
                box.getChildren().add(name);
                if (!team.isEmpty()) {
                    Label teamLbl = new Label(team);
                    teamLbl.setStyle("-fx-font-size:11px;-fx-text-fill:#555;");
                    box.getChildren().add(teamLbl);
                }
                setGraphic(box); setText(null);
            }
        });
    }

    private void showPlayerDetail(Player player) {
        currentPlayerDetail = player;
        playerEmptyState.setVisible(false); playerEmptyState.setManaged(false);
        playerDetailPanel.setVisible(true); playerDetailPanel.setManaged(true);
        updateFavBtn(player);
        playerNameLabel.setText(player.getName());
        playerNatLabel.setText("🌍 " + nvl(player.getNationality()));
        playerAgeLabel.setText("📅 " + (player.getAge() > 0 ? player.getAge() + " años" : "—"));
        playerPosLabel.setText("📍 " + nvl(player.getPosition()));
        PlayerStats s = player.getStats();
        if (s != null) {
            playerTeamLabel.setText(nvl(s.getTeamName()) + " · " + nvl(s.getLeagueName()));
            statGoals.setText(String.valueOf(s.getGoals()));
            statAssists.setText(String.valueOf(s.getAssists()));
            statApps.setText(String.valueOf(s.getAppearances()));
            statMins.setText(String.valueOf(s.getMinutesPlayed()));
            statRating.setText(s.getRating() > 0 ? String.format("%.2f", s.getRating()) : "—");
            buildStatsGrids(s);
        }
    }

    private void buildStatsGrids(PlayerStats s) {
        attackGrid.getChildren().clear();
        defenseGrid.getChildren().clear();
        addGridRow(attackGrid,  0, "Tiros totales",    String.valueOf(s.getShots()));
        addGridRow(attackGrid,  1, "Tiros a puerta",   String.valueOf(s.getShotsOnTarget()));
        addGridRow(attackGrid,  2, "Pases clave",      String.valueOf(s.getKeyPasses()));
        addGridRow(attackGrid,  3, "Precisión pase",   String.format("%.1f%%", s.getPassAccuracy()));
        addGridRow(attackGrid,  4, "Pases totales",    String.valueOf(s.getTotalPasses()));
        addGridRow(attackGrid,  5, "Goles/partido",    String.format("%.2f", s.getGoalsPerGame()));
        addGridRow(defenseGrid, 0, "Entradas",          String.valueOf(s.getTackles()));
        addGridRow(defenseGrid, 1, "Intercepciones",    String.valueOf(s.getInterceptions()));
        addGridRow(defenseGrid, 2, "Duelos totales",    String.valueOf(s.getDuelsTotal()));
        addGridRow(defenseGrid, 3, "Duelos ganados",    String.format("%.1f%%", s.getDuelsWonPercent()));
        addGridRow(defenseGrid, 4, "Tarjetas amarillas",String.valueOf(s.getYellowCards()));
        addGridRow(defenseGrid, 5, "Tarjetas rojas",    String.valueOf(s.getRedCards()));
    }

    private void addGridRow(GridPane grid, int row, String label, String value) {
        Label lbl = new Label(label); lbl.setStyle("-fx-text-fill:#9a9a9a;-fx-font-size:12px;");
        Label val = new Label(value); val.setStyle("-fx-text-fill:#f0f0f0;-fx-font-size:13px;-fx-font-weight:600;");
        GridPane.setHgrow(lbl, Priority.ALWAYS);
        grid.add(lbl, 0, row); grid.add(val, 1, row);
    }

    // =============================================
    // COMPARAR
    // =============================================
    private void setupCompareLists() {
        compareList1.getSelectionModel().selectedItemProperty().addListener((obs, old, p) -> {
            if (p == null) return;
            selectedCompare1 = p;
            compareName1.setText(p.getName());
            compareTeam1.setText(p.getStats() != null ? nvl(p.getStats().getTeamName()) : "");
            // Ocultar búsqueda, mostrar info
            compareSearchBox1.setVisible(false); compareSearchBox1.setManaged(false);
            compareInfo1.setVisible(true);       compareInfo1.setManaged(true);
            setStatus("✔ " + p.getName() + " seleccionado como Jugador 1");
        });
        compareList2.getSelectionModel().selectedItemProperty().addListener((obs, old, p) -> {
            if (p == null) return;
            selectedCompare2 = p;
            compareName2.setText(p.getName());
            compareTeam2.setText(p.getStats() != null ? nvl(p.getStats().getTeamName()) : "");
            compareSearchBox2.setVisible(false); compareSearchBox2.setManaged(false);
            compareInfo2.setVisible(true);       compareInfo2.setManaged(true);
            setStatus("✔ " + p.getName() + " seleccionado como Jugador 2");
        });
        for (ListView<Player> lv : List.of(compareList1, compareList2)) {
            lv.setCellFactory(l -> new ListCell<>() {
                @Override protected void updateItem(Player p, boolean empty) {
                    super.updateItem(p, empty);
                    setText(empty || p == null ? null : p.getName());
                    if (!empty && p != null) setStyle("-fx-text-fill:#00c97a;-fx-font-size:13px;-fx-padding:6 12;-fx-font-weight:600;");
                }
            });
        }
    }

    @FXML private void onResetCompare1() {
        selectedCompare1 = null;
        compareSearch1.clear();
        compareList1.setItems(FXCollections.observableArrayList());
        compareInfo1.setVisible(false);       compareInfo1.setManaged(false);
        compareSearchBox1.setVisible(true);   compareSearchBox1.setManaged(true);
        scoreLabel.setVisible(false);         scoreLabel.setManaged(false);
        compareScrollPane.setVisible(false);  compareScrollPane.setManaged(false);
        setStatus("Busca un nuevo Jugador 1");
    }

    @FXML private void onResetCompare2() {
        selectedCompare2 = null;
        compareSearch2.clear();
        compareList2.setItems(FXCollections.observableArrayList());
        compareInfo2.setVisible(false);       compareInfo2.setManaged(false);
        compareSearchBox2.setVisible(true);   compareSearchBox2.setManaged(true);
        scoreLabel.setVisible(false);         scoreLabel.setManaged(false);
        compareScrollPane.setVisible(false);  compareScrollPane.setManaged(false);
        setStatus("Busca un nuevo Jugador 2");
    }

    @FXML private void onResetCompareAll() {
        onResetCompare1();
        onResetCompare2();
        setStatus("Comparación reiniciada — busca dos jugadores");
    }

    @FXML private void onSearchCompare1() { searchForCompare(compareSearch1.getText(), compareList1); }
    @FXML private void onSearchCompare2() { searchForCompare(compareSearch2.getText(), compareList2); }

    private void searchForCompare(String name, ListView<Player> list) {
        if (name.trim().isEmpty()) { setStatus(HINT); return; }
        setStatus("Buscando '" + name + "'...");
        new Thread(() -> {
            try {
                List<Player> results = playerService.search(name.trim(), compareSeasonCombo.getValue());
                Platform.runLater(() -> { list.setItems(FXCollections.observableArrayList(results)); setStatus("Selecciona el jugador"); });
            } catch (Exception e) { Platform.runLater(() -> setStatus("⚠ " + e.getMessage())); }
        }).start();
    }

    @FXML private void onCompare() {
        if (selectedCompare1 == null || selectedCompare2 == null) { setStatus("Selecciona ambos jugadores"); return; }
        Map<String, Integer> comparison = playerService.compare(selectedCompare1, selectedCompare2);
        if (comparison.isEmpty()) { setStatus("⚠ Sin estadísticas de temporada para estos jugadores"); return; }
        int[] score = playerService.getScore(comparison);
        scoreLabel.setText(score[0] + " — " + score[1]);
        scoreLabel.setVisible(true); scoreLabel.setManaged(true);
        buildCompareTable(comparison, selectedCompare1, selectedCompare2);
        compareScrollPane.setVisible(true); compareScrollPane.setManaged(true);
        setStatus("✔ " + selectedCompare1.getName() + " vs " + selectedCompare2.getName());
    }

    private void buildCompareTable(Map<String, Integer> comparison, Player p1, Player p2) {
        compareStatsContainer.getChildren().clear();
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(0, 0, 10, 0));
        Label h1 = styledLabel(p1.getName(), "#f0f0f0", "16", "700", 180, Pos.CENTER);
        Label hm = styledLabel("ESTADÍSTICA", "#555",   "11", "600", 150, Pos.CENTER);
        Label h2 = styledLabel(p2.getName(), "#f0f0f0", "16", "700", 180, Pos.CENTER);
        HBox.setHgrow(h1, Priority.ALWAYS); HBox.setHgrow(h2, Priority.ALWAYS);
        header.getChildren().addAll(h1, hm, h2);
        compareStatsContainer.getChildren().add(header);

        for (Map.Entry<String, Integer> entry : comparison.entrySet()) {
            String stat = entry.getKey(); int winner = entry.getValue();
            String v1 = getStatValue(p1, stat), v2 = getStatValue(p2, stat);
            HBox row = new HBox();
            row.setAlignment(Pos.CENTER);
            row.setPadding(new Insets(12, 20, 12, 20));
            row.setStyle("-fx-background-color:#1e1e1e;-fx-background-radius:8;-fx-border-color:#2a2a2a;-fx-border-radius:8;-fx-border-width:1;");
            String c1 = winner == 1 ? currentAccent : "#555555";
            String c2 = winner == 2 ? currentAccent : "#555555";
            Label lv1 = styledLabel(v1, c1, "18", winner==1?"700":"400", 180, Pos.CENTER);
            Label ls  = styledLabel(stat, "#9a9a9a", "12", "400", 150, Pos.CENTER);
            Label lv2 = styledLabel(v2, c2, "18", winner==2?"700":"400", 180, Pos.CENTER);
            HBox.setHgrow(lv1, Priority.ALWAYS); HBox.setHgrow(lv2, Priority.ALWAYS);
            row.getChildren().addAll(lv1, ls, lv2);
            compareStatsContainer.getChildren().add(row);
        }
    }

    private Label styledLabel(String text, String color, String size, String weight, double minW, Pos align) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill:" + color + ";-fx-font-size:" + size + "px;-fx-font-weight:" + weight + ";");
        l.setMinWidth(minW); l.setAlignment(align); return l;
    }

    private String getStatValue(Player p, String stat) {
        if (p.getStats() == null) return "-";
        return switch (stat) {
            case "Goles"           -> String.valueOf(p.getStats().getGoals());
            case "Asistencias"     -> String.valueOf(p.getStats().getAssists());
            case "Partidos"        -> String.valueOf(p.getStats().getAppearances());
            case "Minutos jugados" -> String.valueOf(p.getStats().getMinutesPlayed());
            case "Tiros a puerta"  -> String.valueOf(p.getStats().getShotsOnTarget());
            case "Pases clave"     -> String.valueOf(p.getStats().getKeyPasses());
            case "Precisión pase"  -> String.format("%.1f%%", p.getStats().getPassAccuracy());
            case "Entradas"        -> String.valueOf(p.getStats().getTackles());
            case "Intercepciones"  -> String.valueOf(p.getStats().getInterceptions());
            case "Duelos ganados%" -> String.format("%.1f%%", p.getStats().getDuelsWonPercent());
            case "Valoración"      -> String.format("%.2f", p.getStats().getRating());
            default -> "-";
        };
    }

    // =============================================
    // LIGAS — MEJORES
    // =============================================
    @FXML private void onLoadLeague() {
        String league = leagueCombo.getValue(), season = leagueSeasonCombo.getValue();
        if (league == null) { setStatus("Selecciona una liga"); return; }
        setStatus("Cargando " + league + "...");
        leagueEmptyState.setVisible(false); leagueEmptyState.setManaged(false);
        topPlayersContainer.setVisible(false); topPlayersContainer.setManaged(false);
        new Thread(() -> {
            try {
                int id = leagueService.getLeagueId(league);
                currentTopPlayers = leagueService.getTopPlayers(id, season, currentTab);
                Platform.runLater(() -> {
                    leagueTabsBox.setVisible(true); leagueTabsBox.setManaged(true);
                    renderTopPlayers(currentTopPlayers, currentTab, false);
                    setStatus("✔ " + league + " cargada");
                });
            } catch (Exception e) {
                Platform.runLater(() -> { setStatus("⚠ " + e.getMessage()); leagueEmptyState.setVisible(true); leagueEmptyState.setManaged(true); });
            }
        }).start();
    }

    @FXML private void onTabGoals()   { switchTab("goals",   tabGoals,   false); }
    @FXML private void onTabAssists() { switchTab("assists", tabAssists, false); }
    @FXML private void onTabShots()   { switchTab("shots",   tabShots,   false); }
    @FXML private void onTabPasses()  { switchTab("passes",  tabPasses,  false); }
    @FXML private void onTabRating()  { switchTab("rating",  tabRating,  false); }
    @FXML private void onTabYellow()  { switchTab("yellow",  tabYellow,  false); }

    private void switchTab(String tab, Button activeBtn, boolean isWorst) {
        if (!isWorst) {
            currentTab = tab;
            for (Button b : List.of(tabGoals, tabAssists, tabShots, tabPasses, tabRating, tabYellow)) {
                b.getStyleClass().removeAll("top-tab-active");
            }
            activeBtn.getStyleClass().add("top-tab-active");
            if (currentTopPlayers != null) renderTopPlayers(currentTopPlayers, tab, false);
        } else {
            currentWorstTab = tab;
            for (Button b : List.of(worstTabRed, worstTabYellow, worstTabRating, worstTabGoals)) {
                b.getStyleClass().removeAll("top-tab-active", "worst-tab-active");
            }
            activeBtn.getStyleClass().add("worst-tab-active");
            if (currentWorstPlayers != null) renderTopPlayers(currentWorstPlayers, tab, true);
        }
    }

    private void renderTopPlayers(List<Player> players, String tab, boolean worst) {
        VBox container = worst ? worstPlayersContainer : topPlayersContainer;
        container.getChildren().clear();
        container.setVisible(true); container.setManaged(true);

        List<Player> sorted = worst
                ? leagueService.sortByTabWorst(players, tab)
                : leagueService.sortByTab(players, tab);

        String[] medals = {"🥇","🥈","🥉","4","5"};
        String accentColor = worst ? "#e05252" : currentAccent;

        for (int i = 0; i < Math.min(5, sorted.size()); i++) {
            Player p = sorted.get(i);
            HBox row = new HBox(16);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(14, 20, 14, 20));
            row.setStyle("-fx-background-color:#1e1e1e;-fx-background-radius:10;-fx-border-color:#2a2a2a;-fx-border-radius:10;-fx-border-width:1;");

            Label rank = new Label(medals[i]);
            rank.setStyle("-fx-font-size:20px;-fx-min-width:32;");

            VBox info = new VBox(3);
            Label name = new Label(p.getName());
            name.setStyle("-fx-text-fill:#f0f0f0;-fx-font-size:14px;-fx-font-weight:600;");
            Label team = new Label(p.getStats() != null ? nvl(p.getStats().getTeamName()) : "");
            team.setStyle("-fx-text-fill:#9a9a9a;-fx-font-size:12px;");
            info.getChildren().addAll(name, team);
            HBox.setHgrow(info, Priority.ALWAYS);

            VBox valueBox = new VBox(2);
            valueBox.setAlignment(Pos.CENTER_RIGHT);
            Label value = new Label(leagueService.getTabValue(p, tab));
            value.setStyle("-fx-text-fill:" + accentColor + ";-fx-font-size:24px;-fx-font-weight:800;");
            Label unit = new Label(getTabUnit(tab));
            unit.setStyle("-fx-text-fill:#555;-fx-font-size:11px;");
            valueBox.getChildren().addAll(value, unit);

            row.getChildren().addAll(rank, info, valueBox);
            container.getChildren().add(row);
        }
    }

    private String getTabUnit(String tab) {
        return switch (tab) {
            case "goals","worstGoals"  -> "goles";
            case "assists"             -> "asist.";
            case "shots"               -> "tiros";
            case "passes"              -> "pases clave";
            case "rating","worstRating"-> "rating";
            case "yellow","worstYellow"-> "amarillas";
            case "red"                 -> "rojas";
            default -> "";
        };
    }

    // =============================================
    // PEORES
    // =============================================
    @FXML private void onLoadWorst() {
        String league = worstLeagueCombo.getValue(), season = worstSeasonCombo.getValue();
        if (league == null) { setStatus("Selecciona una liga"); return; }
        setStatus("Cargando peores de " + league + "...");
        worstEmptyState.setVisible(false); worstEmptyState.setManaged(false);
        worstPlayersContainer.setVisible(false); worstPlayersContainer.setManaged(false);
        new Thread(() -> {
            try {
                int id = leagueService.getLeagueId(league);
                // Para peores: cargamos el top de rojas (que es un endpoint específico)
                currentWorstPlayers = leagueService.getTopPlayers(id, season, "yellow");
                Platform.runLater(() -> {
                    worstTabsBox.setVisible(true); worstTabsBox.setManaged(true);
                    renderTopPlayers(currentWorstPlayers, currentWorstTab, true);
                    setStatus("✔ Peores de " + league + " cargados");
                });
            } catch (Exception e) {
                Platform.runLater(() -> { setStatus("⚠ " + e.getMessage()); worstEmptyState.setVisible(true); worstEmptyState.setManaged(true); });
            }
        }).start();
    }

    @FXML private void onWorstTabRed()    { switchTab("red",        worstTabRed,    true); }
    @FXML private void onWorstTabYellow() { switchTab("yellow",     worstTabYellow, true); }
    @FXML private void onWorstTabRating() { switchTab("worstRating",worstTabRating, true); }
    @FXML private void onWorstTabGoals()  { switchTab("worstGoals", worstTabGoals,  true); }

    // =============================================
    // AJUSTES
    // =============================================
    @FXML private void onColorGreen()  { applyAccent("#00c97a", btnColorGreen);  }
    @FXML private void onColorBlue()   { applyAccent("#3b8eea", btnColorBlue);   }
    @FXML private void onColorOrange() { applyAccent("#f5a623", btnColorOrange); }
    @FXML private void onColorPurple() { applyAccent("#a855f7", btnColorPurple); }
    @FXML private void onColorRed()    { applyAccent("#e05252", btnColorRed);    }

    private void applyAccent(String color, Button activeBtn) {
        pendingAccent = color; // Solo marcar como pendiente
        for (Button b : List.of(btnColorGreen, btnColorBlue, btnColorOrange, btnColorPurple, btnColorRed))
            b.getStyleClass().remove("color-btn-active");
        activeBtn.getStyleClass().add("color-btn-active");
        colorSavedLabel.setText("Sin guardar — pulsa 'Guardar color'");
        colorSavedLabel.setStyle("-fx-text-fill:#f5a623;-fx-font-size:12px;");
    }

    @FXML private void onSaveAccentColor() {
        currentAccent = pendingAccent;
        sidebarUsername.getScene().getRoot().setStyle("-fx-accent: " + currentAccent + ";");
        colorSavedLabel.setText("✔ Color guardado correctamente");
        colorSavedLabel.setStyle("-fx-text-fill:#00c97a;-fx-font-size:12px;-fx-font-weight:600;");
        setStatus("✔ Color de acento actualizado");
    }

    @FXML private void onSaveDefaultSeason() {
        String season = defaultSeasonCombo.getValue();
        if (season == null) return;
        playerSeasonCombo.setValue(season);
        compareSeasonCombo.setValue(season);
        leagueSeasonCombo.setValue(season);
        worstSeasonCombo.setValue(season);
        seasonSavedLabel.setText("✔ Guardado");
        setStatus("Temporada por defecto: " + season);
    }

    // =============================================
    // RECARGAR TEMPORADA EN COMPARAR
    // =============================================
    @FXML private void onReloadCompareSeason() {
        String season = compareSeasonCombo.getValue();
        if (selectedCompare1 == null && selectedCompare2 == null) {
            setStatus("Selecciona al menos un jugador primero"); return;
        }
        setStatus("Recargando datos para temporada " + season + "...");
        compareSeasonHint.setText("Cargando...");

        new Thread(() -> {
            try {
                if (selectedCompare1 != null) {
                    var results = playerService.search(selectedCompare1.getName(), season);
                    if (!results.isEmpty()) selectedCompare1 = results.get(0);
                }
                if (selectedCompare2 != null) {
                    var results = playerService.search(selectedCompare2.getName(), season);
                    if (!results.isEmpty()) selectedCompare2 = results.get(0);
                }
                Platform.runLater(() -> {
                    // Actualizar info mostrada
                    if (selectedCompare1 != null) {
                        compareName1.setText(selectedCompare1.getName());
                        compareTeam1.setText(selectedCompare1.getStats() != null
                                ? nvl(selectedCompare1.getStats().getTeamName()) : "");
                    }
                    if (selectedCompare2 != null) {
                        compareName2.setText(selectedCompare2.getName());
                        compareTeam2.setText(selectedCompare2.getStats() != null
                                ? nvl(selectedCompare2.getStats().getTeamName()) : "");
                    }
                    compareSeasonHint.setText("✔ Datos actualizados a temporada " + season);
                    // Si ya había tabla, regenerarla
                    if (compareScrollPane.isVisible() && selectedCompare1 != null && selectedCompare2 != null) {
                        var comparison = playerService.compare(selectedCompare1, selectedCompare2);
                        int[] score = playerService.getScore(comparison);
                        scoreLabel.setText(score[0] + " — " + score[1]);
                        buildCompareTable(comparison, selectedCompare1, selectedCompare2);
                    }
                    setStatus("✔ Temporada actualizada a " + season);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    compareSeasonHint.setText("⚠ " + e.getMessage());
                    setStatus("Error: " + e.getMessage());
                });
            }
        }).start();
    }

    // =============================================
    // FAVORITOS
    // =============================================
    private void setupFavSortCombo() {
        favSortCombo.setItems(FXCollections.observableArrayList(
                "RECIENTES", "NOMBRE", "GOLES", "ASISTENCIAS", "PARTIDOS", "MINUTOS", "VALORACIÓN"
        ));
        favSortCombo.setValue("RECIENTES");
    }

    @FXML private void onToggleFavorite() {
        if (currentPlayerDetail == null) return;
        String season = playerSeasonCombo.getValue();
        try {
            boolean already = favoritesService.isFavorite(currentPlayerDetail.getId(), season);
            if (already) {
                favoritesService.removeFavorite(currentPlayerDetail.getId(), season);
                favBtn.setText("⭐ Añadir a favoritos");
                favStatusLabel.setText("Eliminado de favoritos");
                favStatusLabel.setStyle("-fx-text-fill:#e05252;-fx-font-size:12px;");
            } else {
                boolean ok = favoritesService.addFavorite(currentPlayerDetail, season);
                if (ok) {
                    favBtn.setText("💛 En favoritos");
                    favStatusLabel.setText("✔ Añadido a favoritos");
                    favStatusLabel.setStyle("-fx-text-fill:#00c97a;-fx-font-size:12px;");
                } else {
                    favStatusLabel.setText("Ya estaba guardado");
                }
            }
        } catch (Exception e) {
            favStatusLabel.setText("Error: " + e.getMessage());
        }
    }

    private void updateFavBtn(Player player) {
        try {
            String season = playerSeasonCombo.getValue();
            boolean fav = favoritesService.isFavorite(player.getId(), season);
            favBtn.setText(fav ? "💛 En favoritos" : "⭐ Añadir a favoritos");
            favStatusLabel.setText("");
        } catch (Exception e) {
            favBtn.setText("⭐ Añadir a favoritos");
        }
    }

    @FXML private void onSortFavorites() { loadFavorites(); }

    private void loadFavorites() {
        try {
            String order = favSortCombo.getValue() != null ? favSortCombo.getValue() : "RECIENTES";
            List<Player> favs = favoritesService.getFavorites(order);
            favContainer.getChildren().clear();

            boolean empty = favs.isEmpty();
            favEmptyState.setVisible(empty); favEmptyState.setManaged(empty);
            favContainer.setVisible(!empty); favContainer.setManaged(!empty);
            favCountLabel.setText(favs.size() + (favs.size() == 1 ? " favorito" : " favoritos"));

            for (Player p : favs) {
                favContainer.getChildren().add(buildFavCard(p));
            }
        } catch (Exception e) {
            setStatus("Error cargando favoritos: " + e.getMessage());
        }
    }

    private HBox buildFavCard(Player player) {
        HBox card = new HBox(16);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(16, 20, 16, 20));
        card.setStyle("-fx-background-color:#1e1e1e;-fx-background-radius:10;" +
                "-fx-border-color:#2a2a2a;-fx-border-radius:10;-fx-border-width:1;-fx-cursor:hand;");
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color:#252525;-fx-background-radius:10;" +
                "-fx-border-color:#00c97a;-fx-border-radius:10;-fx-border-width:1;-fx-cursor:hand;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color:#1e1e1e;-fx-background-radius:10;" +
                "-fx-border-color:#2a2a2a;-fx-border-radius:10;-fx-border-width:1;-fx-cursor:hand;"));

        // Info principal
        VBox info = new VBox(4);
        Label name = new Label(player.getName());
        name.setStyle("-fx-text-fill:#f0f0f0;-fx-font-size:15px;-fx-font-weight:700;");
        Label team = new Label(player.getStats() != null ? nvl(player.getStats().getTeamName()) : "—");
        team.setStyle("-fx-text-fill:#9a9a9a;-fx-font-size:12px;");
        Label season = new Label("Temporada " + (player.getStats() != null ? player.getStats().getSeason() : "—"));
        season.setStyle("-fx-text-fill:#555;-fx-font-size:11px;");
        info.getChildren().addAll(name, team, season);
        HBox.setHgrow(info, Priority.ALWAYS);

        // Stats rápidas
        PlayerStats s = player.getStats();
        if (s != null) {
            HBox stats = new HBox(20);
            stats.setAlignment(Pos.CENTER);
            stats.getChildren().addAll(
                    miniStat("⚽", String.valueOf(s.getGoals()), "Goles"),
                    miniStat("🎯", String.valueOf(s.getAssists()), "Asist."),
                    miniStat("📋", String.valueOf(s.getAppearances()), "Partidos"),
                    miniStat("⭐", String.format("%.2f", s.getRating()), "Rating")
            );
            card.getChildren().addAll(info, stats);
        } else {
            card.getChildren().add(info);
        }

        // Botón ver perfil
        Button viewBtn = new Button("Ver perfil →");
        viewBtn.setStyle("-fx-background-color:transparent;-fx-text-fill:#00c97a;-fx-font-size:12px;" +
                "-fx-font-weight:600;-fx-cursor:hand;-fx-border-color:#00c97a;-fx-border-radius:6;-fx-padding:6 12;");
        viewBtn.setOnAction(e -> {
            showPage(pagePlayer); setActive(btnPlayer);
            showPlayerDetail(player);
            playerDetailPanel.setVisible(true); playerDetailPanel.setManaged(true);
            playerEmptyState.setVisible(false);  playerEmptyState.setManaged(false);
        });

        // Botón eliminar
        Button delBtn = new Button("🗑");
        delBtn.setStyle("-fx-background-color:transparent;-fx-text-fill:#e05252;-fx-font-size:14px;-fx-cursor:hand;");
        delBtn.setOnAction(e -> {
            try {
                String season2 = player.getStats() != null ? player.getStats().getSeason() : "";
                favoritesService.removeFavorite(player.getId(), season2);
                loadFavorites();
                setStatus("Eliminado de favoritos: " + player.getName());
            } catch (Exception ex) { setStatus("Error: " + ex.getMessage()); }
        });

        card.getChildren().addAll(viewBtn, delBtn);
        return card;
    }

    private VBox miniStat(String icon, String value, String label) {
        VBox box = new VBox(2);
        box.setAlignment(Pos.CENTER);
        Label ico = new Label(icon + " " + value);
        ico.setStyle("-fx-text-fill:#f0f0f0;-fx-font-size:13px;-fx-font-weight:700;");
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill:#555;-fx-font-size:10px;");
        box.getChildren().addAll(ico, lbl);
        return box;
    }

    // =============================================
    // HELPERS
    // =============================================
    private void setupSeasonCombos() {
        List<String> seasons = List.of("2024","2023","2022");
        for (ComboBox<String> cb : List.of(playerSeasonCombo, compareSeasonCombo,
                leagueSeasonCombo, worstSeasonCombo, defaultSeasonCombo)) {
            cb.setItems(FXCollections.observableArrayList(seasons));
            cb.setValue("2023");
        }
    }

    private void setupLeagueCombos() {
        List<String> leagues = List.of(
                "La Liga (España)", "Premier League (Inglaterra)",
                "Serie A (Italia)", "Bundesliga (Alemania)",
                "Ligue 1 (Francia)", "Champions League"
        );
        leagueCombo.setItems(FXCollections.observableArrayList(leagues));
        worstLeagueCombo.setItems(FXCollections.observableArrayList(leagues));
    }

    private String getGreeting() {
        int h = LocalTime.now().getHour();
        return h < 12 ? "Buenos días" : h < 20 ? "Buenas tardes" : "Buenas noches";
    }
    private void setStatus(String msg) { statusLabel.setText(msg); }
    private String nvl(String s) { return s != null && !s.isEmpty() ? s : "—"; }
}
