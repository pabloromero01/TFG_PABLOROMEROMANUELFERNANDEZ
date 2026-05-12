package com.tfg.futbolstats.controller;

import com.tfg.futbolstats.service.DatabaseService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private VBox loginPanel;
    @FXML private VBox registerPanel;

    @FXML private TextField     loginEmail;
    @FXML private PasswordField loginPassword;
    @FXML private Label         loginError;

    @FXML private TextField     regName;
    @FXML private TextField     regEmail;
    @FXML private PasswordField regPassword;
    @FXML private PasswordField regConfirm;
    @FXML private Label         registerError;

    private DatabaseService db;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        db = DatabaseService.getInstance();
    }

    @FXML
    private void onLogin() {
        String email = loginEmail.getText().trim();
        String pass  = loginPassword.getText();

        if (email.isEmpty() || pass.isEmpty()) { showLoginError("Rellena todos los campos"); return; }

        try {
            String name = db.login(email, pass);
            if (name == null) { showLoginError("Email o contraseña incorrectos"); return; }
            openDashboard(name);
        } catch (SQLException e) {
            showLoginError("Error de base de datos: " + e.getMessage());
        }
    }

    @FXML
    private void onRegister() {
        String name    = regName.getText().trim();
        String email   = regEmail.getText().trim();
        String pass    = regPassword.getText();
        String confirm = regConfirm.getText();

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
            showRegisterError("Rellena todos los campos"); return;
        }
        if (!email.contains("@"))     { showRegisterError("El correo no es válido"); return; }
        if (pass.length() < 6)        { showRegisterError("La contraseña debe tener mínimo 6 caracteres"); return; }
        if (!pass.equals(confirm))    { showRegisterError("Las contraseñas no coinciden"); return; }

        try {
            boolean ok = db.register(name, email, pass);
            if (!ok) { showRegisterError("Ya existe una cuenta con ese correo"); return; }
            openDashboard(name);
        } catch (SQLException e) {
            showRegisterError("Error al registrar: " + e.getMessage());
        }
    }

    @FXML private void onShowRegister() {
        loginPanel.setVisible(false);   loginPanel.setManaged(false);
        registerPanel.setVisible(true); registerPanel.setManaged(true);
        loginError.setText("");
    }

    @FXML private void onShowLogin() {
        registerPanel.setVisible(false); registerPanel.setManaged(false);
        loginPanel.setVisible(true);     loginPanel.setManaged(true);
        registerError.setText("");
    }

    @FXML private void onForgotPassword() {
        showLoginError("Contacta con el administrador");
    }

    private void openDashboard(String username) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
            Scene scene = new Scene(loader.load(), 1100, 700);
            scene.getStylesheets().add(getClass().getResource("/fxml/style.css").toExternalForm());

            DashboardController ctrl = loader.getController();
            ctrl.setUsername(username);

            Stage stage = (Stage) loginEmail.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("FútbolStats");
            stage.setMinWidth(900);
            stage.setMinHeight(600);
        } catch (Exception e) {
            showLoginError("Error al abrir el dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showLoginError(String msg)    { loginError.setText(msg); }
    private void showRegisterError(String msg) { registerError.setText(msg); }
}