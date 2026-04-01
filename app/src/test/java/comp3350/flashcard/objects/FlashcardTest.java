package comp3350.flashcard.objects;

import static org.junit.Assert.*;
import org.junit.Test;
import comp3350.flashcard.constants.ValidationConstants;

/**
 * Unit tests for the Flashcard domain object.
 * Tests the factory methods and immutability principles.
 */
public class FlashcardTest {

    // ==================== Factory Method Tests ====================

    @Test
    public void testCreateNew_SetsCurrentTimestamp() {
        long before = System.currentTimeMillis();
        Flashcard card = Flashcard.createNew("Front", "Back", 1);
        long after = System.currentTimeMillis();

        assertTrue(card.getCreatedAt() >= before && card.getCreatedAt() <= after);
        assertFalse(card.getIsKnown());
        assertEquals(ValidationConstants.INVALID_ID, card.getId());
        assertFalse(card.isPersisted());
    }

    @Test
    public void testCreateNew_SetsAllFields() {
        Flashcard card = Flashcard.createNew("Question", "Answer", 5);

        assertEquals("Question", card.getFront());
        assertEquals("Answer", card.getBack());
        assertEquals(5, card.getDeckId());
        assertFalse(card.getIsKnown());
        assertFalse(card.isPersisted());
    }

    @Test
    public void testFromPersistence_SetsAllFields() {
        long timestamp = 1234567890L;
        Flashcard card = Flashcard.fromPersistence(10, "Q", "A", 3, timestamp, true);

        assertEquals(10, card.getId());
        assertEquals("Q", card.getFront());
        assertEquals("A", card.getBack());
        assertEquals(3, card.getDeckId());
        assertEquals(timestamp, card.getCreatedAt());
        assertTrue(card.getIsKnown());
        assertTrue(card.isPersisted());
    }

    @Test
    public void testFromPersistence_WithUnknownCard() {
        Flashcard card = Flashcard.fromPersistence(1, "Front", "Back", 2, 1000L, false);

        assertFalse(card.getIsKnown());
        assertTrue(card.isPersisted());
    }

    // ==================== Immutability Tests ====================

    @Test
    public void testId_IsImmutable() {
        Flashcard card = Flashcard.fromPersistence(10, "Q", "A", 1, 1000L, false);
        assertEquals(10, card.getId());
        // Cannot change ID - it's final
        // card.setId(20); // This should not compile
    }

    @Test
    public void testDeckId_IsImmutable() {
        Flashcard card = Flashcard.fromPersistence(1, "Q", "A", 5, 1000L, false);
        assertEquals(5, card.getDeckId());
        // Cannot change deckId - it's final
        // card.setDeckId(10); // This should not compile
    }

    @Test
    public void testCreatedAt_IsImmutable() {
        long createdAt = 1234567890L;
        Flashcard card = Flashcard.fromPersistence(1, "Q", "A", 1, createdAt, false);
        assertEquals(createdAt, card.getCreatedAt());
        // Cannot change createdAt - it's final
        // card.setCreatedAt(999L); // This should not compile
    }

    // ==================== Getter and Setter Tests ====================

    @Test
    public void testSetFront_UpdatesFront() {
        Flashcard card = Flashcard.createNew("Original", "Answer", 1);
        card.setFront("New Question");
        assertEquals("New Question", card.getFront());
    }

    @Test
    public void testSetBack_UpdatesBack() {
        Flashcard card = Flashcard.createNew("Question", "Original", 1);
        card.setBack("New Answer");
        assertEquals("New Answer", card.getBack());
    }

    @Test
    public void testSetIsKnown_UpdatesIsKnown() {
        Flashcard card = Flashcard.createNew("Q", "A", 1);
        assertFalse(card.getIsKnown());

        card.setIsKnown(true);
        assertTrue(card.getIsKnown());

        card.setIsKnown(false);
        assertFalse(card.getIsKnown());
    }

    // ==================== Utility Method Tests ====================

    @Test
    public void testIsPersisted_NewFlashcard_ReturnsFalse() {
        Flashcard card = Flashcard.createNew("Q", "A", 1);
        assertFalse(card.isPersisted());
    }

    @Test
    public void testIsPersisted_PersistedFlashcard_ReturnsTrue() {
        Flashcard card = Flashcard.fromPersistence(5, "Q", "A", 1, 1000L, false);
        assertTrue(card.isPersisted());
    }

    @Test
    public void testWithUpdatedContent_CreatesNewFlashcard() {
        Flashcard original = Flashcard.fromPersistence(10, "Old Front", "Old Back", 2, 1234L, true);
        Flashcard updated = original.withUpdatedContent("New Front", "New Back");

        // Verify new flashcard has updated content
        assertEquals(10, updated.getId());
        assertEquals("New Front", updated.getFront());
        assertEquals("New Back", updated.getBack());

        // Verify other fields preserved
        assertEquals(original.getDeckId(), updated.getDeckId());
        assertEquals(original.getCreatedAt(), updated.getCreatedAt());
        assertEquals(original.getIsKnown(), updated.getIsKnown());

        // Verify original unchanged
        assertEquals("Old Front", original.getFront());
        assertEquals("Old Back", original.getBack());
    }

    // ==================== Equals and HashCode Tests ====================

    @Test
    public void testEquals_SameId_ReturnsTrue() {
        Flashcard card1 = Flashcard.fromPersistence(1, "Q1", "A1", 1, 1000L, false);
        Flashcard card2 = Flashcard.fromPersistence(1, "Q2", "A2", 2, 2000L, true);

        assertEquals(card1, card2);
    }

    @Test
    public void testEquals_DifferentId_ReturnsFalse() {
        Flashcard card1 = Flashcard.fromPersistence(1, "Q", "A", 1, 1000L, false);
        Flashcard card2 = Flashcard.fromPersistence(2, "Q", "A", 1, 1000L, false);

        assertNotEquals(card1, card2);
    }

    @Test
    public void testEquals_Null_ReturnsFalse() {
        Flashcard card = Flashcard.createNew("Q", "A", 1);
        assertNotEquals(card, null);
    }

    @Test
    public void testEquals_DifferentType_ReturnsFalse() {
        Flashcard card = Flashcard.createNew("Q", "A", 1);
        assertNotEquals(card, "Not a flashcard");
    }

    @Test
    public void testEquals_SameReference_ReturnsTrue() {
        Flashcard card = Flashcard.createNew("Q", "A", 1);
        assertEquals(card, card);
    }

    @Test
    public void testHashCode_EqualFlashcards_SameHashCode() {
        Flashcard card1 = Flashcard.fromPersistence(5, "Q1", "A1", 1, 1000L, false);
        Flashcard card2 = Flashcard.fromPersistence(5, "Q2", "A2", 2, 2000L, true);

        assertEquals(card1.hashCode(), card2.hashCode());
    }

    // ==================== toString Tests ====================

    @Test
    public void testToString_ContainsAllInfo() {
        Flashcard card = Flashcard.fromPersistence(1, "What is 2+2?", "4", 5, 1000L, false);
        String result = card.toString();

        assertTrue(result.contains("id=1"));
        assertTrue(result.contains("front='What is 2+2?'"));
        assertTrue(result.contains("back='4'"));
        assertTrue(result.contains("deckId=5"));
        assertTrue(result.contains("createdAt=1000"));
        assertTrue(result.contains("isKnown=false"));
    }
}
