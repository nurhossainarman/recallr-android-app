package comp3350.flashcard.logic;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.List;
import comp3350.flashcard.constants.ValidationConstants;
import comp3350.flashcard.logic.validators.DeckValidator;
import comp3350.flashcard.logic.validators.IDeckValidator;
import comp3350.flashcard.objects.Deck;
import comp3350.flashcard.persistence.DeckPersistence;
import comp3350.flashcard.persistence.FlashcardPersistence;
import comp3350.flashcard.persistence.sqlite.DeckPersistenceSQLite;
import comp3350.flashcard.persistence.sqlite.FlashcardPersistenceSQLite;
import static org.junit.Assert.*;

/**
 * Integration test for DeckManager + DeckPersistenceSQLite interaction.
 * Tests the business layer ↔ persistence layer seam with real validators.
 */
@RunWith(AndroidJUnit4.class)
public class DeckManagerIntegrationTest {

    private DeckManager deckManager;
    private DeckPersistence deckPersistence;
    private FlashcardPersistence flashcardPersistence;
    private IDeckValidator deckValidator;
    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        context.deleteDatabase("flashcard.db");

        // Create real persistence layer instances (SQLite)
        deckPersistence = new DeckPersistenceSQLite(context);
        flashcardPersistence = new FlashcardPersistenceSQLite(context);

        // Create real validator (not mocked)
        deckValidator = new DeckValidator(deckPersistence);

