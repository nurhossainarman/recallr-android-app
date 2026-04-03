package comp3350.flashcard.objects;

import static org.junit.Assert.*;
import org.junit.Test;
import comp3350.flashcard.constants.ValidationConstants;

/**
 * Unit tests for the Deck domain object.
 * Tests the factory methods and immutability principles.
 */
public class DeckTest {

    // ==================== Factory Method Tests ====================

    @Test
    public void testCreateNew_SetsCurrentTimestamp() {
        long before = System.currentTimeMillis();
        Deck newDeck = Deck.createNew("Test Deck", "Description");
        long after = System.currentTimeMillis();

        assertTrue(newDeck.getCreatedAt() >= before && newDeck.getCreatedAt() <= after);
        assertEquals(0, newDeck.getLastStudiedAt());
        assertEquals(ValidationConstants.INVALID_ID, newDeck.getId());
    }

    @Test
    public void testCreateNew_SetsNameAndDescription() {
        Deck deck = Deck.createNew("My Deck", "A test deck");

        assertEquals("My Deck", deck.getName());
        assertEquals("A test deck", deck.getDescription());
        assertFalse(deck.isPersisted());
    }

    @Test
    public void testCreateNew_NullDescription_BecomesEmptyString() {
        Deck deck = Deck.createNew("My Deck", null);

        assertEquals("My Deck", deck.getName());
        assertEquals("", deck.getDescription());
    }

    @Test
    public void testFromPersistence_SetsAllFields() {
        long createdAt = 1000L;
        long lastStudiedAt = 2000L;
        Deck deck = Deck.fromPersistence(5, "Full Deck", "Description", createdAt, lastStudiedAt);

        assertEquals(5, deck.getId());
        assertEquals("Full Deck", deck.getName());
        assertEquals("Description", deck.getDescription());
        assertEquals(createdAt, deck.getCreatedAt());
        assertEquals(lastStudiedAt, deck.getLastStudiedAt());
        assertTrue(deck.isPersisted());
    }

    // ==================== Immutability Tests ====================

    @Test
    public void testId_IsImmutable() {
        Deck deck = Deck.fromPersistence(10, "Test", "Desc", 1000L, 0L);
        assertEquals(10, deck.getId());
        // Cannot change ID - it's final
        // deck.setId(20); // This should not compile
    }

    @Test
    public void testCreatedAt_IsImmutable() {
        long createdAt = 1000L;
        Deck deck = Deck.fromPersistence(1, "Test", "Desc", createdAt, 0L);
        assertEquals(createdAt, deck.getCreatedAt());
        // Cannot change createdAt - it's final
        // deck.setCreatedAt(2000L); // This should not compile
    }

    // ==================== Getter and Setter Tests ====================

    @Test
    public void testSetName_UpdatesName() {
        Deck deck = Deck.createNew("Original", "Description");
        deck.setName("New Name");
        assertEquals("New Name", deck.getName());
    }

    @Test
    public void testSetDescription_UpdatesDescription() {
        Deck deck = Deck.createNew("Name", "Original Description");
        deck.setDescription("New Description");
        assertEquals("New Description", deck.getDescription());
    }

    @Test
    public void testSetDescription_Null_BecomesEmptyString() {
        Deck deck = Deck.createNew("Name", "Description");
        deck.setDescription(null);
        assertEquals("", deck.getDescription());
    }

    @Test
    public void testSetLastStudiedAt_UpdatesTimestamp() {
        Deck deck = Deck.createNew("Name", "Description");
        deck.setLastStudiedAt(5000L);
        assertEquals(5000L, deck.getLastStudiedAt());
    }

    // ==================== Utility Method Tests ====================

    @Test
    public void testIsPersisted_NewDeck_ReturnsFalse() {
        Deck deck = Deck.createNew("Test", "Description");
        assertFalse(deck.isPersisted());
    }

    @Test
    public void testIsPersisted_PersistedDeck_ReturnsTrue() {
        Deck deck = Deck.fromPersistence(1, "Test", "Desc", 1000L, 0L);
        assertTrue(deck.isPersisted());
    }

    @Test
    public void testHasBeenStudied_NeverStudied_ReturnsFalse() {
        Deck deck = Deck.createNew("Test", "Description");
        assertFalse(deck.hasBeenStudied());
    }

