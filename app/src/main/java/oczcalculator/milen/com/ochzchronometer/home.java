package oczcalculator.milen.com.ochzchronometer;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
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

public class home extends AppCompatActivity {
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

        //making tasksArray for LisView
        String[] tasksArray = {
                "Task1","Task2","Task3","Task4","Task5","Task6","Task7","Task8","Task9","Task10","Task11"
        }; //TODO this is hardcoded make method that get it from DB

        ListAdapter tasksListAdapter = new ArrayAdapter<String>(
                home.this,
                android.R.layout.simple_expandable_list_item_1,
                tasksArray
        );
        lwTasks.setAdapter(tasksListAdapter);

        lwTasks.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (isThereTaskStarted) {
                            return;
                        }

                        String itemText = parent.getItemAtPosition(position).toString();
                        twLabelWorkingOn.setText(getString(R.string.task_chosen_text));
                        twTaskMessage_area.setText(itemText);
                    }
                }
        );

        btnStartStop.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
                    }
                }
        );

        btnInterruption.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isThereTaskStarted){
                            stopTask(false);
                        }else{
                            // on normal usage that should never show
                            Toast.makeText(getApplicationContext(), R.string.note_start_task,
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }
                }
        );

        btnGetAllRecords.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String result = "";

                        for (TaskEntity task : taskMassiv) {
                            result += task.toString() + "\n\n";
                        }

                        Intent intent = new Intent(home.this, AllReports.class);
                        intent.putExtra("reports", result);
                        startActivity(intent);
                       /*Toast.makeText(getApplicationContext(), result,
                                Toast.LENGTH_LONG
                        ).show();*/
                    }
                }
        );

        btnGetReport.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO implement this
                        String result = "Implement ME";

                        Toast.makeText(getApplicationContext(), result,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
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

    private void makeToast(String interruptionText, long secondsElapsed){
        Toast.makeText(getApplicationContext(), "Seconds elapsed: " + secondsElapsed + interruptionText,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
