package comp3350.flashcard.logic.exceptions;

/**
 * Base exception class for all business logic errors.
 * Extends RuntimeException to avoid forcing callers to catch checked exceptions.
 * All business layer exceptions should extend this class.
 */
public abstract class BusinessException extends RuntimeException {

    /**
     * Constructs a new business exception with the specified detail message.
     *
     * @param message the detail message explaining the error
     */
    protected BusinessException(String message) {
        super(message);
    }

    /**
     * Constructs a new business exception with the specified detail message and cause.
     *
     * @param message the detail message explaining the error
     * @param cause the cause of this exception
     */
    protected BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
