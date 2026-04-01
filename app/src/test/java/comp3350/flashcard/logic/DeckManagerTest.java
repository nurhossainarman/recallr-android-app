package comp3350.flashcard.logic;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

import comp3350.flashcard.logic.DeckValidationException;
import comp3350.flashcard.logic.validators.DeckValidator;
import comp3350.flashcard.logic.validators.IDeckValidator;
import comp3350.flashcard.objects.Deck;
import comp3350.flashcard.objects.Flashcard;
import comp3350.flashcard.persistence.DeckPersistence;
import comp3350.flashcard.persistence.FlashcardPersistence;
import comp3350.flashcard.persistence.stubs.DeckPersistenceStub;
import comp3350.flashcard.persistence.stubs.FlashcardPersistenceStub;

import java.util.List;

/**
 * DeckManagerTest - unit tests for DeckManager
 */
public class DeckManagerTest {

    private DeckManager deckManager;
    private DeckPersistence deckPersistence;
    private FlashcardPersistence flashcardPersistence;

    @Before
    public void setUp() {
        // Initialize persistence stubs
        deckPersistence = new DeckPersistenceStub();
        flashcardPersistence = new FlashcardPersistenceStub();
        deckPersistence.clearAll();
        flashcardPersistence.clearAll();

        IDeckValidator validator = new DeckValidator(deckPersistence);
        deckManager = new DeckManager(deckPersistence, flashcardPersistence, validator);
    }

    @After
    public void tearDown() {
        // Clean up test data
        deckManager = null;
        deckPersistence = null;
        flashcardPersistence = null;
    }

    // ---------------- Helpers ----------------

    private Deck createDeck(String name, String description) {
        Deck deck = deckManager.createDeck(name, description);
        assertNotNull("Deck should be created successfully", deck);
        return deck;
    }

    private void addFlashcardsToDeck(int deckId, int count) {
        for (int i = 1; i <= count; i++) {
            Flashcard card = new Flashcard("Front " + i, "Back " + i, deckId);
            flashcardPersistence.insertFlashcard(card);
        }
    }

    // ---------------- createDeck ----------------

    @Test
    public void createDeck_valid_createsDeck() {
        Deck newDeck = deckManager.createDeck("Test Deck", "A test deck for testing");

        assertNotNull("Deck should be created", newDeck);
        assertEquals("Deck name should match", "Test Deck", newDeck.getName());
        assertEquals("Deck description should match", "A test deck for testing", newDeck.getDescription());
        assertTrue("Deck ID should be positive", newDeck.getId() > 0);

        // Verify deck exists in persistence
        Deck retrieved = deckManager.getDeck(newDeck.getId());
        assertNotNull("Created deck should be retrievable", retrieved);
        assertEquals("Retrieved deck should match created deck", newDeck.getId(), retrieved.getId());
    }

    @Test
    public void createDeck_invalidName_throwsException() {
        assertThrows(DeckValidationException.class, () -> deckManager.createDeck(null, "Description"));
        assertThrows(DeckValidationException.class, () -> deckManager.createDeck("", "Description"));
        assertThrows(DeckValidationException.class, () -> deckManager.createDeck("   ", "Description"));
    }

    @Test
    public void createDeck_duplicateName_throwsException() {
        createDeck("Unique Deck", "First deck");

        assertThrows(DeckValidationException.class, () -> deckManager.createDeck("Unique Deck", "Duplicate deck"));
    }

    @Test
    public void createDeck_nameTooLong_throwsException() {
        String longName = "a".repeat(101);
        assertThrows(DeckValidationException.class, () -> deckManager.createDeck(longName, "Description"));

        // Test with exactly 100 characters (should be valid)
        String validLongName = "a".repeat(100);
        Deck validDeck = deckManager.createDeck(validLongName, "Description");
        assertNotNull("Deck with name of exactly 100 characters should be created", validDeck);
    }

    // ---------------- getDeck ----------------

    @Test
    public void getDeck_existing_returnsIt() {
        Deck created = createDeck("Get Test Deck", "Test deck for retrieval");

        Deck retrieved = deckManager.getDeck(created.getId());

        assertNotNull(retrieved);
        assertEquals(created.getId(), retrieved.getId());
        assertEquals(created.getName(), retrieved.getName());
        assertEquals(created.getDescription(), retrieved.getDescription());
    }

    @Test
    public void getDeck_notFound_returnsNull() {
        assertNull(deckManager.getDeck(99999));
    }

    // ---------------- getDeckByName ----------------

    @Test
    public void getDeckByName_existing_returnsIt() {
        Deck created = createDeck("Name Search Deck", "Deck for name search");

        Deck retrieved = deckManager.getDeckByName("Name Search Deck");

        assertNotNull(retrieved);
        assertEquals(created.getId(), retrieved.getId());
    }

    @Test
    public void getDeckByName_notFound_returnsNull() {
        assertNull(deckManager.getDeckByName("Non-Existent Deck"));
    }

    @Test
    public void getDeckByName_invalidName_returnsNull() {
        assertNull(deckManager.getDeckByName(null));
        assertNull(deckManager.getDeckByName(""));
        assertNull(deckManager.getDeckByName("   "));
    }

    // ---------------- updateDeck ----------------

    @Test
    public void updateDeck_valid_updatesContent() {
        Deck original = createDeck("Original Name", "Original description");

        assertTrue(deckManager.updateDeck(original.getId(), "Updated Name", "Updated description"));

        Deck retrieved = deckManager.getDeck(original.getId());
        assertNotNull(retrieved);
        assertEquals("Updated Name", retrieved.getName());
        assertEquals("Updated description", retrieved.getDescription());
    }

