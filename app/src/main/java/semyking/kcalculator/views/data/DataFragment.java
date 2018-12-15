package semyking.kcalculator.views.data;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import semyking.kcalculator.R;
import semyking.kcalculator.database.KcalData;
import semyking.kcalculator.helpers.DataBaseHelper;

import java.util.List;

public class DataFragment extends Fragment {
    public static final String TAG = DataFragment.class.getSimpleName();

    public DataFragment() {
    }

    private DataBaseHelper dataBaseHelper;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View view = layoutInflater.inflate(R.layout.data_layout, viewGroup, false);

        if (getActivity() != null) {
            dataBaseHelper = new DataBaseHelper(getActivity());
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

            ProgressDialog progressDialog;
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setIndeterminate(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(true);
            progressDialog.show();

            do {
                //WAIT FOR DATA TO LOAD FROM DB
            } while (!dataBaseHelper.isAllDataLoaded());

            progressDialog.dismiss();
            DataListAdapter adapter = new DataListAdapter(getActivity(), dataBaseHelper.getAllData());

            ListView listView = view.findViewById(R.id.data_listView);
            listView.setAdapter(adapter);
        } else
            Log.e("getActivity()", "getActivity() is null in DataFragment onCreateView()");


        System.out.println("----------DATA DRAWN");
        return view;
    }







    public class DataListAdapter extends ArrayAdapter<KcalData> {

        private final List<KcalData> DB_data;

        private DataListAdapter(Context context, List<KcalData> DB_data){
            super(context, 0, DB_data);
            this.DB_data = DB_data;
        }

        @Override
        public int getCount() {
            return DB_data.size();
        }

        @Override
        public KcalData getItem(int position) {
            return DB_data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            KcalData kd = getItem(position);

            if (convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.data_row, parent, false);
            }

            TextView data_date =                    convertView.findViewById(R.id.data_date);
            TextView data_spentKcal =               convertView.findViewById(R.id.data_spentKcal);
            TextView data_eatenKcal =               convertView.findViewById(R.id.data_eatenKcal);
            TextView data_weight =                  convertView.findViewById(R.id.data_weight);
            TextView data_kcalDifference =          convertView.findViewById(R.id.data_kcalDifference);
            TextView data_kcalDifferencePercent =   convertView.findViewById(R.id.data_kcalDifferencePercent);
            TextView data_kday =                    convertView.findViewById(R.id.data_kday);

            data_date.setText(      kd.getDate_string());
            data_spentKcal.setText( kd.getSpentKcal());
            data_eatenKcal.setText(kd.getEatenKcal());
            data_weight.setText(    kd.getWeight());

            setDifference( data_kcalDifference, kd.getKcalDifference() );
            setDifference( data_kcalDifferencePercent, kd.getKcalDifferencePercent() );

            data_kday.setText(      kd.getKDay());

            return convertView;
        }
    }

    private void setDifference(TextView tv, String val) {
        tv.setText(val);
        if (val.length() > 0) {
            val = val.replaceAll(" ", "").replaceAll("%", "");
            float valF = Float.parseFloat(val);

            if (valF > 0.0f) {
                tv.setTextColor(getResources().getColor(R.color.colorRed));
            } else {
                tv.setTextColor(getResources().getColor(R.color.colorGreen));
            }
        }
    }

}
