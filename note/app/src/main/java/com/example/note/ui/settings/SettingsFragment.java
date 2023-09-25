package com.example.note.ui.settings;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.note.ChangePassActivity;
import com.example.note.MainActivity;
import com.example.note.R;
import com.example.note.databinding.AppBarMainBinding;
import com.example.note.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private Switch aSwitch;
    private Button change_password_button;

    private boolean isNightMode;
    private String idSinhVienStr;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        idSinhVienStr = ((MainActivity)getMainActivity()).idSinhVien;

        aSwitch = root.findViewById(R.id.switch_button);
        change_password_button = root.findViewById(R.id.change_password_button);

        // Lấy trạng thái chế độ tối hiện tại
        isNightMode = isNightModeEnabled();

        // Thiết lập trạng thái của Switch dựa trên chế độ tối
        aSwitch.setChecked(isNightMode);

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked != isNightMode) {
                    isNightMode = isChecked;
                    MainActivity mainActivity = getMainActivity();
                    if (mainActivity != null) {
                        if (isChecked) {
                            mainActivity.setNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                            aSwitch.setText("Dark mode");
                        } else {
                            mainActivity.setNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                            aSwitch.setText("Light mode");
                        }
                    }
                }
            }
        });

        change_password_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ChangePassActivity.class);
                intent.putExtra("idSinhVien", idSinhVienStr);
                startActivity(intent);
            }
        });

        return root;
    }

    private boolean isNightModeEnabled() {
        int nightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return nightMode == Configuration.UI_MODE_NIGHT_YES;
    }

    private MainActivity getMainActivity() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity instanceof MainActivity) {
            return (MainActivity) activity;
        }
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}