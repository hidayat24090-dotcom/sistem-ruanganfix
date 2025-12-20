package com.sistemruangan.view;

import com.sistemruangan.MainApp;
import com.sistemruangan.controller.RuanganController;
import com.sistemruangan.model.Ruangan;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

/**
 * Controller untuk halaman Daftar Ruangan - FIXED VERSION
 */
public class DaftarRuanganController {
    
    @FXML private TableView<Ruangan> tableRuangan;
    @FXML private TableColumn<Ruangan, Integer> colId;
    @FXML private TableColumn<Ruangan, String> colNama;
    @FXML private TableColumn<Ruangan, Integer> colKursi;
    @FXML private TableColumn<Ruangan, String> colFasilitas;
    @FXML private TableColumn<Ruangan, String> colStatus;
    
    @FXML private TextField txtNama;
    @FXML private TextField txtKursi;
    @FXML private TextArea txtFasilitas; 
    @FXML private ComboBox<String> cbStatus;
    @FXML private TextField txtSearch;
    
    @FXML private Button btnTambah;
    @FXML private Button btnUpdate;
    @FXML private Button btnHapus;
    @FXML private Button btnClear;
    @FXML private Button btnRefresh;
    @FXML private Button btnKembali;
    
    private RuanganController ruanganController;
    private Ruangan selectedRuangan;
    
    @FXML
    public void initialize() {
        System.out.println("🔧 Initializing DaftarRuanganController...");
        
        try {
            ruanganController = new RuanganController();
            
            // Setup table columns
            colId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colNama.setCellValueFactory(new PropertyValueFactory<>("namaRuangan"));
            colKursi.setCellValueFactory(new PropertyValueFactory<>("jumlahKursi"));
            colFasilitas.setCellValueFactory(new PropertyValueFactory<>("fasilitas"));
            colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
            
            // Left align for nama column
            colNama.setStyle("-fx-alignment: CENTER-LEFT;");
            colFasilitas.setStyle("-fx-alignment: CENTER-LEFT;");
            
            // Setup ComboBox
            cbStatus.getItems().addAll("tersedia", "dipinjam");
            cbStatus.setValue("tersedia");
            
            // Load data
            System.out.println("📊 Loading data...");
            loadData();
            
            // Setup table selection
            tableRuangan.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newSelection) -> {
                    if (newSelection != null) {
                        selectedRuangan = newSelection;
                        populateFields(newSelection);
                    }
                }
            );
            
            // Setup button effects
            setupButtonEffect(btnTambah);
            setupButtonEffect(btnUpdate);
            setupButtonEffect(btnHapus);
            setupButtonEffect(btnClear);
            setupButtonEffect(btnRefresh);
            setupButtonEffect(btnKembali);
            
