package comp3350.flashcard.logic;

import comp3350.flashcard.logic.exceptions.BusinessException;

/**
 * Exception thrown when deck validation fails.
 * Contains a descriptive message about what validation rule was violated.
 */
public class DeckValidationException extends BusinessException {

    /**
     * Constructs a new deck validation exception with the specified detail message.
     *
     * @param message the detail message explaining the validation failure
     */
    public DeckValidationException(String message) {
        super(message);
    }
}
