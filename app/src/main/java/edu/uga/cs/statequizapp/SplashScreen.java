package edu.uga.cs.statequizapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SplashScreen extends AppCompatActivity {

    private Button quizModeBtn;
    private Button quizHistoryBtn;
    private DBData dbData = null;
    public static final String DEBUG_TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        dbData = new DBData( this );

        quizModeBtn = findViewById(R.id.quiz_mode);
        quizModeBtn.setOnClickListener(new OnClickButtonListener(0));
        quizHistoryBtn = findViewById(R.id.quiz_history);
        quizHistoryBtn.setOnClickListener(new OnClickButtonListener(1));
    }

    private class OnClickButtonListener implements View.OnClickListener {

        private int screenChoice;

        public OnClickButtonListener(int screenChoice) { this.screenChoice = screenChoice; }

        @Override
        public void onClick(View view) {
            Intent intent;

            if (screenChoice == 0) { // Screen Choice 0 goes to Quiz Mode
                intent = new Intent(view.getContext(), MainActivity.class);
            }
            else { // Screen Choice != 0 goes to Quiz History
                intent = new Intent(view.getContext(), ResultsActivity.class);
            }
            startActivity(intent);
        }
    }

    // This is an AsyncTask class (it extends AsyncTask) to perform DB writing of a job lead, asynchronously.
    public class StateDBWriter extends AsyncTask<State, State> {

        // This method will run as a background process to write into db.
        // It will be automatically invoked by Android, when we call the execute method
        // in the onClick listener of the Save button.
        @Override
        protected State doInBackground( State... states ) {
            dbData.storeState( states[0] );
            return states[0];
        }

        // This method will be automatically called by Android once the writing to the database
        // in a background process has finished.  Note that doInBackground returns a JobLead object.
        // That object will be passed as argument to onPostExecute.
        // onPostExecute is like the notify method in an asynchronous method call discussed in class.
        @Override
        protected void onPostExecute( State state ) {

        }
    }

    @Override
    protected void onResume() {
        Log.d( DEBUG_TAG, "MainActivity.onResume()" );
        // open the database in onResume
        if( dbData != null )
            dbData.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d( DEBUG_TAG, "MainActivity.onPause()" );
        // close the database in onPause
        if( dbData != null )
            dbData.close();
        super.onPause();
    }

    // The following activity callback methods are not needed and are for
    // educational purposes only.
    @Override
    protected void onStart() {
        Log.d( DEBUG_TAG, "MainActivity.onStart()" );
        super.onStart();

        try {
            //InputStream in_s = getAssets().open("state_capitals.csv");
            InputStream in_s = getResources().openRawResource(R.raw.state_capitals);

            CSVReader reader = new CSVReader( new InputStreamReader( in_s ) );
            String[] nextLine;
            String[] cities = new String[2];
            nextLine = reader.readNext();
            while( ( nextLine = reader.readNext() ) != null ) {
                cities[0] = nextLine[2];
                cities[1] = nextLine[3];

                State state = new State(nextLine[0], nextLine[1], cities);
                new StateDBWriter().execute(state);
            }


        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        Log.d( DEBUG_TAG, "MainActivity.onStop()" );
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d( DEBUG_TAG, "MainActivity.onDestroy()" );
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        Log.d( DEBUG_TAG, "MainActivity.onRestart()" );
        super.onRestart();
    }

}