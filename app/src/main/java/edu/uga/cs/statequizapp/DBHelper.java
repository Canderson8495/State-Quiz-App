package edu.uga.cs.statequizapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * This is a SQLiteOpenHelper class, which Android uses to create, upgrade, delete an SQLite database
 * in an app.
 *
 * This class is a singleton, following the Singleton Design Pattern.
 * Only one instance of this class will exist.  To make sure, the
 * only constructor is private.
 * Access to the only instance is via the getInstance method.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DEBUG_TAG = "DBHelper";

    private static final String DB_NAME = "statequiz.db";
    private static final int DB_VERSION = 1;

    // Define all names (strings) for table and column names.
    // This will be useful if we want to change these names later.
    public static final String TABLE_QUIZ = "QUIZ";
    public static final String QUIZ_COLUMN_ID = "_id";
    public static final String QUIZ_COLUMN_STATE1 = "state1";
    public static final String QUIZ_COLUMN_STATE2 = "state2";
    public static final String QUIZ_COLUMN_STATE3 = "state3";
    public static final String QUIZ_COLUMN_STATE4 = "state4";
    public static final String QUIZ_COLUMN_STATE5 = "state5";
    public static final String QUIZ_COLUMN_STATE6 = "state6";
    public static final String QUIZ_COLUMN_CURRENT_QUESTION = "currentQuestion";
    public static final String QUIZ_COLUMN_SCORE = "score";
    public static final String QUIZ_COLUMN_DATE_COMPLETED = "dateCompleted";

    // Define all names (strings) for table and column names.
    // This will be useful if we want to change these names later.
    public static final String TABLE_STATE = "STATE";
    public static final String STATE_COLUMN_ID = "_id";
    public static final String STATE_COLUMN_STATE = "state";
    public static final String STATE_COLUMN_CAPITAL = "capital";
    public static final String STATE_COLUMN_CITY1 = "city1";
    public static final String STATE_COLUMN_CITY2 = "city2";
    // This is a reference to the only instance for the helper.
    private static DBHelper helperInstance;

    // A Create table SQL statement to create a table for job leads.
    // Note that _id is an auto increment primary key, i.e. the database will
    // automatically generate unique id values as keys.
    private static final String CREATE_STATE =
            "create table " + TABLE_STATE + " ("
                    + STATE_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + STATE_COLUMN_STATE + " TEXT, "
                    + STATE_COLUMN_CAPITAL + " TEXT, "
                    + STATE_COLUMN_CITY1 + " TEXT, "
                    + STATE_COLUMN_CITY2 + " TEXT"
                    + ")";

    private static final String CREATE_QUIZ =
            "create table " + TABLE_QUIZ + " ("
                    + QUIZ_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + QUIZ_COLUMN_STATE1 + " TEXT, "
                    + QUIZ_COLUMN_STATE2 + " TEXT, "
                    + QUIZ_COLUMN_STATE3 + " TEXT, "
                    + QUIZ_COLUMN_STATE4 + " TEXT, "
                    + QUIZ_COLUMN_STATE5 + " TEXT, "
                    + QUIZ_COLUMN_STATE6 + " TEXT, "
                    + QUIZ_COLUMN_CURRENT_QUESTION + " INTEGER, "
                    + QUIZ_COLUMN_SCORE + " INTEGER, "
                    + QUIZ_COLUMN_DATE_COMPLETED + " STRING"
                    + ")";

    // Note that the constructor is private!
    // So, it can be called only from
    // this class, in the getInstance method.
    private DBHelper( Context context ) {
        super( context, DB_NAME, null, DB_VERSION );
    }

    // Access method to the single instance of the class.
    // It is synchronized, so that only one thread can executes this method.
    public static synchronized DBHelper getInstance( Context context ) {
        // check if the instance already exists and if not, create the instance
        if( helperInstance == null ) {
            helperInstance = new DBHelper( context.getApplicationContext() );
        }
        return helperInstance;
    }

    // We must override onCreate method, which will be used to create the database if
    // it does not exist yet.
    @Override
    public void onCreate( SQLiteDatabase db ) {
        db.execSQL( CREATE_QUIZ );
        Log.d( DEBUG_TAG, "Table " + TABLE_QUIZ + " created" );
        db.execSQL( CREATE_STATE );
        Log.d( DEBUG_TAG, "Table " + TABLE_STATE + " created" );

    }

    // We should override onUpgrade method, which will be used to upgrade the database if
    // its version (DB_VERSION) has changed.  This will be done automatically by Android
    // if the version will be bumped up, as we modify the database schema.
    @Override
    public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion ) {
        db.execSQL( "drop table if exists " + TABLE_QUIZ );
        db.execSQL( "drop table if exists " + TABLE_STATE );
        onCreate( db );
        Log.d( DEBUG_TAG, "Table " + TABLE_QUIZ + " AND " + TABLE_STATE + " upgraded" );
    }
}
