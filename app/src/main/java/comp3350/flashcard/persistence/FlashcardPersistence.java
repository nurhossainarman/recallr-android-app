package comp3350.flashcard.persistence;

import java.util.List;

import comp3350.flashcard.objects.Flashcard;

/**
 * Interface for flashcard data persistence operations.
 * Implementations may use stub data, SQLite, or other storage mechanisms.
 */
public interface FlashcardPersistence {

    /**
     * Retrieves all flashcards from storage.
     *
     * @return A list of all flashcards, empty list if none exist
     */
    List<Flashcard> getAllFlashcards();

    /**
     * Retrieves all flashcards belonging to a specific deck.
     *
     * @param deckId The ID of the deck
     * @return A list of flashcards in the specified deck, empty list if none exist
     */
    List<Flashcard> getFlashcardsByDeckId(int deckId);

    /**
     * Retrieves a single flashcard by its ID.
     *
     * @param flashcardId The unique identifier of the flashcard
     * @return The flashcard if found, null otherwise
     */
    Flashcard getFlashcardById(int flashcardId);

    /**
     * Inserts a new flashcard into storage.
     * The flashcard's ID will be set by the persistence layer.
     *
     * @param flashcard The flashcard to insert (ID field will be ignored/overwritten)
     * @return The inserted flashcard with its assigned ID
     */
    Flashcard insertFlashcard(Flashcard flashcard);

    /**
     * Updates an existing flashcard in storage.
     *
     * @param flashcard The flashcard with updated values
     * @return true if the update was successful, false if the flashcard was not found
     */
    boolean updateFlashcard(Flashcard flashcard);

    /**
     * Deletes a flashcard from storage.
     *
     * @param flashcardId The ID of the flashcard to delete
     * @return true if the deletion was successful, false if the flashcard was not found
     */
    boolean deleteFlashcard(int flashcardId);

    /**
     * Deletes all flashcards belonging to a specific deck.
     * Used when deleting a deck to maintain referential integrity.
     *
     * @param deckId The ID of the deck whose flashcards should be deleted
     * @return The number of flashcards deleted
     */
    int deleteFlashcardsByDeckId(int deckId);

    /**
     * Returns the count of flashcards in a specific deck.
     *
     * @param deckId The ID of the deck
     * @return The number of flashcards in the deck
     */
    int getFlashcardCountByDeckId(int deckId);

    /**
     * Checks if a flashcard with the given ID exists.
     *
     * @param flashcardId The ID to check
     * @return true if a flashcard with this ID exists
     */
    boolean flashcardExists(int flashcardId);
}
