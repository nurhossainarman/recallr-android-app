package comp3350.flashcard.logic;

import java.util.List;
import comp3350.flashcard.objects.Deck;

/**
 * Interface for managing deck operations.
 * Defines the contract for deck business logic including CRUD operations and validation.
 * Implementations should handle deck validation and persistence through dependency injection.
 */
public interface IDeckManager {

    /**
     * Creates a new deck with the given name and description.
     *
     * @param name the name of the deck
     * @param description optional description of the deck
     * @return the created deck, or null if creation failed
     * @throws DeckValidationException if the deck data is invalid
     */
    Deck createDeck(String name, String description);

    /**
     * Retrieves a deck by its ID.
     *
     * @param deckId the ID of the deck to retrieve
     * @return the deck object, or null if not found
     */
    Deck getDeck(int deckId);

    /**
     * Retrieves a deck by its name.
     *
     * @param name the name of the deck to retrieve
     * @return the deck object, or null if not found
     */
    Deck getDeckByName(String name);

    /**
     * Updates an existing deck with new name and description.
     *
     * @param deckId the ID of the deck to update
     * @param name the new name
     * @param description the new description
     * @return true if update successful, false otherwise
     * @throws DeckValidationException if the deck data is invalid
     */
    boolean updateDeck(int deckId, String name, String description);

    /**
     * Deletes a deck and all its flashcards.
     *
     * @param deckId the ID of the deck to delete
     * @return true if deletion successful, false otherwise
     */
    boolean deleteDeck(int deckId);

    /**
     * Gets all decks.
     *
     * @return list of all decks
     */
    List<Deck> getAllDecks();

    /**
     * Gets the count of flashcards in a deck.
     *
     * @param deckId the ID of the deck
     * @return number of flashcards in the deck
     */
    int getFlashcardCount(int deckId);

    /**
     * Checks if a deck exists.
     *
     * @param deckId the ID of the deck
     * @return true if deck exists, false otherwise
     */
    boolean deckExists(int deckId);

    /**
     * Gets the total number of decks.
     *
     * @return total deck count
     */
    int getDeckCount();

    /**
     * Updates the last studied timestamp for a deck.
     *
     * @param deckId the ID of the deck
     * @return true if update successful, false otherwise
     */
    boolean markDeckAsStudied(int deckId);
}
