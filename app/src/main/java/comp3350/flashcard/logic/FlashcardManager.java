package comp3350.flashcard.logic;

import java.util.List;
import java.util.ArrayList;
import comp3350.flashcard.objects.Flashcard;
import comp3350.flashcard.persistence.FlashcardPersistence;

/**
 * FlashcardManager - handles business logic for individual flashcard operations
 */
public class FlashcardManager {

    private final FlashcardPersistence flashcardPersistence;

    /**
     * Constructor with dependency injection
     * @param flashcardPersistence the persistence layer for flashcards
     */
    public FlashcardManager(FlashcardPersistence flashcardPersistence) {
        this.flashcardPersistence = flashcardPersistence;
    }

    /**
     * Creates a new flashcard
     * @param front the front/question side of the card
     * @param back the back/answer side of the card
     * @param deckId the ID of the deck this card belongs to
     * @return the created flashcard, or null if creation failed
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
     * Retrieves a flashcard by ID
     * @param flashcardId the ID of the flashcard to retrieve
     * @return the flashcard object, or null if not found
     */
    public Flashcard getFlashcard(int flashcardId) {
        if (flashcardId <= 0) {
            return null;
        }
        return flashcardPersistence.getFlashcardById(flashcardId);
    }

    /**
     * Updates an existing flashcard
     * @param flashcardId the ID of the flashcard to update
     * @param front the new front/question text
     * @param back the new back/answer text
     * @return true if update successful, false otherwise
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
        if (flashcardId <= 0) {
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
        if (front == null || front.trim().isEmpty()) {
            return false;
        }
        if (back == null || back.trim().isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * Searches flashcards by keyword in front or back
     * @param keyword the search term
     * @param deckId the deck to search in (or -1 for all decks)
     * @return list of matching flashcards
     */
    public List<Flashcard> searchFlashcards(String keyword, int deckId) {
        if (keyword == null || keyword.trim().isEmpty()) {
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
    public int getKnownAmount(int deckId) {
        if (deckId < 0) {
            return -1;
        }

        List<Flashcard> cardList = flashcardPersistence.getFlashcardsByDeckId(deckId);
        if (cardList == null) {
            return -1;
        }

        if (cardList.isEmpty()) {
            return -1;
        }

        int knownCount = 0;
        for(Flashcard flashcard : cardList) {
            if(flashcard.getIsKnown()) {
                knownCount++;
            }
        }
        return knownCount;
    }

    /**
     * Returns a list of cards marked known or unknown, depending on the markedAsKnown parameter
     * @param deckId the ID of the deck
     * @param markedAsKnown if true, filters by known, if false, filters by unknown
     * @return the list of cards marked as known or unknown, or an empty list if unsuccessful
     */
    public List <Flashcard> filterByIsKnown(int deckId, boolean markedAsKnown) {
        List<Flashcard> results = new ArrayList<>();
        if (deckId < 0) {
            return results;
        }

        List<Flashcard> cardList = getFlashcardsByDeck(deckId);
        if (cardList == null) {
            return results;
        }

        for(int i = 0; i < cardList.size(); i++) {
            if((cardList.get(i)).getIsKnown() == markedAsKnown) {
                results.add(cardList.get(i));
            }
        }
        return results;
    }
}