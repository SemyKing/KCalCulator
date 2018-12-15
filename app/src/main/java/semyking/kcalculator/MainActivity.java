package semyking.kcalculator;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import semyking.kcalculator.helpers.BottomNavigationViewHelper;
import semyking.kcalculator.views.CustomViewPager;
import semyking.kcalculator.views.ViewPagerAdapter;
import semyking.kcalculator.views.charts.ChartsFragment;
import semyking.kcalculator.views.data.DataFragment;
import semyking.kcalculator.views.home.HomeFragment;
import semyking.kcalculator.views.settings.SettingsFragment;

public class MainActivity extends AppCompatActivity {

    private MenuItem prevMenuItem;
//    private ViewPager viewPager;
    private CustomViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
//        viewPager.setOffscreenPageLimit(3);

        final BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:


                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.navigation_chart:


                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.navigation_data:


                        viewPager.setCurrentItem(2);
                        break;
                    case R.id.navigation_settings:


                        viewPager.setCurrentItem(3);
                        break;
                }
                return false;
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);

                // Close keyboard on view change
                InputMethodManager imm = (InputMethodManager)viewPager.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(viewPager.getWindowToken(), 0);
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });


        setupViewPager(viewPager);

    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        HomeFragment        homeFragment        = new HomeFragment();
        ChartsFragment      chartsFragment      = new ChartsFragment();
        DataFragment        dataFragment        = new DataFragment();
        SettingsFragment    settingsFragment    = new SettingsFragment();

        viewPagerAdapter.addFragment(homeFragment);
        viewPagerAdapter.addFragment(chartsFragment);
        viewPagerAdapter.addFragment(dataFragment);
        viewPagerAdapter.addFragment(settingsFragment);

        viewPager.setAdapter(viewPagerAdapter);
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

}
