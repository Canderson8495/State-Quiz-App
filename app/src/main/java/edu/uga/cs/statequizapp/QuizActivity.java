package edu.uga.cs.statequizapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class QuizActivity extends AppCompatActivity implements GestureDetector.OnGestureListener{
    public static final String DEBUG_TAG = "QuizActivity";
    private static final String questionStem = "What is the capital of ";
    private ArrayList<String> choiceMapping = new ArrayList<>();
    private TextView stateNameText;
    private TextView questionText;
    private RadioButton[] choices = new RadioButton[3];
    private RadioGroup radioGroup;
    private Button nextButton;
    private ConstraintLayout layout;
    Quiz newQuiz = new Quiz();
    private DBData dbData = null;


    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        if(motionEvent.getX() - motionEvent1.getX() > 0){
            String correctAnswer = newQuiz.getStates()[newQuiz.getCurrentQuestion()-1].getCapital();
            int userAnswerID = radioGroup.getCheckedRadioButtonId();
            int correct;
            switch(userAnswerID){
                case R.id.choice1:
                    correct = 0;
                    break;
                case R.id.choice2:
                    correct = 1;
                    break;
                case R.id.choice3:
                    correct = 2;
                    break;
                default:
                    return true;
            }

            String userAnswer = choiceMapping.get(correct);
            if(userAnswer.equalsIgnoreCase(correctAnswer)){
                newQuiz.setScore(newQuiz.getScore()+1);
            }
            if(newQuiz.getCurrentQuestion() < 6){
                newQuiz.setCurrentQuestion(newQuiz.getCurrentQuestion()+1);
            }else{
                newQuiz.setCurrentQuestion(-1);
            }

            new DBQuizUpdate().execute(newQuiz);

            if(newQuiz.getCurrentQuestion() == -1){
                //Send to finishing screen
                System.out.println("Tes");
            }else{
                choiceMapping.clear();
                String stateName = newQuiz.getStates()[newQuiz.getCurrentQuestion()-1].getName();
                stateNameText.setText(questionStem + stateName + "?");
                String stateCapital = newQuiz.getStates()[newQuiz.getCurrentQuestion()-1].getCapital();
                ArrayList<String> cities = new ArrayList<>();
                cities.addAll(Arrays.asList(newQuiz.getStates()[newQuiz.getCurrentQuestion()-1].getCities()));
                cities.add(stateCapital);

                Random ran = new Random();
                String randomCityChoice = cities.get(ran.nextInt(2));
                choices[0].setText(randomCityChoice);
                choiceMapping.add(randomCityChoice);
                cities.remove(randomCityChoice);
                randomCityChoice = cities.get(ran.nextInt(1));
                choices[1].setText(randomCityChoice);
                choiceMapping.add(randomCityChoice);
                cities.remove(randomCityChoice);
                randomCityChoice = cities.get(0);
                choices[2].setText(randomCityChoice);
                choiceMapping.add(randomCityChoice);
                cities.remove(randomCityChoice);
            }
        }
        return true;
    }

    public class DBQuizUpdate extends AsyncTask<Quiz, Quiz> {

        // This method will run as a background process to write into db.
        // It will be automatically invoked by Android, when we call the execute method
        // in the onClick listener of the Save button.
        @Override
        protected Quiz doInBackground(Quiz... quiz) {
            dbData.updateQuiz(quiz[0]);
            return quiz[0];
        }

        // This method will be automatically called by Android once the writing to the database
        // in a background process has finished.  Note that doInBackground returns a JobLead object.
        // That object will be passed as argument to onPostExecute.
        // onPostExecute is like the notify method in an asynchronous method call discussed in class.
        @Override
        protected void onPostExecute(Quiz quiz) {
            newQuiz = quiz;
        }
    }
    public class DBQuizCreate extends AsyncTask<Quiz, Quiz> {

        // This method will run as a background process to write into db.
        // It will be automatically invoked by Android, when we call the execute method
        // in the onClick listener of the Save button.
        @Override
        protected Quiz doInBackground(Quiz... quiz) {
            Log.d( DEBUG_TAG, "QuiActivity.DBQuizCreate.doInBackground()" );
            return dbData.createQuiz(quiz[0]);
        }

        // This method will be automatically called by Android once the writing to the database
        // in a background process has finished.  Note that doInBackground returns a JobLead object.
        // That object will be passed as argument to onPostExecute.
        // onPostExecute is like the notify method in an asynchronous method call discussed in class.
        @Override
        protected void onPostExecute(Quiz quiz) {
            Log.d( DEBUG_TAG, "QuizActivity.DBQuizCreate.onPostExecute()" );
            newQuiz = quiz;
        }
    }



    public class StateDBReader extends AsyncTask<Void, List<State>> {

        // This method will run as a background process to write into db.
        // It will be automatically invoked by Android, when we call the execute method
        // in the onClick listener of the Save button.
        @Override
        protected List<State> doInBackground( Void... params ) {
            Log.d( DEBUG_TAG, "QuizActivity.StateDBReader.doInBackground()" );
            Quiz quiz = dbData.getExistingQuiz();

            ArrayList<State> states = new ArrayList<>();
            if(quiz == null){
                Log.d(DEBUG_TAG, "There is no existing quiz");
                states.addAll(dbData.retrieveAllStates());
            }else{
                Log.d(DEBUG_TAG, "There is an existing quiz");
                newQuiz = quiz;
            }

            Log.d(DEBUG_TAG, "Content size of states  =  " + states.size());
            return states;
        }

        // This method will be automatically called by Android once the writing to the database
        // in a background process has finished.  Note that doInBackground returns a JobLead object.
        // That object will be passed as argument to onPostExecute.
        // onPostExecute is like the notify method in an asynchronous method call discussed in class.
        @Override
        protected void onPostExecute( List<State> state ) {
            Random ran = new Random();
            if(state.size() != 0) {
                Log.d(DEBUG_TAG, "Content size of states  =  " + state.size());
                Log.d(DEBUG_TAG, "QuizActivity.StateDBReader.onPostExecute()");

                // generating integer
                ArrayList<Integer> randomStateIDs = new ArrayList<Integer>();
                State[] states = new State[6];
                while (randomStateIDs.size() < 6) {
                    int newState = ran.nextInt(50);
                    if (!randomStateIDs.contains(newState)) {
                        states[randomStateIDs.size()] = state.get(newState);
                        randomStateIDs.add(newState);
                    }
                }
                newQuiz.setStates(states);
                newQuiz.setCurrentQuestion(1);
                newQuiz.setScore(0);
            }


            String stateName = newQuiz.getStates()[newQuiz.getCurrentQuestion()-1].getName();
            stateNameText.setText(questionStem + stateName + "?");
            String stateCapital = newQuiz.getStates()[newQuiz.getCurrentQuestion()-1].getCapital();
            ArrayList<String> cities = new ArrayList<>();
            questionText.setText("Question " + newQuiz.getCurrentQuestion());
            cities.addAll(Arrays.asList(newQuiz.getStates()[newQuiz.getCurrentQuestion()-1].getCities()));
            cities.add(stateCapital);
                String randomCityChoice = cities.get(ran.nextInt(2));
                choices[0].setText(randomCityChoice);
                choiceMapping.add(randomCityChoice);
                cities.remove(randomCityChoice);
            randomCityChoice = cities.get(ran.nextInt(1));
            choices[1].setText(randomCityChoice);
            choiceMapping.add(randomCityChoice);
            cities.remove(randomCityChoice);
            randomCityChoice = cities.get(0);
            choices[2].setText(randomCityChoice);
            choiceMapping.add(randomCityChoice);
            cities.remove(randomCityChoice);
            if(state.size() > 0){
                new DBQuizCreate().execute(newQuiz);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d( DEBUG_TAG, "QuizActivity.onCreate()" );
        super.onCreate(savedInstanceState);
        dbData = new DBData( this );
        setContentView(R.layout.activity_quiz);
        stateNameText = findViewById(R.id.stateName);
        choices[0] =findViewById(R.id.choice1);
        choices[1] = findViewById(R.id.choice2);
        choices[2] = findViewById(R.id.choice3);
        nextButton = findViewById(R.id.next);
        questionText = findViewById(R.id.question);
        layout = findViewById(R.id.layout);
        radioGroup = findViewById(R.id.radioGroup);

        new StateDBReader().execute();
        //I need the state name and the capital and the two alternatives



        //Implement swipe handler
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(DEBUG_TAG, "In OnClick");
                String correctAnswer = newQuiz.getStates()[newQuiz.getCurrentQuestion()-1].getCapital();
                Log.d(DEBUG_TAG, "Correct Answer " + correctAnswer);
                int userAnswerID = radioGroup.getCheckedRadioButtonId();
                int correct;
                switch(userAnswerID){
                    case R.id.choice1:
                        correct = 0;
                        break;
                    case R.id.choice2:
                        correct = 1;
                        break;
                    case R.id.choice3:
                        correct = 2;
                        break;
                    default:
                        return;
                }

                String userAnswer = choiceMapping.get(correct);
                Log.d(DEBUG_TAG, "User Answer " + userAnswer);
                if(userAnswer.equalsIgnoreCase(correctAnswer)){
                    newQuiz.setScore(newQuiz.getScore()+1);
                }
                if(newQuiz.getCurrentQuestion() < 6){
                    newQuiz.setCurrentQuestion(newQuiz.getCurrentQuestion()+1);
                }else{
                    newQuiz.setCurrentQuestion(-1);
                }

                new DBQuizUpdate().execute(newQuiz);

                if(newQuiz.getCurrentQuestion() == -1){
                    //Send to finishing screen
                    Log.d(DEBUG_TAG, "QuizActivity.OnCLick()");
                    Log.d(DEBUG_TAG, "Score: " + String.valueOf(((double)newQuiz.getScore()/6)*100));
                    Intent intent = new Intent(view.getContext(), ResultActivity.class);
                    String score = String.valueOf(((double)newQuiz.getScore()/6)*100);
                    intent.putExtra("score",score);
                    startActivity(intent);
                }else{
                    choiceMapping.clear();
                    String stateName = newQuiz.getStates()[newQuiz.getCurrentQuestion()-1].getName();
                    stateNameText.setText(questionStem + stateName + "?");
                    String stateCapital = newQuiz.getStates()[newQuiz.getCurrentQuestion()-1].getCapital();
                    ArrayList<String> cities = new ArrayList<>();
                    questionText.setText("Question " + newQuiz.getCurrentQuestion());
                    cities.add(stateCapital);
                    cities.addAll(Arrays.asList(newQuiz.getStates()[newQuiz.getCurrentQuestion()-1].getCities()));
                    Random ran = new Random();
                    String randomCityChoice = cities.get(ran.nextInt(2));
                    choices[0].setText(randomCityChoice);
                    choiceMapping.add(randomCityChoice);
                    cities.remove(randomCityChoice);
                    randomCityChoice = cities.get(ran.nextInt(1));
                    choices[1].setText(randomCityChoice);
                    choiceMapping.add(randomCityChoice);
                    cities.remove(randomCityChoice);
                    randomCityChoice = cities.get(0);
                    choices[2].setText(randomCityChoice);
                    choiceMapping.add(randomCityChoice);
                    cities.remove(randomCityChoice);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        Log.d( DEBUG_TAG, "QuizActivity.onStart()" );
        super.onStart();


    }


}