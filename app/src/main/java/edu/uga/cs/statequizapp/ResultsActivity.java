package edu.uga.cs.statequizapp;


import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * This is an activity controller class for listing the current job leads.
 * The current job leads are listed as a RecyclerView.
 */
public class ResultsActivity
        extends AppCompatActivity
{
    //implements AddJobLeadDialogFragment.AddJobLeadDialogListener

    public static final String DEBUG_TAG = "ReviewJobLeadsActivity";

    private RecyclerView recyclerView;
    private QuizRecyclerAdapter quizAdapter;

    private DBData dbData = null;
    private List<Quiz> quizList;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {

        Log.d( DEBUG_TAG, "ReviewJobLeadsActivity.onCreate()" );

        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_quiz_results );

        recyclerView = findViewById( R.id.recyclerView );

        // use a linear layout manager for the recycler view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Create a JobLeadsData instance, since we will need to save a new JobLead to the dn.
        // Note that even though more activites may create their own instances of the JobLeadsData
        // class, we will be using a single instance of the JobLeadsDBHelper object, since
        // that class is a singleton class.
        dbData = new DBData( this );

        // Execute the retrieval of the job leads in an asynchronous way,
        // without blocking the main UI thread.
        new QuizDBReader().execute();

    }

    // This is an AsyncTask class (it extends AsyncTask) to perform DB reading of job leads, asynchronously.
    private class QuizDBReader extends AsyncTask<Void, List<Quiz>> {
        // This method will run as a background process to read from db.
        // It returns a list of retrieved JobLead objects.
        // It will be automatically invoked by Android, when we call the execute method
        // in the onCreate callback (the job leads review activity is started).
        @Override
        protected List<Quiz> doInBackground( Void... params ) {
            dbData.open();
            quizList = dbData.retrieveAllQuizes();

                //TESTING
//            quizList = new ArrayList<Quiz>();
//            State s1 = new State();
//            State s3 = new State();
//            State s2 = new State();
//            State[] states = new State[]{s1, s2, s3};
//            String date_string = "26-09-1989";
//            //Instantiating the SimpleDateFormat class
//            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
//            //Parsing the given String to Date object
//            Date date = null;
//            try {
//                date = formatter.parse(date_string);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            Quiz q1 = new Quiz(states, -1, 6, date);
//            Quiz q2 = new Quiz(states, 4, 6, date);
//            Quiz q3 = new Quiz(states, -1, 6, date);
//            quizList.add(q1);
//            quizList.add(q2);
//            quizList.add(q3);

            for (int i = 0; i < quizList.size(); i++) {
                if (quizList.get(i).getCurrentQuestion() != -1) {
                    quizList.remove(i);
                }
            }

            Log.d( DEBUG_TAG, "QuizDBReaderTask: Quizzes retrieved: " + quizList.size() );

            return quizList;
        }

        // This method will be automatically called by Android once the db reading
        // background process is finished.  It will then create and set an adapter to provide
        // values for the RecyclerView.
        // onPostExecute is like the notify method in an asynchronous method call discussed in class.
        @Override
        protected void onPostExecute( List<Quiz> quizList ) {
            quizAdapter = new QuizRecyclerAdapter( quizList );
            recyclerView.setAdapter( quizAdapter );
        }
    }


    // this is our own callback for a DialogFragment which adds a new job lead.
    public void onFinishNewJobLeadDialog(Quiz jobLead) {
        // add the new job lead
        //new JobLeadDBWriter().execute( jobLead );
    }

    void showDialogFragment( DialogFragment newFragment ) {
        //newFragment.show( getSupportFragmentManager(), null);
    }

    @Override
    protected void onResume() {
        Log.d( DEBUG_TAG, "ReviewJobLeadsActivity.onResume()" );
        // open the database in onResume
        if( dbData != null )
            dbData.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d( DEBUG_TAG, "ReviewJobLeadsActivity.onPause()" );
        // close the database in onPause
        if( dbData != null )
            dbData.close();
        super.onPause();
    }

    // These activity callback methods are not needed and are for edational purposes only
    @Override
    protected void onStart() {
        Log.d( DEBUG_TAG, "ReviewJobLeadsActivity.onStart()" );
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.d( DEBUG_TAG, "ReviewJobLeadsActivity.onStop()" );
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d( DEBUG_TAG, "ReviewJobLeadsActivity.onDestroy()" );
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        Log.d( DEBUG_TAG, "ReviewJobLeadsActivity.onRestart()" );
        super.onRestart();
    }
}

