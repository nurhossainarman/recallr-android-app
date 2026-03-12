package comp3350.flashcard.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import comp3350.flashcard.objects.Flashcard;
import comp3350.flashcard.persistence.FlashcardPersistence;

/**
 * Manages a study session, moving between cards, flipping them,
 * and filtering based on mode.
 */
public class StudySessionManager implements IStudySession {

    private final FlashcardPersistence flashcardPersistence;
    private final FlashcardManager flashcardManager;
    private List<Flashcard> sessionCards;
    private int currentIndex;
    private boolean showingFront;

    public StudySessionManager(FlashcardPersistence flashcardPersistence) {
        this.flashcardPersistence = flashcardPersistence;
        this.flashcardManager = new FlashcardManager(flashcardPersistence);
        this.sessionCards = new ArrayList<>();
        this.currentIndex = -1;
        this.showingFront = true;
    }

    /**
     * Starts a study session for a deck.
     * @param deckId the ID of the deck to study
     * @param shuffle true/false to shuffle the cards
     * @param filterMode which cards to include in session (all, known, or unknown)
     */
    @Override
    public void startSession(int deckId, boolean shuffle, FilterMode filterMode) {
        sessionCards = flashcardManager.getFlashcardsByMode(deckId, filterMode);
        
        if (sessionCards == null || sessionCards.isEmpty()) { // Selected mode has no card
            sessionCards = new ArrayList<>();
            currentIndex = -1;
        } else {    // Selected mode has at least one card
            sessionCards = new ArrayList<>(sessionCards);
            if (shuffle) {
                Collections.shuffle(sessionCards);
            }
            currentIndex = 0;
        }
        showingFront = true;
    }
    
    @Override
    public void nextCard() {
        if (currentIndex < sessionCards.size() - 1) {
            currentIndex++;
            showingFront = true;
        } else {
            currentIndex = sessionCards.size(); 
        }
    }
    
    @Override
    public void previousCard() {
        if (currentIndex > 0) {
            currentIndex--;
            showingFront = true;
        }
    }
    
    @Override
    public void flip() {
        showingFront = !showingFront;
    }

    @Override
    public void setKnown(boolean known) {
        Flashcard current = getCurrentCard();
        if (current != null) {
            current.setIsKnown(known);
            flashcardPersistence.updateFlashcard(current);
        }
    }

    @Override
    public String getCurrentText() {
        Flashcard current = getCurrentCard();
        if (current == null) {
            return "";
        }
        return showingFront ? current.getFront() : current.getBack();
    }

    @Override
    public String getProgressText() {
        return StudySessionHelper.formatProgressText(currentIndex, sessionCards.size());
    }
    
    @Override
    public boolean isFinished() {
        return StudySessionHelper.isFinished(currentIndex, sessionCards.size());
    }
    
    @Override
    public boolean hasCards() {
        return !sessionCards.isEmpty() && currentIndex >= 0 && currentIndex < sessionCards.size();
    }
    
    @Override
    public boolean isCurrentCardKnown() {
        Flashcard current = getCurrentCard();
        return current != null && current.getIsKnown();
    }

    public Flashcard getCurrentCard() {
        if (currentIndex >= 0 && currentIndex < sessionCards.size()) {
            return sessionCards.get(currentIndex);
        }
        return null;
    }

    // Helper methods for unit tests
    public int getPosition() {
        return currentIndex + 1;
    }

    public int getTotalCards() {
        return sessionCards.size();
    }
}
