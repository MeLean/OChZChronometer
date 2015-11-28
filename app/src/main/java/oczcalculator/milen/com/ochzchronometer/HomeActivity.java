package oczcalculator.milen.com.ochzchronometer;

import android.content.Intent;
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
import java.util.ArrayList;
import java.util.Calendar;

import static android.widget.ListView.*;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, ListView.OnItemClickListener{
    private boolean isThereTaskStarted = false;
    private Chronometer chronometer = null;
    Button btnStartStop;
    Button btnInterruption;
    Button btnGetAllRecords;
    Button btnGetReport;
    TextView twLabelWorkingOn;
    TextView twTaskMessage_area;
    EditText etEmployeeName;
    ListView lwTasks;
    private ArrayList<TaskEntity> taskMassiv = new ArrayList<>();
    private static int TaskId = 0;
    //making tasksStringArray needed for ListView
    private String[] tasksStringArray = {
            "Task1","Task2","Task3","Task4","Task5","Task6","Task7","Task8","Task9","Task10","Task11"
    }; //TODO this is hardcoded make method that get it from DB


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        chronometer = (Chronometer)findViewById(R.id.chronometer);
        btnStartStop = (Button) findViewById(R.id.btnStartStop);
        btnInterruption = (Button) findViewById(R.id.btnInterruption);
        btnInterruption.setVisibility(View.INVISIBLE);
        btnGetAllRecords = (Button) findViewById(R.id.btnGetAllRecords);
        btnGetReport = (Button) findViewById(R.id.btnGetReport);
        twTaskMessage_area = (TextView) findViewById(R.id.twTaskMessage_area);
        twLabelWorkingOn = (TextView) findViewById(R.id.twLabelWorkingOn);
        etEmployeeName = (EditText) findViewById(R.id.etEmployeeName);
        lwTasks = (ListView) findViewById(R.id.lwTasks);

        btnStartStop.setOnClickListener(this);
        btnInterruption.setOnClickListener(this);
        btnGetAllRecords.setOnClickListener(this);
        btnGetReport.setOnClickListener(this);

        lwTasks.setOnItemClickListener(this);


        ListAdapter tasksListAdapter = new ArrayAdapter<String>(
                HomeActivity.this,
                android.R.layout.simple_expandable_list_item_1,
                tasksStringArray
        );
        lwTasks.setAdapter(tasksListAdapter);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btnStartStop :
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
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }
                }

            break;

            case R.id.btnInterruption :
                if(isThereTaskStarted){
                    stopTask(false);
                }else{
                    // on normal usage that should never show
                    Toast.makeText(getApplicationContext(), R.string.note_start_task,
                            Toast.LENGTH_LONG
                    ).show();
                }if(isThereTaskStarted){
                stopTask(false);
            }else{
                // on normal usage that should never show
                Toast.makeText(getApplicationContext(), R.string.note_start_task,
                        Toast.LENGTH_LONG
                ).show();
            }

            break;

            case R.id.btnGetAllRecords :
                String result = "";

                for (TaskEntity task : taskMassiv) {
                    result += task.toString() + "\n\n";
                }

                Intent intentAllRecords = new Intent(HomeActivity.this, AllRecordsActivity.class);
                intentAllRecords.putExtra("reports", result);
                startActivity(intentAllRecords);
            break;

            case R.id.btnGetReport :
                //TODO implement this
                Intent intentGetReport = new Intent(HomeActivity.this, TasksReportActivity.class);
                String report = getString(R.string.head_of_table_report);

                for (String taskString : tasksStringArray) {
                    int timesOccur = 0;
                    long secondsTaskWorked = 0;

                    for (TaskEntity task :  taskMassiv) {
                        boolean hasMatch = task.getTaskName().equalsIgnoreCase(taskString);
                        if (hasMatch){
                            secondsTaskWorked += task.getSecondsWorked();
                            if (task.isNotInterrupted()){
                                timesOccur++;
                            }
                        }
                    }

                    if (secondsTaskWorked != 0){
                        float averageTime = timesOccur != 0 ? (float) (secondsTaskWorked / timesOccur) : (float) secondsTaskWorked;
                        report += String.format("%s:\ttimes: %d\tavg: %.2f\n",
                                taskString,
                                timesOccur,
                                averageTime);
                    }
                }

                intentGetReport.putExtra("tasksStringArray", report);
                startActivity(intentGetReport);
            break;

            default: return;
        }

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

    private void stopTask(boolean isNotInterrupted){
        chronometer.stop();
        manageElapsedTime(isNotInterrupted);
        chronometer.setBase(SystemClock.elapsedRealtime());
        btnStartStop.setText(R.string.start_text);
        isThereTaskStarted = false;
        btnInterruption.setVisibility(View.INVISIBLE);
        btnGetAllRecords.setVisibility(View.VISIBLE);
        btnGetReport.setVisibility(View.VISIBLE);
        twTaskMessage_area.setText(R.string.text_nothing);
    }

    private void manageElapsedTime(boolean isNotInterrupted){
        long secondsElapsed = (SystemClock.elapsedRealtime() - chronometer.getBase())/1000;
        TaskEntity entity = new TaskEntity(
                etEmployeeName.getText().toString(),
                TaskId++, //TODO: this is not good. Resolve it better.
                twTaskMessage_area.getText().toString(),
                secondsElapsed,
                isNotInterrupted,
                Calendar.getInstance().getTime()
        );

        taskMassiv.add(entity);

        if (isNotInterrupted){
            makeToast(" is not interrupted", secondsElapsed);
        }else{
            makeToast(" is interrupted", secondsElapsed);
        }

    }

    //TODO decide if you remove this
    private void makeToast(String interruptionText, long secondsElapsed){
        Toast.makeText(getApplicationContext(), "Seconds elapsed: " + secondsElapsed + interruptionText,
                Toast.LENGTH_SHORT).show();
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
}