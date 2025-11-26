package com.sistemruangan.view;

import com.sistemruangan.MainApp;
import com.sistemruangan.controller.PeminjamanController;
import com.sistemruangan.controller.RuanganController;
import com.sistemruangan.model.Peminjaman;
import com.sistemruangan.model.Ruangan;
import com.sistemruangan.util.SessionManager;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Controller untuk Form Pengajuan Peminjaman User
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
        ruanganController = new RuanganController();
        peminjamanController = new PeminjamanController();
        
        // Set nama peminjam dari session
        if (SessionManager.isLoggedIn()) {
            lblNamaPeminjam.setText(SessionManager.getNamaLengkap());
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
    }
    
    /**
     * Load ruangan yang tersedia
     */
    private void loadRuanganTersedia() {
        ObservableList<Ruangan> allRuangan = ruanganController.getAllRuangan();
        cbRuangan.getItems().clear();
        
        for (Ruangan r : allRuangan) {
            if ("tersedia".equalsIgnoreCase(r.getStatus())) {
                cbRuangan.getItems().add(r);
            }
        }
    }
    
    /**
     * Static method untuk set ruangan yang dipilih dari halaman list
     */
    public static void setSelectedRuangan(Ruangan ruangan) {
        preSelectedRuangan = ruangan;
    }
    
    @FXML
    private void handleRuanganChange() {
        Ruangan selected = cbRuangan.getValue();
        
        if (selected != null) {
            lblKapasitas.setText(selected.getJumlahKursi() + " orang");
            lblProyektor.setText(selected.isAdaProyektor() ? "Ada" : "Tidak Ada");
            lblHdmi.setText(selected.getKondisiHdmi());
            vboxInfoRuangan.setVisible(true);
        } else {
            vboxInfoRuangan.setVisible(false);
        }
    }
    
    @FXML
    private void handleDateChange() {
        calculateDuration();
    }
    
    /**
     * Calculate durasi peminjaman
     */
    private void calculateDuration() {
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
    }
    
    @FXML
    private void handleSubmit() {
        if (!validateInput()) {
            return;
        }
        
        // Confirm dialog
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Konfirmasi Pengajuan");
        confirm.setHeaderText("Ajukan Peminjaman Ruangan");
        confirm.setContentText("Yakin ingin mengajukan peminjaman ruangan ini?");
        
        if (confirm.showAndWait().get() != ButtonType.OK) {
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
        
        // Submit peminjaman
        if (peminjamanController.tambahPeminjaman(peminjaman)) {
            showSuccess();
        } else {
            showError("Gagal mengajukan peminjaman! Silakan coba lagi.");
        }
    }
    
    /**
     * Validasi input form
     */
    private boolean validateInput() {
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
    }
    
    /**
     * Menampilkan pesan error
     */
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
    
    /**
     * Menampilkan pesan sukses
     */
    private void showSuccess() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Berhasil");
        alert.setHeaderText("Pengajuan Peminjaman Berhasil!");
        alert.setContentText("Peminjaman ruangan Anda telah berhasil diajukan. " +
                           "Anda dapat melihat riwayat peminjaman di menu Riwayat Peminjaman.");
        alert.showAndWait();
        
        // Kembali ke dashboard
        MainApp.showUserDashboard();
    }
    
    @FXML
    private void handleReset() {
        cbRuangan.getSelectionModel().clearSelection();
        txtKeperluan.clear();
        dpTglPinjam.setValue(LocalDate.now());
        dpTglKembali.setValue(LocalDate.now().plusDays(1));
        vboxInfoRuangan.setVisible(false);
        hboxDurasi.setVisible(false);
        errorLabel.setVisible(false);
    }
    
    @FXML
    private void handleKembali() {
        MainApp.showUserDashboard();
    }
}