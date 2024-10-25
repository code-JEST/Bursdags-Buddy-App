package com.example.s374946;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHjelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAVN = "BursdagDatabase";
    private static final int DATABASE_VERSION = 2;
    public static final String TABELL_NAVN = "Bruker";
    public static final String KEY_ID = "id";
    public static final String KEY_NAVN = "navn";
    public static final String KEY_TELEFON = "nummer";
    public static final String KEY_BURSDAG = "bursdag";

    public DatabaseHjelper(Context context) {
        super(context, DATABASE_NAVN, null, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABELL_NAVN + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NAVN + " TEXT,"
                + KEY_TELEFON + " TEXT,"
                + KEY_BURSDAG + " TEXT" + ")";
        db.execSQL(CREATE_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    { db.execSQL("DROP TABLE IF EXISTS " + TABELL_NAVN);
        onCreate(db);
    }

    //hente antall elemeneter i databasen
    public int getCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABELL_NAVN, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    public void addUser(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAVN, item.getName());
        values.put(KEY_TELEFON, item.getPhone());
        values.put(KEY_BURSDAG, item.getBirthdate());

        db.insert(TABELL_NAVN, null, values);
        db.close();
    }

    public int updatePerson(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAVN, item.getName());
        values.put(KEY_TELEFON, item.getPhone());
        values.put(KEY_BURSDAG, item.getBirthdate());

        return db.update(TABELL_NAVN, values, KEY_ID + " = ?",
                new String[]{String.valueOf(item.getId())});
    }


    public void deletePerson(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABELL_NAVN, KEY_ID + " = ?",
                new String[]{String.valueOf(item.getId())});
        db.close();
    }


    public List<Item> getAllUsers() {
        List<Item> userList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABELL_NAVN, null);

        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(KEY_ID);
                int nameIndex = cursor.getColumnIndex(KEY_NAVN);
                int phoneIndex = cursor.getColumnIndex(KEY_TELEFON);
                int birthdateIndex = cursor.getColumnIndex(KEY_BURSDAG);

                if (idIndex != -1 && nameIndex != -1 && phoneIndex != -1 && birthdateIndex != -1) {
                    int id = cursor.getInt(idIndex);
                    String name = cursor.getString(nameIndex);
                    String phone = cursor.getString(phoneIndex);
                    String birthdate = cursor.getString(birthdateIndex);
                    userList.add(new Item(id, name, phone, birthdate));
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return userList;
    }
}
