package oczcalculator.milen.com.ochzchronometer;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.SQLException;


public class SetTasksActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_set_tasks;
    private EditText etTaskInput;
    private CheckBox chbox_delete_cur_tasks;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor preferencesEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_tasks);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_set_tasks);
        setSupportActionBar(toolbar);

        btn_set_tasks = (Button) findViewById(R.id.btn_set_tasks);
        etTaskInput = (EditText) findViewById(R.id.etTaskInput);
        chbox_delete_cur_tasks = (CheckBox) findViewById(R.id.chbox_delete_cur_tasks);

        btn_set_tasks.setOnClickListener(this);
        chbox_delete_cur_tasks.setOnClickListener(this);

        etTaskInput.setHint(
                (String.format("%s \"%s\"",
                        getString(R.string.first_part_inputTasks_hint),
                        Utils.TASK_SEPARATOR)
                )
        );

        sharedPreferences = getSharedPreferences(Utils.SHARED_PREFERENCES_FILE_NAME, 0);
        preferencesEditor = sharedPreferences.edit();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chbox_delete_cur_tasks: {
                if (chbox_delete_cur_tasks.isChecked()) {
                    btn_set_tasks.setText(R.string.delete_cur_tasks);
                } else {
                    btn_set_tasks.setText(R.string.append_cur_tasks);
                }
            }
            break;
            case R.id.btn_set_tasks: {
                String stringForSave;
                String etTaskInputText = String.valueOf(etTaskInput.getText());
                if (chbox_delete_cur_tasks.isChecked()) {
                    stringForSave = etTaskInputText;
                } else {
                    // todo purify the method
                    // checks if current string ends whit stringSeparator
                    String preferencesString = sharedPreferences.getString(Utils.SHARED_PREFERENCES_STRING_NAME, null);
                    String lastStringChar;
                    if (preferencesString != null) {
                        lastStringChar = preferencesString.substring(preferencesString.length() - 1);

                        if (lastStringChar.equals(Utils.TASK_SEPARATOR)) {
                            //deleting last task separator
                            preferencesString = preferencesString.substring(0, preferencesString.length() - 1);
                        }
                    }

                    stringForSave = String.format("%s%s%s",
                            preferencesString,
                            Utils.TASK_SEPARATOR,
                            etTaskInputText
                    );
                }

                if (stringForSave != null && !stringForSave.equals("")) {
                    putStringInSharedPreferences(stringForSave);
                }
                // get back to previous activity in this case MainActivity
                this.finish();
            }
            break;
            default:
                Toast.makeText(SetTasksActivity.this, R.string.dont_know_what_to_do, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void putStringInSharedPreferences(String stringForSave) {
        preferencesEditor.putString(Utils.SHARED_PREFERENCES_STRING_NAME, stringForSave);
        preferencesEditor.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_set_tasks, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_load_cur_tasks:
                String preferenceString = sharedPreferences.getString(Utils.SHARED_PREFERENCES_STRING_NAME, null);
                if (preferenceString == null) {
                    Toast.makeText(SetTasksActivity.this, R.string.no_task_for_loading, Toast.LENGTH_SHORT).show();
                    break;
                }

                String pureTasksString = Utils.purifyString(preferenceString);
                etTaskInput.setText(pureTasksString);
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
