package com.sistemruangan.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Kelas DatabaseConnection untuk mengelola koneksi ke database MySQL
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3307/sistem_ruangan";
    private static final String USER = "root";
    private static final String PASSWORD = "Jackpot2005JMK58Gen1DataBase2408561!+@_#)"; // Sesuaikan dengan password MySQL Anda
    
    private static Connection connection = null;
    
    /**
     * Mendapatkan koneksi database (Singleton Pattern)
     * @return Connection object
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Koneksi database berhasil!");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver tidak ditemukan!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Gagal membuat koneksi database!");
            e.printStackTrace();
        }
        return connection;
    }
    
    /**
     * Menutup koneksi database
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Koneksi database ditutup.");
            }
        } catch (SQLException e) {
            System.err.println("Gagal menutup koneksi database!");
            e.printStackTrace();
        }
    }
    
    /**
     * Test koneksi database
     * @return true jika koneksi berhasil
     */
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}