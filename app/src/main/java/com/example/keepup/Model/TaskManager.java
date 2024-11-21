package com.example.keepup.Model;


import java.util.ArrayList;
import java.util.List;


public class TaskManager {

    private List<Task> taskList = new ArrayList<>();
    private int nextId = 1; // To simulate auto-increment IDs


    // Add a new task
    public void addTask(Task task) {

        task.setId(nextId++);
        task.setStatus(0); // Default status (e.g., incomplete)
        taskList.add(task);


    }

    public Task getTaskById(int id) {
        for (Task task : taskList) {
            if (task.getId() == id) {
                return task; // Return the task if found
            }
        }
        return null; // Return null if no task is found
    }


    // Get all tasks
    public List<Task> getAllTasks() {
        return taskList;
    }

    // Update a task by ID
    public boolean updateTask(int id, String newTask, int newStatus) {
        for (Task task : taskList) {
            if (task.getId() == id) {
                task.setTaskName(newTask);
                task.setStatus(newStatus);
                return true;
            }
        }
        return false; // Task not found
    }

    // Delete a task by ID
    public boolean deleteTask(int id) {
        return taskList.removeIf(task -> task.getId() == id);
    }


}
