package com.example.boris.to_do_list_last;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class DBManager {

    // Declaring DatabaseHelper object for the variable dbHelper
    private DatabaseHelper dbHelper;

    private Context context;

    // Declaring a SQLiteDatabase
    private SQLiteDatabase database;

    // Constructor
    public DBManager(Context c) {
        context = c;
    }
    // public DBManager(){}

    // Before insert, update or delete any records the database has to be opened
    public DBManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    // Closing the database
    public void close() {
        dbHelper.close();
    }

    // Inserting new record into specific SQLite database
    public void insert(int imageID, String desc) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.DESC_OF_TODO_ITEM, desc);
        contentValue.put(DatabaseHelper.IMAGE_CHECKED_OR_UNCHECKED, imageID);
        database.insert(DatabaseHelper.TABLE_NAME, null, contentValue);
    }

    // Obtaining the to-do items
    public Cursor fetch() {
        String[] columns = new String[] { DatabaseHelper._ID, DatabaseHelper.IMAGE_CHECKED_OR_UNCHECKED,
                DatabaseHelper.DESC_OF_TODO_ITEM};
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    // Get a specific row (by rowId)
    public Cursor getRow(long _id) {
        String[] columns = new String[] { DatabaseHelper._ID, DatabaseHelper.IMAGE_CHECKED_OR_UNCHECKED,
                DatabaseHelper.DESC_OF_TODO_ITEM};
        String where = DatabaseHelper._ID + "=" + _id;
        Cursor c = 	database.query(true, DatabaseHelper.TABLE_NAME, columns,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public boolean updateRow(long _id, int imageID, String desc) {
        String where = DatabaseHelper._ID + "=" + _id;

        // Create row's data:
        ContentValues newValues = new ContentValues();
        newValues.put(DatabaseHelper.IMAGE_CHECKED_OR_UNCHECKED, imageID);
        newValues.put(DatabaseHelper.DESC_OF_TODO_ITEM, desc);

        // Insert it into the database.
        return database.update(DatabaseHelper.TABLE_NAME, newValues, where, null) != 0;
    }

    // Delete a record with the specific _id
    public void delete(long _id) {
        database.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper._ID + "=" + _id, null);
    }
}