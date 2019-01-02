package semyking.kcalculator.views.home;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.github.mikephil.charting.charts.LineChart;
import com.shrikanthravi.collapsiblecalendarview.data.CalendarAdapter;
import com.shrikanthravi.collapsiblecalendarview.data.Day;
import semyking.kcalculator.MainActivity;
import semyking.kcalculator.R;
import semyking.kcalculator.database.KcalData;
import semyking.kcalculator.helpers.CalendarHelper;
import semyking.kcalculator.helpers.ChartHelper;
import semyking.kcalculator.helpers.CustomCollapsibleCalendar;
import semyking.kcalculator.helpers.DataBaseHelper;
import semyking.kcalculator.views.DataStorage;

import java.util.Calendar;

public class HomeFragment extends Fragment {
    public static final String TAG = HomeFragment.class.getSimpleName();

    public HomeFragment() {

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private DataStorage mDataStorage;

    private DataBaseHelper mDbHelper;
    private Calendar mCalendar;
    private LineChart lineChart;
    private ChartHelper mChartHelper;

    private CustomCollapsibleCalendar mCollapsibleCalendar;
    private Day mToday, mSelectedDay;

    private TextInputEditText mSpentKcal, mEatenKcal, mWeight, mKDay;
    private TextView mKcalDifferenceTV, mKcalDifferencePercentTV;

    private Button mSaveButton;

    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstanceState ) {
        View view = layoutInflater.inflate(R.layout.home_layout, viewGroup, false);

        if (getActivity() != null) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            mDbHelper = DataBaseHelper.getInstance(getActivity());
            mDataStorage = ((MainActivity) getActivity()).getDataStorage();
        } else
            Log.e("getActivity()", "getActivity() is null in HomeFragment()");


