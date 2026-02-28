package comp3350.flashcard.persistence;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.List;
import comp3350.flashcard.objects.Deck;
import comp3350.flashcard.objects.Flashcard;
import comp3350.flashcard.persistence.sqlite.DeckPersistenceSQLite;
import comp3350.flashcard.persistence.sqlite.FlashcardPersistenceSQLite;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class DataAccessTest {
    private DeckPersistence deckPersistence;
    private FlashcardPersistence flashcardPersistence;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        context.deleteDatabase("flashcard.db");

        deckPersistence = new DeckPersistenceSQLite(context);
        flashcardPersistence = new FlashcardPersistenceSQLite(context);
    }

    @After
    public void tearDown() {
        deckPersistence.clearAll();
        flashcardPersistence.clearAll();
    }

    @Test
    public void testDatabaseInitialization() {
        List<Deck> decks = deckPersistence.getAllDecks();
        assertEquals(3, decks.size());

        List<Flashcard> flashcards = flashcardPersistence.getAllFlashcards();
        assertEquals(7, flashcards.size());
    }

    @Test
    public void testCreateAndRetrieveDeck() {
        Deck newDeck = new Deck("Test Deck", "Test Description");
        Deck inserted = deckPersistence.insertDeck(newDeck);

        assertNotNull(inserted);
        assertTrue(inserted.getId() > 0);
        assertEquals("Test Deck", inserted.getName());
        assertEquals("Test Description", inserted.getDescription());

        Deck retrieved = deckPersistence.getDeckById(inserted.getId());
        assertNotNull(retrieved);
        assertEquals(inserted.getId(), retrieved.getId());
        assertEquals("Test Deck", retrieved.getName());
    }

    @Test
    public void testGetDeckByName() {
        Deck deck = deckPersistence.getDeckByName("Spanish Vocabulary");
        assertNotNull(deck);
        assertEquals("Spanish Vocabulary", deck.getName());

        Deck nonExistent = deckPersistence.getDeckByName("Nonexistent Deck");
        assertNull(nonExistent);
    }

    @Test
    public void testUpdateDeck() {
        Deck deck = deckPersistence.getDeckById(1);
        assertNotNull(deck);

        String originalName = deck.getName();
        deck.setName("Updated Name");
        deck.setDescription("Updated Description");
        boolean updated = deckPersistence.updateDeck(deck);

        assertTrue(updated);

        Deck retrieved = deckPersistence.getDeckById(1);
        assertEquals("Updated Name", retrieved.getName());
        assertEquals("Updated Description", retrieved.getDescription());
        assertNotEquals(originalName, retrieved.getName());
    }

    @Test
    public void testDeleteDeck() {
        int deckId = 1;
        assertTrue(deckPersistence.deckExists(deckId));

        boolean deleted = deckPersistence.deleteDeck(deckId);
        assertTrue(deleted);

        assertNull(deckPersistence.getDeckById(deckId));
        assertFalse(deckPersistence.deckExists(deckId));
    }

    @Test
    public void testCascadeDeleteFlashcards() {
        int deckId = 1;
        List<Flashcard> flashcards = flashcardPersistence.getFlashcardsByDeckId(deckId);
        int originalCount = flashcards.size();
        assertTrue(originalCount > 0);

        deckPersistence.deleteDeck(deckId);

        List<Flashcard> remaining = flashcardPersistence.getFlashcardsByDeckId(deckId);
        assertEquals(0, remaining.size());
    }

    @Test
    public void testDeckCount() {
        int initialCount = deckPersistence.getDeckCount();
        assertEquals(3, initialCount);

        Deck newDeck = new Deck("Count Test", "Description");
        deckPersistence.insertDeck(newDeck);

        int newCount = deckPersistence.getDeckCount();
        assertEquals(initialCount + 1, newCount);
    }

    @Test
    public void testDeckNameUniqueness() {
        boolean exists = deckPersistence.deckNameExists("Spanish Vocabulary", -1);
        assertTrue(exists);

        boolean notExists = deckPersistence.deckNameExists("Nonexistent Deck", -1);
        assertFalse(notExists);

        Deck deck = deckPersistence.getDeckByName("Spanish Vocabulary");
        boolean excludeSelf = deckPersistence.deckNameExists("Spanish Vocabulary", deck.getId());
        assertFalse(excludeSelf);
    }

    @Test
    public void testCreateAndRetrieveFlashcard() {
        Flashcard newCard = new Flashcard("Question", "Answer", 1);
        Flashcard inserted = flashcardPersistence.insertFlashcard(newCard);

        assertNotNull(inserted);
        assertTrue(inserted.getId() > 0);
        assertEquals("Question", inserted.getFront());
        assertEquals("Answer", inserted.getBack());
        assertEquals(1, inserted.getDeckId());

        Flashcard retrieved = flashcardPersistence.getFlashcardById(inserted.getId());
        assertNotNull(retrieved);
        assertEquals(inserted.getId(), retrieved.getId());
    }

    @Test
    public void testGetFlashcardsByDeckId() {
        List<Flashcard> deck1Cards = flashcardPersistence.getFlashcardsByDeckId(1);
        assertTrue(deck1Cards.size() >= 3);

        List<Flashcard> deck2Cards = flashcardPersistence.getFlashcardsByDeckId(2);
        assertTrue(deck2Cards.size() >= 2);

        for (Flashcard card : deck1Cards) {
            assertEquals(1, card.getDeckId());
        }
    }

    @Test
    public void testUpdateFlashcard() {
        Flashcard card = flashcardPersistence.getFlashcardById(1);
        assertNotNull(card);

        card.setFront("Updated Front");
        card.setBack("Updated Back");
        boolean updated = flashcardPersistence.updateFlashcard(card);

        assertTrue(updated);

        Flashcard retrieved = flashcardPersistence.getFlashcardById(1);
        assertEquals("Updated Front", retrieved.getFront());
        assertEquals("Updated Back", retrieved.getBack());
    }

    @Test
    public void testDeleteFlashcard() {
        int flashcardId = 1;
        assertTrue(flashcardPersistence.flashcardExists(flashcardId));

        boolean deleted = flashcardPersistence.deleteFlashcard(flashcardId);
        assertTrue(deleted);

        assertNull(flashcardPersistence.getFlashcardById(flashcardId));
        assertFalse(flashcardPersistence.flashcardExists(flashcardId));
    }

    @Test
    public void testDeleteFlashcardsByDeckId() {
        int deckId = 1;
        int initialCount = flashcardPersistence.getFlashcardCountByDeckId(deckId);
        assertTrue(initialCount > 0);

        int deleted = flashcardPersistence.deleteFlashcardsByDeckId(deckId);
        assertEquals(initialCount, deleted);

        int newCount = flashcardPersistence.getFlashcardCountByDeckId(deckId);
        assertEquals(0, newCount);
    }

    @Test
    public void testFlashcardCountByDeckId() {
        int count1 = flashcardPersistence.getFlashcardCountByDeckId(1);
        assertTrue(count1 >= 3);

        int count2 = flashcardPersistence.getFlashcardCountByDeckId(2);
        assertTrue(count2 >= 2);

        int countEmpty = flashcardPersistence.getFlashcardCountByDeckId(999);
        assertEquals(0, countEmpty);
    }

    @Test
    public void testClearAll() {
        deckPersistence.clearAll();
        assertEquals(0, deckPersistence.getDeckCount());

        flashcardPersistence.clearAll();
        assertEquals(0, flashcardPersistence.getAllFlashcards().size());
    }

    @Test
    public void testDeckTimestamps() {
        Deck deck = deckPersistence.getDeckById(1);
        assertNotNull(deck);
        assertTrue(deck.getCreatedAt() > 0);

        long newTimestamp = System.currentTimeMillis();
        deck.setLastStudiedAt(newTimestamp);
        deckPersistence.updateDeck(deck);

        Deck retrieved = deckPersistence.getDeckById(1);
        assertEquals(newTimestamp, retrieved.getLastStudiedAt());
    }

    @Test
    public void testFlashcardPersistsAcrossInstances() {
        Flashcard newCard = new Flashcard("Persistent Question", "Persistent Answer", 1);
        Flashcard inserted = flashcardPersistence.insertFlashcard(newCard);
        int insertedId = inserted.getId();

        Context context = ApplicationProvider.getApplicationContext();
        FlashcardPersistence newInstance = new FlashcardPersistenceSQLite(context);

        Flashcard retrieved = newInstance.getFlashcardById(insertedId);
        assertNotNull(retrieved);
        assertEquals("Persistent Question", retrieved.getFront());
        assertEquals("Persistent Answer", retrieved.getBack());
    }
}