    @Test
    public void testHasBeenStudied_HasBeenStudied_ReturnsTrue() {
        Deck deck = Deck.fromPersistence(1, "Test", "Desc", 1000L, 2000L);
        assertTrue(deck.hasBeenStudied());
    }

    @Test
    public void testMarkAsStudied_UpdatesTimestamp() {
        Deck deck = Deck.createNew("Test", "Description");
        long before = System.currentTimeMillis();
        deck.markAsStudied();
        long after = System.currentTimeMillis();

        assertTrue(deck.getLastStudiedAt() >= before && deck.getLastStudiedAt() <= after);
        assertTrue(deck.hasBeenStudied());
    }

    @Test
    public void testWithUpdatedName_CreatesNewDeck() {
        Deck original = Deck.fromPersistence(10, "Original Name", "Description", 1000L, 2000L);
        Deck updated = original.withUpdatedName("Updated Name");

        // Verify new deck has updated name
        assertEquals(10, updated.getId());
        assertEquals("Updated Name", updated.getName());

        // Verify other fields preserved
        assertEquals(original.getDescription(), updated.getDescription());
        assertEquals(original.getCreatedAt(), updated.getCreatedAt());
        assertEquals(original.getLastStudiedAt(), updated.getLastStudiedAt());

        // Verify original unchanged
        assertEquals("Original Name", original.getName());
    }

    @Test
    public void testWithUpdatedDescription_CreatesNewDeck() {
        Deck original = Deck.fromPersistence(10, "Name", "Original Desc", 1000L, 2000L);
        Deck updated = original.withUpdatedDescription("Updated Desc");

        // Verify new deck has updated description
        assertEquals(10, updated.getId());
        assertEquals("Updated Desc", updated.getDescription());

        // Verify other fields preserved
        assertEquals(original.getName(), updated.getName());
        assertEquals(original.getCreatedAt(), updated.getCreatedAt());
        assertEquals(original.getLastStudiedAt(), updated.getLastStudiedAt());

        // Verify original unchanged
        assertEquals("Original Desc", original.getDescription());
    }

    // ==================== Equals and HashCode Tests ====================

    @Test
    public void testEquals_SameId_ReturnsTrue() {
        Deck deck1 = Deck.fromPersistence(1, "Deck 1", "Desc 1", 1000L, 2000L);
        Deck deck2 = Deck.fromPersistence(1, "Deck 2", "Desc 2", 3000L, 4000L);

        assertEquals(deck1, deck2);
    }

    @Test
    public void testEquals_DifferentId_ReturnsFalse() {
        Deck deck1 = Deck.fromPersistence(1, "Deck", "Desc", 1000L, 2000L);
        Deck deck2 = Deck.fromPersistence(2, "Deck", "Desc", 1000L, 2000L);

        assertNotEquals(deck1, deck2);
    }

    @Test
    public void testEquals_Null_ReturnsFalse() {
        Deck deck = Deck.createNew("Test", "Description");
        assertNotEquals(deck, null);
    }

    @Test
    public void testEquals_DifferentType_ReturnsFalse() {
        Deck deck = Deck.createNew("Test", "Description");
        assertNotEquals(deck, "Not a deck");
    }

    @Test
    public void testEquals_SameReference_ReturnsTrue() {
        Deck deck = Deck.createNew("Test", "Description");
        assertEquals(deck, deck);
    }

    @Test
    public void testHashCode_EqualDecks_SameHashCode() {
        Deck deck1 = Deck.fromPersistence(5, "Deck 1", "Desc 1", 1000L, 2000L);
        Deck deck2 = Deck.fromPersistence(5, "Deck 2", "Desc 2", 3000L, 4000L);

        assertEquals(deck1.hashCode(), deck2.hashCode());
    }

    // ==================== toString Tests ====================

    @Test
    public void testToString_ContainsAllInfo() {
        Deck deck = Deck.fromPersistence(1, "Test Deck", "Description", 1000L, 2000L);
        String result = deck.toString();

        assertTrue(result.contains("id=1"));
        assertTrue(result.contains("name='Test Deck'"));
        assertTrue(result.contains("description='Description'"));
        assertTrue(result.contains("createdAt=1000"));
        assertTrue(result.contains("lastStudiedAt=2000"));
    }
}
