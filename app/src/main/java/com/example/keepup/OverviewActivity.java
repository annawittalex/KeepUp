package com.example.keepup;

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

import com.example.keepup.Model.Task;
import com.example.keepup.Model.TaskAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.stream.Collectors;


public class OverviewActivity extends AppCompatActivity {

    private RecyclerView taskRecyclerView;
    private TaskAdapter taskAdapter;
    private ArrayList<Task> taskList;
    private TextView progressText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_overview);

        progressText = findViewById(R.id.taskCompletionText);
        showDynamicGreeting();
        showDynamicDate();
        navigateTo();
        displayTasksFromIntent();
    }

    public void updateProgress() {
        if (taskList != null && !taskList.isEmpty()) {
            int totalTasks = taskList.size();
            int completedTasks = (int) taskList.stream()
                    .filter(task -> task.getStatus() == 1)
                    .count();

            double percentage = (completedTasks * 100.0) / totalTasks;
            progressText.setText(String.format("Progress: %.1f%% (%d/%d tasks completed)",
                    percentage, completedTasks, totalTasks));
        } else {
            progressText.setText("Tasks completed: 0% (0/0)");
        }
    }


    public void displayTasksFromIntent() {
        taskRecyclerView = findViewById(R.id.taskRecyclerView);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            taskList = bundle.getParcelableArrayList("tasks");
            if (taskList != null) {
                taskAdapter = new TaskAdapter(
                        taskList.stream()
                                .filter(task -> {
                                    Date deadline = task.getDeadline();
                                    if (deadline != null) {
                                        Calendar todayStart = Calendar.getInstance();
                                        todayStart.set(Calendar.HOUR_OF_DAY, 0);
                                        todayStart.set(Calendar.MINUTE, 0);
                                        todayStart.set(Calendar.SECOND, 0);
                                        todayStart.set(Calendar.MILLISECOND, 0);
                                        Calendar sevenDaysLater = Calendar.getInstance();
                                        sevenDaysLater.add(Calendar.DATE, 7);
                                        sevenDaysLater.set(Calendar.HOUR_OF_DAY, 23);
                                        sevenDaysLater.set(Calendar.MINUTE, 59);
                                        sevenDaysLater.set(Calendar.SECOND, 59);
                                        sevenDaysLater.set(Calendar.MILLISECOND, 999);
                                        return !deadline.before(todayStart.getTime()) && !deadline.after(sevenDaysLater.getTime());
                                    }
                                    return false;
                                })
                                .collect(Collectors.toList())
                );
                taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                taskRecyclerView.setAdapter(taskAdapter);
                updateProgress();
            } else {
                Toast.makeText(this, "No tasks available", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No tasks found", Toast.LENGTH_SHORT).show();
        }
    }



    public void showDynamicGreeting() {
        TextView welcomeText = findViewById(R.id.textView2);
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        if (timeOfDay >= 0 && timeOfDay < 12) {
            welcomeText.setText("Good Morning!\nHere are some tasks to do!");
        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            welcomeText.setText("Good Afternoon!\nHere are some tasks to do!");
        } else if (timeOfDay >= 16 && timeOfDay < 24) {
            welcomeText.setText("Good Evening!\nHere are some tasks to do!");
        } else {
            welcomeText.setText("Hallo!\nHere are some tasks to do!");
        }
    }
    public void showDynamicDate() {
        TextView dateText = findViewById(R.id.dateText);
        dateText.setText(Calendar.getInstance().getTime().toString().split(" ")[1] + " " +
                Calendar.getInstance().getTime().toString().split(" ")[2]);
    }

    public void navigateTo() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.navBar);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.navigation_create) {
                openAddTaskDialog();
                return true;
            } else if (id == R.id.navigation_mytasks) {
                Intent intent = new Intent(OverviewActivity.this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("tasks", taskList);
                intent.putExtras(bundle);
                startActivity(intent);
                return true;
            } else if (id == R.id.navigation_overview) {
                return true;
            }
            return false;
        });
    }

    public void sortTasksByDeadline() {

        if (taskList != null) {
            taskList.sort((task1, task2) -> {
                if (task1.getDeadline() == null && task2.getDeadline() == null) {
                    return 0; // Both deadlines are null, considered equal
                }
                if (task1.getDeadline() == null) {
                    return 1; // Place tasks with null deadlines at the end
                }
                if (task2.getDeadline() == null) {
                    return -1; // Place tasks with null deadlines at the end
                }
                return task1.getDeadline().compareTo(task2.getDeadline()); // Compare deadlines
            });
            taskAdapter.notifyDataSetChanged();
        }
    }

    private void openAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Task");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(24, 24, 24, 24);


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
                sortTasksByDeadline();

                ArrayList<Task> filteredTasks = (ArrayList<Task>) taskList.stream()
                        .filter(t -> {
                            Date taskDeadline = t.getDeadline();
                            if (taskDeadline != null) {
                                Calendar todayStart = Calendar.getInstance();
                                todayStart.set(Calendar.HOUR_OF_DAY, 0);
                                todayStart.set(Calendar.MINUTE, 0);
                                todayStart.set(Calendar.SECOND, 0);
                                todayStart.set(Calendar.MILLISECOND, 0);

                                Calendar sevenDaysLater = Calendar.getInstance();
                                sevenDaysLater.add(Calendar.DATE, 7);
                                sevenDaysLater.set(Calendar.HOUR_OF_DAY, 23);
                                sevenDaysLater.set(Calendar.MINUTE, 59);
                                sevenDaysLater.set(Calendar.SECOND, 59);
                                sevenDaysLater.set(Calendar.MILLISECOND, 999);

                                return !taskDeadline.before(todayStart.getTime()) &&
                                        !taskDeadline.after(sevenDaysLater.getTime());
                            }
                            return false;
                        })
                        .collect(Collectors.toList());

                    taskAdapter.updateTasks(filteredTasks);
            } else {
                Toast.makeText(this, "Task name cannot be empty!", Toast.LENGTH_SHORT).show();
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
                sortTasksByDeadline();
                taskAdapter.notifyDataSetChanged();
                ArrayList<Task> filteredTasks = (ArrayList<Task>) taskList.stream()
                        .filter(t -> {
                            Date taskDeadline = t.getDeadline();
                            if (taskDeadline != null) {
                                Calendar todayStart = Calendar.getInstance();
                                todayStart.set(Calendar.HOUR_OF_DAY, 0);
                                todayStart.set(Calendar.MINUTE, 0);
                                todayStart.set(Calendar.SECOND, 0);
                                todayStart.set(Calendar.MILLISECOND, 0);

                                Calendar sevenDaysLater = Calendar.getInstance();
                                sevenDaysLater.add(Calendar.DATE, 7);
                                sevenDaysLater.set(Calendar.HOUR_OF_DAY, 23);
                                sevenDaysLater.set(Calendar.MINUTE, 59);
                                sevenDaysLater.set(Calendar.SECOND, 59);
                                sevenDaysLater.set(Calendar.MILLISECOND, 999);

                                return !taskDeadline.before(todayStart.getTime()) &&
                                        !taskDeadline.after(sevenDaysLater.getTime());
                            }
                            return false;
                        })
                        .collect(Collectors.toList());
                taskAdapter.updateTasks(filteredTasks);
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

}