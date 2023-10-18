package com.example.note.Model;

public class TaiKhoan {
    private int idSinhVien;
    private String taiKhoan;
    private String matKhau;

    public TaiKhoan(int idSinhVien, String taiKhoan, String matKhau) {
        this.idSinhVien = idSinhVien;
        this.taiKhoan = taiKhoan;
        this.matKhau = matKhau;
    }

    public int getIdSinhVien() {
        return idSinhVien;
    }

    public void setIdSinhVien(int idSinhVien) {
        this.idSinhVien = idSinhVien;
    }

    public String getTaiKhoan() {
        return taiKhoan;
    }

    public void setTaiKhoan(String taiKhoan) {
        this.taiKhoan = taiKhoan;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }
}
