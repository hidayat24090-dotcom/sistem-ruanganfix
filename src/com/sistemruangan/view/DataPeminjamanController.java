package com.sistemruangan.view;

import com.sistemruangan.MainApp;
import com.sistemruangan.controller.PeminjamanController;
import com.sistemruangan.controller.RuanganController;
import com.sistemruangan.model.Peminjaman;
import com.sistemruangan.model.Ruangan;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

import java.time.LocalDate;

/**
 * Controller untuk halaman Data Peminjaman
 */
public class DataPeminjamanController {
    
    @FXML private TableView<Peminjaman> tablePeminjaman;
    @FXML private TableColumn<Peminjaman, Integer> colId;
    @FXML private TableColumn<Peminjaman, String> colRuangan;
    @FXML private TableColumn<Peminjaman, String> colPeminjam;
    @FXML private TableColumn<Peminjaman, String> colKeperluan;
    @FXML private TableColumn<Peminjaman, LocalDate> colTglPinjam;
    @FXML private TableColumn<Peminjaman, LocalDate> colTglKembali;
    @FXML private TableColumn<Peminjaman, String> colStatus;
    
    @FXML private ComboBox<Ruangan> cbRuangan;
    @FXML private TextField txtPeminjam;
    @FXML private TextField txtKeperluan;
    @FXML private DatePicker dpTglPinjam;
    @FXML private DatePicker dpTglKembali;
    @FXML private TextField txtSearch;
    
    @FXML private Button btnTambah;
    @FXML private Button btnSelesai;
    @FXML private Button btnBatal;
    @FXML private Button btnClear;
    @FXML private Button btnRefresh;
    @FXML private Button btnKembali;
    
    private PeminjamanController peminjamanController;
    private RuanganController ruanganController;
    private Peminjaman selectedPeminjaman;
    
