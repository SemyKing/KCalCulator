package semyking.kcalculator.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "kcal_data_table")
public class KcalData {

    @PrimaryKey(autoGenerate = true)
    private Long id;

    @ColumnInfo(name = "date_string")
    private String date_string;

    @ColumnInfo(name = "date_long")
    private Long date_long;

    @ColumnInfo(name = "eaten_kcal")
    private String eatenKcal;

    @ColumnInfo(name = "spent_kcal")
    private String spentKcal;

    @ColumnInfo(name = "weight")
    private String weight;

    @ColumnInfo(name = "k_day")
    private String KDay;

    @ColumnInfo(name = "kcal_difference")
    private String kcalDifference;

    @ColumnInfo(name = "kcal_difference_percent")
    private String kcalDifferencePercent;

    public KcalData() {}

    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getDate_string() {
        return date_string;
    }
    public void setDate_string(String date_string) {
        this.date_string = date_string;
    }

    public Long getDate_long() {
        return date_long;
    }
    public void setDate_long(Long date_long) {
        this.date_long = date_long;
    }

    public String getEatenKcal() {
        return eatenKcal;
    }
    public void setEatenKcal(String eatenKcal) {
        this.eatenKcal = eatenKcal;
    }

    public String getSpentKcal() {
        return spentKcal;
    }
    public void setSpentKcal(String spentKcal) {
        this.spentKcal = spentKcal;
    }

    public String getWeight() {
        return weight;
    }
    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getKDay() {
        return KDay;
    }
    public void setKDay(String KDay) {
        this.KDay = KDay;
    }

    public String getKcalDifference() {
        return kcalDifference;
    }
    public void setKcalDifference(String kcalDifference) {
        this.kcalDifference = kcalDifference;
    }

    public String getKcalDifferencePercent() {
        return kcalDifferencePercent;
    }
    public void setKcalDifferencePercent(String kcalDifferencePercent) {
        this.kcalDifferencePercent = kcalDifferencePercent;
    }
}
