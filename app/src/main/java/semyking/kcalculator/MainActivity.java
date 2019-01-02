package semyking.kcalculator;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import semyking.kcalculator.helpers.BottomNavigationViewHelper;
import semyking.kcalculator.helpers.DataBaseHelper;
import semyking.kcalculator.views.DataStorage;
import semyking.kcalculator.views.charts.ChartsFragment;
import semyking.kcalculator.views.data.DataFragment;
import semyking.kcalculator.views.home.HomeFragment;
import semyking.kcalculator.views.settings.SettingsFragment;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private DataStorage dataStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            viewId = savedInstanceState.getInt("viewId");
        }

        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        dataStorage = ViewModelProviders.of(this).get(DataStorage.class);

        final DataBaseHelper dbHelper = DataBaseHelper.getInstance(this);

        if (viewId != 0)
            bottomNavigationView.setSelectedItemId(viewId);
        else
            bottomNavigationView.setSelectedItemId(R.id.navigation_home);

        File appDir = new File(this.getExternalFilesDir(null), "KcalCulator");

        if (!appDir.exists()) {
            if (!appDir.mkdirs()) {
                Log.e("ERROR", "MainActivity, cannot create subdirectory 'KcalCulator' for exports");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_toolbar_items, menu);
        return true;
    }

    private boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            this.finish();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.exit_double_click, Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 3000);
    }

    private int viewId;
    private HomeFragment homeFragment = new HomeFragment();
    private ChartsFragment chartsFragment = new ChartsFragment();
    private DataFragment dataFragment = new DataFragment();
    private SettingsFragment settingsFragment = new SettingsFragment();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        MenuItem prevItem = null;
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            if (prevItem == item) {
                return false;
            }
            prevItem = item;

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    viewId = R.id.navigation_home;
                    switchFragment(homeFragment, HomeFragment.TAG);
                    return true;
                case R.id.navigation_chart:
                    viewId = R.id.navigation_chart;
                    switchFragment(chartsFragment, ChartsFragment.TAG);
                    return true;
                case R.id.navigation_data:
                    viewId = R.id.navigation_data;
                    switchFragment(dataFragment, DataFragment.TAG);
                    return true;

                case R.id.navigation_settings:
                    viewId = R.id.navigation_settings;
                    switchFragment(settingsFragment, SettingsFragment.TAG);
                    return true;
            }
            return false;
        }
    };

    private void switchFragment(Fragment newFragment, String tag) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, newFragment, tag);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("viewId", viewId);
    }


    public DataStorage getDataStorage() {
        return dataStorage;
    }
}