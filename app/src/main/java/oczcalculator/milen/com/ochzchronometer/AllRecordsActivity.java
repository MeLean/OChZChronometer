package oczcalculator.milen.com.ochzchronometer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;


public class AllRecordsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_reports);

        TextView twReports = (TextView) findViewById(R.id.twReports);
        Bundle getReports = getIntent().getExtras();
        String reports = getReports.getString(Utils.GET_ALL_REPORTS_EXTRA_STRING);

        if (!(reports.equals(""))){
            twReports.setMovementMethod(new ScrollingMovementMethod());
            twReports.setText(reports);
        }else{
            twReports.setText(getString(R.string.no_entities_text));
        }
    }
}