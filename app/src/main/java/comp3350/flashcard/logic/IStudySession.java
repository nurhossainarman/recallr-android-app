package comp3350.flashcard.logic;

/**
 * Interface for managing a study session's state and navigation.
 */
public interface IStudySession {
    void startSession(int deckId, boolean shuffle);
    void nextCard();
    void previousCard();
    void flip();
    
    String getCurrentText();
    String getProgressText();
    boolean isFinished();
    boolean hasCards();
}
