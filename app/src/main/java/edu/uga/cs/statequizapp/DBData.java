package edu.uga.cs.statequizapp;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class is facilitates storing and restoring job leads stored.
 */
public class DBData {

    public static final String DEBUG_TAG = "DBData";

    // this is a reference to our database; it is used later to run SQL commands
    private SQLiteDatabase db;
    private SQLiteOpenHelper DbHelper;
    private static final String[] allQuizColumns = {
            DBHelper.QUIZ_COLUMN_ID,
            DBHelper.QUIZ_COLUMN_STATE1,
            DBHelper.QUIZ_COLUMN_STATE2,
            DBHelper.QUIZ_COLUMN_STATE3,
            DBHelper.QUIZ_COLUMN_STATE4,
            DBHelper.QUIZ_COLUMN_STATE5,
            DBHelper.QUIZ_COLUMN_STATE6,
            DBHelper.QUIZ_COLUMN_CURRENT_QUESTION,
            DBHelper.QUIZ_COLUMN_SCORE,
            DBHelper.QUIZ_COLUMN_DATE_COMPLETED
    };
    private static final String[] allStateColumns = {
            DBHelper.STATE_COLUMN_ID,
            DBHelper.STATE_COLUMN_STATE,
            DBHelper.STATE_COLUMN_CAPITAL,
            DBHelper.STATE_COLUMN_CITY1,
            DBHelper.STATE_COLUMN_CITY2,
    };

    public DBData( Context context ) {
        this.DbHelper = DBHelper.getInstance( context );
        db = DbHelper.getWritableDatabase();
    }

    // Open the database
    public void open() {
        db = DbHelper.getWritableDatabase();
        Log.d( DEBUG_TAG, "DBData: db open" );
    }

    // Close the database
    public void close() {
        if( DbHelper != null ) {
            DbHelper.close();
            Log.d(DEBUG_TAG, "DBData: db closed");
        }
    }


