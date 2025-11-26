package com.sistemruangan;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.stage.Stage;
import com.sistemruangan.util.DatabaseConnection;

/**
 * Main Application Class with Fixed Fullscreen Support
 */
public class MainApp extends Application {
    
    private static Stage primaryStage;
    private static boolean isFullscreen = false;
    
    @Override
    public void start(Stage stage) {
        try {
            System.out.println("========================================");
            System.out.println("   APLIKASI STARTING...");
            System.out.println("========================================");
            
            // Test koneksi database
            System.out.println("\n[1/3] Testing database connection...");
            if (!DatabaseConnection.testConnection()) {
                showErrorDialog("Database Error", 
                    "Gagal koneksi ke database!\n\n" +
                    "Pastikan:\n" +
                    "1. MySQL service sudah running\n" +
                    "2. Database 'sistem_ruangan' sudah dibuat\n" +
                    "3. Port dan password di DatabaseConnection.java sudah benar");
                System.exit(1);
            }
            System.out.println("✅ Database connection OK");
            
            primaryStage = stage;
            
            // Disable fullscreen exit hint (THIS IS KEY!)
            primaryStage.setFullScreenExitHint("");
            
            // Load User Login Scene
            System.out.println("\n[2/3] Loading User Login scene...");
            showUserLogin();
            
            primaryStage.setTitle("Sistem Inventaris & Peminjaman Ruangan");
            
            // Setup fullscreen toggle (F11)
            setupFullscreenToggle();
            
            // Setup fullscreen event listener
            primaryStage.fullScreenProperty().addListener((obs, wasFullScreen, isNowFullScreen) -> {
                isFullscreen = isNowFullScreen;
                if (isNowFullScreen) {
                    System.out.println("🖥️  Fullscreen Mode: ON");
                } else {
                    System.out.println("🖥️  Fullscreen Mode: OFF");
                }
            });
            
            System.out.println("\n[3/3] Showing stage...");
            primaryStage.show();
            
            System.out.println("\n✅ Application started successfully!");
            System.out.println("📌 Tekan F11 untuk toggle fullscreen");
            System.out.println("========================================\n");
            
        } catch (Exception e) {
            System.err.println("\n❌ FATAL ERROR:");
            e.printStackTrace();
            showErrorDialog("Application Error", 
                "Aplikasi gagal dimulai!\n\n" +
                "Error: " + e.getMessage() + "\n\n" +
                "Lihat console untuk detail error.");
        }
    }
    
    /**
     * Setup fullscreen toggle dengan F11
     */
    private static void setupFullscreenToggle() {
        if (primaryStage.getScene() != null) {
            primaryStage.getScene().getAccelerators().put(
                new KeyCodeCombination(KeyCode.F11),
                () -> toggleFullscreen()
            );
        }
    }
    
    /**
     * Toggle fullscreen mode
     */
    public static void toggleFullscreen() {
        isFullscreen = !isFullscreen;
        primaryStage.setFullScreen(isFullscreen);
        
        if (isFullscreen) {
            System.out.println("🖥️  Fullscreen toggled: ON");
        } else {
            System.out.println("🖥️  Fullscreen toggled: OFF");
        }
    }
    
    // ========== ADMIN SCENES ==========
    
    public static void showLoginScene() {
        loadScene("/fxml/Login.fxml", 700, 500, "Login Admin");
    }
    
    public static void showDashboard() {
        loadScene("/fxml/Dashboard.fxml", 800, 600, "Dashboard Admin");
    }
    
    public static void showRuanganScene() {
        loadScene("/fxml/DaftarRuangan.fxml", 1100, 700, "Daftar Ruangan");
    }
    
    public static void showPeminjamanScene() {
        loadScene("/fxml/DataPeminjaman.fxml", 1200, 700, "Data Peminjaman");
    }
    
    // ========== USER SCENES ==========
    
    public static void showUserLogin() {
        loadScene("/fxml/UserLogin.fxml", 700, 550, "Login User");
    }
    
