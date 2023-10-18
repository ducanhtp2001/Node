package com.example.note.Model;

import java.io.Serializable;
import java.util.List;

public class Course implements Serializable {
    private int maMon;
    private String tenMon;
    private int lopTinChi;
    private int soTinChi;
    private String tenGiaoVien;

    public Course(int maMon, String tenMon, int lopTinChi, int soTinChi, String tenGiaoVien) {
        this.maMon = maMon;
        this.tenMon = tenMon;
        this.lopTinChi = lopTinChi;
        this.soTinChi = soTinChi;
        this.tenGiaoVien = tenGiaoVien;
    }

    public int getMaMon() {
        return maMon;
    }

    public void setMaMon(int maMon) {
        this.maMon = maMon;
    }

    public String getTenMon() {
        return tenMon;
    }

    public void setTenMon(String tenMon) {
        this.tenMon = tenMon;
    }

    public String getTenLop() {
        return tenMon + " L0" + this.getLopTinChi();
    }
    public int getLopTinChi() {
        return lopTinChi;
    }

    public void setLopTinChi(int lopTinChi) {
        this.lopTinChi = lopTinChi;
    }

    public int getSoTinChi() {
        return soTinChi;
    }

    public void setSoTinChi(int soTinChi) {
        this.soTinChi = soTinChi;
    }

    public String getTenGiaoVien() {
        return tenGiaoVien;
    }

    public void setTenGiaoVien(String tenGiaoVien) {
        this.tenGiaoVien = tenGiaoVien;
    }
}
