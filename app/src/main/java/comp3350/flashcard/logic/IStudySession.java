package comp3350.flashcard.logic;

/**
 * Interface for managing a study session's state and navigation.
 * Only contains methods required by the UI.
 */
public interface IStudySession {

    void startSession(int deckId, boolean shuffle, FilterMode filterMode);
    void nextCard();
    void previousCard();
    void flip();
    void setKnown(boolean known);
    
    String getCurrentText();
    String getProgressText();
    boolean isFinished();
    boolean hasCards();
    boolean isCurrentCardKnown();
}
