package comp3350.flashcard.constants;

/**
 * Constants used for validation across the application.
 * Centralized to avoid magic numbers and ensure consistency.
 */
public final class ValidationConstants {

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private ValidationConstants() {
        throw new AssertionError("Cannot instantiate constants class");
    }

    /**
     * ID value used to indicate an invalid or non-existent entity.
     * Used for new entities that haven't been persisted yet.
     */
    public static final int INVALID_ID = -1;

    /**
     * Maximum allowed length for deck names.
     */
    public static final int MAX_DECK_NAME_LENGTH = 100;

    /**
     * Maximum allowed length for flashcard content (front or back).
     */
    public static final int MAX_FLASHCARD_CONTENT_LENGTH = 500;
}
