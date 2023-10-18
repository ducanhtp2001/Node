package com.example.note;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.note.ApiService.ApiService;
import com.example.note.Model.Note;
import com.example.note.Model.SinhVien;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddNoteActivity extends AppCompatActivity {

    private EditText editTextTitle;
    private EditText editTextContent;
    private TextView lastEdit;
    private String idSinhVienstr;
    private String belong;

    private Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        // Set up the Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editTextTitle = findViewById(R.id.addNoteTitle);
        editTextContent = findViewById(R.id.addNoteContent);
        lastEdit = findViewById(R.id.last_edit);
        lastEdit.setText(getCurrentTime());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            idSinhVienstr = extras.getString("idSinhVien");
            belong = extras.getString("belong");
//            Toast.makeText(this, idSinhVienstr, Toast.LENGTH_SHORT).show();
        }

        // Handle the click event of the back button in the Toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
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
        updateBelongValue();
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

        note = new Note(SinhVien.getIdFromMaSinhVien(idSinhVienstr), title, lastTimeEdited, lastTimeEdited, content, belong);

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", note.getId());
        jsonObject.addProperty("tieuDe", note.getTieuDe());
        jsonObject.addProperty("ngayTao", Note.getNgayStr(note.getNgayTao()));
        jsonObject.addProperty("ngayCapNhat", Note.getNgayStr(note.getNgayCapNhat()));
        jsonObject.addProperty("noiDung", note.getNoiDung().toString());
        jsonObject.addProperty("noiDungCua", belong);

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
                            Toast.makeText(AddNoteActivity.this, message, Toast.LENGTH_SHORT).show();
                            Intent intent = getIntent();
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(AddNoteActivity.this, message, Toast.LENGTH_SHORT).show();
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
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateBelongValue();
    }

    private void updateBelongValue() {
        Resources resources = getResources();
        belong = resources.getString(R.string.note_belong);
    }

}