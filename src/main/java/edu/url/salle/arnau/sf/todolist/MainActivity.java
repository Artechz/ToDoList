package edu.url.salle.arnau.sf.todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_TASK = 00;
    private static final String SP_SAVED_DATA = "SAVED_TASKS";

    private RecyclerView mTaskRecyclerView;
    private TaskAdapter mTaskAdapter;

    private ArrayList<Task> rTaskList = new ArrayList<>();

    private SharedPreferences sharedpreferences;
    private int dSPReadingPointer = 0;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        sharedpreferences = getSharedPreferences(SP_SAVED_DATA, Context.MODE_PRIVATE);
        //READING tasks from previous session; saving is done on onPause();
        while(sharedpreferences.contains(Integer.toString(dSPReadingPointer))) {
            try {
                rTaskList.add(new Task(sharedpreferences.getString(Integer.toString(dSPReadingPointer++), "null"),
                        dateFormat.parse(sharedpreferences.getString(Integer.toString(dSPReadingPointer++), "null")),
                        sharedpreferences.getBoolean(Integer.toString(dSPReadingPointer++), false)
                        ));
            } catch (ParseException e) {
                e.printStackTrace();
                Log.e("ERROR - MainActivity: ", "date parsing failed on " + dSPReadingPointer);
            }
        }

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        //toolbar.inflateMenu(R.menu.action_bar_main); tried to -not- need onCreateOptionsMenu() but failed miserably. Leaving it as hope (or warning) for the brave Arnau of the future.
        setSupportActionBar(toolbar);

        mTaskRecyclerView = findViewById(R.id.list_recycler_view);
        mTaskRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_addTask) {
                startActivityForResult(new Intent(MainActivity.this, TaskCreationActivity.class), REQUEST_TASK);
                return true;
            }
            return super.onMenuItemSelected(item.getItemId(), item); //honestly the first parameter is probably wrong but this method is the closest I found to super.onOptionsItemSelected() from Android docs tutorial on actionBar actions.
        });

        //adding initial tasks to avoid ugly empty list
        //rTaskList.add(new Task("Call mum"));
        //rTaskList.add(new Task("Deliver Android AC"));

        updateUI();
        //startActivityForResult(new Intent(MainActivity.this, TaskCreationActivity.class), REQUEST_TASK);
    }

    private void updateUI() {
        mTaskAdapter = new TaskAdapter(rTaskList);
        mTaskRecyclerView.setAdapter(mTaskAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(mTaskAdapter));
        itemTouchHelper.attachToRecyclerView(mTaskRecyclerView);
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode  != Activity.RESULT_OK) return;
        if (requestCode == REQUEST_TASK) {
            if (intent == null) return;
            rTaskList.add(TaskCreationActivity.getTaskFromIntent(intent));
            updateUI();
            Toast.makeText(MainActivity.this, R.string.toast_task_created, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_main, menu);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        //SAVING the session's tasks for the next time
        SharedPreferences.Editor editSP = sharedpreferences.edit();

        dSPReadingPointer = 0; //cutre way
        while (sharedpreferences.contains(Integer.toString(dSPReadingPointer))) {
            editSP.remove(Integer.toString(dSPReadingPointer++));
        }

        dSPReadingPointer = 0;
        for (Task t : rTaskList) {
            editSP.putString(Integer.toString(dSPReadingPointer++), t.getTitle());
            editSP.putString(Integer.toString(dSPReadingPointer++), dateFormat.format(t.getDate()));
            editSP.putBoolean(Integer.toString(dSPReadingPointer++), t.isCompleted());
        }
        editSP.commit();
    }

    /*
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_addTask:
                    Toast.makeText(this, "new Task!", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
            return true;
        }
    */
    private class TaskAdapter extends RecyclerView.Adapter<TaskHolder> {
        private final List<Task> rTasks;

        public TaskAdapter (List<Task> tasks) {
            rTasks = tasks;
        }

        public void deleteItem(int pos) {
            rTaskList.remove(pos);
            notifyItemRemoved(pos);
            updateUI();
        }

        @Override
        public TaskHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new TaskHolder(LayoutInflater.from(getApplicationContext()), parent);
        }

        @Override
        public void onBindViewHolder(TaskHolder holder, int position) {
            holder.bind(rTasks.get(position));
        }

        @Override
        public int getItemCount() {
            return rTasks.size();
        }

    }

    private static class TaskHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Task mTask;

        private TextView txtviewTitle;
        private TextView txtviewDate;
        private CheckBox checkboxStatus;

        public TaskHolder (LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_task, parent, false));
            //itemView.setOnClickListener(this);

            txtviewTitle = itemView.findViewById(R.id.task_title);
            txtviewDate = itemView.findViewById(R.id.task_dueDate);
            checkboxStatus = itemView.findViewById(R.id.checkbox_status);

            checkboxStatus.setOnCheckedChangeListener((compoundButton, b) -> {
                mTask.setCompleted(compoundButton.isChecked());
            });
        }

        public void bind(Task task) {
            mTask = task;
            txtviewTitle.setText(mTask.getTitle());
            txtviewDate.setText(mTask.getDate().toString());
            checkboxStatus.setChecked(mTask.isCompleted());
        }

        @Override
        public void onClick(View view) {
            //do nothing.
        }
    }

    private class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
        private final TaskAdapter mAdapter;

        public SwipeToDeleteCallback(TaskAdapter adapter) {
            super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
            mAdapter = adapter;
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            mAdapter.deleteItem(viewHolder.getAdapterPosition());
        }
    }
}