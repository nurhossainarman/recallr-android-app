package comp3350.flashcard.presentation.deck;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.textfield.TextInputEditText;
import comp3350.flashcard.R;
import comp3350.flashcard.application.Services;
import comp3350.flashcard.constants.ValidationConstants;
import comp3350.flashcard.logic.IDeckManager;
import comp3350.flashcard.logic.DeckValidationException;
import comp3350.flashcard.objects.Deck;

/**
 * Controls create a deck screen and edit an existing deck screen.
 * Sets the texts and button on the screen depending on
 * if the user is creating a deck or editing a deck.
 */
public class EditDeckActivity extends AppCompatActivity {

    private TextInputEditText inputDeckName;
    private IDeckManager deckManager;
    private int deckId = ValidationConstants.INVALID_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_deck);

        deckManager = Services.getDeckManager();
        initUI();
    }

    /**
     * Initializing the UI.
     * Finds the input boxes and buttons on the screen.
     */
    private void initUI() {
        inputDeckName = findViewById(R.id.inputDeckName);
        Button btnSaveDeck = findViewById(R.id.btnSaveDeck);
        Toolbar toolbar = findViewById(R.id.toolbar);

        // If intent returns a deck ID, we are editing an existing deck
        // Otherwise, we are creating a new deck
        this.deckId = getIntent().getIntExtra("DECK_ID", ValidationConstants.INVALID_ID);

        setupMode(toolbar, btnSaveDeck);

        btnSaveDeck.setOnClickListener(v -> handleSave());
    }

    /**
     * Sets the text on the toolbar and button depending on if we are adding or editing a deck.
     * @param toolbar The toolbar containing the deck name
     * @param  saveButton The save button object
     */
    private void setupMode(Toolbar toolbar, Button saveButton) {
        if (this.deckId != ValidationConstants.INVALID_ID) {
            // Intent returns a valid id, that means we are editing an existing deck
            Deck deck = deckManager.getDeck(deckId);
            if (deck != null) {
                inputDeckName.setText(deck.getName());
                toolbar.setTitle(R.string.edit_deck);
                saveButton.setText(R.string.save_deck);
            }
        } else {
            // Intent returns INVALID_ID, that means we are creating a new deck
            toolbar.setTitle(R.string.create_deck);
            saveButton.setText(R.string.create_deck);
        }
    }

    /**
     * Saves the deck using the logic layer, which handles validation.
     */
    private void handleSave() {
        String name = inputDeckName.getText().toString().trim();

        try {
            if (deckId == ValidationConstants.INVALID_ID) {
                if (deckManager.createDeck(name, "") != null) {
                    printToast(getString(R.string.deck_added_prompt));
                    finish();
                }
            } else {
                if (deckManager.updateDeck(deckId, name, "")) {
                    printToast(getString(R.string.deck_updated_prompt));
                    finish();
                }
            }
        } catch (DeckValidationException e) {
            printToast(e.getMessage());
        }
    }

    /**
     * Shows a quick message at the bottom of the screen.
     */
    private void printToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
