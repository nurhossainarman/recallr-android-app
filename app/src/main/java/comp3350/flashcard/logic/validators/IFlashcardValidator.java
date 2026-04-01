package comp3350.flashcard.logic.validators;

public interface IFlashcardValidator {

    /**
     * Validates the front and back content of a flashcard.
     *
     * @param front the front-side text
     * @param back  the back-side text
     * @return a ValidationResult indicating success or describing the first
     *         validation error
     */
    ValidationResult validate(String front, String back);
}
