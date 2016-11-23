package com.example.boris.to_do_list;


import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private String stringToDoItem;
    private SimpleCursorAdapter adapter;
    private ListView listViewToDoItems;
    private DBManager dbManager;
    private Cursor cursor;
    private SharedPreferences prefs;
    public static final String PREFERENCES = "checked_preferences";

    // For obtaining the columns from the database
    final String[] from = new String[] { DatabaseHelper.DESC_OF_TODO_ITEM };

    // Setting the columns to the textview inside listview
    final int[] to = new int[] { R.id.textviewListItem };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Open the database
        dbManager = new DBManager(this);
        dbManager.open();

        prefs = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        if(!(prefs.contains("intially"))) {
            dbManager.insert("Add to-do items in the list by filling in the input field below");
            dbManager.insert("Then click the + button to add your to-do");
            dbManager.insert("Short click on item to check/uncheck, long click an item to delete!");
            editor.putString("intially", "intially");
            editor.commit();
        }

        // Set the listview
        setList();

    }

    private void setList() {

        // Obtain all information of database and save it into a cursor object
        cursor = dbManager.fetch();

        listViewToDoItems = (ListView) findViewById(R.id.listView);

        // adapter will fill the listview with the to-do items from the sql table using the cursor object
        adapter = new SimpleCursorAdapter(this, R.layout.list_item, cursor, from, to, 0);

        // Setting the to-do items to the listview
        listViewToDoItems.setAdapter(adapter);

        // when a list item is LongClicked -> remove from listview and sql database
        listViewToDoItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                prefs = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();

                // select to-do item string
                TextView textViewItem = (TextView)view.findViewById(R.id.textviewListItem);
                String textStringItem = textViewItem.getText().toString();

                // remove the key from sharedpreference
                editor.remove(textStringItem);
                editor.apply();

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

                // Get sharedpreferences to save a key (determine if listview item is checked
                // or unchecked)
                prefs = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();

                // select to-do item string
                TextView textViewItem = (TextView)view.findViewById(R.id.textviewListItem);
                String textStringItem = textViewItem.getText().toString();

                // Find the imageview inside the list item
                ImageView imageCheckButton = (ImageView)view.findViewById(R.id.imageCheck);

                // If the image is an unchecked image -> change to checked image
                if (imageCheckButton.getDrawable().getConstantState() == getResources().getDrawable(
                        R.drawable.ic_unchecked).getConstantState())
                {
                    imageCheckButton.setImageResource(R.drawable.ic_check_box);

                    // add a key to sharedpreference
                    editor.putString(textStringItem, "CHECKED");
                    editor.commit();
                }
                else
                {
                    imageCheckButton.setImageResource(R.drawable.ic_unchecked);

                    // add a key to sharedpreference
                    editor.putString(textStringItem, "UNCHECKED");
                    editor.commit();
                }
            }
        });
    }

    public void restore(View view){

        // Find the ListView
        listViewToDoItems = (ListView) findViewById(R.id.listView);

        // Get sharedpreferences to decide which checkboxes are already checked (or unchecked)
        prefs = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);

        // Get amount items in ListView
        int count = listViewToDoItems.getChildCount();

        for(int i= 0; i <count; i++){

            // Obtain the text(description) from an item in the listview
            View viewItem = (View)listViewToDoItems.getChildAt(i);
            TextView textViewItem = (TextView)viewItem.findViewById(R.id.textviewListItem);
            String textStringItem = textViewItem.getText().toString();

            // Loop through all key:value pairs in sharedpreferences
            Map<String, ?> keys = prefs.getAll();

            for (Map.Entry<String, ?> entry : keys.entrySet()) {
                if (entry.getKey().equals(textStringItem)) {
                    if (entry.getValue().equals("CHECKED")){

                        ImageView imageCheckButton = (ImageView)viewItem.findViewById(R.id.imageCheck);
                        imageCheckButton.setImageResource(R.drawable.ic_check_box);
                    }
                    else if(entry.getValue().equals("UNCHECKED")) {

                        ImageView imageCheckButton = (ImageView)viewItem.findViewById(R.id.imageCheck);
                        imageCheckButton.setImageResource(R.drawable.ic_unchecked);
                    }
                }
            }
        }
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

            // Insert string in database
            dbManager.insert(stringToDoItem);

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
}