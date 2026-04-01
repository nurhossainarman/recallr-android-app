package comp3350.flashcard.logic.validators;

public interface IDeckValidator {

    /**
     * Validates a deck name.
     *
     * @param name          the deck name to validate
     * @param excludeDeckId the deck ID to exclude from uniqueness checks, use
     *                      ValidationConstants.INVALID_ID for new decks
     * @return a ValidationResult indicating success or describing the first
     *         validation error
     */
    ValidationResult validate(String name, int excludeDeckId);
}
