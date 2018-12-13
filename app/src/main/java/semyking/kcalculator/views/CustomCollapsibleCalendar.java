package semyking.kcalculator.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.TableRow;
import android.widget.TextView;
import com.shrikanthravi.collapsiblecalendarview.R.id;
import com.shrikanthravi.collapsiblecalendarview.R.layout;
import com.shrikanthravi.collapsiblecalendarview.data.CalendarAdapter;
import com.shrikanthravi.collapsiblecalendarview.data.Day;
import com.shrikanthravi.collapsiblecalendarview.data.Event;
import com.shrikanthravi.collapsiblecalendarview.widget.UICalendar;
import semyking.kcalculator.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CustomCollapsibleCalendar extends UICalendar {
    private CalendarAdapter mAdapter;
    private CustomCollapsibleCalendar.CalendarListener mListener;
    private boolean expanded = false;
    private int mInitHeight = 0;
    private Handler mHandler = new Handler();
    private boolean mIsWaitingForUpdate = false;
    private int mCurrentWeekIndex;
    private OnSwipeTouchListener mOnSwipeTouchListener;

    public CustomCollapsibleCalendar(Context context) {
        super(context);
    }

    public CustomCollapsibleCalendar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomCollapsibleCalendar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void init(Context context) {
        super.init(context);
        Calendar cal = Calendar.getInstance();
        CalendarAdapter adapter = new CalendarAdapter(context, cal);
        this.setAdapter(adapter);
        this.mBtnPrevMonth.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                CustomCollapsibleCalendar.this.prevMonth();
            }
        });
        this.mBtnNextMonth.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                CustomCollapsibleCalendar.this.nextMonth();
            }
        });
        this.mBtnPrevWeek.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                CustomCollapsibleCalendar.this.prevWeek();
            }
        });
        this.mBtnNextWeek.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                CustomCollapsibleCalendar.this.nextWeek();
            }
        });
        this.expandIconView.setState(0, true);
        this.expandIconView.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (CustomCollapsibleCalendar.this.expanded) {
                    CustomCollapsibleCalendar.this.collapse(400);
                } else {
                    CustomCollapsibleCalendar.this.expand(400);
                }

                CustomCollapsibleCalendar.this.expanded = !CustomCollapsibleCalendar.this.expanded;
            }
        });
        this.post(new Runnable() {
            public void run() {
                CustomCollapsibleCalendar.this.collapseTo(CustomCollapsibleCalendar.this.mCurrentWeekIndex);
            }
        });
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.mInitHeight = this.mTableBody.getMeasuredHeight();
        if (this.mIsWaitingForUpdate) {
            this.redraw();
            this.mHandler.post(new Runnable() {
                public void run() {
                    CustomCollapsibleCalendar.this.collapseTo(CustomCollapsibleCalendar.this.mCurrentWeekIndex);
                }
            });
            this.mIsWaitingForUpdate = false;
            if (this.mListener != null) {
                this.mListener.onDataUpdate();
            }
        }

    }

    protected void redraw() {
        TableRow rowWeek = (TableRow)this.mTableHead.getChildAt(0);
        int i;
        if (rowWeek != null) {
            for(i = 0; i < rowWeek.getChildCount(); ++i) {
                ((TextView)rowWeek.getChildAt(i)).setTextColor(this.getTextColor());
            }
        }

        if (this.mAdapter != null) {
            for(i = 0; i < this.mAdapter.getCount(); ++i) {
                Day day = this.mAdapter.getItem(i);
                View view = this.mAdapter.getView(i);
                TextView txtDay = view.findViewById(id.txt_day);
                txtDay.setBackgroundColor(0);
                txtDay.setTextColor(this.getTextColor());
                if (this.isToady(day)) {
                    txtDay.setBackgroundDrawable(this.getTodayItemBackgroundDrawable());
                    txtDay.setTextColor(this.getTodayItemTextColor());
                }

                if (this.isSelectedDay(day)) {
                    txtDay.setBackgroundDrawable(this.getSelectedItemBackgroundDrawable());
                    txtDay.setTextColor(this.getSelectedItemTextColor());
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void initCalendarSwipeGesture() {
        mOnSwipeTouchListener = new OnSwipeTouchListener(mContext) {
            public boolean onSwipeTop() {
//                Toast.makeText(mContext, "top", Toast.LENGTH_SHORT).show();

                if (getState() == 0) {
                    collapse(400);
                }
                return true;
            }
            public boolean onSwipeRight() {
//                Toast.makeText(mContext, "right", Toast.LENGTH_SHORT).show();

                if (getState() == 0)
                    prevMonth();
                else
                    prevWeek();

                return true;
            }
            public boolean onSwipeLeft() {
//                Toast.makeText(mContext, "left", Toast.LENGTH_SHORT).show();

                if (getState() == 0)
                    nextMonth();
                else
                    nextWeek();

                return true;
            }
            public boolean onSwipeBottom() {
//                Toast.makeText(mContext, "bottom", Toast.LENGTH_SHORT).show();

                if (getState() == 1)
                    expand(400);

                return true;
            }
        };
    }

    protected void reload() {
        int i;
        if (this.mAdapter != null) {
            this.mAdapter.refresh();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM yyyy");
            dateFormat.setTimeZone(this.mAdapter.getCalendar().getTimeZone());
            this.mTxtTitle.setText(dateFormat.format(this.mAdapter.getCalendar().getTime()));
            this.mTableHead.removeAllViews();
            this.mTableBody.removeAllViews();
            int[] dayOfWeekIds = new int[]{R.string.sunday, R.string.monday, R.string.tuesday, R.string.wednesday, R.string.thursday, R.string.friday, R.string.saturday};
            TableRow rowCurrent = new TableRow(this.mContext);
            rowCurrent.setLayoutParams(new LayoutParams(-1, -2));

            for(i = 0; i < 7; ++i) {
                View view = this.mInflater.inflate(layout.layout_day_of_week, null);
                TextView txtDayOfWeek = view.findViewById(id.txt_day_of_week);
                txtDayOfWeek.setText(dayOfWeekIds[(i + this.getFirstDayOfWeek()) % 7]);
                view.setLayoutParams(new android.widget.TableRow.LayoutParams(0, -2, 1.0F));
                rowCurrent.addView(view);
            }

            this.mTableHead.addView(rowCurrent);

            for(i = 0; i < this.mAdapter.getCount(); ++i) {
                if (i % 7 == 0) {
                    rowCurrent = new TableRow(this.mContext);
                    rowCurrent.setLayoutParams(new LayoutParams(-1, -2));
                    this.mTableBody.addView(rowCurrent);
                }

                View view = this.mAdapter.getView(i);
                view.setLayoutParams(new android.widget.TableRow.LayoutParams(0, -2, 1.0F));
                final int finalI = i;
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CustomCollapsibleCalendar.this.onItemClicked(v, CustomCollapsibleCalendar.this.mAdapter.getItem(finalI));
                    }
                });
                rowCurrent.addView(view);

                view.setOnTouchListener(mOnSwipeTouchListener);
            }

            this.redraw();
            this.mIsWaitingForUpdate = true;
        }
    }

    private int getSuitableRowIndex() {
        View view;
        TableRow row;
        if (this.getSelectedItemPosition() != -1) {
            view = this.mAdapter.getView(this.getSelectedItemPosition());
            row = (TableRow)view.getParent();
            return this.mTableBody.indexOfChild(row);
        } else if (this.getTodayItemPosition() != -1) {
            view = this.mAdapter.getView(this.getTodayItemPosition());
            row = (TableRow)view.getParent();
            return this.mTableBody.indexOfChild(row);
        } else {
            return 0;
        }
    }

    public void onItemClicked(View view, Day day) {
        this.select(day);
        Calendar cal = this.mAdapter.getCalendar();
        int newYear = day.getYear();
        int newMonth = day.getMonth();
        int oldYear = cal.get(Calendar.YEAR);
        int oldMonth = cal.get(Calendar.MONTH);
        if (newMonth != oldMonth) {
            cal.set(day.getYear(), day.getMonth(), 1);
            if (newYear > oldYear || newMonth > oldMonth) {
                this.mCurrentWeekIndex = 0;
            }

            if (newYear < oldYear || newMonth < oldMonth) {
                this.mCurrentWeekIndex = -1;
            }

            if (this.mListener != null) {
                this.mListener.onMonthChange();
            }

            this.reload();
        }

        if (this.mListener != null) {
            this.mListener.onItemClick(view);
        }

    }

    public void setAdapter(CalendarAdapter adapter) {
        this.mAdapter = adapter;
        adapter.setFirstDayOfWeek(this.getFirstDayOfWeek());
        this.reload();
        this.mCurrentWeekIndex = this.getSuitableRowIndex();
    }

    public void addEventTag(int numYear, int numMonth, int numDay) {
        this.mAdapter.addEvent(new Event(numYear, numMonth, numDay));
        this.reload();
    }

    public void prevMonth() {
        Calendar cal = this.mAdapter.getCalendar();
        if (cal.get(Calendar.MONTH) == cal.getActualMinimum(Calendar.MONTH)) {
            cal.set(cal.get(Calendar.YEAR) - 1, cal.getActualMaximum(Calendar.MONTH), 1);
        } else {
            cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
        }

        this.reload();
        if (this.mListener != null) {
            this.mListener.onMonthChange();
        }

    }

    public void nextMonth() {
        Calendar cal = this.mAdapter.getCalendar();
        if (cal.get(Calendar.MONTH) == cal.getActualMaximum(Calendar.MONTH)) {
            cal.set(cal.get(Calendar.YEAR) + 1, cal.getActualMinimum(Calendar.MONTH), 1);
        } else {
            cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1);
        }

        this.reload();
        if (this.mListener != null) {
            this.mListener.onMonthChange();
        }

    }

    public void prevWeek() {
        if (this.mCurrentWeekIndex - 1 < 0) {
            this.mCurrentWeekIndex = -1;
            this.prevMonth();
        } else {
            --this.mCurrentWeekIndex;
            this.collapseTo(this.mCurrentWeekIndex);
        }

    }

    public void nextWeek() {
        if (this.mCurrentWeekIndex + 1 >= this.mTableBody.getChildCount()) {
            this.mCurrentWeekIndex = 0;
            this.nextMonth();
        } else {
            ++this.mCurrentWeekIndex;
            this.collapseTo(this.mCurrentWeekIndex);
        }

    }

    public int getYear() {
        return this.mAdapter.getCalendar().get(Calendar.YEAR);
    }

    public int getMonth() {
        return this.mAdapter.getCalendar().get(Calendar.MONTH);
    }

    public Day getSelectedDay() {
        if (this.getSelectedItem() == null) {
            Calendar cal = Calendar.getInstance();
//            int day = cal.get(5);
            int day = cal.get(Calendar.DATE);
            int month = cal.get(Calendar.MONTH);
            int year = cal.get(Calendar.YEAR);
            return new Day(year, month + 1, day);
        } else {
            return new Day(this.getSelectedItem().getYear(), this.getSelectedItem().getMonth(), this.getSelectedItem().getDay());
        }
    }

    public boolean isSelectedDay(Day day) {
        return day != null && this.getSelectedItem() != null && day.getYear() == this.getSelectedItem().getYear() && day.getMonth() == this.getSelectedItem().getMonth() && day.getDay() == this.getSelectedItem().getDay();
    }

    public boolean isToady(Day day) {
        Calendar todayCal = Calendar.getInstance();
        return day != null && day.getYear() == todayCal.get(Calendar.YEAR) && day.getMonth() == todayCal.get(Calendar.MONTH) && day.getDay() == todayCal.get(Calendar.DATE);
    }

    public int getSelectedItemPosition() {
        int position = -1;

        for(int i = 0; i < this.mAdapter.getCount(); ++i) {
            Day day = this.mAdapter.getItem(i);
            if (this.isSelectedDay(day)) {
                position = i;
                break;
            }
        }

        return position;
    }

    public int getTodayItemPosition() {
        int position = -1;

        for(int i = 0; i < this.mAdapter.getCount(); ++i) {
            Day day = this.mAdapter.getItem(i);
            if (this.isToady(day)) {
                position = i;
                break;
            }
        }

        return position;
    }

    private int tempHeight;
    public void collapse(int duration) {
        if (this.getState() == 0) {
            this.setState(2);
            this.mLayoutBtnGroupMonth.setVisibility(View.INVISIBLE);
            this.mLayoutBtnGroupWeek.setVisibility(View.VISIBLE);


            this.mBtnPrevWeek.setClickable(false);
            this.mBtnNextWeek.setClickable(false);
            int index = this.getSuitableRowIndex();
            this.mCurrentWeekIndex = index;
            final int currentHeight = this.mInitHeight;
            final int targetHeight = this.mTableBody.getChildAt(index).getMeasuredHeight();
            tempHeight = 0;

            for(int i = 0; i < index; ++i) {
                tempHeight += this.mTableBody.getChildAt(i).getMeasuredHeight();
            }

            Animation anim = new Animation() {
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    CustomCollapsibleCalendar.this.mScrollViewBody.getLayoutParams().height = interpolatedTime == 1.0F ? targetHeight : currentHeight - (int)((float)(currentHeight - targetHeight) * interpolatedTime);
                    CustomCollapsibleCalendar.this.mScrollViewBody.requestLayout();
                    if (CustomCollapsibleCalendar.this.mScrollViewBody.getMeasuredHeight() < tempHeight + targetHeight) {
                        int position = tempHeight + targetHeight - CustomCollapsibleCalendar.this.mScrollViewBody.getMeasuredHeight();
                        CustomCollapsibleCalendar.this.mScrollViewBody.smoothScrollTo(0, position);
                    }

                    if (interpolatedTime == 1.0F) {
                        CustomCollapsibleCalendar.this.setState(1);
                        CustomCollapsibleCalendar.this.mBtnPrevWeek.setClickable(true);
                        CustomCollapsibleCalendar.this.mBtnNextWeek.setClickable(true);
                    }

                }
            };
            anim.setDuration((long)duration);
            this.startAnimation(anim);
        }

        this.expandIconView.setState(0, true);
    }

    private void collapseTo(int index) {
        if (this.getState() == 1) {
            if (index == -1) {
                index = this.mTableBody.getChildCount() - 1;
            }

            this.mCurrentWeekIndex = index;
            int targetHeight = this.mTableBody.getChildAt(index).getMeasuredHeight();
            tempHeight = 0;

            for(int i = 0; i < index; ++i) {
                tempHeight += this.mTableBody.getChildAt(i).getMeasuredHeight();
            }

            this.mScrollViewBody.getLayoutParams().height = targetHeight;
            this.mScrollViewBody.requestLayout();
            this.mHandler.post(new Runnable() {
                public void run() {
                    CustomCollapsibleCalendar.this.mScrollViewBody.smoothScrollTo(0, tempHeight);
                }
            });
            if (this.mListener != null) {
                this.mListener.onWeekChange(this.mCurrentWeekIndex);
            }
        }
    }

    public void collapseToToday() {
        if (this.getState() == 1) {
            int index = getSuitableRowIndex();
            if (index == -1) {
                index = this.mTableBody.getChildCount() - 1;
            }

            this.mCurrentWeekIndex = index;
            int targetHeight = this.mTableBody.getChildAt(index).getMeasuredHeight();
            tempHeight = 0;

            for(int i = 0; i < index; ++i) {
                tempHeight += this.mTableBody.getChildAt(i).getMeasuredHeight();
            }

            this.mScrollViewBody.getLayoutParams().height = targetHeight;
            this.mScrollViewBody.requestLayout();
            this.mHandler.post(new Runnable() {
                public void run() {
                    CustomCollapsibleCalendar.this.mScrollViewBody.scrollTo(0, tempHeight);
                }
            });
            if (this.mListener != null) {
                this.mListener.onWeekChange(this.mCurrentWeekIndex);
            }
        }
    }

    public void expand(int duration) {
        if (this.getState() == 1) {
            this.setState(2);
//            this.mLayoutBtnGroupMonth.setVisibility(0);
//            this.mLayoutBtnGroupWeek.setVisibility(8);
            this.mLayoutBtnGroupMonth.setVisibility(View.VISIBLE);
            this.mLayoutBtnGroupWeek.setVisibility(View.INVISIBLE);

            this.mBtnPrevMonth.setClickable(false);
            this.mBtnNextMonth.setClickable(false);
            final int currentHeight = this.mScrollViewBody.getMeasuredHeight();
            final int targetHeight = this.mInitHeight;
            Animation anim = new Animation() {
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    CustomCollapsibleCalendar.this.mScrollViewBody.getLayoutParams().height = interpolatedTime == 1.0F ? -2 : currentHeight - (int)((float)(currentHeight - targetHeight) * interpolatedTime);
                    CustomCollapsibleCalendar.this.mScrollViewBody.requestLayout();
                    if (interpolatedTime == 1.0F) {
                        CustomCollapsibleCalendar.this.setState(0);
                        CustomCollapsibleCalendar.this.mBtnPrevMonth.setClickable(true);
                        CustomCollapsibleCalendar.this.mBtnNextMonth.setClickable(true);
                    }
                }
            };
            anim.setDuration((long)duration);
            this.startAnimation(anim);
        }

        this.expandIconView.setState(1, true);
    }

    public void select(Day day) {
        this.setSelectedItem(new Day(day.getYear(), day.getMonth(), day.getDay()));
        this.redraw();
        if (this.mListener != null) {
            this.mListener.onDaySelect();
        }

    }

    public void setStateWithUpdateUI(int state) {
        this.setState(state);
        if (this.getState() != state) {
            this.mIsWaitingForUpdate = true;
            this.requestLayout();
        }

    }

    public void setCalendarListener(CustomCollapsibleCalendar.CalendarListener listener) {
        this.mListener = listener;
    }

    public interface CalendarListener {
        void onDaySelect();

        void onItemClick(View var1);

        void onDataUpdate();

        void onMonthChange();

        void onWeekChange(int var1);
    }
}
