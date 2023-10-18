package com.example.note;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
    TextView forgotPasword;
    Button loginBtn;
    EditText lPassword;
    EditText lIdStuden;

    private String idSinhVienStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tv_createAccount = findViewById(R.id.createAccount);
        loginBtn = findViewById(R.id.loginBtn);
        lIdStuden = findViewById(R.id.lIdStuden);
        lPassword = findViewById(R.id.lPassword);
        forgotPasword = findViewById(R.id.forgotPasword);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(lIdStuden.getText().toString())) {
                    lIdStuden.setError("Vui lòng nhập họ tên");
                    return;
                }

                if (TextUtils.isEmpty(lPassword.getText().toString())) {
                    lPassword.setError("Vui lòng nhập Password");
                    return;
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
                                if (status) {
                                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.putExtra("ID_SINHVIEN", idSinhVien);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);
                                } else {
                                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        });

        CheckBox showPasswordCheckBox = findViewById(R.id.showPasswordCheckBox);
        showPasswordCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Show password
                    lPassword.setTransformationMethod(SingleLineTransformationMethod.getInstance());
                } else {
                    // Hide password
                    lPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        tv_createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        forgotPasword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgetPassActivity.class);
                intent.putExtra("idSinhVien", idSinhVienStr);
                startActivity(intent);
            }
        });
    }
}
