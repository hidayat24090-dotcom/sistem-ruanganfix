package com.sistemruangan;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import com.sistemruangan.util.DatabaseConnection;

/**
 * Main Application Class - SINGLE SCENE (Zero Flicker!)
 * Revolutionary approach: Satu Scene, ganti Content saja!
 */
public class MainApp extends Application {
    
    private static Stage primaryStage;
    private static StackPane rootContainer;
    private static Scene mainScene;
    private static boolean isFullscreen = false;
    
    @Override
    public void start(Stage stage) {
        try {
            System.out.println("========================================");
            System.out.println("   APLIKASI STARTING...");
            System.out.println("========================================");
            
            // Test koneksi database
            System.out.println("\n[1/4] Testing database connection...");
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
            
            // [REVOLUTIONARY CHANGE] Buat SATU SCENE saja untuk semua halaman!
            System.out.println("\n[2/4] Creating main scene container...");
            rootContainer = new StackPane();
            rootContainer.setStyle("-fx-background-color: transparent;");
            
            // Get screen size for adaptive layout
            javafx.stage.Screen screen = javafx.stage.Screen.getPrimary();
            double screenWidth = screen.getBounds().getWidth();
            double screenHeight = screen.getBounds().getHeight();
            
            mainScene = new Scene(rootContainer, screenWidth, screenHeight);
            
            // Load CSS
            String cssPath = "/css/style.css";
            if (MainApp.class.getResource(cssPath) != null) {
                mainScene.getStylesheets().add(MainApp.class.getResource(cssPath).toExternalForm());
                System.out.println("✅ CSS loaded");
            }
            
            // Set scene SEKALI SAJA! (tidak akan pernah diganti lagi!)
            primaryStage.setScene(mainScene);
            primaryStage.setTitle("Sistem Inventaris & Peminjaman Ruangan");
            
            // Disable fullscreen exit hint
            primaryStage.setFullScreenExitHint("");
            
            // Setup fullscreen toggle
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
            
            System.out.println("\n[3/4] Loading initial content...");
            // Load konten pertama (User Login)
            showUserLogin();
            
            System.out.println("\n[4/4] Showing stage...");
            primaryStage.show();
            
            // START IN FULLSCREEN BY DEFAULT
            primaryStage.setFullScreen(true);
            isFullscreen = true;
            System.out.println("🖥️  Starting in FULLSCREEN mode (default)");
            
            System.out.println("\n✅ Application started successfully!");
            System.out.println("⚡ Using SINGLE SCENE architecture (Zero Flicker!)");
            System.out.println("📌 Tekan F11 atau ESC untuk toggle fullscreen");
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
     * Setup fullscreen toggle dengan F11 dan ESC
     */
    private static void setupFullscreenToggle() {
        if (mainScene != null) {
            // F11 untuk toggle fullscreen
            mainScene.getAccelerators().put(
                new KeyCodeCombination(KeyCode.F11),
                () -> toggleFullscreen()
            );
            
            // ESC untuk keluar dari fullscreen
            mainScene.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ESCAPE && isFullscreen) {
                    toggleFullscreen();
                    event.consume();
                }
            });
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
        loadContent("/fxml/Login.fxml", "Login Admin");
    }
    
    public static void showDashboard() {
        loadContent("/fxml/Dashboard.fxml", "Dashboard Admin");
    }
    
    public static void showRuanganScene() {
        loadContent("/fxml/DaftarRuangan.fxml", "Daftar Ruangan");
    }
    
    public static void showPeminjamanScene() {
        loadContent("/fxml/DataPeminjaman.fxml", "Data Peminjaman");
    }
    
    // ========== USER SCENES ==========
    
    public static void showUserLogin() {
        loadContent("/fxml/UserLogin.fxml", "Login User");
    }
    
    public static void showUserRegister() {
        loadContent("/fxml/UserRegister.fxml", "Registrasi User");
    }
    
    public static void showUserDashboard() {
        loadContent("/fxml/UserDashboard.fxml", "Dashboard User");
    }
    
    public static void showUserRuanganList() {
        loadContent("/fxml/UserRuanganList.fxml", "Daftar Ruangan");
    }
    
    public static void showUserPeminjamanForm() {
        loadContent("/fxml/UserPeminjamanForm.fxml", "Form Peminjaman");
    }
    
    public static void showUserRiwayat() {
        loadContent("/fxml/UserRiwayat.fxml", "Riwayat Peminjaman");
    }
    
    public static void showJadwalBulanan() {
        loadContent("/fxml/JadwalBulanan.fxml", "Jadwal Bulanan");
    }
    
    public static void showUserProfile() {
        loadContent("/fxml/UserProfile.fxml", "Profile User");
    }
    
    // ========== REVOLUTIONARY METHOD: Load Content Instead of Scene! ==========
    
    /**
     * Load FXML content dan replace di rootContainer
     * INI YANG BIKIN ZERO FLICKER! ⚡
     */
    private static void loadContent(String fxmlPath, String title) {
        try {
            System.out.println("\n📄 Loading content: " + fxmlPath);
            
            // Check if file exists
            if (MainApp.class.getResource(fxmlPath) == null) {
                throw new Exception("FXML file not found: " + fxmlPath);
            }
            System.out.println("   ✅ FXML file found");
            
            // Load FXML
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource(fxmlPath));
            Parent content = loader.load();
            System.out.println("   ✅ Content loaded successfully");
            
            // [MAGIC HAPPENS HERE] Ganti content, BUKAN scene!
            // Ini yang bikin instant & zero flicker! ⚡⚡⚡
            rootContainer.getChildren().setAll(content);
            
            // Make content fill the container
            if (content instanceof javafx.scene.layout.Region) {
                javafx.scene.layout.Region region = (javafx.scene.layout.Region) content;
                region.setPrefWidth(javafx.stage.Screen.getPrimary().getBounds().getWidth());
                region.setPrefHeight(javafx.stage.Screen.getPrimary().getBounds().getHeight());
            }
            
            // Update title
            primaryStage.setTitle(title);
            
            System.out.println("   ⚡ Content swapped instantly (zero flicker!)");
            System.out.println("   ✅ Page loaded: " + title);
            
        } catch (Exception e) {
            System.err.println("\n❌ ERROR loading content: " + fxmlPath);
            e.printStackTrace();
            
            showErrorDialog("Content Load Error", 
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
        System.out.println("\n🚀 Starting Sistem Inventaris & Peminjaman Ruangan...");
        System.out.println("⚡ Single Scene Architecture - Zero Flicker Mode!\n");
        launch(args);
    }
}