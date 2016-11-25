package com.example.boris.to_do_list_last;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // Declaring list with the sources for the checked/unchecked images
    int[] imageIDs = {
            R.drawable.ic_unchecked,
            R.drawable.ic_checked,
    };

    private String stringToDoItem;
    private SimpleCursorAdapter adapter;
    private ListView listViewToDoItems;
    private DBManager dbManager;
    private Cursor cursor;
    private SharedPreferences prefs;
    public static final String PREFERENCES = "checked_preferences";

    // Containing the image for checked/unchecked image and description for list item
    final String[] from = new String[] { DatabaseHelper.IMAGE_CHECKED_OR_UNCHECKED,
            DatabaseHelper.DESC_OF_TODO_ITEM};

    // Pass the elements to list item xml
    final int[] to = new int[] { R.id.imageCheck, R.id.textviewListItem };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Open the database
        dbManager = new DBManager(this);
        dbManager.open();

        // Manual when app first starts
        prefs = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        if(!(prefs.contains("intially"))) {
            dbManager.insert(imageIDs[0], "Add to-do items in the list by filling in the input field below");
            dbManager.insert(imageIDs[0], "Then click the + button to add your to-do");
            dbManager.insert(imageIDs[0], "Short click on item to check/uncheck, long click an item to delete!");
            editor.putString("intially", "intially");
            editor.commit();
        }

        // Set the listview
        setList();

    }

    private void setList() {

        // Obtain all the data from the database
        cursor = dbManager.fetch();

        // Obtain the listview
        listViewToDoItems = (ListView) findViewById(R.id.listViewMain);
        adapter = new SimpleCursorAdapter(this, R.layout.list_item, cursor, from, to, 0);

        // Set the adapter for the list view
        listViewToDoItems.setAdapter(adapter);

        // when a list item is LongClicked -> remove from listview and sql database
        listViewToDoItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                // Delete selected item
                dbManager.delete(l);

                // Update cursor object
                cursor.requery();

                // Change the content in the listview
                adapter.notifyDataSetChanged();

                Toast toast = Toast.makeText(MainActivity.this, "Item deleted!", Toast.LENGTH_SHORT);
                toast.show();
                return true;
            }
        });


        // when a listview item is (short) onclicked -> check the item as task done
        listViewToDoItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                // Get intire row of the list item clicked
                Cursor cursorRow = dbManager.getRow(l);
                if (cursorRow.moveToFirst()) {

                    // Obtaining the right data out of the row
                    int idOfImage = cursorRow.getInt(1);
                    String description = cursorRow.getString(2);

                    // Changing the image to checked or unchecked
                    if (idOfImage == imageIDs[0]) {
                        idOfImage = imageIDs[1];
                    }
                    else {
                        idOfImage = imageIDs[0];
                    }

                    // Update the row with the new image
                    dbManager.updateRow(l, idOfImage, description);
                }
                cursorRow.close();

                // Update cursor object
                cursor.requery();

                // Change the content in the listview
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void addItemtoView(View view) {
        EditText editToDoItem = (EditText)findViewById(R.id.editTextToDoItem);
        stringToDoItem = editToDoItem.getText().toString();
        editToDoItem.setText("");

        // make sure user enters an item
        if (!(stringToDoItem.length() == 0)) {
            prefs = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            editor.putString(stringToDoItem, "UNCHECKED");
            editor.commit();

            // Add it to the DB and re-draw the ListView
            dbManager.insert(imageIDs[0], stringToDoItem);

            // Update cursor object
            cursor.requery();

            // Change the content in the listview
            adapter.notifyDataSetChanged();
        }
        else {
            Toast toast = Toast.makeText(this, "You did not enter an item", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbManager.close();
    }
}