    public static void showUserRegister() {
        loadScene("/fxml/UserRegister.fxml", 700, 650, "Registrasi User");
    }
    
    public static void showUserDashboard() {
        loadScene("/fxml/UserDashboard.fxml", 900, 650, "Dashboard User");
    }
    
    public static void showUserRuanganList() {
        loadScene("/fxml/UserRuanganList.fxml", 1000, 700, "Daftar Ruangan");
    }
    
    public static void showUserPeminjamanForm() {
        loadScene("/fxml/UserPeminjamanForm.fxml", 700, 750, "Form Peminjaman");
    }
    
    public static void showUserRiwayat() {
        loadScene("/fxml/UserRiwayat.fxml", 1100, 700, "Riwayat Peminjaman");
    }
    
    public static void showJadwalBulanan() {
        loadScene("/fxml/JadwalBulanan.fxml", 1100, 700, "Jadwal Bulanan");
    }
    
    public static void showUserProfile() {
        loadScene("/fxml/UserProfile.fxml", 700, 700, "Profile User");
    }
    
    // ========== HELPER METHOD ==========
    
    private static void loadScene(String fxmlPath, int width, int height, String title) {
        try {
            System.out.println("\n📄 Loading scene: " + fxmlPath);
            
            // Save current fullscreen state BEFORE loading
            boolean wasFullscreen = isFullscreen;
            
            // Check if file exists
            if (MainApp.class.getResource(fxmlPath) == null) {
                throw new Exception("FXML file not found: " + fxmlPath);
            }
            System.out.println("   ✅ FXML file found");
            
            // Load FXML
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource(fxmlPath));
            Parent root = loader.load();
            System.out.println("   ✅ FXML loaded successfully");
            
            // Check CSS file
            String cssPath = "/css/style.css";
            if (MainApp.class.getResource(cssPath) == null) {
                System.out.println("   ⚠️  CSS file not found: " + cssPath);
            } else {
                System.out.println("   ✅ CSS file found");
            }
            
            // Create scene
            Scene scene = new Scene(root, width, height);
            
            // Add CSS if exists
            if (MainApp.class.getResource(cssPath) != null) {
                scene.getStylesheets().add(MainApp.class.getResource(cssPath).toExternalForm());
                System.out.println("   ✅ CSS applied");
            }
            
            // IMPORTANT: If was fullscreen, temporarily exit to set new scene
            if (wasFullscreen) {
                primaryStage.setFullScreen(false);
            }
            
            // Set scene
            primaryStage.setScene(scene);
            primaryStage.setTitle(title);
            
            // Re-setup fullscreen toggle for new scene
            setupFullscreenToggle();
            
            // IMMEDIATELY restore fullscreen BEFORE showing (no delay!)
            if (wasFullscreen) {
                primaryStage.setFullScreen(true);
                System.out.println("🖥️  Fullscreen restored (no delay)");
            } else {
                primaryStage.centerOnScreen();
            }
            
            System.out.println("   ✅ Scene loaded: " + title);
            
        } catch (Exception e) {
            System.err.println("\n❌ ERROR loading scene: " + fxmlPath);
            e.printStackTrace();
            
            showErrorDialog("Scene Load Error", 
                "Gagal memuat halaman: " + title + "\n\n" +
                "File: " + fxmlPath + "\n" +
                "Error: " + e.getMessage() + "\n\n" +
                "Periksa:\n" +
                "1. File FXML ada di folder resources/fxml/\n" +
                "2. File sudah di-compile (ada di bin/fxml/)\n" +
                "3. Controller class sudah ada dan benar");
        }
    }
    
    /**
     * Show error dialog
     */
    private static void showErrorDialog(String title, String message) {
        try {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        } catch (Exception e) {
            System.err.println("Error showing dialog: " + e.getMessage());
        }
    }
    
    @Override
    public void stop() {
        DatabaseConnection.closeConnection();
        System.out.println("\n👋 Application closed.");
    }
    
    public static void main(String[] args) {
        System.out.println("\n🚀 Starting Sistem Inventaris & Peminjaman Ruangan...\n");
        launch(args);
    }
}