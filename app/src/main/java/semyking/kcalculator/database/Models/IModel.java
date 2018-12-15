package semyking.kcalculator.database.Models;

import semyking.kcalculator.database.KcalData;

import java.util.List;

public interface IModel<E, K> {

    int INSERT = 0, UPDATE = 1;


    interface LoadAllItemsCallback<E> {
        void onDataNotAvailable(String str);

        void onItemsLoaded(List<E> list);
    }

    interface ItemSaveCallback<E> {
        void onItemSaved(int status);
    }



    void deleteAll();

    void save(E e);

    void saveItem(ItemSaveCallback<E> itemSaveCallback, KcalData kcalData);

    void loadAll(LoadAllItemsCallback<E> loadAllItemsCallback);
}
