-- Database Schema untuk Sistem Inventaris dan Peminjaman Ruangan

CREATE DATABASE IF NOT EXISTS sistem_ruangan;
USE sistem_ruangan;

-- Tabel Admin
CREATE TABLE IF NOT EXISTS admin (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nama_lengkap VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabel Ruangan
CREATE TABLE IF NOT EXISTS ruangan (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nama_ruangan VARCHAR(100) NOT NULL UNIQUE,
    jumlah_kursi INT NOT NULL,
    ada_proyektor BOOLEAN DEFAULT FALSE,
    kondisi_hdmi ENUM('baik', 'rusak') DEFAULT 'baik',
    status ENUM('tersedia', 'dipinjam') DEFAULT 'tersedia',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Tabel Peminjaman
CREATE TABLE IF NOT EXISTS peminjaman (
    id INT PRIMARY KEY AUTO_INCREMENT,
    id_ruangan INT NOT NULL,
    nama_peminjam VARCHAR(100) NOT NULL,
    keperluan VARCHAR(255),
    tanggal_pinjam DATE NOT NULL,
    tanggal_kembali DATE NOT NULL,
    status_peminjaman ENUM('aktif', 'selesai', 'batal') DEFAULT 'aktif',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_ruangan) REFERENCES ruangan(id) ON DELETE CASCADE,
    INDEX idx_tanggal (tanggal_pinjam, tanggal_kembali),
    INDEX idx_status (status_peminjaman)
);

-- Insert data admin default (password: admin123)
INSERT INTO admin (username, password, nama_lengkap) VALUES 
('admin', 'admin123', 'Administrator Sistem');

-- Insert sample data ruangan
INSERT INTO ruangan (nama_ruangan, jumlah_kursi, ada_proyektor, kondisi_hdmi, status) VALUES 
('Ruang Meeting A', 20, TRUE, 'baik', 'tersedia'),
('Ruang Meeting B', 15, TRUE, 'baik', 'tersedia'),
('Ruang Seminar', 50, TRUE, 'baik', 'tersedia'),
('Ruang Rapat Kecil', 10, FALSE, 'rusak', 'tersedia'),
('Aula Utama', 100, TRUE, 'baik', 'tersedia');

-- Insert sample peminjaman
INSERT INTO peminjaman (id_ruangan, nama_peminjam, keperluan, tanggal_pinjam, tanggal_kembali, status_peminjaman) VALUES 
(1, 'Budi Santoso', 'Meeting Tim Marketing', '2025-10-23', '2025-10-23', 'aktif'),
(3, 'Siti Nurhaliza', 'Workshop Pelatihan', '2025-10-25', '2025-10-26', 'aktif');

-- Tabel User (untuk pengguna biasa)
CREATE TABLE IF NOT EXISTS user (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nama_lengkap VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    no_telepon VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Update tabel peminjaman untuk menambahkan relasi ke user
ALTER TABLE peminjaman 
ADD COLUMN id_user INT AFTER id,
ADD COLUMN status_approval ENUM('pending', 'approved', 'rejected') DEFAULT 'pending' AFTER status_peminjaman,
ADD FOREIGN KEY (id_user) REFERENCES user(id) ON DELETE SET NULL;

-- Insert sample users
INSERT INTO user (username, password, nama_lengkap, email, no_telepon) VALUES 
('user1', 'user123', 'Budi Santoso', 'budi@email.com', '081234567890'),
('user2', 'user123', 'Siti Nurhaliza', 'siti@email.com', '082345678901'),
('user3', 'user123', 'Ahmad Rizki', 'ahmad@email.com', '083456789012');

-- Create view untuk laporan peminjaman bulanan
CREATE OR REPLACE VIEW v_peminjaman_bulanan AS
SELECT 
    YEAR(p.tanggal_pinjam) as tahun,
    MONTH(p.tanggal_pinjam) as bulan,
    MONTHNAME(p.tanggal_pinjam) as nama_bulan,
    r.nama_ruangan,
    COUNT(*) as jumlah_peminjaman,
    SUM(CASE WHEN p.status_peminjaman = 'aktif' THEN 1 ELSE 0 END) as aktif,
    SUM(CASE WHEN p.status_peminjaman = 'selesai' THEN 1 ELSE 0 END) as selesai,
    SUM(CASE WHEN p.status_peminjaman = 'batal' THEN 1 ELSE 0 END) as batal
FROM peminjaman p
JOIN ruangan r ON p.id_ruangan = r.id
GROUP BY tahun, bulan, r.nama_ruangan
ORDER BY tahun DESC, bulan DESC;