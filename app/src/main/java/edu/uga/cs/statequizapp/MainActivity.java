package edu.uga.cs.statequizapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

public class MainActivity extends AppCompatActivity {

    public static final String DEBUG_TAG = "MainActivity";
    private DBData dbData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbData = new DBData( this );

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
        //Intent intent = new Intent(getApplicationContext(), ResultsActivity.class);
        //startActivity(intent);
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