package comp3350.flashcard.presentation;

import android.view.GestureDetector;
import android.view.MotionEvent;
import androidx.annotation.NonNull;
import comp3350.flashcard.constants.UIConstants;

/**
 * Handles gesture detection for the study session, decoupling input handling from the Activity.
 */
public class StudyGestureListener extends GestureDetector.SimpleOnGestureListener {

    /**
     * Interface to delegate study actions back to the presenter or activity.
     */
    public interface Actions {
        void onSwipeLeft();
        void onSwipeRight();
        void onTap();
    }

    private final Actions actions;

    public StudyGestureListener(Actions actions) {
        this.actions = actions;
    }

    @Override
    public boolean onDown(@NonNull MotionEvent e) {
        return true;
    }

    @Override
    public boolean onFling(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
        float deltaX = e2.getX() - e1.getX();
        float deltaY = e2.getY() - e1.getY();

        // Check if the horizontal movement is significant enough to be a swipe
        if (Math.abs(deltaX) > Math.abs(deltaY)
                && Math.abs(deltaX) > UIConstants.SWIPE_THRESHOLD
                && Math.abs(velocityX) > UIConstants.SWIPE_VELOCITY_THRESHOLD) {
            
            if (deltaX < 0) {
                actions.onSwipeLeft();
            } else {
                actions.onSwipeRight();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onSingleTapUp(@NonNull MotionEvent e) {
        actions.onTap();
        return true;
    }
}
