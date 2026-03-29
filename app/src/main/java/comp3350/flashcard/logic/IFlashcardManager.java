package comp3350.flashcard.logic;

import java.util.List;
import comp3350.flashcard.objects.Flashcard;

/**
 * Interface for managing flashcard operations.
 * Defines the contract for flashcard business logic including CRUD operations, validation, and filtering.
 * Implementations should handle flashcard validation and persistence through dependency injection.
 */
public interface IFlashcardManager {

    /**
     * Creates a new flashcard and saves it.
     *
     * @param front the text on the front side
     * @param back the text on the back side
     * @param deckId the ID of the deck it belongs to
     * @return the new card, or null if it couldn't be created
     */
    Flashcard createFlashcard(String front, String back, int deckId);

    /**
     * Gets a flashcard by its ID.
     *
     * @param flashcardId the card's unique ID
     * @return the card object, or null if not found
     */
    Flashcard getFlashcard(int flashcardId);

    /**
     * Updates an existing flashcard's front and back text.
     *
     * @param flashcardId the ID of the card to change
     * @param front the new front text
     * @param back the new back text
     * @return true if updated successfully
     */
    boolean updateFlashcard(int flashcardId, String front, String back);

    /**
     * Deletes a flashcard.
     *
     * @param flashcardId the ID of the flashcard to delete
     * @return true if deletion successful, false otherwise
     */
    boolean deleteFlashcard(int flashcardId);

    /**
     * Gets all flashcards in a specific deck.
     *
     * @param deckId the ID of the deck
     * @return list of flashcards in the deck
     */
    List<Flashcard> getFlashcardsByDeck(int deckId);

    /**
     * Gets all flashcards across all decks.
     *
     * @return list of all flashcards
     */
    List<Flashcard> getAllFlashcards();

    /**
     * Validates flashcard data.
     *
     * @param front the front side text
     * @param back the back side text
     * @return true if valid, false otherwise
     */
    boolean validateFlashcard(String front, String back);

    /**
     * Searches for cards containing a specific keyword.
     *
     * @param keyword the text to look for
     * @param deckId the ID of the deck to search, or -1 for all decks
     * @return a list of matching cards
     */
    List<Flashcard> searchFlashcards(String keyword, int deckId);

    /**
     * Gets the count of flashcards in a specific deck.
     *
     * @param deckId the deck ID
     * @return number of flashcards in the deck
     */
    int getFlashcardCount(int deckId);

    /**
     * Deletes all flashcards in a specific deck.
     *
     * @param deckId the deck ID
     * @return number of flashcards deleted
     */
    int deleteFlashcardsByDeck(int deckId);

    /**
     * Returns the number of cards marked as known in a deck.
     *
     * @param deckId the ID of the deck
     * @return the number of known cards, or -1 if unsuccessful
     */
    int getKnownAmount(int deckId);

    /**
     * Gets a list of cards filtered by their known status.
     *
     * @param deckId the ID of the deck
     * @param mode the filter type (ALL, KNOWN, UNKNOWN)
     * @return a filtered list of cards
     */
    List<Flashcard> getFlashcardsByMode(int deckId, FilterMode mode);
}
