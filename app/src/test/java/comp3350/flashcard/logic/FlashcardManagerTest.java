package comp3350.flashcard.logic;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import comp3350.flashcard.logic.exceptions.FlashcardValidationException;
import comp3350.flashcard.logic.validators.FlashcardValidator;
import comp3350.flashcard.logic.validators.IFlashcardValidator;
import comp3350.flashcard.objects.Flashcard;
import comp3350.flashcard.persistence.FlashcardPersistence;
import comp3350.flashcard.persistence.stubs.FlashcardPersistenceStub;

import static org.junit.Assert.*;

public class FlashcardManagerTest {

    private FlashcardManager manager;

    @Before
    public void setUp() {
        FlashcardPersistence persistence = new FlashcardPersistenceStub();
        persistence.clearAll();
        IFlashcardValidator validator = new FlashcardValidator();
        manager = new FlashcardManager(persistence, validator);
    }

    // ---------------- Helpers ----------------

    private Flashcard create(String front, String back, int deckId) {
        Flashcard c = manager.createFlashcard(front, back, deckId);
        assertNotNull(c);
        return c;
    }

    // ---------------- createFlashcard ----------------

    @Test
    public void createFlashcard_valid_createsCard() {
        Flashcard c = manager.createFlashcard("What is Java?", "A programming language", 1);
        assertNotNull(c);
        assertEquals("What is Java?", c.getFront());
        assertEquals("A programming language", c.getBack());
        assertEquals(1, c.getDeckId());
        assertTrue(c.getId() > 0);
        assertFalse(c.getIsKnown());
    }

    @Test
    public void createFlashcard_invalidFront_throwsException() {
        assertThrows(FlashcardValidationException.class, () -> manager.createFlashcard(null, "A", 1));
        assertThrows(FlashcardValidationException.class, () -> manager.createFlashcard("", "A", 1));
        assertThrows(FlashcardValidationException.class, () -> manager.createFlashcard("   ", "A", 1));
    }

    @Test
    public void createFlashcard_invalidBack_throwsException() {
        assertThrows(FlashcardValidationException.class, () -> manager.createFlashcard("Q", null, 1));
        assertThrows(FlashcardValidationException.class, () -> manager.createFlashcard("Q", "", 1));
        assertThrows(FlashcardValidationException.class, () -> manager.createFlashcard("Q", "   ", 1));
    }

    @Test
    public void createFlashcard_deckIdZero_throwsException() {
        assertThrows(FlashcardValidationException.class, () -> manager.createFlashcard("Q", "A", 0));
    }

    @Test
    public void createFlashcard_negativeDeckId_throwsException() {
        assertThrows(FlashcardValidationException.class, () -> manager.createFlashcard("Q", "A", -1));
    }

    // ---------------- getFlashcard ----------------

    @Test
    public void getFlashcard_existing_returnsIt() {
        Flashcard created = create("Capital of France?", "Paris", 1);

        Flashcard fetched = manager.getFlashcard(created.getId());
        assertNotNull(fetched);
        assertEquals(created.getId(), fetched.getId());
        assertEquals("Capital of France?", fetched.getFront());
        assertEquals("Paris", fetched.getBack());
        assertEquals(1, fetched.getDeckId());
        assertFalse(fetched.getIsKnown());
    }

    @Test
    public void getFlashcard_invalidId_returnsNull() {
        assertNull(manager.getFlashcard(0));
        assertNull(manager.getFlashcard(-1));
    }

    @Test
    public void getFlashcard_notFound_returnsNull() {
        assertNull(manager.getFlashcard(99999));
    }

    // ---------------- updateFlashcard ----------------

    @Test
    public void updateFlashcard_valid_updatesContent() {
        Flashcard created = create("Old Q", "Old A", 1);
        created.setIsKnown(true);

        assertTrue(manager.updateFlashcard(created.getId(), "New Q", "New A"));

        Flashcard updated = manager.getFlashcard(created.getId());
        assertNotNull(updated);
        assertEquals("New Q", updated.getFront());
        assertEquals("New A", updated.getBack());
        assertEquals(1, updated.getDeckId()); // deckId unchanged
    }

