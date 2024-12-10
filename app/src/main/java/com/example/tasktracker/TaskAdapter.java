package com.example.tasktracker;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class TaskAdapter extends BaseAdapter {
    private Context context;
    private List<Task> taskList;
    private OnTaskClickListener listener;
    private TaskDatabaseHelper dbHelper; //

    public TaskAdapter(Context context, List<Task> taskList, OnTaskClickListener listener, TaskDatabaseHelper dbHelper) {
        this.context = context;
        this.taskList = taskList;
        this.listener = listener;
        this.dbHelper = dbHelper;
    }

    public void updateTasks(List<Task> newTaskList) {
        this.taskList = newTaskList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return taskList.size();
    }

    @Override
    public Object getItem(int position) {
        return taskList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return taskList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.task_item, parent, false);
        }

        Task task = taskList.get(position);

        TextView titleTextView = convertView.findViewById(R.id.titleTextView);
        titleTextView.setText(task.getTitle());

        TextView descriptionTextView = convertView.findViewById(R.id.descriptionTextView);
        descriptionTextView.setText(task.getDescription());

        TextView dueDateTextView = convertView.findViewById(R.id.dueDateTextView);
        dueDateTextView.setText("Срок: " + task.getDueDate());

        CheckBox completedCheckBox = convertView.findViewById(R.id.completedCheckBox);
        completedCheckBox.setChecked(task.isCompleted());

        completedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                task.setCompleted(isChecked);
                dbHelper.updateTask(task);

                if (isChecked) {
                    showWelldoneImage();
                }
            }
        });

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (listener != null) {
                    listener.onTaskLongClick(task);
                }
                return true;
            }
        });

        return convertView;
    }

    private void showWelldoneImage() {
        ImageView welldoneImageView = ((MainActivity) context).findViewById(R.id.welldoneImageView);
        welldoneImageView.setVisibility(View.VISIBLE);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                welldoneImageView.setVisibility(View.GONE);
            }
        }, 1500);
    }

    public interface OnTaskClickListener {
        void onTaskLongClick(Task task);
    }
}