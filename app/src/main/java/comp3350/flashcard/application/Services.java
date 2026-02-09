package comp3350.flashcard.application;

import comp3350.flashcard.persistence.DeckPersistence;
import comp3350.flashcard.persistence.FlashcardPersistence;
import comp3350.flashcard.persistence.stubs.DeckPersistenceStub;
import comp3350.flashcard.persistence.stubs.FlashcardPersistenceStub;

/**
 * Service Locator to manage persistence singletons.
 * Promotes low coupling by hiding implementation details from the presentation layer.
 */
public class Services {
    private static DeckPersistence deckPersistence = null;
    private static FlashcardPersistence flashcardPersistence = null;

    public static synchronized DeckPersistence getDeckPersistence() {
        if (deckPersistence == null) {
            deckPersistence = new DeckPersistenceStub();
        }
        return deckPersistence;
    }

    public static synchronized FlashcardPersistence getFlashcardPersistence() {
        if (flashcardPersistence == null) {
            flashcardPersistence = new FlashcardPersistenceStub();
        }
        return flashcardPersistence;
    }
}
