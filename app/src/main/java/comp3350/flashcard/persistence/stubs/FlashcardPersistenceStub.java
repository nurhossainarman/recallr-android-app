package comp3350.flashcard.persistence.stubs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import comp3350.flashcard.objects.Flashcard;
import comp3350.flashcard.persistence.FlashcardPersistence;

/**
 * Stub implementation of FlashcardPersistence for development and testing.
 * Data is stored in memory and will be lost when the application terminates.
 * Pre-populated with sample flashcards for demonstration purposes.
 */
public class FlashcardPersistenceStub implements FlashcardPersistence {

    private final List<Flashcard> flashcards;
    private final AtomicInteger nextId;

    /**
     * Creates a new stub with pre-populated sample data.
     */
    public FlashcardPersistenceStub() {
        this.flashcards = Collections.synchronizedList(new ArrayList<>());
        this.nextId = new AtomicInteger(1);
        initializeSampleData();
    }

    /**
     * Populates the stub with sample flashcards for testing and demonstration.
     * Creates cards for the sample decks (IDs 1, 2, 3).
     */
    private void initializeSampleData() {
        long now = System.currentTimeMillis();

        // Spanish Vocabulary Deck (ID: 1)
        addSampleFlashcard("Hola", "Hello", 1, now);
        addSampleFlashcard("Adiós", "Goodbye", 1, now);
        addSampleFlashcard("Por favor", "Please", 1, now);
        addSampleFlashcard("Gracias", "Thank you", 1, now);
        addSampleFlashcard("Buenos días", "Good morning", 1, now);

        // Java Basics Deck (ID: 2)
        addSampleFlashcard("What is a class in Java?", 
                "A class is a blueprint for creating objects that defines attributes and behaviors.", 
                2, now);
        addSampleFlashcard("What is inheritance?", 
                "Inheritance is a mechanism where a new class inherits properties and behaviors from an existing class.", 
                2, now);
        addSampleFlashcard("What is the difference between == and .equals()?", 
                "== compares object references, while .equals() compares the actual content of objects.", 
                2, now);
        addSampleFlashcard("What is a constructor?", 
                "A constructor is a special method that initializes a new object when it is created.", 
                2, now);

        // World Capitals Deck (ID: 3)
        addSampleFlashcard("Canada", "Ottawa", 3, now);
        addSampleFlashcard("France", "Paris", 3, now);
        addSampleFlashcard("Japan", "Tokyo", 3, now);
        addSampleFlashcard("Australia", "Canberra", 3, now);
        addSampleFlashcard("Brazil", "Brasília", 3, now);
        addSampleFlashcard("Egypt", "Cairo", 3, now);
    }

    /**
     * Helper method to add a sample flashcard during initialization.
     */
    private void addSampleFlashcard(String front, String back, int deckId, long createdAt) {
        int id = nextId.getAndIncrement();
        Flashcard card = new Flashcard(id, front, back, deckId, createdAt);
        flashcards.add(card);
    }

    @Override
    public List<Flashcard> getAllFlashcards() {
        return new ArrayList<>(flashcards);
    }

    @Override
    public List<Flashcard> getFlashcardsByDeckId(int deckId) {
        List<Flashcard> result = new ArrayList<>();
        for (Flashcard card : flashcards) {
            if (card.getDeckId() == deckId) {
                result.add(card);
            }
        }
        return result;
    }

    @Override
    public Flashcard getFlashcardById(int flashcardId) {
        for (Flashcard card : flashcards) {
            if (card.getId() == flashcardId) {
                return card;
            }
        }
        return null;
    }

    @Override
    public Flashcard insertFlashcard(Flashcard flashcard) {
        int id = nextId.getAndIncrement();
        Flashcard newCard = new Flashcard(
                id,
                flashcard.getFront(),
                flashcard.getBack(),
                flashcard.getDeckId(),
                System.currentTimeMillis()
        );
        flashcards.add(newCard);
        return newCard;
    }

    @Override
    public boolean updateFlashcard(Flashcard flashcard) {
        for (int i = 0; i < flashcards.size(); i++) {
            if (flashcards.get(i).getId() == flashcard.getId()) {
                // Create updated flashcard preserving original creation time
                Flashcard existing = flashcards.get(i);
                Flashcard updated = new Flashcard(
                        flashcard.getId(),
                        flashcard.getFront(),
                        flashcard.getBack(),
                        flashcard.getDeckId(),
                        existing.getCreatedAt()
                );
                flashcards.set(i, updated);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteFlashcard(int flashcardId) {
        return flashcards.removeIf(card -> card.getId() == flashcardId);
    }

    @Override
    public int deleteFlashcardsByDeckId(int deckId) {
        int countBefore = flashcards.size();
        flashcards.removeIf(card -> card.getDeckId() == deckId);
        return countBefore - flashcards.size();
    }

    @Override
    public int getFlashcardCountByDeckId(int deckId) {
        int count = 0;
        for (Flashcard card : flashcards) {
            if (card.getDeckId() == deckId) {
                count++;
            }
        }
        return count;
    }

    @Override
    public boolean flashcardExists(int flashcardId) {
        return getFlashcardById(flashcardId) != null;
    }

    /**
     * Clears all flashcards. Useful for testing.
     */
    public void clearAll() {
        flashcards.clear();
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