    @Test
    public void updateDeck_invalidData_throwsException() {
        Deck deck = createDeck("Valid Deck", "Description");
        int id = deck.getId();

        assertThrows(DeckValidationException.class, () -> deckManager.updateDeck(id, null, "Description"));
        assertThrows(DeckValidationException.class, () -> deckManager.updateDeck(id, "", "Description"));
        assertThrows(DeckValidationException.class, () -> deckManager.updateDeck(id, "   ", "Description"));
        assertThrows(DeckValidationException.class, () -> deckManager.updateDeck(id, "a".repeat(101), "Description"));

        // Verify original deck is unchanged
        Deck retrieved = deckManager.getDeck(id);
        assertEquals("Valid Deck", retrieved.getName());
    }

    @Test
    public void updateDeck_notFound_fails() {
        assertFalse(deckManager.updateDeck(99999, "New Name", "New Description"));
    }

    // ---------------- deleteDeck ----------------

    @Test
    public void deleteDeck_existing_deletesIt() {
        Deck deck = createDeck("To Delete", "This deck will be deleted");
        int deckId = deck.getId();

        assertTrue(deckManager.deckExists(deckId));
        assertTrue(deckManager.deleteDeck(deckId));
        assertFalse(deckManager.deckExists(deckId));
        assertNull(deckManager.getDeck(deckId));
    }

    @Test
    public void deleteDeck_withFlashcards_deletesAllCards() {
        Deck deck = createDeck("Deck with Cards", "Has flashcards");
        int deckId = deck.getId();

        addFlashcardsToDeck(deckId, 3);

        assertEquals(3, deckManager.getFlashcardCount(deckId));

        assertTrue(deckManager.deleteDeck(deckId));
        assertFalse(deckManager.deckExists(deckId));
        assertEquals(0, flashcardPersistence.getFlashcardCountByDeckId(deckId));
    }

    @Test
    public void deleteDeck_notFound_fails() {
        assertFalse(deckManager.deleteDeck(99999));
    }

    // ---------------- getAllDecks ----------------

    @Test
    public void getAllDecks_returnsAll() {
        int initialCount = deckManager.getAllDecks().size();

        createDeck("Deck 1", "First deck");
        createDeck("Deck 2", "Second deck");
        createDeck("Deck 3", "Third deck");

        List<Deck> allDecks = deckManager.getAllDecks();

        assertNotNull(allDecks);
        assertEquals(initialCount + 3, allDecks.size());
    }

    @Test
    public void getAllDecks_emptyPersistence_returnsEmptyList() {
        // Clear all decks (using stub's reset method if available)
        if (deckPersistence instanceof DeckPersistenceStub) {
            deckPersistence.clearAll();
        }

        List<Deck> allDecks = deckManager.getAllDecks();

        assertNotNull(allDecks);
        assertEquals(0, allDecks.size());
    }

    // ---------------- getFlashcardCount ----------------

    @Test
    public void getFlashcardCount_countsPerDeck() {
        Deck deck = createDeck("Card Count Test", "Test deck");
        int deckId = deck.getId();

        assertEquals(0, deckManager.getFlashcardCount(deckId));

        addFlashcardsToDeck(deckId, 3);

        assertEquals(3, deckManager.getFlashcardCount(deckId));
    }

    @Test
    public void getFlashcardCount_emptyDeck_returnsZero() {
        Deck deck = createDeck("Empty Deck", "No cards");

        assertEquals(0, deckManager.getFlashcardCount(deck.getId()));
    }

    @Test
    public void getFlashcardCount_nonExistentDeck_returnsZero() {
        assertEquals(0, deckManager.getFlashcardCount(99999));
    }

    // ---------------- deckExists ----------------

    @Test
    public void deckExists_existing_returnsTrue() {
        Deck deck = createDeck("Exists Test", "Testing existence");

        assertTrue(deckManager.deckExists(deck.getId()));
    }

    @Test
    public void deckExists_nonExistent_returnsFalse() {
        assertFalse(deckManager.deckExists(99999));
        assertFalse(deckManager.deckExists(-1));
        assertFalse(deckManager.deckExists(0));
    }

    // ---------------- getDeckCount ----------------

    @Test
    public void getDeckCount_tracksCreateAndDelete() {
        int initialCount = deckManager.getDeckCount();

        createDeck("Count Test 1", "First");
        assertEquals(initialCount + 1, deckManager.getDeckCount());

        createDeck("Count Test 2", "Second");
        assertEquals(initialCount + 2, deckManager.getDeckCount());

        Deck toDelete = deckManager.getDeckByName("Count Test 1");
        deckManager.deleteDeck(toDelete.getId());
        assertEquals(initialCount + 1, deckManager.getDeckCount());
    }

    // ---------------- markDeckAsStudied ----------------

    @Test
    public void markDeckAsStudied_updatesTimestamp() {
        Deck deck = createDeck("Study Test", "Test studying");
        int deckId = deck.getId();

        Deck original = deckManager.getDeck(deckId);
        long originalTime = original.getLastStudiedAt();
        assertEquals(0, originalTime); // New deck never studied

        // Wait to ensure timestamp difference
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            // Ignore
        }

        assertTrue(deckManager.markDeckAsStudied(deckId));

        Deck updated = deckManager.getDeck(deckId);
        long updatedTime = updated.getLastStudiedAt();
        assertTrue(updatedTime > originalTime);
        assertTrue(updatedTime > 0);
    }

    @Test
    public void markDeckAsStudied_nonExistent_fails() {
        assertFalse(deckManager.markDeckAsStudied(99999));
    }
}
