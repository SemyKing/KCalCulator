package semyking.kcalculator.views;

import android.arch.lifecycle.ViewModel;
import com.shrikanthravi.collapsiblecalendarview.data.Day;

public class DataStorage extends ViewModel {

    public DataStorage() {}

    private Day selectedDay;

    public Day getSelectedDay() {
        return selectedDay;
    }
    public void setSelectedDay(Day selectedDay) {
        this.selectedDay = selectedDay;
    }


    private Long fromCalDate, toCalDate;

    public Long getFromCalDate() {
        return fromCalDate;
    }
    public void setFromCalDate(long newDate) {
        fromCalDate = newDate;
    }

    public Long getToCalDate() {
        return toCalDate;
    }
    public void setToCalDate(long newDate) {
        toCalDate = newDate;
    }
}
