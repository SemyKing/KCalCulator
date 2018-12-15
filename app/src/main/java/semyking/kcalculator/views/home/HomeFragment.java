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
import com.shrikanthravi.collapsiblecalendarview.data.CalendarAdapter;
import com.shrikanthravi.collapsiblecalendarview.data.Day;
import semyking.kcalculator.R;
import semyking.kcalculator.database.KcalData;
import semyking.kcalculator.helpers.CustomCollapsibleCalendar;
import semyking.kcalculator.helpers.DataBaseHelper;

import java.util.Calendar;
import java.util.Locale;

public class HomeFragment extends Fragment {
    public static final String TAG = HomeFragment.class.getSimpleName();

    public HomeFragment() {}

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private DataBaseHelper dbHelper;
    private Calendar mCalendar;

    private CustomCollapsibleCalendar mCollapsibleCalendar;
    private Day mToday, mSelectedDay;

    private EditText mSpentKcal, mEatenKcal, mWeight, mKDay;
    private TextView kcalDifferenceTV;
    private TextView kcalDifferencePercentTV;
    private TextView mWeekOfYear;

    private Button saveButton;

    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstanceState ) {
        View view = layoutInflater.inflate(R.layout.home_layout, viewGroup, false);

        if (getActivity() != null) {
            dbHelper = new DataBaseHelper(getActivity());
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else
            Log.e("getActivity()", "getActivity() is null in HomeFragment() constructor");

//        dbHelper.deleteAllFromDB();

        mCalendar = Calendar.getInstance(Locale.GERMAN);
        mCalendar.set(Calendar.HOUR_OF_DAY, 0);
        mCalendar.set(Calendar.MINUTE, 0);
        mCalendar.set(Calendar.SECOND, 0);
        mCalendar.set(Calendar.MILLISECOND, 0);
        mToday = new Day(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH)); //year, month, day

        mCollapsibleCalendar = view.findViewById(R.id.calendarView);
        mCollapsibleCalendar.initCalendarSwipeGesture();
        mCollapsibleCalendar.setFirstDayOfWeek(1); //MONDAY

        mWeekOfYear = mCollapsibleCalendar.findViewById(R.id.mWeekOfYear);

        TextView calendar_today_TV = mCollapsibleCalendar.findViewById(R.id.calendar_today_TV);
        calendar_today_TV.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                selectToday();
            }
        });

        CalendarAdapter mAdapter = new CalendarAdapter(getActivity(), mCalendar);
        mAdapter.setFirstDayOfWeek(1);
        mAdapter.refresh();

        mCollapsibleCalendar.setAdapter(mAdapter);
        mCollapsibleCalendar.setCalendarListener(new CustomCollapsibleCalendar.CalendarListener() {
            @Override
            public void onDaySelect() {
                mSelectedDay = mCollapsibleCalendar.getSelectedDay();
                mCalendar.set(mSelectedDay.getYear(), mSelectedDay.getMonth(), mSelectedDay.getDay());

                mWeekOfYear.setText(String.valueOf(mCalendar.get(Calendar.WEEK_OF_YEAR)));

                getSelectedDayData();
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

        kcalDifferenceTV = view.findViewById(R.id.difference_TV);
        kcalDifferencePercentTV = view.findViewById(R.id.difference_percent_TV);

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
                mSpentKcal.requestFocus();
            }
        });
        view.findViewById(R.id.eatenInputLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEatenKcal.requestFocus();
            }
        });
        view.findViewById(R.id.weightInputLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWeight.requestFocus();
            }
        });
        view.findViewById(R.id.kdayInputLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mKDay.requestFocus();
            }
        });

        saveButton = view.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToDB();
            }
        });


