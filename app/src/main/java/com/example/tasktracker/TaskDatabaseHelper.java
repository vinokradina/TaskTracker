package com.example.tasktracker;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class TaskDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "TaskDB";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_NAME = "tasks";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_COMPLETED = "completed";
    private static final String COLUMN_DUE_DATE = "due_date";
    private static final String COLUMN_PRIORITY = "priority";
    private static final String COLUMN_CATEGORY = "category";

    public TaskDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_COMPLETED + " INTEGER, " +
                COLUMN_DUE_DATE + " TEXT, " +
                COLUMN_PRIORITY + " TEXT, " +
                COLUMN_CATEGORY + " TEXT)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, task.getTitle());
        values.put(COLUMN_DESCRIPTION, task.getDescription());
        values.put(COLUMN_COMPLETED, task.isCompleted() ? 1 : 0);
        values.put(COLUMN_DUE_DATE, task.getDueDate());
        values.put(COLUMN_PRIORITY, task.getPriority());
        values.put(COLUMN_CATEGORY, task.getCategory());
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public List<Task> getAllTasks(String sortBy) {
        List<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String orderBy = "";
        switch (sortBy) {
            case "category":
                orderBy = COLUMN_CATEGORY + " ASC";
                break;
            case "priority":
                orderBy = COLUMN_PRIORITY + " ASC";
                break;
            case "due_date":
                orderBy = COLUMN_DUE_DATE + " ASC";
                break;
            default:
                orderBy = COLUMN_ID + " ASC"; // По умолчанию сортируем по ID
        }

        Cursor cursor = db.query(
                TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                orderBy
        );

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
                @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));
                @SuppressLint("Range") boolean completed = cursor.getInt(cursor.getColumnIndex(COLUMN_COMPLETED)) == 1;
                @SuppressLint("Range") String dueDate = cursor.getString(cursor.getColumnIndex(COLUMN_DUE_DATE));
                @SuppressLint("Range") String priority = cursor.getString(cursor.getColumnIndex(COLUMN_PRIORITY));
                @SuppressLint("Range") String category = cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY));
                taskList.add(new Task(id, title, description, completed, dueDate, priority, category));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return taskList;
    }

    public void updateTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, task.getTitle());
        values.put(COLUMN_DESCRIPTION, task.getDescription());
        values.put(COLUMN_COMPLETED, task.isCompleted() ? 1 : 0);
        values.put(COLUMN_DUE_DATE, task.getDueDate());
        values.put(COLUMN_PRIORITY, task.getPriority());
        values.put(COLUMN_CATEGORY, task.getCategory());
        db.update(TABLE_NAME, values, COLUMN_ID + "=?", new String[]{String.valueOf(task.getId())});
        db.close();
    }

    public void deleteTask(int taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(taskId)});
        db.close();
    }

    public List<Task> searchTasks(String keyword) {
        List<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " +
                COLUMN_TITLE + " LIKE ? OR " +
                COLUMN_DESCRIPTION + " LIKE ? OR " +
                COLUMN_CATEGORY + " LIKE ? OR " +
                COLUMN_PRIORITY + " LIKE ?";

        Cursor cursor = db.rawQuery(query, new String[]{"%" + keyword + "%", "%" + keyword + "%", "%" + keyword + "%", "%" + keyword + "%"});

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
                @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));
                @SuppressLint("Range") boolean completed = cursor.getInt(cursor.getColumnIndex(COLUMN_COMPLETED)) == 1;
                @SuppressLint("Range") String dueDate = cursor.getString(cursor.getColumnIndex(COLUMN_DUE_DATE));
                @SuppressLint("Range") String priority = cursor.getString(cursor.getColumnIndex(COLUMN_PRIORITY));
                @SuppressLint("Range") String category = cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY));
                taskList.add(new Task(id, title, description, completed, dueDate, priority, category));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return taskList;
    }
}