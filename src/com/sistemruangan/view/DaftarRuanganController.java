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
 * Controller untuk halaman Daftar Ruangan
 */
public class DaftarRuanganController {
    
    @FXML private TableView<Ruangan> tableRuangan;
    @FXML private TableColumn<Ruangan, Integer> colId;
    @FXML private TableColumn<Ruangan, String> colNama;
    @FXML private TableColumn<Ruangan, Integer> colKursi;
    @FXML private TableColumn<Ruangan, String> colProyektor;
    @FXML private TableColumn<Ruangan, String> colHdmi;
    @FXML private TableColumn<Ruangan, String> colStatus;
    
    @FXML private TextField txtNama;
    @FXML private TextField txtKursi;
    @FXML private CheckBox cbProyektor;
    @FXML private ComboBox<String> cbHdmi;
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
        ruanganController = new RuanganController();
        
        // Setup table columns
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNama.setCellValueFactory(new PropertyValueFactory<>("namaRuangan"));
        colKursi.setCellValueFactory(new PropertyValueFactory<>("jumlahKursi"));
        colProyektor.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().isAdaProyektor() ? "Ada" : "Tidak Ada"
            )
        );
        colHdmi.setCellValueFactory(new PropertyValueFactory<>("kondisiHdmi"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        // Setup ComboBox
        cbHdmi.getItems().addAll("baik", "rusak");
        cbStatus.getItems().addAll("tersedia", "dipinjam");
        cbHdmi.setValue("baik");
        cbStatus.setValue("tersedia");
        
        // Load data
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
    }
    
    /**
     * Load data ruangan ke table
     */
    private void loadData() {
        ObservableList<Ruangan> ruanganList = ruanganController.getAllRuangan();
        tableRuangan.setItems(ruanganList);
    }
    
    /**
     * Populate fields dengan data ruangan yang dipilih
     */
    private void populateFields(Ruangan ruangan) {
        txtNama.setText(ruangan.getNamaRuangan());
        txtKursi.setText(String.valueOf(ruangan.getJumlahKursi()));
        cbProyektor.setSelected(ruangan.isAdaProyektor());
        cbHdmi.setValue(ruangan.getKondisiHdmi());
        cbStatus.setValue(ruangan.getStatus());
    }
    
    /**
     * Clear all input fields
     */
    private void clearFields() {
        txtNama.clear();
        txtKursi.clear();
        cbProyektor.setSelected(false);
        cbHdmi.setValue("baik");
        cbStatus.setValue("tersedia");
        selectedRuangan = null;
        tableRuangan.getSelectionModel().clearSelection();
    }
    
    @FXML
    private void handleTambah() {
        if (!validateInput()) return;
        
        Ruangan ruangan = new Ruangan();
        ruangan.setNamaRuangan(txtNama.getText().trim());
        ruangan.setJumlahKursi(Integer.parseInt(txtKursi.getText().trim()));
        ruangan.setAdaProyektor(cbProyektor.isSelected());
        ruangan.setKondisiHdmi(cbHdmi.getValue());
        ruangan.setStatus(cbStatus.getValue());
        
        if (ruanganController.tambahRuangan(ruangan)) {
            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Ruangan berhasil ditambahkan!");
            loadData();
            clearFields();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal menambahkan ruangan!");
        }
    }
    
    @FXML
    private void handleUpdate() {
        if (selectedRuangan == null) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Pilih ruangan yang akan diupdate!");
            return;
        }
        
        if (!validateInput()) return;
        
        selectedRuangan.setNamaRuangan(txtNama.getText().trim());
        selectedRuangan.setJumlahKursi(Integer.parseInt(txtKursi.getText().trim()));
        selectedRuangan.setAdaProyektor(cbProyektor.isSelected());
        selectedRuangan.setKondisiHdmi(cbHdmi.getValue());
        selectedRuangan.setStatus(cbStatus.getValue());
        
        if (ruanganController.updateRuangan(selectedRuangan)) {
            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Ruangan berhasil diupdate!");
            loadData();
            clearFields();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal update ruangan!");
        }
    }
    
    @FXML
    private void handleHapus() {
        if (selectedRuangan == null) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Pilih ruangan yang akan dihapus!");
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Konfirmasi Hapus");
        confirmAlert.setHeaderText("Hapus Ruangan");
        confirmAlert.setContentText(new StringBuilder("Yakin ingin menghapus ruangan ")
            .append(selectedRuangan.getNamaRuangan())
            .append("?")
            .toString());
        
        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            if (ruanganController.deleteRuangan(selectedRuangan.getId())) {
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Ruangan berhasil dihapus!");
                loadData();
                clearFields();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Gagal menghapus ruangan!");
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
        txtSearch.clear();
    }
    
    @FXML
    private void handleSearch() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            loadData();
        } else {
            ObservableList<Ruangan> searchResult = ruanganController.searchRuangan(keyword);
            tableRuangan.setItems(searchResult);
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
        if (txtNama.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Nama ruangan tidak boleh kosong!");
            return false;
        }
        
        try {
            int kursi = Integer.parseInt(txtKursi.getText().trim());
            if (kursi <= 0) {
                showAlert(Alert.AlertType.WARNING, "Peringatan", "Jumlah kursi harus lebih dari 0!");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Jumlah kursi harus berupa angka!");
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
        
        button.setOnMouseEntered(_ -> {
            button.setEffect(shadow);
        });
        button.setOnMouseExited(_ -> {
            button.setEffect(null);
        });
    }
}