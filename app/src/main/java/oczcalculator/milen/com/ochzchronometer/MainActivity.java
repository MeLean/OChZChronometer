package oczcalculator.milen.com.ochzchronometer;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
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

import static android.widget.Toast.LENGTH_LONG;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ListView.OnItemClickListener {
    private static String DEFAULT_TASK_NAMES_STRING = "Task 1; Task 2; Task 3;";
    private boolean isThereTaskStarted = false;
    private Chronometer chronometer = null;
    private Button btnStartStop;
    private Button btnInterruption;
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
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

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

        btnStartStop.setOnClickListener(this);
        btnInterruption.setOnClickListener(this);

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
        tasksStringArray = Utils.splitBySeparator(stringList);

        ListAdapter tasksListAdapter = new ArrayAdapter<>(
                MainActivity.this,
                android.R.layout.simple_expandable_list_item_1,
                Utils.removeDuplicateOrEmptyTasks(tasksStringArray)
        );

        return tasksListAdapter;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {

            case R.id.action_get_report:

                //TODO make it asinc
                try {
                    db.open();
                    taskMassiv = db.getAllTasks();
                    db.open();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String report = Utils.makeStringReport(this, tasksStringArray, taskMassiv);

                Intent intentGetReport = new Intent(MainActivity.this, TasksReportActivity.class);
                intentGetReport.putExtra("tasksStringArray", report);
                startActivity(intentGetReport);
                break;

            case R.id.action_get_all_entities:
                StringBuilder result = new StringBuilder();
                //TODO ASINCTASK
                try {
                    db.open();
                    taskMassiv = db.getAllTasks();
                    db.close();
                } catch (Exception e) {//TODO better exceptionCatch
                    e.printStackTrace();
                }

                for (TaskEntity task : taskMassiv) {
                    result.append(task.toString());
                    result.append("\n\n");
                }

                Intent intentAllRecords = new Intent(MainActivity.this, AllRecordsActivity.class);
                intentAllRecords.putExtra("reports", result.toString());
                startActivity(intentAllRecords);
                break;



            case R.id.action_change_tasks:
                Intent intent = new Intent(MainActivity.this, SetTasksActivity.class);
                startActivity(intent);
                break;

            case R.id.action_clear_db:
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                try {
                                    db.open();
                                    db.deleteAllTasks();
                                    db.close();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getString(R.string.delete_db_alert));
                builder.setPositiveButton(getString(R.string.dialog_yes), dialogClickListener);
                builder.setNegativeButton(getString(R.string.dialog_no), dialogClickListener);
                builder.show();
                break;

            default:
                Toast.makeText(MainActivity.this, R.string.dont_know_what_to_do, Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onOptionsItemSelected(item);
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

            default:
                Toast.makeText(MainActivity.this, R.string.dont_know_what_to_do, Toast.LENGTH_SHORT).show();
            break;
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
        etEmployeeName.setFocusable(false);
    }

    private void stopTask(boolean isNotInterrupted) {
        chronometer.stop();
        addDataToDB(isNotInterrupted);
        chronometer.setBase(SystemClock.elapsedRealtime());
        btnStartStop.setText(R.string.start_text);
        isThereTaskStarted = false;
        btnInterruption.setVisibility(View.INVISIBLE);
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