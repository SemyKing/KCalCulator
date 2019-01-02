package semyking.kcalculator.views.charts;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.shrikanthravi.collapsiblecalendarview.data.Day;
import semyking.kcalculator.MainActivity;
import semyking.kcalculator.R;
import semyking.kcalculator.database.KcalData;
import semyking.kcalculator.helpers.CalendarHelper;
import semyking.kcalculator.helpers.ChartHelper;
import semyking.kcalculator.views.DataStorage;

import java.util.Calendar;

public class ChartsFragment extends Fragment {
    public static final String TAG = ChartsFragment.class.getSimpleName();

    public ChartsFragment() {}

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    private DataStorage mDataStorage;
    private ChartHelper mChartHelper;

    private LineChart mLineChart;
    private Calendar mFromCalendar, mToCalendar;

    private TextInputEditText mDateFromEditText, mDateToEditText;

    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View view = layoutInflater.inflate(R.layout.charts_layout, viewGroup, false);

        mDateFromEditText = view.findViewById(R.id.dateFromEditText);
        mDateToEditText = view.findViewById(R.id.dateToEditText);

        if (getActivity() != null) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            mDataStorage = ((MainActivity) getActivity()).getDataStorage();
            mChartHelper = new ChartHelper(getActivity());
        } else
            Log.e("ERROR", "getActivity() is null in ChartsFragment()");

        mFromCalendar = CalendarHelper.getCalendar();
        mToCalendar = CalendarHelper.getCalendar();

        final TextView selectedChartEntryTextView = view.findViewById(R.id.selectedChartEntryTextView);

        mLineChart = view.findViewById(R.id.customLineChart);
        mLineChart = mChartHelper.initLineChart(mLineChart);
        mLineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                KcalData selectedObj;

                if (e.getData() instanceof KcalData) {
                    selectedObj = ((KcalData) e.getData());
                } else
                    return;

                String selected = getActivity().getString(R.string.date)+": "+
                        selectedObj.getDate_string()+",    "+getActivity().getString(R.string.weight)+": "+
                        selectedObj.getWeight()+",    "+getActivity().getString(R.string.kcal_difference_percent)+": "+
                        selectedObj.getKcalDifferencePercent();

                selectedChartEntryTextView.setText(selected);
            }
            @Override
            public void onNothingSelected() {
            }
        });

        final AppCompatImageView btn_from_prev_month = view.findViewById(R.id.btn_from_prev_month);
        btn_from_prev_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevMonth(mDateFromEditText, mFromCalendar, false);
                updateChart();
            }
        });

        final AppCompatImageView btn_from_next_month = view.findViewById(R.id.btn_from_next_month);
        btn_from_next_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextMonth(mDateFromEditText, mFromCalendar, false);
                updateChart();
            }
        });

        final AppCompatImageView btn_to_prev_month = view.findViewById(R.id.btn_to_prev_month);
        btn_to_prev_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevMonth(mDateToEditText, mToCalendar, true);
                updateChart();
            }
        });

        final AppCompatImageView btn_to_next_month = view.findViewById(R.id.btn_to_next_month);
        btn_to_next_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextMonth(mDateToEditText, mToCalendar ,true);
                updateChart();
            }
        });


        if (bundle != null) {
            if (bundle.getBoolean("ChartsFragment")) {
                mFromCalendar.setTimeInMillis(bundle.getLong("mFromCalendar.getTimeInMillis()"));
                mToCalendar.setTimeInMillis(bundle.getLong("mToCalendar.getTimeInMillis()"));
            }
        } else {
            if (mDataStorage != null) {
                if (mDataStorage.getFromCalDate() != null) {
                    mFromCalendar.setTimeInMillis(mDataStorage.getFromCalDate());
                    mToCalendar.setTimeInMillis(mDataStorage.getToCalDate());
                } else {
                    Day date = mDataStorage.getSelectedDay();

                    if (date != null) {
                        mFromCalendar.set(date.getYear(), date.getMonth(), 1);

                        mToCalendar.set(Calendar.YEAR, date.getYear());
                        mToCalendar.set(Calendar.MONTH, date.getMonth());
                        mToCalendar.set(Calendar.DAY_OF_MONTH, mToCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                    }
                }
            }
        }

        mDateFromEditText.setText(CalendarHelper.formatFullDate(mFromCalendar.getTime()));
        mDateToEditText.setText(CalendarHelper.formatFullDate(mToCalendar.getTime()));

        updateChart();

        Log.d("viewLoaded", "ChartsFragment");
        return view;
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        System.out.println("--------------------------SAVE INSTANCE ChartsFragment");
        super.onSaveInstanceState(outState);
        if (mFromCalendar != null && mToCalendar != null) {
            outState.putBoolean("ChartsFragment", true);
            outState.putLong("mFromCalendar.getTimeInMillis()", mFromCalendar.getTimeInMillis());
            outState.putLong("mToCalendar.getTimeInMillis()", mToCalendar.getTimeInMillis());
        }
    }

    private void prevMonth(EditText editText, Calendar calendar, boolean to) {
        calendar.set(Calendar.MONTH, (calendar.get(Calendar.MONTH)-1));
        if (to) {
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        }
        editText.setText(CalendarHelper.formatFullDate(calendar.getTime()));
    }

    private void nextMonth(EditText editText, Calendar calendar, boolean to) {
        calendar.set(Calendar.MONTH, (calendar.get(Calendar.MONTH)+1));
        if (to) {
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        }
        editText.setText(CalendarHelper.formatFullDate(calendar.getTime()));
    }

    private void updateChart() {
        mDataStorage.setFromCalDate(mFromCalendar.getTimeInMillis());
        mDataStorage.setToCalDate(mToCalendar.getTimeInMillis());

        mChartHelper.updateChartData(mLineChart, mFromCalendar, mToCalendar);
        mLineChart.invalidate();
    }
}
