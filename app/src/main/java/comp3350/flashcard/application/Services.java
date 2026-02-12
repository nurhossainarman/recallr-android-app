package comp3350.flashcard.application;

import comp3350.flashcard.logic.FlashcardManager;
import comp3350.flashcard.logic.DeckManager;
import comp3350.flashcard.persistence.FlashcardPersistence;
import comp3350.flashcard.persistence.DeckPersistence;
import comp3350.flashcard.persistence.stubs.DeckPersistenceStub;
import comp3350.flashcard.persistence.stubs.FlashcardPersistenceStub;

/**
 * Services class - manages application-wide service instances
 * Implements a simple service locator pattern for dependency management
 */
public class Services {
    private static FlashcardManager flashcardManager;
    private static DeckManager deckManager;
    private static FlashcardPersistence flashcardPersistence;
    private static DeckPersistence deckPersistence;

    public static FlashcardPersistence getFlashcardPersistence() {
        if (flashcardPersistence == null) {
            flashcardPersistence = new FlashcardPersistenceStub();
        }
        return flashcardPersistence;
    }

    public static DeckPersistence getDeckPersistence() {
        if (deckPersistence == null) {
            deckPersistence = new DeckPersistenceStub();
        }
        return deckPersistence;
    }

    public static FlashcardManager getFlashcardManager() {
        if (flashcardManager == null) {
            flashcardManager = new FlashcardManager(getFlashcardPersistence());
        }
        return flashcardManager;
    }

    public static DeckManager getDeckManager() {
        if (deckManager == null) {
            deckManager = new DeckManager(getDeckPersistence(), getFlashcardPersistence());
        }
        return deckManager;
    }

    public static void initialize(DeckPersistence deckPersist, FlashcardPersistence flashcardPersist) {
        deckPersistence = deckPersist;
        flashcardPersistence = flashcardPersist;
        deckManager = new DeckManager(deckPersist, flashcardPersist);
        flashcardManager = new FlashcardManager(flashcardPersist);
    }

    public static void cleanup() {
        flashcardManager = null;
        deckManager = null;
        flashcardPersistence = null;
        deckPersistence = null;
    }
}