    @Test
    public void updateFlashcard_invalidContent_throwsException() {
        Flashcard created = create("Q", "A", 1);

        assertThrows(FlashcardValidationException.class, () -> manager.updateFlashcard(created.getId(), null, "A"));
        assertThrows(FlashcardValidationException.class, () -> manager.updateFlashcard(created.getId(), "Q", ""));
        assertThrows(FlashcardValidationException.class, () -> manager.updateFlashcard(created.getId(), "   ", "A"));
        assertThrows(FlashcardValidationException.class, () -> manager.updateFlashcard(created.getId(), "Q", "   "));
    }

    @Test
    public void updateFlashcard_notFound_fails() {
        assertFalse(manager.updateFlashcard(99999, "Q", "A"));
    }

    @Test
    public void updateFlashcard_invalidIdZero_fails() {
        assertFalse(manager.updateFlashcard(0, "Q", "A"));
    }

    @Test
    public void updateFlashcard_invalidIdNegative_fails() {
        assertFalse(manager.updateFlashcard(-1, "Q", "A"));
    }

    // ---------------- deleteFlashcard ----------------

    @Test
    public void deleteFlashcard_existing_deletesIt() {
        Flashcard created = create("To Delete", "Delete Me", 1);

        assertTrue(manager.deleteFlashcard(created.getId()));
        assertNull(manager.getFlashcard(created.getId()));
    }

    @Test
    public void deleteFlashcard_invalidId_fails() {
        assertFalse(manager.deleteFlashcard(0));
        assertFalse(manager.deleteFlashcard(-1));
    }

    @Test
    public void deleteFlashcard_notFound_fails() {
        assertFalse(manager.deleteFlashcard(99999));
    }

    // ---------------- getFlashcardsByDeck ----------------

    @Test
    public void getFlashcardsByDeck_returnsOnlyThatDeck() {
        create("Q1", "A1", 1);
        create("Q2", "A2", 1);
        create("Q3", "A3", 1);
        create("Q4", "A4", 2);

        List<Flashcard> deck1 = manager.getFlashcardsByDeck(1);
        assertNotNull(deck1);
        assertEquals(3, deck1.size());
        for (Flashcard c : deck1) {
            assertEquals(1, c.getDeckId());
        }
    }

    @Test
    public void getFlashcardsByDeck_negativeDeckId_returnsNull() {
        assertNull(manager.getFlashcardsByDeck(-1));
    }

