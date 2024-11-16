package com.example.keepup;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;

public class OverviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_overview);
        showDynamicGreeting();
        showDynamicDate();
        navigateTo();
    }

    public void showDynamicGreeting() {
        TextView welcomeText = findViewById(R.id.textView2);
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        if(timeOfDay >= 0 && timeOfDay < 12){
            welcomeText.setText("Good Morning!\nHere are some tasks to do!");
        }else if(timeOfDay >= 12 && timeOfDay < 16){
            welcomeText.setText("Good Afternoon!\nHere are some tasks to do!");
        }else if(timeOfDay >= 16 && timeOfDay < 24){
            welcomeText.setText("Good Evening!\nHere are some tasks to do!");
        }else{
            welcomeText.setText("Hallo!\nHere are some tasks to do!");
        }
    }
    public void showDynamicDate(){
        TextView dateText = findViewById(R.id.dateText);
        dateText.setText(Calendar.getInstance().getTime().toString().split(" ")[1] + " "+
                         Calendar.getInstance().getTime().toString().split(" ")[2] );
    }

    public void navigateTo(){
        BottomNavigationView bottomNavigationView = findViewById(R.id.navBar);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_mytasks) {
                return true;
            } else if (id == R.id.navigation_create) {
                // switch screen from Main Activity to OverviewActivity
                Intent intent = new Intent(OverviewActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.navigation_overview) {

            }
            return false;
        });

    }
}