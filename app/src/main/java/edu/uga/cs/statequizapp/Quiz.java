package edu.uga.cs.statequizapp;

import java.util.Date;

public class Quiz {
    private long id;
    private State[] states = new State[6];
    private int currentQuestion;
    private int score;
    private Date dateCompleted;

    public Quiz(){
        this.id = -1;
        this.states = null;
        this.currentQuestion = -1;
        this.score = -1;
        this.dateCompleted = null;
    }

    public Quiz(State[] states, int currentQuestion, int score, Date dateCompleted){
        this.id = -1;
        this.states = states;
        this.currentQuestion = currentQuestion;
        this.score = score;
        this.dateCompleted = dateCompleted;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public State[] getStates() {
        return states;
    }

    public void setStates(State[] states) {
        this.states = states;
    }

    public int getCurrentQuestion() {
        return currentQuestion;
    }

    public void setCurrentQuestion(int currentQuestion) {
        this.currentQuestion = currentQuestion;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Date getDateCompleted() {
        return dateCompleted;
    }

    public void setDateCompleted(Date dateCompleted) {
        this.dateCompleted = dateCompleted;
    }

    public String toString() {
        return id + ": " + states + " " + currentQuestion + " " + score + " " + dateCompleted;

    }
}
