package comp3350.flashcard.presentation.study;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import comp3350.flashcard.R;
import comp3350.flashcard.application.Services;
import comp3350.flashcard.constants.UIConstants;
import comp3350.flashcard.constants.ValidationConstants;
import comp3350.flashcard.logic.FilterMode;
import comp3350.flashcard.logic.IStudySession;
import comp3350.flashcard.presentation.StudyGestureListener;

/**
 * Screen for studying flashcards in a session.
 * Handles the UI interactions, animations, and delegates session logic to IStudySession.
 */
public class StudyActivity extends AppCompatActivity implements StudyGestureListener.Actions {

    private TextView tvProgress;
    private TextView tvContent;
    private TextView tvContentBack;
    private TextView tvHint;
    private TextView tvHintLeft;
    private TextView tvHintRight;
    private CheckBox cbKnown;
    private FrameLayout cardContainer;
    private View cardFront;
    private View cardBack;
    
    private IStudySession studySession;
    private boolean isShowingFront = true;
    private boolean isFirstCard = true;

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
        tvHint = findViewById(R.id.tvHint);
        tvHintLeft = findViewById(R.id.tvHintLeft);
        tvHintRight = findViewById(R.id.tvHintRight);
        cbKnown = findViewById(R.id.cbKnown);
        cardContainer = findViewById(R.id.cardContainer);
        cardFront = findViewById(R.id.cardFront);
        cardBack = findViewById(R.id.cardBack);

        setupGestures();

        findViewById(R.id.btnFlip).setOnClickListener(v -> flipCard());
        findViewById(R.id.btnNext).setOnClickListener(v -> goToNextCard());
        findViewById(R.id.btnPrevious).setOnClickListener(v -> goToPreviousCard());
        
        cbKnown.setOnCheckedChangeListener((buttonView, isChecked) -> studySession.setKnown(isChecked));
    }

    private void setupGestures() {
        GestureDetector gestureDetector = new GestureDetector(this, new StudyGestureListener(this));
        setupTouch(gestureDetector);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupTouch(GestureDetector gestureDetector) {
        cardContainer.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            if (event.getAction() == MotionEvent.ACTION_UP) {
                v.performClick();
            }
            return true;
        });
    }

    private void startSession() {
        int deckId = getIntent().getIntExtra("DECK_ID", ValidationConstants.INVALID_ID);
        boolean shuffle = getIntent().getBooleanExtra("SHUFFLE", false);
        String filterModeStr = getIntent().getStringExtra("FILTER_MODE");
        
        FilterMode filterMode = parseFilterMode(filterModeStr);
        studySession.startSession(deckId, shuffle, filterMode);

        if (studySession.hasCards()) {
            isFirstCard = true;
            resetCardToFront();
            updateUI();
        } else {
            handleEmptySession(deckId);
        }
    }

    private FilterMode parseFilterMode(String filterModeStr) {
        if (filterModeStr != null) {
            try {
                return FilterMode.valueOf(filterModeStr);
            } catch (IllegalArgumentException ignored) {}
        }
        return FilterMode.ALL;
    }

    private void handleEmptySession(int deckId) {
        String message = studySession.isDeckEmpty(deckId) ? "Add some cards first!" : "No cards match this filter";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        finish();
    }

    private void flipCard() {
        AnimatorSet flipOut = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.card_flip_out);
        AnimatorSet flipIn = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.card_flip_in);

        final View targetOut = isShowingFront ? cardFront : cardBack;
        final View targetIn = isShowingFront ? cardBack : cardFront;
        final TextView contentTarget = isShowingFront ? tvContentBack : tvContent;

        flipOut.setTarget(targetOut);
        flipIn.setTarget(targetIn);
        flipOut.start();
        flipIn.start();

        targetOut.postDelayed(() -> {
            targetOut.setVisibility(View.GONE);
            targetIn.setVisibility(View.VISIBLE);
            studySession.flip();
            contentTarget.setText(studySession.getCurrentText());
        }, UIConstants.ANIMATION_DURATION_FLIP);

        isShowingFront = !isShowingFront;
        hideHints();
        tvProgress.setText(studySession.getProgressText());
    }

    private void goToNextCard() {
        studySession.nextCard();
        if (studySession.isFinished()) {
            finish();
            return;
        }
        isFirstCard = false;
        slideCardOut(true, () -> {
            resetCardToFront();
            updateUI();
            slideCardIn(false);
        });
    }

    private void goToPreviousCard() {
        isFirstCard = false;
        studySession.previousCard();
        slideCardOut(false, () -> {
            resetCardToFront();
            updateUI();
            slideCardIn(true);
        });
    }

    private void slideCardOut(boolean toLeft, Runnable onFinish) {
        float screenWidth = getResources().getDisplayMetrics().widthPixels;
        float targetX = toLeft ? -screenWidth : screenWidth;

        ObjectAnimator slideX = ObjectAnimator.ofFloat(cardContainer, "translationX", 0f, targetX);
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(cardContainer, "alpha", 1f, 0f);
        slideX.setDuration(UIConstants.ANIMATION_DURATION_SLIDE);
        fadeOut.setDuration(UIConstants.ANIMATION_DURATION_SLIDE);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(slideX, fadeOut);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                cardContainer.setTranslationX(0f);
                cardContainer.setAlpha(1f);
                if (onFinish != null) onFinish.run();
            }
        });
        set.start();
    }

    private void slideCardIn(boolean fromLeft) {
        float screenWidth = getResources().getDisplayMetrics().widthPixels;
        float startX = fromLeft ? -screenWidth : screenWidth;

        cardContainer.setTranslationX(startX);
        cardContainer.setAlpha(0f);

        ObjectAnimator slideX = ObjectAnimator.ofFloat(cardContainer, "translationX", startX, 0f);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(cardContainer, "alpha", 0f, 1f);
        slideX.setDuration(UIConstants.ANIMATION_DURATION_SLIDE);
        fadeIn.setDuration(UIConstants.ANIMATION_DURATION_SLIDE);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(slideX, fadeIn);
        set.start();
    }

    private void resetCardToFront() {
        isShowingFront = true;
        cardFront.setVisibility(View.VISIBLE);
        cardBack.setVisibility(View.GONE);
        cardFront.setRotationY(0f);
        cardBack.setRotationY(0f);

        if (isFirstCard) {
            tvHint.setVisibility(View.VISIBLE);
            tvHintLeft.setVisibility(View.VISIBLE);
            tvHintRight.setVisibility(View.VISIBLE);
        } else {
            hideHints();
        }
    }

    private void hideHints() {
        tvHint.setVisibility(View.GONE);
        tvHintLeft.setVisibility(View.GONE);
        tvHintRight.setVisibility(View.GONE);
    }

    private void updateUI() {
        tvContent.setText(studySession.getCurrentText());
        tvProgress.setText(studySession.getProgressText());
        tvContentBack.setText(studySession.getCurrentText());
        
        cbKnown.setOnCheckedChangeListener(null);
        cbKnown.setChecked(studySession.isCurrentCardKnown());
        cbKnown.setOnCheckedChangeListener((buttonView, isChecked) -> studySession.setKnown(isChecked));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    // Callbacks from StudyGestureListener
    @Override
    public void onSwipeLeft() {
        goToNextCard();
    }

    @Override
    public void onSwipeRight() {
        goToPreviousCard();
    }

    @Override
    public void onTap() {
        flipCard();
    }
}