//        System.out.println("---------savedInstanceState: "+savedInstanceState);
//        if (savedInstanceState != null) {
//            System.out.println("---------"+savedInstanceState.getBoolean("daySelected"));
//            if (savedInstanceState.getBoolean("daySelected")) {
//                Day d = new Day(savedInstanceState.getInt("year"), savedInstanceState.getInt("month"), savedInstanceState.getInt("day"));
//                System.out.println("---------"+d);
//                mCollapsibleCalendar.select(d);
//            }
//        } else {
//
//        }

        selectToday();

        System.out.println("----------HOME DRAWN");
        return view;
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        System.out.println("--------------------------SAVE INSTANCE");

        super.onSaveInstanceState(outState);
        outState.putBoolean("daySelected", true);
        outState.putInt("year", mSelectedDay.getYear());
        outState.putInt("month", mSelectedDay.getMonth());
        outState.putInt("day", mSelectedDay.getDay());

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
                kcalDifferenceTV.setText("");
                kcalDifferencePercentTV.setText("");
            } else
                calculateKcalDifference();
        }
    };

    private void selectToday() {
        mCollapsibleCalendar.select(mToday);

        if (mCollapsibleCalendar.getYear() != mToday.getYear()) {
            if (mCollapsibleCalendar.getYear() < mToday.getYear()) {
                do {
                    mCollapsibleCalendar.nextMonth();
                } while (mCollapsibleCalendar.getYear() != mToday.getYear());
            } else {
                do {
                    mCollapsibleCalendar.prevMonth();
                } while (mCollapsibleCalendar.getYear() != mToday.getYear());
            }
        }

        if (mCollapsibleCalendar.getMonth() != mToday.getMonth()) {
            if (mCollapsibleCalendar.getMonth() < mToday.getMonth()) {
                do {
                    mCollapsibleCalendar.nextMonth();
                } while (mCollapsibleCalendar.getMonth() != mToday.getMonth());
            } else {
                do {
                    mCollapsibleCalendar.prevMonth();
                } while (mCollapsibleCalendar.getMonth() != mToday.getMonth());
            }
        }

        mCollapsibleCalendar.collapseToToday();
    }

    private void calculateKcalDifference() {
        double spentD = Double.parseDouble(mSpentKcal.getText().toString().replaceAll(",", ".").replaceAll(" ", ""));
        double eatenD = Double.parseDouble(mEatenKcal.getText().toString().replaceAll(",", ".").replaceAll(" ", ""));
        float spentF = ((float) Math.round((((float) eatenD) - ((float) spentD)) * 10.0f)) / 10.0f;
        float eatenF = ((float) Math.round((((eatenD / spentD) * 100.0d) - 100) * 10.0d)) / 10.0f;

        if (spentF > 0.0f) {
            this.kcalDifferenceTV.setTextColor(getResources().getColor(R.color.colorRed));
        } else {
            this.kcalDifferenceTV.setTextColor(getResources().getColor(R.color.colorGreen));
        }

        if (eatenF > 0.0f) {
            this.kcalDifferencePercentTV.setTextColor(getResources().getColor(R.color.colorRed));
        } else {
            this.kcalDifferencePercentTV.setTextColor(getResources().getColor(R.color.colorGreen));
        }

        kcalDifferenceTV.setText(String.valueOf(spentF));

        String difPercent = String.valueOf(eatenF) + " %";
        kcalDifferencePercentTV.setText(difPercent);
    }

    private void saveToDB() {
        String selectedDateStr = mSelectedDay.getDay()+"/"+(mSelectedDay.getMonth() +1)+"/"+mSelectedDay.getYear();
        long selectedDateLong = mCalendar.getTimeInMillis();

        dbHelper.saveToDB(selectedDateStr, selectedDateLong, mEatenKcal.getText().toString(), mSpentKcal.getText().toString(), mWeight.getText().toString(),
                mKDay.getText().toString(), kcalDifferenceTV.getText().toString(), kcalDifferencePercentTV.getText().toString());

        // Close keyboard
        InputMethodManager imm = (InputMethodManager)saveButton.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(saveButton.getWindowToken(), 0);
        }
    }

    private void getSelectedDayData() {
        KcalData kd = dbHelper.getSelectedDayData(mCalendar.getTimeInMillis());

        if (kd != null) {
            mSpentKcal.setText(kd.getSpentKcal());
            mEatenKcal.setText(kd.getEatenKcal());
            mWeight.setText(kd.getWeight());
            mKDay.setText(kd.getKDay());
        } else {
            mSpentKcal.setText("");
            mEatenKcal.setText("");
            mWeight.setText("");
            mKDay.setText("");
        }
    }
}
