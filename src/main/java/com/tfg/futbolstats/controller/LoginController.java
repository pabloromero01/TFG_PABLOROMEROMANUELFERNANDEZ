package com.tfg.futbolstats.controller;

import com.tfg.futbolstats.service.DatabaseService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.sql.SQLException;
import java.util.Properties;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private VBox loginPanel;
    @FXML private VBox registerPanel;

    @FXML private TextField     loginEmail;
    @FXML private PasswordField loginPassword;
    @FXML private Label         loginError;
    @FXML private CheckBox      recordarCheck;

    @FXML private TextField     regName;
    @FXML private TextField     regEmail;
    @FXML private PasswordField regPassword;
    @FXML private PasswordField regConfirm;
    @FXML private Label         registerError;

    private DatabaseService db;

    private static final String PREFS_PATH =
            System.getProperty("user.home") + "/FutbolStats/recordar.properties";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        db = DatabaseService.getInstance();
        cargarCredenciales();
    }

    // =============================================
    // RECORDAR CREDENCIALES
    // =============================================

    private void cargarCredenciales() {
        File f = new File(PREFS_PATH);
        if (!f.exists()) return;
        try (InputStream is = new FileInputStream(f)) {
            Properties p = new Properties();
            p.load(is);
            String email = p.getProperty("email", "");
            String pass  = p.getProperty("pass",  "");
            if (!email.isEmpty()) {
                loginEmail.setText(email);
                loginPassword.setText(pass);
                recordarCheck.setSelected(true);
            }
        } catch (IOException e) {
            System.err.println("Error cargando credenciales: " + e.getMessage());
        }
    }

    private void guardarCredenciales(String email, String pass) {
        try {
            Files.createDirectories(Paths.get(PREFS_PATH).getParent());
            Properties p = new Properties();
            p.setProperty("email", email);
            p.setProperty("pass",  pass);
            try (OutputStream os = new FileOutputStream(PREFS_PATH)) {
                p.store(os, "FutbolStats - credenciales guardadas");
            }
        } catch (IOException e) {
            System.err.println("Error guardando credenciales: " + e.getMessage());
        }
    }

    private void borrarCredenciales() {
        try { Files.deleteIfExists(Paths.get(PREFS_PATH)); }
        catch (IOException e) { System.err.println("Error borrando credenciales: " + e.getMessage()); }
    }

    // =============================================
    // LOGIN
    // =============================================

    @FXML
    private void onLogin() {
        String email = loginEmail.getText().trim();
        String pass  = loginPassword.getText();

        if (email.isEmpty() || pass.isEmpty()) { showLoginError("Rellena todos los campos"); return; }

        try {
            String name = db.login(email, pass);
            if (name == null) { showLoginError("Email o contraseña incorrectos"); return; }

            if (recordarCheck.isSelected()) {
                guardarCredenciales(email, pass);
            } else {
                borrarCredenciales();
            }

            openDashboard(name);
        } catch (SQLException e) {
            showLoginError("Error de base de datos: " + e.getMessage());
        }
    }

    // =============================================
    // REGISTRO
    // =============================================

    @FXML
    private void onRegister() {
        String name    = regName.getText().trim();
        String email   = regEmail.getText().trim();
        String pass    = regPassword.getText();
        String confirm = regConfirm.getText();

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
            showRegisterError("Rellena todos los campos"); return;
        }
        if (!email.contains("@"))  { showRegisterError("El correo no es válido"); return; }
        if (pass.length() < 6)     { showRegisterError("La contraseña debe tener mínimo 6 caracteres"); return; }
        if (!pass.equals(confirm)) { showRegisterError("Las contraseñas no coinciden"); return; }

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