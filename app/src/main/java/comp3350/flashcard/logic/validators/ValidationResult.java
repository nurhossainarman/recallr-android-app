package comp3350.flashcard.logic.validators;

public class ValidationResult {

    private final boolean isValid;
    private final String errorMessage;

    private ValidationResult(boolean isValid, String errorMessage) {
        this.isValid = isValid;
        this.errorMessage = errorMessage;
    }

    /**
     * Creates a result representing a successful validation.
     *
     * @return a valid ValidationResult with no error message
     */
    public static ValidationResult success() {
        return new ValidationResult(true, null);
    }

    /**
     * Creates a result representing a failed validation.
     *
     * @param message a descriptive message explaining what went wrong
     * @return an invalid ValidationResult with the given message
     */
    public static ValidationResult error(String message) {
        return new ValidationResult(false, message);
    }

    public boolean isValid() {
        return isValid;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
