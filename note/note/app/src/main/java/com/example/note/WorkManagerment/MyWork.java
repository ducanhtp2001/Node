package com.example.note.WorkManagerment;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.note.ApiService.ApiService;
import com.example.note.Model.ResponseSchedule;
import com.example.note.Model.Schedule;
import com.example.note.Model.SinhVien;
import com.example.note.R;
import com.example.note.ui.home.HomeFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyWork extends Worker {

    public MyWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Data inputData = getInputData();
        String idSinhVien = inputData.getString("idSinhVien");
        Log.e("TAG", "Work is running");
        callApi(idSinhVien);
        return Result.success();
    }

    private void callApi(String idSinhVien) {
        Map id = new HashMap();
        id.put("id", SinhVien.getIdFromMaSinhVien(idSinhVien));
        ApiService.apiService.getSchedules(id).enqueue(new Callback<ResponseSchedule>() {
            @Override
            public void onResponse(Call<ResponseSchedule> call, Response<ResponseSchedule> response) {
                Log.e("TAG", "onResponse: " + response.body().toString());
                ResponseSchedule res = response.body();
                Log.e("TAG", "body: " + res.getSchedules().get(0));
                boolean status = res.getStatus();
                List<Schedule> schedules = res.getSchedules();
                Calendar calendar = Calendar.getInstance();
                Date date = calendar.getTime();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String now = dateFormat.format(date);
                if(schedules != null && !schedules.isEmpty()) {
                    for (int i = 0; i < schedules.size(); i++) {
                        Date scheDate = schedules.get(i).getNgayHoc();
                        String scheDateStr = dateFormat.format(scheDate);
                        Log.e("TAG", scheDateStr);

                        if (now.equals(scheDateStr)) {
                            Log.d("tag", "equal");
                            showPinNotification(schedules.get(i).getTenMon() + ". Ca học: " + schedules.get(i).getCaHoc());
                        } else Log.d("tag", "not equal");

//                        if(date.before(scheDate)) {
//                            Log.d("tag", "bef");
//                        } else if (date.after(schedules.get(i).getNgayHoc())) {
//                            Log.d("tag", "aft");
//                        } else {
//                            Log.d("tag", "equal");
//                            showPinNotification(schedules.get(i).getTenMon() + ". Ca học: " + schedules.get(i).getCaHoc());
//                        }
                    }
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });
    }

    private void showPinNotification(String title) {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the notification channel (required for Android Oreo and above)
            String channelId = title;  // Use note_title as the channel ID
            NotificationChannel channel = new NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), title)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(true);

        notificationManager.notify(0, builder.build());
    }
}
