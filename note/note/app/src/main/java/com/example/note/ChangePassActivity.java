package com.example.note;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.note.Adapter.ScheduleAdapter;
import com.example.note.Model.Schedule;
import com.example.note.Model.SinhVien;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChangePassActivity extends AppCompatActivity {

    private EditText oldPass;
    private EditText newPassword;
    private EditText rePassword;
    private Button changePassBtn;
    private TextView cancelBtn;
    private String idSinhVien;

    String oldP;
    String newP;
    String reP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

        idSinhVien = getIntent().getStringExtra("idSinhVien");

        oldPass = findViewById(R.id.oldPass);
        newPassword = findViewById(R.id.newPassword);
        rePassword = findViewById(R.id.lPassword);
        changePassBtn = findViewById(R.id.changePassBtn);
        cancelBtn = findViewById(R.id.cancelBtn);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        
        changePassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                oldP = oldPass.getText().toString();
                newP = newPassword.getText().toString();
                reP = rePassword.getText().toString();
                
                if (oldP.isEmpty() || newP.isEmpty() || reP.isEmpty())
                    Toast.makeText(ChangePassActivity.this, "Hãy nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                else if(!checkPass(newP) || !checkPass(oldP ) || !checkPass(reP)) {
//                    Toast.makeText(ChangePassActivity.this, "Hãy nhập password đúng định dạng", Toast.LENGTH_SHORT).show();
                } else if(oldP.equals(newP)) {
                    Toast.makeText(ChangePassActivity.this, "Hãy nhập password mới khác password cũ", Toast.LENGTH_SHORT).show();
                } else if (!newP.equals(reP)) {
                    Toast.makeText(ChangePassActivity.this, "Password nhập lại không chính xác", Toast.LENGTH_SHORT).show();
                } else {
                    callApi();
                }
            }
        });

    }

    private void callApi() {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", SinhVien.getIdFromMaSinhVien(idSinhVien));
        jsonObject.addProperty("oldPass", oldP);
        jsonObject.addProperty("newPass", newP);


        // Chuyển đối tượng JSON thành chuỗi
        String json = jsonObject.toString();
        Log.e("TAG", "saveNote: " + json);
        Request request = new Request.Builder()
                .url("https://ttcs-test.000webhostapp.com/androidApi/updatePass.php")
                .post(RequestBody.create(mediaType, json))
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(ChangePassActivity.this, "Network false", Toast.LENGTH_SHORT).show();
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
                        String message = jsonObject.get("message").getAsString();
                        if (status) {
                            Toast.makeText(ChangePassActivity.this, message, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Xóa tất cả các Activity khỏi back stack
                            startActivity(intent);
                            finishAffinity();
                        } else {
                            Toast.makeText(ChangePassActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private boolean checkPass(String str) {
        boolean isHasNumber = false, isHasLetter = false;
        if(str.length() < 6) {
            Toast.makeText(this, "password at least 6 characters", Toast.LENGTH_SHORT).show();
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            if(Character.isDigit(str.charAt(i))) {
                isHasNumber = true;
                continue;
            }
            if(Character.isLetter(str.charAt(i))) {
                isHasLetter = true;
                continue;
            }
        }
        if(isHasNumber && isHasLetter) return true;
        Toast.makeText(this, "Password must contain letters and numbers", Toast.LENGTH_SHORT).show();
        return false;
    }
}