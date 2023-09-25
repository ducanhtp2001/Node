package com.example.note;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.note.Adapter.ScheduleAdapter;
import com.example.note.Adapter.StudentAdapter;
import com.example.note.Model.Course;
import com.example.note.Model.Schedule;
import com.example.note.Model.SinhVien;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
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

public class ShowClassActivity extends AppCompatActivity {

    private String idSinhVienStr;
    private Course course;
    private TextView classNameValues;
    private TextView teacherValues;
    private TextView totalCreditValues;
    private ListView studentListView;
    private List<SinhVien> studentList;
    private StudentAdapter studentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.classmate_layout);


        idSinhVienStr = (String) getIntent().getExtras().get("idSinhVienStr");
        course = (Course) getIntent().getExtras().get("course");
        classNameValues = findViewById(R.id.classNameValues);
        teacherValues = findViewById(R.id.teacherValues);
        totalCreditValues = findViewById(R.id.totalCreditValues);
        studentListView = findViewById(R.id.studentListView);

        classNameValues.setText(course.getTenLop());
        teacherValues.setText(course.getTenGiaoVien());
        totalCreditValues.setText("" + course.getSoTinChi());
        studentList = new ArrayList<>();

        callApi();
    }

    private void callApi() {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("maMon", course.getMaMon());
        jsonObject.addProperty("lopTinChi", course.getLopTinChi());

        // Chuyển đối tượng JSON thành chuỗi
        String json = jsonObject.toString();
        Log.e("TAG", "saveNote: " + json);
        Request request = new Request.Builder()
                .url("https://ttcs-test.000webhostapp.com/androidApi/getStudents.php")
                .post(RequestBody.create(mediaType, json))
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(ShowClassActivity.this, "False", Toast.LENGTH_SHORT).show();
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
                        boolean status = jsonObject.get("status").getAsBoolean();
                        if (status) {
                            JsonArray studentJsonArray = jsonObject.get("sinhVien").getAsJsonArray();
                            studentList.clear();
                            for (JsonElement studentJsonElement : studentJsonArray) {
                                JsonObject studentJsonObject = studentJsonElement.getAsJsonObject();
                                SinhVien sinhVien = gson.fromJson(studentJsonObject, SinhVien.class);

                                studentList.add(sinhVien);
                            }
                            studentAdapter = new StudentAdapter(ShowClassActivity.this, R.layout.student_item, studentList);
                            studentListView.setAdapter(studentAdapter);
                            studentListView.deferNotifyDataSetChanged();
                            // Sử dụng danh sách noteList như mong muốn
                        } else {
                            Toast.makeText(ShowClassActivity.this, "Không Lấy được dữ liệu", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}