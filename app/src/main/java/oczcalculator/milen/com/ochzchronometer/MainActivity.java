package oczcalculator.milen.com.ochzchronometer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import static android.widget.Toast.LENGTH_LONG;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ListView.OnItemClickListener {
    private static String DEFAULT_TASK_NAMES_STRING = "Task 1; Task 2; Task 3;";
    private boolean isThereTaskStarted = false;
    private Chronometer chronometer = null;
    private Button btnStartStop;
    private Button btnInterruption;
    private Button btnGetAllRecords;
    private Button btnGetReport;
    private Button btnChangeTasks;
    private TextView twLabelWorkingOn;
    private TextView twTaskMessage_area;
    private EditText etEmployeeName;
    private ListView lwTasks;
    private ArrayList<TaskEntity> taskMassiv;
    private String[] tasksStringArray;
    private String stringList;
    private SharedPreferences SharedPreferencesFile;
    private SharedPreferences.Editor editor;
    private DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initializeComponents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        stringList = SharedPreferencesFile.getString(Utils.SHARED_PREFERENCES_STRING_NAME, null);
        if (stringList == null) {
            stringList = DEFAULT_TASK_NAMES_STRING;
        }

        lwTasks.setAdapter(makeListAdapterFromString(stringList));
    }

    @Override
    protected void onPause() {
        super.onPause();
        editor.putString(Utils.SHARED_PREFERENCES_STRING_NAME, stringList);
        editor.commit();
    }

    private void initializeComponents() {
        // TODO make buttons menuItems
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        btnStartStop = (Button) findViewById(R.id.btnStartStop);
        btnInterruption = (Button) findViewById(R.id.btnInterruption);
        btnInterruption.setVisibility(View.INVISIBLE);
        btnGetAllRecords = (Button) findViewById(R.id.btnGetAllRecords);
        btnGetReport = (Button) findViewById(R.id.btnGetReport);
        btnChangeTasks = (Button) findViewById(R.id.btnChangeTasks);

        btnStartStop.setOnClickListener(this);
        btnInterruption.setOnClickListener(this);
        btnGetAllRecords.setOnClickListener(this);
        btnGetReport.setOnClickListener(this);
        btnChangeTasks.setOnClickListener(this);

        twTaskMessage_area = (TextView) findViewById(R.id.twTaskMessage_area);
        twLabelWorkingOn = (TextView) findViewById(R.id.twLabelWorkingOn);
        etEmployeeName = (EditText) findViewById(R.id.etEmployeeName);
        lwTasks = (ListView) findViewById(R.id.lwTasks);

        lwTasks.setOnItemClickListener(this);

        taskMassiv = new ArrayList<TaskEntity>();

        SharedPreferencesFile = getSharedPreferences(Utils.SHARED_PREFERENCES_FILE_NAME, 0);
        editor = SharedPreferencesFile.edit();
        db = DBHelper.getInstance(this);
    }

    private ListAdapter makeListAdapterFromString(String stringList) {
        tasksStringArray = stringList.trim().split(String.format("\\s*%s\\s*", Utils.TASK_SEPARATOR));

        ListAdapter tasksListAdapter = new ArrayAdapter<>(
                MainActivity.this,
                android.R.layout.simple_expandable_list_item_1,
                Utils.removeDuplicateOrEmptyTasks(tasksStringArray)
        );

        return tasksListAdapter;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnStartStop:
                if (isThereTaskStarted) {
                    stopTask(true);
                } else {
                    if (TextUtils.isEmpty(etEmployeeName.getText().toString())) {
                        Toast.makeText(getApplicationContext(), getString(R.string.error_employee_name_text),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        boolean taskChosen = !(twTaskMessage_area.getText().toString().equals(getString(R.string.text_nothing)));
                        if (taskChosen) {
                            startTask();
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.note_start_task,
                                    LENGTH_LONG
                            ).show();
                        }
                    }
                }
                break;

            case R.id.btnInterruption:
                if (isThereTaskStarted) {
                    stopTask(false);
                } else {
                    // on normal usage that should never show
                    Toast.makeText(getApplicationContext(), R.string.note_start_task, LENGTH_LONG).show();
                }
                if (isThereTaskStarted) {
                    stopTask(false);
                } else {
                    // on normal usage that should never show
                    Toast.makeText(getApplicationContext(), R.string.note_start_task,LENGTH_LONG).show();
                }
                break;

            case R.id.btnGetAllRecords:
                String result = "";
                //TODO ASINCTASK
                try {
                    db.open();
                    taskMassiv = db.getAllTasks();
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                for (TaskEntity task : taskMassiv) {
                    result += task.toString() + "\n\n";
                }

                Intent intentAllRecords = new Intent(MainActivity.this, AllRecordsActivity.class);
                intentAllRecords.putExtra("reports", result);
                startActivity(intentAllRecords);
                break;

            case R.id.btnGetReport:
                String report = getString(R.string.head_of_table_report);
                //TODO make it asinc
                try {
                    db.open();
                    taskMassiv = db.getAllTasks();
                    db.open();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String[] uniqueTasks = new HashSet<String>(Arrays.asList(tasksStringArray)).toArray(new String[0]);
                for (String taskString : uniqueTasks) {
                    int timesOccur = 0;
                    long secondsTaskWorked = 0;

                    for (TaskEntity task : taskMassiv) {
                        boolean hasMatch = task.getTaskName().equalsIgnoreCase(taskString);
                        if (hasMatch) {
                            secondsTaskWorked += task.getSecondsWorked();
                            if (task.isNotInterrupted()) {
                                timesOccur++;
                            }
                        }
                    }

                    if (secondsTaskWorked != 0) {
                        float averageTime = timesOccur != 0 ? (float) (secondsTaskWorked / timesOccur) : (float) secondsTaskWorked;
                        report += String.format("%s:\ttimes: %d\tavg: %.2f\n",
                                taskString,
                                timesOccur,
                                averageTime);
                    }
                }

                Intent intentGetReport = new Intent(MainActivity.this, TasksReportActivity.class);
                intentGetReport.putExtra("tasksStringArray", report);
                startActivity(intentGetReport);
                break;

            case R.id.btnChangeTasks:
                Intent intent = new Intent(MainActivity.this, SetTaskActivity.class);
                startActivity(intent);
                break;

            default:
                Toast.makeText(MainActivity.this, R.string.dont_know_what_to_do, Toast.LENGTH_SHORT).show();
            return;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //it is only one ListView in the activity no switch needed
        if (isThereTaskStarted) {
            return;
        }

        String itemText = parent.getItemAtPosition(position).toString();
        twLabelWorkingOn.setText(getString(R.string.task_chosen_text));
        twTaskMessage_area.setText(itemText);
    }

    private void startTask() {
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        twLabelWorkingOn.setText(getString(R.string.workingOn_label));
        btnStartStop.setText(R.string.stop_text);
        isThereTaskStarted = true;
        btnInterruption.setVisibility(View.VISIBLE);
        btnGetAllRecords.setVisibility(View.INVISIBLE);
        btnGetReport.setVisibility(View.INVISIBLE);
        etEmployeeName.setFocusable(false);
    }

    private void stopTask(boolean isNotInterrupted) {
        chronometer.stop();
        addDataToDB(isNotInterrupted);
        chronometer.setBase(SystemClock.elapsedRealtime());
        btnStartStop.setText(R.string.start_text);
        isThereTaskStarted = false;
        btnInterruption.setVisibility(View.INVISIBLE);
        btnGetAllRecords.setVisibility(View.VISIBLE);
        btnGetReport.setVisibility(View.VISIBLE);
        twTaskMessage_area.setText(R.string.text_nothing);
    }

    private void addDataToDB(boolean isNotInterrupted) {
        long secondsElapsed = (SystemClock.elapsedRealtime() - chronometer.getBase()) / 1000;
        TaskEntity entity = new TaskEntity(
                etEmployeeName.getText().toString(),
                twTaskMessage_area.getText().toString(),
                secondsElapsed,
                isNotInterrupted,
                Utils.getDateAsString(getString(R.string.date_time_pattern))
        );
        try {
            //TODO ASINCTASK
            db.open();
            db.addTask(entity);
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // simple notification
        if (isNotInterrupted) {
            makeToast(getString(R.string.is_not_interrupted), secondsElapsed);
        } else {
            makeToast(getString(R.string.is_interrupted), secondsElapsed);
        }
    }

    //TODO decide if you remove this notification
    private void makeToast(String interruptionText, long secondsElapsed) {
        Toast.makeText(getApplicationContext(), "Seconds elapsed: " + secondsElapsed + interruptionText,
                Toast.LENGTH_SHORT).show();
    }


}