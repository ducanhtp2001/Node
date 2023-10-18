package com.example.note.ui.trash;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.note.Model.NoteInTrash;
import com.example.note.Model.SinhVien;
import com.example.note.SQLite.Connect;
import com.example.note.SQLite.ConnectSharing;

import java.util.ArrayList;
import java.util.List;

public class TrashFragmentViewModel extends ViewModel {
    private List<NoteInTrash> noteList;
    private String idSinhVien;
    private MutableLiveData<List<NoteInTrash>> noteListLiveData;

    private Connect connect;
    private Context context;

    public TrashFragmentViewModel() {
        this.noteList = new ArrayList<>();
        this.noteListLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<List<NoteInTrash>> getNoteListLiveData() {
        return noteListLiveData;
    }

    public void setNoteListLiveData() {
        loadDataFromSQLite();
    }




    private void loadDataFromSQLite() {
        noteList.clear();
        String query = "SELECT * FROM note WHERE idsinhvien = " + SinhVien.getIdFromMaSinhVien(this.idSinhVien);
        Cursor data = connect.returnQuery(query);

        while(data.moveToNext()) {
            String ngayTao = data.getString(3);
            String ngaycapnhat = data.getString(4);
            this.noteList.add(new NoteInTrash(data.getInt(0), data.getInt(1),
                    data.getString(2), data.getString(3),
                    data.getString(4), data.getString(5), data.getString(6)));
        }

        if(this.noteList != null) noteListLiveData.setValue(noteList);
        else Log.e("TAG", "noteList is null");
    }

    public void deleteNote(int id) {
        connect.nonReturnQuery("DELETE FROM note WHERE id = " + id);
        setNoteListLiveData();
    }


    public void setIdSinhVien(String idSinhVien) {
        this.idSinhVien = idSinhVien;
    }

    public void setContext(Context context) {
        this.context = context;
        connect = ConnectSharing.getConnectSharing(context);
    }
}
