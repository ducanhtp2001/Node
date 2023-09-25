package com.example.note.ui.Calendar;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.note.Adapter.ScheduleAdapter;
import com.example.note.MainActivity;
import com.example.note.Model.Note;
import com.example.note.Model.Schedule;
import com.example.note.Model.SinhVien;
import com.example.note.R;
import com.example.note.databinding.FragmentAccountBinding;
import com.example.note.databinding.FragmentCalendarBinding;
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


public class CalendarFragment extends Fragment {

    private FragmentCalendarBinding binding;
    private ListView listView;
    private List<Schedule> schedules;
    private ScheduleAdapter scheduleAdapter;
    private String idSinhVien;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        idSinhVien = ((MainActivity) getActivity()).idSinhVien;
        schedules = new ArrayList<>();

        callApi();

        return root;
    }

    private void callApi() {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", SinhVien.getIdFromMaSinhVien(idSinhVien));

        // Chuyển đối tượng JSON thành chuỗi
        String json = jsonObject.toString();
        Log.e("TAG", "saveNote: " + json);
        Request request = new Request.Builder()
                .url("https://ttcs-test.000webhostapp.com/androidApi/getCalendar.php")
                .post(RequestBody.create(mediaType, json))
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if(getActivity() != null) getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(getActivity() != null) Toast.makeText(getActivity(), "False", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();

                if(getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Gson gson = new Gson();
                            JsonElement jsonElement = gson.fromJson(json, JsonElement.class);
                            JsonObject jsonObject = jsonElement.getAsJsonObject();
                            boolean status = jsonObject.get("status").getAsBoolean();
                            if (status) {
                                JsonArray schedulesJsonArray = jsonObject.get("schedules").getAsJsonArray();
                                schedules.clear();
                                for (JsonElement scheduleJsonElement : schedulesJsonArray) {
                                    JsonObject noteJsonObject = scheduleJsonElement.getAsJsonObject();
                                    int maMon = noteJsonObject.get("maMon").getAsInt();
                                    String tenMon = noteJsonObject.get("tenMon").getAsString();
                                    int soTinChi = noteJsonObject.get("soTinChi").getAsInt();
                                    int lopTinChi = noteJsonObject.get("lopTinChi").getAsInt();
                                    String ngayHoc = noteJsonObject.get("ngayHoc").getAsString();
                                    int caHoc = noteJsonObject.get("caHoc").getAsInt();
                                    String phongHoc = noteJsonObject.get("phongHoc").getAsString();

                                    Schedule schedule = new Schedule(maMon, tenMon, soTinChi, lopTinChi, ngayHoc, caHoc, phongHoc);
                                    schedules.add(schedule);
                                }
                                // Sử dụng danh sách noteList như mong muốn
                            } else {
                                Toast.makeText(getActivity(), "Không Lấy được dữ liệu", Toast.LENGTH_SHORT).show();
                            }
                            scheduleAdapter = new ScheduleAdapter(getActivity(), R.layout.schedule_layout, schedules);
                            if(getActivity() != null) listView = getActivity().findViewById(R.id.calendarListView);
                            listView.setAdapter(scheduleAdapter);
                            scheduleAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }

    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}