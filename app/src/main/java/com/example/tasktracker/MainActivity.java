package com.example.tasktracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskClickListener {
    private TaskDatabaseHelper dbHelper;
    private ListView taskListView;
    private TaskAdapter taskAdapter;
    private Spinner sortSpinner;
    private EditText searchEditText;
    private FloatingActionButton completedTasksButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new TaskDatabaseHelper(this);
        taskListView = findViewById(R.id.taskListView);
        taskAdapter = new TaskAdapter(this, dbHelper.getAllTasks(""), this, dbHelper);
        taskListView.setAdapter(taskAdapter);

        searchEditText = findViewById(R.id.searchEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keyword = s.toString().trim();
                if (keyword.isEmpty()) {
                    taskAdapter.updateTasks(dbHelper.getAllTasks(""));
                } else {
                    taskAdapter.updateTasks(dbHelper.searchTasks(keyword));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        sortSpinner = findViewById(R.id.sortSpinner);
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedSort = sortSpinner.getSelectedItem().toString();
                List<Task> sortedTasks = new ArrayList<>();

                if (selectedSort.equals("По категории")) {
                    sortedTasks = sortByCategory(dbHelper.getAllTasks(""));
                } else if (selectedSort.equals("По приоритету")) {
                    sortedTasks = sortByPriority(dbHelper.getAllTasks(""));
                } else if (selectedSort.equals("По сроку выполнения")) {
                    sortedTasks = dbHelper.getAllTasks("due_date");
                } else {
                    sortedTasks = dbHelper.getAllTasks("");
                }

                taskAdapter.updateTasks(sortedTasks);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        completedTasksButton = findViewById(R.id.completedTasksButton);
        completedTasksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CompletedTasksActivity.class));
            }
        });

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
        taskAdapter.updateTasks(dbHelper.getAllTasks(""));
    }

    private List<Task> sortByCategory(List<Task> tasks) {
        List<Task> sortedTasks = new ArrayList<>();
        Map<String, List<Task>> categoryMap = new HashMap<>();

        for (Task task : tasks) {
            String category = task.getCategory();
            if (!categoryMap.containsKey(category)) {
                categoryMap.put(category, new ArrayList<>());
            }
            categoryMap.get(category).add(task);
        }

        for (String category : categoryMap.keySet()) {
            sortedTasks.add(new Task(category));
            sortedTasks.addAll(categoryMap.get(category));
        }

        return sortedTasks;
    }

    private List<Task> sortByPriority(List<Task> tasks) {
        List<Task> sortedTasks = new ArrayList<>();
        Map<String, List<Task>> priorityMap = new HashMap<>();

        for (Task task : tasks) {
            String priority = task.getPriority();
            if (!priorityMap.containsKey(priority)) {
                priorityMap.put(priority, new ArrayList<>());
            }
            priorityMap.get(priority).add(task);
        }

        for (String priority : priorityMap.keySet()) {
            sortedTasks.add(new Task(priority));
            sortedTasks.addAll(priorityMap.get(priority));
        }

        return sortedTasks;
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
        intent.putExtra("task_priority", task.getPriority());
        intent.putExtra("task_category", task.getCategory());
        startActivity(intent);
    }

    private void deleteTask(Task task) {
        dbHelper.deleteTask(task.getId());
        taskAdapter.updateTasks(dbHelper.getAllTasks(""));
    }

    public void refreshTaskList() {
        taskAdapter.updateTasks(dbHelper.getAllTasks(""));
    }
}