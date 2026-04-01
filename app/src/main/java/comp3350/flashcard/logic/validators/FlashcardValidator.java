package comp3350.flashcard.logic.validators;

import comp3350.flashcard.utils.StringUtils;

public class FlashcardValidator implements IFlashcardValidator {

    /**
     * Validates the front and back content of a flashcard.
     *
     * @param front the front-side text
     * @param back  the back-side text
     * @return a ValidationResult indicating success or describing the first
     *         validation error
     */
    public ValidationResult validate(String front, String back) {
        if (StringUtils.isNullOrEmpty(front)) {
            return ValidationResult.error("Front of flashcard cannot be empty");
        }

        if (StringUtils.isNullOrEmpty(back)) {
            return ValidationResult.error("Back of flashcard cannot be empty");
        }

        return ValidationResult.success();
    }
}
