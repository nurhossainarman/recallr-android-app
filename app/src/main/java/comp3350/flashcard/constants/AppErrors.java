package comp3350.flashcard.constants;

/**
 * Constants for error messages throughout the application.
 */
public final class AppErrors {

    /**
     * Private constructor to prevent instantiation.
     */
    private AppErrors() {
        throw new AssertionError("Cannot instantiate AppErrors");
    }

    // Deck related errors
    public static final String DECK_NOT_FOUND = "Deck not found";
    public static final String FAILED_CREATE_DECK = "Failed to create new deck";
    public static final String FAILED_UPDATE_DECK = "Failed to update deck";

    // Flashcard related errors
    public static final String FLASHCARD_NOT_FOUND = "Flashcard not found";
    public static final String FAILED_CREATE_FLASHCARD = "Failed to create flashcard";
    public static final String FAILED_UPDATE_FLASHCARD = "Failed to update flashcard";

    // Study Session errors
    public static final String NO_CARDS_FOR_SESSION = "Add some cards first!";
    public static final String NO_MATCHING_CARDS_FILTER = "No cards match this filter";
}
