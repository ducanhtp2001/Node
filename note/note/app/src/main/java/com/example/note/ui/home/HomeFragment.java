package com.example.note.ui.home;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.note.Adapter.NoteAdapter;
import com.example.note.Adapter.ScheduleAdapter;
import com.example.note.AddNoteActivity;
import com.example.note.ApiService.ApiService;
import com.example.note.EditNoteActivity;
import com.example.note.MainActivity;
import com.example.note.Model.Note;
import com.example.note.Model.ResponseNote;
import com.example.note.Model.Schedule;
import com.example.note.Model.SinhVien;
import com.example.note.R;
import com.example.note.SQLite.Connect;
import com.example.note.SQLite.ConnectSharing;
import com.example.note.databinding.FragmentHomeBinding;
import com.example.note.ui.Calendar.CalendarFragment;
import com.example.note.ui.School.SchoolFragment;
import com.example.note.ui.settings.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private FloatingActionButton fab;

    private String belong;
    private BottomNavigationView bottom_nav;
    private final int RESULT_CODE_ADDNOTE = 1;
    private final int RESULT_CODE_EDITNOTE = 2;

    private boolean isFragmentHome = true;

    private List<Note> notes;
    private NoteAdapter noteAdapter;

    private List<Note> notesToSearch;
    private NoteAdapter noteAdapterToSearch;

    private ListView listView;
    private LinearLayout layoutSearch;
    private Button btnExit;
    private EditText ed_search;
    private Button btnSearch;

    private String idSinhVienstr;

    private Connect connect;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        listView = root.findViewById(R.id.listView);
        layoutSearch = root.findViewById(R.id.layoutSearch);
        btnExit = root.findViewById(R.id.btnExit);
        ed_search = root.findViewById(R.id.ed_search);
        btnSearch = root.findViewById(R.id.btnSearch);
        fab = root.findViewById(R.id.fab_addNote);
        bottom_nav = root.findViewById(R.id.bottomNavigationView);
        belong = getResources().getString(R.string.note_belong);

        setHasOptionsMenu(true);
        hideSearch();
