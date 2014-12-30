package com.pikespeacock.spellingtutor;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Random;
import java.util.Set;


public class MainActivity extends ActionBarActivity {

    public final static String EXTRA_MESSAGE = "com.pikespeacock.spellingtutor.MESSAGE";
    private static final String MY_TEST_DEVICE_ID = "B3EEABB8EE11C2BE770B684D95219ECB";


    public ArrayList<String> spellingWordsArrayList = new ArrayList<String>();
    public Set<String> spellingWordsSet;
    public TextToSpeech textToSpeech;
    public int currentWordIndex = 0;
    public SharedPreferences preferences;
    public EditText input_text;
    public EditText correct_words_text;
    public Button listen_button;
    public Button check_button;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the ad banner
        adView = (AdView) this.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//                .addTestDevice(MY_TEST_DEVICE_ID)
                .build();
        adView.loadAd(adRequest);

        input_text = (EditText) findViewById(R.id.inputText);
        correct_words_text = (EditText) findViewById(R.id.correctWordsText);
        listen_button = (Button) findViewById(R.id.listenButton);
        check_button = (Button) findViewById(R.id.checkButton);

        textToSpeech = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.US);
                }
            }
        });

        listen_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                textToSpeech.speak(MainActivity.this.spellingWordsArrayList.get(currentWordIndex), TextToSpeech.QUEUE_FLUSH, null);
            }
        });
        check_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                checkWord();
            }
        });

        initialize();

    }

    public void newWord() {
        EditText input_text = (EditText) findViewById(R.id.inputText);

        currentWordIndex = new Random().nextInt(spellingWordsArrayList.size());

        textToSpeech.speak(spellingWordsArrayList.get(currentWordIndex), TextToSpeech.QUEUE_ADD, null);
        input_text.setText("");
    }

    public void checkWord() {

        EditText correct_words_text = (EditText) findViewById(R.id.correctWordsText);
        Button listen_button = (Button) findViewById(R.id.listenButton);
        Button check_button = (Button) findViewById(R.id.checkButton);

        if (input_text.getText().toString().equals(spellingWordsArrayList.get(currentWordIndex))) {

            // Correct
            correct_words_text.append((correct_words_text.length() > 0 ? "\n" : "") + spellingWordsArrayList.get(currentWordIndex));
            spellingWordsArrayList.remove(currentWordIndex);
            textToSpeech.speak("Correct!", TextToSpeech.QUEUE_FLUSH, null);
            if (spellingWordsArrayList.size() > 0) {
                newWord();
            } else {
                textToSpeech.speak("Congratulations!  You have spelled every word correctly!", TextToSpeech.QUEUE_ADD, null);
                check_button.setEnabled(false);
                listen_button.setEnabled(false);
                input_text.setEnabled(false);
                input_text.setText("Congratulations!");
            }
        } else {

            // Incorrect
            textToSpeech.speak("Incorrect.  Please spell " + spellingWordsArrayList.get(currentWordIndex), TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    public void initialize () {

        String defaultWord = getString(R.string.defaultSpellingWord);

        preferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        spellingWordsSet = preferences.getStringSet("spellingWords", new HashSet<String>(Arrays.asList(defaultWord)));

        if (spellingWordsSet.size() <= 0) {
            spellingWordsSet.add(defaultWord);
            Toast.makeText(this, "No words in list - added default spelling word: " + defaultWord, Toast.LENGTH_LONG).show();
        }

        if(spellingWordsArrayList != null) {
            spellingWordsArrayList.clear();
        }

        for (String str : spellingWordsSet) {
            spellingWordsArrayList.add(str);
        }

        input_text.setEnabled(true);
        listen_button.setEnabled(true);
        check_button.setEnabled(true);

        correct_words_text.setText("");

        newWord();

    }

    @Override
    protected void onPause() {
        if (textToSpeech != null) {
            textToSpeech.stop();
        }
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
        initialize();
    }


    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            EditText input_text = (EditText) findViewById(R.id.inputText);
            String message = input_text.getText().toString();
            intent.putExtra(EXTRA_MESSAGE, message);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}