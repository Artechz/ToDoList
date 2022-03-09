package edu.url.salle.arnau.sf.todolist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_TASK = 00;

    private RecyclerView mTaskRecyclerView;
    private TaskAdapter mTaskAdapter;

    private ArrayList<Task> rTaskList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTaskRecyclerView = findViewById(R.id.list_recycler_view);
        mTaskRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        rTaskList.add(new Task("Call mum"));
        rTaskList.add(new Task("Deliver Android AC"));

        updateUI();
        startActivityForResult(new Intent(MainActivity.this, TaskCreationActivity.class), REQUEST_TASK);
    }

    private void updateUI() {
        mTaskAdapter = new TaskAdapter(rTaskList);
        mTaskRecyclerView.setAdapter(mTaskAdapter);
    }

    private class TaskAdapter extends RecyclerView.Adapter<TaskHolder> {
        private List<Task> rTasks;

        public TaskAdapter (List<Task> tasks) {
            rTasks = tasks;
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

    private class TaskHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Task mTask;

        private TextView txtviewTitle;
        private TextView txtviewDate;
        private CheckBox checkboxStatus;

        public TaskHolder (LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_task, parent, false));
            itemView.setOnClickListener(this);

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
        }

        @Override
        public void onClick(View view) {
            //do nothing.
        }
    }
}