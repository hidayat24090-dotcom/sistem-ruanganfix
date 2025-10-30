package com.sistemruangan.model;

import javafx.beans.property.*;

/**
 * Model class untuk Ruangan
 */
public class Ruangan {
    private final IntegerProperty id;
    private final StringProperty namaRuangan;
    private final IntegerProperty jumlahKursi;
    private final BooleanProperty adaProyektor;
    private final StringProperty kondisiHdmi;
    private final StringProperty status;
    
    // Constructor
    public Ruangan() {
        this(0, "", 0, false, "baik", "tersedia");
    }
    
    public Ruangan(int id, String namaRuangan, int jumlahKursi, 
                   boolean adaProyektor, String kondisiHdmi, String status) {
        this.id = new SimpleIntegerProperty(id);
        this.namaRuangan = new SimpleStringProperty(namaRuangan);
        this.jumlahKursi = new SimpleIntegerProperty(jumlahKursi);
        this.adaProyektor = new SimpleBooleanProperty(adaProyektor);
        this.kondisiHdmi = new SimpleStringProperty(kondisiHdmi);
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
    
    public boolean isAdaProyektor() { return adaProyektor.get(); }
    public void setAdaProyektor(boolean value) { adaProyektor.set(value); }
    public BooleanProperty adaProyektorProperty() { return adaProyektor; }
    
    public String getKondisiHdmi() { return kondisiHdmi.get(); }
    public void setKondisiHdmi(String value) { kondisiHdmi.set(value); }
    public StringProperty kondisiHdmiProperty() { return kondisiHdmi; }
    
    public String getStatus() { return status.get(); }
    public void setStatus(String value) { status.set(value); }
    public StringProperty statusProperty() { return status; }
    
    // Helper method untuk menampilkan proyektor
    public String getProyektorText() {
        return adaProyektor.get() ? "Ada" : "Tidak Ada";
    }
    
    @Override
    public String toString() {
        return namaRuangan.get();
    }
}