package semyking.kcalculator.views.settings;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import com.opencsv.CSVReader;
import semyking.kcalculator.R;
import semyking.kcalculator.database.KcalData;
import semyking.kcalculator.helpers.CalendarHelper;
import semyking.kcalculator.helpers.DataBaseHelper;

import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class SettingsFragment extends Fragment {
    public static final String TAG = SettingsFragment.class.getSimpleName();

    private DataBaseHelper mDbHelper;

    private Calendar mCalendar;

    public SettingsFragment() {

    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View view = layoutInflater.inflate(R.layout.settings_layout, viewGroup, false);

        if (getActivity() != null) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        mDbHelper = DataBaseHelper.getInstance(getContext());
        mCalendar = CalendarHelper.getCalendar();

        final Button deleteButton = view.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleDelete();
            }
        });

        final Button exportButton = view.findViewById(R.id.exportButton);
        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportDataToCSV();
            }
        });

        final Button importButton = view.findViewById(R.id.importButton);
        importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleImport();
            }
        });

        Log.d("viewLoaded", "SettingsFragment");
        return view;
    }

    private void handleDelete() {
        if (getActivity() == null) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.confirm_db_clear_title);
        builder.setMessage(R.string.confirm_db_clear_msg);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDbHelper.deleteAllFromDB();
                Toast.makeText(getActivity(), R.string.db_clear_successful, Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    private void handleImport() {
        Intent intent = new Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_import_file)), 123);
    }

    /*handleImport() listener*/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==123 && resultCode==RESULT_OK) {
            Uri selectedFile = data.getData(); //The uri with the location of the file
            importToDB(selectedFile);
        }
    }

    private void importToDB(Uri file) {
        ArrayList<KcalData> importedList = new ArrayList<>();

        try {
            if (getActivity() == null)
                throw new IOException("getActivity() == null");

            if (getActivity().getContentResolver() == null)
                throw new IOException("getActivity().getContentResolver() == null");

            if (getActivity().getContentResolver().openInputStream(file) == null)
                throw new IOException("getActivity().getContentResolver().openInputStream(file) == null");

            CSVReader reader = new CSVReader(new InputStreamReader(new BufferedInputStream(getActivity().getContentResolver().openInputStream(file))));
            String[] nextLine;

            reader.readNext(); //skip titles
            while ((nextLine = reader.readNext()) != null) {
                String dateString = nextLine[0];
                Long dateLong = Long.parseLong(nextLine[1]);
                String spent = nextLine[2];
                String eaten = nextLine[3];
                String weight = nextLine[4];
                String kday = nextLine[5];
                String dif = nextLine[6];
                String difPercent = nextLine[7];

                String importStr = "dateString:   " + dateString + "\n" +
                        "dateLong:     " + dateLong + "\n" +
                        "spent:        " + spent + "\n" +
                        "eaten:        " + eaten + "\n" +
                        "weight:       " + weight + "\n" +
                        "dif:          " + dif + "\n" +
                        "difPercent:   " + difPercent + "\n" +
                        "kday:         " + kday + "\n";

                Log.d("IMPORT", importStr);

                KcalData kd = new KcalData();
                kd.setDate_string(          dateString);
                kd.setDate_long(            dateLong);
                kd.setSpentKcal(            spent);
                kd.setEatenKcal(            eaten);
                kd.setWeight(               weight);
                kd.setKDay(                 kday);
                kd.setKcalDifference(       dif);
                kd.setKcalDifferencePercent(difPercent);

                importedList.add(kd);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        mDbHelper.saveImportedData(importedList);
    }

    private void exportDataToCSV() {
        final char DEFAULT_SEPARATOR = ',';

        ArrayList<KcalData> exportList = new ArrayList<>(mDbHelper.getAllData());

        if (exportList.size() > 0) {
            mCalendar = CalendarHelper.getCalendar();
            String today = CalendarHelper.formatFullDate(mCalendar.getTime()).replaceAll("\\.", "-");

            try {
                if (getActivity() == null)
                    throw new IOException("getActivity() == null");

                File appDir = new File(getActivity().getExternalFilesDir(null), "KcalCulator");

                final String filename = appDir.toString() + "/" +today+".csv";
                final FileWriter writer = new FileWriter(filename);

                List<String> headers = new ArrayList<>();
                headers.add("Date_string");
                headers.add("Date_long (milliseconds)");
                headers.add("SpentKcal");
                headers.add("EatenKcal");
                headers.add("Weight");
                headers.add("KDay");
                headers.add("KcalDifference");
                headers.add("KcalDifferencePercent");

                boolean first = true;

                StringBuilder sb = new StringBuilder();
                for (String value : headers) {
                    if (!first) {
                        sb.append(DEFAULT_SEPARATOR);
                    }
                    sb.append(value);
                    first = false;
                }
                sb.append("\n");
                writer.append(sb.toString());

                for (KcalData kd : exportList) {
                    List<String> list = new ArrayList<>();
                    list.add(kd.getDate_string());
                    list.add(kd.getDate_long().toString());
                    list.add(kd.getSpentKcal());
                    list.add(kd.getEatenKcal());
                    list.add(kd.getWeight());
                    list.add(kd.getKDay());
                    list.add(kd.getKcalDifference());
                    list.add(kd.getKcalDifferencePercent());

                    first = true;

                    sb = new StringBuilder();
                    for (String value : list) {
                        if (!first) {
                            sb.append(DEFAULT_SEPARATOR);
                        }
                        sb.append(value);
                        first = false;
                    }
                    sb.append("\n");
                    writer.append(sb.toString());
                }
                writer.flush();
                writer.close();

                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.export_successful, Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
