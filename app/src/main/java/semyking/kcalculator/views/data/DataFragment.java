package semyking.kcalculator.views.data;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import semyking.kcalculator.R;

import java.util.ArrayList;

public class DataFragment extends Fragment {

    public static final String TAG = DataFragment.class.getSimpleName();

    public DataFragment() {}

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View view = layoutInflater.inflate(R.layout.data_layout, viewGroup, false);


        return view;
    }







    public class DataListAdapter extends BaseAdapter implements ListAdapter {

        private final Context context;
        private final ArrayList<String> DB_data;
        private LayoutInflater inflater = null;

        private DataListAdapter(Context context, ArrayList<String> DB_data){
            this.context=context;
            this.DB_data= DB_data;
        }

        @Override
        public int getCount() {
            return DB_data.size();
        }

        @Override
        public Object getItem(int position) {
            return DB_data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;

            if (view == null){
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.data_row, parent, false);
            }

            TextView data_date =                    view.findViewById(R.id.data_date);
            TextView data_spentKcal =               view.findViewById(R.id.data_spentKcal);
            TextView data_eatenKckal =              view.findViewById(R.id.data_eatenKckal);
            TextView data_weight =                  view.findViewById(R.id.data_weight);
            TextView data_kcalDifference =          view.findViewById(R.id.data_kcalDifference);
            TextView data_kcalDifferencePercent =   view.findViewById(R.id.data_kcalDifferencePercent);
            TextView data_kday =                    view.findViewById(R.id.data_kday);





            return null;
        }
    }

}
