package com.example.keepup;

import com.example.keepup.Model.Task;
import com.example.keepup.R;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keepup.Model.TaskAdapter;
import com.example.keepup.Model.TaskManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

// MainActivity.java
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

        // Fetch data: First check the Bundle for tasks, else use TaskManager
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
            } else if (id == R.id.navigation_overview) {
                // Create the Intent to navigate to OverviewActivity
                Intent intent = new Intent(MainActivity.this, OverviewActivity.class);

                // Ensure taskList is not null
                if (taskList != null) {
                    // Create a Bundle and pass the task list
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("tasks", taskList);
                    intent.putExtras(bundle);

                    startActivity(intent);
                } else {
                    Toast.makeText(this, "No tasks available", Toast.LENGTH_SHORT).show();
                }
            }
            return false;
        });
    }
    // Fetch tasks either from Intent or TaskManager
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
        // Example method to open a dialog for entering a new task (you can create your own layout for this)
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Task");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(16, 16, 16, 16);

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
}
