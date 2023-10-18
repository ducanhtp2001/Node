package com.example.note.ui.account;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.note.Adapter.ScheduleAdapter;
import com.example.note.ApiService.ApiService;
import com.example.note.MainActivity;
import com.example.note.Model.AvatarUrl;
import com.example.note.Model.ResponseAvatar;
import com.example.note.Model.ResponseClass;
import com.example.note.Model.ResponseStatus;
import com.example.note.Model.Schedule;
import com.example.note.Model.SinhVien;
import com.example.note.R;
import com.example.note.databinding.FragmentAccountBinding;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AccountFragment extends Fragment {

    private FragmentAccountBinding binding;

    private ImageView imgAvatar;
    private TextView textViewIDLabel;
    private TextView textViewIDValue;
    private TextView textViewHoTenLabel;
    private TextView textViewHoTenValue;
    private TextView textViewNgaySinhLabel;
    private TextView textViewNgaySinhValue;
    private TextView textViewGioiTinhLabel;
    private TextView textViewGioiTinhValue;
    private TextView textViewQueQuanLabel;
    private TextView textViewQueQuanValue;
    private TextView textViewGmailLabel;
    private TextView textViewGmailValue;
    private TextView textViewSDTLabel;
    private TextView textViewSDTValue;
    private TextView textViewKhoaLabel;
    private TextView textViewKhoaValue;
    private TextView textViewNienKhoaLabel;
    private TextView textViewNienKhoaValue;
    private TextView textViewLopLabel;
    private TextView textViewLopValue;
    private String idSinhVien;
    private SinhVien sinhVien;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAccountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        idSinhVien = ((MainActivity) getActivity()).idSinhVien;

        imgAvatar = root.findViewById(R.id.avatar);

        textViewIDLabel = root.findViewById(R.id.textViewIDLabel);
        textViewIDValue = root.findViewById(R.id.textViewIDValue);
        textViewIDValue.setText("");

        textViewHoTenLabel = root.findViewById(R.id.textViewHoTenLabel);
        textViewHoTenValue = root.findViewById(R.id.textViewHoTenValue);
        textViewHoTenValue.setText("");

        textViewNgaySinhLabel = root.findViewById(R.id.textViewNgaySinhLabel);
        textViewNgaySinhLabel.setText("Ngày sinh: ");
        textViewNgaySinhValue = root.findViewById(R.id.textViewNgaySinhValue);
        textViewNgaySinhValue.setText("");

        textViewGioiTinhLabel = root.findViewById(R.id.textViewGioiTinhLabel);
        textViewGioiTinhLabel.setText("Giới tính: ");
        textViewGioiTinhValue = root.findViewById(R.id.textViewGioiTinhValue);
        textViewGioiTinhValue.setText("");

        textViewQueQuanLabel = root.findViewById(R.id.textViewQueQuanLabel);
        textViewQueQuanLabel.setText("Quê quán: ");
        textViewQueQuanValue = root.findViewById(R.id.textViewQueQuanValue);
        textViewQueQuanValue.setText("");

        textViewGmailLabel = root.findViewById(R.id.textViewGmailLabel);
        textViewGmailLabel.setText("Gmail: ");
        textViewGmailValue = root.findViewById(R.id.textViewGmailValue);
        textViewGmailValue.setText("");

        textViewSDTLabel = root.findViewById(R.id.textViewSDTLabel);
        textViewSDTLabel.setText("Số điện thoại: ");
        textViewSDTValue = root.findViewById(R.id.textViewSDTValue);
        textViewSDTValue.setText("");

        textViewKhoaLabel = root.findViewById(R.id.textViewKhoaLabel);
        textViewKhoaLabel.setText("Khoa: ");
        textViewKhoaValue = root.findViewById(R.id.textViewKhoaValue);
        textViewKhoaValue.setText("");

        textViewNienKhoaLabel = root.findViewById(R.id.textViewNienKhoaLabel);
        textViewNienKhoaLabel.setText("Niên khóa: ");
        textViewNienKhoaValue = root.findViewById(R.id.textViewNienKhoaValue);
        textViewNienKhoaValue.setText("");

        textViewLopLabel = root.findViewById(R.id.textViewLopLabel);
        textViewLopLabel.setText("Lớp: ");
        textViewLopValue = root.findViewById(R.id.textViewLopValue);
        textViewLopValue.setText("");


        callGetAvatarApi();

        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Nhập url của avatar");

                final EditText input = new EditText(getContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String url = input.getText().toString();
                        Toast.makeText(getContext(), "URL: " + url, Toast.LENGTH_SHORT).show();

                        Map avtMap = new HashMap();
                        avtMap.put("id", SinhVien.getIdFromMaSinhVien(idSinhVien));
                        avtMap.put("url", url);

                        ApiService.apiService.setAvatarUrl(avtMap).enqueue(new retrofit2.Callback<ResponseStatus>() {
                            @Override
                            public void onResponse(retrofit2.Call<ResponseStatus> call, retrofit2.Response<ResponseStatus> response) {
                                Log.e("TAG", "onResponse: " + response.body().toString());
                                ResponseStatus res = response.body();
                                boolean status = res.isStatus();
                                if(status) {
                                    callGetAvatarApi();
                                } else {
                                    Toast.makeText(getContext(), "Set Avatar err", Toast.LENGTH_SHORT).show();
                                    Log.e("TAG", "setActivity err");
                                }
                            }

                            @Override
                            public void onFailure(retrofit2.Call<ResponseStatus> call, Throwable t) {
                                Log.e("TAG", "setActivity err");
                            }
                        });
                    }
                });

                builder.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getContext(), "Canceled", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.show();
            }
        });

        callApi();

        return root;
    }

    private void callGetAvatarApi() {
        Map idMap = new HashMap();
        idMap.put("id", SinhVien.getIdFromMaSinhVien(idSinhVien));

        ApiService.apiService.getAvatarUrl(idMap).enqueue(new retrofit2.Callback<ResponseAvatar>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseAvatar> call, retrofit2.Response<ResponseAvatar> response) {
                Log.e("TAG", "onResponse: " + response.body().toString());
                ResponseAvatar res = response.body();
                boolean status = res.isStatus();
                Log.e("url", res.getAvatar().getAvatar());
                Glide.with(getActivity())
                        .load(res.getAvatar().getAvatar())
                        .override(120, 160)
                        .into(imgAvatar);
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseAvatar> call, Throwable t) {
                Log.e("TAG", "getActivity err");
            }
        });
    }

    private void callApi() {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", SinhVien.getIdFromMaSinhVien(idSinhVien));

        // Chuyển đối tượng JSON thành chuỗi
        String json = jsonObject.toString();
        Log.e("TAG", "id: " + json);
        Request request = new Request.Builder()
                .url("https://ttcs-test.000webhostapp.com/androidApi/getInfoAccount.php")
                .post(RequestBody.create(mediaType, json))
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(getActivity(), "False", Toast.LENGTH_SHORT).show();
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
                        boolean status = jsonObject.get("status").getAsBoolean();
                        JsonObject sinhVienJson = jsonObject.get("sinhVien").getAsJsonObject();
                        if (status) {
                            sinhVien = gson.fromJson(sinhVienJson, SinhVien.class);

                            textViewIDValue.setText(sinhVien.getMaSinhVien());
                            textViewHoTenValue.setText(sinhVien.getHoTen());

//                            if(sinhVien.getGioiTinhBoolean()) imgAvatar.setImageResource(R.drawable.nam);
//                            else imgAvatar.setImageResource(R.drawable.nu);

                            textViewNgaySinhLabel.setText("Ngày sinh: ");
                            textViewNgaySinhValue.setText(sinhVien.getNgaySinhStr());

                            textViewGioiTinhLabel.setText("Giới tính: ");
                            textViewGioiTinhValue.setText(sinhVien.getGioiTinh());

                            textViewQueQuanLabel.setText("Quê quán: ");
                            textViewQueQuanValue.setText(sinhVien.getQueQuan());

                            textViewGmailLabel.setText("Gmail: ");
                            textViewGmailValue.setText(sinhVien.getGmail());

                            textViewSDTLabel.setText("Số điện thoại: ");
                            textViewSDTValue.setText(sinhVien.getSdt());

                            textViewKhoaLabel.setText("Khoa: ");
                            textViewKhoaValue.setText(sinhVien.getKhoaStr());

                            textViewNienKhoaLabel.setText("Niên khóa: ");
                            textViewNienKhoaValue.setText(sinhVien.getNienKhoaStr());

                            textViewLopLabel.setText("Lớp: ");
                            textViewLopValue.setText(sinhVien.getLopStr());

                            // Sử dụng danh sách noteList như mong muốn
                        } else {
                            Toast.makeText(getActivity(), "Không Lấy được dữ liệu", Toast.LENGTH_SHORT).show();
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
