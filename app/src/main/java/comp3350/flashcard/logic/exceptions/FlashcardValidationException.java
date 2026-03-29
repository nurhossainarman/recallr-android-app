package comp3350.flashcard.logic.exceptions;

/**
 * Exception thrown when flashcard validation fails.
 * Contains a descriptive message about what validation rule was violated.
 */
public class FlashcardValidationException extends BusinessException {

    /**
     * Constructs a new flashcard validation exception with the specified detail message.
     *
     * @param message the detail message explaining the validation failure
     */
    public FlashcardValidationException(String message) {
        super(message);
    }
}
