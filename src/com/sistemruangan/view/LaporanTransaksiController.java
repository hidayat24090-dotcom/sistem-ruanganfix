package com.sistemruangan.view;

import com.sistemruangan.MainApp;
import com.sistemruangan.controller.LaporanController;
import com.sistemruangan.model.StatistikRuangan;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

/**
 * Controller untuk Laporan Transaksi dengan Charts dan Export PDF
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
    
    @FXML
    public void initialize() {
        System.out.println("🔧 Initializing LaporanTransaksiController...");
        
        laporanController = new LaporanController();
        
        // Setup table columns
        colRuangan.setCellValueFactory(new PropertyValueFactory<>("namaRuangan"));
        colTotalPeminjaman.setCellValueFactory(new PropertyValueFactory<>("totalPeminjaman"));
        colAktif.setCellValueFactory(new PropertyValueFactory<>("peminjamanAktif"));
        colSelesai.setCellValueFactory(new PropertyValueFactory<>("peminjamanSelesai"));
        colBatal.setCellValueFactory(new PropertyValueFactory<>("peminjamanBatal"));
        
        // Custom cell factory untuk persentase
        colPersentase.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPersentaseFormatted())
        );
        
        // Load data
        loadAllData();
        
        // Set periode label
        lblPeriode.setText("Periode: " + LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy")));
        
        System.out.println("✅ LaporanTransaksiController initialized");
    }
    
    /**
     * Load semua data laporan
     */
    private void loadAllData() {
        try {
            // Load summary statistics
            loadSummaryStats();
            
            // Load chart data
            loadBarChart();
            loadPieChart();
            
            // Load table data
            loadTableData();
            
            // Load top ruangan
            loadTopRuangan();
            
        } catch (Exception e) {
            System.err.println("❌ Error loading data: " + e.getMessage());
            e.printStackTrace();
            showError("Gagal memuat data laporan: " + e.getMessage());
        }
    }
    
    /**
     * Load summary statistics
     */
    private void loadSummaryStats() {
        Map<String, Object> stats = laporanController.getTotalStatistik();
        
        lblTotalRuangan.setText(String.valueOf(stats.getOrDefault("totalRuangan", 0)));
        lblRuanganTersedia.setText(stats.getOrDefault("ruanganTersedia", 0) + " Tersedia");
        lblTotalPeminjaman.setText(String.valueOf(stats.getOrDefault("totalPeminjaman", 0)));
        lblPeminjamanAktif.setText(String.valueOf(stats.getOrDefault("peminjamanAktif", 0)));
        lblPeminjamanSelesai.setText(String.valueOf(stats.getOrDefault("peminjamanSelesai", 0)));
    }
    
    /**
     * Load bar chart data
     */
    private void loadBarChart() {
        ObservableList<StatistikRuangan> statistikList = laporanController.getStatistikRuangan();
        
        XYChart.Series<String, Number> seriesTotal = new XYChart.Series<>();
        seriesTotal.setName("Total Peminjaman");
        
        XYChart.Series<String, Number> seriesAktif = new XYChart.Series<>();
        seriesAktif.setName("Aktif");
        
        XYChart.Series<String, Number> seriesSelesai = new XYChart.Series<>();
        seriesSelesai.setName("Selesai");
        
        for (StatistikRuangan stat : statistikList) {
            seriesTotal.getData().add(new XYChart.Data<>(stat.getNamaRuangan(), stat.getTotalPeminjaman()));
            seriesAktif.getData().add(new XYChart.Data<>(stat.getNamaRuangan(), stat.getPeminjamanAktif()));
            seriesSelesai.getData().add(new XYChart.Data<>(stat.getNamaRuangan(), stat.getPeminjamanSelesai()));
        }
        
        chartRuangan.getData().clear();
        chartRuangan.getData().addAll(seriesTotal, seriesAktif, seriesSelesai);
        
        // Style chart
        chartRuangan.setLegendVisible(true);
        chartRuangan.setAnimated(true);
    }
    
    /**
     * Load pie chart data
     */
    private void loadPieChart() {
        Map<String, Integer> statusData = laporanController.getStatusPeminjamanData();
        
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        
        for (Map.Entry<String, Integer> entry : statusData.entrySet()) {
            PieChart.Data data = new PieChart.Data(
                entry.getKey() + " (" + entry.getValue() + ")",
                entry.getValue()
            );
            pieChartData.add(data);
        }
        
        chartStatus.setData(pieChartData);
        chartStatus.setLegendVisible(true);
        chartStatus.setAnimated(true);
        
        // Apply colors to pie chart slices
        applyPieChartColors();
    }
    
    /**
     * Apply custom colors to pie chart
     */
    private void applyPieChartColors() {
        chartStatus.getData().forEach(data -> {
            String name = data.getName().toLowerCase();
            String color;
            
            if (name.contains("aktif")) {
                color = "#4facfe"; // Blue
            } else if (name.contains("selesai")) {
                color = "#43e97b"; // Green
            } else if (name.contains("batal")) {
                color = "#f5576c"; // Red
            } else {
                color = "#95a5a6"; // Gray
            }
            
            data.getNode().setStyle("-fx-pie-color: " + color + ";");
        });
    }
    
    /**
     * Load table data
     */
    private void loadTableData() {
        allStatistik = laporanController.getStatistikRuangan();
        filteredData = new FilteredList<>(allStatistik, p -> true);
        tableStatistik.setItems(filteredData);
    }
    
    /**
     * Load top 5 ruangan populer
     */
    private void loadTopRuangan() {
        vboxTopRuangan.getChildren().clear();
        ObservableList<StatistikRuangan> topRuangan = laporanController.getRuanganPopuler(5);
        
        int rank = 1;
        for (StatistikRuangan stat : topRuangan) {
            HBox card = createTopRuanganCard(rank, stat);
            vboxTopRuangan.getChildren().add(card);
            rank++;
        }
    }
    
    /**
     * Create card untuk top ruangan
     */
    private HBox createTopRuanganCard(int rank, StatistikRuangan stat) {
        HBox card = new HBox(15);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-background-radius: 10;");
        
        // Rank badge
        Label lblRank = new Label("#" + rank);
        lblRank.setStyle("-fx-background-color: " + getRankColor(rank) + "; " +
                        "-fx-text-fill: white; -fx-font-weight: bold; " +
                        "-fx-padding: 5 15 5 15; -fx-background-radius: 15; -fx-font-size: 16px;");
        
        // Ruangan name
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
            case 1: return "#FFD700"; // Gold
            case 2: return "#C0C0C0"; // Silver
            case 3: return "#CD7F32"; // Bronze
            case 4: return "#5B9BD5"; // Blue
            case 5: return "#70C1B3"; // Teal
            default: return "#95a5a6"; // Gray
        }
    }
    
    @FXML
    private void handleSearch() {
        String keyword = txtSearch.getText().trim().toLowerCase();
        
        filteredData.setPredicate(statistik -> {
            if (keyword.isEmpty()) {
                return true;
            }
            return statistik.getNamaRuangan().toLowerCase().contains(keyword);
        });
    }
    
    @FXML
    private void handleRefresh() {
        txtSearch.clear();
        loadAllData();
        showInfo("Data berhasil di-refresh!");
    }
    
    @FXML
    private void handleExportPDF() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Export Laporan PDF");
            fileChooser.setInitialFileName("Laporan_Transaksi_" + 
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".pdf");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );
            
            File file = fileChooser.showSaveDialog(btnExportPDF.getScene().getWindow());
            
            if (file != null) {
                generatePDF(file);
                showSuccess("Laporan berhasil di-export ke:\n" + file.getAbsolutePath());
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error exporting PDF: " + e.getMessage());
            e.printStackTrace();
            showError("Gagal export PDF: " + e.getMessage());
        }
    }
    
    /**
     * Generate PDF laporan
     */
    private void generatePDF(File file) throws Exception {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(file));
        
        document.open();
        
        // Title
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
        Paragraph title = new Paragraph("LAPORAN TRANSAKSI & STATISTIK", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(10);
        document.add(title);
        
        // Subtitle
        Font subtitleFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
        Paragraph subtitle = new Paragraph(
            "Sistem Inventaris & Peminjaman Ruangan\n" +
            "Periode: " + LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy")) + "\n" +
            "Tanggal Cetak: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")),
            subtitleFont
        );
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(20);
        document.add(subtitle);
        
        // Summary Statistics
        document.add(new Paragraph("RINGKASAN STATISTIK", new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD)));
        document.add(new Paragraph(" "));
        
        Map<String, Object> stats = laporanController.getTotalStatistik();
        
        PdfPTable summaryTable = new PdfPTable(2);
        summaryTable.setWidthPercentage(100);
        summaryTable.setSpacingAfter(20);
        
        addSummaryRow(summaryTable, "Total Ruangan", String.valueOf(stats.get("totalRuangan")));
        addSummaryRow(summaryTable, "Ruangan Tersedia", String.valueOf(stats.get("ruanganTersedia")));
        addSummaryRow(summaryTable, "Total Peminjaman", String.valueOf(stats.get("totalPeminjaman")));
        addSummaryRow(summaryTable, "Peminjaman Aktif", String.valueOf(stats.get("peminjamanAktif")));
        addSummaryRow(summaryTable, "Peminjaman Selesai", String.valueOf(stats.get("peminjamanSelesai")));
        
        document.add(summaryTable);
        
        // Detail Statistik per Ruangan
        document.add(new Paragraph("DETAIL STATISTIK PER RUANGAN", new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD)));
        document.add(new Paragraph(" "));
        
        PdfPTable detailTable = new PdfPTable(6);
        detailTable.setWidthPercentage(100);
        detailTable.setWidths(new int[]{3, 2, 1, 1, 1, 2});
        
        // Header
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
        addTableHeader(detailTable, "Nama Ruangan", headerFont);
        addTableHeader(detailTable, "Total", headerFont);
        addTableHeader(detailTable, "Aktif", headerFont);
        addTableHeader(detailTable, "Selesai", headerFont);
        addTableHeader(detailTable, "Batal", headerFont);
        addTableHeader(detailTable, "Persentase", headerFont);
        
        // Data
        Font dataFont = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL);
        for (StatistikRuangan stat : allStatistik) {
            addTableCell(detailTable, stat.getNamaRuangan(), dataFont);
            addTableCell(detailTable, String.valueOf(stat.getTotalPeminjaman()), dataFont);
            addTableCell(detailTable, String.valueOf(stat.getPeminjamanAktif()), dataFont);
            addTableCell(detailTable, String.valueOf(stat.getPeminjamanSelesai()), dataFont);
            addTableCell(detailTable, String.valueOf(stat.getPeminjamanBatal()), dataFont);
            addTableCell(detailTable, stat.getPersentaseFormatted(), dataFont);
        }
        
        document.add(detailTable);
        
        // Top 5 Ruangan Populer
        document.add(new Paragraph(" "));
        document.add(new Paragraph("TOP 5 RUANGAN PALING POPULER", new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD)));
        document.add(new Paragraph(" "));
        
        ObservableList<StatistikRuangan> topRuangan = laporanController.getRuanganPopuler(5);
        
        int rank = 1;
        for (StatistikRuangan stat : topRuangan) {
            document.add(new Paragraph(
                "#" + rank + " - " + stat.getNamaRuangan() + 
                " (" + stat.getTotalPeminjaman() + " peminjaman, " + 
                stat.getPersentaseFormatted() + ")",
                new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL)
            ));
            rank++;
        }
        
        // Footer
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));
        Paragraph footer = new Paragraph(
            "--- End of Report ---\n" +
            "Generated by Sistem Inventaris & Peminjaman Ruangan",
            new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC)
        );
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);
        
        document.close();
    }
    
    /**
     * Helper methods untuk PDF generation
     */
    private void addSummaryRow(PdfPTable table, String label, String value) {
        Font labelFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);
        Font valueFont = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL);
        
        PdfPCell cellLabel = new PdfPCell(new Phrase(label, labelFont));
        cellLabel.setBorder(Rectangle.NO_BORDER);
        cellLabel.setPadding(5);
        
        PdfPCell cellValue = new PdfPCell(new Phrase(value, valueFont));
        cellValue.setBorder(Rectangle.NO_BORDER);
        cellValue.setPadding(5);
        cellValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
        
        table.addCell(cellLabel);
        table.addCell(cellValue);
    }
    
    private void addTableHeader(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        table.addCell(cell);
    }
    
    private void addTableCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(3);
        table.addCell(cell);
    }
    
    @FXML
    private void handleKembali() {
        MainApp.showDashboard();
    }
    
    /**
     * Show alert dialogs
     */
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
    
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}