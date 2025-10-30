package com.sistemruangan.controller;

import com.sistemruangan.model.Ruangan;
import com.sistemruangan.util.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

/**
 * Controller untuk operasi CRUD Ruangan
 */
public class RuanganController {
    
    /**
     * Mengambil semua data ruangan dari database
     */
    public ObservableList<Ruangan> getAllRuangan() {
        ObservableList<Ruangan> ruanganList = FXCollections.observableArrayList();
        String query = "SELECT * FROM ruangan ORDER BY id DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Ruangan ruangan = new Ruangan(
                    rs.getInt("id"),
                    rs.getString("nama_ruangan"),
                    rs.getInt("jumlah_kursi"),
                    rs.getBoolean("ada_proyektor"),
                    rs.getString("kondisi_hdmi"),
                    rs.getString("status")
                );
                ruanganList.add(ruangan);
            }
        } catch (SQLException e) {
            System.err.println(new StringBuilder("Error mengambil data ruangan: ").append(e.getMessage()).toString());
            e.printStackTrace();
        }
        
        return ruanganList;
    }
    
    /**
     * Menambah ruangan baru ke database
     */
    public boolean tambahRuangan(Ruangan ruangan) {
        String query = "INSERT INTO ruangan (nama_ruangan, jumlah_kursi, ada_proyektor, kondisi_hdmi, status) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, ruangan.getNamaRuangan());
            pstmt.setInt(2, ruangan.getJumlahKursi());
            pstmt.setBoolean(3, ruangan.isAdaProyektor());
            pstmt.setString(4, ruangan.getKondisiHdmi());
            pstmt.setString(5, ruangan.getStatus());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println(new StringBuilder("Error menambah ruangan: ").append(e.getMessage()).toString());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Mengupdate data ruangan
     */
    public boolean updateRuangan(Ruangan ruangan) {
        String query = "UPDATE ruangan SET nama_ruangan=?, jumlah_kursi=?, ada_proyektor=?, kondisi_hdmi=?, status=? WHERE id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, ruangan.getNamaRuangan());
            pstmt.setInt(2, ruangan.getJumlahKursi());
            pstmt.setBoolean(3, ruangan.isAdaProyektor());
            pstmt.setString(4, ruangan.getKondisiHdmi());
            pstmt.setString(5, ruangan.getStatus());
            pstmt.setInt(6, ruangan.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println(new StringBuilder("Error update ruangan: ").append(e.getMessage()).toString());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Menghapus ruangan dari database
     */
    public boolean deleteRuangan(int id) {
        String query = "DELETE FROM ruangan WHERE id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println(new StringBuilder("Error hapus ruangan: ").append(e.getMessage()).toString());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Mengubah status ruangan
     */
    public boolean updateStatusRuangan(int id, String status) {
        String query = "UPDATE ruangan SET status=? WHERE id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, id);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println(new StringBuilder("Error update status ruangan: ").append(e.getMessage()).toString());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Mendapatkan ruangan berdasarkan ID
     */
    public Ruangan getRuanganById(int id) {
        String query = "SELECT * FROM ruangan WHERE id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Ruangan(
                    rs.getInt("id"),
                    rs.getString("nama_ruangan"),
                    rs.getInt("jumlah_kursi"),
                    rs.getBoolean("ada_proyektor"),
                    rs.getString("kondisi_hdmi"),
                    rs.getString("status")
                );
            }
        } catch (SQLException e) {
            System.err.println(new StringBuilder("Error mendapatkan ruangan: ").append(e.getMessage()).toString());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Mencari ruangan berdasarkan nama
     */
    public ObservableList<Ruangan> searchRuangan(String keyword) {
        ObservableList<Ruangan> ruanganList = FXCollections.observableArrayList();
        String query = "SELECT * FROM ruangan WHERE nama_ruangan LIKE ? ORDER BY id DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Ruangan ruangan = new Ruangan(
                    rs.getInt("id"),
                    rs.getString("nama_ruangan"),
                    rs.getInt("jumlah_kursi"),
                    rs.getBoolean("ada_proyektor"),
                    rs.getString("kondisi_hdmi"),
                    rs.getString("status")
                );
                ruanganList.add(ruangan);
            }
        } catch (SQLException e) {
            System.err.println(new StringBuilder("Error mencari ruangan: ").append(e.getMessage()).toString());
            e.printStackTrace();
        }
        
        return ruanganList;
    }
}