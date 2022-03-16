package edu.url.salle.arnau.sf.todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class TaskCreationActivity extends AppCompatActivity {

    private static final String EXTRA_TASK_NAME = "TASK_NAME";
    private static final String TAG = "WHAT";
    private EditText edittxtTaskName;
    private Button bttnAddTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_creation);

        Toolbar toolbar = findViewById(R.id.task_toolbar);
        setSupportActionBar(toolbar);

        edittxtTaskName = findViewById(R.id.edittxt_input_task_name);
        bttnAddTask = findViewById(R.id.button_addTask);

        bttnAddTask.setOnClickListener(v -> addTask());

        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_addTask) {
                //startActivityForResult(new Intent(MainActivity.this, TaskCreationActivity.class), REQUEST_TASK);
                addTask();
                return true;
            }
            return super.onMenuItemSelected(item.getItemId(), item); //honestly the first parameter is probably wrong but this method is the closest I found to super.onOptionsItemSelected() from Android docs tutorial on actionBar actions.
        });
    }

    private void addTask() {
        setResult(RESULT_OK, new Intent().putExtra(EXTRA_TASK_NAME, edittxtTaskName.getText().toString()));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_main, menu);
        return true;
    }

    public static Task getTaskFromIntent(@NonNull Intent intent) {
        return new Task(intent.getStringExtra(EXTRA_TASK_NAME));
    }
}