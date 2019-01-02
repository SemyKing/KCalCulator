package semyking.kcalculator.helpers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import semyking.kcalculator.database.DataBase;
import semyking.kcalculator.database.KcalData;
import semyking.kcalculator.database.KcalDataModel;
import semyking.kcalculator.database.Models.IModel;

import java.util.*;

public class DataBaseHelper {

    private final Context mContext;
    private final IModel<KcalData, String> mDB;
    private List<KcalData> mAllData;

    private Calendar mCalendar1, mCalendar2;

    private static DataBaseHelper mDataBaseHelper;

    private DataBaseHelper(final Context context) {
        mContext = context;
        mDB = KcalDataModel.getInstance(DataBase.getDatabase(mContext));

        mCalendar1 = CalendarHelper.getCalendar();
        mCalendar2 = CalendarHelper.getCalendar();

        mAllData = new ArrayList<>();
        getAll();
    }

    public static DataBaseHelper getInstance(final Context context) {
        if (mDataBaseHelper == null) {
            mDataBaseHelper = new DataBaseHelper(context);
        }
        return mDataBaseHelper;
    }

    private boolean mAllDataLoaded = false;

    private void getAll() {
        mAllDataLoaded = false;
        mDB.loadAll(new IModel.LoadAllItemsCallback<KcalData>() {
            @Override
            public void onDataNotAvailable(String str) {
                mAllDataLoaded = true;
                Log.i("getAll from DB", str);
            }

            @Override
            public void onItemsLoaded(List<KcalData> list) {
                mAllData = getSortedList(list);
                mAllDataLoaded = true;
            }
        });
    }

    private List<KcalData> getSortedList(List<KcalData> list) {
        List<KcalData> sorted = new ArrayList<>();
        sorted.addAll(list);

        Collections.sort(sorted, new Comparator<KcalData>() {
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

        mDB.saveItem(new IModel.ItemSaveCallback<KcalData>() {
            @Override
            public void onItemSaved(int status) {
                if (status == IModel.INSERT) {
                    mAllData.add(kd);
                    mAllData = getSortedList(mAllData);
                } else if (status == IModel.UPDATE) {
                    for (KcalData kcalData : mAllData) {
                        if (kcalData.getDate_long().equals(kd.getDate_long())) {
                            mAllData.set(mAllData.indexOf(kcalData), kd);
                        }
                    }
                }
            }
        }, kd);
    }

    public void saveImportedData(List<KcalData> importedData) {
        try {
            for(final KcalData kd : importedData) {
                mDB.saveItem(new IModel.ItemSaveCallback<KcalData>() {
                    @Override
                    public void onItemSaved(int status) {

                    }
                }, kd);
            }
            getAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(mContext, "IMPORT SUCCESSFUL", Toast.LENGTH_SHORT).show();
    }

    public KcalData getSelectedDayData(long date_l) {
        KcalData kcalData = new KcalData();
        kcalData.setDate_long(date_l);

        for ( KcalData data : mAllData) {
            if (data.getDate_long().equals(date_l)) {

                kcalData.setSpentKcal(data.getSpentKcal());
                kcalData.setEatenKcal(data.getEatenKcal());
                kcalData.setWeight(data.getWeight());
                kcalData.setKDay(data.getKDay());

                break;
            }

        }

        return kcalData;
    }

    public void deleteAllFromDB() {
        mDB.deleteAll();
        mAllData.clear();
    }

    public boolean isAllDataLoaded() {
        return mAllDataLoaded;
    }

    public List<KcalData> getAllData() {
        List<KcalData> result = new ArrayList<>();
        result.addAll(mAllData);
        return result;
    }
}
