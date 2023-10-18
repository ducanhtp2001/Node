package com.example.note.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.note.Model.Note;
import com.example.note.Model.NoteInTrash;
import com.example.note.R;

import java.util.ArrayList;
import java.util.List;

public class NoteInTrashAdapter extends ArrayAdapter<NoteInTrash> {

    Context context;
    int layout;
    List<NoteInTrash> noteList;

    public NoteInTrashAdapter(Context context, int layout, List<NoteInTrash> noteList) {
        super(context, layout, noteList);
        this.context = context;
        this.layout = layout;
        if(noteList != null) {
            this.noteList = noteList;
        } else {
            noteList = new ArrayList<>();
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(this.context.getApplicationContext());
        convertView = inflater.inflate(layout, null);

        TextView tv_tieuDe = convertView.findViewById(R.id.note_title);
        TextView tv_capNhatCuoi = convertView.findViewById(R.id.edit_time);
        TextView tv_thuocVe = convertView.findViewById(R.id.note_belong);

        tv_tieuDe.setText(this.noteList.get(position).getTieuDe());
        tv_capNhatCuoi.setText(Note.getNgayStr(this.noteList.get(position).getNgayCapNhat()));
        tv_thuocVe.setText(this.noteList.get(position).getNoiDungCua());

        return convertView;
    }
}
