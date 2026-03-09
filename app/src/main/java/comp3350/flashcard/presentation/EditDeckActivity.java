package comp3350.flashcard.presentation;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.textfield.TextInputEditText;
import comp3350.flashcard.R;
import comp3350.flashcard.application.Services;
import comp3350.flashcard.logic.DeckManager;
import comp3350.flashcard.objects.Deck;

/**
 * Controls create a deck screen and edit an existing deck screen.
 * Sets the texts and button on the screen depending on
 * if the user is creating a deck or editing a deck.
 */
public class EditDeckActivity extends AppCompatActivity {

    private TextInputEditText inputDeckName;
    private DeckManager deckManager;
    private int deckId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_deck);

        // Get the deck manager for logic/business using helper(Services)
        deckManager = Services.getDeckManager();
        initUI();
    }

    /**
     * Initializing the UI.
     * Finds the input boxes and buttons on the screen.
     */
    private void initUI() {
        inputDeckName = findViewById(R.id.inputDeckName); //Find input field for deck name
        Button btnSaveDeck = findViewById(R.id.btnSaveDeck); //Find save button
        Toolbar toolbar = findViewById(R.id.toolbar); //Find toolbar(containing deck name)

        // If intent returns a deck ID, we are editing an existing deck
        // Otherwise, we are creating a new deck (deckId == -1)
        this.deckId = getIntent().getIntExtra("DECK_ID", -1);

        //Setup the mode of screen (adding or editing)
        setupMode(toolbar, btnSaveDeck);

        // Save name when the button is clicked
        btnSaveDeck.setOnClickListener(v -> handleSave());
    }

    /**
     * Sets the text on the toolbar and button depending on if we are adding or editing a deck.
     * @param toolbar The toolbar containing the deck name
     * @param  saveButton The save button object
     */
    private void setupMode(Toolbar toolbar, Button saveButton) {
        if (this.deckId != -1) {
            // Intent returns a valid id, that means we are editing an existing deck
            Deck deck = deckManager.getDeck(deckId);
            if (deck != null) {
                inputDeckName.setText(deck.getName());
                toolbar.setTitle(R.string.edit_deck);
                saveButton.setText(R.string.save_deck);
            }
        } else {
            // Intent returns -1, that means we are creating a new deck
            toolbar.setTitle(R.string.create_deck);
            saveButton.setText(R.string.create_deck);
        }
    }

    /**
     * Checks if the name is valid and saves the deck.
     */
    private void handleSave() {
        String name = inputDeckName.getText().toString().trim();
        boolean success;

        if (deckId == -1) {
            // Manager handles validation
            success = deckManager.createDeck(name, "") != null;
            if (success) {
                printToast(R.string.deck_added_prompt);
            }
        } else {
            // Manager handles validation
            success = deckManager.updateDeck(deckId, name, "");
            if (success) {
                printToast(R.string.deck_updated_prompt);
            }
        }

        if (success) {
            finish();
        }
        else {

            printToast(R.string.invalid_deck_name); 
        }
    }

    /**
     * Shows a quick message at the bottom of the screen.
     */
    private void printToast(int stringId) {
        Toast.makeText(this, getString(stringId), Toast.LENGTH_SHORT).show();
    }
}
