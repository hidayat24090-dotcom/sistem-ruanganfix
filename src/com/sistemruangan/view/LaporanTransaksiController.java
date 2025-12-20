package com.sistemruangan.view;

import com.sistemruangan.MainApp;
import com.sistemruangan.controller.LaporanController;
import com.sistemruangan.model.StatistikRuangan;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.concurrent.Task;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

/**
 * Controller untuk Laporan Transaksi - OPTIMIZED VERSION
 * Menggunakan lazy loading dan background tasks untuk performa lebih baik
 */
public class LaporanTransaksiController {
    
    @FXML private Label lblPeriode;
    @FXML private Label lblTotalRuangan;
    @FXML private Label lblRuanganTersedia;
    @FXML private Label lblTotalPeminjaman;
    @FXML private Label lblPeminjamanAktif;
    @FXML private Label lblPeminjamanSelesai;
    
    @FXML private BarChart<String, Number> chartRuangan;
    @FXML private PieChart chartStatus;
    
    @FXML private TableView<StatistikRuangan> tableStatistik;
    @FXML private TableColumn<StatistikRuangan, String> colRuangan;
    @FXML private TableColumn<StatistikRuangan, Integer> colTotalPeminjaman;
    @FXML private TableColumn<StatistikRuangan, Integer> colAktif;
    @FXML private TableColumn<StatistikRuangan, Integer> colSelesai;
    @FXML private TableColumn<StatistikRuangan, Integer> colBatal;
    @FXML private TableColumn<StatistikRuangan, String> colPersentase;
    
    @FXML private javafx.scene.control.TextField txtSearch;
    @FXML private VBox vboxTopRuangan;
    @FXML private Button btnExportPDF;
    @FXML private Button btnRefresh;
    @FXML private Button btnKembali;
    
    private LaporanController laporanController;
    private FilteredList<StatistikRuangan> filteredData;
    private ObservableList<StatistikRuangan> allStatistik;
    
    // Cache untuk optimasi
    private Map<String, Object> cachedStats;
    private boolean isDataLoaded = false;
    
