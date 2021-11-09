package edu.uga.cs.statequizapp;


import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * This is an adapter class for the RecyclerView to show all job leads.
 */
public class QuizRecyclerAdapter extends RecyclerView.Adapter<QuizRecyclerAdapter.QuizHolder> {

    public static final String DEBUG_TAG = "QuizRecyclerAdapter";

    private List<Quiz> quizList;

    public QuizRecyclerAdapter( List<Quiz> jobLeadList ) {
        this.quizList = jobLeadList;
    }

    // The adapter must have a ViewHolder class to "hold" one item to show.
    class QuizHolder extends RecyclerView.ViewHolder {

        TextView date;
        TextView score;

        public QuizHolder(View itemView ) {
            super(itemView);

            date = (TextView) itemView.findViewById( R.id.date );
            score = (TextView) itemView.findViewById( R.id.score );
        }
    }

    @Override
    public QuizHolder onCreateViewHolder( ViewGroup parent, int viewType ) {
        // We need to make sure that all CardViews have the same, full width, allowed by the parent view.
        // This is a bit tricky, and we must provide the parent reference (the second param of inflate)
        // and false as the third parameter (don't attach to root).
        // Consequently, the parent view's (the RecyclerView) width will be used (match_parent).
        View view = LayoutInflater.from( parent.getContext()).inflate( R.layout.result, parent, false );
        return new QuizHolder( view );
    }

    // This method fills in the values of a holder to show a JobLead.
    // The position parameter indicates the position on the list of jobs list.
    @Override
    public void onBindViewHolder( QuizHolder holder, int position ) {
        Quiz quiz = quizList.get( position );

        Log.d( DEBUG_TAG, "onBindViewHolder: " + quiz );

        holder.date.setText( "Date: " + quiz.getDateCompleted().toString());
        holder.score.setText("Score: " + quiz.getScore() + "/6" );
//        holder.url.setText( jobLead.getUrl() );
//        holder.comments.setText( jobLead.getComments() );
    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }
}
