package com.sistemruangan.view;

import com.sistemruangan.MainApp;
import com.sistemruangan.controller.PeminjamanController;
import com.sistemruangan.controller.RuanganController;
import com.sistemruangan.model.Peminjaman;
import com.sistemruangan.model.Ruangan;
import com.sistemruangan.util.SessionManager;
import com.sistemruangan.util.DialogUtil;
import javafx.scene.layout.StackPane;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Controller untuk Form Pengajuan Peminjaman User - WITH APPROVAL SYSTEM
 */
public class UserPeminjamanFormController {
    
    @FXML private Label lblNamaPeminjam;
    @FXML private ComboBox<Ruangan> cbRuangan;
    @FXML private TextArea txtKeperluan;
    
    // NEW: Jenis Kegiatan
    @FXML private RadioButton rbKuliah;
    @FXML private RadioButton rbNonKuliah;
    @FXML private VBox vboxNonKuliah;
    @FXML private TextArea txtPenjelasanKegiatan;
    @FXML private Button btnUploadSurat;
    @FXML private Label lblSuratName;
    
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
    private String selectedSuratPath = null;
    private ToggleGroup jenisKegiatanGroup;
    
    private static final String SURAT_DIR = "resources/surat/";
    
    @FXML
    public void initialize() {
        try {
            System.out.println("🔧 Initializing UserPeminjamanFormController with Approval...");
            
            ruanganController = new RuanganController();
            peminjamanController = new PeminjamanController();
            
            // Create surat directory
            createSuratDirectory();
            
            // Setup RadioButton group
            jenisKegiatanGroup = new ToggleGroup();
            rbKuliah.setToggleGroup(jenisKegiatanGroup);
            rbNonKuliah.setToggleGroup(jenisKegiatanGroup);
            rbKuliah.setSelected(true);
            
            // Set nama peminjam
            if (SessionManager.isLoggedIn()) {
                lblNamaPeminjam.setText(SessionManager.getNamaLengkap());
            }
            
            // Load ruangan
            loadRuanganTersedia();
            
            // Set default dates
            dpTglPinjam.setValue(LocalDate.now());
            dpTglKembali.setValue(LocalDate.now().plusDays(1));
            
            // Pre-select ruangan if any
            if (preSelectedRuangan != null) {
                cbRuangan.setValue(preSelectedRuangan);
                handleRuanganChange();
                preSelectedRuangan = null;
            }
            
            calculateDuration();
            
            System.out.println("✅ Controller initialized successfully");
            
        } catch (Exception e) {
            System.err.println("❌ ERROR in initialize(): " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Create directory untuk surat
     */
    private void createSuratDirectory() {
        try {
            Path path = Paths.get(SURAT_DIR);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                System.out.println("📁 Created surat directory: " + SURAT_DIR);
            }
        } catch (IOException e) {
            System.err.println("❌ Failed to create surat directory: " + e.getMessage());
        }
    }
    
    /**
     * Handle perubahan jenis kegiatan
     */
    @FXML
    private void handleJenisKegiatanChange() {
        boolean isNonKuliah = rbNonKuliah.isSelected();
        vboxNonKuliah.setVisible(isNonKuliah);
        vboxNonKuliah.setManaged(isNonKuliah);
        
        if (isNonKuliah) {
            System.out.println("⚠️ Non-Kuliah selected - Approval required");
        } else {
            System.out.println("✅ Kuliah selected - Auto-approved");
        }
    }
    
    /**
     * Handle upload surat
     */
    @FXML
    private void handleUploadSurat() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Pilih Surat Permohonan");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );
            
            File selectedFile = fileChooser.showOpenDialog(btnUploadSurat.getScene().getWindow());
            
            if (selectedFile != null) {
                // Check file size (max 5MB)
                long fileSize = selectedFile.length();
                if (fileSize > 5 * 1024 * 1024) {
                    showError("File terlalu besar! Maksimal 5MB");
                    return;
                }
                
                // Generate unique filename
                String timestamp = String.valueOf(System.currentTimeMillis());
                String newFileName = "surat_" + SessionManager.getUserId() + "_" + timestamp + ".pdf";
                
                // Copy file
                Path sourcePath = selectedFile.toPath();
                Path destinationPath = Paths.get(SURAT_DIR + newFileName);
                
                Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                
                selectedSuratPath = newFileName;
                lblSuratName.setText(selectedFile.getName());
                
                System.out.println("✅ Surat uploaded: " + newFileName);
            }
            
        } catch (Exception e) {
            System.err.println("❌ ERROR uploading surat: " + e.getMessage());
            e.printStackTrace();
            showError("Gagal upload surat: " + e.getMessage());
        }
    }
    
