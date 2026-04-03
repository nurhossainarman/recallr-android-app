package comp3350.flashcard.presentation;

import comp3350.flashcard.constants.ValidationConstants;

/**
 * Helper class for common Activity logic.
 */
public class ActivityHelper {

    /**
     * Checks if the given ID represents a new entity (is equal to INVALID_ID).
     * @param id The entity ID to check
     * @return true if it's a new entity, false otherwise
     */
    public static boolean isNew(int id) {
        return id == ValidationConstants.INVALID_ID;
    }
}
