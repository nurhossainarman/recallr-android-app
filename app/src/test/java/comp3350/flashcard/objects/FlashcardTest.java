package comp3350.flashcard.objects;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the Flashcard domain object.
 */
public class FlashcardTest {

    private Flashcard flashcard;

    @Before
    public void setUp() {
        flashcard = new Flashcard("What is 2+2?", "4", 1);
    }

    // ==================== Constructor Tests ====================

    @Test
    public void testDefaultConstructor_SetsCurrentTimestamp() {
        long before = System.currentTimeMillis();
        Flashcard card = new Flashcard();
        long after = System.currentTimeMillis();

        assertTrue(card.getCreatedAt() >= before && card.getCreatedAt() <= after);
    }

    @Test
    public void testThreeArgConstructor_SetsAllFields() {
        Flashcard card = new Flashcard("Front", "Back", 5);

        assertEquals("Front", card.getFront());
        assertEquals("Back", card.getBack());
        assertEquals(5, card.getDeckId());
    }

    @Test
    public void testFullConstructor_SetsAllFields() {
        long timestamp = 1234567890L;
        Flashcard card = new Flashcard(10, "Q", "A", 3, timestamp);

        assertEquals(10, card.getId());
        assertEquals("Q", card.getFront());
        assertEquals("A", card.getBack());
        assertEquals(3, card.getDeckId());
        assertEquals(timestamp, card.getCreatedAt());
    }

    // ==================== Validation Tests ====================

    @Test(expected = IllegalArgumentException.class)
    public void testSetFront_Null_ThrowsException() {
        new Flashcard(null, "Back", 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetFront_Empty_ThrowsException() {
        new Flashcard("", "Back", 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetFront_WhitespaceOnly_ThrowsException() {
        new Flashcard("   ", "Back", 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetBack_Null_ThrowsException() {
        new Flashcard("Front", null, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetBack_Empty_ThrowsException() {
        new Flashcard("Front", "", 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetDeckId_Negative_ThrowsException() {
        new Flashcard("Front", "Back", -1);
    }

    @Test
    public void testFrontAndBack_AreTrimmed() {
        Flashcard card = new Flashcard("  Question  ", "  Answer  ", 1);

        assertEquals("Question", card.getFront());
        assertEquals("Answer", card.getBack());
    }

    // ==================== Getter and Setter Tests ====================

    @Test
    public void testSetId_UpdatesId() {
        flashcard.setId(99);
        assertEquals(99, flashcard.getId());
    }

    @Test
    public void testSetFront_UpdatesFront() {
        flashcard.setFront("New Question");
        assertEquals("New Question", flashcard.getFront());
    }

    @Test
    public void testSetBack_UpdatesBack() {
        flashcard.setBack("New Answer");
        assertEquals("New Answer", flashcard.getBack());
    }

    @Test
    public void testSetDeckId_UpdatesDeckId() {
        flashcard.setDeckId(10);
        assertEquals(10, flashcard.getDeckId());
    }

    // ==================== Utility Method Tests ====================

    @Test
    public void testIsPersisted_IdZero_ReturnsFalse() {
        flashcard.setId(0);
        assertFalse(flashcard.isPersisted());
    }

    @Test
    public void testIsPersisted_IdPositive_ReturnsTrue() {
        flashcard.setId(5);
        assertTrue(flashcard.isPersisted());
    }

    @Test
    public void testWithUpdatedContent_CreatesNewFlashcard() {
        flashcard.setId(10);
        Flashcard updated = flashcard.withUpdatedContent("New Front", "New Back");

        assertEquals(10, updated.getId());
        assertEquals("New Front", updated.getFront());
        assertEquals("New Back", updated.getBack());
        assertEquals(flashcard.getDeckId(), updated.getDeckId());
        assertEquals(flashcard.getCreatedAt(), updated.getCreatedAt());
    }

    // ==================== Equals and HashCode Tests ====================

    @Test
    public void testEquals_SameId_ReturnsTrue() {
        Flashcard card1 = new Flashcard(1, "Q1", "A1", 1, 1000L);
        Flashcard card2 = new Flashcard(1, "Q2", "A2", 2, 2000L);

        assertEquals(card1, card2);
    }

    @Test
    public void testEquals_DifferentId_ReturnsFalse() {
        Flashcard card1 = new Flashcard(1, "Q", "A", 1, 1000L);
        Flashcard card2 = new Flashcard(2, "Q", "A", 1, 1000L);

        assertNotEquals(card1, card2);
    }

    @Test
    public void testEquals_Null_ReturnsFalse() {
        assertNotEquals(flashcard, null);
    }

    @Test
    public void testEquals_DifferentType_ReturnsFalse() {
        assertNotEquals(flashcard, "Not a flashcard");
    }

    @Test
    public void testHashCode_EqualFlashcards_SameHashCode() {
        Flashcard card1 = new Flashcard(5, "Q1", "A1", 1, 1000L);
        Flashcard card2 = new Flashcard(5, "Q2", "A2", 2, 2000L);

        assertEquals(card1.hashCode(), card2.hashCode());
    }

    // ==================== toString Tests ====================

    @Test
    public void testToString_ContainsAllInfo() {
        flashcard.setId(1);
        String result = flashcard.toString();

        assertTrue(result.contains("id=1"));
        assertTrue(result.contains("front='What is 2+2?'"));
        assertTrue(result.contains("back='4'"));
        assertTrue(result.contains("deckId=1"));
    }
}
