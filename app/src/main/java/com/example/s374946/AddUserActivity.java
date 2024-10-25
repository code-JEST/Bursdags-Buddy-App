package com.example.s374946;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddUserActivity extends AppCompatActivity {

    private EditText editTextName, editTextPhone, editTextBirthdate;
    private Button buttonAdd;
    private DatabaseHjelper dbHelper;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        // Initialiser visningene
        editTextName = findViewById(R.id.editTextName);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextBirthdate = findViewById(R.id.editTextBirthdate);
        buttonAdd = findViewById(R.id.buttonAdd);
        editTextBirthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });

        // Opprett en instans av databasehjelperen
        dbHelper = new DatabaseHjelper(this);



        // Hent dataene fra intent for å redigere
        Intent intent = getIntent();
        userId = intent.getIntExtra("id", -1); // Hent ID fra Intent (default -1 for nye brukere)
        String name = intent.getStringExtra("name");
        String phone = intent.getStringExtra("phone");
        String birthdate = intent.getStringExtra("birthdate");

        // Hvis dataene ikke er null, fyll ut feltene
        if (userId != -1) {
            editTextName.setText(name);
            editTextPhone.setText(phone);
            editTextBirthdate.setText(birthdate);
            buttonAdd.setText("Oppdater");
        }



        // Sett en OnClickListener for knappen
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hent tekst fra EditText-feltene
                String name = editTextName.getText().toString();
                String phone = editTextPhone.getText().toString();
                String birthdate = editTextBirthdate.getText().toString();

                // Sjekk om noen av feltene er tomme
                if (name.isEmpty() || phone.isEmpty() || birthdate.isEmpty()) {
                    Toast.makeText(AddUserActivity.this, "Alle feltene må fylles ut", Toast.LENGTH_SHORT).show();
                    return;
                }

                Item updatedItem = new Item(userId, name, phone, birthdate); // Opprett nytt Item-objekt med ID

                if (userId != -1) {
                    // Oppdater eksisterende bruker
                    dbHelper.updatePerson(updatedItem); // Bruk ID for oppdatering
                    Toast.makeText(AddUserActivity.this, "Oppføring oppdatert", Toast.LENGTH_SHORT).show();
                } else {
                    // Legg til ny bruker
                    dbHelper.addUser(updatedItem); // Legg til ny bruker
                    Toast.makeText(AddUserActivity.this, "Oppføring lagt til", Toast.LENGTH_SHORT).show();
                }

                // Tøm EditText-feltene for nye oppføringer
                editTextName.getText().clear();
                editTextPhone.getText().clear();
                editTextBirthdate.getText().clear();

                // Sett tilbake til hovedaktiviteten eller ønsket aktivitet etter lagring
                finish(); // Gå tilbake til forrige aktivitet
            }
        });
    }

    public void showDatePickerDialog(View v) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, dayOfMonth);

                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                        String strDate = format.format(calendar.getTime());

                        editTextBirthdate.setText(strDate);
                    }
                }, year, month, day);
        datePickerDialog.show();
    }
}

