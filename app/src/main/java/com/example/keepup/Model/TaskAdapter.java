package com.example.keepup.Model;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keepup.R;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> tasks;

    // Constructor to receive the list of tasks
    public TaskAdapter(List<Task> tasks) {
        Log.d("TaskAdapter", "Adapter created with tasks size: " + tasks.size());
        this.tasks = tasks;
    }

    // Create a new view holder to represent each task in the list
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the task_item layout (which contains a CheckBox and TextView)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(view);
    }

    // Bind data to the view holder (task details)
    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
    // Get the task for this position
        holder.bind(task); // Bind the task to the view holder
    }

    // Return the total number of tasks in the list
    @Override
    public int getItemCount() {
        return tasks.size();
    }

    // ViewHolder class to hold references to views in each task item
    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        private TextView taskNameTextView;
        private CheckBox taskCheckBox;
        private TextView taskDeadlineTextView;

        public TaskViewHolder(View itemView) {
            super(itemView);
            taskNameTextView = itemView.findViewById(R.id.task_name);  // The TextView for task name
            taskCheckBox = itemView.findViewById(R.id.checkbox); // The CheckBox for task completion
            taskDeadlineTextView = itemView.findViewById(R.id.task_deadline); // The TextView for deadline
        }

        // Bind the task data to the views
        public void bind(Task task) {

            taskNameTextView.setText(task.getTaskName()); // Set task name
            taskDeadlineTextView.setText(task.getDeadline() != null ?
                    android.text.format.DateFormat.format("MMM dd, yyyy", task.getDeadline()).toString() : "No Deadline"); // Format deadline
            taskCheckBox.setChecked(task.getStatus() == 1); // If status is 1 (completed), set checkbox as checked
            taskCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {

                // Update task status when checkbox is toggled
                task.setStatus(isChecked ? 1 : 0);
            });
        }
    }
}