    /**
     * Load ruangan tersedia
     */
    private void loadRuanganTersedia() {
        try {
            ObservableList<Ruangan> allRuangan = ruanganController.getAllRuangan();
            cbRuangan.getItems().clear();
            
            for (Ruangan r : allRuangan) {
                if ("tersedia".equalsIgnoreCase(r.getStatus())) {
                    cbRuangan.getItems().add(r);
                }
            }
            
        } catch (Exception e) {
            System.err.println("❌ ERROR loading ruangan: " + e.getMessage());
            showError("Gagal memuat daftar ruangan: " + e.getMessage());
        }
    }
    
    public static void setSelectedRuangan(Ruangan ruangan) {
        preSelectedRuangan = ruangan;
    }
    
    @FXML
    private void handleRuanganChange() {
        Ruangan selected = cbRuangan.getValue();
        
        if (selected != null) {
            lblKapasitas.setText(selected.getJumlahKursi() + " orang");
            String fasilitas = selected.getFasilitas();
            
            if (fasilitas == null || fasilitas.trim().isEmpty()) {
                lblProyektor.setText("Tidak ada fasilitas");
                lblHdmi.setText("-");
            } else {
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
        } else {
            vboxInfoRuangan.setVisible(false);
        }
    }
    
    @FXML
    private void handleDateChange() {
        calculateDuration();
    }
    
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
        try {
            System.out.println("\n📤 Submitting peminjaman...");
            
            if (!validateInput()) {
                return;
            }
            
            String jenisKegiatan = rbKuliah.isSelected() ? "kuliah" : "lainnya";
            
            StackPane root = MainApp.getRootContainer();
            
            String confirmMessage = rbKuliah.isSelected() 
                ? "Peminjaman untuk kegiatan kuliah akan langsung disetujui.\nLanjutkan?" 
                : "Peminjaman untuk kegiatan non-kuliah memerlukan persetujuan admin.\nLanjutkan?";
            
            DialogUtil.showConfirmation(
                "Konfirmasi Pengajuan",
                confirmMessage,
                root,
                () -> {
                    // On Confirm - Submit
                    Peminjaman peminjaman = new Peminjaman();
                    peminjaman.setIdRuangan(cbRuangan.getValue().getId());
                    peminjaman.setNamaRuangan(cbRuangan.getValue().getNamaRuangan());
                    peminjaman.setNamaPeminjam(SessionManager.getNamaLengkap());
                    peminjaman.setKeperluan(txtKeperluan.getText().trim());
                    peminjaman.setJenisKegiatan(jenisKegiatan);
                    peminjaman.setTanggalPinjam(dpTglPinjam.getValue());
                    peminjaman.setTanggalKembali(dpTglKembali.getValue());
                    peminjaman.setStatusPeminjaman("aktif");
                    
                    if (rbNonKuliah.isSelected()) {
                        peminjaman.setPenjelasanKegiatan(txtPenjelasanKegiatan.getText().trim());
                        peminjaman.setSuratPath(selectedSuratPath);
                    }
                    
                    if (peminjamanController.tambahPeminjaman(peminjaman)) {
                        showSuccess(rbKuliah.isSelected());
                    } else {
                        DialogUtil.showDialog(
                            DialogUtil.DialogType.ERROR,
                            "Gagal",
                            "Gagal mengajukan peminjaman! Silakan coba lagi.",
                            root
                        );
                    }
                },
                () -> {
                    System.out.println("Submission cancelled");
                }
            );
            
        } catch (Exception e) {
            System.err.println("❌ ERROR in handleSubmit(): " + e.getMessage());
            e.printStackTrace();
            showError("Terjadi kesalahan: " + e.getMessage());
        }
    }
    
