package comp3350.flashcard.logic;

import java.util.List;
import comp3350.flashcard.objects.Deck;
import comp3350.flashcard.persistence.DeckPersistence;
import comp3350.flashcard.persistence.FlashcardPersistence;

public class DeckManager {

    private final DeckPersistence deckPersistence;
    private final FlashcardPersistence flashcardPersistence;

    /**
     * Constructor with dependency injection
     * @param deckPersistence the persistence layer for decks
     * @param flashcardPersistence the persistence layer for flashcards
     */
    public DeckManager(DeckPersistence deckPersistence, FlashcardPersistence flashcardPersistence) {
        this.deckPersistence = deckPersistence;
        this.flashcardPersistence = flashcardPersistence;
    }

    /**
     * Creates a new deck
     * @param name the name of the deck
     * @param description optional description of the deck
     * @return the created deck, or null if creation failed
     */
    public Deck createDeck(String name, String description) {
        validateDeck(name);
        validateDeckNameUnique(name, -1);

        Deck newDeck = new Deck(name, description);
        return deckPersistence.insertDeck(newDeck);
    }

    /**
     * Retrieves a deck by ID
     * @param deckId the ID of the deck to retrieve
     * @return the deck object, or null if not found
     */
    public Deck getDeck(int deckId) {
        return deckPersistence.getDeckById(deckId);
    }

    /**
     * Retrieves a deck by name
     * @param name the name of the deck to retrieve
     * @return the deck object, or null if not found
     */
    public Deck getDeckByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }

        return deckPersistence.getDeckByName(name);
    }

    /**
     * Updates an existing deck
     * @param deckId the ID of the deck to update
     * @param name the new name
     * @param description the new description
     * @return true if update successful, false otherwise
     */
    public boolean updateDeck(int deckId, String name, String description) {
        validateDeck(name);

        if (!deckExists(deckId)) {
            return false;
        }

        validateDeckNameUnique(name, deckId);

        Deck existingDeck = deckPersistence.getDeckById(deckId);
        if (existingDeck == null) {
            return false;
        }

        existingDeck.setName(name);
        existingDeck.setDescription(description);

        return deckPersistence.updateDeck(existingDeck);
    }

    /**
     * Deletes a deck and all its flashcards
     * @param deckId the ID of the deck to delete
     * @return true if deletion successful, false otherwise
     */
    public boolean deleteDeck(int deckId) {
        if (!deckExists(deckId)) {
            return false;
        }

        // Delete all flashcards in the deck first
        flashcardPersistence.deleteFlashcardsByDeckId(deckId);

        // Then delete the deck itself
        return deckPersistence.deleteDeck(deckId);
    }

    /**
     * Gets all decks
     * @return list of all decks
     */
    public List<Deck> getAllDecks() {
        return deckPersistence.getAllDecks();
    }

    /**
     * Validates deck data
     * @param name the deck name
     * @return true if valid, false otherwise
     */
    public void validateDeck(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new DeckValidationException("Deck name cannot be empty");
        }
        if (name.trim().length() > 100) {
            throw new DeckValidationException("Deck name cannot exceed 100 characters");
        }
    }

    /**
     * Validates that a deck name is unique
     * @param name the deck name to check
     * @param excludeDeckId deck ID to exclude from check (for updates), or -1 for new decks
     * @throws DeckValidationException if the name is already taken
     */
    public void validateDeckNameUnique(String name, int excludeDeckId) {
        if (deckPersistence.deckNameExists(name, excludeDeckId)) {
            throw new DeckValidationException("A deck with this name already exists");
        }
    }

    /**
     * Gets the count of flashcards in a deck
     * @param deckId the ID of the deck
     * @return number of flashcards in the deck
     */
    public int getFlashcardCount(int deckId) {
        if (!deckExists(deckId)) {
            return 0;
        }

        return flashcardPersistence.getFlashcardCountByDeckId(deckId);
    }

    /**
     * Checks if a deck exists
     * @param deckId the ID of the deck
     * @return true if deck exists, false otherwise
     */
    public boolean deckExists(int deckId) {
        return deckPersistence.deckExists(deckId);
    }

    /**
     * Gets the total number of decks
     * @return total deck count
     */
    public int getDeckCount() {
        return deckPersistence.getDeckCount();
    }

    /**
     * Updates the last studied timestamp for a deck
     * @param deckId the ID of the deck
     * @return true if update successful, false otherwise
     */
    public boolean markDeckAsStudied(int deckId) {
        if (!deckExists(deckId)) {
            return false;
        }

        Deck deck = deckPersistence.getDeckById(deckId);
        if (deck == null) {
            return false;
        }

        deck.setLastStudiedAt(System.currentTimeMillis());
        return deckPersistence.updateDeck(deck);
    }
}