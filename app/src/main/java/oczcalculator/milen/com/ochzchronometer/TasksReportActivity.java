package oczcalculator.milen.com.ochzchronometer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import java.util.ArrayList;

public class TasksReportActivity extends AppCompatActivity {
    private DBHelper db;
    private ArrayList<TaskEntity> taskMassiv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_report);
        taskMassiv = new ArrayList<>();
        db = DBHelper.getInstance(this);
        try {
            taskMassiv = db.getAllTasks();
        } catch (Exception e) {//TODO better exceptionCatch
            e.printStackTrace();
        }

        SharedPreferences taskNamesPref = getSharedPreferences(Utils.SHARED_PREFERENCES_FILE_NAME, 0);
        String[] tasksNames = Utils.splitBySeparator(taskNamesPref.getString(Utils.SHARED_PREFERENCES_STRING_NAME, null));
        String taskReportString = Utils.makeStringReport(tasksNames, taskMassiv);


        TextView twTaskReport = (TextView) findViewById(R.id.twTaskReport);
        if (!(taskReportString.equals(""))) {
            twTaskReport.setMovementMethod(new ScrollingMovementMethod());
            twTaskReport.setText(taskReportString);
        }else{
            twTaskReport.setText(getString(R.string.no_entities_text));
        }
    }
}