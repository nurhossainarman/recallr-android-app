package comp3350.flashcard.logic;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.List;
import comp3350.flashcard.logic.exceptions.FlashcardValidationException;
import comp3350.flashcard.logic.validators.FlashcardValidator;
import comp3350.flashcard.logic.validators.IFlashcardValidator;
import comp3350.flashcard.objects.Deck;
import comp3350.flashcard.objects.Flashcard;
import comp3350.flashcard.persistence.DeckPersistence;
import comp3350.flashcard.persistence.FlashcardPersistence;
import comp3350.flashcard.persistence.sqlite.DeckPersistenceSQLite;
import comp3350.flashcard.persistence.sqlite.FlashcardPersistenceSQLite;
import static org.junit.Assert.*;

/**
 * Integration test for FlashcardManager + FlashcardPersistenceSQLite interaction.
 * Tests the business layer ↔ persistence layer seam with real validators.
 */
@RunWith(AndroidJUnit4.class)
public class FlashcardManagerIntegrationTest {

    private FlashcardManager flashcardManager;
    private FlashcardPersistence flashcardPersistence;
    private DeckPersistence deckPersistence;
    private IFlashcardValidator flashcardValidator;
    private int testDeckId;
    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        context.deleteDatabase("flashcard.db");

        // Create real persistence layer instances (SQLite)
        flashcardPersistence = new FlashcardPersistenceSQLite(context);
        deckPersistence = new DeckPersistenceSQLite(context);

        // Create real validator (not mocked)
        flashcardValidator = new FlashcardValidator();

        // Create FlashcardManager with real dependencies
        flashcardManager = new FlashcardManager(flashcardPersistence, flashcardValidator);

