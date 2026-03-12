package comp3350.flashcard.logic;

/**
 * Interface for managing a study session and navigation.
 */
public interface IStudySession {

    /**
     * Initializes and starts a new study session.
     * @param deckId the ID of the deck to study
     * @param shuffle whether the cards should be presented in random order
     * @param filterMode specifies which cards to include (e.g., ALL, KNOWN, UNKNOWN)
     */
    void startSession(int deckId, boolean shuffle, FilterMode filterMode);

    /**
     * Moves to the next card in the session.
     */
    void nextCard();

    /**
     * Moves to the previous card in the session.
     */
    void previousCard();

    /**
     * Toggles between the front and back side of the current card.
     */
    void flip();

    /**
     * Updates whether the current card is marked as known.
     * @param known true if the user knows the card, false otherwise
     */
    void setKnown(boolean known);

    /**
     * Gets the text for the currently visible side of the card.
     * @return the front or back text
     */
    String getCurrentText();

    /**
     * Gets a formatted string representing session progress (e.g., "Card 1 of 10").
     * @return the progress display text
     */
    String getProgressText();

    /**
     * Checks if the session has reached the end.
     * @return true if there are no more cards to study
     */
    boolean isFinished();

    /**
     * Checks if there are cards available in the current session.
     * @return true if the session contains cards and is not finished
     */
    boolean hasCards();

    /**
     * Checks if the current card is marked as known.
     * @return true if the current card is known
     */
    boolean isCurrentCardKnown();
}
