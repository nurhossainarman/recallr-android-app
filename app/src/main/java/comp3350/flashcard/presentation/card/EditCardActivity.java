package comp3350.flashcard.presentation.card;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.textfield.TextInputEditText;
import comp3350.flashcard.R;
import comp3350.flashcard.application.Services;
import comp3350.flashcard.constants.ValidationConstants;
import comp3350.flashcard.logic.IFlashcardManager;
import comp3350.flashcard.logic.exceptions.FlashcardValidationException;
import comp3350.flashcard.objects.Flashcard;
import comp3350.flashcard.presentation.ActivityHelper;
import comp3350.flashcard.presentation.Messages;

/**
 * Controller for creating a new flashcard or editing a flashcard.
 */
public class EditCardActivity extends AppCompatActivity {

    private TextInputEditText inputCardFront;
    private TextInputEditText inputCardBack;
    private IFlashcardManager flashcardManager;
    private int deckId = ValidationConstants.INVALID_ID;
    private int cardId = ValidationConstants.INVALID_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_card);

        // Get the card manager for logic/business using helper(Services)
        flashcardManager = Services.getFlashcardManager();
        initUI();
    }

    /**
     * Initializing the UI.
     * Finds the input boxes and buttons on the screen.
     */
    private void initUI() {
        inputCardFront = findViewById(R.id.inputCardFront);
        inputCardBack = findViewById(R.id.inputCardBack);
        Button btnSaveCard = findViewById(R.id.btnSaveCard);
        Toolbar toolbar = findViewById(R.id.toolbar);

        // If intent returns a card ID, we are editing an existing card
        // Otherwise, we are creating a new card
        this.deckId = getIntent().getIntExtra("DECK_ID", ValidationConstants.INVALID_ID);
        this.cardId = getIntent().getIntExtra("CARD_ID", ValidationConstants.INVALID_ID);

        // Setup the mode of screen (adding or editing)
        setupMode(toolbar, btnSaveCard);

        // Save the card when the button is clicked
        btnSaveCard.setOnClickListener(v -> handleSave());
    }

    /**
     * Sets the text on the toolbar and button depending on if we are adding or editing a card.
     * @param toolbar The toolbar containing the deck name
     * @param  saveButton The save button object
     */
    private void setupMode(Toolbar toolbar, Button saveButton) {
        if (!ActivityHelper.isNew(this.cardId)) {
            // Intent returns a valid id, that means we are editing an existing card
            Flashcard card = flashcardManager.getFlashcard(cardId);
            if (card != null) {
                inputCardFront.setText(card.getFront());
                inputCardBack.setText(card.getBack());
                this.deckId = card.getDeckId();
                toolbar.setTitle(R.string.edit_card);
                saveButton.setText(R.string.save_card);
            }
        } else {
            // Intent returns INVALID_ID, that means we are creating a new card
            toolbar.setTitle(R.string.add_card);
            saveButton.setText(R.string.add_card);
        }
    }

    /**
     * Checks if the card has text on both sides and saves it.
     */
    private void handleSave() {
        String front = inputCardFront.getText().toString().trim();
        String back = inputCardBack.getText().toString().trim();

        try {
            // Check if the card is new or not
            if (ActivityHelper.isNew(this.cardId)) {
                if (flashcardManager.createFlashcard(front, back, deckId) != null) {
                    Messages.show(this, getString(R.string.card_added_prompt));
                    finish();
                }
            } else {
                if (flashcardManager.updateFlashcard(cardId, front, back)) {
                    Messages.show(this, getString(R.string.card_updated_prompt));
                    finish();
                }
            }
        } catch (FlashcardValidationException e) {
            Messages.show(this, e.getMessage());
        }
    }
}
