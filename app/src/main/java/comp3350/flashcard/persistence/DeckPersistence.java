package comp3350.flashcard.persistence;

import java.util.List;

import comp3350.flashcard.objects.Deck;

/**
 * Interface for deck data persistence operations.
 * Implementations may use stub data, SQLite, or other storage mechanisms.
 */
public interface DeckPersistence {

    /**
     * Retrieves all decks from storage.
     *
     * @return A list of all decks, empty list if none exist
     */
    List<Deck> getAllDecks();

    /**
     * Retrieves a single deck by its ID.
     *
     * @param deckId The unique identifier of the deck
     * @return The deck if found, null otherwise
     */
    Deck getDeckById(int deckId);

    /**
     * Retrieves a deck by its name.
     * Deck names should be unique.
     *
     * @param name The name of the deck
     * @return The deck if found, null otherwise
     */
    Deck getDeckByName(String name);

    /**
     * Inserts a new deck into storage.
     * The deck's ID will be set by the persistence layer.
     *
     * @param deck The deck to insert (ID field will be ignored/overwritten)
     * @return The inserted deck with its assigned ID
     */
    Deck insertDeck(Deck deck);

    /**
     * Updates an existing deck in storage.
     *
     * @param deck The deck with updated values
     * @return true if the update was successful, false if the deck was not found
     */
    boolean updateDeck(Deck deck);

    /**
     * Deletes a deck from storage.
     * Note: This should NOT delete associated flashcards - that's handled by the logic layer.
     *
     * @param deckId The ID of the deck to delete
     * @return true if the deletion was successful, false if the deck was not found
     */
    boolean deleteDeck(int deckId);

    /**
     * Returns the total count of decks.
     *
     * @return The number of decks in storage
     */
    int getDeckCount();

    /**
     * Checks if a deck with the given ID exists.
     *
     * @param deckId The ID to check
     * @return true if a deck with this ID exists
     */
    boolean deckExists(int deckId);

    /**
     * Checks if a deck with the given name already exists.
     * Used for validation before creating or renaming decks.
     *
     * @param name   The name to check
     * @param excludeId The deck ID to exclude from the check (for rename operations), use -1 to include all
     * @return true if a deck with this name exists (excluding the specified ID)
     */
    boolean deckNameExists(String name, int excludeId);
}
