package semyking.kcalculator.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {KcalData.class}, exportSchema = false, version = 1)
public abstract class DataBase extends RoomDatabase {
    private static DataBase INSTANCE;

    abstract KcalDataDao kcalDataDao();

    public static DataBase getDatabase(Context context) {
        if (INSTANCE == null && INSTANCE == null) {
            INSTANCE = (DataBase) Room.databaseBuilder(context.getApplicationContext(), DataBase.class, "KcalCulator.db").fallbackToDestructiveMigration().build();
        }
        return INSTANCE;
    }
}
