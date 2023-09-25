package com.example.note.ui.School;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.note.Adapter.ClassAdapter;
import com.example.note.ApiService.ApiService;
import com.example.note.MainActivity;
import com.example.note.Model.Course;
import com.example.note.Model.ResponseClass;
import com.example.note.Model.SinhVien;
import com.example.note.R;
import com.example.note.ShowClassActivity;
import com.example.note.databinding.FragmentSchoolBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SchoolFragment extends Fragment {

    private FragmentSchoolBinding binding;
    private ListView listView_school;
    private List<Course> courses;
    private ClassAdapter classAdapter;
    private String idSinhVienStr;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentSchoolBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        listView_school = root.findViewById(R.id.listView_school);

        idSinhVienStr = ((MainActivity) getActivity()).idSinhVien;
        courses = new ArrayList<>();
        classAdapter = new ClassAdapter(getActivity(), R.layout.classmate_item, courses);
        listView_school.setAdapter(classAdapter);

        listView_school.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), ShowClassActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("course", courses.get(i));
                bundle.putString("idSinhVienStr", idSinhVienStr);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        callApi();

        return root;
    }

    private void callApi() {
        int idSinhVien = SinhVien.getIdFromMaSinhVien(this.idSinhVienStr);
        Map<String, Integer> idMap = new HashMap<>();
        idMap.put("id", idSinhVien);
        ApiService.apiService.getClassById(idMap).enqueue(new Callback<ResponseClass>() {
            @Override
            public void onResponse(Call<ResponseClass> call, Response<ResponseClass> response) {
                Log.e("TAG", "onResponse: " + response.body().toString());
                ResponseClass res = response.body();
                boolean status = res.isStatus();
                courses.clear();
                courses.addAll(res.getCourses());
                classAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ResponseClass> call, Throwable t) {
                Toast.makeText(getActivity(), "False", Toast.LENGTH_SHORT).show();
            }
        });
    }
}