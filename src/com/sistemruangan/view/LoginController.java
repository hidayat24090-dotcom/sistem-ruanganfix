package com.sistemruangan.view;

import com.sistemruangan.MainApp;
import com.sistemruangan.util.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Controller untuk halaman Login
 */
public class LoginController {
    
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label errorLabel;
    
    @FXML
    public void initialize() {
        // Add hover effect
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web("#5B9BD5"));
        
        loginButton.setOnMouseEntered(e -> loginButton.setEffect(shadow));
        loginButton.setOnMouseExited(e -> loginButton.setEffect(null));
    }
    
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        
        if (username.isEmpty() || password.isEmpty()) {
            showError("Username dan password tidak boleh kosong!");
            return;
        }
        
        if (validateLogin(username, password)) {
            errorLabel.setVisible(false);
            MainApp.showDashboard();
        } else {
            showError("Username atau password salah!");
        }
    }
    
    /**
     * Validasi login ke database
     */
    private boolean validateLogin(String username, String password) {
        String query = "SELECT * FROM admin WHERE username=? AND password=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // Return true jika data ditemukan
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Menampilkan pesan error
     */
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
    
    @FXML
    private void handleEnter(javafx.scene.input.KeyEvent event) {
        if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
            handleLogin();
        }
    }
}