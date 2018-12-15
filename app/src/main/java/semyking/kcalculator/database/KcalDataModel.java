package semyking.kcalculator.database;

import semyking.kcalculator.database.Models.IModel;

import java.util.ArrayList;
import java.util.List;

public class KcalDataModel implements IModel<KcalData, String> {
    private static KcalDataModel instance;
    private DataBase dataBase;
    private final Worker worker = Worker.getInstance();

    private KcalDataModel(DataBase dataBase) {
        this.dataBase = dataBase;
    }

    public static KcalDataModel getInstance(DataBase dataBase) {
        if (instance == null) {
            instance = new KcalDataModel(dataBase);
        }
        return instance;
    }


    public void deleteAll() {
        this.worker.registerTask(new Runnable() {
            public void run() {
                dataBase.kcalDataDao().deleteAll();
            }
        });
    }

    public void save(final KcalData kcalData) {
        this.worker.registerTask(new Runnable() {
            public void run() {

                int id = dataBase.kcalDataDao().getIdByDate(kcalData.getDate_long());

                if (id == 0)
                    dataBase.kcalDataDao().insert(kcalData);
                else
                    dataBase.kcalDataDao().updateKcalData(kcalData);

            }
        });
    }

    public void saveItem(final ItemSaveCallback<KcalData> itemSaveCallback, final KcalData kcalData) {
        this.worker.registerTask(new Runnable() {
            @Override
            public void run() {
                int id = dataBase.kcalDataDao().getIdByDate(kcalData.getDate_long());

                if (itemSaveCallback == null) {
                    System.out.println("-----itemSaveCallback = null");
                    return;
                }
                if (id == 0) {
                    dataBase.kcalDataDao().insert(kcalData);
                    System.out.println("-----itemSaveCallback = INSERT");
                    itemSaveCallback.onItemSaved(INSERT);
                } else {
                    dataBase.kcalDataDao().updateKcalData(kcalData);
                    System.out.println("-----itemSaveCallback = UPDATE");
                    itemSaveCallback.onItemSaved(UPDATE);
                }
            }
        });
    }




    public void loadAll(final LoadAllItemsCallback<KcalData> loadAllItemsCallback) {
        this.worker.registerTask(new Runnable() {
            public void run() {
                List<KcalData> arrayList = new ArrayList<>(dataBase.kcalDataDao().loadAll());
                if (loadAllItemsCallback == null) {
                    return;
                }
                if (arrayList.size() < 1) {
                    loadAllItemsCallback.onDataNotAvailable("DATABASE IS EMPTY");
                } else {
                    loadAllItemsCallback.onItemsLoaded(arrayList);
                }
            }
        });
    }
}
