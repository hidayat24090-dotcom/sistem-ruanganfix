package com.sistemruangan.controller;

import com.sistemruangan.model.Peminjaman;
import com.sistemruangan.util.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;

/**
 * Controller untuk operasi CRUD Peminjaman (FIXED VERSION)
 */
public class PeminjamanController {
    private static final String SQL_SELECT_ALL = "SELECT p.*, r.nama_ruangan FROM peminjaman p JOIN ruangan r ON p.id_ruangan = r.id ORDER BY p.id DESC";
    private static final String SQL_INSERT = "INSERT INTO peminjaman (id_ruangan, nama_peminjam, keperluan, tanggal_pinjam, tanggal_kembali, status_peminjaman) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE = "UPDATE peminjaman SET id_ruangan=?, nama_peminjam=?, keperluan=?, tanggal_pinjam=?, tanggal_kembali=?, status_peminjaman=? WHERE id=?";
    private static final String SQL_SEARCH = "SELECT p.*, r.nama_ruangan FROM peminjaman p JOIN ruangan r ON p.id_ruangan = r.id WHERE p.nama_peminjam LIKE ? OR r.nama_ruangan LIKE ? ORDER BY p.id DESC";

    private RuanganController ruanganController = new RuanganController();
    
    /**
     * Mengambil semua data peminjaman dari database dengan join ke tabel ruangan
     */
    public ObservableList<Peminjaman> getAllPeminjaman() {
        ObservableList<Peminjaman> peminjamanList = FXCollections.observableArrayList();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL)) {
            
            while (rs.next()) {
                Peminjaman peminjaman = createPeminjamanFromResultSet(rs);
                if (peminjaman != null) {
                    peminjamanList.add(peminjaman);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil data peminjaman: " + e.getMessage());
            e.printStackTrace();
        }
        
        return peminjamanList;
    }
    
    /**
     * Menambah peminjaman baru dan mengubah status ruangan (FIXED)
     */
    public boolean tambahPeminjaman(Peminjaman peminjaman) {
        if (!validatePeminjaman(peminjaman)) {
            System.err.println("Data peminjaman tidak valid");
            return false;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            
            // Cek apakah ruangan masih tersedia
            String checkQuery = "SELECT status FROM ruangan WHERE id = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setInt(1, peminjaman.getIdRuangan());
                ResultSet rs = checkStmt.executeQuery();
                
                if (rs.next()) {
                    String currentStatus = rs.getString("status");
                    if (!"tersedia".equalsIgnoreCase(currentStatus)) {
                        System.err.println("Ruangan tidak tersedia untuk dipinjam");
                        return false;
                    }
                } else {
                    System.err.println("Ruangan tidak ditemukan");
                    return false;
                }
            }
            
            // Mulai transaksi
            conn.setAutoCommit(false);
            
            // Insert peminjaman
            pstmt = conn.prepareStatement(SQL_INSERT);
            setPeminjamanParameters(pstmt, peminjaman);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Update status ruangan
                String updateQuery = "UPDATE ruangan SET status = 'dipinjam' WHERE id = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                    updateStmt.setInt(1, peminjaman.getIdRuangan());
                    int updateRows = updateStmt.executeUpdate();
                    
                    if (updateRows > 0) {
                        conn.commit();
                        System.out.println("Peminjaman berhasil ditambahkan");
                        return true;
                    }
                }
            }
            
            conn.rollback();
            return false;
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("Error menambah peminjaman: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Update data peminjaman
     */
    public boolean updatePeminjaman(Peminjaman peminjaman) {
        if (!validatePeminjaman(peminjaman)) {
            System.err.println("Data peminjaman tidak valid");
            return false;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_UPDATE)) {
            
            setPeminjamanParameters(pstmt, peminjaman);
            pstmt.setInt(7, peminjaman.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error update peminjaman: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Menyelesaikan peminjaman dan mengubah status ruangan kembali tersedia
     */
    public boolean selesaikanPeminjaman(int idPeminjaman, int idRuangan) {
        return updateStatusPeminjaman(idPeminjaman, idRuangan, "selesai");
    }
    
    /**
     * Membatalkan peminjaman dan mengubah status ruangan kembali tersedia
     */
    public boolean batalkanPeminjaman(int idPeminjaman, int idRuangan) {
        return updateStatusPeminjaman(idPeminjaman, idRuangan, "batal");
    }
    
    /**
     * Update status peminjaman (helper method)
     */
    private boolean updateStatusPeminjaman(int idPeminjaman, int idRuangan, String newStatus) {
        if (idPeminjaman <= 0 || idRuangan <= 0) {
            System.err.println("ID tidak valid");
            return false;
        }

        Connection conn = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Update status peminjaman
            String updatePeminjamanQuery = "UPDATE peminjaman SET status_peminjaman=? WHERE id=?";
            try (PreparedStatement pstmt = conn.prepareStatement(updatePeminjamanQuery)) {
                pstmt.setString(1, newStatus);
                pstmt.setInt(2, idPeminjaman);
                
                int rowsAffected = pstmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    // Update status ruangan
                    String updateRuanganQuery = "UPDATE ruangan SET status='tersedia' WHERE id=?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateRuanganQuery)) {
                        updateStmt.setInt(1, idRuangan);
                        int updateRows = updateStmt.executeUpdate();
                        
                        if (updateRows > 0) {
                            conn.commit();
                            return true;
                        }
                    }
                }
            }
            
            conn.rollback();
            return false;
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("Error mengubah status peminjaman: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Menghapus peminjaman
     */
    public boolean deletePeminjaman(int id) {
        if (id <= 0) {
            System.err.println("ID peminjaman tidak valid");
            return false;
        }

        String query = "DELETE FROM peminjaman WHERE id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error hapus peminjaman: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Mencari peminjaman berdasarkan nama peminjam atau nama ruangan
     */
    public ObservableList<Peminjaman> searchPeminjaman(String keyword) {
        ObservableList<Peminjaman> peminjamanList = FXCollections.observableArrayList();
        
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllPeminjaman();
        }
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_SEARCH)) {
            
            String searchKeyword = "%" + keyword.trim() + "%";
            pstmt.setString(1, searchKeyword);
            pstmt.setString(2, searchKeyword);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Peminjaman peminjaman = createPeminjamanFromResultSet(rs);
                    if (peminjaman != null) {
                        peminjamanList.add(peminjaman);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error mencari peminjaman: " + e.getMessage());
            e.printStackTrace();
        }
        
        return peminjamanList;
    }
    
    /**
     * Mendapatkan peminjaman berdasarkan bulan dan tahun
     */
    public ObservableList<Peminjaman> getPeminjamanByMonth(int month, int year) {
        ObservableList<Peminjaman> peminjamanList = FXCollections.observableArrayList();
        String query = "SELECT p.*, r.nama_ruangan FROM peminjaman p " +
                      "JOIN ruangan r ON p.id_ruangan = r.id " +
                      "WHERE MONTH(p.tanggal_pinjam) = ? AND YEAR(p.tanggal_pinjam) = ? " +
                      "ORDER BY p.tanggal_pinjam ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, month);
            pstmt.setInt(2, year);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Peminjaman peminjaman = createPeminjamanFromResultSet(rs);
                    if (peminjaman != null) {
                        peminjamanList.add(peminjaman);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil peminjaman bulanan: " + e.getMessage());
            e.printStackTrace();
        }
        
        return peminjamanList;
    }

    /**
     * Membuat objek Peminjaman dari ResultSet
     */
    private Peminjaman createPeminjamanFromResultSet(ResultSet rs) throws SQLException {
        return new Peminjaman(
            rs.getInt("id"),
            rs.getInt("id_ruangan"),
            rs.getString("nama_ruangan"),
            rs.getString("nama_peminjam"),
            rs.getString("keperluan"),
            rs.getDate("tanggal_pinjam").toLocalDate(),
            rs.getDate("tanggal_kembali").toLocalDate(),
            rs.getString("status_peminjaman")
        );
    }

    /**
     * Set parameter PreparedStatement untuk Peminjaman
     */
    private void setPeminjamanParameters(PreparedStatement pstmt, Peminjaman peminjaman) throws SQLException {
        pstmt.setInt(1, peminjaman.getIdRuangan());
        pstmt.setString(2, peminjaman.getNamaPeminjam());
        pstmt.setString(3, peminjaman.getKeperluan());
        pstmt.setDate(4, Date.valueOf(peminjaman.getTanggalPinjam()));
        pstmt.setDate(5, Date.valueOf(peminjaman.getTanggalKembali()));
        pstmt.setString(6, peminjaman.getStatusPeminjaman());
    }

    /**
     * Validasi data Peminjaman
     */
    private boolean validatePeminjaman(Peminjaman peminjaman) {
        if (peminjaman == null) {
            return false;
        }

        if (peminjaman.getIdRuangan() <= 0 || 
            peminjaman.getNamaPeminjam() == null || peminjaman.getNamaPeminjam().trim().isEmpty() ||
            peminjaman.getKeperluan() == null || peminjaman.getKeperluan().trim().isEmpty() ||
            peminjaman.getTanggalPinjam() == null || peminjaman.getTanggalKembali() == null ||
            peminjaman.getStatusPeminjaman() == null || peminjaman.getStatusPeminjaman().trim().isEmpty()) {
            return false;
        }

        // Validasi tanggal pinjam harus sebelum atau sama dengan tanggal kembali
        return !peminjaman.getTanggalPinjam().isAfter(peminjaman.getTanggalKembali());
    }
}