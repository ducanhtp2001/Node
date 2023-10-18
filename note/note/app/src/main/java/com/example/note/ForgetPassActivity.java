package com.example.note;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.note.Model.SinhVien;
import com.example.note.Model.TaiKhoan;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

public class ForgetPassActivity extends AppCompatActivity {

    EditText ed_studentid;
    TextView tv_fullname;
    EditText ed_password;
    EditText ed_passwordConfirm;
    Button btn_createAccount;
    TextView tv_login;

    boolean hasStudent = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass);

        ed_studentid = findViewById(R.id.ed_studentid);
        tv_fullname = findViewById(R.id.tv_fullname);
        ed_password = findViewById(R.id.password);
        ed_passwordConfirm = findViewById(R.id.passwordConfirm);
        btn_createAccount = findViewById(R.id.createAccount);
        tv_login = findViewById(R.id.login);

        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ForgetPassActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        ed_studentid.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE || i == EditorInfo.IME_ACTION_NEXT) {
                    // Xử lý khi nhập xong (hoàn thành)
                    if(ed_studentid.getText().toString().isEmpty()) {
                        Toast.makeText(ForgetPassActivity.this, "Vui lòng nhập mã sinh viên", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    OkHttpClient client = new OkHttpClient();
                    MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
                    String idSinhVien = ed_studentid.getText().toString();
                    int id = SinhVien.getIdFromMaSinhVien(idSinhVien);
                    int khoa = SinhVien.getKhoaFromMaSinhVien(idSinhVien);
                    int nienKhoa = SinhVien.getNienKhoaFromMaSinhVien(idSinhVien);
                    int lop = SinhVien.getLopFromMaSinhVien(idSinhVien);

                    // Tạo đối tượng JSON
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("idSinhVien", id);
                    jsonObject.addProperty("khoa", khoa);
                    jsonObject.addProperty("nienKhoa", nienKhoa);
                    jsonObject.addProperty("lop", lop);

                    // Chuyển đối tượng JSON thành chuỗi
                    String json = jsonObject.toString();

//                    tv_fullname.setText(json);
//                    RequestBody requestBody = RequestBody.create(mediaType, json);
                    Request request = new Request.Builder()
                            .url("https://ttcs-test.000webhostapp.com/androidApi/checkRegister.php")
                            .post(RequestBody.create(mediaType, json))
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e("Error", "Network Error");
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            // Lấy thông tin JSON trả về. Bạn có thể log lại biến json này để xem nó như thế nào.
                            String json = response.body().string();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                    Toast.makeText(RegisterActivity.this, json, Toast.LENGTH_SHORT).show();
                                    Gson gson = new Gson();
                                    JsonElement jsonElement = gson.fromJson(json, JsonElement.class);
                                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                                    String tenSinhVien = "";
                                    boolean status = false;
                                    if (jsonObject.has("tenSinhVien")) {
                                        tenSinhVien = jsonObject.get("tenSinhVien").getAsString();
                                    }
                                    if (jsonObject.has("status")) {
                                        status = jsonObject.get("status").getAsBoolean();
                                    }
//                                    Toast.makeText(RegisterActivity.this, tenSinhVien + status, Toast.LENGTH_SHORT).show();
                                    if(status) {
                                        tv_fullname.setText(tenSinhVien);
                                        hasStudent = true;
                                    } else {
                                        Toast.makeText(ForgetPassActivity.this, "Sinh viên không tồn tại", Toast.LENGTH_SHORT).show();
                                        tv_fullname.setText(tenSinhVien);
                                        hasStudent = false;
                                    }

                                }
                            });

                        }
                    });

                    return true; // Trả về true để ngăn việc xử lý mặc định của EditText
                }
                return false;
            }
        });
        CheckBox showPasswordCheckBox = findViewById(R.id.showPasswordCheckBox);
        showPasswordCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Show password
                    ed_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    ed_passwordConfirm.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    // Hide password
                    ed_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    ed_passwordConfirm.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                // Move cursor to the end of the text
                ed_password.setSelection(ed_password.getText().length());
                ed_passwordConfirm.setSelection(ed_passwordConfirm.getText().length());
            }
        });

        ed_studentid.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    // Xử lý khi EditText bị mất focus (chuyển sang thành phần khác)
                    if(ed_studentid.getText().toString().isEmpty()) {
                        Toast.makeText(ForgetPassActivity.this, "Vui lòng nhập mã sinh viên", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    OkHttpClient client = new OkHttpClient();
                    MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
                    String idSinhVien = ed_studentid.getText().toString();
                    int id = SinhVien.getIdFromMaSinhVien(ed_studentid.getText().toString());
                    int khoa = SinhVien.getKhoaFromMaSinhVien(idSinhVien);
                    int nienKhoa = SinhVien.getNienKhoaFromMaSinhVien(idSinhVien);
                    int lop = SinhVien.getLopFromMaSinhVien(idSinhVien);

                    // Tạo đối tượng JSON
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("idSinhVien", id);
                    jsonObject.addProperty("khoa", khoa);
                    jsonObject.addProperty("nienKhoa", nienKhoa);
                    jsonObject.addProperty("lop", lop);

                    // Chuyển đối tượng JSON thành chuỗi
                    String json = jsonObject.toString();

//                    tv_fullname.setText(json);
//                    RequestBody requestBody = RequestBody.create(mediaType, json);
                    Request request = new Request.Builder()
                            .url("https://ttcs-test.000webhostapp.com/androidApi/checkRegister.php")
                            .post(RequestBody.create(mediaType, json))
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e("Error", "Network Error");
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            // Lấy thông tin JSON trả về. Bạn có thể log lại biến json này để xem nó như thế nào.
                            String json = response.body().string();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                    Toast.makeText(RegisterActivity.this, json, Toast.LENGTH_SHORT).show();
                                    Gson gson = new Gson();
                                    JsonElement jsonElement = gson.fromJson(json, JsonElement.class);
                                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                                    String tenSinhVien = "";
                                    boolean status = false;
                                    if (jsonObject.has("tenSinhVien")) {
                                        tenSinhVien = jsonObject.get("tenSinhVien").getAsString();
                                    }
                                    if (jsonObject.has("status")) {
                                        status = jsonObject.get("status").getAsBoolean();
                                    }
                                    if(status) {
                                        tv_fullname.setText(tenSinhVien);
                                        hasStudent = true;
                                    } else {
                                        Toast.makeText(ForgetPassActivity.this, "Sinh viên không tồn tại", Toast.LENGTH_SHORT).show();
                                        tv_fullname.setText(tenSinhVien);
                                        hasStudent = false;
                                    }

                                }
                            });

                        }
                    });
                }
            }
        });


        btn_createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(RegisterActivity.this, "" + hasStudent, Toast.LENGTH_SHORT).show();
                if(!hasStudent) {
                    Toast.makeText(ForgetPassActivity.this, "Sinh viên không tồn tại", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(ed_studentid.getText().toString().isEmpty() ||
                   ed_password.getText().toString().isEmpty() ||
                   ed_passwordConfirm.getText().toString().isEmpty()) {
                    Toast.makeText(ForgetPassActivity.this, "Không được để trông các trường", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(ed_password.getText().toString().equals(ed_passwordConfirm.getText().toString())) {
                    if(!checkPass(ed_password.getText().toString())) return;

                    TaiKhoan tk = new TaiKhoan(SinhVien.getIdFromMaSinhVien(ed_studentid.getText().toString()),
                            ed_studentid.getText().toString().toUpperCase(),
                            ed_password.getText().toString());

                    OkHttpClient client = new OkHttpClient();
                    MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
                    Gson gson = new GsonBuilder().create();
                    String json = gson.toJson(tk);
                    RequestBody requestBody = RequestBody.create(mediaType, json);
                    Request request = new Request.Builder()
                            .url("https://ttcs-test.000webhostapp.com/androidApi/register.php")
                            .post(requestBody)
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e("Error", "Network Error");
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
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
                                    if(status) {
                                        Toast.makeText(ForgetPassActivity.this, message, Toast.LENGTH_LONG).show();
                                        finish();
                                    } else Toast.makeText(ForgetPassActivity.this, message, Toast.LENGTH_LONG).show();
                                }
                            });

                        }
                    });

                } else Toast.makeText(ForgetPassActivity.this,
                        "Mật khẩu nhập lại không chính xác", Toast.LENGTH_SHORT).show();

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