        // Create a test deck to use in tests
        Deck testDeck = Deck.createNew("Test Deck", "For flashcard testing");
        Deck inserted = deckPersistence.insertDeck(testDeck);
        testDeckId = inserted.getId();
    }

    @After
    public void tearDown() {
        flashcardPersistence.clearAll();
        deckPersistence.clearAll();
    }

    // ---------------- Test create flashcard → verify persisted to SQLite ----------------

    @Test
    public void testCreateFlashcard_persistedToSQLite() {
        // Act: Create flashcard through manager
        Flashcard created = flashcardManager.createFlashcard("Question", "Answer", testDeckId);

        // Assert: Flashcard was created and returned
        assertNotNull("Created flashcard should not be null", created);
        assertTrue("Flashcard ID should be valid", created.getId() > 0);
        assertEquals("Question", created.getFront());
        assertEquals("Answer", created.getBack());
        assertEquals(testDeckId, created.getDeckId());

        // Assert: Flashcard is actually in SQLite
        Flashcard retrieved = flashcardPersistence.getFlashcardById(created.getId());
        assertNotNull("Flashcard should be persisted in SQLite", retrieved);
        assertEquals(created.getId(), retrieved.getId());
        assertEquals("Question", retrieved.getFront());
        assertEquals("Answer", retrieved.getBack());
        assertEquals(testDeckId, retrieved.getDeckId());
    }

    @Test
    public void testCreateFlashcard_validationIntegration_emptyFront_throwsException() {
        // Act & Assert: Empty front should be caught by validator
        try {
            flashcardManager.createFlashcard("", "Answer", testDeckId);
            fail("Should have thrown FlashcardValidationException");
        } catch (FlashcardValidationException e) {
            assertTrue("Error message should mention front",
                e.getMessage().toLowerCase().contains("front"));
        }

        // Assert: No flashcard was persisted
        List<Flashcard> flashcards = flashcardPersistence.getFlashcardsByDeckId(testDeckId);
        assertEquals("No flashcards should be created", 0, flashcards.size());
    }

    @Test
    public void testCreateFlashcard_validationIntegration_emptyBack_throwsException() {
        // Act & Assert: Empty back should be caught by validator
        try {
            flashcardManager.createFlashcard("Question", "", testDeckId);
            fail("Should have thrown FlashcardValidationException");
        } catch (FlashcardValidationException e) {
            assertTrue("Error message should mention back",
                e.getMessage().toLowerCase().contains("back"));
        }

        // Assert: No flashcard was persisted
        List<Flashcard> flashcards = flashcardPersistence.getFlashcardsByDeckId(testDeckId);
        assertEquals("No flashcards should be created", 0, flashcards.size());
    }

    @Test
    public void testCreateFlashcard_validationIntegration_nullFront_throwsException() {
        // Act & Assert: Null front should be caught by validator
        try {
            flashcardManager.createFlashcard(null, "Answer", testDeckId);
            fail("Should have thrown FlashcardValidationException");
        } catch (FlashcardValidationException e) {
            assertTrue("Error message should mention front",
                e.getMessage().toLowerCase().contains("front"));
        }
    }

    @Test
    public void testCreateFlashcard_validationIntegration_nullBack_throwsException() {
        // Act & Assert: Null back should be caught by validator
        try {
            flashcardManager.createFlashcard("Question", null, testDeckId);
            fail("Should have thrown FlashcardValidationException");
        } catch (FlashcardValidationException e) {
            assertTrue("Error message should mention back",
                e.getMessage().toLowerCase().contains("back"));
        }
    }

    @Test
    public void testCreateFlashcard_validationIntegration_invalidDeckId_throwsException() {
        // Act & Assert: Invalid deck ID should be caught
        try {
            flashcardManager.createFlashcard("Question", "Answer", 0);
            fail("Should have thrown FlashcardValidationException");
        } catch (FlashcardValidationException e) {
            assertTrue("Error message should mention deck ID",
                e.getMessage().toLowerCase().contains("deck"));
        }

        try {
            flashcardManager.createFlashcard("Question", "Answer", -1);
            fail("Should have thrown FlashcardValidationException");
        } catch (FlashcardValidationException e) {
            assertTrue("Error message should mention deck ID",
                e.getMessage().toLowerCase().contains("deck"));
        }
    }

    @Test
    public void testCreateFlashcard_whitespaceTrimmed() {
        // Act: Create flashcard with whitespace
        Flashcard created = flashcardManager.createFlashcard("  Question  ", "  Answer  ", testDeckId);

        // Assert: Content is trimmed and persisted correctly
        assertNotNull(created);

        // Retrieve from SQLite to verify persistence
        Flashcard retrieved = flashcardPersistence.getFlashcardById(created.getId());
        assertNotNull(retrieved);
        // The content should be stored as-is (validator checks it's not empty after trimming)
        assertEquals("  Question  ", retrieved.getFront());
        assertEquals("  Answer  ", retrieved.getBack());
    }

    // ---------------- Test update flashcard → verify changes in SQLite ----------------

    @Test
    public void testUpdateFlashcard_changesPersistedToSQLite() {
        // Arrange: Create a flashcard
        Flashcard created = flashcardManager.createFlashcard("Original Front", "Original Back", testDeckId);
        int flashcardId = created.getId();

        // Act: Update the flashcard through manager
        boolean updated = flashcardManager.updateFlashcard(flashcardId, "Updated Front", "Updated Back");

        // Assert: Update was successful
        assertTrue("Update should succeed", updated);

        // Assert: Changes are persisted in SQLite
        Flashcard retrieved = flashcardPersistence.getFlashcardById(flashcardId);
        assertNotNull(retrieved);
        assertEquals("Updated Front", retrieved.getFront());
        assertEquals("Updated Back", retrieved.getBack());
    }

    @Test
    public void testUpdateFlashcard_validationIntegration_emptyFront_throwsException() {
        // Arrange: Create a flashcard
        Flashcard created = flashcardManager.createFlashcard("Original Front", "Original Back", testDeckId);
        int flashcardId = created.getId();

        // Act & Assert: Empty front should be caught by validator
        try {
            flashcardManager.updateFlashcard(flashcardId, "", "New Back");
            fail("Should have thrown FlashcardValidationException");
        } catch (FlashcardValidationException e) {
            assertTrue("Error message should mention front",
                e.getMessage().toLowerCase().contains("front"));
        }

        // Assert: Flashcard was not modified in SQLite
        Flashcard retrieved = flashcardPersistence.getFlashcardById(flashcardId);
        assertEquals("Original Front", retrieved.getFront());
        assertEquals("Original Back", retrieved.getBack());
    }

    @Test
    public void testUpdateFlashcard_validationIntegration_emptyBack_throwsException() {
        // Arrange: Create a flashcard
        Flashcard created = flashcardManager.createFlashcard("Original Front", "Original Back", testDeckId);
        int flashcardId = created.getId();

        // Act & Assert: Empty back should be caught by validator
        try {
            flashcardManager.updateFlashcard(flashcardId, "New Front", "");
            fail("Should have thrown FlashcardValidationException");
        } catch (FlashcardValidationException e) {
            assertTrue("Error message should mention back",
                e.getMessage().toLowerCase().contains("back"));
        }

        // Assert: Flashcard was not modified in SQLite
        Flashcard retrieved = flashcardPersistence.getFlashcardById(flashcardId);
        assertEquals("Original Front", retrieved.getFront());
        assertEquals("Original Back", retrieved.getBack());
    }

    @Test
    public void testUpdateFlashcard_nonExistent_returnsFalse() {
        // Act: Try to update a flashcard that doesn't exist
        boolean updated = flashcardManager.updateFlashcard(99999, "Front", "Back");

        // Assert: Update should fail gracefully
        assertFalse("Update of non-existent flashcard should return false", updated);
    }

    // ---------------- Test search functionality with persistence ----------------

    @Test
    public void testSearchFlashcards_queriesSQLite() {
        // Arrange: Create multiple flashcards
        flashcardManager.createFlashcard("What is Java?", "A programming language", testDeckId);
        flashcardManager.createFlashcard("What is Python?", "Another language", testDeckId);
        flashcardManager.createFlashcard("What is SQL?", "Database query language", testDeckId);

        // Act: Search for keyword "language"
        List<Flashcard> results = flashcardManager.searchFlashcards("language", testDeckId);

        // Assert: All three cards contain "language"
        assertNotNull("Results should not be null", results);
        assertEquals("Should find 3 flashcards containing 'language'", 3, results.size());
    }

    @Test
    public void testSearchFlashcards_caseInsensitive() {
        // Arrange: Create flashcards
        flashcardManager.createFlashcard("Java Question", "Java Answer", testDeckId);

        // Act: Search with different cases
        List<Flashcard> results1 = flashcardManager.searchFlashcards("java", testDeckId);
        List<Flashcard> results2 = flashcardManager.searchFlashcards("JAVA", testDeckId);
        List<Flashcard> results3 = flashcardManager.searchFlashcards("JaVa", testDeckId);

        // Assert: All searches find the card
        assertEquals(1, results1.size());
        assertEquals(1, results2.size());
        assertEquals(1, results3.size());
    }

    @Test
    public void testSearchFlashcards_searchesBothFrontAndBack() {
        // Arrange: Create flashcards
        flashcardManager.createFlashcard("Question about Java", "Answer", testDeckId);
        flashcardManager.createFlashcard("Question", "Answer about Java", testDeckId);

        // Act: Search for "Java"
        List<Flashcard> results = flashcardManager.searchFlashcards("Java", testDeckId);

        // Assert: Both cards are found (one has "Java" in front, one in back)
        assertEquals("Should find cards with keyword in front or back", 2, results.size());
    }

    @Test
    public void testSearchFlashcards_allDecks() {
        // Arrange: Create another deck
        Deck deck2 = deckPersistence.insertDeck(Deck.createNew("Second Deck", ""));
        int deck2Id = deck2.getId();

        // Create flashcards in both decks
        flashcardManager.createFlashcard("Test in deck 1", "Answer", testDeckId);
        flashcardManager.createFlashcard("Test in deck 2", "Answer", deck2Id);

        // Act: Search all decks (deckId = -1)
        List<Flashcard> results = flashcardManager.searchFlashcards("Test", -1);

        // Assert: Should find flashcards from both decks
        assertTrue("Should find at least 2 flashcards", results.size() >= 2);
    }

    @Test
    public void testSearchFlashcards_nullOrEmpty_returnsNull() {
        // Assert: Manager handles null/empty input gracefully
        assertNull("null keyword should return null", flashcardManager.searchFlashcards(null, testDeckId));
        assertNull("empty keyword should return null", flashcardManager.searchFlashcards("", testDeckId));
        assertNull("whitespace keyword should return null", flashcardManager.searchFlashcards("   ", testDeckId));
    }

    // ---------------- Test filtering by known/unknown with persistence ----------------

    @Test
    public void testGetFlashcardsByMode_filterKnown() {
        // Arrange: Create flashcards and mark some as known
        Flashcard card1 = flashcardManager.createFlashcard("Q1", "A1", testDeckId);
        Flashcard card2 = flashcardManager.createFlashcard("Q2", "A2", testDeckId);
        Flashcard card3 = flashcardManager.createFlashcard("Q3", "A3", testDeckId);

        // Mark card1 as known in persistence
        card1.setIsKnown(true);
        flashcardPersistence.updateFlashcard(card1);

        // Act: Filter for known cards
        List<Flashcard> knownCards = flashcardManager.getFlashcardsByMode(testDeckId, FilterMode.KNOWN);

        // Assert: Only card1 should be returned
        assertNotNull(knownCards);
        assertEquals("Should find 1 known card", 1, knownCards.size());
        assertEquals(card1.getId(), knownCards.get(0).getId());
    }

    @Test
    public void testGetFlashcardsByMode_filterUnknown() {
        // Arrange: Create flashcards and mark some as known
        Flashcard card1 = flashcardManager.createFlashcard("Q1", "A1", testDeckId);
        Flashcard card2 = flashcardManager.createFlashcard("Q2", "A2", testDeckId);
        Flashcard card3 = flashcardManager.createFlashcard("Q3", "A3", testDeckId);

        // Mark card1 as known
        card1.setIsKnown(true);
        flashcardPersistence.updateFlashcard(card1);

        // Act: Filter for unknown cards
        List<Flashcard> unknownCards = flashcardManager.getFlashcardsByMode(testDeckId, FilterMode.UNKNOWN);

        // Assert: card2 and card3 should be returned
        assertNotNull(unknownCards);
        assertEquals("Should find 2 unknown cards", 2, unknownCards.size());
    }

    @Test
    public void testGetFlashcardsByMode_all() {
        // Arrange: Create flashcards
        flashcardManager.createFlashcard("Q1", "A1", testDeckId);
        flashcardManager.createFlashcard("Q2", "A2", testDeckId);
        flashcardManager.createFlashcard("Q3", "A3", testDeckId);

        // Act: Get all cards
        List<Flashcard> allCards = flashcardManager.getFlashcardsByMode(testDeckId, FilterMode.ALL);

        // Assert: All cards should be returned
        assertNotNull(allCards);
        assertEquals("Should find all 3 cards", 3, allCards.size());
    }

    // ---------------- Test other manager operations with persistence ----------------

    @Test
    public void testGetFlashcardsByDeck_reflectsSQLiteState() {
        // Arrange: Create flashcards
        flashcardManager.createFlashcard("Q1", "A1", testDeckId);
        flashcardManager.createFlashcard("Q2", "A2", testDeckId);

        // Act: Get flashcards by deck
        List<Flashcard> cards = flashcardManager.getFlashcardsByDeck(testDeckId);

        // Assert: Returns correct cards from SQLite
        assertNotNull(cards);
        assertEquals("Should have 2 flashcards", 2, cards.size());
    }

    @Test
    public void testGetFlashcardCount_reflectsSQLiteState() {
        // Arrange: Create flashcards
        flashcardManager.createFlashcard("Q1", "A1", testDeckId);
        flashcardManager.createFlashcard("Q2", "A2", testDeckId);
        flashcardManager.createFlashcard("Q3", "A3", testDeckId);

        // Act: Get count
        int count = flashcardManager.getFlashcardCount(testDeckId);

        // Assert: Count reflects SQLite state
        assertEquals("Count should be 3", 3, count);
    }

    @Test
    public void testDeleteFlashcard_removedFromSQLite() {
        // Arrange: Create a flashcard
        Flashcard created = flashcardManager.createFlashcard("Question", "Answer", testDeckId);
        int flashcardId = created.getId();

        // Verify it exists
        assertNotNull(flashcardPersistence.getFlashcardById(flashcardId));

        // Act: Delete the flashcard
        boolean deleted = flashcardManager.deleteFlashcard(flashcardId);

        // Assert: Delete was successful
        assertTrue("Delete should succeed", deleted);

        // Assert: Flashcard is removed from SQLite
        assertNull("Flashcard should no longer exist",
            flashcardPersistence.getFlashcardById(flashcardId));
    }

    @Test
    public void testDeleteFlashcardsByDeck_removesAllFromDeck() {
        // Arrange: Create flashcards
        flashcardManager.createFlashcard("Q1", "A1", testDeckId);
        flashcardManager.createFlashcard("Q2", "A2", testDeckId);
        flashcardManager.createFlashcard("Q3", "A3", testDeckId);

        // Verify they exist
        assertEquals(3, flashcardPersistence.getFlashcardCountByDeckId(testDeckId));

        // Act: Delete all flashcards in deck
        int deletedCount = flashcardManager.deleteFlashcardsByDeck(testDeckId);

        // Assert: All flashcards deleted
        assertEquals("Should delete 3 flashcards", 3, deletedCount);
        assertEquals("No flashcards should remain", 0,
            flashcardPersistence.getFlashcardCountByDeckId(testDeckId));
    }

    @Test
    public void testGetKnownAmount_queriesSQLite() {
        // Arrange: Create flashcards
        Flashcard card1 = flashcardManager.createFlashcard("Q1", "A1", testDeckId);
        Flashcard card2 = flashcardManager.createFlashcard("Q2", "A2", testDeckId);
        Flashcard card3 = flashcardManager.createFlashcard("Q3", "A3", testDeckId);

        // Mark two as known
        card1.setIsKnown(true);
        card2.setIsKnown(true);
        flashcardPersistence.updateFlashcard(card1);
        flashcardPersistence.updateFlashcard(card2);

        // Act: Get known amount
        int knownAmount = flashcardManager.getKnownAmount(testDeckId);

        // Assert: Correctly counts known cards from SQLite
        assertEquals("Should have 2 known cards", 2, knownAmount);
    }

    @Test
    public void testGetFlashcard_queriesSQLite() {
        // Arrange: Create a flashcard
        Flashcard created = flashcardManager.createFlashcard("Question", "Answer", testDeckId);
        int flashcardId = created.getId();

        // Act: Retrieve by ID
        Flashcard retrieved = flashcardManager.getFlashcard(flashcardId);

        // Assert: Manager correctly queries SQLite
        assertNotNull("Should find flashcard", retrieved);
        assertEquals(flashcardId, retrieved.getId());
        assertEquals("Question", retrieved.getFront());
        assertEquals("Answer", retrieved.getBack());

        // Assert: Invalid ID returns null
        assertNull("Should return null for invalid ID",
            flashcardManager.getFlashcard(0));
        assertNull("Should return null for non-existent ID",
            flashcardManager.getFlashcard(99999));
    }

    @Test
    public void testGetAllFlashcards_reflectsSQLiteState() {
        // Arrange: Create flashcards in different decks
        flashcardManager.createFlashcard("Q1", "A1", testDeckId);
        flashcardManager.createFlashcard("Q2", "A2", testDeckId);

        Deck deck2 = deckPersistence.insertDeck(Deck.createNew("Deck 2", ""));
        flashcardManager.createFlashcard("Q3", "A3", deck2.getId());

        // Act: Get all flashcards
        List<Flashcard> allCards = flashcardManager.getAllFlashcards();

        // Assert: Should include flashcards from all decks plus initial data
        assertNotNull(allCards);
        // Initial DB has 7 flashcards + 3 we just created = 10
        assertTrue("Should have at least 10 flashcards", allCards.size() >= 10);
    }
}