        mCalendar = CalendarHelper.getCalendar();
        mToday = new Day(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH)); //year, month, day

        mCollapsibleCalendar = view.findViewById(R.id.calendarView);
        mCollapsibleCalendar.initCalendarSwipeGesture();
        mCollapsibleCalendar.setFirstDayOfWeek(1); //MONDAY

        final TextView weekOfYearTextView = mCollapsibleCalendar.findViewById(R.id.weekOfYearTextView);

        final TextView calendarTodayTextView = mCollapsibleCalendar.findViewById(R.id.calendarTodayTextView);
        calendarTodayTextView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                scrollToDay(mToday);
            }
        });

        final CalendarAdapter calendarAdapter = new CalendarAdapter(getActivity(), mCalendar);
        calendarAdapter.setFirstDayOfWeek(1);
        calendarAdapter.refresh();

        mCollapsibleCalendar.setAdapter(calendarAdapter);
        mCollapsibleCalendar.setCalendarListener(new CustomCollapsibleCalendar.CalendarListener() {
            @Override
            public void onDaySelect() {
                mSelectedDay = mCollapsibleCalendar.getSelectedDay();
                mDataStorage.setSelectedDay(mSelectedDay);
                mCalendar.set(mSelectedDay.getYear(), mSelectedDay.getMonth(), mSelectedDay.getDay());
                weekOfYearTextView.setText(String.valueOf(mCalendar.get(Calendar.WEEK_OF_YEAR)));

                setSelectedDayData();
            }
            @Override
            public void onItemClick(View view) {
            }
            @Override
            public void onDataUpdate() {
            }
            @Override
            public void onMonthChange() {
            }
            @Override
            public void onWeekChange(int i) {
            }
        });

        mKcalDifferenceTV = view.findViewById(R.id.difference_TV);
        mKcalDifferencePercentTV = view.findViewById(R.id.difference_percent_TV);

        mSpentKcal = view.findViewById(R.id.spent_EditText);
        mSpentKcal.setOnEditorActionListener(mEditTextListener);
        mSpentKcal.addTextChangedListener(textWatcher);

        mEatenKcal = view.findViewById(R.id.eaten_EditText);
        mEatenKcal.setOnEditorActionListener(mEditTextListener);
        mEatenKcal.addTextChangedListener(textWatcher);

        mWeight = view.findViewById(R.id.weight_EditText);
        mWeight.setOnEditorActionListener(mEditTextListener);

        mKDay = view.findViewById(R.id.kday_EditText);
        mKDay.setOnEditorActionListener(mEditTextListener);

        view.findViewById(R.id.spentInputLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAndOpenKeyboard(mSpentKcal);
            }
        });
        view.findViewById(R.id.eatenInputLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAndOpenKeyboard(mEatenKcal);
            }
        });
        view.findViewById(R.id.weightInputLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAndOpenKeyboard(mWeight);
            }
        });
        view.findViewById(R.id.kdayInputLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAndOpenKeyboard(mKDay);
            }
        });

        mSaveButton = view.findViewById(R.id.saveButton);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToDB();
            }
        });


        //LineChart
        mChartHelper = new ChartHelper(getActivity());
        lineChart = view.findViewById(R.id.lineChart);
        lineChart = mChartHelper.initLineChart(lineChart);

        if (mDataStorage != null) {
            if (mDataStorage.getSelectedDay() != null) {
                mCollapsibleCalendar.select(mDataStorage.getSelectedDay());
                scrollToDay(mSelectedDay);
            } else
                scrollToDay(mToday);
        }

        Log.d("viewLoaded", "HomeFragment");
        return view;
    }

    private void selectAndOpenKeyboard(TextInputEditText textInputEditText) {
        textInputEditText.requestFocus();
        InputMethodManager imm = (InputMethodManager) mSaveButton.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        System.out.println("--------------------------SAVE INSTANCE HomeFragment");
        super.onSaveInstanceState(outState);
    }

    private EditText.OnEditorActionListener mEditTextListener = new EditText.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                if (getView() != null) {
                    TextInputEditText next = getView().findViewById(v.getNextFocusForwardId());
                    if (next != null)
                        next.requestFocus();
                }
                return true;

            } else if (actionId == EditorInfo.IME_ACTION_DONE) {
                saveToDB();
                return true;
            }
            return false;
        }
    };

    private TextWatcher textWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {}
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (mEatenKcal.getText().length() <= 0 || mSpentKcal.getText().length() <= 0) {
                mKcalDifferenceTV.setText("");
                mKcalDifferencePercentTV.setText("");
            } else
                calculateKcalDifference();
        }
    };

    private void scrollToDay(Day day) {
        mCollapsibleCalendar.select(day);

        if (mCollapsibleCalendar.getYear() != day.getYear()) {
            if (mCollapsibleCalendar.getYear() < day.getYear()) {
                do {
                    mCollapsibleCalendar.nextMonth();
                } while (mCollapsibleCalendar.getYear() != day.getYear());
            } else {
                do {
                    mCollapsibleCalendar.prevMonth();
                } while (mCollapsibleCalendar.getYear() != day.getYear());
            }
        }

        if (mCollapsibleCalendar.getMonth() != day.getMonth()) {
            if (mCollapsibleCalendar.getMonth() < day.getMonth()) {
                do {
                    mCollapsibleCalendar.nextMonth();
                } while (mCollapsibleCalendar.getMonth() != day.getMonth());
            } else {
                do {
                    mCollapsibleCalendar.prevMonth();
                } while (mCollapsibleCalendar.getMonth() != day.getMonth());
            }
        }

        mCollapsibleCalendar.collapseToSelectedDay();
    }

    private void calculateKcalDifference() {
        double spentD = Double.parseDouble(mSpentKcal.getText().toString().replaceAll(",", ".").replaceAll(" ", ""));
        double eatenD = Double.parseDouble(mEatenKcal.getText().toString().replaceAll(",", ".").replaceAll(" ", ""));
        float dif = ((float) Math.round((((float) eatenD) - ((float) spentD)) * 10.0f)) / 10.0f;
        float difPercent = ((float) Math.round((((eatenD / spentD) * 100.0d) - 100) * 10.0d)) / 10.0f;

        if (dif > 0.0f) {
            this.mKcalDifferenceTV.setTextColor(getResources().getColor(R.color.colorRed));
        } else {
            this.mKcalDifferenceTV.setTextColor(getResources().getColor(R.color.colorGreen));
        }

        if (difPercent > 0.0f) {
            this.mKcalDifferencePercentTV.setTextColor(getResources().getColor(R.color.colorRed));
        } else {
            this.mKcalDifferencePercentTV.setTextColor(getResources().getColor(R.color.colorGreen));
        }

        mKcalDifferenceTV.setText(String.valueOf(dif));
        mKcalDifferencePercentTV.setText(String.valueOf(difPercent));
    }

    private void saveToDB() {
        String selectedDateStr = CalendarHelper.formatFullDate(mCalendar.getTime());
        long selectedDateLong = mCalendar.getTimeInMillis();

        System.out.println("selectedDateStr: "+selectedDateStr+", selectedDateLong: "+selectedDateLong);

        mDbHelper.saveToDB(selectedDateStr, selectedDateLong, mEatenKcal.getText().toString(), mSpentKcal.getText().toString(), mWeight.getText().toString(),
                mKDay.getText().toString(), mKcalDifferenceTV.getText().toString(), mKcalDifferencePercentTV.getText().toString());

        // Close keyboard
        InputMethodManager imm = (InputMethodManager) mSaveButton.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(mSaveButton.getWindowToken(), 0);
        }

        if (getActivity() != null)
            Toast.makeText(getActivity(), R.string.saved, Toast.LENGTH_SHORT).show();
    }

    private void setSelectedDayData() {
        KcalData kd = mDbHelper.getSelectedDayData(mCalendar.getTimeInMillis());

        mSpentKcal.setText(kd.getSpentKcal());
        mEatenKcal.setText(kd.getEatenKcal());
        mWeight.setText(kd.getWeight());
        mKDay.setText(kd.getKDay());

        mChartHelper.updateChartData(lineChart, null, null);
        lineChart.invalidate();
    }
}
