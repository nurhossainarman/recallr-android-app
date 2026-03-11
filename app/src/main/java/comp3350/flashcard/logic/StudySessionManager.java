package comp3350.flashcard.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import comp3350.flashcard.objects.Flashcard;
import comp3350.flashcard.persistence.FlashcardPersistence;

/**
 * StudySessionManager - handles logic for a study session, including shuffling cards and filtering.
 */
public class StudySessionManager implements IStudySession {

    private final FlashcardPersistence flashcardPersistence;
    private List<Flashcard> sessionCards;
    private int currentIndex;
    private boolean showingFront;

    public StudySessionManager(FlashcardPersistence flashcardPersistence) {
        this.flashcardPersistence = flashcardPersistence;
        this.sessionCards = new ArrayList<>();
        this.currentIndex = -1;
        this.showingFront = true;
    }

    @Override
    public void startSession(int deckId, boolean shuffle, FilterMode filterMode) {
        List<Flashcard> allCards = flashcardPersistence.getFlashcardsByDeckId(deckId);
        
        if (allCards == null || allCards.isEmpty()) {
            sessionCards = new ArrayList<>();
            currentIndex = -1;
        } else {
            sessionCards = new ArrayList<>();
            for (Flashcard card : allCards) {
                if (filterMode == FilterMode.ALL) {
                    sessionCards.add(card);
                } else if (filterMode == FilterMode.KNOWN && card.getIsKnown()) {
                    sessionCards.add(card);
                } else if (filterMode == FilterMode.UNKNOWN && !card.getIsKnown()) {
                    sessionCards.add(card);
                }
            }

            if (shuffle) {
                Collections.shuffle(sessionCards);
            }
            
            if (sessionCards.isEmpty()) {
                currentIndex = -1;
            } else {
                currentIndex = 0;
            }
        }
        showingFront = true;
    }

    @Override
    public void nextCard() {
        if (currentIndex < sessionCards.size() - 1) {
            currentIndex++;
            showingFront = true;
        } else {
            currentIndex = sessionCards.size(); // Mark as finished
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
        if (sessionCards.isEmpty()) {
            return "No cards to display";
        }
        // Clamping position for display
        int displayPos = Math.min(currentIndex + 1, sessionCards.size());
        return "Card " + displayPos + " of " + sessionCards.size();
    }

    @Override
    public boolean isFinished() {
        return sessionCards.isEmpty() || currentIndex >= sessionCards.size();
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

    @Override
    public int getPosition() {
        return currentIndex + 1;
    }

    @Override
    public int getTotalCards() {
        return sessionCards.size();
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
}
