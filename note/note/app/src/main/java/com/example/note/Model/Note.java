    package com.example.note.Model;

    import java.io.Serializable;
    import java.text.ParseException;
    import java.text.SimpleDateFormat;
    import java.util.Date;

    public class Note implements Serializable {
        private int id;
        private String tieuDe;
        private Date ngayTao;
        private Date ngayCapNhat;
        private String noiDung;
        private String noiDungCua;

        public Note(int id, String tieuDe, String ngayTao, String ngayCapNhat, String noiDung, String noiDungCua) {
            this.id = id;
            this.tieuDe = tieuDe;
            this.ngayTao = convertStringToDate(ngayTao);
            if(this.ngayTao == null) this.ngayTao = convertStringToDateVer2(ngayTao);
            this.ngayCapNhat = convertStringToDate(ngayCapNhat);
            if(this.ngayCapNhat == null) this.ngayCapNhat = convertStringToDateVer2(ngayTao);
            this.noiDung = noiDung;
            this.noiDungCua = noiDungCua;
        }

        private Date convertStringToDate(String dateString) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            try {
                return dateFormat.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
                return null; // Trả về null hoặc xử lý lỗi theo ý của bạn
            }
        }

        private Date convertStringToDateVer2(String dateString) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

            try {
                return dateFormat.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
                return null; // Trả về null hoặc xử lý lỗi theo ý của bạn
            }
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTieuDe() {
            return tieuDe;
        }

        public void setTieuDe(String tieuDe) {
            this.tieuDe = tieuDe;
        }

        public Date getNgayTao() {
            return ngayTao;
        }

        public static String getNgayStr(Date date) {
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy/MM/dd");

            try {
                String dateStr = outputFormat.format(date);
                return dateStr;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null; // Xử lý lỗi hoặc trả về giá trị mặc định theo ý bạn
        }


        public void setNgayTao(Date ngayTao) {
            this.ngayTao = ngayTao;
        }

        public Date getNgayCapNhat() {
            return ngayCapNhat;
        }

        public void setNgayCapNhat(Date ngayCapNhat) {
            this.ngayCapNhat = ngayCapNhat;
        }

        public String getNoiDung() {
            return noiDung;
        }

        public void setNoiDung(String noiDung) {
            this.noiDung = noiDung;
        }

        public String getNoiDungCua() {
            return noiDungCua;
        }

        public void setNoiDungCua(String noiDungCua) {
            this.noiDungCua = noiDungCua;
        }
    }
