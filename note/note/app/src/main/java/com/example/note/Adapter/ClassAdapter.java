package com.example.note.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.note.Model.Course;
import com.example.note.R;

import java.util.List;

public class ClassAdapter extends ArrayAdapter<Course> {
    private Activity activity;
    private int layout;
    private List<Course> courseList;

    public ClassAdapter(Activity activity, int layout, List<Course> courseList) {
        super(activity, layout, courseList);
        this.activity = activity;
        this.layout = layout;
        this.courseList = courseList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();
        convertView = inflater.inflate(layout, null);

        TextView className = convertView.findViewById(R.id.className);
        className.setText(this.courseList.get(position).getTenMon() + " L0" + this.courseList.get(position).getLopTinChi());

        return convertView;
    }
}
