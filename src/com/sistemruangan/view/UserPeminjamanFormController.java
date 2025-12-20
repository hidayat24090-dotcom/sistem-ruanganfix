package com.sistemruangan.view;

import com.sistemruangan.MainApp;
import com.sistemruangan.controller.PeminjamanController;
import com.sistemruangan.controller.RuanganController;
import com.sistemruangan.model.Peminjaman;
import com.sistemruangan.model.Ruangan;
import com.sistemruangan.util.SessionManager;
import com.sistemruangan.view.BuktiPeminjamanDialog;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Controller untuk Form Pengajuan Peminjaman User - BUG FIXED
 */
public class UserPeminjamanFormController {
    
    @FXML private Label lblNamaPeminjam;
    @FXML private ComboBox<Ruangan> cbRuangan;
    @FXML private TextArea txtKeperluan;
    @FXML private DatePicker dpTglPinjam;
    @FXML private DatePicker dpTglKembali;
    @FXML private VBox vboxInfoRuangan;
    @FXML private Label lblKapasitas;
    @FXML private Label lblProyektor;
    @FXML private Label lblHdmi;
    @FXML private HBox hboxDurasi;
    @FXML private Label lblDurasi;
    @FXML private Label errorLabel;
    @FXML private Button btnSubmit;
    @FXML private Button btnReset;
    @FXML private Button btnKembali;
    
    private RuanganController ruanganController;
    private PeminjamanController peminjamanController;
    private static Ruangan preSelectedRuangan = null;
    
    @FXML
    public void initialize() {
        try {
            System.out.println("🔧 Initializing UserPeminjamanFormController...");
            
            ruanganController = new RuanganController();
            peminjamanController = new PeminjamanController();
            
            // Set nama peminjam dari session
            if (SessionManager.isLoggedIn()) {
                lblNamaPeminjam.setText(SessionManager.getNamaLengkap());
                System.out.println("✅ User logged in: " + SessionManager.getNamaLengkap());
            } else {
                System.err.println("⚠️  No user logged in!");
            }
            
            // Load ruangan tersedia
            loadRuanganTersedia();
            
            // Set default dates
            dpTglPinjam.setValue(LocalDate.now());
            dpTglKembali.setValue(LocalDate.now().plusDays(1));
            
            // Check if there's a pre-selected ruangan
            if (preSelectedRuangan != null) {
                cbRuangan.setValue(preSelectedRuangan);
                handleRuanganChange();
                preSelectedRuangan = null; // Reset after use
            }
            
            calculateDuration();
            
            System.out.println("✅ UserPeminjamanFormController initialized successfully");
            
        } catch (Exception e) {
            System.err.println("❌ ERROR in initialize(): " + e.getMessage());
            e.printStackTrace();
            showError("Error saat memuat form: " + e.getMessage());
        }
    }
    
    /**
     * Load ruangan yang tersedia
     */
    private void loadRuanganTersedia() {
        try {
            System.out.println("📋 Loading available ruangan...");
            ObservableList<Ruangan> allRuangan = ruanganController.getAllRuangan();
            cbRuangan.getItems().clear();
            
            int count = 0;
            for (Ruangan r : allRuangan) {
                if ("tersedia".equalsIgnoreCase(r.getStatus())) {
                    cbRuangan.getItems().add(r);
                    count++;
                }
            }
            
            System.out.println("✅ Loaded " + count + " available ruangan");
            
        } catch (Exception e) {
            System.err.println("❌ ERROR loading ruangan: " + e.getMessage());
            e.printStackTrace();
            showError("Gagal memuat daftar ruangan: " + e.getMessage());
        }
    }
    
    /**
     * Static method untuk set ruangan yang dipilih dari halaman list
     */
    public static void setSelectedRuangan(Ruangan ruangan) {
        preSelectedRuangan = ruangan;
        System.out.println("📌 Pre-selected ruangan: " + (ruangan != null ? ruangan.getNamaRuangan() : "null"));
    }
    
