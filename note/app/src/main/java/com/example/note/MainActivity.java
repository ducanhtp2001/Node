    package com.example.note;

    import android.app.Activity;
    import android.app.NotificationChannel;
    import android.app.NotificationManager;
    import android.content.Intent;
    import android.os.Build;
    import android.os.Bundle;
    import android.view.ContextMenu;
    import android.view.Menu;
    import android.view.MenuItem;
    import android.view.View;
    import android.widget.AdapterView;
    import android.widget.ListView;
    import android.widget.Toast;

    import androidx.annotation.NonNull;
    import androidx.annotation.Nullable;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.appcompat.app.AppCompatDelegate;
    import androidx.core.app.NotificationCompat;
    import androidx.core.view.GravityCompat;
    import androidx.drawerlayout.widget.DrawerLayout;
    import androidx.fragment.app.Fragment;
    import androidx.fragment.app.FragmentTransaction;
    import androidx.navigation.NavController;
    import androidx.navigation.Navigation;
    import androidx.navigation.ui.AppBarConfiguration;
    import androidx.navigation.ui.NavigationUI;

    import com.example.note.Adapter.NoteAdapter;
    import com.example.note.Model.Note;
    import com.example.note.databinding.ActivityMainBinding;
    import com.example.note.ui.Calendar.CalendarFragment;
    import com.example.note.ui.account.AccountFragment;
    import com.example.note.ui.home.HomeFragment;
    import com.example.note.ui.settings.SettingsFragment;
    import com.example.note.ui.trash.TrashFragment;
    import com.google.android.material.bottomnavigation.BottomNavigationView;
    import com.google.android.material.floatingactionbutton.FloatingActionButton;
    import com.google.android.material.navigation.NavigationView;

    import java.util.ArrayList;
    import java.util.List;

    public class MainActivity extends AppCompatActivity {

        private AppBarConfiguration mAppBarConfiguration;
        private ActivityMainBinding binding;
        NavController navController;

        private final int FRAGMENT_HOME = 1;
        private final int FRAGMENT_ACCOUNT = 2;
        private final int FRAGMENT_TRASH = 3;
        private final int FRAGMENT_SETTING = 4;
        private final int FRAGMENT_LOGOUT = 5;
        private  int curentFragment = FRAGMENT_HOME;

        public String idSinhVien;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            Intent intent = getIntent();
            idSinhVien = ((Intent) intent).getStringExtra("ID_SINHVIEN");

            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            DrawerLayout drawer = binding.drawerLayout;
            NavigationView navigationView = binding.navView;


            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top-level destinations.
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_home, R.id.nav_account, R.id.nav_settings, R.id.nav_logout, R.id.nav_trash)
                    .setOpenableLayout(drawer)
                    .build();

            navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);



            // lấy id
    //        Toast.makeText(this, idSinhVien, Toast.LENGTH_SHORT).show();

            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int id = item.getItemId();

                    if (id == R.id.nav_home) {
                        Toast.makeText(MainActivity.this, "home", Toast.LENGTH_SHORT).show();

                        if (curentFragment != FRAGMENT_HOME) {
                            curentFragment = FRAGMENT_HOME;
                            navController.navigate(R.id.nav_home);
                        }
                    } else if (id == R.id.nav_account) {
                        Toast.makeText(MainActivity.this, "account", Toast.LENGTH_SHORT).show();

                        if (curentFragment != FRAGMENT_ACCOUNT) {
                            curentFragment = FRAGMENT_ACCOUNT;
                            navController.navigate(R.id.nav_account);
                        }
                    } else if (id == R.id.nav_trash) {
                        Toast.makeText(MainActivity.this, "trash", Toast.LENGTH_SHORT).show();

                        if (curentFragment != FRAGMENT_TRASH) {
                            curentFragment = FRAGMENT_TRASH;
                            navController.navigate(R.id.nav_trash);
                        }
                    } else if (id == R.id.nav_logout) {
                        Toast.makeText(MainActivity.this, "logout", Toast.LENGTH_SHORT).show();

                        if (curentFragment != FRAGMENT_LOGOUT) {
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }

                    }
                    DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);

                    return true;
                }
            });
        }

        @Override
        public boolean onSupportNavigateUp() {
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
        }

        public void setNightMode(int mode) {
            AppCompatDelegate.setDefaultNightMode(mode);
            recreate(); // Khởi động lại Activity để áp dụng chế độ mới
        }
    }
