package com.example.boris.to_do_list_last;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Name of the table
    public static final String TABLE_NAME = "TO_DO_ITEMS_LAST";

    // Table columns
    public static final String _ID = "_id";
    public static final String IMAGE_CHECKED_OR_UNCHECKED = "image_file";
    public static final String DESC_OF_TODO_ITEM = "description_of_todo_item";


    // Database Information
    static final String DB_NAME = "TODOLISTLAST.DB";

    // Database version
    static final int DB_VERSION = 2;

    // Creating table query
    private static final String CREATE_TABLE = "create table " + TABLE_NAME + "(" + _ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + IMAGE_CHECKED_OR_UNCHECKED + " integer not null, " + DESC_OF_TODO_ITEM + " text not null);";

    // The constructor
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}