    @FXML
    private void handleRuanganChange() {
        try {
            Ruangan selected = cbRuangan.getValue();
            
            if (selected != null) {
                lblKapasitas.setText(selected.getJumlahKursi() + " orang");
                String fasilitas = selected.getFasilitas();
                if (fasilitas == null || fasilitas.trim().isEmpty()) {
                    lblProyektor.setText("Tidak ada fasilitas");
                    lblHdmi.setText("-");
                } else {
                    // Tampilkan semua fasilitas
                    lblProyektor.getParent().setVisible(true);
                    VBox vbox = (VBox) lblProyektor.getParent().getParent();
                    vbox.getChildren().clear();
                    
                    Label titleLabel = new Label("Fasilitas:");
                    titleLabel.setStyle("-fx-font-weight: bold;");
                    vbox.getChildren().add(titleLabel);
                    
                    String[] items = fasilitas.split(",");
                    for (String item : items) {
                        Label itemLabel = new Label("• " + item.trim());
                        vbox.getChildren().add(itemLabel);
                    }
                }
                vboxInfoRuangan.setVisible(true);
                System.out.println("✅ Ruangan selected: " + selected.getNamaRuangan());
            } else {
                vboxInfoRuangan.setVisible(false);
            }
        } catch (Exception e) {
            System.err.println("❌ ERROR in handleRuanganChange(): " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleDateChange() {
        try {
            calculateDuration();
        } catch (Exception e) {
            System.err.println("❌ ERROR in handleDateChange(): " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Calculate durasi peminjaman
     */
    private void calculateDuration() {
        try {
            if (dpTglPinjam.getValue() != null && dpTglKembali.getValue() != null) {
                long days = ChronoUnit.DAYS.between(dpTglPinjam.getValue(), dpTglKembali.getValue()) + 1;
                
                if (days > 0) {
                    lblDurasi.setText("Durasi peminjaman: " + days + " hari");
                    hboxDurasi.setVisible(true);
                    hboxDurasi.setStyle("-fx-background-color: #D4EDDA; -fx-padding: 10; -fx-background-radius: 6;");
                } else {
                    lblDurasi.setText("Tanggal kembali harus setelah tanggal pinjam!");
                    hboxDurasi.setVisible(true);
                    hboxDurasi.setStyle("-fx-background-color: #F8D7DA; -fx-padding: 10; -fx-background-radius: 6;");
                }
            } else {
                hboxDurasi.setVisible(false);
            }
        } catch (Exception e) {
            System.err.println("❌ ERROR calculating duration: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleSubmit() {
        try {
            System.out.println("\n📤 Submitting peminjaman...");
            
            if (!validateInput()) {
                System.out.println("❌ Validation failed");
                return;
            }
            
            // Confirm dialog
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Konfirmasi Pengajuan");
            confirm.setHeaderText("Ajukan Peminjaman Ruangan");
            confirm.setContentText("Yakin ingin mengajukan peminjaman ruangan ini?");
            
            if (confirm.showAndWait().get() != ButtonType.OK) {
                System.out.println("⚠️  User cancelled submission");
                return;
            }
            
            // Create peminjaman object
            Peminjaman peminjaman = new Peminjaman();
            peminjaman.setIdRuangan(cbRuangan.getValue().getId());
            peminjaman.setNamaRuangan(cbRuangan.getValue().getNamaRuangan());
            peminjaman.setNamaPeminjam(SessionManager.getNamaLengkap());
            peminjaman.setKeperluan(txtKeperluan.getText().trim());
            peminjaman.setTanggalPinjam(dpTglPinjam.getValue());
            peminjaman.setTanggalKembali(dpTglKembali.getValue());
            peminjaman.setStatusPeminjaman("aktif");
            
            System.out.println("📝 Peminjaman details:");
            System.out.println("   Ruangan: " + peminjaman.getNamaRuangan());
            System.out.println("   Peminjam: " + peminjaman.getNamaPeminjam());
            System.out.println("   Tanggal: " + peminjaman.getTanggalPinjam() + " - " + peminjaman.getTanggalKembali());
            
            // Submit peminjaman
            if (peminjamanController.tambahPeminjaman(peminjaman)) {
                System.out.println("✅ Peminjaman submitted successfully!");
                showSuccess();
            } else {
                System.err.println("❌ Failed to submit peminjaman");
                showError("Gagal mengajukan peminjaman! Silakan coba lagi.");
            }
            
        } catch (Exception e) {
            System.err.println("❌ FATAL ERROR in handleSubmit(): " + e.getMessage());
            e.printStackTrace();
            showError("Terjadi kesalahan: " + e.getMessage() + "\nSilakan coba lagi atau hubungi admin.");
        }
    }
    
    /**
     * Validasi input form
     */
    private boolean validateInput() {
        try {
            // Ruangan
            if (cbRuangan.getValue() == null) {
                showError("Pilih ruangan yang akan dipinjam!");
                return false;
            }
            
            // Keperluan
            if (txtKeperluan.getText().trim().isEmpty()) {
                showError("Keperluan peminjaman tidak boleh kosong!");
                return false;
            }
            
            if (txtKeperluan.getText().trim().length() < 10) {
                showError("Keperluan minimal 10 karakter!");
                return false;
            }
            
            // Tanggal
            if (dpTglPinjam.getValue() == null) {
                showError("Tanggal pinjam harus diisi!");
                return false;
            }
            
            if (dpTglKembali.getValue() == null) {
                showError("Tanggal kembali harus diisi!");
                return false;
            }
            
            // Validasi tanggal
            if (dpTglPinjam.getValue().isBefore(LocalDate.now())) {
                showError("Tanggal pinjam tidak boleh di masa lalu!");
                return false;
            }
            
            if (dpTglKembali.getValue().isBefore(dpTglPinjam.getValue())) {
                showError("Tanggal kembali harus setelah tanggal pinjam!");
                return false;
            }
            
            // Validasi maksimal durasi (misalnya 30 hari)
            long days = ChronoUnit.DAYS.between(dpTglPinjam.getValue(), dpTglKembali.getValue());
            if (days > 30) {
                showError("Durasi peminjaman maksimal 30 hari!");
                return false;
            }
            
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ ERROR in validateInput(): " + e.getMessage());
            e.printStackTrace();
            showError("Error saat validasi: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Menampilkan pesan error
     */
    private void showError(String message) {
        try {
            errorLabel.setText(message);
            errorLabel.setVisible(true);
            System.err.println("⚠️  " + message);
        } catch (Exception e) {
            System.err.println("❌ ERROR showing error message: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Menampilkan pesan sukses
     */
    private void showSuccess() {
        try {
            // Show bukti peminjaman
            Peminjaman completePeminjaman = new Peminjaman(
                0, // ID akan di-generate database
                cbRuangan.getValue().getId(),
                cbRuangan.getValue().getNamaRuangan(),
                SessionManager.getNamaLengkap(),
                txtKeperluan.getText().trim(),
                dpTglPinjam.getValue(),
                dpTglKembali.getValue(),
                "aktif"
            );
            
            // Panggil BuktiPeminjamanDialog (sudah di package view)
            BuktiPeminjamanDialog.showBuktiPeminjaman(completePeminjaman);
            
            System.out.println("✅ Bukti peminjaman shown, navigating to dashboard...");
            
            // Kembali ke dashboard
            MainApp.showUserDashboard();
            
        } catch (Exception e) {
            System.err.println("❌ ERROR in showSuccess(): " + e.getMessage());
            e.printStackTrace();
            
            // Fallback to simple success dialog
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Berhasil");
            alert.setHeaderText("Pengajuan Peminjaman Berhasil!");
            alert.setContentText("Peminjaman ruangan Anda telah berhasil diajukan.");
            alert.showAndWait();
            MainApp.showUserDashboard();
        }
    }
    
    @FXML
    private void handleReset() {
        try {
            System.out.println("🔄 Resetting form...");
            
            cbRuangan.getSelectionModel().clearSelection();
            txtKeperluan.clear();
            dpTglPinjam.setValue(LocalDate.now());
            dpTglKembali.setValue(LocalDate.now().plusDays(1));
            vboxInfoRuangan.setVisible(false);
            hboxDurasi.setVisible(false);
            errorLabel.setVisible(false);
            
            System.out.println("✅ Form reset complete");
            
        } catch (Exception e) {
            System.err.println("❌ ERROR in handleReset(): " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleKembali() {
        try {
            System.out.println("⬅️  Navigating back to dashboard...");
            MainApp.showUserDashboard();
        } catch (Exception e) {
            System.err.println("❌ ERROR in handleKembali(): " + e.getMessage());
            e.printStackTrace();
            showError("Gagal kembali ke dashboard: " + e.getMessage());
        }
    }
}