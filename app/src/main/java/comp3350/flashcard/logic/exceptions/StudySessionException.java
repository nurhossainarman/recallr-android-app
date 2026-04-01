package comp3350.flashcard.logic.exceptions;

/**
 * Exception thrown when a study session cannot be started or maintained.
 */
public class StudySessionException extends BusinessException {
    public StudySessionException(String message) {
        super(message);
    }
}
