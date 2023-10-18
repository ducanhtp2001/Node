package com.example.note.ui.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.note.ChangePassActivity;
import com.example.note.MainActivity;
import com.example.note.R;
import com.example.note.databinding.FragmentSettingsBinding;

import java.util.Locale;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private Switch aSwitch;
    private Button change_password_button;
    private TextView dialog_language;

    private boolean isNightMode;
    private String idSinhVienStr;
    private boolean isEnglish = true;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        idSinhVienStr = ((MainActivity) requireActivity()).idSinhVien;

        aSwitch = root.findViewById(R.id.switch_button);
        change_password_button = root.findViewById(R.id.change_password_button);
        dialog_language = root.findViewById(R.id.dialog_language);

        // Lấy trạng thái chế độ tối hiện tại
        isNightMode = isNightModeEnabled();

        // Thiết lập trạng thái của Switch dựa trên chế độ tối
        aSwitch.setChecked(isNightMode);

        // Set initial text based on the current mode
        setSwitchText(isNightMode);
        // Get the stored language name
        SharedPreferences langPreferences = requireContext().getSharedPreferences("LANGUAGE_NAME_PREFERENCES", AppCompatActivity.MODE_PRIVATE);

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked != isNightMode) {
                    isNightMode = isChecked;
                    MainActivity mainActivity = getMainActivity();
                    if (mainActivity != null) {
                        if (isChecked) {
                            mainActivity.setNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        } else {
                            mainActivity.setNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        }
                    }
                    // Update the switch text when the mode changes
                    setSwitchText(isNightMode);
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

        dialog_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLanguageSelectionDialog();
            }
        });

        return root;
    }

    private void setSwitchText(boolean isNightMode) {
        if (isNightMode) {
            aSwitch.setText("Dark mode");
        } else {
            aSwitch.setText("Light mode");
        }
    }

    private void showLanguageSelectionDialog() {
        String[] languages = { "English", "Tiếng Việt" };

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        builder.setTitle("Select Language");

        // Get the current language code
        SharedPreferences preferences = requireContext().getSharedPreferences("LANGUAGE_PREFERENCES", AppCompatActivity.MODE_PRIVATE);
        String currentLanguage = preferences.getString("LANGUAGE", "");

        // Determine the selected language index based on the current language
        int selectedIndex = currentLanguage.equals("en") ? 0 : 1;

        builder.setSingleChoiceItems(languages, selectedIndex, (dialog, which) -> {
            if (which == 0) {
                setLanguage(new Locale("en"));
            } else {
                setLanguage(new Locale("vi"));
            }

            dialog.dismiss();
        });
        builder.show();
    }

    private void setLanguage(Locale locale) {
        SharedPreferences preferences = requireContext().getSharedPreferences("LANGUAGE_PREFERENCES", AppCompatActivity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        String languageCode = locale.getLanguage();
        editor.putString("LANGUAGE", languageCode);
        editor.apply();

        setLocale();

        if (languageCode.equals("en")) {
            Toast.makeText(requireContext(), "Switched to English ", Toast.LENGTH_SHORT).show();
        } else if (languageCode.equals("vi")) {
            Toast.makeText(requireContext(), "Đã chuyển sang Tiếng Việt", Toast.LENGTH_SHORT).show();
        }

        // Update the stored language name
        SharedPreferences langPreferences = requireContext().getSharedPreferences("LANGUAGE_NAME_PREFERENCES", AppCompatActivity.MODE_PRIVATE);
        SharedPreferences.Editor langEditor = langPreferences.edit();
        langEditor.apply();

        // Restart the activity to apply the language change
        restartActivity();
    }


    private void setLocale() {
        SharedPreferences preferences = requireContext().getSharedPreferences("LANGUAGE_PREFERENCES", AppCompatActivity.MODE_PRIVATE);
        String languageCode = preferences.getString("LANGUAGE", "");

        if (!languageCode.isEmpty()) {
            Locale locale = new Locale(languageCode);
            Locale.setDefault(locale);

            Configuration configuration = new Configuration();
            configuration.setLocale(locale);

            getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
        }
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

    private void restartActivity() {
        Intent intent = requireActivity().getIntent();
        requireActivity().finish();
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
