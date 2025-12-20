package com.sistemruangan.controller;

import com.sistemruangan.model.Ruangan;
import com.sistemruangan.util.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

/**
 * Controller untuk operasi CRUD Ruangan - WITH PHOTO SUPPORT
 */
public class RuanganController {
    
    /**
     * Mengambil semua data ruangan dari database (dengan foto)
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
                    rs.getString("fasilitas"),
                    rs.getString("status"),
                    rs.getString("foto_path") // NEW: foto path
                );
                ruanganList.add(ruangan);
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil data ruangan: " + e.getMessage());
            e.printStackTrace();
        }
        
        return ruanganList;
    }
    
    /**
     * Menambah ruangan baru ke database (dengan foto)
     */
    public boolean tambahRuangan(Ruangan ruangan) {
        String query = "INSERT INTO ruangan (nama_ruangan, jumlah_kursi, fasilitas, status, foto_path) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, ruangan.getNamaRuangan());
            pstmt.setInt(2, ruangan.getJumlahKursi());
            pstmt.setString(3, ruangan.getFasilitas());
            pstmt.setString(4, ruangan.getStatus());
            pstmt.setString(5, ruangan.getFotoPath());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error menambah ruangan: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Mengupdate data ruangan (dengan foto)
     */
    public boolean updateRuangan(Ruangan ruangan) {
        String query = "UPDATE ruangan SET nama_ruangan=?, jumlah_kursi=?, fasilitas=?, status=?, foto_path=? WHERE id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, ruangan.getNamaRuangan());
            pstmt.setInt(2, ruangan.getJumlahKursi());
            pstmt.setString(3, ruangan.getFasilitas());
            pstmt.setString(4, ruangan.getStatus());
            pstmt.setString(5, ruangan.getFotoPath());
            pstmt.setInt(6, ruangan.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error update ruangan: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update foto ruangan saja
     */
    public boolean updateFotoRuangan(int idRuangan, String fotoPath) {
        String query = "UPDATE ruangan SET foto_path=? WHERE id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, fotoPath);
            pstmt.setInt(2, idRuangan);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error update foto ruangan: " + e.getMessage());
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
            System.err.println("Error hapus ruangan: " + e.getMessage());
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
            System.err.println("Error update status ruangan: " + e.getMessage());
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
                    rs.getString("fasilitas"),
                    rs.getString("status"),
                    rs.getString("foto_path")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error mendapatkan ruangan: " + e.getMessage());
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
                    rs.getString("fasilitas"),
                    rs.getString("status"),
                    rs.getString("foto_path")
                );
                ruanganList.add(ruangan);
            }
        } catch (SQLException e) {
            System.err.println("Error mencari ruangan: " + e.getMessage());
            e.printStackTrace();
        }
        
        return ruanganList;
    }
}