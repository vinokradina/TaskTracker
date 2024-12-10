package com.example.tasktracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskClickListener {
    private TaskDatabaseHelper dbHelper;
    private ListView taskListView;
    private TaskAdapter taskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new TaskDatabaseHelper(this);
        taskListView = findViewById(R.id.taskListView);
        taskAdapter = new TaskAdapter(this, dbHelper.getAllTasks(), this, dbHelper);
        taskListView.setAdapter(taskAdapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddTaskActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        taskAdapter.updateTasks(dbHelper.getAllTasks());
    }

    @Override
    public void onTaskLongClick(Task task) {
        showContextMenu(task);
    }

    private void showContextMenu(Task task) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Выберите действие");
        builder.setItems(new String[]{"Редактировать", "Удалить"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    editTask(task);
                } else if (which == 1) {
                    deleteTask(task);
                }
            }
        });
        builder.show();
    }

    private void editTask(Task task) {
        Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
        intent.putExtra("task_id", task.getId());
        intent.putExtra("task_title", task.getTitle());
        intent.putExtra("task_description", task.getDescription());
        intent.putExtra("task_due_date", task.getDueDate());
        startActivity(intent);
    }

    private void deleteTask(Task task) {
        dbHelper.deleteTask(task.getId());
        taskAdapter.updateTasks(dbHelper.getAllTasks());
    }
}