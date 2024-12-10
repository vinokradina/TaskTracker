package com.example.tasktracker;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class CompletedTasksActivity extends AppCompatActivity {
    private TaskDatabaseHelper dbHelper;
    private ListView completedTaskListView;
    private TaskAdapter taskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_tasks);

        dbHelper = new TaskDatabaseHelper(this);
        completedTaskListView = findViewById(R.id.completedTaskListView);
        taskAdapter = new TaskAdapter(this, dbHelper.getCompletedTasks(), null, dbHelper);
        completedTaskListView.setAdapter(taskAdapter);
    }
}
