package comp3350.flashcard.logic;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import comp3350.flashcard.objects.Flashcard;
import comp3350.flashcard.persistence.FlashcardPersistence;
import comp3350.flashcard.persistence.stubs.FlashcardPersistenceStub;

import static org.junit.Assert.*;

public class StudySessionManagerTest {

    private StudySessionManager manager;
    private FlashcardPersistence persistence;

    @Before
    public void setUp() {
        persistence = new FlashcardPersistenceStub();
        persistence.clearAll();
        manager = new StudySessionManager(persistence);
    }

    private void addCards(int deckId, int count) {
        for (int i = 0; i < count; i++) {
            persistence.insertFlashcard(new Flashcard("Front " + i, "Back " + i, deckId));
        }
    }

    @Test
    public void startSession_emptyDeck_initializesEmpty() {
        manager.startSession(1, false, IStudySession.FilterMode.ALL);
        assertNull(manager.getCurrentCard());
        assertEquals(0, manager.getTotalCards());
        assertTrue(manager.isFinished());
    }

    @Test
    public void startSession_withCards_initializesCorrectly() {
        addCards(1, 3);
        manager.startSession(1, false, IStudySession.FilterMode.ALL);
        
        Flashcard first = manager.getCurrentCard();
        assertNotNull(first);
        assertEquals("Front 0", first.getFront());
        assertEquals(1, manager.getPosition());
        assertEquals(3, manager.getTotalCards());
        assertFalse(manager.isFinished());
    }

    @Test
    public void nextCard_traversesList() {
        addCards(1, 3);
        manager.startSession(1, false, IStudySession.FilterMode.ALL);
        
        manager.nextCard();
        Flashcard second = manager.getCurrentCard();
        assertNotNull(second);
        assertEquals("Front 1", second.getFront());
        assertEquals(2, manager.getPosition());

        manager.nextCard();
        Flashcard third = manager.getCurrentCard();
        assertNotNull(third);
        assertEquals("Front 2", third.getFront());
        assertEquals(3, manager.getPosition());

        assertFalse(manager.isFinished());

        manager.nextCard(); // Move past last card
        assertNull(manager.getCurrentCard());
        assertTrue(manager.isFinished());
    }

    @Test
    public void previousCard_traversesBackwards() {
        addCards(1, 3);
        manager.startSession(1, false, IStudySession.FilterMode.ALL);
        manager.nextCard(); // Move to second card
        
        manager.previousCard();
        Flashcard first = manager.getCurrentCard();
        assertNotNull(first);
        assertEquals("Front 0", first.getFront());
        assertEquals(1, manager.getPosition());

        manager.previousCard(); // Already at beginning, should stay at first
        assertEquals(1, manager.getPosition());
    }

    @Test
    public void startSession_withShuffle_changesOrder() {
        addCards(1, 50); // Use many cards to minimize accidental same-order
        
        // Session 1: No shuffle
        manager.startSession(1, false, IStudySession.FilterMode.ALL);
        List<Integer> order1 = new ArrayList<>();
        order1.add(manager.getCurrentCard().getId());
        while (!manager.isFinished()) {
            manager.nextCard();
            if (manager.getCurrentCard() != null) {
                order1.add(manager.getCurrentCard().getId());
            }
        }

        // Session 2: Shuffle
        manager.startSession(1, true, IStudySession.FilterMode.ALL);
        List<Integer> order2 = new ArrayList<>();
        order2.add(manager.getCurrentCard().getId());
        while (!manager.isFinished()) {
            manager.nextCard();
            if (manager.getCurrentCard() != null) {
                order2.add(manager.getCurrentCard().getId());
            }
        }

        assertNotEquals("Shuffle should change the card order", order1, order2);
        assertEquals(50, order2.size());
    }

    @Test
    public void startSession_filterKnown_onlyReturnsKnown() {
        Flashcard c1 = new Flashcard("Q1", "A1", 1);
        c1.setIsKnown(true);
        persistence.insertFlashcard(c1);
        
        Flashcard c2 = new Flashcard("Q2", "A2", 1);
        c2.setIsKnown(false);
        persistence.insertFlashcard(c2);

        manager.startSession(1, false, IStudySession.FilterMode.KNOWN);
        assertEquals(1, manager.getTotalCards());
        assertEquals("Q1", manager.getCurrentText());
    }

    @Test
    public void startSession_filterUnknown_onlyReturnsUnknown() {
        Flashcard c1 = new Flashcard("Q1", "A1", 1);
        c1.setIsKnown(true);
        persistence.insertFlashcard(c1);
        
        Flashcard c2 = new Flashcard("Q2", "A2", 1);
        c2.setIsKnown(false);
        persistence.insertFlashcard(c2);

        manager.startSession(1, false, IStudySession.FilterMode.UNKNOWN);
        assertEquals(1, manager.getTotalCards());
        assertEquals("Q2", manager.getCurrentText());
    }

    @Test
    public void setKnown_updatesPersistence() {
        addCards(1, 1);
        manager.startSession(1, false, IStudySession.FilterMode.ALL);
        
        Flashcard card = manager.getCurrentCard();
        assertFalse(card.getIsKnown());
        
        manager.setKnown(true);
        assertTrue(manager.isCurrentCardKnown());
        
        // Verify in persistence
        Flashcard updated = persistence.getFlashcardById(card.getId());
        assertTrue(updated.getIsKnown());
    }

    @Test
    public void flip_changesShowingSide() {
        persistence.insertFlashcard(new Flashcard("Question", "Answer", 1));
        manager.startSession(1, false, IStudySession.FilterMode.ALL);
        
        assertEquals("Question", manager.getCurrentText());
        manager.flip();
        assertEquals("Answer", manager.getCurrentText());
        manager.flip();
        assertEquals("Question", manager.getCurrentText());
    }
}