    private boolean validateInput() {
        // Ruangan
        if (cbRuangan.getValue() == null) {
            showError("Pilih ruangan yang akan dipinjam!");
            return false;
        }
        
        // Keperluan
        if (txtKeperluan.getText().trim().isEmpty()) {
            showError("Keperluan tidak boleh kosong!");
            return false;
        }
        
        if (txtKeperluan.getText().trim().length() < 10) {
            showError("Keperluan minimal 10 karakter!");
            return false;
        }
        
        // Validasi untuk non-kuliah
        if (rbNonKuliah.isSelected()) {
            if (txtPenjelasanKegiatan.getText().trim().isEmpty()) {
                showError("Penjelasan detail kegiatan harus diisi!");
                return false;
            }
            
            if (txtPenjelasanKegiatan.getText().trim().length() < 50) {
                showError("Penjelasan detail minimal 50 karakter!");
                return false;
            }
            
            if (selectedSuratPath == null) {
                showError("Surat permohonan harus dilampirkan!");
                return false;
            }
        }
        
        // Tanggal
        if (dpTglPinjam.getValue() == null || dpTglKembali.getValue() == null) {
            showError("Tanggal pinjam dan kembali harus diisi!");
            return false;
        }
        
        if (dpTglPinjam.getValue().isBefore(LocalDate.now())) {
            showError("Tanggal pinjam tidak boleh di masa lalu!");
            return false;
        }
        
        if (dpTglKembali.getValue().isBefore(dpTglPinjam.getValue())) {
            showError("Tanggal kembali harus setelah tanggal pinjam!");
            return false;
        }
        
        return true;
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        
        // Also show as overlay dialog for better visibility
        DialogUtil.showDialog(
            DialogUtil.DialogType.ERROR,
            "Error Validasi",
            message,
            MainApp.getRootContainer()
        );
    }
    
    private void showSuccess(boolean isKuliah) {
        StackPane root = MainApp.getRootContainer();
        
        String message = isKuliah 
            ? "Peminjaman ruangan Anda telah disetujui secara otomatis.\nSilakan gunakan ruangan sesuai jadwal yang ditentukan."
            : "Pengajuan peminjaman Anda telah dikirim.\nSilakan tunggu persetujuan dari admin (maks. 1x24 jam).";
        
        DialogUtil.showDialog(
            DialogUtil.DialogType.SUCCESS,
            "Pengajuan Berhasil",
            message,
            root
        );
        
        // Delay before redirect
        javafx.application.Platform.runLater(() -> {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            MainApp.showUserDashboard();
        });
    }
    
    @FXML
    private void handleReset() {
        cbRuangan.getSelectionModel().clearSelection();
        txtKeperluan.clear();
        rbKuliah.setSelected(true);
        txtPenjelasanKegiatan.clear();
        lblSuratName.setText("Belum ada file dipilih");
        selectedSuratPath = null;
        dpTglPinjam.setValue(LocalDate.now());
        dpTglKembali.setValue(LocalDate.now().plusDays(1));
        vboxInfoRuangan.setVisible(false);
        vboxNonKuliah.setVisible(false);
        vboxNonKuliah.setManaged(false);
        hboxDurasi.setVisible(false);
        errorLabel.setVisible(false);
    }
    
    @FXML
    private void handleKembali() {
        MainApp.showUserDashboard();
    }
}