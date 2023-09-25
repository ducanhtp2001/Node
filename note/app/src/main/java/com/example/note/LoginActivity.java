package com.example.note;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    TextView tv_createAccount;
    Button loginBtn;
    EditText lPassword;
    EditText lIdStuden;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tv_createAccount = findViewById(R.id.createAccount);
        loginBtn = findViewById(R.id.loginBtn);
        lIdStuden = findViewById(R.id.lIdStuden);
        lPassword = findViewById(R.id.lPassword);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(lIdStuden.getText().toString().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Hãy nhập ID Studen", Toast.LENGTH_SHORT).show();
                } else if(lPassword.getText().toString().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Hãy nhập Password", Toast.LENGTH_SHORT).show();
                }

                OkHttpClient client = new OkHttpClient();
                MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
                String idSinhVien = lIdStuden.getText().toString().toUpperCase();
                String password = lPassword.getText().toString();

                // Tạo đối tượng JSON
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("idSinhVien", idSinhVien);
                jsonObject.addProperty("password", password);

                // Chuyển đối tượng JSON thành chuỗi
                String json = jsonObject.toString();
//                Toast.makeText(MainActivity.this, json, Toast.LENGTH_LONG).show();

//                    tv_fullname.setText(json);
//                    RequestBody requestBody = RequestBody.create(mediaType, json);
                Request request = new Request.Builder()
                        .url("https://ttcs-test.000webhostapp.com/androidApi/login.php")
                        .post(RequestBody.create(mediaType, json))
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(okhttp3.Call call, IOException e) {
                        Log.e("Error", "Network Error");
                    }

                    @Override
                    public void onResponse(okhttp3.Call call, Response response) throws IOException {
                        // Lấy thông tin JSON trả về. Bạn có thể log lại biến json này để xem nó như thế nào.
                        String json = response.body().string();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                    Toast.makeText(RegisterActivity.this, json, Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.putExtra("ID_SINHVIEN", idSinhVien);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                    }

                });
            }
        });

        tv_createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }
}