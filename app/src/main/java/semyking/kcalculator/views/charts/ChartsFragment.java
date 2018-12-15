package semyking.kcalculator.views.charts;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import semyking.kcalculator.R;

public class ChartsFragment extends Fragment {
    public static final String TAG = ChartsFragment.class.getSimpleName();

    public ChartsFragment() {}

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View view = layoutInflater.inflate(R.layout.charts_layout, viewGroup, false);


        System.out.println("----------CHARTS DRAWN");
        return view;
    }
}
