package com.tfg.futbolstats.controller;

import com.tfg.futbolstats.model.GamePlayer;
import com.tfg.futbolstats.service.GameService;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class GameController implements Initializable {

    // Header
    @FXML private Label scoreLabel;
    @FXML private Label recordLabel;
    @FXML private Label categoryIcon;
    @FXML private Label categoryLabel;
    @FXML private Label streakLabel;

    // Jugador izquierda
    @FXML private Label leftName;
    @FXML private Label leftTeam;
    @FXML private Label leftNat;
    @FXML private Label leftValue;
    @FXML private Label leftValueLabel;
    @FXML private VBox  leftCard;

    // Jugador derecha
    @FXML private Label rightName;
    @FXML private Label rightTeam;
    @FXML private Label rightNat;
    @FXML private VBox  rightCard;
    @FXML private VBox  rightValueHidden;
    @FXML private VBox  rightValueRevealed;
    @FXML private Label rightValue;
    @FXML private Label rightValueLabel;

    // Pregunta central
    @FXML private Label questionLabel;
    @FXML private Button btnLeft;
    @FXML private Button btnRight;
    @FXML private VBox   questionBox;

    // Pantalla Game Over
    @FXML private VBox  gameOverPanel;
    @FXML private Label gameOverScore;
    @FXML private Label gameOverRecord;
    @FXML private Label gameOverMsg;
    @FXML private VBox  gamePanel;

    // Selector de categoría
    @FXML private HBox  categorySelector;
    @FXML private ComboBox<String> categoryCombo;

    private final GameService gameService = new GameService();
    private GamePlayer leftPlayer, rightPlayer;
    private String currentCategory;
    private int score = 0;
    private int record = 0;
    private boolean gameOver = false;
    private boolean waitingNext = false; // esperando que el usuario confirme la respuesta

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupCategoryCombo();
        startNewGame();
    }

    private void setupCategoryCombo() {
        categoryCombo.getItems().addAll(
                "🎲 Aleatoria",
                "⚽ Goles",
                "🎯 Asistencias",
                "📋 Partidos",
                "⏱ Minutos",
                "🥅 Tiros",
                "🟨 Amarillas"
        );
        categoryCombo.setValue("🎲 Aleatoria");
    }

    @FXML private void onStartGame() { startNewGame(); }
    @FXML private void onRestart()   { startNewGame(); }

    private void startNewGame() {
        score = 0;
        gameOver = false;
        waitingNext = false;
        updateScoreLabel();
        gameOverPanel.setVisible(false); gameOverPanel.setManaged(false);
        gamePanel.setVisible(true);      gamePanel.setManaged(true);
        leftCard.getStyleClass().removeAll("card-correct", "card-wrong");
        rightCard.getStyleClass().removeAll("card-correct", "card-wrong");
        nextRound();
    }

    private void nextRound() {
        // Determinar categoría
        String sel = categoryCombo.getValue();
        currentCategory = switch (sel) {
            case "⚽ Goles"       -> "GOLES";
            case "🎯 Asistencias" -> "ASISTENCIAS";
            case "📋 Partidos"    -> "PARTIDOS";
            case "⏱ Minutos"     -> "MINUTOS";
            case "🥅 Tiros"       -> "TIROS";
            case "🟨 Amarillas"   -> "AMARILLAS";
            default               -> gameService.randomCategory();
        };

        GamePlayer[] pair = gameService.randomPair(currentCategory);
        leftPlayer  = pair[0];
        rightPlayer = pair[1];

        // Actualizar UI
        categoryIcon.setText(GameService.CATEGORY_ICONS.getOrDefault(currentCategory, "⚽"));
        categoryLabel.setText(currentCategory);
        questionLabel.setText(GameService.CATEGORY_DESC.getOrDefault(currentCategory, "¿Quién tiene más?"));

        // Jugador izquierda — mostramos su valor
        leftName.setText(leftPlayer.getName().toUpperCase());
        leftTeam.setText(leftPlayer.getTeam());
        leftNat.setText(leftPlayer.getNationality());
        leftValue.setText(String.valueOf(leftPlayer.getValueForCategory(currentCategory)));
        leftValueLabel.setText(currentCategory);

        // Jugador derecha — NO mostramos valor (hay que adivinarlo)
        rightName.setText(rightPlayer.getName().toUpperCase());
        rightTeam.setText(rightPlayer.getTeam());
        rightNat.setText(rightPlayer.getNationality());

        // Etiquetas de botones
        btnLeft.setText("◀  " + leftPlayer.getName().split(" ")[0]);
        btnRight.setText(rightPlayer.getName().split(" ")[0] + "  ▶");

        // Resetear colores
        leftCard.getStyleClass().removeAll("card-correct", "card-wrong", "card-neutral");
        rightCard.getStyleClass().removeAll("card-correct", "card-wrong", "card-neutral");
        leftCard.getStyleClass().add("card-neutral");
        rightCard.getStyleClass().add("card-neutral");

        // Ocultar valor derecho de nuevo
        rightValueHidden.setVisible(true);   rightValueHidden.setManaged(true);
        rightValueRevealed.setVisible(false); rightValueRevealed.setManaged(false);

        btnLeft.setDisable(false);
        btnRight.setDisable(false);
        waitingNext = false;
    }

    @FXML private void onChooseLeft()  { evaluate(leftPlayer,  rightPlayer); }
    @FXML private void onChooseRight() { evaluate(rightPlayer, leftPlayer);  }

    private void evaluate(GamePlayer chosen, GamePlayer other) {
        if (waitingNext || gameOver) return;
        waitingNext = true;
        btnLeft.setDisable(true);
        btnRight.setDisable(true);

        // Revelar valor del jugador derecha siempre
        rightValue.setText(String.valueOf(rightPlayer.getValueForCategory(currentCategory)));
        rightValueLabel.setText(currentCategory);
        rightValueHidden.setVisible(false);   rightValueHidden.setManaged(false);
        rightValueRevealed.setVisible(true);  rightValueRevealed.setManaged(true);

        boolean correct = gameService.isCorrect(chosen, other, currentCategory);

        if (correct) {
            score++;
            if (score > record) record = score;
            updateScoreLabel();
            flashCard(chosen == leftPlayer ? leftCard : rightCard, true);
            flashCard(chosen == leftPlayer ? rightCard : leftCard, false);
            streakLabel.setText("🔥 Racha: " + score);

            // Siguiente ronda tras 1.5s
            PauseTransition pause = new PauseTransition(Duration.millis(1400));
            pause.setOnFinished(e -> nextRound());
            pause.play();
        } else {
            flashCard(chosen == leftPlayer ? leftCard : rightCard, false);
            flashCard(chosen == leftPlayer ? rightCard : leftCard, true);
            streakLabel.setText("");

            // Game Over tras 1.5s
            PauseTransition pause = new PauseTransition(Duration.millis(1400));
            pause.setOnFinished(e -> showGameOver());
            pause.play();
        }
    }

    private void flashCard(VBox card, boolean correct) {
        card.getStyleClass().removeAll("card-correct", "card-wrong", "card-neutral");
        card.getStyleClass().add(correct ? "card-correct" : "card-wrong");
    }

    private void showGameOver() {
        gameOver = true;
        gamePanel.setVisible(false);  gamePanel.setManaged(false);
        gameOverPanel.setVisible(true); gameOverPanel.setManaged(true);

        gameOverScore.setText(String.valueOf(score));
        gameOverRecord.setText("Récord: " + record);

        if (score == 0)       gameOverMsg.setText("¡Mala suerte! Intenta de nuevo 😅");
        else if (score < 3)   gameOverMsg.setText("¡Bien empezado! Puedes mejorar 💪");
        else if (score < 7)   gameOverMsg.setText("¡Buen conocimiento futbolero! ⚽");
        else if (score < 12)  gameOverMsg.setText("¡Excelente! Eres un crack 🔥");
        else                  gameOverMsg.setText("¡LEYENDA DEL FÚTBOL! 🏆🐐");
    }

    private void updateScoreLabel() {
        scoreLabel.setText("PUNTOS  " + score);
        recordLabel.setText("MI RÉCORD  " + record);
    }
}


