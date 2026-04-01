package comp3350.flashcard.logic;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import comp3350.flashcard.logic.exceptions.StudySessionException;
import comp3350.flashcard.objects.Flashcard;
import comp3350.flashcard.persistence.FlashcardPersistence;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class StudySessionManagerTest {

    private StudySessionManager manager;

    @Mock
    private FlashcardPersistence persistence;

    @Mock
    private IFlashcardManager flashcardManager;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        manager = new StudySessionManager(persistence, flashcardManager);
    }

    private List<Flashcard> createMockCards(int count) {
        List<Flashcard> cards = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            cards.add(Flashcard.fromPersistence(i, "Front " + i, "Back " + i, 1, 0, false));
        }
        return cards;
    }

    // ---------------- Session Initialization & Customization ----------------

    @Test(expected = StudySessionException.class)
    public void startSession_emptyDeck_throwsException() throws StudySessionException {
        when(flashcardManager.getFlashcardCount(1)).thenReturn(0);
        when(flashcardManager.getFlashcardsByMode(1, FilterMode.ALL)).thenReturn(Collections.emptyList());

        manager.startSession(1, false, FilterMode.ALL);
    }

    @Test
    public void startSession_withCards_initializesCorrectly() throws StudySessionException {
        List<Flashcard> cards = createMockCards(3);
        when(flashcardManager.getFlashcardCount(1)).thenReturn(3);
        when(flashcardManager.getFlashcardsByMode(1, FilterMode.ALL)).thenReturn(cards);

        manager.startSession(1, false, FilterMode.ALL);

        Flashcard first = manager.getCurrentCard();
        assertNotNull(first);
        assertEquals("Front 1", first.getFront());
        assertEquals(1, manager.getPosition());
        assertEquals(3, manager.getTotalCards());
        assertFalse(manager.isFinished());
    }

    @Test
    public void startSession_withShuffle_changesOrder() throws StudySessionException {
        List<Flashcard> cards = createMockCards(50);
        when(flashcardManager.getFlashcardCount(1)).thenReturn(50);
        when(flashcardManager.getFlashcardsByMode(1, FilterMode.ALL)).thenReturn(new ArrayList<>(cards));

        manager.startSession(1, true, FilterMode.ALL);

        assertEquals(50, manager.getTotalCards());
        assertFalse(manager.isFinished());
    }

    @Test
    public void startSession_filterKnown_onlyReturnsKnown() throws StudySessionException {
        Flashcard known = Flashcard.fromPersistence(1, "Q1", "A1", 1, 0, true);
        
        when(flashcardManager.getFlashcardCount(1)).thenReturn(2);
        when(flashcardManager.getFlashcardsByMode(1, FilterMode.KNOWN)).thenReturn(Arrays.asList(known));

        manager.startSession(1, false, FilterMode.KNOWN);

        assertEquals(1, manager.getTotalCards());
        assertEquals("Q1", manager.getCurrentText());
    }

    @Test
    public void startSession_filterUnknown_onlyReturnsUnknown() throws StudySessionException {
        Flashcard unknown = Flashcard.fromPersistence(2, "Q2", "A2", 1, 0, false);

        when(flashcardManager.getFlashcardCount(1)).thenReturn(2);
        when(flashcardManager.getFlashcardsByMode(1, FilterMode.UNKNOWN)).thenReturn(Arrays.asList(unknown));

        manager.startSession(1, false, FilterMode.UNKNOWN);

        assertEquals(1, manager.getTotalCards());
        assertEquals("Q2", manager.getCurrentText());
    }

    @Test
    public void startSession_resetsProgress() throws StudySessionException {
        List<Flashcard> cards = createMockCards(3);
        when(flashcardManager.getFlashcardCount(1)).thenReturn(3);
        when(flashcardManager.getFlashcardsByMode(1, FilterMode.ALL)).thenReturn(cards);

        manager.startSession(1, false, FilterMode.ALL);
        manager.nextCard();
        assertEquals(2, manager.getPosition());

        manager.startSession(1, false, FilterMode.ALL);
        assertEquals(1, manager.getPosition());
    }

    // ---------------- Navigation ----------------

    @Test
    public void nextCard_traversesList() throws StudySessionException {
        List<Flashcard> cards = createMockCards(2);
        when(flashcardManager.getFlashcardCount(1)).thenReturn(2);
        when(flashcardManager.getFlashcardsByMode(1, FilterMode.ALL)).thenReturn(cards);

        manager.startSession(1, false, FilterMode.ALL);

        manager.nextCard();
        Flashcard second = manager.getCurrentCard();
        assertNotNull(second);
        assertEquals("Front 2", second.getFront());
        assertEquals(2, manager.getPosition());

        manager.nextCard(); // Move past last card
        assertNull(manager.getCurrentCard());
        assertTrue(manager.isFinished());
    }

    @Test
    public void previousCard_traversesBackwards() throws StudySessionException {
        List<Flashcard> cards = createMockCards(2);
        when(flashcardManager.getFlashcardCount(1)).thenReturn(2);
        when(flashcardManager.getFlashcardsByMode(1, FilterMode.ALL)).thenReturn(cards);

        manager.startSession(1, false, FilterMode.ALL);
        manager.nextCard(); // Move to second card

        manager.previousCard();
        Flashcard first = manager.getCurrentCard();
        assertNotNull(first);
        assertEquals("Front 1", first.getFront());
        assertEquals(1, manager.getPosition());
    }

    // ---------------- Card Operations (Flip/Known) ----------------

    @Test
    public void flip_changesShowingSide() throws StudySessionException {
        Flashcard card = Flashcard.fromPersistence(1, "Question", "Answer", 1, 0, false);
        when(flashcardManager.getFlashcardCount(1)).thenReturn(1);
        when(flashcardManager.getFlashcardsByMode(1, FilterMode.ALL)).thenReturn(Collections.singletonList(card));

        manager.startSession(1, false, FilterMode.ALL);

        assertEquals("Question", manager.getCurrentText());
        manager.flip();
        assertEquals("Answer", manager.getCurrentText());
        manager.flip();
        assertEquals("Question", manager.getCurrentText());
    }

    @Test
    public void setKnown_updatesPersistence() throws StudySessionException {
        Flashcard card = Flashcard.fromPersistence(1, "Q", "A", 1, 0, false);
        when(flashcardManager.getFlashcardCount(1)).thenReturn(1);
        when(flashcardManager.getFlashcardsByMode(1, FilterMode.ALL)).thenReturn(Collections.singletonList(card));
        when(persistence.updateFlashcard(any(Flashcard.class))).thenReturn(true);

        manager.startSession(1, false, FilterMode.ALL);
        manager.setKnown(true);

        assertTrue(manager.isCurrentCardKnown());
        verify(persistence).updateFlashcard(argThat(Flashcard::getIsKnown));
    }

    // ---------------- Progress & State ----------------

    @Test
    public void getProgressText_formatsCorrectly() throws StudySessionException {
        List<Flashcard> cards = createMockCards(2);
        when(flashcardManager.getFlashcardCount(1)).thenReturn(2);
        when(flashcardManager.getFlashcardsByMode(1, FilterMode.ALL)).thenReturn(cards);

        manager.startSession(1, false, FilterMode.ALL);
        assertEquals("Card 1 of 2", manager.getProgressText());

        manager.nextCard();
        assertEquals("Card 2 of 2", manager.getProgressText());

        manager.nextCard(); // Finished state
        assertEquals("Card 2 of 2", manager.getProgressText());
    }

    @Test
    public void hasCards_checksState() throws StudySessionException {
        when(flashcardManager.getFlashcardCount(1)).thenReturn(0);
        when(flashcardManager.getFlashcardsByMode(1, FilterMode.ALL)).thenReturn(Collections.emptyList());
        
        try {
            manager.startSession(1, false, FilterMode.ALL);
            fail("Should have thrown StudySessionException");
        } catch (StudySessionException ignored) {}

        List<Flashcard> cards = createMockCards(1);
        when(flashcardManager.getFlashcardCount(1)).thenReturn(1);
        when(flashcardManager.getFlashcardsByMode(1, FilterMode.ALL)).thenReturn(cards);
        manager.startSession(1, false, FilterMode.ALL);
        assertTrue(manager.hasCards());

        manager.nextCard();
        assertFalse(manager.hasCards());
    }
}
