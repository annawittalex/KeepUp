package com.example.keepup.Model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keepup.MainActivity;
import com.example.keepup.OverviewActivity;
import com.example.keepup.R;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> tasks;

    public TaskAdapter(List<Task> tasks) {
        this.tasks = tasks;

        }
    public void updateTasks(List<Task> newTasks) {
        tasks.clear();
        tasks.addAll(newTasks);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.bind(task, position, this);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        private TextView taskNameTextView;
        private CheckBox taskCheckBox;
        private TextView taskDeadlineTextView;
        private ImageButton editButton;

        public TaskViewHolder(View itemView) {
            super(itemView);
            taskNameTextView = itemView.findViewById(R.id.task_name);
            taskCheckBox = itemView.findViewById(R.id.checkbox);
            taskDeadlineTextView = itemView.findViewById(R.id.task_deadline);
            editButton = itemView.findViewById(R.id.edit_button);
        }

        public void bind(Task task, int position, TaskAdapter adapter) {

            taskNameTextView.setText(task.getTaskName());
            taskDeadlineTextView.setText(task.getDeadline() != null
                    ? android.text.format.DateFormat.format("MMM dd, yyyy", task.getDeadline()).toString()
                    : "No Deadline");
            taskCheckBox.setChecked(task.getStatus() == 1);


            taskCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                task.setStatus(isChecked ? 1 : 0);
                if (itemView.getContext() instanceof OverviewActivity) {
                    ((OverviewActivity) itemView.getContext()).updateProgress();
                }
            });


            editButton.setOnClickListener(v -> {
                if (itemView.getContext() instanceof OverviewActivity) {
                    ((OverviewActivity) itemView.getContext()).openEditTaskDialog(task, position);
                } else if (itemView.getContext() instanceof MainActivity) {
                    ((MainActivity) itemView.getContext()).openEditTaskDialog(task, position);
                }

            });
        }
    }
}
