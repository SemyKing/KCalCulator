package semyking.kcalculator.helpers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.*;
import com.shrikanthravi.collapsiblecalendarview.data.Day;
import semyking.kcalculator.MainActivity;
import semyking.kcalculator.R;
import semyking.kcalculator.database.KcalData;
import semyking.kcalculator.views.DataStorage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChartHelper {

    private final Context mContext;
    private final DataBaseHelper mDbHelper;
    private final DataStorage mDataStorage;
    private final List<ILineDataSet> sets;

    public ChartHelper(Context context) {
        mContext = context;
        mDbHelper = DataBaseHelper.getInstance(context);
        mDataStorage = ((MainActivity) context).getDataStorage();

        sets = new ArrayList<>();
    }

    private LineDataSet differencePercentSet;
    private LineDataSet weightSet;
    private XAxis mXAxis;

    public LineChart initLineChart(LineChart chart) {
        mXAxis = chart.getXAxis();
        mXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        mXAxis.setGranularity(1f);

        final YAxis rightAxis = chart.getAxisRight();
        rightAxis.setAxisMinimum(65f); //SharedPreferences
        rightAxis.setAxisMaximum(85f); //SharedPreferences
        rightAxis.setGranularity(5f);
        rightAxis.setGranularityEnabled(true);
        rightAxis.setLabelCount(5, true);

        chart.setXAxisRenderer(new CustomXAxisRenderer(chart.getViewPortHandler(), mXAxis, chart.getTransformer(YAxis.AxisDependency.LEFT)));
        chart.setExtraBottomOffset(20f);

        differencePercentSet = new LineDataSet(null, mContext.getString(R.string.kcal_difference_percent));
        weightSet = new LineDataSet(null, mContext.getString(R.string.weight));

        differencePercentSet.setColor(mContext.getResources().getColor(R.color.colorGreen));            //SharedPreferences
        differencePercentSet.setCircleColors(mContext.getResources().getColor(R.color.colorGreen));     //SharedPreferences

        weightSet.setColor(mContext.getResources().getColor(R.color.colorRed));         //SharedPreferences
        weightSet.setCircleColors(mContext.getResources().getColor(R.color.colorRed));  //SharedPreferences

        differencePercentSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        weightSet.setAxisDependency(YAxis.AxisDependency.RIGHT);

        chart.setDescription(null);

        Legend legend = chart.getLegend();
        legend.setPosition(Legend.LegendPosition.ABOVE_CHART_CENTER);
        legend.setYOffset(10f);

        return chart;
    }


    public void updateChartData(LineChart chart, Calendar fromCal, Calendar toCal) {
        Day day = null;
        if (mDataStorage.getSelectedDay() != null) {
            day = mDataStorage.getSelectedDay();
        }

        List<Entry> differencePercentEntries = new ArrayList<>();
        List<Entry> weightEntries = new ArrayList<>();

        List<KcalData> allData = mDbHelper.getAllData();
        List<KcalData> chartData = new ArrayList<>();


        if (fromCal == null || toCal == null) {
            if(day == null)
                return;

            for (KcalData kd : allData) {
                if (CalendarHelper.dateInWeek(kd.getDate_long(), CalendarHelper.getWeekOfYear(day)))
                    chartData.add(kd);
            }
        } else {
            for (KcalData kd : allData) {
                if (CalendarHelper.dateBetween(kd.getDate_long(), fromCal, toCal))
                    chartData.add(kd);
            }
        }

        final String[] mXAxisLabels = new String[chartData.size()];

        for (int i = 0; i < chartData.size(); i++) {
            String kcalDifStr = chartData.get(i).getKcalDifferencePercent();
            float kcalDif = (kcalDifStr.length() == 0) ? 0f : Float.parseFloat(kcalDifStr);

            String weightStr = chartData.get(i).getWeight();
            float weight = (kcalDifStr.length() == 0) ? 0f : Float.parseFloat(weightStr);

            differencePercentEntries.add(new Entry(i, kcalDif, chartData.get(i)));
            weightEntries.add(new Entry(i, weight, chartData.get(i)));
            mXAxisLabels[i] = CalendarHelper.formatShortDate(CalendarHelper.getDateFromMilliseconds(chartData.get(i).getDate_long()))+"\n"+chartData.get(i).getKDay();
        }

        mXAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return ((int) value >= mXAxisLabels.length || (int) value < 0) ? "" : mXAxisLabels[(int) value];
            }
        });


        differencePercentSet.clear();
        differencePercentSet.setValues(differencePercentEntries);
        weightSet.clear();
        weightSet.setValues(weightEntries);

        sets.clear();
        sets.add(differencePercentSet);
        sets.add(weightSet);

        final LineData allLineData = new LineData(sets);
        chart.setData(allLineData);
    }


    /**
     * Class allows for multi row xAxis labels rendering
     */
    private class CustomXAxisRenderer extends XAxisRenderer {
        CustomXAxisRenderer(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans) {
            super(viewPortHandler, xAxis, trans);
        }
        @Override
        protected void drawLabel(Canvas c, String formattedLabel, float x, float y, MPPointF anchor, float angleDegrees) {
            String line[] = formattedLabel.split("\n");
            Utils.drawXAxisValue(c, line[0], x, y, mAxisLabelPaint, anchor, angleDegrees);
            if (line.length > 1)
                Utils.drawXAxisValue(c, line[1], x, y + mAxisLabelPaint.getTextSize(), mAxisLabelPaint, anchor, angleDegrees);
        }
    }
}
