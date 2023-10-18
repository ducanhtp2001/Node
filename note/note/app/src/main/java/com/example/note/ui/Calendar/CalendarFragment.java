package com.example.note.ui.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.note.Adapter.ScheduleAdapter;
import com.example.note.AddNoteActivity;
import com.example.note.MainActivity;
import com.example.note.Model.Schedule;
import com.example.note.Model.SinhVien;
import com.example.note.R;
import com.example.note.databinding.FragmentCalendarBinding;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CalendarFragment extends Fragment {

    private final int RESULT_CODE_ADDNOTE = 1;
    private FragmentCalendarBinding binding;
    private ListView listView;
    private List<Schedule> schedules;
    private ScheduleAdapter scheduleAdapter;
    private String idSinhVien;
    private CalendarView calendarView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        idSinhVien = ((MainActivity) getActivity()).idSinhVien;
        schedules = new ArrayList<>();

        calendarView = root.findViewById(R.id.calendarView);
        listView = root.findViewById(R.id.calendarListView);
        scheduleAdapter = new ScheduleAdapter(getActivity(), R.layout.schedule_layout, schedules);
        listView.setAdapter(scheduleAdapter);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date selectedDate = new Date(year - 1900, month, dayOfMonth);
                String formattedDate = sdf.format(selectedDate);

                calendarView.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);

                callApi(formattedDate);
                String toastMessage = "Đây là lịch của ngày " + dayOfMonth + "/" + (month + 1) + "/" + year;
                Toast.makeText(getActivity(), toastMessage, Toast.LENGTH_SHORT).show();

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(getActivity(), AddNoteActivity.class);
                        intent.putExtra("idSinhVien", idSinhVien);
                        intent.putExtra("belong", schedules.get(i).getTenMon() + " : " + schedules.get(i).getNgayHocToString());
                        startActivityForResult(intent, RESULT_CODE_ADDNOTE);

//                        Toast.makeText(getActivity(), schedules.get(i).getTenMon() + " : " + schedules.get(i).getNgayHocToString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        return root;
    }

    private void callApi(String selectedDate) {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", SinhVien.getIdFromMaSinhVien(idSinhVien));
        jsonObject.addProperty("selectedDate", selectedDate);

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
                // Xử lý lỗi
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
                                    JsonObject scheduleJsonObject = scheduleJsonElement.getAsJsonObject();
                                    String ngayHoc = scheduleJsonObject.get("ngayHoc").getAsString();

                                    // Kiểm tra ngayHoc trùng khớp với selectedDate
                                    if (ngayHoc.equals(selectedDate)) {
                                        int maMon = scheduleJsonObject.get("maMon").getAsInt();
                                        String tenMon = scheduleJsonObject.get("tenMon").getAsString();
                                        int soTinChi = scheduleJsonObject.get("soTinChi").getAsInt();
                                        int lopTinChi = scheduleJsonObject.get("lopTinChi").getAsInt();
                                        int caHoc = scheduleJsonObject.get("caHoc").getAsInt();
                                        String phongHoc = scheduleJsonObject.get("phongHoc").getAsString();

                                        Schedule schedule = new Schedule(maMon, tenMon, soTinChi, lopTinChi, ngayHoc, caHoc, phongHoc);
                                        schedules.add(schedule);
                                    }
                                }
                                scheduleAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(getActivity(), "Không Lấy được dữ liệu", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_CODE_ADDNOTE) {
            if (resultCode == Activity.RESULT_OK) {
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getActivity(), "Cancel", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}