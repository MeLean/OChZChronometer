package oczcalculator.milen.com.ochzchronometer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;


public class SetTaskActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btn_set_tasks;
    private EditText etTaskInput;
    private CheckBox chbox_delete_cur_tasks;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor preferencesEditor;
    private String stringSeparator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_task);
        stringSeparator = getIntent().getExtras().getString("stringsSeparator");
        btn_set_tasks = (Button) findViewById(R.id.btn_set_tasks);
        etTaskInput = (EditText) findViewById(R.id.etTaskInput);
        chbox_delete_cur_tasks = (CheckBox) findViewById(R.id.chbox_delete_cur_tasks);

        btn_set_tasks.setOnClickListener(this);
        chbox_delete_cur_tasks.setOnClickListener(this);

        etTaskInput.setHint(
                (String.format("%s \"%s\"",
                        getString(R.string.first_part_inputTasks_hint),
                        stringSeparator)
                )
        );

        sharedPreferences = getSharedPreferences("ListViewTaskNames",0);
        preferencesEditor = sharedPreferences.edit();
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.chbox_delete_cur_tasks : {
                if (chbox_delete_cur_tasks.isChecked()){
                    btn_set_tasks.setText(R.string.delete_cur_tasks);
                }else {
                    btn_set_tasks.setText(R.string.append_cur_tasks);
                }
            } break;
            case R.id.btn_set_tasks : {
                String stringForSave;
                String etTaskInputText = String.valueOf(etTaskInput.getText());
                if (chbox_delete_cur_tasks.isChecked()){
                    stringForSave = etTaskInputText;
                }else{
                    stringForSave = String.format("%s%s%s",//need to place an TASK_STRING_SEPARATOR after the current string of tasks
                            sharedPreferences.getString("stringList", null),
                            stringSeparator,
                            etTaskInputText
                    );
                }

                if (stringForSave != null && !stringForSave.equals("")){
                    putStringInSharedPreferences(stringForSave);
                }
                // get back to previous activity in this case MainActivity
                this.finish();
            } break;

            default: return;
        }
    }

    private void putStringInSharedPreferences(String stringForSave){
        preferencesEditor.putString("stringList", stringForSave);
        preferencesEditor.commit();
    }
}