    @FXML
    public void initialize() {
        System.out.println("🔧 Initializing Optimized LaporanTransaksiController...");
        
        try {
            laporanController = new LaporanController();
            
            // Setup table columns
            setupTableColumns();
            
            // Set periode label
            lblPeriode.setText("Periode: " + LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy")));
            
            // Load data in background untuk tidak freeze UI
            loadDataInBackground();
            
            System.out.println("✅ LaporanTransaksiController initialized");
            
        } catch (Exception e) {
            System.err.println("❌ Error initializing controller: " + e.getMessage());
            e.printStackTrace();
            showError("Gagal memuat laporan: " + e.getMessage());
        }
    }
    
    /**
     * Setup table columns dengan alignment yang benar
     */
    private void setupTableColumns() {
        // Left align untuk nama ruangan
        colRuangan.setCellValueFactory(new PropertyValueFactory<>("namaRuangan"));
        colRuangan.setStyle("-fx-alignment: CENTER-LEFT;");
        
        // Center align untuk angka
        colTotalPeminjaman.setCellValueFactory(new PropertyValueFactory<>("totalPeminjaman"));
        colTotalPeminjaman.setStyle("-fx-alignment: CENTER;");
        
        colAktif.setCellValueFactory(new PropertyValueFactory<>("peminjamanAktif"));
        colAktif.setStyle("-fx-alignment: CENTER;");
        
        colSelesai.setCellValueFactory(new PropertyValueFactory<>("peminjamanSelesai"));
        colSelesai.setStyle("-fx-alignment: CENTER;");
        
        colBatal.setCellValueFactory(new PropertyValueFactory<>("peminjamanBatal"));
        colBatal.setStyle("-fx-alignment: CENTER;");
        
        // Custom cell factory untuk persentase
        colPersentase.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPersentaseFormatted())
        );
        colPersentase.setStyle("-fx-alignment: CENTER;");
    }
    
    /**
     * Load data di background thread untuk tidak freeze UI
     */
    private void loadDataInBackground() {
        System.out.println("⏳ Loading data in background...");
        
        // Show loading indicator
        showLoadingIndicator(true);
        
        Task<Void> loadTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    // Load summary stats
                    cachedStats = laporanController.getTotalStatistik();
                    
                    // Load statistik ruangan
                    allStatistik = laporanController.getStatistikRuangan();
                    
                    return null;
                } catch (Exception e) {
                    System.err.println("❌ Error loading data: " + e.getMessage());
                    throw e;
                }
            }
            
            @Override
            protected void succeeded() {
                try {
                    // Update UI di JavaFX Application Thread
                    updateUI();
                    isDataLoaded = true;
                    showLoadingIndicator(false);
                    System.out.println("✅ Data loaded successfully");
                } catch (Exception e) {
                    System.err.println("❌ Error updating UI: " + e.getMessage());
                    e.printStackTrace();
                    showError("Gagal menampilkan data: " + e.getMessage());
                }
            }
            
            @Override
            protected void failed() {
                showLoadingIndicator(false);
                Throwable ex = getException();
                System.err.println("❌ Failed to load data: " + ex.getMessage());
                ex.printStackTrace();
                showError("Gagal memuat data: " + ex.getMessage());
            }
        };
        
        new Thread(loadTask).start();
    }
    
    /**
     * Update UI dengan data yang sudah di-load
     */
    private void updateUI() {
        // Update summary statistics
        updateSummaryStats();
        
        // Update charts (lazy - hanya ketika tab visible)
        updateChartsLazy();
        
        // Update table
        updateTable();
        
        // Update top ruangan
        updateTopRuangan();
    }
    
    /**
     * Update summary statistics
     */
    private void updateSummaryStats() {
        if (cachedStats == null) return;
        
        lblTotalRuangan.setText(String.valueOf(cachedStats.getOrDefault("totalRuangan", 0)));
        lblRuanganTersedia.setText(cachedStats.getOrDefault("ruanganTersedia", 0) + " Tersedia");
        lblTotalPeminjaman.setText(String.valueOf(cachedStats.getOrDefault("totalPeminjaman", 0)));
        lblPeminjamanAktif.setText(String.valueOf(cachedStats.getOrDefault("peminjamanAktif", 0)));
        lblPeminjamanSelesai.setText(String.valueOf(cachedStats.getOrDefault("peminjamanSelesai", 0)));
    }
    
    /**
     * Update charts dengan lazy loading
     */
    private void updateChartsLazy() {
        // Disable animation untuk performa lebih baik
        chartRuangan.setAnimated(false);
        chartStatus.setAnimated(false);
        
        // Load bar chart
        loadBarChart();
        
        // Load pie chart
        loadPieChart();
    }
    
    /**
     * Load bar chart data
     */
    private void loadBarChart() {
        try {
            if (allStatistik == null || allStatistik.isEmpty()) return;
            
            XYChart.Series<String, Number> seriesTotal = new XYChart.Series<>();
            seriesTotal.setName("Total");
            
            XYChart.Series<String, Number> seriesAktif = new XYChart.Series<>();
            seriesAktif.setName("Aktif");
            
            XYChart.Series<String, Number> seriesSelesai = new XYChart.Series<>();
            seriesSelesai.setName("Selesai");
            
            // Ambil max 10 ruangan untuk tidak overload chart
            int limit = Math.min(10, allStatistik.size());
            
            for (int i = 0; i < limit; i++) {
                StatistikRuangan stat = allStatistik.get(i);
                String nama = stat.getNamaRuangan();
                
                // Potong nama jika terlalu panjang
                if (nama.length() > 15) {
                    nama = nama.substring(0, 12) + "...";
                }
                
                seriesTotal.getData().add(new XYChart.Data<>(nama, stat.getTotalPeminjaman()));
                seriesAktif.getData().add(new XYChart.Data<>(nama, stat.getPeminjamanAktif()));
                seriesSelesai.getData().add(new XYChart.Data<>(nama, stat.getPeminjamanSelesai()));
            }
            
            chartRuangan.getData().clear();
            chartRuangan.getData().addAll(seriesTotal, seriesAktif, seriesSelesai);
            
        } catch (Exception e) {
            System.err.println("❌ Error loading bar chart: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Load pie chart data
     */
    private void loadPieChart() {
        try {
            Map<String, Integer> statusData = laporanController.getStatusPeminjamanData();
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            
            for (Map.Entry<String, Integer> entry : statusData.entrySet()) {
                if (entry.getValue() > 0) { // Hanya tampilkan yang ada datanya
                    PieChart.Data data = new PieChart.Data(
                        entry.getKey() + " (" + entry.getValue() + ")",
                        entry.getValue()
                    );
                    pieChartData.add(data);
                }
            }
            
            chartStatus.setData(pieChartData);
            applyPieChartColors();
            
        } catch (Exception e) {
            System.err.println("❌ Error loading pie chart: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Apply custom colors to pie chart
     */
    private void applyPieChartColors() {
        chartStatus.getData().forEach(data -> {
            String name = data.getName().toLowerCase();
            String color;
            
            if (name.contains("aktif")) {
                color = "#4facfe";
            } else if (name.contains("selesai")) {
                color = "#43e97b";
            } else if (name.contains("batal")) {
                color = "#f5576c";
            } else {
                color = "#95a5a6";
            }
            
            data.getNode().setStyle("-fx-pie-color: " + color + ";");
        });
    }
    
    /**
     * Update table dengan filtered data
     */
    private void updateTable() {
        if (allStatistik == null) return;
        
        filteredData = new FilteredList<>(allStatistik, p -> true);
        tableStatistik.setItems(filteredData);
    }
    
    /**
     * Update top 5 ruangan populer
     */
    private void updateTopRuangan() {
        try {
            vboxTopRuangan.getChildren().clear();
            ObservableList<StatistikRuangan> topRuangan = laporanController.getRuanganPopuler(5);
            
            if (topRuangan == null || topRuangan.isEmpty()) {
                Label noData = new Label("Belum ada data peminjaman");
                noData.setStyle("-fx-text-fill: #6c757d; -fx-font-size: 13px;");
                vboxTopRuangan.getChildren().add(noData);
                return;
            }
            
            int rank = 1;
            for (StatistikRuangan stat : topRuangan) {
                HBox card = createTopRuanganCard(rank, stat);
                vboxTopRuangan.getChildren().add(card);
                rank++;
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error loading top ruangan: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Create card untuk top ruangan
     */
    private HBox createTopRuanganCard(int rank, StatistikRuangan stat) {
        HBox card = new HBox(15);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 10; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);");
        
        // Rank badge
        Label lblRank = new Label("#" + rank);
        lblRank.setStyle("-fx-background-color: " + getRankColor(rank) + "; " +
                        "-fx-text-fill: white; -fx-font-weight: bold; " +
                        "-fx-padding: 5 15 5 15; -fx-background-radius: 15; -fx-font-size: 16px;");
        
        // Info
        VBox vboxInfo = new VBox(5);
        Label lblNama = new Label(stat.getNamaRuangan());
        lblNama.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        Label lblInfo = new Label(stat.getTotalPeminjaman() + " peminjaman • " + 
                                 stat.getPersentaseFormatted() + " dari total");
        lblInfo.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px;");
        
        vboxInfo.getChildren().addAll(lblNama, lblInfo);
        HBox.setHgrow(vboxInfo, Priority.ALWAYS);
        
        // Progress bar
        ProgressBar progressBar = new ProgressBar(stat.getPersentasePenggunaan() / 100.0);
        progressBar.setPrefWidth(150);
        progressBar.setStyle("-fx-accent: " + getRankColor(rank) + ";");
        
        card.getChildren().addAll(lblRank, vboxInfo, progressBar);
        return card;
    }
    
    /**
     * Get color based on rank
     */
    private String getRankColor(int rank) {
        switch (rank) {
            case 1: return "#FFD700";
            case 2: return "#C0C0C0";
            case 3: return "#CD7F32";
            case 4: return "#5B9BD5";
            case 5: return "#70C1B3";
            default: return "#95a5a6";
        }
    }
    
    /**
     * Show/hide loading indicator
     */
    private void showLoadingIndicator(boolean show) {
        // Disable buttons saat loading
        btnExportPDF.setDisable(show);
        btnRefresh.setDisable(show);
        
        if (show) {
            lblPeriode.setText("⏳ Memuat data...");
        } else {
            lblPeriode.setText("Periode: " + LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy")));
        }
    }
    
    @FXML
    private void handleSearch() {
        if (filteredData == null) return;
        
        String keyword = txtSearch.getText().trim().toLowerCase();
        
        filteredData.setPredicate(statistik -> {
            if (keyword.isEmpty()) return true;
            return statistik.getNamaRuangan().toLowerCase().contains(keyword);
        });
    }
    
    @FXML
    private void handleRefresh() {
        System.out.println("🔄 Refreshing data...");
        txtSearch.clear();
        isDataLoaded = false;
        cachedStats = null;
        loadDataInBackground();
    }
    
    @FXML
    private void handleExportPDF() {
        if (!isDataLoaded) {
            showWarning("Data masih dimuat. Mohon tunggu sebentar.");
            return;
        }
        
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Export Laporan PDF");
            fileChooser.setInitialFileName("Laporan_" + 
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );
            
            File file = fileChooser.showSaveDialog(btnExportPDF.getScene().getWindow());
            
            if (file != null) {
                // Export di background thread
                exportPDFInBackground(file);
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error exporting PDF: " + e.getMessage());
            e.printStackTrace();
            showError("Gagal export PDF: " + e.getMessage());
        }
    }
    
    /**
     * Export PDF in background
     */
    private void exportPDFInBackground(File file) {
        btnExportPDF.setDisable(true);
        btnExportPDF.setText("⏳ Exporting...");
        
        Task<Void> exportTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                generateSimplePDF(file);
                return null;
            }
            
            @Override
            protected void succeeded() {
                btnExportPDF.setDisable(false);
                btnExportPDF.setText("📄 Export PDF");
                showSuccess("Laporan berhasil di-export!\n" + file.getAbsolutePath());
            }
            
            @Override
            protected void failed() {
                btnExportPDF.setDisable(false);
                btnExportPDF.setText("📄 Export PDF");
                showError("Gagal export PDF: " + getException().getMessage());
            }
        };
        
        new Thread(exportTask).start();
    }
    
    /**
     * Generate simple PDF (optimized version)
     */
    private void generateSimplePDF(File file) throws Exception {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(file));
        
        document.open();
        
        // Title
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
        Paragraph title = new Paragraph("LAPORAN TRANSAKSI & STATISTIK", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);
        
        // Date
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 11);
        Paragraph date = new Paragraph("Tanggal: " + 
            LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")), normalFont);
        date.setAlignment(Element.ALIGN_CENTER);
        date.setSpacingAfter(30);
        document.add(date);
        
        // Summary
        document.add(new Paragraph("RINGKASAN", new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD)));
        document.add(new Paragraph(" "));
        
        if (cachedStats != null) {
            document.add(new Paragraph("Total Ruangan: " + cachedStats.get("totalRuangan"), normalFont));
            document.add(new Paragraph("Total Peminjaman: " + cachedStats.get("totalPeminjaman"), normalFont));
            document.add(new Paragraph("Peminjaman Aktif: " + cachedStats.get("peminjamanAktif"), normalFont));
            document.add(new Paragraph("Peminjaman Selesai: " + cachedStats.get("peminjamanSelesai"), normalFont));
        }
        
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));
        
        // Table
        document.add(new Paragraph("DETAIL STATISTIK", new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD)));
        document.add(new Paragraph(" "));
        
        if (allStatistik != null && !allStatistik.isEmpty()) {
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            
            // Header
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
            table.addCell(new PdfPCell(new Phrase("Ruangan", headerFont)));
            table.addCell(new PdfPCell(new Phrase("Total", headerFont)));
            table.addCell(new PdfPCell(new Phrase("Aktif", headerFont)));
            table.addCell(new PdfPCell(new Phrase("Selesai", headerFont)));
            table.addCell(new PdfPCell(new Phrase("Batal", headerFont)));
            
            // Data
            Font dataFont = new Font(Font.FontFamily.HELVETICA, 9);
            for (StatistikRuangan stat : allStatistik) {
                table.addCell(new PdfPCell(new Phrase(stat.getNamaRuangan(), dataFont)));
                table.addCell(new PdfPCell(new Phrase(String.valueOf(stat.getTotalPeminjaman()), dataFont)));
                table.addCell(new PdfPCell(new Phrase(String.valueOf(stat.getPeminjamanAktif()), dataFont)));
                table.addCell(new PdfPCell(new Phrase(String.valueOf(stat.getPeminjamanSelesai()), dataFont)));
                table.addCell(new PdfPCell(new Phrase(String.valueOf(stat.getPeminjamanBatal()), dataFont)));
            }
            
            document.add(table);
        }
        
        document.close();
    }
    
    @FXML
    private void handleKembali() {
        MainApp.showDashboard();
    }
    
    // Alert helper methods
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Berhasil");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
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
}