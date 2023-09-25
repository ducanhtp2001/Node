package com.example.note.ui.trash;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.note.Adapter.NoteAdapter;
import com.example.note.Adapter.NoteInTrashAdapter;
import com.example.note.AddNoteActivity;
import com.example.note.Model.Note;
import com.example.note.Model.NoteInTrash;
import com.example.note.Model.SinhVien;
import com.example.note.R;
import com.example.note.SQLite.Connect;
import com.example.note.SQLite.ConnectSharing;
import com.example.note.databinding.FragmentTrashBinding;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TrashFragment extends Fragment {

    private FragmentTrashBinding binding;
    private Connect connect;
    private ListView listViewTrash;
    private List<NoteInTrash> noteList;
    private NoteInTrashAdapter noteAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentTrashBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        listViewTrash = root.findViewById(R.id.listViewTrash);

        connect = ConnectSharing.getConnectSharing(getActivity());
        noteList = new ArrayList<>();

        loadDataFromSQLite();

        if(noteList.isEmpty()) {
            Toast.makeText(getActivity(), "Empty", Toast.LENGTH_SHORT).show();
        } else {
            noteAdapter = new NoteInTrashAdapter(getActivity(), R.layout.note_layout, noteList);
            listViewTrash.setAdapter(noteAdapter);
        }

        listViewTrash.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                NoteInTrash selectedNote = noteList.get(i);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(selectedNote.getTieuDe());
                String message = "Ngày tạo: " + Note.getNgayStr(selectedNote.getNgayTao()) +
                        "\n Cập nhật lần cuối: " + Note.getNgayStr(selectedNote.getNgayCapNhat()) +
                        "\n Nội dung: " + selectedNote.getNoiDung();
                builder.setMessage(message);
                builder.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        registerForContextMenu(listViewTrash);

        return root;
    }

    private void loadDataFromSQLite() {
        noteList.clear();
        String query = "SELECT * FROM note";
        Cursor data = connect.returnQuery(query);

        while(data.moveToNext()) {
            String ngayTao = data.getString(3);
            String ngaycapnhat = data.getString(4);
            noteList.add(new NoteInTrash(data.getInt(0), data.getInt(1),
                    data.getString(2), data.getString(3),
                    data.getString(4), data.getString(5)));
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        NoteInTrash selectedNote = noteAdapter.getItem(info.position);

        if (selectedNote != null) {
            getActivity().getMenuInflater().inflate(R.menu.note_trash_menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        NoteInTrash selectedNote = noteAdapter.getItem(position);

        if (selectedNote != null) {
            int itemId = item.getItemId();
            if (itemId == R.id.action_deleteForever) {
                connect.nonReturnQuery("DELETE FROM note WHERE id = " + selectedNote.getId());
                loadDataFromSQLite();
                noteAdapter.notifyDataSetChanged();
                return true;
            } else if (itemId == R.id.action_restore) {
                // Handle the "Delete" action
                callApi(selectedNote);
                return true;
            }
        }

        return super.onContextItemSelected(item);
    }

    private void callApi(NoteInTrash selectedNote) {


        Note note = new Note(selectedNote.getIdSinhVien(), selectedNote.getTieuDe(),
                Note.getNgayStr(selectedNote.getNgayTao()), Note.getNgayStr(selectedNote.getNgayCapNhat()),
                selectedNote.getNoiDung());

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", note.getId());
        jsonObject.addProperty("tieuDe", note.getTieuDe());
        jsonObject.addProperty("ngayTao", Note.getNgayStr(note.getNgayTao()));
        jsonObject.addProperty("ngayCapNhat", Note.getNgayStr(note.getNgayCapNhat()));
        jsonObject.addProperty("noiDung", note.getNoiDung().toString());

        // Chuyển đối tượng JSON thành chuỗi
        String json = jsonObject.toString();
        Log.e("TAG", "saveNote: " + json);
        Request request = new Request.Builder()
                .url("https://ttcs-test.000webhostapp.com/androidApi/insertNote.php")
                .post(RequestBody.create(mediaType, json))
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Error", "Error");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        JsonElement jsonElement = gson.fromJson(json, JsonElement.class);
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        String message = "";
                        boolean status = false;
                        if (jsonObject.has("message")) {
                            message = jsonObject.get("message").getAsString();

                        }
                        if (jsonObject.has("status")) {
                            status = jsonObject.get("status").getAsBoolean();
                        }
//                                    Toast.makeText(RegisterActivity.this, tenSinhVien + status, Toast.LENGTH_SHORT).show();
                        if(status) {
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            connect.nonReturnQuery("DELETE FROM note WHERE id = " + selectedNote.getId());
                            loadDataFromSQLite();
                            noteAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}