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
    public void startSession_emptyDeck_returnsNull() {
        assertNull(manager.startSession(1, false));
        assertNull(manager.getCurrentCard());
        assertEquals(0, manager.getTotalCards());
        assertTrue(manager.isFinished());
    }

    @Test
    public void startSession_withCards_returnsFirstCard() {
        addCards(1, 3);
        Flashcard first = manager.startSession(1, false);
        
        assertNotNull(first);
        assertEquals("Front 0", first.getFront());
        assertEquals(1, manager.getPosition());
        assertEquals(3, manager.getTotalCards());
        assertFalse(manager.isFinished());
    }

    @Test
    public void nextCard_traversesList() {
        addCards(1, 3);
        manager.startSession(1, false);
        
        Flashcard second = manager.nextCard();
        assertNotNull(second);
        assertEquals("Front 1", second.getFront());
        assertEquals(2, manager.getPosition());

        Flashcard third = manager.nextCard();
        assertNotNull(third);
        assertEquals("Front 2", third.getFront());
        assertEquals(3, manager.getPosition());
        assertTrue(manager.isFinished());

        assertNull(manager.nextCard()); // End of session
    }

    @Test
    public void previousCard_traversesBackwards() {
        addCards(1, 3);
        manager.startSession(1, false);
        manager.nextCard(); // Move to second card
        
        Flashcard first = manager.previousCard();
        assertNotNull(first);
        assertEquals("Front 0", first.getFront());
        assertEquals(1, manager.getPosition());

        assertNull(manager.previousCard()); // Already at beginning
    }

    @Test
    public void startSession_withShuffle_changesOrder() {
        addCards(1, 50); // Use many cards to minimize accidental same-order
        
        // Session 1: No shuffle
        manager.startSession(1, false);
        List<Integer> order1 = new ArrayList<>();
        order1.add(manager.getCurrentCard().getId());
        while (!manager.isFinished()) {
            order1.add(manager.nextCard().getId());
        }

        // Session 2: Shuffle
        manager.startSession(1, true);
        List<Integer> order2 = new ArrayList<>();
        order2.add(manager.getCurrentCard().getId());
        while (!manager.isFinished()) {
            order2.add(manager.nextCard().getId());
        }

        assertNotEquals("Shuffle should change the card order", order1, order2);
        assertEquals(50, order2.size());
    }

    @Test
    public void isFinished_trueOnlyAtLastCard() {
        addCards(1, 2);
        manager.startSession(1, false);
        
        assertFalse(manager.isFinished());
        manager.nextCard();
        assertTrue(manager.isFinished());
    }
}
