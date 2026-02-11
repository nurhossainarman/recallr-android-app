package comp3350.flashcard.application;

import comp3350.flashcard.logic.FlashcardManager;
import comp3350.flashcard.logic.DeckManager;
import comp3350.flashcard.persistence.FlashcardPersistence;
import comp3350.flashcard.persistence.DeckPersistence;

/**
 * Services class - manages application-wide service instances
 * Implements a simple service locator pattern for dependency management
 */
public class Services {
    private static FlashcardManager flashcardManager;
    private static DeckManager deckManager;
    private static FlashcardPersistence flashcardPersistence;
    private static DeckPersistence deckPersistence;

    /**
     * Gets the FlashcardPersistence instance
     * @return FlashcardPersistence instance
     */
    public static FlashcardPersistence getFlashcardPersistence() {
        return flashcardPersistence;
    }

    /**
     * Gets the DeckPersistence instance
     * @return DeckPersistence instance
     */
    public static DeckPersistence getDeckPersistence() {
        return deckPersistence;
    }

    /**
     * Sets the FlashcardPersistence instance
     * @param persistence FlashcardPersistence to set
     */
    public static void setFlashcardPersistence(FlashcardPersistence persistence) {
        flashcardPersistence = persistence;
    }

    /**
     * Sets the DeckPersistence instance
     * @param persistence DeckPersistence to set
     */
    public static void setDeckPersistence(DeckPersistence persistence) {
        deckPersistence = persistence;
    }

    /**
     * Gets the FlashcardManager instance
     * @return FlashcardManager instance
     */
    public static FlashcardManager getFlashcardManager() {
        if (flashcardManager == null) {
            flashcardManager = new FlashcardManager(flashcardPersistence);
        }
        return flashcardManager;
    }

    /**
     * Gets the DeckManager instance
     * @return DeckManager instance
     */
    public static DeckManager getDeckManager() {
        if (deckManager == null) {
            deckManager = new DeckManager(deckPersistence, flashcardPersistence);
        }
        return deckManager;
    }

    /**
     * Sets the FlashcardManager instance
     * @param manager FlashcardManager to set
     */
    public static void setFlashcardManager(FlashcardManager manager) {
        flashcardManager = manager;
    }

    /**
     * Sets the DeckManager instance
     * @param manager DeckManager to set
     */
    public static void setDeckManager(DeckManager manager) {
        deckManager = manager;
    }


    /**
     * Initializes all services with the provided persistence layers
     * This is a convenience method for setting up the entire service layer
     * @param deckPersist the deck persistence implementation
     * @param flashcardPersist the flashcard persistence implementation
     */
    public static void initialize(DeckPersistence deckPersist, FlashcardPersistence flashcardPersist) {
        setDeckPersistence(deckPersist);
        setFlashcardPersistence(flashcardPersist);

        // Create manager instances with the persistence layers
        deckManager = new DeckManager(deckPersist, flashcardPersist);
        flashcardManager = new FlashcardManager(flashcardPersist);
    }

    /**
     * Resets all services (useful for testing)
     */
    public static void cleanup() {
        flashcardManager = null;
        deckManager = null;
        flashcardPersistence = null;
        deckPersistence = null;
    }
}