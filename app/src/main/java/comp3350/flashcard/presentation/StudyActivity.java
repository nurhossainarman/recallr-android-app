package comp3350.flashcard.presentation;

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
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import comp3350.flashcard.R;
import comp3350.flashcard.application.Services;
import comp3350.flashcard.logic.IStudySession;

/**
 * StudyActivity - UI for studying flashcards in a session.
 */
public class StudyActivity extends AppCompatActivity {

    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;

    private TextView tvProgress;
    private TextView tvContent;
    private TextView tvContentBack;
    private TextView tvHint;
    private TextView tvHintLeft;
    private TextView tvHintRight;
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
        cardContainer = findViewById(R.id.cardContainer);
        cardFront = findViewById(R.id.cardFront);
        cardBack = findViewById(R.id.cardBack);

        GestureDetector gestureDetector = new GestureDetector(this,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDown(@NonNull MotionEvent e) {
                        return true;
                    }

                    @Override
                    public boolean onFling(@NonNull MotionEvent e1, @NonNull MotionEvent e2,
                                           float velocityX, float velocityY) {
                        float deltaX = e2.getX() - e1.getX();
                        float deltaY = e2.getY() - e1.getY();

                        if (Math.abs(deltaX) > Math.abs(deltaY)
                                && Math.abs(deltaX) > SWIPE_THRESHOLD
                                && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (deltaX < 0) {
                                goToNextCard();
                            } else {
                                goToPreviousCard();
                            }
                            return true;
                        }
                        return false;
                    }

                    @Override
                    public boolean onSingleTapUp(@NonNull MotionEvent e) {
                        flipCard();
                        return true;
                    }
                });

        setupTouch(gestureDetector);

        findViewById(R.id.btnFlip).setOnClickListener(v -> flipCard());
        findViewById(R.id.btnNext).setOnClickListener(v -> goToNextCard());
        findViewById(R.id.btnPrevious).setOnClickListener(v -> goToPreviousCard());
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
        int deckId = getIntent().getIntExtra("DECK_ID", -1);
        boolean shuffle = getIntent().getBooleanExtra("SHUFFLE", false);

        studySession.startSession(deckId, shuffle);

        if (studySession.hasCards()) {
            isFirstCard = true;
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
        hideHints();
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
        slideX.setDuration(320);
        fadeOut.setDuration(320);

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
        slideX.setDuration(320);
        fadeIn.setDuration(320);

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
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}