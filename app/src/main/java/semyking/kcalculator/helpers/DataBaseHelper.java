package semyking.kcalculator.helpers;

import android.content.Context;
import android.util.Log;
import semyking.kcalculator.database.DataBase;
import semyking.kcalculator.database.KcalData;
import semyking.kcalculator.database.KcalDataModel;
import semyking.kcalculator.database.Models.IModel;

import java.util.*;

public class DataBaseHelper {

    private IModel<KcalData, String> db;
    private List<KcalData> allData;

    private Calendar mCalendar1, mCalendar2;

    public DataBaseHelper(Context context) {
        db = KcalDataModel.getInstance(DataBase.getDatabase(context));

        mCalendar1 = Calendar.getInstance(Locale.GERMAN);
        mCalendar1.set(Calendar.HOUR_OF_DAY, 0);
        mCalendar1.set(Calendar.MINUTE, 0);
        mCalendar1.set(Calendar.SECOND, 0);
        mCalendar1.set(Calendar.MILLISECOND, 0);

        mCalendar2 = Calendar.getInstance(Locale.GERMAN);
        mCalendar2.set(Calendar.HOUR_OF_DAY, 0);
        mCalendar2.set(Calendar.MINUTE, 0);
        mCalendar2.set(Calendar.SECOND, 0);
        mCalendar2.set(Calendar.MILLISECOND, 0);

        allData = new ArrayList<>();
        getAll();
    }

    private boolean allDataLoaded = false;

    private void getAll() {
        allDataLoaded = false;
        db.loadAll(new IModel.LoadAllItemsCallback<KcalData>() {
            @Override
            public void onDataNotAvailable(String str) {
                allDataLoaded = true;
                Log.i("getAll from DB", str);
            }

            @Override
            public void onItemsLoaded(List<KcalData> list) {
                allData = getSortedList(list);
                allDataLoaded = true;
            }
        });
    }

    private List<KcalData> getSortedList(List<KcalData> list) {
        List<KcalData> sorted = new ArrayList<>();
        sorted.addAll(list);

        Collections.sort(allData, new Comparator<KcalData>() {
            public int compare(KcalData o1, KcalData o2) {
                mCalendar1.setTimeInMillis(o1.getDate_long());
                mCalendar2.setTimeInMillis(o2.getDate_long());

                if (mCalendar1.before(mCalendar2)) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });

        return sorted;
    }

    private KcalData kd;

    public void saveToDB(String selectedDateStr, final long selectedDateLong, String eaten, String spent, String weight, String kday, String difference, String differencePercent) {
        kd = new KcalData();
        kd.setDate_string(selectedDateStr);
        kd.setDate_long(selectedDateLong);
        kd.setEatenKcal(eaten);
        kd.setSpentKcal(spent);
        kd.setWeight(weight);
        kd.setKDay(kday);
        kd.setKcalDifference(difference);
        kd.setKcalDifferencePercent(differencePercent);

        db.saveItem(new IModel.ItemSaveCallback<KcalData>() {
            @Override
            public void onItemSaved(int status) {
                if (status == IModel.INSERT) {
                    allData.add(kd);
                    allData = getSortedList(allData);
                }
            }
        }, kd);
    }

    public KcalData getSelectedDayData(long date_l) {
        for ( KcalData data : allData ) {
            if (data.getDate_long().equals(date_l)) {
                return data;
            }

        }
        return null;
    }

    public void deleteAllFromDB() {
        db.deleteAll();
    }

    public boolean isAllDataLoaded() {
        return allDataLoaded;
    }

    public List<KcalData> getAllData() {
        List<KcalData> result = new ArrayList<>();
        result.addAll(allData);
        return result;
    }
}
