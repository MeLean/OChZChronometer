package oczcalculator.milen.com.ochzchronometer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class TasksReportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_report);

        Bundle taskReport = getIntent().getExtras();
        String taskReportString = taskReport.getString("tasksStringArray");

        TextView twTaskReport = (TextView) findViewById(R.id.twTaskReport);
        if (!(taskReportString.equals(getString(R.string.head_of_table_report)))){
            twTaskReport.setMovementMethod(new ScrollingMovementMethod());
            twTaskReport.setText(taskReportString);
        }
    }
}