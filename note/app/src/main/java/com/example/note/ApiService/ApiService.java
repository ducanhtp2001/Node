package com.example.note.ApiService;

import com.example.note.Model.ResponseClass;
import com.example.note.Model.ResponseNote;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH-:mm:ss").create();

    ApiService apiService = new Retrofit.Builder()
            .baseUrl("https://ttcs-test.000webhostapp.com/androidApi/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService.class);

    @POST("getNote.php")
    Call<ResponseNote> getNoteById(@Body Map<String, Integer> id);

    @POST("getClass.php")
    Call<ResponseClass> getClassById(@Body Map<String, Integer> id);
}
