package com.example.s374946;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.Manifest;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ItemAdapter.OnItemClickListener {
    // Variabler for SMS-funksjonalitet
    private static final long CHECK_INTERVAL = 60000; // Sjekk hvert minutt
    private Handler handler;
    private boolean smsServiceEnabled;
    private String smsTime;
    private Runnable smsChecker;

    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private DatabaseHjelper databaseHelper;
    private Button buttonSettings;
    private static final int PERMISSION_REQUEST_SEND_SMS = 1;
    private static final int PERMISSION_REQUEST_POST_NOTIFICATIONS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Sjekk SMS-tillatelse
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            // Be om tillatelse
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_SEND_SMS);
        }
        // Sjekk om tillatelse for å vise notifikasjoner er gitt
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_POST_NOTIFICATIONS);
            }
        }

        // henter antall elementer i databasen
        databaseHelper = new DatabaseHjelper(this);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        List<Item> itemList = databaseHelper.getAllUsers();
        itemAdapter = new ItemAdapter(itemList, this, databaseHelper);
        recyclerView.setAdapter(itemAdapter);

        // Add person knappen
        Button buttonAddPerson = findViewById(R.id.buttonAddPerson);
        buttonAddPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddUserActivity.class);
                startActivity(intent);
            }
        });

        // Settings knappen
        buttonSettings = findViewById(R.id.buttonSettings);
        buttonSettings.setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new SettingsFragment())
                    .addToBackStack(null)
                    .commit();
        });
        // Sjekk SMS-tjenesten fra SharedPreferences
        SharedPreferences prefs = getSharedPreferences("com.example.s374946_preferences", MODE_PRIVATE);
        smsServiceEnabled = prefs.getBoolean("sms_service", false);
        smsTime = prefs.getString("sms_time", "08:00");

        // Sett opp en handler for å sjekke tid
        handler = new Handler(Looper.getMainLooper());
        smsChecker = new Runnable() {
            @Override
            public void run() {
                checkAndSendSMS();
                handler.postDelayed(this, CHECK_INTERVAL); // Sjekk igjen etter CHECK_INTERVAL
            }
        };
        handler.post(smsChecker); // Start sjekking
    }

    @Override
    protected void onResume() {
        super.onResume();

        List<Item> updatedItemList = databaseHelper.getAllUsers();
        itemAdapter = new ItemAdapter(updatedItemList, this, databaseHelper);
        recyclerView.setAdapter(itemAdapter);
        itemAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(Item item) {
        // Handle the item click event here
        Toast.makeText(this, "Clicked: " + item.getName(), Toast.LENGTH_SHORT).show();
    }


    private void checkAndSendSMS() {
        SharedPreferences prefs = getSharedPreferences("com.example.s374946_preferences", MODE_PRIVATE);
        boolean smsServiceEnabled = prefs.getBoolean("sms_service", false);
        String smsTime = prefs.getString("sms_time", "08:00");

        // Sjekk om SMS-tjenesten er aktivert
        if (smsServiceEnabled) {
            // Hent dagens dato og tid
            Calendar calendar = Calendar.getInstance();
            int todayDay = calendar.get(Calendar.DAY_OF_MONTH);
            int todayMonth = calendar.get(Calendar.MONTH) + 1; // +1 fordi månedene starter på 0

            // Hent tid fra smsTime
            String[] timeParts = smsTime.split(":");
            int smsHour = Integer.parseInt(timeParts[0]);
            int smsMinute = Integer.parseInt(timeParts[1]);

            // Hent alle personer fra databasen
            List<Item> users = databaseHelper.getAllUsers();
            for (Item user : users) {
                String[] birthdateParts = user.getBirthdate().split("/"); // Forventet format dd/MM/yyyy
                int birthDay = Integer.parseInt(birthdateParts[0]);
                int birthMonth = Integer.parseInt(birthdateParts[1]);

                // Sjekk om i dag er bursdagen til brukeren
                if (todayDay == birthDay && todayMonth == birthMonth) {
                    // Sjekk om nåværende tid er lik eller etter sms-tid
                    if (calendar.get(Calendar.HOUR_OF_DAY) == smsHour && calendar.get(Calendar.MINUTE) == smsMinute) {
                        MinSendService.sendSMS(this, user.getPhone(), user.getName());
                    }
                }
            }
        }
    }
}

