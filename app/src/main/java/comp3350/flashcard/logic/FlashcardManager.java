package comp3350.flashcard.logic;

import java.util.ArrayList;
import java.util.List;
import comp3350.flashcard.constants.AppErrors;
import comp3350.flashcard.logic.exceptions.FlashcardValidationException;
import comp3350.flashcard.logic.validators.IFlashcardValidator;
import comp3350.flashcard.logic.validators.ValidationResult;
import comp3350.flashcard.objects.Flashcard;
import comp3350.flashcard.persistence.FlashcardPersistence;

/**
 * Handles operations related to individual flashcards.
 * Manages creating, retrieving, updating, and searching cards.
 */
public class FlashcardManager implements IFlashcardManager {

    private final FlashcardPersistence flashcardPersistence;
    private final IFlashcardValidator validator;

    /**
     * Creates a new flashcard manager.
     * @param flashcardPersistence the storage for flashcards
     * @param validator the strategy used to validate flashcard data
     */
    public FlashcardManager(FlashcardPersistence flashcardPersistence, IFlashcardValidator validator) {
        this.flashcardPersistence = flashcardPersistence;
        this.validator = validator;
    }

    @Override
    public Flashcard createFlashcard(String front, String back, int deckId) {
        ValidationResult result = validator.validate(front, back);
        if (!result.isValid()) {
            throw new FlashcardValidationException(result.getErrorMessage());
        }
        if (deckId <= 0) {
            throw new FlashcardValidationException(AppErrors.DECK_NOT_FOUND);
        }

        Flashcard flashcard = Flashcard.createNew(front, back, deckId);
        Flashcard inserted = flashcardPersistence.insertFlashcard(flashcard);
        if (inserted == null) {
            throw new NullPointerException(AppErrors.FAILED_CREATE_FLASHCARD);
        }
        return inserted;
    }

    @Override
    public Flashcard getFlashcard(int flashcardId) {
        if (flashcardId <= 0) {
            return null;
        }
        return flashcardPersistence.getFlashcardById(flashcardId);
    }

    @Override
    public boolean updateFlashcard(int flashcardId, String front, String back) {
        ValidationResult result = validator.validate(front, back);
        if (!result.isValid()) {
            throw new FlashcardValidationException(result.getErrorMessage());
        }

        Flashcard existingFlashcard = flashcardPersistence.getFlashcardById(flashcardId);
        if (existingFlashcard == null) {
            throw new FlashcardValidationException(AppErrors.FLASHCARD_NOT_FOUND);
        }

        Flashcard updatedFlashcard = existingFlashcard.withUpdatedContent(front, back);
        if (!flashcardPersistence.updateFlashcard(updatedFlashcard)) {
            throw new NullPointerException(AppErrors.FAILED_UPDATE_FLASHCARD);
        }
        return true;
    }

    @Override
    public boolean deleteFlashcard(int flashcardId) {
        if (!flashcardPersistence.flashcardExists(flashcardId)) {
            throw new FlashcardValidationException(AppErrors.FLASHCARD_NOT_FOUND);
        }
        return flashcardPersistence.deleteFlashcard(flashcardId);
    }

    @Override
    public List<Flashcard> getFlashcardsByDeck(int deckId) {
        if (deckId < 0) {
            return null;
        }
        return flashcardPersistence.getFlashcardsByDeckId(deckId);
    }

    @Override
    public List<Flashcard> getAllFlashcards() {
        return flashcardPersistence.getAllFlashcards();
    }

    @Override
    public int getFlashcardCount(int deckId) {
        if (deckId < 0) {
            return 0;
        }
        return flashcardPersistence.getFlashcardCountByDeckId(deckId);
    }

    @Override
    public List<Flashcard> getFlashcardsByMode(int deckId, FilterMode mode) {
        List<Flashcard> allCards = getFlashcardsByDeck(deckId);
        if (allCards == null) {
            return null;
        }

        if (mode == FilterMode.ALL) {
            return allCards;
        }

        List<Flashcard> results = new ArrayList<>();
        if (deckId < 0) {
            return results;
        }
        boolean wantKnown = (mode == FilterMode.KNOWN);

        for (Flashcard card : allCards) {
            if (card.getIsKnown() == wantKnown) {
                results.add(card);
            }
        }
        return results;
    }
}
