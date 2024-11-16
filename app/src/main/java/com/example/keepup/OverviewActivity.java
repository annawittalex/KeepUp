package com.example.keepup;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keepup.Model.Task;
import com.example.keepup.Model.TaskAdapter;
import com.example.keepup.Model.TaskManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Calendar;
// OverviewActivity.java
public class OverviewActivity extends AppCompatActivity {

    private RecyclerView taskRecyclerView;
    private TaskAdapter taskAdapter;
    private ArrayList<Task> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_overview);

        showDynamicGreeting();
        showDynamicDate();
        navigateTo();
        displayTasksFromIntent();
    }

    public void displayTasksFromIntent() {
        taskRecyclerView = findViewById(R.id.taskRecyclerView);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            taskList = bundle.getParcelableArrayList("tasks");

            if (taskList != null) {
                taskAdapter = new TaskAdapter(taskList);
                taskRecyclerView.setLayoutManager(new LinearLayoutManager(this)); // Set layout manager
                taskRecyclerView.setAdapter(taskAdapter); // Set the adapter to RecyclerView
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
}