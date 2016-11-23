package com.example.boris.to_do_list;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Name of the table
    public static final String TABLE_NAME = "TO_DO_ITEMS";

    // Table columns
    public static final String _ID = "_id";
    public static final String DESC_OF_TODO_ITEM = "description_of_todo_item";

    // Database Information
    static final String DB_NAME = "TODOLIST.DB";

    // Database version
    static final int DB_VERSION = 1;

    // Creating table query
    private static final String CREATE_TABLE = "create table " + TABLE_NAME + "(" + _ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + DESC_OF_TODO_ITEM + " TEXT NOT NULL);";

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
