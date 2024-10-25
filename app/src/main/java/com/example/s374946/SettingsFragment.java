package com.example.s374946;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import java.util.Calendar;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        // Hent referanse til preferansen for å aktivere/deaktivere SMS-tjenesten
        SwitchPreferenceCompat smsPreference = findPreference("sms_service");
        if (smsPreference != null) {
            smsPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                boolean isEnabled = (Boolean) newValue;
                if (isEnabled) {
                    // Start tjenesten når SMS-tjenesten slås på
                    startSmsService();
                } else {
                    // Stoppe tjenesten når SMS-tjenesten slås av
                    stopSmsService();
                }
                return true;
            });
        }

        // Hent referanse til preferansen for SMS-tid
        Preference smsTimePreference = findPreference("sms_time");
        if (smsTimePreference != null) {
            // Hent lagret tid fra SharedPreferences
            SharedPreferences sharedPreferences = getPreferenceManager().getSharedPreferences();
            String savedTime = sharedPreferences.getString("sms_time", "08:00");
            smsTimePreference.setSummary(savedTime);

            // Sett click listener for å vise TimePicker
            smsTimePreference.setOnPreferenceClickListener(preference -> {
                showTimePickerDialog();
                return true;
            });
        }
    }


    private void showTimePickerDialog() {
        // Hent nåværende tid
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Opprett og vis TimePickerDialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (view, selectedHour, selectedMinute) -> {
            // Lagre den valgte tiden i SharedPreferences
            String time = String.format("%02d:%02d", selectedHour, selectedMinute);
            SharedPreferences sharedPreferences = getPreferenceManager().getSharedPreferences();
            sharedPreferences.edit().putString("sms_time", time).apply();

            // Oppdater oppsummeringsteksten
            Preference smsTimePreference = findPreference("sms_time");
            if (smsTimePreference != null) {
                smsTimePreference.setSummary(time);
            }
        }, hour, minute, true);

        timePickerDialog.setTitle("Velg tid");
        timePickerDialog.show();
    }


    private void startSmsService() {
        // Start MinPeriodisk-tjenesten
        Intent serviceIntent = new Intent(getContext(), MinPeriodisk.class);
        getContext().startService(serviceIntent);
    }

    private void stopSmsService() {
        // Stopp MinPeriodisk-tjenesten
        Intent serviceIntent = new Intent(getContext(), MinPeriodisk.class);
        getContext().stopService(serviceIntent);
    }

    private void updateSmsTime(String newTime) {
        // Hent SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        // Lagre den nye tiden i SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("sms_time", newTime); // Lagre den nye tiden
        editor.apply(); // Bruk apply() for å asynkront lagre endringer
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        // Sett bakgrunnsfargen
        view.setBackgroundColor(Color.parseColor("#FFFFFF"));
        return view;
    }
}
