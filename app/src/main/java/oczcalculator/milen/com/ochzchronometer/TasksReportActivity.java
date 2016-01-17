package oczcalculator.milen.com.ochzchronometer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

import static oczcalculator.milen.com.ochzchronometer.R.drawable.cell_background;

public class TasksReportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_report);

        ArrayList<TaskEntity> taskArray = new ArrayList<>();
        DBHelper db = DBHelper.getInstance(this);
        try {
            taskArray = db.getAllTasks();
        } catch (Exception e) {//TODO better exceptionCatch
            e.printStackTrace();
        }

        SharedPreferences taskNamesPref = getSharedPreferences(Utils.SHARED_PREFERENCES_FILE_NAME, 0);
        String[] tasksNames = Utils.splitBySeparator(taskNamesPref.getString(Utils.SHARED_PREFERENCES_STRING_NAME, null));

        TableLayout tlResultTable = (TableLayout) findViewById(R.id.tlResultTable);
        if (taskArray.size() > 0) {
            makeReportsInTable(this, tlResultTable, taskArray, tasksNames);
        }else{
            TextView twNoEntities = new TextView(this);
            twNoEntities.setText(getString(R.string.no_entities_text));
            TableRow trNoEntities = new TableRow(this);
            trNoEntities.addView(twNoEntities);
            tlResultTable.addView(trNoEntities);
        }
    }

    private void makeReportsInTable(Context context, TableLayout tlResultTable, ArrayList<TaskEntity> taskArray, String[] tasksNames) {

        //make a table header
        TableRow tableHeader = makeRow(
                context,
                getString(R.string.header_task_name),
                getString(R.string.header_times_occur),
                getString(R.string.header_average_time)
        );
        tlResultTable.addView(tableHeader);

        for (String taskName : tasksNames) {
            int timesOccur = 0;
            long secondsTaskWorked = 0;

            for (TaskEntity task : taskArray) {
                boolean hasMatch = task.getTaskName().equalsIgnoreCase(taskName);
                if (hasMatch) {
                    secondsTaskWorked += task.getSecondsWorked();
                    if (task.isNotInterrupted()) {
                        timesOccur++;
                    }
                }
            }

            if (secondsTaskWorked != 0) {
                float averageTime = timesOccur != 0 ? (float) (secondsTaskWorked / timesOccur) : (float) secondsTaskWorked;
                TableRow row = makeRow(context, taskName, String.valueOf(timesOccur), String.valueOf(averageTime));
                tlResultTable.addView(row);
            }
        }
    }

    private TableRow makeRow(Context context, String taskName, String timesOccur, String averageTime) {
        TableRow row = new TableRow(context);
        TableRow.LayoutParams rowLayoutParameters = new TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT
        );
        row.setLayoutParams(rowLayoutParameters);
        row.setGravity(Gravity.CENTER_HORIZONTAL);

        TableRow.LayoutParams taskNameParams =
                new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 0.4f);
        TableRow.LayoutParams timesOccurParams =
                new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 0.3f);
        TableRow.LayoutParams averageTimeParams =
                new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 0.3f);


        TextView twTaskName = new TextView(context);
        twTaskName.setLayoutParams(taskNameParams);
        purefyView(twTaskName);

        TextView twTimesOccur = new TextView(context);
        twTimesOccur.setLayoutParams(timesOccurParams);
        purefyView(twTimesOccur);


        TextView twAverageTime = new TextView(context);
        twAverageTime.setLayoutParams(averageTimeParams);
        purefyView(twAverageTime);

        twTaskName.setText(taskName);
        twTimesOccur.setText(timesOccur);
        //TODO make a hours, minutes and seconds
        twAverageTime.setText(averageTime);

        row.addView(twTaskName);
        row.addView(twTimesOccur);
        row.addView(twAverageTime);

        return row;
    }

    private void purefyView(TextView view) {
        view.setBackgroundResource(cell_background);
        int padding = 10;
        int textSize = 18;
        float density = getResources().getDisplayMetrics().density;
        int dpPadding = (int)(padding * density);
        view.setPadding(dpPadding,dpPadding,dpPadding,dpPadding);
        view.setGravity(Gravity.CENTER);
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
    }

}