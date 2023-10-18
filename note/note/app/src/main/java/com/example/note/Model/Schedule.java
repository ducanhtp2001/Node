package com.example.note.Model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Schedule {
    private int maMon;
    private String tenMon;
    private int soTinChi;
    private int lopTinChi;
    private Date ngayHoc;
    private int caHoc;
    private String phongHoc;

    public Schedule(int maMon, String tenMon, int soTinChi, int lopTinChi, String ngayHoc, int caHoc, String phongHoc) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        this.maMon = maMon;
        this.tenMon = tenMon;
        this.soTinChi = soTinChi;
        this.lopTinChi = lopTinChi;
        try {
            this.ngayHoc = dateFormat.parse(ngayHoc);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        this.caHoc = caHoc;
        this.phongHoc = phongHoc;
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

    public int getSoTinChi() {
        return soTinChi;
    }

    public void setSoTinChi(int soTinChi) {
        this.soTinChi = soTinChi;
    }

    public int getLopTinChi() {
        return lopTinChi;
    }

    public void setLopTinChi(int lopTinChi) {
        this.lopTinChi = lopTinChi;
    }

    public Date getNgayHoc() {
        return ngayHoc;
    }

    public void setNgayHoc(Date ngayHoc) {
        this.ngayHoc = ngayHoc;
    }

    public String getNgayHocToString() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E dd-MM-yyyy");
        String ngayHocString = simpleDateFormat.format(ngayHoc);

        return ngayHocString;
    }

    public int getCaHoc() {
        return caHoc;
    }

    public void setCaHoc(int caHoc) {
        this.caHoc = caHoc;
    }

    public String getPhongHoc() {
        return phongHoc;
    }

    public void setPhongHoc(String phongHoc) {
        this.phongHoc = phongHoc;
    }

    public String getThoiGianHoc() {
        switch (this.caHoc) {
            case 1: {
                return "7h - 9h25";
            }
            case 2: {
                return "9h35 - 12h AM";
            }
            case 3: {
                return "12h30 - 14h55";
            }
            case 4: {
                return "15h05 - 17h30";
            }
            case 5: {
                return "18h - 21h15 ";
            }

        }
        return "";
    }
}
