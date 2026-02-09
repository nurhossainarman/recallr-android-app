package comp3350.flashcard.persistence.stubs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import comp3350.flashcard.objects.Deck;
import comp3350.flashcard.persistence.DeckPersistence;

/**
 * Stub implementation of DeckPersistence for development and testing.
 * Data is stored in memory and will be lost when the application terminates.
 * Pre-populated with sample decks for demonstration purposes.
 */
public class DeckPersistenceStub implements DeckPersistence {

    private final List<Deck> decks;
    private final AtomicInteger nextId;

    /**
     * Creates a new stub with pre-populated sample data.
     */
    public DeckPersistenceStub() {
        this.decks = Collections.synchronizedList(new ArrayList<>());
        this.nextId = new AtomicInteger(1);
        initializeSampleData();
    }

    /**
     * Populates the stub with sample decks for testing and demonstration.
     */
    private void initializeSampleData() {
        long now = System.currentTimeMillis();

        // Sample Deck 1: Spanish Vocabulary
        Deck spanishDeck = new Deck(
                nextId.getAndIncrement(),
                "Spanish Vocabulary",
                "Common Spanish words and phrases for beginners",
                now - 86400000, // Created 1 day ago
                now - 3600000   // Last studied 1 hour ago
        );
        decks.add(spanishDeck);

        // Sample Deck 2: Java Basics
        Deck javaDeck = new Deck(
                nextId.getAndIncrement(),
                "Java Basics",
                "Fundamental Java programming concepts",
                now - 172800000, // Created 2 days ago
                now - 7200000    // Last studied 2 hours ago
        );
        decks.add(javaDeck);

        // Sample Deck 3: World Capitals
        Deck capitalsDeck = new Deck(
                nextId.getAndIncrement(),
                "World Capitals",
                "Capital cities of countries around the world",
                now - 259200000, // Created 3 days ago
                0                // Never studied
        );
        decks.add(capitalsDeck);
    }

    @Override
    public List<Deck> getAllDecks() {
        return new ArrayList<>(decks);
    }

    @Override
    public Deck getDeckById(int deckId) {
        for (Deck deck : decks) {
            if (deck.getId() == deckId) {
                return deck;
            }
        }
        return null;
    }

    @Override
    public Deck getDeckByName(String name) {
        if (name == null) {
            return null;
        }
        String normalizedName = name.trim().toLowerCase();
        for (Deck deck : decks) {
            if (deck.getName().toLowerCase().equals(normalizedName)) {
                return deck;
            }
        }
        return null;
    }

    @Override
    public Deck insertDeck(Deck deck) {
        int id = nextId.getAndIncrement();
        Deck newDeck = new Deck(
                id,
                deck.getName(),
                deck.getDescription(),
                System.currentTimeMillis(),
                0 // Never studied yet
        );
        decks.add(newDeck);
        return newDeck;
    }

    @Override
    public boolean updateDeck(Deck deck) {
        for (int i = 0; i < decks.size(); i++) {
            if (decks.get(i).getId() == deck.getId()) {
                Deck existing = decks.get(i);
                Deck updated = new Deck(
                        deck.getId(),
                        deck.getName(),
                        deck.getDescription(),
                        existing.getCreatedAt(),
                        deck.getLastStudiedAt() > 0 ? deck.getLastStudiedAt() : existing.getLastStudiedAt()
                );
                decks.set(i, updated);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteDeck(int deckId) {
        return decks.removeIf(deck -> deck.getId() == deckId);
    }

    @Override
    public int getDeckCount() {
        return decks.size();
    }

    @Override
    public boolean deckExists(int deckId) {
        return getDeckById(deckId) != null;
    }

    @Override
    public boolean deckNameExists(String name, int excludeId) {
        if (name == null) {
            return false;
        }
        String normalizedName = name.trim().toLowerCase();
        for (Deck deck : decks) {
            if (deck.getId() != excludeId && deck.getName().toLowerCase().equals(normalizedName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Clears all decks. Useful for testing.
     */
    public void clearAll() {
        decks.clear();
        nextId.set(1);
    }

    /**
     * Resets to initial sample data. Useful for testing.
     */
    public void reset() {
        clearAll();
        initializeSampleData();
    }
}
