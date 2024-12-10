package com.example.tasktracker;

public class Task {
    private int id;
    private String title;
    private String description;
    private boolean isCompleted;
    private String dueDate;
    private String priority;
    private String category;
    private boolean isHeader;

    public Task(int id, String title, String description, boolean isCompleted, String dueDate, String priority, String category) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.isCompleted = isCompleted;
        this.dueDate = dueDate;
        this.priority = priority;
        this.category = category;
        this.isHeader = false;
    }

    public Task(String title) {
        this.title = title;
        this.isHeader = true;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getPriority() {
        return priority;
    }

    public String getCategory() {
        return category;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}