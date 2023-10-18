package com.example.note;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.note.Model.Note;
import com.example.note.Model.SinhVien;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EditNoteActivity extends AppCompatActivity {

    private EditText editTextTitle;
    private EditText editTextContent;
    private TextView lastEdit;
    private String idSinhVienstr;

    private Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        // Set up the Toolbar
        Toolbar toolbarView = findViewById(R.id.toolbar);
        setSupportActionBar(toolbarView);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            // Cấu hình các thuộc tính khác của ActionBar (nếu cần)
        }


        editTextTitle = findViewById(R.id.addNoteTitle);
        editTextContent = findViewById(R.id.addNoteContent);
        lastEdit = findViewById(R.id.last_edit);
        lastEdit.setText(getCurrentTime());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            idSinhVienstr = extras.getString("idSinhVien");
            note = (Note) extras.getSerializable("note");
            editTextTitle.setText(note.getTieuDe());
            editTextContent.setText(note.getNoiDung());
            lastEdit.setText(getCurrentTime());
//            Toast.makeText(this, idSinhVienstr + note.toString(), Toast.LENGTH_SHORT).show();
        }

        // Handle the click event of the back button in the Toolbar
        toolbarView.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // Handle the click event of the FloatingActionButton
        findViewById(R.id.fab_saveNote).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNote();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.fab_saveNote) {
            saveNote();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveNote() {
        String title = editTextTitle.getText().toString();
        String content = editTextContent.getText().toString();
        String lastTimeEdited = getCurrentTime();

        Note updateNote = new Note(note.getId(), title, lastTimeEdited, lastTimeEdited, content, note.getNoiDungCua());

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", updateNote.getId());
        jsonObject.addProperty("tieuDe", updateNote.getTieuDe());
        jsonObject.addProperty("ngayCapNhat", lastEdit.getText().toString());
        jsonObject.addProperty("noiDung", updateNote.getNoiDung().toString());
        jsonObject.addProperty("noiDungCua", updateNote.getNoiDung().toString());

        // Chuyển đối tượng JSON thành chuỗi
        String json = jsonObject.toString();
        Log.e("TAG", "saveNote: " + json);
        Request request = new Request.Builder()
                .url("https://ttcs-test.000webhostapp.com/androidApi/updateNote.php")
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
                runOnUiThread(new Runnable() {
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
                            Toast.makeText(EditNoteActivity.this, message, Toast.LENGTH_SHORT).show();
                            Intent intent = getIntent();
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(EditNoteActivity.this, message, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
            }
        });
    }

    private String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}