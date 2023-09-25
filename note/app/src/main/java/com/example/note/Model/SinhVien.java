package com.example.note.Model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SinhVien {
    private int id;
    private String hoTen;
    private Date ngaySinh;
    private String gioiTinh;
    private String queQuan;
    private String gmail;
    private String sdt;
    private int khoa;
    private int nienKhoa;
    private int lop;

    public SinhVien(int id, String hoTen, Date ngaySinh, String gioiTinh, String queQuan, String gmail, String sdt, int khoa, int nienKhoa, int lop) {
        this.id = id;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.queQuan = queQuan;
        this.gmail = gmail;
        this.sdt = sdt;
        this.khoa = khoa;
        this.nienKhoa = nienKhoa;
        this.lop = lop;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public Date getNgaySinh() {
        return ngaySinh;
    }

    public String getNgaySinhStr() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(this.ngaySinh);
    }

    public void setNgaySinh(Date ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public boolean getGioiTinhBoolean() {
        return this.getGioiTinh().equals("Nam");
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public String getQueQuan() {
        return queQuan;
    }

    public void setQueQuan(String queQuan) {
        this.queQuan = queQuan;
    }

    public String getGmail() {
        return gmail;
    }

    public void setGmail(String gmail) {
        this.gmail = gmail;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public int getKhoa() {
        return khoa;
    }

    public void setKhoa(int khoa) {
        this.khoa = khoa;
    }

    public String getKhoaStr() {
        switch (this.khoa) {
            case 1: return "CNTT";
            case 2: return "ATTT";
            case 3: return "DTVT";
        }
        return "";
    }

    public int getNienKhoa() {
        return nienKhoa;
    }

    public String getNienKhoaStr() {
        int start = this.nienKhoa + 2015;
        int end = this.nienKhoa + 2015 + 5;

        return "" + start + " - " + end;
    }

    public void setNienKhoa(int nienKhoa) {
        this.nienKhoa = nienKhoa;
    }

    public int getLop() {
        return lop;
    }

    public String getLopStr() {
        String lopStr = "";
        String khoaStr = "";
        if(this.lop == 1) lopStr = "A";
        if(this.lop == 2) lopStr = "B";
        if(this.lop == 3) lopStr = "C";
        if(this.lop == 4) lopStr = "D";

        if(this.khoa == 1) khoaStr = "CT";
        if(this.khoa == 2) khoaStr = "AT";
        if(this.khoa == 3) khoaStr = "DT";

        return "" + khoaStr + this.getNienKhoa() + lopStr;
    }

    public void setLop(int lop) {
        this.lop = lop;
    }

    public String getMaSinhVien() {
        String khoa1 = "";
        if(this.khoa == 1) khoa1 = "CT";
        else if(this.khoa == 2) khoa1 = "AT";
        else if(this.khoa == 3) khoa1 = "DT";
        return khoa1 + "0" + String.valueOf(this.nienKhoa) + "0" + String.valueOf(this.lop) + "0" + this.id;
    }

    public static int getIdFromMaSinhVien(String maSinhVien) {
        String id = "";
        for (int i = 6; i < maSinhVien.length(); i++) {
            id = id + maSinhVien.charAt(i);
        }
        return Integer.parseInt(id);
    }

    public static int getKhoaFromMaSinhVien(String maSinhVien) {
        String khoa = "";
        for (int i = 0; i < 2; i++) {
            khoa = khoa + maSinhVien.charAt(i);
        }
        khoa = khoa.toUpperCase();
        if((khoa.charAt(0) == 'c' || khoa.charAt(0) == 'C') && (khoa.charAt(1) == 't' || khoa.charAt(1) == 'T')) return 1;
        if((khoa.charAt(0) == 'a' || khoa.charAt(0) == 'A') && (khoa.charAt(1) == 't' || khoa.charAt(1) == 'T')) return 2;
        if((khoa.charAt(0) == 'd' || khoa.charAt(0) == 'D') && (khoa.charAt(1) == 't' || khoa.charAt(1) == 'T')) return 3;
        return -1;
    }
    public static int getNienKhoaFromMaSinhVien(String maSinhVien) {
        String nienKhoa = "";
        for (int i = 2; i < 4; i++) {
            nienKhoa = nienKhoa + maSinhVien.charAt(i);
        }

        return Integer.parseInt(nienKhoa);
    }
    public static int getLopFromMaSinhVien(String maSinhVien) {
        String lop = "";
        for (int i = 4; i < 6; i++) {
            lop = lop + maSinhVien.charAt(i);
        }

        return Integer.parseInt(lop);
    }
}
