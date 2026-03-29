package comp3350.flashcard.constants;

/**
 * Constants used for UI interactions and animations.
 * Centralized to ensure consistent user experience.
 */
public final class UIConstants {

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private UIConstants() {
        throw new AssertionError("Cannot instantiate constants class");
    }

    /**
     * Minimum distance (in pixels) a swipe must travel to be recognized as a swipe gesture.
     */
    public static final int SWIPE_THRESHOLD = 100;

    /**
     * Minimum velocity (in pixels per second) required for a swipe gesture to be recognized.
     */
    public static final int SWIPE_VELOCITY_THRESHOLD = 100;
}
