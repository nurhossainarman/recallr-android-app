package comp3350.flashcard.presentation;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import comp3350.flashcard.R;
import comp3350.flashcard.application.Services;
import comp3350.flashcard.logic.IStudySession;

/**
 * StudyActivity - UI for studying flashcards in a session.
 */
public class StudyActivity extends AppCompatActivity {

    private TextView tvProgress;
    private TextView tvContent;
    private TextView tvContentBack;
    private FrameLayout cardContainer;
    private View cardFront;
    private View cardBack;
    private IStudySession studySession;
    private boolean isShowingFront = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study);

        studySession = Services.getStudySession();
        initUI();
        startSession();
    }

    private void initUI() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tvProgress = findViewById(R.id.tvProgress);
        tvContent = findViewById(R.id.tvContent);
        tvContentBack = findViewById(R.id.tvContentBack);
        cardContainer = findViewById(R.id.cardContainer);
        cardFront = findViewById(R.id.cardFront);
        cardBack = findViewById(R.id.cardBack);

        cardContainer.setOnClickListener(v -> flipCard());

        findViewById(R.id.btnFlip).setOnClickListener(v -> flipCard());
        findViewById(R.id.btnNext).setOnClickListener(v -> goToNextCard());
        findViewById(R.id.btnPrevious).setOnClickListener(v -> goToPreviousCard());
    }

    private void startSession() {
        int deckId = getIntent().getIntExtra("DECK_ID", -1);
        boolean shuffle = getIntent().getBooleanExtra("SHUFFLE", false);

        studySession.startSession(deckId, shuffle);

        if (studySession.hasCards()) {
            resetCardToFront();
            updateUI();
        } else {
            finish();
        }
    }

    private void flipCard() {
        AnimatorSet flipOut = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.card_flip_out);
        AnimatorSet flipIn = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.card_flip_in);

        if (isShowingFront) {
            flipOut.setTarget(cardFront);
            flipIn.setTarget(cardBack);
            flipOut.start();
            flipIn.start();
            cardFront.postDelayed(() -> {
                cardFront.setVisibility(View.GONE);
                cardBack.setVisibility(View.VISIBLE);
                studySession.flip();
                tvContentBack.setText(studySession.getCurrentText());
            }, 400);
        } else {
            flipOut.setTarget(cardBack);
            flipIn.setTarget(cardFront);
            flipOut.start();
            flipIn.start();
            cardBack.postDelayed(() -> {
                cardBack.setVisibility(View.GONE);
                cardFront.setVisibility(View.VISIBLE);
                studySession.flip();
                tvContent.setText(studySession.getCurrentText());
            }, 400);
        }

        isShowingFront = !isShowingFront;
        tvProgress.setText(studySession.getProgressText());
    }

    private void goToNextCard() {
        if (studySession.isFinished()) {
            return;
        }
        studySession.nextCard();
        if (studySession.isFinished()) {
            return;
        }
        resetCardToFront();
        updateUI();
    }

    private void goToPreviousCard() {
        studySession.previousCard();
        resetCardToFront();
        updateUI();
    }

    private void resetCardToFront() {
        isShowingFront = true;
        cardFront.setVisibility(View.VISIBLE);
        cardBack.setVisibility(View.GONE);
        cardFront.setRotationY(0f);
        cardBack.setRotationY(0f);
    }

    private void updateUI() {
        tvContent.setText(studySession.getCurrentText());
        tvProgress.setText(studySession.getProgressText());
        tvContentBack.setText(studySession.getCurrentText());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}