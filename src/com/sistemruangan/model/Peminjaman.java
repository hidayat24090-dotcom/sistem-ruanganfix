package com.sistemruangan.model;

import javafx.beans.property.*;
import java.time.LocalDate;

/**
 * Model class untuk Peminjaman
 */
public class Peminjaman {
    private final IntegerProperty id;
    private final IntegerProperty idRuangan;
    private final StringProperty namaRuangan;
    private final StringProperty namaPeminjam;
    private final StringProperty keperluan;
    private final ObjectProperty<LocalDate> tanggalPinjam;
    private final ObjectProperty<LocalDate> tanggalKembali;
    private final StringProperty statusPeminjaman;
    
    // Constructor
    public Peminjaman() {
        this(0, 0, "", "", "", LocalDate.now(), LocalDate.now(), "aktif");
    }
    
    public Peminjaman(int id, int idRuangan, String namaRuangan, 
                      String namaPeminjam, String keperluan,
                      LocalDate tanggalPinjam, LocalDate tanggalKembali, 
                      String statusPeminjaman) {
        this.id = new SimpleIntegerProperty(id);
        this.idRuangan = new SimpleIntegerProperty(idRuangan);
        this.namaRuangan = new SimpleStringProperty(namaRuangan);
        this.namaPeminjam = new SimpleStringProperty(namaPeminjam);
        this.keperluan = new SimpleStringProperty(keperluan);
        this.tanggalPinjam = new SimpleObjectProperty<>(tanggalPinjam);
        this.tanggalKembali = new SimpleObjectProperty<>(tanggalKembali);
        this.statusPeminjaman = new SimpleStringProperty(statusPeminjaman);
    }
    
    // Getters and Setters
    public int getId() { return id.get(); }
    public void setId(int value) { id.set(value); }
    public IntegerProperty idProperty() { return id; }
    
    public int getIdRuangan() { return idRuangan.get(); }
    public void setIdRuangan(int value) { idRuangan.set(value); }
    public IntegerProperty idRuanganProperty() { return idRuangan; }
    
    public String getNamaRuangan() { return namaRuangan.get(); }
    public void setNamaRuangan(String value) { namaRuangan.set(value); }
    public StringProperty namaRuanganProperty() { return namaRuangan; }
    
    public String getNamaPeminjam() { return namaPeminjam.get(); }
    public void setNamaPeminjam(String value) { namaPeminjam.set(value); }
    public StringProperty namaPeminjamProperty() { return namaPeminjam; }
    
    public String getKeperluan() { return keperluan.get(); }
    public void setKeperluan(String value) { keperluan.set(value); }
    public StringProperty keperluanProperty() { return keperluan; }
    
    public LocalDate getTanggalPinjam() { return tanggalPinjam.get(); }
    public void setTanggalPinjam(LocalDate value) { tanggalPinjam.set(value); }
    public ObjectProperty<LocalDate> tanggalPinjamProperty() { return tanggalPinjam; }
    
    public LocalDate getTanggalKembali() { return tanggalKembali.get(); }
    public void setTanggalKembali(LocalDate value) { tanggalKembali.set(value); }
    public ObjectProperty<LocalDate> tanggalKembaliProperty() { return tanggalKembali; }
    
    public String getStatusPeminjaman() { return statusPeminjaman.get(); }
    public void setStatusPeminjaman(String value) { statusPeminjaman.set(value); }
    public StringProperty statusPeminjamanProperty() { return statusPeminjaman; }
}