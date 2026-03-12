package comp3350.flashcard.logic;

import java.util.ArrayList;
import java.util.List;

import comp3350.flashcard.objects.Flashcard;
import comp3350.flashcard.persistence.FlashcardPersistence;

/**
 * Handles operations related to individual flashcards.
 * Manages creating, retrieving, updating, and searching cards.
 */
public class FlashcardManager {

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
     * Deletes a card from the system.
     * @param flashcardId the ID of the card to remove
     * @return true if deleted successfully
     */
    public boolean deleteFlashcard(int flashcardId) {
        if (flashcardId <= 0) {
            return false;
        }
        return flashcardPersistence.deleteFlashcard(flashcardId);
    }

    /**
     * Gets all cards in a specific deck.
     * @param deckId the deck's ID
     * @return a list of cards
     */
    public List<Flashcard> getFlashcardsByDeck(int deckId) {
        if (deckId < 0) {
            return null;
        }
        return flashcardPersistence.getFlashcardsByDeckId(deckId);
    }

    /**
     * Gets every flashcard across all decks.
     * @return a list of all cards
     */
    public List<Flashcard> getAllFlashcards() {
        return flashcardPersistence.getAllFlashcards();
    }

    /**
     * Checks if card text is valid (not empty).
     * @param front the front text
     * @param back the back text
     * @return true if valid
     */
    public boolean validateFlashcard(String front, String back) {
        if (front == null || front.trim().isEmpty()) {
            return false;
        }
        if (back == null || back.trim().isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * Searches for cards containing a specific keyword.
     * @param keyword the text to look for
     * @param deckId the ID of the deck to search, or -1 for all decks
     * @return a list of matching cards
     */
    public List<Flashcard> searchFlashcards(String keyword, int deckId) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return null;
        }

        List<Flashcard> flashcards;
        if (deckId < 0) {
            flashcards = flashcardPersistence.getAllFlashcards();
        } else {
            flashcards = flashcardPersistence.getFlashcardsByDeckId(deckId);
        }

        if (flashcards == null) {
            return null;
        }

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
     * Counts how many cards are in a deck.
     * @param deckId the ID of the deck
     * @return the number of cards
     */
    public int getFlashcardCount(int deckId) {
        if (deckId < 0) {
            return 0;
        }
        return flashcardPersistence.getFlashcardCountByDeckId(deckId);
    }

    /**
     * Deletes every card inside a deck.
     * @param deckId the ID of the deck
     * @return the number of cards deleted
     */
    public int deleteFlashcardsByDeck(int deckId) {
        if (deckId < 0) {
            return 0;
        }
        return flashcardPersistence.deleteFlashcardsByDeckId(deckId);
    }

    /**
     * Counts how many cards are marked as known in a deck.
     * @param deckId the ID of the deck
     * @return the count of known cards
     */
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
    public List<Flashcard> getFlashcardsByMode(int deckId, FilterMode mode) {
        List<Flashcard> allCards = getFlashcardsByDeck(deckId);
        if (allCards == null) {
            return null;
        }

        if (mode == FilterMode.ALL) {
            return allCards;
        }

        List<Flashcard> results = new ArrayList<>();
        boolean wantKnown = (mode == FilterMode.KNOWN);

        for (Flashcard card : allCards) {
            if (card.getIsKnown() == wantKnown) {
                results.add(card);
            }
        }
        return results;
    }
}
