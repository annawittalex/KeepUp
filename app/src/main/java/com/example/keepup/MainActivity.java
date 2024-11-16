package com.example.keepup;

import com.example.keepup.Model.Task;
import com.example.keepup.R;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

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

import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    private TaskManager taskManager;
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        taskManager = new TaskManager(); // Initialize TaskManager


        recyclerView = findViewById(R.id.recyclerView);

        // Set up the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        taskAdapter = new TaskAdapter(taskManager.getAllTasks()); // Initialize adapter with current tasks
        recyclerView.setAdapter(taskAdapter);


//--------------------Navigation-----------------------------------------------------//
        BottomNavigationView bottomNavigationView = findViewById(R.id.navBar);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_mytasks) {

                return true;
            } else if (id == R.id.navigation_create) {

                openAddTaskDialog();
                return true;
            } else if (id == R.id.navigation_overview) {
                // switch screen from Main Activity to OverviewActivity
                Intent intent = new Intent(MainActivity.this, OverviewActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }



        private void openAddTaskDialog() {
            // Example method to open a dialog for entering a new task (you can create your own layout for this)
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Add New Task");

            // Create a layout for the dialog
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(16, 16, 16, 16);

            // Input field for task name
            final EditText taskNameInput = new EditText(this);
            taskNameInput.setHint("Task Name");
            layout.addView(taskNameInput);

            // Button to select deadline
            final Button selectDeadlineButton = new Button(this);
            selectDeadlineButton.setText("Select Deadline");
            layout.addView(selectDeadlineButton);

            // TextView to display the selected deadline
            final TextView selectedDateText = new TextView(this);
            selectedDateText.setText("No deadline selected");
            layout.addView(selectedDateText);

            // Add layout to dialog
            builder.setView(layout);


            // Create a DatePickerDialog for the deadline selection
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

            // Positive button to add the task
            builder.setPositiveButton("Add", (dialog, which) -> {
                String taskName = taskNameInput.getText().toString();
                Date deadline = calendar.getTime();
                if (!taskName.isEmpty()) {

                    Task task = new Task();
                    task.setTaskName(taskName);
                    task.setDeadline(deadline);
                    taskManager.addTask(task);
                    taskAdapter.notifyItemInserted(taskManager.getAllTasks().size() - 1);
                    recyclerView.scrollToPosition(taskAdapter.getItemCount() - 1);
                }
            });

            // Negative button to cancel
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

            // Show the dialog
            builder.show();
        }


    }
