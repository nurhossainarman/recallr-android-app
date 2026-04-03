package comp3350.flashcard.logic;

import java.util.List;
import comp3350.flashcard.logic.exceptions.DeckValidationException;
import comp3350.flashcard.constants.AppErrors;
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

    @Override
    public Deck createDeck(String name, String description) {
        ValidationResult result = validator.validate(name, ValidationConstants.INVALID_ID);
        if (!result.isValid()) {
            throw new DeckValidationException(result.getErrorMessage());
        }

        Deck newDeck = Deck.createNew(name, description);
        Deck insert = deckPersistence.insertDeck(newDeck);
        if (insert == null) {
            throw new NullPointerException(AppErrors.FAILED_CREATE_DECK);
        }
        return insert;
    }

   @Override
    public Deck getDeck(int deckId) {
        return deckPersistence.getDeckById(deckId);
    }

   @Override
    public Deck getDeckByName(String name) {
        if (StringUtils.isNullOrEmpty(name)) {
            return null;
        }

        return deckPersistence.getDeckByName(name);
    }

    @Override
    public boolean updateDeck(int deckId, String name, String description) {
        ValidationResult result = validator.validate(name, deckId);
        if (!result.isValid()) {
            throw new DeckValidationException(result.getErrorMessage());
        }
        if (!deckExists(deckId)) {
            throw new DeckValidationException(AppErrors.DECK_NOT_FOUND);
        }

        Deck existingDeck = deckPersistence.getDeckById(deckId);
        if (existingDeck == null) {
            throw new NullPointerException(AppErrors.FAILED_UPDATE_DECK);
        }

        existingDeck.setName(name);
        existingDeck.setDescription(description);

        return deckPersistence.updateDeck(existingDeck);
    }

    @Override
    public boolean deleteDeck(int deckId) {
        if (!deckExists(deckId)) {
            throw new DeckValidationException(AppErrors.DECK_NOT_FOUND);
        }

        // Delete all flashcards in the deck first
        flashcardPersistence.deleteFlashcardsByDeckId(deckId);

        // Then delete the deck itself
        return deckPersistence.deleteDeck(deckId);
    }

    @Override
    public List<Deck> getAllDecks() {
        return deckPersistence.getAllDecks();
    }

    @Override
    public int getFlashcardCount(int deckId) {
        if (!deckExists(deckId)) {
            return 0;
        }

        return flashcardPersistence.getFlashcardCountByDeckId(deckId);
    }

    @Override
    public boolean deckExists(int deckId) {
        return deckPersistence.deckExists(deckId);
    }

    @Override
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
            throw new DeckValidationException(AppErrors.DECK_NOT_FOUND);
        }

        Deck deck = deckPersistence.getDeckById(deckId);
        if (deck == null) {
            throw new NullPointerException(AppErrors.DECK_NOT_FOUND);
        }

        deck.setLastStudiedAt(System.currentTimeMillis());
        return deckPersistence.updateDeck(deck);
    }
}
