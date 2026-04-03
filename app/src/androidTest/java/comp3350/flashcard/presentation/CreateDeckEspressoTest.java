package comp3350.flashcard.presentation;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
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
 * Espresso UI test for creating a deck.
 * Tests the complete user flow: MainActivity -> EditDeckActivity -> Create Deck -> Return to MainActivity
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class CreateDeckEspressoTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        // Clear database before each test for clean state
        context.deleteDatabase("flashcard.db");
        // Reinitialize services to get fresh database
        Services.cleanup();
    }

    @After
    public void tearDown() {
        Services.cleanup();
    }

    /**
     * Test: Create a new deck with valid name
     * Flow:
     * 1. Click FAB button to create new deck
     * 2. Enter deck name
     * 3. Click save
     * 4. Verify returned to main activity
     * 5. Verify deck appears in list
     */
    @Test
    public void testCreateDeck_validName_deckCreatedSuccessfully() {
        String deckName = "Test Deck UI";

        // Step 1: Click the floating action button to add a new deck
        onView(withId(R.id.btnAddDeck))
                .perform(click());

        // Step 2: Enter deck name in the EditDeckActivity
        onView(withId(R.id.inputDeckName))
                .perform(typeText(deckName), closeSoftKeyboard());

        // Step 3: Click save button
        onView(withId(R.id.btnSaveDeck))
                .perform(click());

        // Step 4 & 5: Verify we're back on MainActivity and the deck is displayed
        // We should see the new deck name in the RecyclerView
        onView(allOf(withId(R.id.tvDeckTitle), withText(deckName)))
                .check(matches(isDisplayed()));
    }

    /**
     * Test: Create multiple decks
     * Verifies that multiple decks can be created in sequence
     */
    @Test
    public void testCreateDeck_multipleDecks_allDecksDisplayed() {
        String deck1 = "First Deck";
        String deck2 = "Second Deck";

        // Create first deck
        onView(withId(R.id.btnAddDeck)).perform(click());
        onView(withId(R.id.inputDeckName)).perform(typeText(deck1), closeSoftKeyboard());
        onView(withId(R.id.btnSaveDeck)).perform(click());

        // Verify first deck is displayed
        onView(allOf(withId(R.id.tvDeckTitle), withText(deck1)))
                .check(matches(isDisplayed()));

        // Create second deck
        onView(withId(R.id.btnAddDeck)).perform(click());
        onView(withId(R.id.inputDeckName)).perform(typeText(deck2), closeSoftKeyboard());
        onView(withId(R.id.btnSaveDeck)).perform(click());

        // Verify both decks are displayed
        onView(allOf(withId(R.id.tvDeckTitle), withText(deck1)))
                .check(matches(isDisplayed()));
        onView(allOf(withId(R.id.tvDeckTitle), withText(deck2)))
                .check(matches(isDisplayed()));
    }

    /**
     * Test: Create deck with long name
     * Verifies that deck names with multiple words are handled correctly
     */
    @Test
    public void testCreateDeck_longName_deckCreatedSuccessfully() {
        String longDeckName = "My Advanced Spanish Vocabulary Deck";

        onView(withId(R.id.btnAddDeck)).perform(click());
        onView(withId(R.id.inputDeckName))
                .perform(typeText(longDeckName), closeSoftKeyboard());
        onView(withId(R.id.btnSaveDeck)).perform(click());

        // Verify the deck with long name is displayed
        onView(allOf(withId(R.id.tvDeckTitle), withText(longDeckName)))
                .check(matches(isDisplayed()));
    }
}
