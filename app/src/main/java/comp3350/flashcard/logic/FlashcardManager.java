package comp3350.flashcard.logic;

import java.util.ArrayList;
import java.util.List;

import comp3350.flashcard.objects.Flashcard;
import comp3350.flashcard.persistence.FlashcardPersistence;
import comp3350.flashcard.utils.StringUtils;

/**
 * Handles operations related to individual flashcards.
 * Manages creating, retrieving, updating, and searching cards.
 */
public class FlashcardManager implements IFlashcardManager {

    private final FlashcardPersistence flashcardPersistence;

    /**
     * Creates a new flashcard manager.
     * @param flashcardPersistence the storage for flashcards
     */
    public FlashcardManager(FlashcardPersistence flashcardPersistence) {
        this.flashcardPersistence = flashcardPersistence;
    }

    /**
     * Creates a new flashcard and saves it.
     * @param front the text on the front side
     * @param back the text on the back side
     * @param deckId the ID of the deck it belongs to
     * @return the new card, or null if it couldn't be created
     */
    public Flashcard createFlashcard(String front, String back, int deckId) {
        if (!validateFlashcard(front, back)) {
            return null;
        }

        if (deckId <= 0) {
            return null;
        }

        Flashcard flashcard = new Flashcard(front, back, deckId);
        return flashcardPersistence.insertFlashcard(flashcard);
    }

    /**
     * Gets a card by its ID.
     * @param flashcardId the card's unique ID
     * @return the card object, or null if not found
     */
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
     */
    public boolean updateFlashcard(int flashcardId, String front, String back) {
        if (!validateFlashcard(front, back)) {
            return false;
        }

        Flashcard existingFlashcard = flashcardPersistence.getFlashcardById(flashcardId);
        if (existingFlashcard == null) {
            return false;
        }

        Flashcard updatedFlashcard = existingFlashcard.withUpdatedContent(front, back);
        return flashcardPersistence.updateFlashcard(updatedFlashcard);
    }

    /**
     * Deletes a flashcard
     * @param flashcardId the ID of the flashcard to delete
     * @return true if deletion successful, false otherwise
     */
    public boolean deleteFlashcard(int flashcardId) {
        if (!isValidFlashcardId(flashcardId)) {
            return false;
        }
        return flashcardPersistence.deleteFlashcard(flashcardId);
    }

    /**
     * Gets all flashcards in a specific deck
     * @param deckId the ID of the deck
     * @return list of flashcards in the deck
     */
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
    public List<Flashcard> getAllFlashcards() {
        return flashcardPersistence.getAllFlashcards();
    }

    /**
     * Validates flashcard data
     * @param front the front side text
     * @param back the back side text
     * @return true if valid, false otherwise
     */
    public boolean validateFlashcard(String front, String back) {
        if (StringUtils.isNullOrEmpty(front)) {
            return false;
        }
        if (StringUtils.isNullOrEmpty(back)) {
            return false;
        }
        return true;
    }

    private boolean isValidFlashcardId(int flashcardId) {
        return flashcardId > 0;
    }

    private boolean isValidDeckId(int deckId) {
        return deckId >= 0;
    }

    /**
     * Searches for cards containing a specific keyword.
     * @param keyword the text to look for
     * @param deckId the ID of the deck to search, or -1 for all decks
     * @return a list of matching cards
     */
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
    public int getFlashcardCount(int deckId) {
        if (!isValidDeckId(deckId)) {
            return 0;
        }
        return flashcardPersistence.getFlashcardCountByDeckId(deckId);
    }

    /**
     * Deletes all flashcards in a specific deck
     * @param deckId the deck ID
     * @return number of flashcards deleted
     */
    public int deleteFlashcardsByDeck(int deckId) {
        if (!isValidDeckId(deckId)) {
            return 0;
        }
        return flashcardPersistence.deleteFlashcardsByDeckId(deckId);
    }

    /**
     * Returns the number of cards marked as known in a deck
     * @param deckId the ID of the deck
     * @return the number of known cards, or -1 if unsuccessful
     */
    public int getKnownAmount(int deckId) {
        if (!isValidDeckId(deckId)) {
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
    public List<Flashcard> getFlashcardsByMode(int deckId, FilterMode mode) {
        List<Flashcard> allCards = getFlashcardsByDeck(deckId);
        if (allCards == null) {
            return null;
        }

        if (mode == FilterMode.ALL) {
            return allCards;
        }

        List<Flashcard> results = new ArrayList<>();
        if (!isValidDeckId(deckId)) {
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