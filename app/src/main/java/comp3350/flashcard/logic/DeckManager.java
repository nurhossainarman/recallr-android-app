package comp3350.flashcard.logic;

import java.util.List;
import comp3350.flashcard.logic.exceptions.DeckValidationException;
import comp3350.flashcard.constants.ValidationConstants;
import comp3350.flashcard.logic.validators.IDeckValidator;
import comp3350.flashcard.logic.validators.ValidationResult;
import comp3350.flashcard.objects.Deck;
import comp3350.flashcard.persistence.DeckPersistence;
import comp3350.flashcard.persistence.FlashcardPersistence;
import comp3350.flashcard.utils.StringUtils;

public class DeckManager implements IDeckManager {

    private final DeckPersistence deckPersistence;
    private final FlashcardPersistence flashcardPersistence;
    private final IDeckValidator validator;

    /**
     * Constructor with dependency injection
     * @param deckPersistence the persistence layer for decks
     * @param flashcardPersistence the persistence layer for flashcards
     * @param validator the strategy used to validate deck data
     */
    public DeckManager(DeckPersistence deckPersistence, FlashcardPersistence flashcardPersistence, IDeckValidator validator) {
        this.deckPersistence = deckPersistence;
        this.flashcardPersistence = flashcardPersistence;
        this.validator = validator;
    }

    /**
     * Creates a new deck
     * @param name the name of the deck
     * @param description optional description of the deck
     * @return the created deck
     * @throws DeckValidationException if the name is invalid or already taken
     */
    public Deck createDeck(String name, String description) {
        ValidationResult result = validator.validate(name, ValidationConstants.INVALID_ID);
        if (!result.isValid()) {
            throw new DeckValidationException(result.getErrorMessage());
        }

        Deck newDeck = Deck.createNew(name, description);
        Deck insert = deckPersistence.insertDeck(newDeck);
        if (insert == null) {
            throw new NullPointerException("Failed to create new deck");
        }
        return insert;
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
        if (StringUtils.isNullOrEmpty(name)) {
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
     * @throws DeckValidationException if the name is invalid or already taken by another deck
     */
    public boolean updateDeck(int deckId, String name, String description) {
        ValidationResult result = validator.validate(name, deckId);
        if (!result.isValid()) {
            throw new DeckValidationException(result.getErrorMessage());
        }

        if (!deckExists(deckId)) {
            throw new DeckValidationException("Deck not found");
        }

        Deck existingDeck = deckPersistence.getDeckById(deckId);
        if (existingDeck == null) {
            throw new NullPointerException("Failed to create deck.");
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
            throw new DeckValidationException("Deck not found");
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
            throw new DeckValidationException("Deck not found");
        }

        Deck deck = deckPersistence.getDeckById(deckId);
        if (deck == null) {
            throw new NullPointerException("Deck not found");
        }

        deck.setLastStudiedAt(System.currentTimeMillis());
        return deckPersistence.updateDeck(deck);
    }
}
