package comp3350.flashcard.objects;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the Deck domain object.
 */
public class DeckTest {

    private Deck deck;

    @Before
    public void setUp() {
        deck = new Deck("Test Deck");
    }

    // ==================== Constructor Tests ====================

    @Test
    public void testDefaultConstructor_SetsCurrentTimestamp() {
        long before = System.currentTimeMillis();
        Deck newDeck = new Deck();
        long after = System.currentTimeMillis();

        assertTrue(newDeck.getCreatedAt() >= before && newDeck.getCreatedAt() <= after);
        assertEquals(0, newDeck.getLastStudiedAt());
        assertEquals(0, newDeck.getCardCount());
    }

    @Test
    public void testSingleArgConstructor_SetsName() {
        Deck newDeck = new Deck("My Deck");
        assertEquals("My Deck", newDeck.getName());
    }

    @Test
    public void testTwoArgConstructor_SetsNameAndDescription() {
        Deck newDeck = new Deck("My Deck", "A test deck");

        assertEquals("My Deck", newDeck.getName());
        assertEquals("A test deck", newDeck.getDescription());
    }

    @Test
    public void testFullConstructor_SetsAllFields() {
        long createdAt = 1000L;
        long lastStudiedAt = 2000L;
        Deck newDeck = new Deck(5, "Full Deck", "Description", createdAt, lastStudiedAt);

        assertEquals(5, newDeck.getId());
        assertEquals("Full Deck", newDeck.getName());
        assertEquals("Description", newDeck.getDescription());
        assertEquals(createdAt, newDeck.getCreatedAt());
        assertEquals(lastStudiedAt, newDeck.getLastStudiedAt());
    }

    // ==================== Validation Tests ====================

    @Test
    public void testSetName_ExactlyMaxLength_Succeeds() {
        String maxName = new String(new char[100]).replace('\0', 'A');
        Deck newDeck = new Deck(maxName);
        assertEquals(100, newDeck.getName().length());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetDescription_TooLong_ThrowsException() {
        String longDescription = new String(new char[501]).replace('\0', 'A');
        new Deck("Deck", longDescription);
    }

    @Test
    public void testSetDescription_Null_BecomesEmptyString() {
        deck.setDescription(null);
        assertEquals("", deck.getDescription());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetCardCount_Negative_ThrowsException() {
        deck.setCardCount(-1);
    }

    @Test
    public void testSetName_Trimmed() {
        Deck newDeck = new Deck("  Trimmed Name  ");
        assertEquals("Trimmed Name", newDeck.getName());
    }

    // ==================== Getter and Setter Tests ====================

    @Test
    public void testSetId_UpdatesId() {
        deck.setId(42);
        assertEquals(42, deck.getId());
    }

    @Test
    public void testSetName_UpdatesName() {
        deck.setName("New Name");
        assertEquals("New Name", deck.getName());
    }

    @Test
    public void testSetDescription_UpdatesDescription() {
        deck.setDescription("New Description");
        assertEquals("New Description", deck.getDescription());
    }

    @Test
    public void testSetCardCount_UpdatesCardCount() {
        deck.setCardCount(10);
        assertEquals(10, deck.getCardCount());
    }

    @Test
    public void testSetLastStudiedAt_UpdatesTimestamp() {
        deck.setLastStudiedAt(5000L);
        assertEquals(5000L, deck.getLastStudiedAt());
    }

    // ==================== Utility Method Tests ====================

    @Test
    public void testIsPersisted_IdZero_ReturnsFalse() {
        deck.setId(0);
        assertFalse(deck.isPersisted());
    }

    @Test
    public void testIsPersisted_IdPositive_ReturnsTrue() {
        deck.setId(1);
        assertTrue(deck.isPersisted());
    }

    @Test
    public void testHasCards_ZeroCards_ReturnsFalse() {
        deck.setCardCount(0);
        assertFalse(deck.hasCards());
    }

    @Test
    public void testHasCards_PositiveCards_ReturnsTrue() {
        deck.setCardCount(5);
        assertTrue(deck.hasCards());
    }

    @Test
    public void testHasBeenStudied_NeverStudied_ReturnsFalse() {
        deck.setLastStudiedAt(0);
        assertFalse(deck.hasBeenStudied());
    }

    @Test
    public void testHasBeenStudied_HasBeenStudied_ReturnsTrue() {
        deck.setLastStudiedAt(1000L);
        assertTrue(deck.hasBeenStudied());
    }

    @Test
    public void testMarkAsStudied_UpdatesTimestamp() {
        long before = System.currentTimeMillis();
        deck.markAsStudied();
        long after = System.currentTimeMillis();

        assertTrue(deck.getLastStudiedAt() >= before && deck.getLastStudiedAt() <= after);
    }

    @Test
    public void testWithUpdatedName_CreatesNewDeck() {
        deck.setId(10);
        deck.setCardCount(5);
        Deck updated = deck.withUpdatedName("Updated Name");

        assertEquals(10, updated.getId());
        assertEquals("Updated Name", updated.getName());
        assertEquals(deck.getDescription(), updated.getDescription());
        assertEquals(deck.getCreatedAt(), updated.getCreatedAt());
        assertEquals(deck.getCardCount(), updated.getCardCount());
    }

    // ==================== Equals and HashCode Tests ====================

    @Test
    public void testEquals_SameId_ReturnsTrue() {
        Deck deck1 = new Deck(1, "Deck 1", "Desc 1", 1000L, 2000L);
        Deck deck2 = new Deck(1, "Deck 2", "Desc 2", 3000L, 4000L);

        assertEquals(deck1, deck2);
    }

    @Test
    public void testEquals_DifferentId_ReturnsFalse() {
        Deck deck1 = new Deck(1, "Deck", "Desc", 1000L, 2000L);
        Deck deck2 = new Deck(2, "Deck", "Desc", 1000L, 2000L);

        assertNotEquals(deck1, deck2);
    }

    @Test
    public void testEquals_Null_ReturnsFalse() {
        assertNotEquals(deck, null);
    }

    @Test
    public void testEquals_DifferentType_ReturnsFalse() {
        assertNotEquals(deck, "Not a deck");
    }

    @Test
    public void testHashCode_EqualDecks_SameHashCode() {
        Deck deck1 = new Deck(5, "Deck 1", "Desc 1", 1000L, 2000L);
        Deck deck2 = new Deck(5, "Deck 2", "Desc 2", 3000L, 4000L);

        assertEquals(deck1.hashCode(), deck2.hashCode());
    }

    // ==================== toString Tests ====================

    @Test
    public void testToString_ContainsAllInfo() {
        deck.setId(1);
        deck.setCardCount(10);
        String result = deck.toString();

        assertTrue(result.contains("id=1"));
        assertTrue(result.contains("name='Test Deck'"));
        assertTrue(result.contains("cardCount=10"));
    }
}