    public List<State> retrieveAllStates() {
        ArrayList<State> states = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.query(DBHelper.TABLE_STATE, allStateColumns, null, null, null, null, null);
            if(cursor.getCount() > 0 ){
                while(cursor.moveToNext() ){
                    long id = cursor.getLong(cursor.getColumnIndex(DBHelper.STATE_COLUMN_ID));
                    String name = cursor.getString(cursor.getColumnIndex(DBHelper.STATE_COLUMN_STATE));
                    String capital = cursor.getString(cursor.getColumnIndex(DBHelper.STATE_COLUMN_CAPITAL));
                    String[] tmpCities = new String[2];
                    tmpCities[0] = cursor.getString(cursor.getColumnIndex(DBHelper.STATE_COLUMN_CITY1));
                    tmpCities[1] = cursor.getString(cursor.getColumnIndex(DBHelper.STATE_COLUMN_CITY2));
                    State state = new State( name, capital, tmpCities);

                    state.setId( id ); // set the id (the primary key) of this object
                    // add it to the list
                    states.add( state );
                    Log.d( DEBUG_TAG, "Retrieved State: " + state );
                }

            }
            Log.d( DEBUG_TAG, "Number of records from DB: " + cursor.getCount() );
        }catch( Exception e ){
            Log.d( DEBUG_TAG, "Exception caught: " + e );
        }
        finally{
            // we should close the cursor
            if (cursor != null) {
                cursor.close();
            }
        }
        // return a list of retrieved job leads
        return states;

    }

    public List<Quiz> retrieveAllQuizes() {
        ArrayList<Quiz> quizes = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.query(DBHelper.TABLE_QUIZ, allQuizColumns, null, null, null, null, null);
            if(cursor.getCount() > 0 ){
                while(cursor.moveToNext() ){
                    //PUll all DB values
                    long id = cursor.getLong(cursor.getColumnIndex(DBHelper.QUIZ_COLUMN_ID));
                    int[] stateIDs = new int[6];
                    stateIDs[0] = cursor.getInt(cursor.getColumnIndex(DBHelper.QUIZ_COLUMN_STATE1));
                    stateIDs[1] = cursor.getInt(cursor.getColumnIndex(DBHelper.QUIZ_COLUMN_STATE2));
                    stateIDs[2] = cursor.getInt(cursor.getColumnIndex(DBHelper.QUIZ_COLUMN_STATE3));
                    stateIDs[3] = cursor.getInt(cursor.getColumnIndex(DBHelper.QUIZ_COLUMN_STATE4));
                    stateIDs[4] = cursor.getInt(cursor.getColumnIndex(DBHelper.QUIZ_COLUMN_STATE5));
                    stateIDs[5] = cursor.getInt(cursor.getColumnIndex(DBHelper.QUIZ_COLUMN_STATE6));

                    int currentQuestion = cursor.getInt(cursor.getColumnIndex(DBHelper.QUIZ_COLUMN_CURRENT_QUESTION));
                    int score = cursor.getInt(cursor.getColumnIndex(DBHelper.QUIZ_COLUMN_SCORE));
                    String dateCompleted = cursor.getString(cursor.getColumnIndex(DBHelper.QUIZ_COLUMN_DATE_COMPLETED));
                    State[] states = new State[6];
                    //Do another lookup to get the states as well
                    for(int x = 0; x < stateIDs.length; x++){
                        states[x] = getStateByID(stateIDs[x]);
                    }
                    Quiz quiz = new Quiz(states, currentQuestion, score, new Date(Long.parseLong(dateCompleted)));

                    quiz.setId( id ); // set the id (the primary key) of this object
                    // add it to the list
                    quizes.add( quiz );
                    Log.d( DEBUG_TAG, "Retrieved State: " + quiz );
                }

            }
            Log.d( DEBUG_TAG, "Number of records from DB: " + cursor.getCount() );
        }catch( Exception e ){
            Log.d( DEBUG_TAG, "Exception caught: " + e );
        }
        finally{
            // we should close the cursor
            if (cursor != null) {
                cursor.close();
            }
        }
        // return a list of retrieved job leads
        return quizes;

    }
    // Retrieve all job leads and return them as a List.
    // This is how we restore persistent objects stored as rows in the job leads table in the database.
    // For each retrieved row, we create a new JobLead (Java POJO object) instance and add it to the list.
    // Store a new job lead in the database.
    public State storeState( State state ) {

        // Prepare the values for all of the necessary columns in the table
        // and set their values to the variables of the JobLead argument.
        // This is how we are providing persistence to a JobLead (Java object) instance
        // by storing it as a new row in the database table representing job leads.
        ContentValues values = new ContentValues();
        values.put( DBHelper.STATE_COLUMN_STATE, state.getName());
        values.put( DBHelper.STATE_COLUMN_CAPITAL, state.getCapital() );
        values.put( DBHelper.STATE_COLUMN_CITY1, state.getCities()[0] );
        values.put( DBHelper.STATE_COLUMN_CITY2,  state.getCities()[1] );

        // Insert the new row into the database table;
        // The id (primary key) is automatically generated by the database system
        // and returned as from the insert method call.

        //Log.d(DEBUG_TAG, values.getAsString(DBHelper.TABLE_STATE));
        long id = db.insert(DBHelper.TABLE_STATE, null, values );

        // store the id (the primary key) in the JobLead instance, as it is now persistent
        state.setId( id );

        Log.d( DEBUG_TAG, "Stored new state with id: " + String.valueOf( state.getId() ) );

        return state;
    }

    public Quiz createQuiz( Quiz quiz ) {

        // Prepare the values for all of the necessary columns in the table
        // and set their values to the variables of the JobLead argument.
        // This is how we are providing persistence to a JobLead (Java object) instance
        // by storing it as a new row in the database table representing job leads.
        ContentValues values = new ContentValues();
        values.put( DBHelper.QUIZ_COLUMN_STATE1, quiz.getStates()[0].getId());
        values.put( DBHelper.QUIZ_COLUMN_STATE2, quiz.getStates()[1].getId());
        values.put( DBHelper.QUIZ_COLUMN_STATE3, quiz.getStates()[2].getId());
        values.put( DBHelper.QUIZ_COLUMN_STATE4, quiz.getStates()[3].getId());
        values.put( DBHelper.QUIZ_COLUMN_STATE5, quiz.getStates()[4].getId());
        values.put( DBHelper.QUIZ_COLUMN_STATE6, quiz.getStates()[5].getId());


        values.put( DBHelper.QUIZ_COLUMN_CURRENT_QUESTION, quiz.getCurrentQuestion() );
        values.put( DBHelper.QUIZ_COLUMN_SCORE, quiz.getScore() );
        if(quiz.getDateCompleted() != null){
            values.put( DBHelper.QUIZ_COLUMN_DATE_COMPLETED,  quiz.getDateCompleted().getTime() );
        }


        // Insert the new row into the database table;
        // The id (primary key) is automatically generated by the database system
        // and returned as from the insert method call.
        long id = db.insert( DBHelper.TABLE_QUIZ, null, values );

        // store the id (the primary key) in the JobLead instance, as it is now persistent
        quiz.setId( id );

        Log.d( DEBUG_TAG, "Stored new quiz with id: " + String.valueOf( quiz.getId() ) );

        return quiz;
    }

    public Quiz updateQuiz( Quiz quiz ) {

        // Prepare the values for all of the necessary columns in the table
        // and set their values to the variables of the JobLead argument.
        // This is how we are providing persistence to a JobLead (Java object) instance
        // by storing it as a new row in the database table representing job leads.
        ContentValues values = new ContentValues();
        values.put( DBHelper.QUIZ_COLUMN_STATE1, quiz.getStates()[0].getId());
        values.put( DBHelper.QUIZ_COLUMN_STATE2, quiz.getStates()[1].getId());
        values.put( DBHelper.QUIZ_COLUMN_STATE3, quiz.getStates()[2].getId());
        values.put( DBHelper.QUIZ_COLUMN_STATE4, quiz.getStates()[3].getId());
        values.put( DBHelper.QUIZ_COLUMN_STATE5, quiz.getStates()[4].getId());
        values.put( DBHelper.QUIZ_COLUMN_STATE6, quiz.getStates()[5].getId());


        values.put( DBHelper.QUIZ_COLUMN_CURRENT_QUESTION, quiz.getCurrentQuestion() );
        values.put( DBHelper.QUIZ_COLUMN_SCORE, quiz.getScore() );
        values.put( DBHelper.QUIZ_COLUMN_DATE_COMPLETED,  quiz.getDateCompleted().getTime() );

        // Insert the new row into the database table;
        // The id (primary key) is automatically generated by the database system
        // and returned as from the insert method call.
        int update = db.update(DBHelper.TABLE_QUIZ, values, DBHelper.QUIZ_COLUMN_ID + "=?", new String[]{String.valueOf(quiz.getId())});


        Log.d( DEBUG_TAG, "updated quiz with id: " + String.valueOf( quiz.getId() ) );

        return quiz;
    }

    public State getStateByID(int id){
        ArrayList<State> states = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.query(DBHelper.TABLE_STATE, allStateColumns, DBHelper.STATE_COLUMN_ID + " = " + id, null, null, null, null);
            if(cursor.getCount() > 0 ){
                while(cursor.moveToNext() ){
                    String name = cursor.getString(cursor.getColumnIndex(DBHelper.STATE_COLUMN_STATE));
                    String capital = cursor.getString(cursor.getColumnIndex(DBHelper.STATE_COLUMN_CAPITAL));
                    String[] tmpCities = new String[2];
                    tmpCities[0] = cursor.getString(cursor.getColumnIndex(DBHelper.STATE_COLUMN_CITY1));
                    tmpCities[1] = cursor.getString(cursor.getColumnIndex(DBHelper.STATE_COLUMN_CITY2));
                    State state = new State( name, capital, tmpCities);

                    state.setId( id ); // set the id (the primary key) of this object
                    // add it to the list
                    states.add( state );
                    Log.d( DEBUG_TAG, "Retrieved State: " + state );
                }

            }
            Log.d( DEBUG_TAG, "Number of records from DB: " + cursor.getCount() );
        }catch( Exception e ){
            Log.d( DEBUG_TAG, "Exception caught: " + e );
        }
        finally{
            // we should close the cursor
            if (cursor != null) {
                cursor.close();
            }
        }
        // return a list of retrieved job leads
        return states.get(0);

    }

    public Quiz getQuizByID(int id){
        ArrayList<Quiz> quizes = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.query(DBHelper.TABLE_STATE, allStateColumns, DBHelper.QUIZ_COLUMN_ID + " = " + id, null, null, null, null);
            if(cursor.getCount() > 0 ){
                while(cursor.moveToNext() ){
                    //PUll all DB values
                    int[] stateIDs = new int[6];
                    stateIDs[0] = cursor.getInt(cursor.getColumnIndex(DBHelper.QUIZ_COLUMN_STATE1));
                    stateIDs[1] = cursor.getInt(cursor.getColumnIndex(DBHelper.QUIZ_COLUMN_STATE2));
                    stateIDs[2] = cursor.getInt(cursor.getColumnIndex(DBHelper.QUIZ_COLUMN_STATE3));
                    stateIDs[3] = cursor.getInt(cursor.getColumnIndex(DBHelper.QUIZ_COLUMN_STATE4));
                    stateIDs[4] = cursor.getInt(cursor.getColumnIndex(DBHelper.QUIZ_COLUMN_STATE5));
                    stateIDs[5] = cursor.getInt(cursor.getColumnIndex(DBHelper.QUIZ_COLUMN_STATE6));

                    int currentQuestion = cursor.getInt(cursor.getColumnIndex(DBHelper.QUIZ_COLUMN_CURRENT_QUESTION));
                    int score = cursor.getInt(cursor.getColumnIndex(DBHelper.QUIZ_COLUMN_SCORE));
                    String dateCompleted = cursor.getString(cursor.getColumnIndex(DBHelper.QUIZ_COLUMN_DATE_COMPLETED));
                    State[] states = new State[6];
                    //Do another lookup to get the states as well
                    for(int x = 0; x < stateIDs.length; x++){
                        states[x].setId(stateIDs[x]);
                    }
                    Quiz quiz = new Quiz(states, currentQuestion, score, new Date(Long.parseLong(dateCompleted)));

                    quiz.setId( id ); // set the id (the primary key) of this object
                    // add it to the list
                    quizes.add( quiz );
                    Log.d( DEBUG_TAG, "Retrieved State: " + quiz );
                }

            }
            Log.d( DEBUG_TAG, "Number of records from DB: " + cursor.getCount() );
        }catch( Exception e ){
            Log.d( DEBUG_TAG, "Exception caught: " + e );
        }
        finally{
            // we should close the cursor
            if (cursor != null) {
                cursor.close();
            }
        }
        // return a list of retrieved job leads
        return quizes.get(0);

    }

    public void clearDatabase(String TABLE_NAME) {
        db.delete(TABLE_NAME, "name=?", new String[]{TABLE_NAME});
        db.close();
    }




}

