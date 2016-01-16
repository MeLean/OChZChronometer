package oczcalculator.milen.com.ochzchronometer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import java.util.ArrayList;


public class AllRecordsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_reports);

        DBHelper db = DBHelper.getInstance(this);
        ArrayList<TaskEntity> taskMassiv = new ArrayList<>();
        //TODO ASINCTASK
        try {
            taskMassiv = db.getAllTasks();
        } catch (Exception e) {//TODO better exceptionCatch
            e.printStackTrace();
        }

        StringBuilder result = new StringBuilder();
        for (TaskEntity task : taskMassiv) {
            result.append(task.toString());
            result.append(getString(R.string.two_new_rows));
        }

        TextView twReports = (TextView) findViewById(R.id.twReports);
        String reports = result.toString();

        if (reports != null && !(reports.equals(""))){
            twReports.setMovementMethod(new ScrollingMovementMethod());
            twReports.setText(reports);
        }else{
            twReports.setText(getString(R.string.no_entities_text));
        }
    }
}