package com.sistemruangan.model;

import javafx.beans.property.*;

/**
 * Model class untuk Ruangan - UPDATED with Fasilitas List
 */
public class Ruangan {
    private final IntegerProperty id;
    private final StringProperty namaRuangan;
    private final IntegerProperty jumlahKursi;
    private final StringProperty fasilitas;  // NEW: Fasilitas as comma-separated string
    private final StringProperty status;
    
    // Constructor
    public Ruangan() {
        this(0, "", 0, "", "tersedia");
    }
    
    public Ruangan(int id, String namaRuangan, int jumlahKursi, 
                   String fasilitas, String status) {
        this.id = new SimpleIntegerProperty(id);
        this.namaRuangan = new SimpleStringProperty(namaRuangan);
        this.jumlahKursi = new SimpleIntegerProperty(jumlahKursi);
        this.fasilitas = new SimpleStringProperty(fasilitas != null ? fasilitas : "");
        this.status = new SimpleStringProperty(status);
    }
    
    // Getters and Setters untuk Properties
    public int getId() { return id.get(); }
    public void setId(int value) { id.set(value); }
    public IntegerProperty idProperty() { return id; }
    
    public String getNamaRuangan() { return namaRuangan.get(); }
    public void setNamaRuangan(String value) { namaRuangan.set(value); }
    public StringProperty namaRuanganProperty() { return namaRuangan; }
    
    public int getJumlahKursi() { return jumlahKursi.get(); }
    public void setJumlahKursi(int value) { jumlahKursi.set(value); }
    public IntegerProperty jumlahKursiProperty() { return jumlahKursi; }
    
    public String getFasilitas() { return fasilitas.get(); }
    public void setFasilitas(String value) { fasilitas.set(value != null ? value : ""); }
    public StringProperty fasilitasProperty() { return fasilitas; }
    
    public String getStatus() { return status.get(); }
    public void setStatus(String value) { status.set(value); }
    public StringProperty statusProperty() { return status; }
    
    // Helper methods untuk fasilitas
    
    /**
     * Get fasilitas as array (split by comma)
     */
    public String[] getFasilitasArray() {
        if (fasilitas.get() == null || fasilitas.get().trim().isEmpty()) {
            return new String[0];
        }
        return fasilitas.get().split(",");
    }
    
    /**
     * Get formatted fasilitas untuk display
     */
    public String getFasilitasFormatted() {
        if (fasilitas.get() == null || fasilitas.get().trim().isEmpty()) {
            return "Tidak ada";
        }
        
        String[] items = getFasilitasArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < items.length; i++) {
            sb.append("• ").append(items[i].trim());
            if (i < items.length - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
    
    /**
     * Check if has specific fasilitas
     */
    public boolean hasFasilitas(String fasilitasName) {
        if (fasilitas.get() == null || fasilitas.get().trim().isEmpty()) {
            return false;
        }
        
        String[] items = getFasilitasArray();
        for (String item : items) {
            if (item.trim().toLowerCase().contains(fasilitasName.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        return namaRuangan.get();
    }
}