package comp3350.flashcard.logic;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

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

        deckManager = new DeckManager(deckPersistence, flashcardPersistence);
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
    public void createDeck_invalidName_returnsNull() {
        // Test with null name
        assertNull(deckManager.createDeck(null, "Description"));

        // Test with empty name
        assertNull(deckManager.createDeck("", "Description"));

        // Test with whitespace-only name
        assertNull(deckManager.createDeck("   ", "Description"));
    }

    @Test
    public void createDeck_duplicateName_returnsNull() {
        createDeck("Unique Deck", "First deck");

        // Try to create deck with same name
        Deck duplicateDeck = deckManager.createDeck("Unique Deck", "Duplicate deck");
        assertNull("Deck with duplicate name should not be created", duplicateDeck);
    }

    @Test
    public void createDeck_nameTooLong_returnsNull() {
        // Create a name longer than 100 characters
        String longName = "a".repeat(101);
        assertNull(deckManager.createDeck(longName, "Description"));

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
    public void updateDeck_invalidData_fails() {
        Deck deck = createDeck("Valid Deck", "Description");

        assertFalse(deckManager.updateDeck(deck.getId(), null, "Description"));
        assertFalse(deckManager.updateDeck(deck.getId(), "", "Description"));
        assertFalse(deckManager.updateDeck(deck.getId(), "   ", "Description"));

        String tooLong = "a".repeat(101);
        assertFalse(deckManager.updateDeck(deck.getId(), tooLong, "Description"));

        // Verify original deck is unchanged
        Deck retrieved = deckManager.getDeck(deck.getId());
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

    // ---------------- validateDeck ----------------

    @Test
    public void validateDeck_valid_passes() {
        assertTrue(deckManager.validateDeck("Valid Deck"));
        assertTrue(deckManager.validateDeck("Valid Deck Name"));
        assertTrue(deckManager.validateDeck("a".repeat(100))); // Max length
        assertTrue(deckManager.validateDeck("Deck-123!")); // Special chars
    }

    @Test
    public void validateDeck_invalid_fails() {
        assertFalse(deckManager.validateDeck(null));
        assertFalse(deckManager.validateDeck(""));
        assertFalse(deckManager.validateDeck("   "));
        assertFalse(deckManager.validateDeck("a".repeat(101))); // Too long
    }

    // ---------------- validateDeckNameUnique ----------------

    @Test
    public void validateDeckNameUnique_newDeck_checksUniqueness() {
        createDeck("Existing Deck", "Description");

        // Existing name should not be unique for new deck (-1 excludeId)
        assertFalse(deckManager.validateDeckNameUnique("Existing Deck", -1));

        // New name should be unique
        assertTrue(deckManager.validateDeckNameUnique("New Unique Deck", -1));
    }

    @Test
    public void validateDeckNameUnique_updateDeck_allowsSameName() {
        Deck existing = createDeck("Existing Deck", "Description");

        // Same name should be valid when updating same deck
        assertTrue(deckManager.validateDeckNameUnique("Existing Deck", existing.getId()));
    }

    @Test
    public void validateDeckNameUnique_updateDeck_preventsOtherDeckNames() {
        Deck deck1 = createDeck("Existing Deck", "Description");
        createDeck("Another Deck", "Description");

        // Cannot update deck1 to another deck's name
        assertFalse(deckManager.validateDeckNameUnique("Another Deck", deck1.getId()));
    }

    @Test
    public void validateDeckNameUnique_invalidInput_fails() {
        assertFalse(deckManager.validateDeckNameUnique(null, -1));
        assertFalse(deckManager.validateDeckNameUnique("", -1));
        assertFalse(deckManager.validateDeckNameUnique("   ", -1));
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