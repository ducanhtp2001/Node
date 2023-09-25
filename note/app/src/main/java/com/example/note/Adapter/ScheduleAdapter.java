package com.example.note.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.note.Model.Schedule;
import com.example.note.R;

import java.util.ArrayList;
import java.util.List;

public class ScheduleAdapter extends ArrayAdapter<Schedule> {
    private Activity myActivity;
    private int layoutID;
    private List<Schedule> scheduleList;


    public ScheduleAdapter(Activity myActivity, int layoutID, List<Schedule> scheduleList) {
        super(myActivity, layoutID, scheduleList);
        this.myActivity = myActivity;
        this.layoutID = layoutID;
        if(scheduleList != null) this.scheduleList = scheduleList;
        else this.scheduleList = new ArrayList<>();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = myActivity.getLayoutInflater();
        convertView = inflater.inflate(layoutID, null);
        TextView tv_tenMon = convertView.findViewById(R.id.tv_sche_tenMonHoc);
        TextView tv_soTinChi = convertView.findViewById(R.id.tv_sche_soTinChi);
        TextView tv_maMon = convertView.findViewById(R.id.tv_sche_maMonHoc);
        TextView tv_lopTinChi = convertView.findViewById(R.id.tv_sche_lopTinChi);
        TextView tv_phongHoc = convertView.findViewById(R.id.tv_sche_phongHoc);
        TextView tv_caHoc = convertView.findViewById(R.id.tv_sche_caHoc);
        TextView tv_ngayHoc = convertView.findViewById(R.id.tv_sche_ngayHoc);
        TextView tv_thoiGian = convertView.findViewById(R.id.tv_sche_thoiGianHoc);

        Schedule schedule = this.scheduleList.get(position);

        tv_tenMon.setText(schedule.getTenMon());
        tv_soTinChi.setText("Số tín chỉ: " + schedule.getSoTinChi());
        tv_maMon.setText("" + schedule.getMaMon());
        tv_lopTinChi.setText("L0" + schedule.getLopTinChi());
        tv_phongHoc.setText(schedule.getPhongHoc());
        tv_caHoc.setText("Ca " + schedule.getCaHoc());
        tv_ngayHoc.setText(schedule.getNgayHocToString());
        tv_thoiGian.setText(schedule.getThoiGianHoc());

        return convertView;
    }
}
