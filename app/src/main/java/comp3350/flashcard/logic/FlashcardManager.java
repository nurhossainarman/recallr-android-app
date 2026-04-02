package comp3350.flashcard.logic;

import java.util.ArrayList;
import java.util.List;
import comp3350.flashcard.logic.exceptions.FlashcardValidationException;
import comp3350.flashcard.logic.validators.IFlashcardValidator;
import comp3350.flashcard.logic.validators.ValidationResult;
import comp3350.flashcard.objects.Flashcard;
import comp3350.flashcard.persistence.FlashcardPersistence;
import comp3350.flashcard.utils.StringUtils;

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

    /**
     * Creates a new flashcard and saves it.
     * @param front the text on the front side
     * @param back the text on the back side
     * @param deckId the ID of the deck it belongs to
     * @return the new card, never null
     * @throws FlashcardValidationException if the front or back is blank, or the deck ID is invalid
     * @throws NullPointerException if the database fails to insert the card
     */
    @Override
    public Flashcard createFlashcard(String front, String back, int deckId) {
        // Validate input
        ValidationResult result = validator.validate(front, back);
        if (!result.isValid()) {
            throw new FlashcardValidationException(result.getErrorMessage());
        }
        if (deckId <= 0) {
            throw new FlashcardValidationException("Deck not found");
        }
        // Create
        Flashcard flashcard = Flashcard.createNew(front, back, deckId);
        Flashcard inserted = flashcardPersistence.insertFlashcard(flashcard);
        if (inserted == null) {
            throw new NullPointerException("Failed to create flashcard");
        }
        return inserted;
    }

    /**
     * Gets a card by its ID.
     * @param flashcardId the card's unique ID
     * @return the card object, or null if not found
     */
    @Override
    public Flashcard getFlashcard(int flashcardId) {
        if (flashcardId <= 0) {
            return null;
        }
        return flashcardPersistence.getFlashcardById(flashcardId);
    }

    /**
     * Updates an existing card's front and back text.
     * @param flashcardId the ID of the card to change
     * @param front the new front text
     * @param back the new back text
     * @return true if updated successfully
     * @throws FlashcardValidationException if the front or back is blank
     * @throws NullPointerException if the card does not exist or update fails
     */
    @Override
    public boolean updateFlashcard(int flashcardId, String front, String back) {
        // Validate input
        ValidationResult result = validator.validate(front, back);
        if (!result.isValid()) {
            throw new FlashcardValidationException(result.getErrorMessage());
        }
        // Get the card
        Flashcard existingFlashcard = flashcardPersistence.getFlashcardById(flashcardId);
        if (existingFlashcard == null) {
            throw new FlashcardValidationException("Flashcard not found");
        }
        // Update
        Flashcard updatedFlashcard = existingFlashcard.withUpdatedContent(front, back);
        if (!flashcardPersistence.updateFlashcard(updatedFlashcard)) {
            throw new NullPointerException("Failed to update flashcard");
        }
        return true;
    }

    /**
     * Deletes a flashcard
     * @param flashcardId the ID of the flashcard to delete
     * @return true if deletion successful
     * @throws FlashcardValidationException if the card is not found
     */
    @Override
    public boolean deleteFlashcard(int flashcardId) {
        if (!flashcardPersistence.flashcardExists(flashcardId)) {
            throw new FlashcardValidationException("Flashcard not found");
        }
        return flashcardPersistence.deleteFlashcard(flashcardId);
    }

    /**
     * Gets all flashcards in a specific deck
     * @param deckId the ID of the deck
     * @return list of flashcards in the deck
     */
    @Override
    public List<Flashcard> getFlashcardsByDeck(int deckId) {
        if (deckId < 0) {
            return null;
        }
        return flashcardPersistence.getFlashcardsByDeckId(deckId);
    }

    /**
     * Gets all flashcards across all decks
     * @return list of all flashcards
     */
    @Override
    public List<Flashcard> getAllFlashcards() {
        return flashcardPersistence.getAllFlashcards();
    }

    private boolean isValidFlashcardId(int flashcardId) {
        return flashcardId > 0;
    }

    /**
     * Searches for cards containing a specific keyword.
     * @param keyword the text to look for
     * @param deckId the ID of the deck to search, or -1 for all decks
     * @return a list of matching cards
     */
    @Override
    public List<Flashcard> searchFlashcards(String keyword, int deckId) {
        if (StringUtils.isNullOrEmpty(keyword)) {
            return null;
        }

        List<Flashcard> flashcards;
        if (deckId < 0) {
            // Search all flashcards
            flashcards = flashcardPersistence.getAllFlashcards();
        } else {
            // Search within specific deck
            flashcards = flashcardPersistence.getFlashcardsByDeckId(deckId);
        }

        if (flashcards == null) {
            return null;
        }

        // Filter flashcards that contain the keyword (case-insensitive)
        String lowerKeyword = keyword.toLowerCase().trim();
        List<Flashcard> results = new ArrayList<>();

        for (Flashcard flashcard : flashcards) {
            String front = flashcard.getFront().toLowerCase();
            String back = flashcard.getBack().toLowerCase();

            if (front.contains(lowerKeyword) || back.contains(lowerKeyword)) {
                results.add(flashcard);
            }
        }

        return results;
    }

    /**
     * Gets the count of flashcards in a specific deck
     * @param deckId the deck ID
     * @return number of flashcards in the deck
     */
    @Override
    public int getFlashcardCount(int deckId) {
        if (deckId < 0) {
            return 0;
        }
        return flashcardPersistence.getFlashcardCountByDeckId(deckId);
    }

    /**
     * Deletes all flashcards in a specific deck
     * @param deckId the deck ID
     * @return number of flashcards deleted
     */
    @Override
    public int deleteFlashcardsByDeck(int deckId) {
        if (deckId < 0) {
            return 0;
        }
        return flashcardPersistence.deleteFlashcardsByDeckId(deckId);
    }

    /**
     * Returns the number of cards marked as known in a deck
     * @param deckId the ID of the deck
     * @return the number of known cards, or -1 if unsuccessful
     */
    @Override
    public int getKnownAmount(int deckId) {
        if (deckId < 0) {
            return -1;
        }

        List<Flashcard> cardList = flashcardPersistence.getFlashcardsByDeckId(deckId);
        if (cardList == null || cardList.isEmpty()) {
            return 0;
        }

        int knownCount = 0;
        for (Flashcard flashcard : cardList) {
            if (flashcard.getIsKnown()) {
                knownCount++;
            }
        }
        return knownCount;
    }

    /**
     * Gets a list of cards filtered by their known status.
     * @param deckId the ID of the deck
     * @param mode the filter type (ALL, KNOWN, UNKNOWN)
     * @return a filtered list of cards
     */
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
