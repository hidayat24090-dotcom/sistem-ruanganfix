package com.sistemruangan.view;

import com.sistemruangan.MainApp;
import com.sistemruangan.controller.LaporanController;
import com.sistemruangan.model.StatistikRuangan;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

/**
 * Controller untuk Laporan Transaksi - BEAUTIFUL & COMPLETE VERSION
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
    @FXML private LineChart<String, Number> chartTrend;
    
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
    @FXML private ProgressIndicator progressIndicator;
    
    private LaporanController laporanController;
    private FilteredList<StatistikRuangan> filteredData;
    private ObservableList<StatistikRuangan> allStatistik;
    private Map<String, Object> cachedStats;
    
    @FXML
    public void initialize() {
        System.out.println("🔧 Initializing Beautiful LaporanTransaksiController...");
        
        try {
            laporanController = new LaporanController();
            
            // Setup table columns
            setupTableColumns();
            
            // Setup charts
            setupCharts();
            
            // Set periode label
            lblPeriode.setText("Periode: " + LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy")));
            
            // Load data
            loadDataAsync();
            
            System.out.println("✅ LaporanTransaksiController initialized");
            
        } catch (Exception e) {
            System.err.println("❌ Error initializing controller: " + e.getMessage());
            e.printStackTrace();
            showError("Gagal memuat laporan: " + e.getMessage());
        }
    }
    
    /**
     * Setup table columns
     */
    private void setupTableColumns() {
        colRuangan.setCellValueFactory(new PropertyValueFactory<>("namaRuangan"));
        colRuangan.setStyle("-fx-alignment: CENTER-LEFT;");
        
        colTotalPeminjaman.setCellValueFactory(new PropertyValueFactory<>("totalPeminjaman"));
        colTotalPeminjaman.setStyle("-fx-alignment: CENTER;");
        
        colAktif.setCellValueFactory(new PropertyValueFactory<>("peminjamanAktif"));
        colAktif.setStyle("-fx-alignment: CENTER;");
        
        colSelesai.setCellValueFactory(new PropertyValueFactory<>("peminjamanSelesai"));
        colSelesai.setStyle("-fx-alignment: CENTER;");
        
        colBatal.setCellValueFactory(new PropertyValueFactory<>("peminjamanBatal"));
        colBatal.setStyle("-fx-alignment: CENTER;");
        
        colPersentase.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPersentaseFormatted())
        );
        colPersentase.setStyle("-fx-alignment: CENTER;");
    }
    
    /**
     * Setup charts dengan styling yang cantik
     */
    private void setupCharts() {
        // Bar Chart
        chartRuangan.setLegendSide(Side.TOP);
        chartRuangan.setAnimated(true);
        chartRuangan.setLegendVisible(true);
        
        // Pie Chart
        chartStatus.setLegendSide(Side.RIGHT);
        chartStatus.setAnimated(true);
        chartStatus.setLabelLineLength(10);
        chartStatus.setLegendVisible(true);
        
        // Line Chart (Trend)
        if (chartTrend != null) {
            chartTrend.setLegendSide(Side.TOP);
            chartTrend.setAnimated(true);
            chartTrend.setCreateSymbols(true);
        }
    }
    
    /**
     * Load data asynchronously
     */
    private void loadDataAsync() {
        progressIndicator.setVisible(true);
        btnExportPDF.setDisable(true);
        btnRefresh.setDisable(true);
        
        Task<Void> loadTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // Load summary stats
                cachedStats = laporanController.getTotalStatistik();
                
                // Load statistik ruangan
                allStatistik = laporanController.getStatistikRuangan();
                
                return null;
            }
            
            @Override
            protected void succeeded() {
                updateUI();
                progressIndicator.setVisible(false);
                btnExportPDF.setDisable(false);
                btnRefresh.setDisable(false);
                System.out.println("✅ Data loaded successfully");
            }
            
            @Override
            protected void failed() {
                progressIndicator.setVisible(false);
                btnExportPDF.setDisable(false);
                btnRefresh.setDisable(false);
                showError("Gagal memuat data: " + getException().getMessage());
            }
        };
        
        new Thread(loadTask).start();
    }
    
    /**
     * Update UI dengan data
     */
    private void updateUI() {
        updateSummaryStats();
        updateCharts();
        updateTable();
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
     * Update all charts
     */
    private void updateCharts() {
        updateBarChart();
        updatePieChart();
        updateLineChart();
    }
    
    /**
     * Update bar chart
     */
    private void updateBarChart() {
        try {
            if (allStatistik == null || allStatistik.isEmpty()) return;
            
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Total Peminjaman");
            
            // Top 8 ruangan
            int limit = Math.min(8, allStatistik.size());
            
            for (int i = 0; i < limit; i++) {
                StatistikRuangan stat = allStatistik.get(i);
                String nama = stat.getNamaRuangan();
                
                if (nama.length() > 15) {
                    nama = nama.substring(0, 12) + "...";
                }
                
                series.getData().add(new XYChart.Data<>(nama, stat.getTotalPeminjaman()));
            }
            
            chartRuangan.getData().clear();
            chartRuangan.getData().add(series);
            
            // Style bars
            for (XYChart.Data<String, Number> data : series.getData()) {
                data.getNode().setStyle("-fx-bar-fill: #5B9BD5;");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error updating bar chart: " + e.getMessage());
        }
    }
    
    /**
     * Update pie chart
     */
    private void updatePieChart() {
        try {
            Map<String, Integer> statusData = laporanController.getStatusPeminjamanData();
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            
            int totalAll = statusData.values().stream().mapToInt(Integer::intValue).sum();
            
            for (Map.Entry<String, Integer> entry : statusData.entrySet()) {
                if (entry.getValue() > 0) {
                    double percentage = (entry.getValue() * 100.0) / totalAll;
                    PieChart.Data data = new PieChart.Data(
                        entry.getKey().toUpperCase() + " (" + 
                        String.format("%.1f%%", percentage) + ")",
                        entry.getValue()
                    );
                    pieChartData.add(data);
                }
            }
            
            chartStatus.setData(pieChartData);
            
            // Apply colors
            javafx.application.Platform.runLater(() -> {
                int index = 0;
                for (PieChart.Data data : chartStatus.getData()) {
                    String color = getStatusColor(data.getName());
                    data.getNode().setStyle("-fx-pie-color: " + color + ";");
                    index++;
                }
            });
            
        } catch (Exception e) {
            System.err.println("❌ Error updating pie chart: " + e.getMessage());
        }
    }
    
    /**
     * Update line chart (trend)
     */
    private void updateLineChart() {
        if (chartTrend == null) return;
        
        try {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Trend Peminjaman");
            
            // Sample data - last 6 months
            String[] months = {"Jan", "Feb", "Mar", "Apr", "Mei", "Jun"};
            
            if (allStatistik != null && !allStatistik.isEmpty()) {
                // Distribute data across months (simulation)
                int totalPeminjaman = allStatistik.stream()
                    .mapToInt(StatistikRuangan::getTotalPeminjaman)
                    .sum();
                
                int avgPerMonth = totalPeminjaman / 6;
                
                for (int i = 0; i < months.length; i++) {
                    // Add some variance
                    int value = (int)(avgPerMonth * (0.7 + Math.random() * 0.6));
                    series.getData().add(new XYChart.Data<>(months[i], value));
                }
            }
            
            chartTrend.getData().clear();
            chartTrend.getData().add(series);
            
        } catch (Exception e) {
            System.err.println("❌ Error updating line chart: " + e.getMessage());
        }
    }
    
    /**
     * Get color based on status
     */
    private String getStatusColor(String name) {
        if (name.toLowerCase().contains("aktif")) {
            return "#4facfe";
        } else if (name.toLowerCase().contains("selesai")) {
            return "#43e97b";
        } else if (name.toLowerCase().contains("batal")) {
            return "#f5576c";
        }
        return "#95a5a6";
    }
    
    /**
     * Update table
     */
    private void updateTable() {
        if (allStatistik == null) return;
        
        filteredData = new FilteredList<>(allStatistik, p -> true);
        tableStatistik.setItems(filteredData);
    }
    
    /**
     * Update top 5 ruangan
     */
    private void updateTopRuangan() {
        try {
            vboxTopRuangan.getChildren().clear();
            ObservableList<StatistikRuangan> topRuangan = laporanController.getRuanganPopuler(5);
            
            if (topRuangan == null || topRuangan.isEmpty()) {
                Label noData = new Label("Belum ada data peminjaman");
                noData.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 13px;");
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
        }
    }
    
    /**
     * Create card untuk top ruangan
     */
    private HBox createTopRuanganCard(int rank, StatistikRuangan stat) {
        HBox card = new HBox(15);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-padding: 15;" +
            "-fx-background-radius: 10;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);"
        );
        
        // Rank badge
        Label lblRank = new Label("#" + rank);
        lblRank.setStyle(
            "-fx-background-color: " + getRankColor(rank) + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 5 15;" +
            "-fx-background-radius: 15;" +
            "-fx-font-size: 16px;"
        );
        
        // Info
        VBox vboxInfo = new VBox(5);
        Label lblNama = new Label(stat.getNamaRuangan());
        lblNama.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        Label lblInfo = new Label(
            stat.getTotalPeminjaman() + " peminjaman • " + 
            stat.getPersentaseFormatted()
        );
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
            case 1: return "#FFD700"; // Gold
            case 2: return "#C0C0C0"; // Silver
            case 3: return "#CD7F32"; // Bronze
            case 4: return "#5B9BD5"; // Blue
            case 5: return "#70C1B3"; // Green
            default: return "#95a5a6";
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
        cachedStats = null;
        loadDataAsync();
    }
    
    @FXML
    private void handleExportPDF() {
        exportPDFSimple();
    }
    
    /**
     * Simple PDF export
     */
    private void exportPDFSimple() {
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
                generatePDF(file);
                showSuccess("Laporan berhasil di-export!\n" + file.getAbsolutePath());
            }
            
        } catch (Exception e) {
            showError("Gagal export PDF: " + e.getMessage());
        }
    }
    
    /**
     * Generate PDF
     */
    private void generatePDF(File file) throws Exception {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(file));
        
        document.open();
        
        // Title
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
        Paragraph title = new Paragraph("LAPORAN STATISTIK PEMINJAMAN RUANGAN", titleFont);
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
            
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
            table.addCell(new PdfPCell(new Phrase("Ruangan", headerFont)));
            table.addCell(new PdfPCell(new Phrase("Total", headerFont)));
            table.addCell(new PdfPCell(new Phrase("Aktif", headerFont)));
            table.addCell(new PdfPCell(new Phrase("Selesai", headerFont)));
            table.addCell(new PdfPCell(new Phrase("Batal", headerFont)));
            
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
}