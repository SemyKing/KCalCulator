package semyking.kcalculator;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import semyking.kcalculator.views.BottomNavigationViewHelper;
import semyking.kcalculator.views.data.DataFragment;
import semyking.kcalculator.views.home.HomeFragment;

public class MainActivity extends AppCompatActivity {

    public TextView mWeekOfYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            //Restore the fragment's instance

//            fragment = getSupportFragmentManager().getFragment(savedInstanceState, "fragment");
//            fragmentTag = savedInstanceState.getString("fragmentTag");
            viewId = savedInstanceState.getInt("viewId");
        }

        setContentView(R.layout.activity_main);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
//        if (getSupportActionBar() != null)
//            getSupportActionBar().setTitle(R.string.week);

        mWeekOfYear = myToolbar.findViewById(R.id.weekNumber_textView);

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        if (viewId != 0)
            bottomNavigationView.setSelectedItemId(viewId);
        else
            bottomNavigationView.setSelectedItemId(R.id.navigation_home);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_toolbar_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case android.R.id.home:
//                onBackPressed();
//                break;
            case R.id.action_today:
                if (fragment instanceof HomeFragment) {
                    ((HomeFragment) fragment).selectToday();
                }
                break;
        }
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

    private Fragment fragment = null;
    private String fragmentTag;
    private int viewId;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    viewId = R.id.navigation_home;

                    fragment = new HomeFragment();
                    fragmentTag = HomeFragment.TAG;
//                    ((HomeFragment) fragment).setVars(getApplicationContext(), mWeekOfYear);

                    if (getSupportActionBar() != null)
                        getSupportActionBar().show();

                    switchFragment(fragment, fragmentTag);
//                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    return true;
                case R.id.navigation_chart:
                    viewId = R.id.navigation_chart;



                    return true;
                case R.id.navigation_data:
                    viewId = R.id.navigation_data;

                    fragment = new DataFragment();
                    fragmentTag = DataFragment.TAG;
                    if (getSupportActionBar() != null)
                        getSupportActionBar().hide();

                    switchFragment(fragment, fragmentTag);
//                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // RESETS ACTIVITY AND LAUNCHES HomeFragment
                    return true;

                case R.id.navigation_settings:


                    return true;
            }
            return false;
        }
    };

    private void switchFragment(Fragment newFragment, String tag) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, newFragment, tag);
        fragmentTransaction.commit();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Save the fragment's instance

//        getSupportFragmentManager().putFragment(outState, "fragment", fragment);
//        outState.putString("fragmentTag", fragmentTag);
        outState.putInt("viewId", viewId);

    }


}