            System.out.println("✅ DaftarRuanganController initialized successfully");
            
        } catch (Exception e) {
            System.err.println("❌ ERROR in initialize(): " + e.getMessage());
            e.printStackTrace();
            showError("Gagal menginisialisasi halaman: " + e.getMessage());
        }
    }
    
    /**
     * Load data ruangan ke table
     */
    private void loadData() {
        try {
            System.out.println("📋 Fetching ruangan data from database...");
            ObservableList<Ruangan> ruanganList = ruanganController.getAllRuangan();
            
            if (ruanganList == null) {
                System.err.println("⚠️ Ruangan list is NULL!");
                showError("Gagal memuat data: Tidak ada data yang dikembalikan dari database");
                return;
            }
            
            System.out.println("✅ Loaded " + ruanganList.size() + " ruangan(s)");
            
            tableRuangan.setItems(ruanganList);
            
            if (ruanganList.isEmpty()) {
                System.out.println("⚠️ No ruangan data found in database");
                showInfo("Belum ada data ruangan. Silakan tambah ruangan baru.");
            }
            
        } catch (Exception e) {
            System.err.println("❌ ERROR loading data: " + e.getMessage());
            e.printStackTrace();
            showError("Gagal memuat data ruangan:\n" + e.getMessage() + 
                     "\n\nPastikan database sudah running dan tabel 'ruangan' sudah ada.");
        }
    }
    
    /**
     * Populate fields dengan data ruangan yang dipilih
     */
    private void populateFields(Ruangan ruangan) {
        try {
            txtNama.setText(ruangan.getNamaRuangan());
            txtKursi.setText(String.valueOf(ruangan.getJumlahKursi()));
            txtFasilitas.setText(ruangan.getFasilitas());
            cbStatus.setValue(ruangan.getStatus());
            System.out.println("✅ Fields populated for: " + ruangan.getNamaRuangan());
        } catch (Exception e) {
            System.err.println("❌ ERROR populating fields: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Clear all input fields
     */
    private void clearFields() {
        txtNama.clear();
        txtKursi.clear();
        txtFasilitas.clear();
        cbStatus.setValue("tersedia");
        selectedRuangan = null;
        tableRuangan.getSelectionModel().clearSelection();
        System.out.println("🔄 Fields cleared");
    }
    
    @FXML
    private void handleTambah() {
        try {
            System.out.println("➕ Adding new ruangan...");
            
            if (!validateInput()) {
                System.out.println("❌ Validation failed");
                return;
            }
            
            Ruangan ruangan = new Ruangan();
            ruangan.setNamaRuangan(txtNama.getText().trim());
            ruangan.setJumlahKursi(Integer.parseInt(txtKursi.getText().trim()));
            ruangan.setFasilitas(txtFasilitas.getText().trim());
            ruangan.setStatus(cbStatus.getValue());
            
            if (ruanganController.tambahRuangan(ruangan)) {
                showSuccess("Ruangan berhasil ditambahkan!");
                System.out.println("✅ Ruangan added successfully");
                loadData();
                clearFields();
            } else {
                showError("Gagal menambahkan ruangan!");
                System.err.println("❌ Failed to add ruangan");
            }
            
        } catch (Exception e) {
            System.err.println("❌ ERROR in handleTambah(): " + e.getMessage());
            e.printStackTrace();
            showError("Terjadi kesalahan: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleUpdate() {
        try {
            if (selectedRuangan == null) {
                showWarning("Pilih ruangan yang akan diupdate!");
                return;
            }
            
            if (!validateInput()) return;
            
            selectedRuangan.setNamaRuangan(txtNama.getText().trim());
            selectedRuangan.setJumlahKursi(Integer.parseInt(txtKursi.getText().trim()));
            selectedRuangan.setFasilitas(txtFasilitas.getText().trim());
            selectedRuangan.setStatus(cbStatus.getValue());
            
            if (ruanganController.updateRuangan(selectedRuangan)) {
                showSuccess("Ruangan berhasil diupdate!");
                loadData();
                clearFields();
            } else {
                showError("Gagal update ruangan!");
            }
            
        } catch (Exception e) {
            System.err.println("❌ ERROR in handleUpdate(): " + e.getMessage());
            e.printStackTrace();
            showError("Terjadi kesalahan: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleHapus() {
        try {
            if (selectedRuangan == null) {
                showWarning("Pilih ruangan yang akan dihapus!");
                return;
            }
            
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Konfirmasi Hapus");
            confirmAlert.setHeaderText("Hapus Ruangan");
            confirmAlert.setContentText("Yakin ingin menghapus ruangan " + 
                                       selectedRuangan.getNamaRuangan() + "?");
            
            if (confirmAlert.showAndWait().get() == ButtonType.OK) {
                if (ruanganController.deleteRuangan(selectedRuangan.getId())) {
                    showSuccess("Ruangan berhasil dihapus!");
                    loadData();
                    clearFields();
                } else {
                    showError("Gagal menghapus ruangan!");
                }
            }
            
        } catch (Exception e) {
            System.err.println("❌ ERROR in handleHapus(): " + e.getMessage());
            e.printStackTrace();
            showError("Terjadi kesalahan: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleClear() {
        clearFields();
    }
    
    @FXML
    private void handleRefresh() {
        try {
            System.out.println("🔄 Refreshing data...");
            loadData();
            txtSearch.clear();
            showInfo("Data berhasil di-refresh!");
        } catch (Exception e) {
            System.err.println("❌ ERROR in handleRefresh(): " + e.getMessage());
            e.printStackTrace();
            showError("Gagal refresh data: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleSearch() {
        try {
            String keyword = txtSearch.getText().trim();
            
            if (keyword.isEmpty()) {
                loadData();
            } else {
                System.out.println("🔍 Searching for: " + keyword);
                ObservableList<Ruangan> searchResult = ruanganController.searchRuangan(keyword);
                tableRuangan.setItems(searchResult);
                System.out.println("✅ Found " + searchResult.size() + " result(s)");
            }
            
        } catch (Exception e) {
            System.err.println("❌ ERROR in handleSearch(): " + e.getMessage());
            e.printStackTrace();
            showError("Gagal mencari data: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleKembali() {
        try {
            System.out.println("⬅️ Navigating back to dashboard...");
            MainApp.showDashboard();
        } catch (Exception e) {
            System.err.println("❌ ERROR in handleKembali(): " + e.getMessage());
            e.printStackTrace();
            showError("Gagal kembali ke dashboard: " + e.getMessage());
        }
    }
    
    /**
     * Validasi input form
     */
    private boolean validateInput() {
        if (txtNama.getText().trim().isEmpty()) {
            showWarning("Nama ruangan tidak boleh kosong!");
            return false;
        }
        
        try {
            int kursi = Integer.parseInt(txtKursi.getText().trim());
            if (kursi <= 0) {
                showWarning("Jumlah kursi harus lebih dari 0!");
                return false;
            }
        } catch (NumberFormatException e) {
            showWarning("Jumlah kursi harus berupa angka!");
            return false;
        }
        
        return true;
    }
    
    /**
     * Show alert dialogs
     */
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sukses");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Terjadi Kesalahan");
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Peringatan");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informasi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Setup button hover effect
     */
    private void setupButtonEffect(Button button) {
        try {
            DropShadow shadow = new DropShadow();
            shadow.setColor(Color.web("#5B9BD5"));
            
            button.setOnMouseEntered(e -> button.setEffect(shadow));
            button.setOnMouseExited(e -> button.setEffect(null));
        } catch (Exception e) {
            // Ignore styling errors
        }
    }
}