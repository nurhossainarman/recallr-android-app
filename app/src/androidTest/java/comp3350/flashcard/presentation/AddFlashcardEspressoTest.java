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
 * Espresso UI test for adding flashcards to a deck.
 * Tests the complete user flow: Create Deck -> Open Deck -> Add Flashcard -> Verify Flashcard Displayed
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class AddFlashcardEspressoTest {

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
     * Test: Add a flashcard to a deck
     * Flow:
     * 1. Create a new deck
     * 2. Click on the deck to open it
     * 3. Click FAB to add flashcard
     * 4. Enter front and back text
     * 5. Save flashcard
     * 6. Verify flashcard appears in the deck
     */
    @Test
    public void testAddFlashcard_validContent_flashcardAddedSuccessfully() {
        String deckName = "Test Deck for Cards";
        String cardFront = "What is Java?";
        String cardBack = "A programming language";

        // Step 1: Create a deck first
        createDeck(deckName);

        // Step 2: Click on the deck to open DeckDetailActivity
        onView(allOf(withId(R.id.tvDeckTitle), withText(deckName)))
                .perform(click());

        // Step 3: Click FAB to add a flashcard
        onView(withId(R.id.fabAddCard))
                .perform(click());

        // Step 4: Enter flashcard content
        onView(withId(R.id.inputCardFront))
                .perform(typeText(cardFront), closeSoftKeyboard());
        onView(withId(R.id.inputCardBack))
                .perform(typeText(cardBack), closeSoftKeyboard());

        // Step 5: Save the flashcard
        onView(withId(R.id.btnSaveCard))
                .perform(click());

        // Step 6: Verify flashcard is displayed in the RecyclerView
        // The card's front text should be visible
        onView(withText(cardFront))
                .check(matches(isDisplayed()));
    }

    /**
     * Test: Add multiple flashcards to a deck
     * Verifies that multiple flashcards can be added to the same deck
     */
    @Test
    public void testAddFlashcard_multipleCards_allCardsDisplayed() {
        String deckName = "Multi-Card Deck";
        String card1Front = "Question 1";
        String card1Back = "Answer 1";
        String card2Front = "Question 2";
        String card2Back = "Answer 2";

        // Create deck and open it
        createDeck(deckName);
        onView(allOf(withId(R.id.tvDeckTitle), withText(deckName))).perform(click());

        // Add first flashcard
        onView(withId(R.id.fabAddCard)).perform(click());
        onView(withId(R.id.inputCardFront)).perform(typeText(card1Front), closeSoftKeyboard());
        onView(withId(R.id.inputCardBack)).perform(typeText(card1Back), closeSoftKeyboard());
        onView(withId(R.id.btnSaveCard)).perform(click());

        // Add second flashcard
        onView(withId(R.id.fabAddCard)).perform(click());
        onView(withId(R.id.inputCardFront)).perform(typeText(card2Front), closeSoftKeyboard());
        onView(withId(R.id.inputCardBack)).perform(typeText(card2Back), closeSoftKeyboard());
        onView(withId(R.id.btnSaveCard)).perform(click());

        // Verify both flashcards are displayed
        onView(withText(card1Front)).check(matches(isDisplayed()));
        onView(withText(card2Front)).check(matches(isDisplayed()));
    }

    /**
     * Test: Add flashcard with long content
     * Verifies that flashcards with longer text are handled correctly
     */
    @Test
    public void testAddFlashcard_longContent_flashcardAddedSuccessfully() {
        String deckName = "Long Content Deck";
        String longFront = "Explain the concept of polymorphism in object-oriented programming";
        String longBack = "Polymorphism allows objects of different types to be treated as objects of a common type";

        createDeck(deckName);
        onView(allOf(withId(R.id.tvDeckTitle), withText(deckName))).perform(click());

        onView(withId(R.id.fabAddCard)).perform(click());
        onView(withId(R.id.inputCardFront)).perform(typeText(longFront), closeSoftKeyboard());
        onView(withId(R.id.inputCardBack)).perform(typeText(longBack), closeSoftKeyboard());
        onView(withId(R.id.btnSaveCard)).perform(click());

        // Verify the flashcard is displayed
        onView(withText(longFront)).check(matches(isDisplayed()));
    }

    /**
     * Test: Filter buttons are displayed and can be clicked
     * Flow:
     * 1. Create deck with flashcards
     * 2. Open deck detail
     * 3. Verify filter chips are displayed
     * 4. Click different filter options
     */
    @Test
    public void testDeckDetail_filterChips_areDisplayedAndClickable() {
        String deckName = "Filter Test Deck";
        String cardFront = "Test Question";
        String cardBack = "Test Answer";

        // Setup: Create deck with flashcard
        createDeck(deckName);
        onView(allOf(withId(R.id.tvDeckTitle), withText(deckName))).perform(click());
        addFlashcard(cardFront, cardBack);

        // Verify filter chips are displayed
        onView(withId(R.id.rbAll)).check(matches(isDisplayed()));
        onView(withId(R.id.rbKnown)).check(matches(isDisplayed()));
        onView(withId(R.id.rbUnknown)).check(matches(isDisplayed()));

        // Click on Known filter
        onView(withId(R.id.rbKnown)).perform(click());

        // Click on Unknown filter
        onView(withId(R.id.rbUnknown)).perform(click());

        // Click back to All filter
        onView(withId(R.id.rbAll)).perform(click());

        // Verify flashcard is still displayed (filter to All shows all cards)
        onView(withText(cardFront)).check(matches(isDisplayed()));
    }

    /**
     * Helper method to create a deck
     */
    private void createDeck(String deckName) {
        onView(withId(R.id.btnAddDeck)).perform(click());
        onView(withId(R.id.inputDeckName)).perform(typeText(deckName), closeSoftKeyboard());
        onView(withId(R.id.btnSaveDeck)).perform(click());
    }

    /**
     * Helper method to add a flashcard
     */
    private void addFlashcard(String front, String back) {
        onView(withId(R.id.fabAddCard)).perform(click());
        onView(withId(R.id.inputCardFront)).perform(typeText(front), closeSoftKeyboard());
        onView(withId(R.id.inputCardBack)).perform(typeText(back), closeSoftKeyboard());
        onView(withId(R.id.btnSaveCard)).perform(click());
    }
}
