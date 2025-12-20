package com.sistemruangan.view;

import com.sistemruangan.MainApp;
import com.sistemruangan.controller.RuanganController;
import com.sistemruangan.model.Ruangan;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.File;

/**
 * Controller untuk Daftar Ruangan User - BEAUTIFUL CARD VIEW
 */
public class UserRuanganListController {
    
    @FXML private ScrollPane scrollPane;
    @FXML private FlowPane flowPaneCards;
    @FXML private ComboBox<String> cbFilterStatus;
    @FXML private TextField txtSearch;
    @FXML private Label lblTotalRuangan;
    @FXML private Label lblTersedia;
    @FXML private Label lblDipinjam;
    @FXML private Button btnRefresh;
    @FXML private Button btnKembali;
    
    private RuanganController ruanganController;
    private FilteredList<Ruangan> filteredData;
    private static final String FOTO_DIR = "resources/images/ruangan/";
    
    @FXML
    public void initialize() {
        ruanganController = new RuanganController();
        
        // Setup filter combo box
        cbFilterStatus.getItems().addAll("Semua", "Tersedia", "Dipinjam");
        cbFilterStatus.setValue("Semua");
        
        // Setup FlowPane for cards
        flowPaneCards.setHgap(20);
        flowPaneCards.setVgap(20);
        flowPaneCards.setPadding(new Insets(10));
        flowPaneCards.setStyle("-fx-background-color: transparent;");
        
        // Load data
        loadData();
    }
    
    /**
     * Load data ruangan sebagai cards
     */
    private void loadData() {
        ObservableList<Ruangan> ruanganList = ruanganController.getAllRuangan();
        filteredData = new FilteredList<>(ruanganList, p -> true);
        
        displayCards();
        updateSummary();
    }
    
    /**
     * Display ruangan sebagai cards yang cantik
     */
    private void displayCards() {
        flowPaneCards.getChildren().clear();
        
        for (Ruangan ruangan : filteredData) {
            VBox card = createRuanganCard(ruangan);
            flowPaneCards.getChildren().add(card);
        }
        
        if (filteredData.isEmpty()) {
            Label noData = new Label("Tidak ada ruangan yang ditemukan");
            noData.setStyle("-fx-font-size: 16px; -fx-text-fill: #7f8c8d;");
            flowPaneCards.getChildren().add(noData);
        }
    }
    
