package com.sistemruangan;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.sistemruangan.util.DatabaseConnection;

/**
 * Main Application Class untuk Sistem Inventaris dan Peminjaman Ruangan
 * Updated with User Features
 */
public class MainApp extends Application {
    
    private static Stage primaryStage;
    
    @Override
    public void start(Stage stage) {
        try {
            // Test koneksi database
            if (!DatabaseConnection.testConnection()) {
                System.err.println("Gagal koneksi ke database!");
                System.exit(1);
            }
            
            primaryStage = stage;
            
            // Load User Login Scene sebagai halaman awal
            showUserLogin();
            
            primaryStage.setTitle("Sistem Inventaris & Peminjaman Ruangan");
            primaryStage.setResizable(false);
            primaryStage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // ========== ADMIN SCENES ==========
    
    /**
     * Menampilkan halaman Login Admin
     */
    public static void showLoginScene() {
        loadScene("/fxml/Login.fxml", 700, 500);
    }
    
    /**
     * Menampilkan Dashboard Admin
     */
    public static void showDashboard() {
        loadScene("/fxml/Dashboard.fxml", 800, 600);
    }
    
    /**
     * Menampilkan halaman Daftar Ruangan
     */
    public static void showRuanganScene() {
        loadScene("/fxml/DaftarRuangan.fxml", 1100, 700);
    }
    
    /**
     * Menampilkan halaman Data Peminjaman
     */
    public static void showPeminjamanScene() {
        loadScene("/fxml/DataPeminjaman.fxml", 1200, 700);
    }
    
    // ========== USER SCENES ==========
    
    /**
     * Menampilkan halaman Login User
     */
    public static void showUserLogin() {
        loadScene("/fxml/UserLogin.fxml", 700, 550);
    }
    
    /**
     * Menampilkan halaman Register User
     */
    public static void showUserRegister() {
        loadScene("/fxml/UserRegister.fxml", 700, 650);
    }
    
    /**
     * Menampilkan Dashboard User
     */
    public static void showUserDashboard() {
        loadScene("/fxml/UserDashboard.fxml", 900, 650);
    }
    
    /**
     * Menampilkan halaman Daftar Ruangan untuk User
     */
    public static void showUserRuanganList() {
        loadScene("/fxml/UserRuanganList.fxml", 1000, 700);
    }
    
    /**
     * Menampilkan halaman Form Peminjaman untuk User
     */
    public static void showUserPeminjamanForm() {
        loadScene("/fxml/UserPeminjamanForm.fxml", 700, 650);
    }
    
    /**
     * Menampilkan halaman Riwayat Peminjaman User
     */
    public static void showUserRiwayat() {
        loadScene("/fxml/UserRiwayat.fxml", 1100, 700);
    }
    
    /**
     * Menampilkan halaman Jadwal Bulanan
     */
    public static void showJadwalBulanan() {
        loadScene("/fxml/JadwalBulanan.fxml", 1100, 700);
    }
    
    /**
     * Menampilkan halaman Profile User
     */
    public static void showUserProfile() {
        loadScene("/fxml/UserProfile.fxml", 700, 600);
    }
    
    // ========== HELPER METHOD ==========
    
    /**
     * Load scene helper method
     */
    private static void loadScene(String fxmlPath, int width, int height) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root, width, height);
            scene.getStylesheets().add(MainApp.class.getResource("/css/style.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
        } catch (Exception e) {
            System.err.println("Error loading scene: " + fxmlPath);
            e.printStackTrace();
        }
    }
    
    @Override
    public void stop() {
        // Tutup koneksi database saat aplikasi ditutup
        DatabaseConnection.closeConnection();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}