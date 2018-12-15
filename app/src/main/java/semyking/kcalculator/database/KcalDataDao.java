package semyking.kcalculator.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface KcalDataDao {

    @Query("DELETE FROM kcal_data_table")
    void deleteAll();

    @Query("SELECT id FROM kcal_data_table WHERE date_long = :date")
    int getIdByDate(long date);

    @Update
    int updateKcalData(KcalData... kcalData);

    @Insert(onConflict = 1)
    void insert(KcalData kcalData);

    @Query("SELECT * FROM kcal_data_table")
    List<KcalData> loadAll();
}