    @Test
    public void getFlashcardsByDeck_emptyDeck_returnsEmptyList() {
        List<Flashcard> result = manager.getFlashcardsByDeck(999);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ---------------- getAllFlashcards ----------------

    @Test
    public void getAllFlashcards_returnsAll() {
        create("Q1", "A1", 1);
        create("Q2", "A2", 1);
        create("Q3", "A3", 2);
        create("Q4", "A4", 3);

        List<Flashcard> all = manager.getAllFlashcards();
        assertNotNull(all);
        assertEquals(4, all.size());
    }

    // ---------------- searchFlashcards ----------------

    @Test
    public void searchFlashcards_allDecks_caseInsensitiveContains() {
        create("What is Java?", "A programming language", 1);
        create("What is Python?", "Another programming language", 1);
        create("Capital of France?", "Paris", 2);
        create("What is JavaScript?", "Web programming language", 2);

        List<Flashcard> java = manager.searchFlashcards("java", -1);
        assertNotNull(java);
        assertEquals(2, java.size()); // Java + JavaScript

        List<Flashcard> prog = manager.searchFlashcards("PROGRAMMING", -1);
        assertNotNull(prog);
        assertEquals(3, prog.size()); // Java, Python, JavaScript
    }

    @Test
    public void searchFlashcards_deckFilter_applies() {
        create("Java Question", "Java Answer", 1);
        create("Python Question", "Python Answer", 2);
        create("JavaScript Question", "JavaScript Answer", 2);

        List<Flashcard> results = manager.searchFlashcards("Java", 2);
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(2, results.get(0).getDeckId());
        assertTrue(results.get(0).getFront().contains("JavaScript"));
    }

    @Test
    public void searchFlashcards_emptyDeck_returnsEmptyList() {
        List<Flashcard> results = manager.searchFlashcards("anything", 999);
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    public void searchFlashcards_invalidKeyword_returnsNull() {
        assertNull(manager.searchFlashcards(null, -1));
        assertNull(manager.searchFlashcards("", -1));
        assertNull(manager.searchFlashcards("   ", -1));
    }

    // ---------------- getFlashcardCount ----------------

    @Test
    public void getFlashcardCount_countsPerDeck() {
        create("Q1", "A1", 1);
        create("Q2", "A2", 1);
        create("Q3", "A3", 1);
        create("Q4", "A4", 2);

        assertEquals(3, manager.getFlashcardCount(1));
        assertEquals(1, manager.getFlashcardCount(2));
        assertEquals(0, manager.getFlashcardCount(999));
        assertEquals(0, manager.getFlashcardCount(-1));
    }

    // ---------------- deleteFlashcardsByDeck ----------------

    @Test
    public void deleteFlashcardsByDeck_deletesOnlyThatDeck() {
        create("Q1", "A1", 1);
        create("Q2", "A2", 1);
        create("Q3", "A3", 1);
        create("Q4", "A4", 2);

        assertEquals(3, manager.deleteFlashcardsByDeck(1));
        assertTrue(manager.getFlashcardsByDeck(1).isEmpty());
        assertEquals(1, manager.getFlashcardsByDeck(2).size());

        assertEquals(0, manager.deleteFlashcardsByDeck(999));
        assertEquals(0, manager.deleteFlashcardsByDeck(-1));
    }

    // ---------------- getKnownAmount ----------------
    @Test
    public void getKnownAmount_countsPerDeck() {
        create("Q1", "A1", 1).setIsKnown(true);
        create("Q2", "A2", 1).setIsKnown(true);
        create("Q3", "A3", 1);
        create("Q4", "A4", 2);

        assertEquals(2, manager.getKnownAmount(1));
        assertEquals(0, manager.getKnownAmount(2));
        assertEquals(0, manager.getKnownAmount(999));
        assertEquals(-1, manager.getKnownAmount(-1));
    }

    // ---------------- getFlashcardsByMode ----------------
    @Test
    public void getFlashcardsByMode_listsPerDeck() {
        Flashcard ca = create("Q1", "A1", 1);
        ca.setIsKnown(true);
        Flashcard cb = create("Q2", "A2", 1);
        cb.setIsKnown(true);
        Flashcard cc = create("Q3", "A3", 1);
        Flashcard cd = create("Q4", "A4", 2);

        // ALL mode
        assertEquals(3, manager.getFlashcardsByMode(1, FilterMode.ALL).size());

        // KNOWN mode
        assertEquals(2, manager.getFlashcardsByMode(1, FilterMode.KNOWN).size());
        
        // UNKNOWN mode
        assertEquals(1, manager.getFlashcardsByMode(1, FilterMode.UNKNOWN).size());
        assertEquals(cc, manager.getFlashcardsByMode(1, FilterMode.UNKNOWN).get(0));

        // Other deck
        assertEquals(1, manager.getFlashcardsByMode(2, FilterMode.UNKNOWN).size());
        assertEquals(cd, manager.getFlashcardsByMode(2, FilterMode.UNKNOWN).get(0));

        // Invalid Inputs
        assertNotNull(manager.getFlashcardsByMode(999, FilterMode.ALL));
        assertEquals(0, manager.getFlashcardsByMode(999, FilterMode.ALL).size());

        assertNull(manager.getFlashcardsByMode(-1, FilterMode.ALL));
    }
}