    /**
     * Create card yang cantik untuk setiap ruangan
     */
    private VBox createRuanganCard(Ruangan ruangan) {
        VBox card = new VBox(15);
        card.setPrefWidth(320);
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 20, 0, 0, 5);" +
            "-fx-padding: 0;" +
            "-fx-cursor: hand;"
        );
        
        // Hover effect
        card.setOnMouseEntered(e -> 
            card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 15;" +
                "-fx-effect: dropshadow(gaussian, rgba(91,155,213,0.4), 25, 0, 0, 8);" +
                "-fx-padding: 0;" +
                "-fx-cursor: hand;" +
                "-fx-scale-x: 1.03;" +
                "-fx-scale-y: 1.03;"
            )
        );
        
        card.setOnMouseExited(e -> 
            card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 15;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 20, 0, 0, 5);" +
                "-fx-padding: 0;" +
                "-fx-cursor: hand;"
            )
        );
        
        // Image section
        VBox imageContainer = new VBox();
        imageContainer.setPrefHeight(200);
        imageContainer.setStyle(
            "-fx-background-color: #f5f7fa;" +
            "-fx-background-radius: 15 15 0 0;"
        );
        imageContainer.setAlignment(Pos.CENTER);
        
        ImageView imageView = new ImageView();
        imageView.setFitWidth(320);
        imageView.setFitHeight(200);
        imageView.setPreserveRatio(false);
        
        // Load foto ruangan
        try {
            if (ruangan.getFotoPath() != null && !ruangan.getFotoPath().isEmpty()) {
                File fotoFile = new File(FOTO_DIR + ruangan.getFotoPath());
                if (fotoFile.exists()) {
                    Image image = new Image(fotoFile.toURI().toString());
                    imageView.setImage(image);
                } else {
                    setDefaultImage(imageView);
                }
            } else {
                setDefaultImage(imageView);
            }
        } catch (Exception e) {
            setDefaultImage(imageView);
        }
        
        imageContainer.getChildren().add(imageView);
        
        // Content section
        VBox content = new VBox(12);
        content.setPadding(new Insets(20));
        
        // Nama Ruangan
        Label lblNama = new Label(ruangan.getNamaRuangan());
        lblNama.setFont(Font.font("System", FontWeight.BOLD, 18));
        lblNama.setStyle("-fx-text-fill: #2c3e50;");
        lblNama.setWrapText(true);
        
        // Info Row 1: Kapasitas
        HBox infoKapasitas = new HBox(8);
        infoKapasitas.setAlignment(Pos.CENTER_LEFT);
        
        Label iconKapasitas = new Label("👥");
        iconKapasitas.setStyle("-fx-font-size: 16px;");
        
        Label lblKapasitas = new Label(ruangan.getJumlahKursi() + " Kursi");
        lblKapasitas.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 13px;");
        
        infoKapasitas.getChildren().addAll(iconKapasitas, lblKapasitas);
        
        // Fasilitas
        VBox vboxFasilitas = new VBox(5);
        
        Label lblFasilitasTitle = new Label("📋 Fasilitas:");
        lblFasilitasTitle.setStyle("-fx-text-fill: #34495e; -fx-font-weight: bold; -fx-font-size: 12px;");
        
        VBox fasilitasList = new VBox(3);
        if (ruangan.getFasilitas() != null && !ruangan.getFasilitas().trim().isEmpty()) {
            String[] fasilitas = ruangan.getFasilitas().split(",");
            int count = 0;
            for (String f : fasilitas) {
                if (count >= 3) break; // Maksimal 3 fasilitas di card
                Label lblFas = new Label("• " + f.trim());
                lblFas.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 11px;");
                fasilitasList.getChildren().add(lblFas);
                count++;
            }
            if (fasilitas.length > 3) {
                Label lblMore = new Label("  +" + (fasilitas.length - 3) + " lainnya");
                lblMore.setStyle("-fx-text-fill: #5B9BD5; -fx-font-size: 11px; -fx-font-style: italic;");
                fasilitasList.getChildren().add(lblMore);
            }
        } else {
            Label lblNoFas = new Label("• Tidak ada fasilitas");
            lblNoFas.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 11px; -fx-font-style: italic;");
            fasilitasList.getChildren().add(lblNoFas);
        }
        
        vboxFasilitas.getChildren().addAll(lblFasilitasTitle, fasilitasList);
        
        // Status Badge
        HBox statusRow = new HBox();
        statusRow.setAlignment(Pos.CENTER_LEFT);
        
        Label lblStatus = new Label(ruangan.getStatus().toUpperCase());
        lblStatus.setPadding(new Insets(5, 15, 5, 15));
        lblStatus.setStyle(
            "-fx-background-radius: 15;" +
            "-fx-font-size: 11px;" +
            "-fx-font-weight: bold;" +
            (ruangan.getStatus().equalsIgnoreCase("tersedia") 
                ? "-fx-background-color: #d4edda; -fx-text-fill: #155724;" 
                : "-fx-background-color: #fff3cd; -fx-text-fill: #856404;")
        );
        
        statusRow.getChildren().add(lblStatus);
        
        // Button Pinjam
        Button btnPinjam = new Button(
            ruangan.getStatus().equalsIgnoreCase("tersedia") 
                ? "📝 Ajukan Peminjaman" 
                : "❌ Tidak Tersedia"
        );
        btnPinjam.setMaxWidth(Double.MAX_VALUE);
        btnPinjam.setDisable(!ruangan.getStatus().equalsIgnoreCase("tersedia"));
        btnPinjam.setStyle(
            ruangan.getStatus().equalsIgnoreCase("tersedia")
                ? "-fx-background-color: linear-gradient(to right, #667eea, #764ba2);" +
                  "-fx-text-fill: white;" +
                  "-fx-font-weight: bold;" +
                  "-fx-font-size: 13px;" +
                  "-fx-padding: 12 20;" +
                  "-fx-background-radius: 8;" +
                  "-fx-cursor: hand;"
                : "-fx-background-color: #e8edf2;" +
                  "-fx-text-fill: #95a5a6;" +
                  "-fx-font-weight: bold;" +
                  "-fx-font-size: 13px;" +
                  "-fx-padding: 12 20;" +
                  "-fx-background-radius: 8;"
        );
        
        if (ruangan.getStatus().equalsIgnoreCase("tersedia")) {
            btnPinjam.setOnMouseEntered(e -> 
                btnPinjam.setStyle(
                    "-fx-background-color: linear-gradient(to right, #5568d3, #6a3d8f);" +
                    "-fx-text-fill: white;" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-size: 13px;" +
                    "-fx-padding: 12 20;" +
                    "-fx-background-radius: 8;" +
                    "-fx-cursor: hand;"
                )
            );
            
            btnPinjam.setOnMouseExited(e -> 
                btnPinjam.setStyle(
                    "-fx-background-color: linear-gradient(to right, #667eea, #764ba2);" +
                    "-fx-text-fill: white;" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-size: 13px;" +
                    "-fx-padding: 12 20;" +
                    "-fx-background-radius: 8;" +
                    "-fx-cursor: hand;"
                )
            );
        }
        
        btnPinjam.setOnAction(e -> handlePinjam(ruangan));
        
        // Add all to content
        content.getChildren().addAll(
            lblNama,
            new Separator(),
            infoKapasitas,
            vboxFasilitas,
            statusRow,
            btnPinjam
        );
        
        // Add to card
        card.getChildren().addAll(imageContainer, content);
        
        return card;
    }
    
    /**
     * Set default image jika foto tidak ada
     */
    private void setDefaultImage(ImageView imageView) {
        try {
            Image defaultImage = new Image(getClass().getResourceAsStream("/images/default_room.png"));
            imageView.setImage(defaultImage);
        } catch (Exception e) {
            // Create placeholder
            imageView.setStyle("-fx-background-color: #e8edf2;");
        }
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
        
        displayCards();
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