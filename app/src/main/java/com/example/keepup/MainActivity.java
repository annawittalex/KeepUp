package com.example.keepup;

import com.example.keepup.R;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

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


//--------------------Navigation-----------------------------------------------------
        BottomNavigationView bottomNavigationView = findViewById(R.id.navBar);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_mytasks) {
                Log.d("MainActivity", "My Tasks clicked");
                return true;
            } else if (id == R.id.navigation_create) {
                Log.d("MainActivity", "Create clicked");
                openAddTaskDialog();
                return true;
            } else if (id == R.id.navigation_overview) {
                Log.d("MainActivity", "Overview clicked");
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

            final EditText input = new EditText(this);
            builder.setView(input);

            builder.setPositiveButton("Add", (dialog, which) -> {
                String taskName = input.getText().toString();
                if (!taskName.isEmpty()) {
                    Log.d("MainActivity", "Adding task: " + taskName);
                    taskManager.addTask(taskName);
                    taskAdapter.notifyItemInserted(taskManager.getAllTasks().size() - 1);
                    recyclerView.scrollToPosition(taskAdapter.getItemCount() - 1);

                } else{
                    Log.d("MainActivity", "Task name is empty, no task added.");
                }
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

            builder.show();
        }


    }
