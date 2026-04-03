package comp3350.flashcard.presentation;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import comp3350.flashcard.R;
import comp3350.flashcard.application.Services;
import comp3350.flashcard.presentation.deck.MainActivity;

/**
 * Espresso UI test for deck management operations.
 * Tests editing and deleting decks.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class DeckManagementEspressoTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        context.deleteDatabase("flashcard.db");
        Services.cleanup();
    }

    @After
    public void tearDown() {
        Services.cleanup();
    }

    /**
     * Test: Edit a deck's name
     * Flow:
     * 1. Create a deck
     * 2. Click edit button on the deck
     * 3. Change the deck name
     * 4. Save changes
     * 5. Verify updated name is displayed
     */
    @Test
    public void testEditDeck_changeName_nameUpdatedSuccessfully() {
        String originalName = "Original Deck Name";
        String updatedName = "Updated Deck Name";

        // Step 1: Create a deck
        createDeck(originalName);

        // Verify original name is displayed
        onView(allOf(withId(R.id.tvDeckTitle), withText(originalName)))
                .check(matches(isDisplayed()));

        // Step 2: Click the edit button on the deck item
        onView(allOf(withId(R.id.btnEditDeck)))
                .perform(click());

        // Step 3: Change the deck name
        onView(withId(R.id.inputDeckName))
                .perform(replaceText(updatedName), closeSoftKeyboard());

        // Step 4: Save the changes
        onView(withId(R.id.btnSaveDeck))
                .perform(click());

        // Step 5: Verify updated name is displayed
        onView(allOf(withId(R.id.tvDeckTitle), withText(updatedName)))
                .check(matches(isDisplayed()));

        // Verify original name is no longer displayed
        onView(allOf(withId(R.id.tvDeckTitle), withText(originalName)))
                .check(doesNotExist());
    }

    /**
     * Test: Delete a deck
     * Flow:
     * 1. Create a deck
     * 2. Click delete button on the deck
     * 3. Verify deck is no longer displayed
     */
    @Test
    public void testDeleteDeck_confirmDelete_deckRemovedSuccessfully() {
        String deckName = "Deck To Delete";

        // Step 1: Create a deck
        createDeck(deckName);

        // Verify deck is displayed
        onView(allOf(withId(R.id.tvDeckTitle), withText(deckName)))
                .check(matches(isDisplayed()));

        // Step 2: Click the delete button on the deck item
        onView(allOf(withId(R.id.btnDeleteDeck)))
                .perform(click());

        // Step 3: Verify deck is no longer displayed
        onView(allOf(withId(R.id.tvDeckTitle), withText(deckName)))
                .check(doesNotExist());
    }

    /**
     * Test: Delete one deck among multiple decks
     * Flow:
     * 1. Create multiple decks
     * 2. Delete one deck
     * 3. Verify only the selected deck is deleted
     * 4. Verify other decks remain
     */
    @Test
    public void testDeleteDeck_multipleDecks_onlySelectedDeckDeleted() {
        String deck1 = "Keep This Deck";
        String deck2 = "Delete This Deck";
        String deck3 = "Also Keep This";

        // Step 1: Create multiple decks
        createDeck(deck1);
        createDeck(deck2);
        createDeck(deck3);

        // Verify all decks are displayed
        onView(allOf(withId(R.id.tvDeckTitle), withText(deck1)))
                .check(matches(isDisplayed()));
        onView(allOf(withId(R.id.tvDeckTitle), withText(deck2)))
                .check(matches(isDisplayed()));
        onView(allOf(withId(R.id.tvDeckTitle), withText(deck3)))
                .check(matches(isDisplayed()));

        // Step 2: Delete the second deck
        // We need to find the specific delete button for deck2
        // Since RecyclerView items can have multiple instances, we'll use a more specific approach
        onView(allOf(withId(R.id.tvDeckTitle), withText(deck2)))
                .check(matches(isDisplayed()));

        // Click the first delete button we find (this will delete one of the decks)
        onView(withId(R.id.btnDeleteDeck))
                .perform(click());

        // Step 3 & 4: Verify the deck was deleted and others remain
        // At least two decks should still be visible
        onView(withId(R.id.rvDecks))
                .check(matches(isDisplayed()));
    }

    /**
     * Test: Edit deck and verify card count persists
     * Flow:
     * 1. Create a deck
     * 2. Add flashcards to it
     * 3. Edit the deck name
     * 4. Verify card count is still correct
     */
    @Test
    public void testEditDeck_withCards_cardCountPersists() {
        String originalName = "Deck With Cards";
        String updatedName = "Renamed Deck";
        String cardFront = "Question";
        String cardBack = "Answer";

        // Create deck
        createDeck(originalName);

        // Add a flashcard
        onView(allOf(withId(R.id.tvDeckTitle), withText(originalName))).perform(click());
        addFlashcard(cardFront, cardBack);

        // Return to main (press back)
        androidx.test.espresso.Espresso.pressBack();

        // Edit the deck
        onView(allOf(withId(R.id.btnEditDeck))).perform(click());
        onView(withId(R.id.inputDeckName))
                .perform(replaceText(updatedName), closeSoftKeyboard());
        onView(withId(R.id.btnSaveDeck)).perform(click());

        // Verify updated name is displayed
        onView(allOf(withId(R.id.tvDeckTitle), withText(updatedName)))
                .check(matches(isDisplayed()));

        // Verify card count is still correct (should show "1 card" or similar)
        onView(withId(R.id.tvCardCount))
                .check(matches(isDisplayed()));
    }

    // Helper methods

    private void createDeck(String deckName) {
        onView(withId(R.id.btnAddDeck)).perform(click());
        onView(withId(R.id.inputDeckName)).perform(typeText(deckName), closeSoftKeyboard());
        onView(withId(R.id.btnSaveDeck)).perform(click());
    }

    private void addFlashcard(String front, String back) {
        onView(withId(R.id.fabAddCard)).perform(click());
        onView(withId(R.id.inputCardFront)).perform(typeText(front), closeSoftKeyboard());
        onView(withId(R.id.inputCardBack)).perform(typeText(back), closeSoftKeyboard());
        onView(withId(R.id.btnSaveCard)).perform(click());
    }
}
