package com.example.note.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.note.Model.SinhVien;
import com.example.note.R;

import java.util.List;

public class StudentAdapter extends ArrayAdapter<SinhVien> {
    private Activity activity;
    private int layout;
    private List<SinhVien> sinhVienList;

    public StudentAdapter(Activity activity, int layout, List<SinhVien> sinhVienList) {
        super(activity, layout, sinhVienList);
        this.activity = activity;
        this.layout = layout;
        this.sinhVienList = sinhVienList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();
        convertView = inflater.inflate(layout, null);

        TextView studentName = convertView.findViewById(R.id.studentName);
        TextView studentId = convertView.findViewById(R.id.studentId);

        studentName.setText(this.sinhVienList.get(position).getHoTen());
        studentId.setText(this.sinhVienList.get(position).getMaSinhVien());

        return convertView;
    }
}
