package oczcalculator.milen.com.ochzchronometer;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class allRecords extends AppCompatActivity {

    private TextView twAllRecords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_records);

        twAllRecords = (TextView)findViewById(R.id.twAllRecords);

        twAllRecords.setText("I am bad Mamba Djamba! Implement me!");


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