//        showSearch();

        // Initialize the notes list and adapter
        notes = new ArrayList<>();
        notesToSearch = new ArrayList<>();
        noteAdapter = new NoteAdapter(getActivity(), R.layout.note_layout, notes);
        noteAdapterToSearch = new NoteAdapter(getActivity(), R.layout.note_layout, notesToSearch);
        listView.setAdapter(noteAdapter);

        idSinhVienstr = ((MainActivity) getActivity()).idSinhVien;
        Log.e("TAG", "id sinh vien: " + idSinhVienstr);

        connect = ConnectSharing.getConnectSharing(getActivity());

        connect.nonReturnQuery("CREATE TABLE IF NOT EXISTS note (" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    idsinhvien INT," +
                "    tieude VARCHAR(100)," +
                "    ngayTao DATE," +
                "    ngayCapNhat DATE," +
                "    noidung TEXT," +
                "    noidungcua TEXT" +
                ")");

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddNoteActivity.class);
                intent.putExtra("idSinhVien", idSinhVienstr);
                intent.putExtra("belong", belong); //
                startActivityForResult(intent, RESULT_CODE_ADDNOTE);
            }
        });
        bottom_nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.calendar) {
                    replaceFragment(new CalendarFragment());
                } else if (id == R.id.home) {
                    if (!(getParentFragment() instanceof HomeFragment)) {
                        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
                        navController.navigate(R.id.nav_home);
                    }
                } else if (id == R.id.school) {
                    replaceFragment(new SchoolFragment());
                } else if (id == R.id.setting) {
                    replaceFragment(new SettingsFragment());
                }
                return true;
            }
        });

        // Handle item click event
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Note selectedNote = notes.get(position);
                if (selectedNote != null) {
                    Intent intent = new Intent(getActivity(), EditNoteActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("note", selectedNote);
                    bundle.putString("idSinhVien", idSinhVienstr);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, RESULT_CODE_EDITNOTE);
                }
            }
        });
        
        registerForContextMenu(listView);

        callApi();

        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_search:
                showSearch();
                return true;
            case R.id.action_sort_by_create:
                sortByCreate();
                return true;
            case R.id.action_sort_by_modify:
                shortByModify();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private void shortByModify() {
        Toast.makeText(getActivity(), "Sort by modify time", Toast.LENGTH_SHORT).show();
        Collections.sort(notes, new Comparator<Note>() {
            @Override
            public int compare(Note note1, Note note2) {
                return Long.compare(note1.getNgayCapNhat().getTime(), note2.getNgayCapNhat().getTime());
            }
        });
        noteAdapter.notifyDataSetChanged();
    }

    private void sortByCreate() {
        Toast.makeText(getActivity(), "Sort by create time", Toast.LENGTH_SHORT).show();
        Collections.sort(notes, new Comparator<Note>() {
            @Override
            public int compare(Note note1, Note note2) {
                return Long.compare(note1.getNgayTao().getTime(), note2.getNgayTao().getTime());
            }
        });
        noteAdapter.notifyDataSetChanged();
    }

    public void hideSearch() {
        layoutSearch.setVisibility(View.GONE);
    }

    public void showSearch() {
        layoutSearch.setVisibility(View.VISIBLE);
        btnExit.setVisibility(View.VISIBLE);
        btnSearch.setVisibility(View.VISIBLE);
        ed_search.setVisibility(View.VISIBLE);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callApi();
                hideSearch();
            }
        });
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listView.setAdapter(noteAdapterToSearch);
                String str = ed_search.getText().toString();
                notesToSearch.clear();
                for (Note note : notes) {
                    if(note.getTieuDe().contains(str) || note.getNoiDung().contains(str)) notesToSearch.add(note);
                }
                noteAdapterToSearch.notifyDataSetChanged();
            }
        });
    }

    private void callApi() {
        listView.setAdapter(noteAdapter);
        int idSinhVien = SinhVien.getIdFromMaSinhVien(this.idSinhVienstr);
        Map<String, Integer> idMap = new HashMap<>();
        idMap.put("id", idSinhVien);
        ApiService.apiService.getNoteById(idMap).enqueue(new Callback<ResponseNote>() {
            @Override
            public void onResponse(Call<ResponseNote> call, Response<ResponseNote> response) {
                Log.e("TAG", "onResponse: " + response.body().toString());
                ResponseNote res = response.body();
                boolean status = res.isStatus();
                notes.clear();
                if(res.getNotes() != null) {
                    notes.addAll(res.getNotes());
                }
                noteAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ResponseNote> call, Throwable t) {
                Toast.makeText(getActivity(), "Load err", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        int destinationId = getDestinationId(fragment);
        if (destinationId != 0) {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(destinationId);
        }
    }

    private int getDestinationId(Fragment fragment) {
        int id = 0;
        if (fragment instanceof CalendarFragment) {
            id = R.id.calendar;
        } else if (fragment instanceof HomeFragment) {
            id = R.id.home;
        } else if (fragment instanceof SchoolFragment) {
            id = R.id.school;
        } else if (fragment instanceof SettingsFragment) {
            id = R.id.setting;
        }
        return id;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_CODE_ADDNOTE) {
            if (resultCode == Activity.RESULT_OK) {
                callApi();
            } else if (resultCode == Activity.RESULT_CANCELED) {

            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        Note selectedNote = noteAdapter.getItem(info.position);

        if (selectedNote != null) {
            getActivity().getMenuInflater().inflate(R.menu.note_menu, menu);
        }
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        Note selectedNote = noteAdapter.getItem(position);

        if (selectedNote != null) {
            int itemId = item.getItemId();
            if (itemId == R.id.action_pin) {
                // Handle the "Pin" action
                String noteTitle = selectedNote.getTieuDe();
                showPinNotification(noteTitle);
                return true;
            } else if (itemId == R.id.action_del) {
                // Handle the "Delete" action
                callDeleteApi(selectedNote);
                return true;
            }
        }

        return super.onContextItemSelected(item);
    }

    private void callDeleteApi(Note selectedNote) {
        int idSinhVien = SinhVien.getIdFromMaSinhVien(idSinhVienstr);
        int id = selectedNote.getId();

        String query = "INSERT INTO note VALUES (null, " + idSinhVien + ", '" + selectedNote.getTieuDe() +
                "', '" + Note.getNgayStr(selectedNote.getNgayTao()) +
                "', '" + Note.getNgayStr(selectedNote.getNgayCapNhat()) +
                "', '" + selectedNote.getNoiDung() +
                "', '" + selectedNote.getNoiDungCua() + "')";


        connect.nonReturnQuery(query);

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("idSinhVien", idSinhVien);
        jsonObject.addProperty("id", id);

        // Chuyển đối tượng JSON thành chuỗi
        String json = jsonObject.toString();
        Log.e("TAG", "saveNote: " + json);
        Request request = new Request.Builder()
                .url("https://ttcs-test.000webhostapp.com/androidApi/deleteNote.php")
                .post(RequestBody.create(mediaType, json))
                .build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Toast.makeText(getActivity(), "False", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                String json = response.body().string();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        JsonElement jsonElement = gson.fromJson(json, JsonElement.class);
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        boolean status = jsonObject.get("status").getAsBoolean();
                        String message = jsonObject.get("message").getAsString();
                        if (status) {
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            callApi();
                        } else {
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void showPinNotification(String noteTitle) {
        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the notification channel (required for Android Oreo and above)
            String channelId = noteTitle;  // Use note_title as the channel ID
            NotificationChannel channel = new NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), noteTitle)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(noteTitle)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(true);

        notificationManager.notify(0, builder.build());
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
