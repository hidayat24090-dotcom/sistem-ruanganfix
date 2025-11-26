package com.sistemruangan.view;

import com.sistemruangan.MainApp;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * Controller untuk halaman Dashboard
 */
public class DashboardController {
    
    @FXML private Button btnRuangan;
    @FXML private Button btnPeminjaman;
    @FXML private Button btnLogout;
    
    @FXML
    public void initialize() {
        setupButtonEffects(btnRuangan);
        setupButtonEffects(btnPeminjaman);
        setupButtonEffects(btnLogout);
    }
    
    /**
     * Setup hover effect untuk button
     */
    private void setupButtonEffects(Button button) {
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web("#5B9BD5"));
        shadow.setRadius(10);
        
        button.setOnMouseEntered(_ -> {
            button.setEffect(shadow);
            FadeTransition ft = new FadeTransition(Duration.millis(200), button);
            ft.setFromValue(1.0);
            ft.setToValue(0.8);
            ft.play();
        });
        
        button.setOnMouseExited(_ -> {
            button.setEffect(null);
            FadeTransition ft = new FadeTransition(Duration.millis(200), button);
            ft.setFromValue(0.8);
            ft.setToValue(1.0);
            ft.play();
        });
    }
    
    @FXML
    private void handleDaftarRuangan() {
        MainApp.showRuanganScene();
    }
    
    @FXML
    private void handleDataPeminjaman() {
        MainApp.showPeminjamanScene();
    }
    
    @FXML
    private void handleLogout() {
        MainApp.showLoginScene();
    }
}