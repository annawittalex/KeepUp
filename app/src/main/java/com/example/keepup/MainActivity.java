package com.example.keepup;

import com.example.keepup.Model.Task;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keepup.Model.TaskAdapter;
import com.example.keepup.Model.TaskManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    private TaskManager taskManager;
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private ArrayList<Task> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        taskManager = new TaskManager();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        displayTasksFromIntentOrTaskManager();
        NavigateTo();

    }
    public void NavigateTo(){
        BottomNavigationView bottomNavigationView = findViewById(R.id.navBar);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_mytasks) {
                return true;
            } else if (id == R.id.navigation_create) {
                openAddTaskDialog();
                return  true;
            } else if (id == R.id.navigation_overview) {
                Intent intent = new Intent(MainActivity.this, OverviewActivity.class);
                if (taskList != null) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("tasks", taskList);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "No tasks available", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            return false;
        });
    }
    public void displayTasksFromIntentOrTaskManager() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            taskList = bundle.getParcelableArrayList("tasks");
            if (taskList != null) {
                taskAdapter = new TaskAdapter(taskList);
                recyclerView.setAdapter(taskAdapter);
            } else {
                taskList = (ArrayList<Task>) taskManager.getAllTasks(); // Default to TaskManager if bundle is empty
                taskAdapter = new TaskAdapter(taskList);
                recyclerView.setAdapter(taskAdapter);
            }
        } else {
            taskList = (ArrayList<Task>) taskManager.getAllTasks(); // Default to TaskManager if no Bundle
            taskAdapter = new TaskAdapter(taskList);
            recyclerView.setAdapter(taskAdapter);
        }
    }

    private void openAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Task");

        // Create a custom view for the dialog content
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(24, 24, 24, 24);


        // Set minimum width and height for bigger dialog
        layout.setMinimumWidth(getResources().getDisplayMetrics().widthPixels - 64);
        layout.setMinimumHeight(getResources().getDisplayMetrics().heightPixels / 4);


        final EditText taskNameInput = new EditText(this);
        taskNameInput.setHint("Task Name");
        layout.addView(taskNameInput);
        final Button selectDeadlineButton = new Button(this);
        selectDeadlineButton.setText("Select Deadline");
        layout.addView(selectDeadlineButton);
        final TextView selectedDateText = new TextView(this);
        selectedDateText.setText("No deadline selected");
        layout.addView(selectedDateText);
        builder.setView(layout);

        final Calendar calendar = Calendar.getInstance();
        selectDeadlineButton.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        calendar.set(year, month, dayOfMonth);
                        selectedDateText.setText("Deadline: " + (month + 1) + "/" + dayOfMonth + "/" + year);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            // Set minimum date to today not to be able to chose yesterday
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);
            datePickerDialog.getDatePicker().setMinDate(today.getTimeInMillis());

            datePickerDialog.show();
        });
        builder.setPositiveButton("Add", (dialog, which) -> {
            String taskName = taskNameInput.getText().toString();
            Date deadline = calendar.getTime();
            if (!taskName.isEmpty()) {
                Task task = new Task();
                task.setTaskName(taskName);
                task.setDeadline(deadline);
                taskList.add(task);
                 taskAdapter.notifyItemInserted(taskList.size() - 1);
                recyclerView.scrollToPosition(taskAdapter.getItemCount() - 1);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
    public void openEditTaskDialog(Task task, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Task");
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(16, 16, 16, 16);
        final EditText taskNameInput = new EditText(this);
        taskNameInput.setText(task.getTaskName());
        layout.addView(taskNameInput);
        final Button selectDeadlineButton = new Button(this);
        selectDeadlineButton.setText("Select Deadline");
        layout.addView(selectDeadlineButton);
        final TextView selectedDateText = new TextView(this);
        if (task.getDeadline() != null) {
            selectedDateText.setText(android.text.format.DateFormat.format("MMM dd, yyyy", task.getDeadline()));
        } else {
            selectedDateText.setText("No deadline selected");
        }
        layout.addView(selectedDateText);
        builder.setView(layout);



        final Calendar calendar = Calendar.getInstance();
        selectDeadlineButton.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        calendar.set(year, month, dayOfMonth);
                        selectedDateText.setText("Deadline: " + (month + 1) + "/" + dayOfMonth + "/" + year);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            // Set minimum date to today not to be able to chose yesterday
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);
            datePickerDialog.getDatePicker().setMinDate(today.getTimeInMillis());

            datePickerDialog.show();
        });
        builder.setPositiveButton("Update", (dialog, which) -> {
            String updatedTaskName = taskNameInput.getText().toString();
            Date updatedDeadline = calendar.getTime();
            if (!updatedTaskName.isEmpty()) {
                task.setTaskName(updatedTaskName);
                task.setDeadline(updatedDeadline);
                taskAdapter.notifyItemChanged(position);
            }
            taskList.get(position).setTaskName(taskNameInput.getText().toString());
            taskList.get(position).setDeadline(updatedDeadline);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

}