        // Create DeckManager with real dependencies
        deckManager = new DeckManager(deckPersistence, flashcardPersistence, deckValidator);
    }

    @After
    public void tearDown() {
        deckPersistence.clearAll();
        flashcardPersistence.clearAll();
    }

    // ---------------- Test create deck → verify persisted to SQLite ----------------

    @Test
    public void testCreateDeck_persistedToSQLite() {
        // Act: Create deck through manager
        Deck created = deckManager.createDeck("New Deck", "New Description");

        // Assert: Deck was created and returned
        assertNotNull("Created deck should not be null", created);
        assertTrue("Deck ID should be valid", created.getId() > 0);
        assertEquals("New Deck", created.getName());
        assertEquals("New Description", created.getDescription());

        // Assert: Deck is actually in SQLite
        Deck retrieved = deckPersistence.getDeckById(created.getId());
        assertNotNull("Deck should be persisted in SQLite", retrieved);
        assertEquals(created.getId(), retrieved.getId());
        assertEquals("New Deck", retrieved.getName());
        assertEquals("New Description", retrieved.getDescription());
    }

    @Test
    public void testCreateDeck_validationIntegration_emptyName_throwsException() {
        // Act & Assert: Empty name should be caught by validator
        try {
            deckManager.createDeck("", "Description");
            fail("Should have thrown DeckValidationException");
        } catch (DeckValidationException e) {
            assertTrue("Error message should mention empty",
                e.getMessage().toLowerCase().contains("empty"));
        }

        // Assert: No deck was persisted
        List<Deck> allDecks = deckPersistence.getAllDecks();
        // Should only have the 3 default decks from initialization
        assertEquals(3, allDecks.size());
    }

    @Test
    public void testCreateDeck_validationIntegration_nameTooLong_throwsException() {
        // Arrange: Create name that exceeds ValidationConstants.MAX_DECK_NAME_LENGTH (100)
        String longName = "a".repeat(101);

        // Act & Assert: Long name should be caught by validator
        try {
            deckManager.createDeck(longName, "Description");
            fail("Should have thrown DeckValidationException");
        } catch (DeckValidationException e) {
            assertTrue("Error message should mention exceed",
                e.getMessage().toLowerCase().contains("exceed"));
        }

        // Assert: No deck was persisted
        List<Deck> allDecks = deckPersistence.getAllDecks();
        assertEquals(3, allDecks.size());
    }

    @Test
    public void testCreateDeck_validationIntegration_duplicateName_throwsException() {
        // Arrange: Create first deck
        deckManager.createDeck("Unique Name", "Description");

        // Act & Assert: Duplicate name should be caught by validator checking persistence
        try {
            deckManager.createDeck("Unique Name", "Another Description");
            fail("Should have thrown DeckValidationException for duplicate name");
        } catch (DeckValidationException e) {
            assertTrue("Error message should mention already exists",
                e.getMessage().toLowerCase().contains("already exists"));
        }

        // Assert: Only one deck with that name exists
        Deck deck = deckPersistence.getDeckByName("Unique Name");
        assertNotNull(deck);
        assertEquals("Description", deck.getDescription()); // Should be the first one
    }

    // ---------------- Test update deck → verify changes in SQLite ----------------

    @Test
    public void testUpdateDeck_changesPersistedToSQLite() {
        // Arrange: Create a deck
        Deck created = deckManager.createDeck("Original Name", "Original Description");
        int deckId = created.getId();

        // Act: Update the deck through manager
        boolean updated = deckManager.updateDeck(deckId, "Updated Name", "Updated Description");

        // Assert: Update was successful
        assertTrue("Update should succeed", updated);

        // Assert: Changes are persisted in SQLite
        Deck retrieved = deckPersistence.getDeckById(deckId);
        assertNotNull(retrieved);
        assertEquals("Updated Name", retrieved.getName());
        assertEquals("Updated Description", retrieved.getDescription());
    }

    @Test
    public void testUpdateDeck_validationIntegration_emptyName_throwsException() {
        // Arrange: Create a deck
        Deck created = deckManager.createDeck("Original Name", "Original Description");
        int deckId = created.getId();

        // Act & Assert: Empty name should be caught by validator
        try {
            deckManager.updateDeck(deckId, "", "New Description");
            fail("Should have thrown DeckValidationException");
        } catch (DeckValidationException e) {
            assertTrue("Error message should mention empty",
                e.getMessage().toLowerCase().contains("empty"));
        }

        // Assert: Deck was not modified in SQLite
        Deck retrieved = deckPersistence.getDeckById(deckId);
        assertEquals("Original Name", retrieved.getName());
        assertEquals("Original Description", retrieved.getDescription());
    }

    @Test
    public void testUpdateDeck_sameName_allowed() {
        // Arrange: Create a deck
        Deck created = deckManager.createDeck("My Deck", "Description");
        int deckId = created.getId();

        // Act: Update deck but keep same name (should be allowed - updating own name)
        boolean updated = deckManager.updateDeck(deckId, "My Deck", "New Description");

        // Assert: Update should succeed
        assertTrue("Should allow updating to same name", updated);

        // Assert: Description was updated in SQLite
        Deck retrieved = deckPersistence.getDeckById(deckId);
        assertEquals("My Deck", retrieved.getName());
        assertEquals("New Description", retrieved.getDescription());
    }

    @Test
    public void testUpdateDeck_duplicateNameOfAnotherDeck_throwsException() {
        // Arrange: Create two decks
        deckManager.createDeck("Deck One", "Description 1");
        Deck deck2 = deckManager.createDeck("Deck Two", "Description 2");

        // Act & Assert: Trying to rename deck2 to deck1's name should fail
        try {
            deckManager.updateDeck(deck2.getId(), "Deck One", "New Description");
            fail("Should have thrown DeckValidationException for duplicate name");
        } catch (DeckValidationException e) {
            assertTrue("Error message should mention already exists",
                e.getMessage().toLowerCase().contains("already exists"));
        }

        // Assert: Deck was not modified in SQLite
        Deck retrieved = deckPersistence.getDeckById(deck2.getId());
        assertEquals("Deck Two", retrieved.getName());
    }

    // ---------------- Test delete deck → verify cascading delete ----------------

    @Test
    public void testDeleteDeck_removedFromSQLite() {
        // Arrange: Create a deck
        Deck created = deckManager.createDeck("Deck to Delete", "Description");
        int deckId = created.getId();

        // Verify it exists
        assertTrue(deckPersistence.deckExists(deckId));

        // Act: Delete the deck through manager
        boolean deleted = deckManager.deleteDeck(deckId);

        // Assert: Delete was successful
        assertTrue("Delete should succeed", deleted);

        // Assert: Deck is removed from SQLite
        assertFalse("Deck should no longer exist", deckPersistence.deckExists(deckId));
        assertNull("getDeckById should return null", deckPersistence.getDeckById(deckId));
    }

    @Test
    public void testDeleteDeck_cascadingDeleteFlashcards() {
        // Arrange: Create a deck
        Deck deck = deckManager.createDeck("Deck with Cards", "Description");
        int deckId = deck.getId();

        // Add flashcards to the deck
        flashcardPersistence.insertFlashcard(comp3350.flashcard.objects.Flashcard.createNew("Front 1", "Back 1", deckId));
        flashcardPersistence.insertFlashcard(comp3350.flashcard.objects.Flashcard.createNew("Front 2", "Back 2", deckId));

        // Verify flashcards exist
        assertEquals(2, flashcardPersistence.getFlashcardCountByDeckId(deckId));

        // Act: Delete the deck
        boolean deleted = deckManager.deleteDeck(deckId);

        // Assert: Delete was successful
        assertTrue("Delete should succeed", deleted);

        // Assert: Cascading delete removed all flashcards
        assertEquals("All flashcards should be deleted", 0,
            flashcardPersistence.getFlashcardCountByDeckId(deckId));
        assertTrue("getFlashcardsByDeckId should return empty list",
            flashcardPersistence.getFlashcardsByDeckId(deckId).isEmpty());
    }

    @Test
    public void testDeleteDeck_nonExistentDeck_returnsFalse() {
        // Act: Try to delete a deck that doesn't exist
        boolean deleted = deckManager.deleteDeck(99999);

        // Assert: Delete should fail gracefully
        assertFalse("Delete of non-existent deck should return false", deleted);
    }

    // ---------------- Test other manager operations with persistence ----------------

    @Test
    public void testGetAllDecks_reflectsSQLiteState() {
        // Arrange: Initial decks from database initialization
        List<Deck> initialDecks = deckManager.getAllDecks();
        int initialCount = initialDecks.size();

        // Act: Create new decks
        deckManager.createDeck("Test Deck 1", "Description 1");
        deckManager.createDeck("Test Deck 2", "Description 2");

        // Assert: getAllDecks reflects new state from SQLite
        List<Deck> updatedDecks = deckManager.getAllDecks();
        assertEquals("Should have 2 more decks", initialCount + 2, updatedDecks.size());
    }

    @Test
    public void testGetFlashcardCount_reflectsSQLiteState() {
        // Arrange: Create a deck
        Deck deck = deckManager.createDeck("Deck for Count Test", "Description");
        int deckId = deck.getId();

        // Act: Add flashcards directly to persistence
        flashcardPersistence.insertFlashcard(comp3350.flashcard.objects.Flashcard.createNew("Q1", "A1", deckId));
        flashcardPersistence.insertFlashcard(comp3350.flashcard.objects.Flashcard.createNew("Q2", "A2", deckId));
        flashcardPersistence.insertFlashcard(comp3350.flashcard.objects.Flashcard.createNew("Q3", "A3", deckId));

        // Assert: Manager's getFlashcardCount queries SQLite correctly
        int count = deckManager.getFlashcardCount(deckId);
        assertEquals("Count should reflect SQLite state", 3, count);
    }

    @Test
    public void testMarkDeckAsStudied_persistsTimestamp() {
        // Arrange: Create a deck
        Deck deck = deckManager.createDeck("Deck to Study", "Description");
        int deckId = deck.getId();

        // Verify initially not studied
        assertFalse("Deck should not have been studied initially",
            deckPersistence.getDeckById(deckId).hasBeenStudied());

        // Act: Mark as studied
        long beforeTime = System.currentTimeMillis();
        boolean marked = deckManager.markDeckAsStudied(deckId);
        long afterTime = System.currentTimeMillis();

        // Assert: Operation succeeded
        assertTrue("markDeckAsStudied should succeed", marked);

        // Assert: Timestamp is persisted in SQLite
        Deck retrieved = deckPersistence.getDeckById(deckId);
        assertTrue("Deck should now be marked as studied", retrieved.hasBeenStudied());
        assertTrue("Last studied time should be recent",
            retrieved.getLastStudiedAt() >= beforeTime && retrieved.getLastStudiedAt() <= afterTime);
    }

    @Test
    public void testDeckExists_queriesSQLite() {
        // Arrange: Create a deck
        Deck deck = deckManager.createDeck("Existing Deck", "Description");
        int deckId = deck.getId();

        // Assert: Manager correctly queries SQLite for existence
        assertTrue("Should return true for existing deck", deckManager.deckExists(deckId));
        assertFalse("Should return false for non-existent deck", deckManager.deckExists(99999));
    }

    @Test
    public void testGetDeckByName_queriesSQLite() {
        // Arrange: Create a deck
        deckManager.createDeck("Searchable Deck", "Description");

        // Act: Search by name through manager
        Deck found = deckManager.getDeckByName("Searchable Deck");

        // Assert: Manager correctly queries SQLite
        assertNotNull("Should find deck by name", found);
        assertEquals("Searchable Deck", found.getName());

        // Assert: Non-existent name returns null
        assertNull("Should return null for non-existent name",
            deckManager.getDeckByName("Nonexistent Deck"));
    }

    @Test
    public void testGetDeckByName_nullOrEmpty_returnsNull() {
        // Assert: Manager handles null/empty input gracefully
        assertNull("null name should return null", deckManager.getDeckByName(null));
        assertNull("empty name should return null", deckManager.getDeckByName(""));
        assertNull("whitespace name should return null", deckManager.getDeckByName("   "));
    }
}