    @FXML
    public void initialize() {
        peminjamanController = new PeminjamanController();
        ruanganController = new RuanganController();
        
        // Setup table columns
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colRuangan.setCellValueFactory(new PropertyValueFactory<>("namaRuangan"));
        colPeminjam.setCellValueFactory(new PropertyValueFactory<>("namaPeminjam"));
        colKeperluan.setCellValueFactory(new PropertyValueFactory<>("keperluan"));
        colTglPinjam.setCellValueFactory(new PropertyValueFactory<>("tanggalPinjam"));
        colTglKembali.setCellValueFactory(new PropertyValueFactory<>("tanggalKembali"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("statusPeminjaman"));
        
        // Load ruangan yang tersedia
        loadRuanganTersedia();
        
        // Set default date
        dpTglPinjam.setValue(LocalDate.now());
        dpTglKembali.setValue(LocalDate.now().plusDays(1));
        
        // Load data
        loadData();
        
        // Setup table selection
        tablePeminjaman.getSelectionModel().selectedItemProperty().addListener(
            (_, _, newSelection) -> {
                if (newSelection != null) {
                    selectedPeminjaman = newSelection;
                }
            }
        );
        
        // Setup button effects
        setupButtonEffect(btnTambah);
        setupButtonEffect(btnSelesai);
        setupButtonEffect(btnBatal);
        setupButtonEffect(btnClear);
        setupButtonEffect(btnRefresh);
        setupButtonEffect(btnKembali);
    }
    
    /**
     * Load data peminjaman ke table
     */
    private void loadData() {
        ObservableList<Peminjaman> peminjamanList = peminjamanController.getAllPeminjaman();
        tablePeminjaman.setItems(peminjamanList);
    }
    
    /**
     * Load ruangan yang tersedia ke ComboBox
     */
    private void loadRuanganTersedia() {
        ObservableList<Ruangan> allRuangan = ruanganController.getAllRuangan();
        ObservableList<Ruangan> ruanganTersedia = javafx.collections.FXCollections.observableArrayList();
        
        for (Ruangan r : allRuangan) {
            if ("tersedia".equalsIgnoreCase(r.getStatus())) {
                ruanganTersedia.add(r);
            }
        }
        
        cbRuangan.setItems(ruanganTersedia);
    }
    
    /**
     * Clear all input fields
     */
    private void clearFields() {
        cbRuangan.getSelectionModel().clearSelection();
        txtPeminjam.clear();
        txtKeperluan.clear();
        dpTglPinjam.setValue(LocalDate.now());
        dpTglKembali.setValue(LocalDate.now().plusDays(1));
        selectedPeminjaman = null;
        tablePeminjaman.getSelectionModel().clearSelection();
    }
    
    @FXML
    private void handleTambah() {
        if (!validateInput()) return;
        
        Peminjaman peminjaman = new Peminjaman();
        peminjaman.setIdRuangan(cbRuangan.getValue().getId());
        peminjaman.setNamaPeminjam(txtPeminjam.getText().trim());
        peminjaman.setKeperluan(txtKeperluan.getText().trim());
        peminjaman.setTanggalPinjam(dpTglPinjam.getValue());
        peminjaman.setTanggalKembali(dpTglKembali.getValue());
        peminjaman.setStatusPeminjaman("aktif");
        
        if (peminjamanController.tambahPeminjaman(peminjaman)) {
            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Peminjaman berhasil ditambahkan!");
            loadData();
            loadRuanganTersedia();
            clearFields();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal menambahkan peminjaman!");
        }
    }
    
    @FXML
    private void handleSelesai() {
        if (selectedPeminjaman == null) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Pilih peminjaman yang akan diselesaikan!");
            return;
        }
        
        if (!"aktif".equalsIgnoreCase(selectedPeminjaman.getStatusPeminjaman())) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Hanya peminjaman aktif yang dapat diselesaikan!");
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Konfirmasi Selesai");
        confirmAlert.setHeaderText("Selesaikan Peminjaman");
        confirmAlert.setContentText(new StringBuilder("Yakin menyelesaikan peminjaman ini?").toString());
        
        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            if (peminjamanController.selesaikanPeminjaman(selectedPeminjaman.getId(), selectedPeminjaman.getIdRuangan())) {
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Peminjaman berhasil diselesaikan!");
                loadData();
                loadRuanganTersedia();
                clearFields();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Gagal menyelesaikan peminjaman!");
            }
        }
    }
    
    @FXML
    private void handleBatal() {
        if (selectedPeminjaman == null) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Pilih peminjaman yang akan dibatalkan!");
            return;
        }
        
        if (!"aktif".equalsIgnoreCase(selectedPeminjaman.getStatusPeminjaman())) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Hanya peminjaman aktif yang dapat dibatalkan!");
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Konfirmasi Batal");
        confirmAlert.setHeaderText("Batalkan Peminjaman");
        confirmAlert.setContentText(new StringBuilder("Yakin membatalkan peminjaman ini?").toString());
        
        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            if (peminjamanController.batalkanPeminjaman(selectedPeminjaman.getId(), selectedPeminjaman.getIdRuangan())) {
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Peminjaman berhasil dibatalkan!");
                loadData();
                loadRuanganTersedia();
                clearFields();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Gagal membatalkan peminjaman!");
            }
        }
    }
    
    @FXML
    private void handleClear() {
        clearFields();
    }
    
    @FXML
    private void handleRefresh() {
        loadData();
        loadRuanganTersedia();
        txtSearch.clear();
    }
    
    @FXML
    private void handleSearch() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            loadData();
        } else {
            ObservableList<Peminjaman> searchResult = peminjamanController.searchPeminjaman(keyword);
            tablePeminjaman.setItems(searchResult);
        }
    }
    
    @FXML
    private void handleKembali() {
        MainApp.showDashboard();
    }
    
    /**
     * Validasi input form
     */
    private boolean validateInput() {
        if (cbRuangan.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Pilih ruangan yang akan dipinjam!");
            return false;
        }
        
        if (txtPeminjam.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Nama peminjam tidak boleh kosong!");
            return false;
        }
        
        if (txtKeperluan.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Keperluan tidak boleh kosong!");
            return false;
        }
        
        if (dpTglPinjam.getValue() == null || dpTglKembali.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Tanggal pinjam dan kembali harus diisi!");
            return false;
        }
        
        if (dpTglKembali.getValue().isBefore(dpTglPinjam.getValue())) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Tanggal kembali tidak boleh lebih awal dari tanggal pinjam!");
            return false;
        }
        
        return true;
    }
    
    /**
     * Show alert dialog
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Setup button hover effect
     */
    private void setupButtonEffect(Button button) {
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web("#5B9BD5"));
        
        button.setOnMouseEntered(_ -> button.setEffect(shadow));
        button.setOnMouseExited(_ -> button.setEffect(null));
    }
}