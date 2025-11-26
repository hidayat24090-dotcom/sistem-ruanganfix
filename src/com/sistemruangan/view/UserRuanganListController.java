package com.sistemruangan.view;

import com.sistemruangan.MainApp;
import com.sistemruangan.controller.RuanganController;
import com.sistemruangan.model.Ruangan;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

/**
 * Controller untuk halaman Daftar Ruangan User
 */
public class UserRuanganListController {
    
    @FXML private TableView<Ruangan> tableRuangan;
    @FXML private TableColumn<Ruangan, String> colNama;
    @FXML private TableColumn<Ruangan, Integer> colKursi;
    @FXML private TableColumn<Ruangan, String> colProyektor;
    @FXML private TableColumn<Ruangan, String> colHdmi;
    @FXML private TableColumn<Ruangan, String> colStatus;
    @FXML private TableColumn<Ruangan, Void> colAksi;
    
    @FXML private ComboBox<String> cbFilterStatus;
    @FXML private TextField txtSearch;
    @FXML private Label lblTotalRuangan;
    @FXML private Label lblTersedia;
    @FXML private Label lblDipinjam;
    @FXML private Button btnRefresh;
    @FXML private Button btnKembali;
    
    private RuanganController ruanganController;
    private FilteredList<Ruangan> filteredData;
    
    @FXML
    public void initialize() {
        ruanganController = new RuanganController();
        
        // Setup table columns
        colNama.setCellValueFactory(new PropertyValueFactory<>("namaRuangan"));
        colKursi.setCellValueFactory(new PropertyValueFactory<>("jumlahKursi"));
        colProyektor.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().isAdaProyektor() ? "Ada" : "Tidak Ada"
            )
        );
        colHdmi.setCellValueFactory(new PropertyValueFactory<>("kondisiHdmi"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        // Setup action column with button
        setupActionColumn();
        
        // Setup filter combo box
        cbFilterStatus.getItems().addAll("Semua", "Tersedia", "Dipinjam");
        cbFilterStatus.setValue("Semua");
        
        // Load data
        loadData();
    }
    
    /**
     * Setup kolom aksi dengan tombol
     */
    private void setupActionColumn() {
        colAksi.setCellFactory(param -> new TableCell<>() {
            private final Button btnPinjam = new Button("📝 Pinjam");
            private final HBox container = new HBox(btnPinjam);
            
            {
                btnPinjam.setStyle("-fx-background-color: #70C1B3; -fx-text-fill: white; " +
                                 "-fx-font-weight: bold; -fx-padding: 5 15 5 15; " +
                                 "-fx-background-radius: 6px; -fx-cursor: hand;");
                
                btnPinjam.setOnMouseEntered(e -> 
                    btnPinjam.setStyle("-fx-background-color: #5fb0a1; -fx-text-fill: white; " +
                                     "-fx-font-weight: bold; -fx-padding: 5 15 5 15; " +
                                     "-fx-background-radius: 6px; -fx-cursor: hand;")
                );
                
                btnPinjam.setOnMouseExited(e -> 
                    btnPinjam.setStyle("-fx-background-color: #70C1B3; -fx-text-fill: white; " +
                                     "-fx-font-weight: bold; -fx-padding: 5 15 5 15; " +
                                     "-fx-background-radius: 6px; -fx-cursor: hand;")
                );
                
                btnPinjam.setOnAction(e -> {
                    Ruangan ruangan = getTableView().getItems().get(getIndex());
                    handlePinjam(ruangan);
                });
                
                container.setAlignment(Pos.CENTER);
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                
                if (empty) {
                    setGraphic(null);
                } else {
                    Ruangan ruangan = getTableView().getItems().get(getIndex());
                    
                    if ("tersedia".equalsIgnoreCase(ruangan.getStatus())) {
                        btnPinjam.setDisable(false);
                        setGraphic(container);
                    } else {
                        btnPinjam.setDisable(true);
                        btnPinjam.setText("Tidak Tersedia");
                        btnPinjam.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; " +
                                         "-fx-font-weight: bold; -fx-padding: 5 15 5 15; " +
                                         "-fx-background-radius: 6px;");
                        setGraphic(container);
                    }
                }
            }
        });
    }
    
    /**
     * Load data ruangan
     */
    private void loadData() {
        ObservableList<Ruangan> ruanganList = ruanganController.getAllRuangan();
        filteredData = new FilteredList<>(ruanganList, p -> true);
        tableRuangan.setItems(filteredData);
        updateSummary();
    }
    
    /**
     * Update summary statistik
     */
    private void updateSummary() {
        int total = filteredData.size();
        int tersedia = 0;
        int dipinjam = 0;
        
        for (Ruangan r : filteredData) {
            if ("tersedia".equalsIgnoreCase(r.getStatus())) {
                tersedia++;
            } else if ("dipinjam".equalsIgnoreCase(r.getStatus())) {
                dipinjam++;
            }
        }
        
        lblTotalRuangan.setText(String.valueOf(total));
        lblTersedia.setText(String.valueOf(tersedia));
        lblDipinjam.setText(String.valueOf(dipinjam));
    }
    
    /**
     * Handle tombol pinjam
     */
    private void handlePinjam(Ruangan ruangan) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Konfirmasi Peminjaman");
        confirm.setHeaderText("Pinjam Ruangan: " + ruangan.getNamaRuangan());
        confirm.setContentText("Anda akan diarahkan ke form peminjaman. Lanjutkan?");
        
        if (confirm.showAndWait().get() == ButtonType.OK) {
            // Simpan ruangan yang dipilih untuk form peminjaman
            UserPeminjamanFormController.setSelectedRuangan(ruangan);
            MainApp.showUserPeminjamanForm();
        }
    }
    
    @FXML
    private void handleFilter() {
        String selectedStatus = cbFilterStatus.getValue();
        
        filteredData.setPredicate(ruangan -> {
            // Filter berdasarkan search text
            boolean matchSearch = true;
            if (txtSearch.getText() != null && !txtSearch.getText().isEmpty()) {
                String searchText = txtSearch.getText().toLowerCase();
                matchSearch = ruangan.getNamaRuangan().toLowerCase().contains(searchText);
            }
            
            // Filter berdasarkan status
            boolean matchStatus = true;
            if (!"Semua".equals(selectedStatus)) {
                matchStatus = ruangan.getStatus().equalsIgnoreCase(selectedStatus);
            }
            
            return matchSearch && matchStatus;
        });
        
        updateSummary();
    }
    
    @FXML
    private void handleSearch() {
        handleFilter();
    }
    
    @FXML
    private void handleRefresh() {
        txtSearch.clear();
        cbFilterStatus.setValue("Semua");
        loadData();
    }
    
    @FXML
    private void handleKembali() {
        MainApp.showUserDashboard();
    }
}