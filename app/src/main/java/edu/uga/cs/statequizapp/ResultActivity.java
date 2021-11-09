package edu.uga.cs.statequizapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    private Button splashScreenBtn;
    private Button quizHistoryBtn;
    private TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Intent myIntent = getIntent();
        String score = myIntent.getStringExtra("score") + "%";
        textView = findViewById(R.id.textView);
        textView.setText(score);

        splashScreenBtn = findViewById(R.id.splash_screen);
        splashScreenBtn.setOnClickListener(new OnClickButtonListener(0));
        quizHistoryBtn = findViewById(R.id.quiz_history);
        quizHistoryBtn.setOnClickListener(new OnClickButtonListener(1));
    }

    private class OnClickButtonListener implements View.OnClickListener {

        private int screenChoice;

        public OnClickButtonListener(int screenChoice) { this.screenChoice = screenChoice; }

        @Override
        public void onClick(View view) {
            Intent intent = null;

            if (screenChoice == 0) { // Screen Choice 0 goes to Quiz Mode
                intent = new Intent(view.getContext(), SplashScreen.class);
            }
            else { // Screen Choice != 0 goes to Quiz History
                //intent = new Intent(view.getContext(), ResultsActivity.class);
            }
            startActivity(intent);
        }
    }
}