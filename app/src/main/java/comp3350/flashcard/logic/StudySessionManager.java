package comp3350.flashcard.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import comp3350.flashcard.objects.Flashcard;
import comp3350.flashcard.persistence.FlashcardPersistence;

/**
 * StudySessionManager - handles logic for a study session, including shuffling cards.
 */
public class StudySessionManager {

    private final FlashcardPersistence flashcardPersistence;
    private List<Flashcard> sessionCards;
    private int currentIndex;

    /**
     * Constructor with dependency injection
     * @param flashcardPersistence the persistence layer for flashcards
     */
    public StudySessionManager(FlashcardPersistence flashcardPersistence) {
        this.flashcardPersistence = flashcardPersistence;
        this.sessionCards = new ArrayList<>();
        this.currentIndex = -1;
    }

    /**
     * Starts a new study session for a specific deck.
     * @param deckId the ID of the deck to study
     * @param shuffle whether to shuffle the cards or not
     * @return the first flashcard in the session, or null if deck is empty
     */
    public Flashcard startSession(int deckId, boolean shuffle) {
        List<Flashcard> cards = flashcardPersistence.getFlashcardsByDeckId(deckId);
        
        if (cards == null || cards.isEmpty()) {
            sessionCards = new ArrayList<>();
            currentIndex = -1;
            return null;
        }

        sessionCards = new ArrayList<>(cards);
        
        if (shuffle) {
            Collections.shuffle(sessionCards);
        }

        currentIndex = 0;
        return sessionCards.get(currentIndex);
    }

    /**
     * Gets the current card in the session.
     * @return current flashcard or null if no session active
     */
    public Flashcard getCurrentCard() {
        if (currentIndex >= 0 && currentIndex < sessionCards.size()) {
            return sessionCards.get(currentIndex);
        }
        return null;
    }

    /**
     * Moves to the next card in the session.
     * @return the next flashcard, or null if at the end
     */
    public Flashcard nextCard() {
        if (currentIndex < sessionCards.size() - 1) {
            currentIndex++;
            return sessionCards.get(currentIndex);
        }
        return null;
    }

    /**
     * Moves to the previous card in the session.
     * @return the previous flashcard, or null if at the beginning
     */
    public Flashcard previousCard() {
        if (currentIndex > 0) {
            currentIndex--;
            return sessionCards.get(currentIndex);
        }
        return null;
    }

    /**
     * Gets the progress of the current session.
     * @return current position (1-based)
     */
    public int getPosition() {
        return currentIndex + 1;
    }

    /**
     * Gets total number of cards in session.
     * @return total cards
     */
    public int getTotalCards() {
        return sessionCards.size();
    }

    /**
     * Checks if session is finished.
     * @return true if no more cards left
     */
    public boolean isFinished() {
        return sessionCards.isEmpty() || currentIndex >= sessionCards.size() - 1;
    }
}
