package com.example.tasktracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddTaskActivity extends AppCompatActivity {
    private EditText titleEditText, descriptionEditText, dueDateEditText;
    private Spinner prioritySpinner, categorySpinner;
    private TaskDatabaseHelper dbHelper;
    private int taskId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        dbHelper = new TaskDatabaseHelper(this);

        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        dueDateEditText = findViewById(R.id.dueDateEditText);
        prioritySpinner = findViewById(R.id.prioritySpinner);
        categorySpinner = findViewById(R.id.categorySpinner);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("task_id")) {
            taskId = intent.getIntExtra("task_id", -1);
            String title = intent.getStringExtra("task_title");
            String description = intent.getStringExtra("task_description");
            String dueDate = intent.getStringExtra("task_due_date");
            String priority = intent.getStringExtra("task_priority");
            String category = intent.getStringExtra("task_category");

            titleEditText.setText(title);
            descriptionEditText.setText(description);
            dueDateEditText.setText(dueDate);
            setSpinnerSelection(prioritySpinner, priority);
            setSpinnerSelection(categorySpinner, category);
        }

        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEditText.getText().toString().trim();
                String description = descriptionEditText.getText().toString().trim();
                String dueDate = dueDateEditText.getText().toString().trim();
                String priority = prioritySpinner.getSelectedItem().toString();
                String category = categorySpinner.getSelectedItem().toString();

                if (title.isEmpty()) {
                    Toast.makeText(AddTaskActivity.this, "Поле 'Название' обязательно для заполнения", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isValidDate(dueDate)) {
                    Toast.makeText(AddTaskActivity.this, "Поле 'Срок выполнения' должно быть в формате YYYY-MM-DD", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (taskId != -1) {
                    Task updatedTask = new Task(taskId, title, description, false, dueDate, priority, category);
                    dbHelper.updateTask(updatedTask);
                } else {
                    dbHelper.addTask(new Task(0, title, description, false, dueDate, priority, category));
                }

                finish();
            }
        });
    }

    private boolean isValidDate(String date) {
        if (date.isEmpty()) {
            return true;
        }

        String datePattern = "^\\d{4}-\\d{2}-\\d{2}$";
        return date.matches(datePattern);
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }
}