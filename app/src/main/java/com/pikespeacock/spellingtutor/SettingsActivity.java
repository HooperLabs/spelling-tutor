package com.pikespeacock.spellingtutor;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class SettingsActivity extends ActionBarActivity {

    Button addWordButton;
    Button deleteAllButton;
    EditText addWordText;
    ListView spellingWordsListView;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        spellingWordsListView = (ListView) findViewById(R.id.spellingWordsListView);
        addWordText = (EditText) findViewById(R.id.addWordEditText);
        addWordButton = (Button) findViewById(R.id.addWordButton);
        deleteAllButton = (Button) findViewById(R.id.deleteAllButton);
        builder = new AlertDialog.Builder(SettingsActivity.this);
        preferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        editor = preferences.edit();

        // Load spelling words from preferences into variables
        final Set<String> spellingWordsSet = preferences.getStringSet("spellingWords",
                new HashSet<>(Arrays.asList(getString(R.string.defaultSpellingWord))));
        final ArrayList<String> spellingWordsArrayList = new ArrayList<>();
        for (String str : spellingWordsSet) {
            spellingWordsArrayList.add(str);
        }

        // Load spelling words into ListView
        final ArrayAdapter<String> arrayAdapter;
        arrayAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                spellingWordsArrayList
        );
        spellingWordsListView.setAdapter(arrayAdapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addWordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spellingWordsArrayList.add(addWordText.getText().toString());
                arrayAdapter.notifyDataSetChanged();
                editor.putStringSet("spellingWords", new HashSet<>(spellingWordsArrayList)).apply();
                addWordText.setText("");
            }
        });

        deleteAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.setTitle("Confirm Delete All Words")
                        .setMessage("Are you sure you want to delete all spelling words?")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // User canceled "Delete All" dialog
                            }
                        })
                        .setPositiveButton("Delete All", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                spellingWordsArrayList.clear();
                                arrayAdapter.notifyDataSetChanged();
                                editor.putStringSet("spellingWords", new HashSet<String>()).apply();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        spellingWordsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                // Show dialog
                builder.setTitle("Confirm Delete Word")
                         .setMessage("Delete this word?")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // User canceled "Delete" dialog
                            }
                        })
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                spellingWordsArrayList.remove(position);
                                arrayAdapter.notifyDataSetChanged();
                                editor.putStringSet("spellingWords", new HashSet<>(spellingWordsArrayList)).apply();
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });
    }

}
