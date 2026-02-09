// Controller for activity_edit_deck screen
package comp3350.flashcard.presentation;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.textfield.TextInputEditText;
import comp3350.flashcard.R;
import comp3350.flashcard.application.Services;
import comp3350.flashcard.objects.Deck;
import comp3350.flashcard.persistence.DeckPersistence;

public class EditDeckActivity extends AppCompatActivity {

    private TextInputEditText inputDeckName;
    private DeckPersistence deckPersistence;
    private int deckId = -1;
    //Called when the activity is first created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_deck);

        deckPersistence = Services.getDeckPersistence();
        initUI();
    }

    private void initUI() {
        inputDeckName = findViewById(R.id.inputDeckName);
        Button btnSaveDeck = findViewById(R.id.btnSaveDeck);
        Toolbar toolbar = findViewById(R.id.toolbar);

        deckId = getIntent().getIntExtra("DECK_ID", -1);
        setupMode(toolbar, btnSaveDeck);

        btnSaveDeck.setOnClickListener(v -> handleSave());
    }

    private void setupMode(Toolbar toolbar, Button saveButton) {
        if (deckId != -1) {
            Deck deck = deckPersistence.getDeckById(deckId);
            if (deck != null) {
                inputDeckName.setText(deck.getName());
                toolbar.setTitle(R.string.edit_deck);
                saveButton.setText(R.string.save_deck);
            }
        } else {
            toolbar.setTitle(R.string.create_deck);
            saveButton.setText(R.string.create_deck);
        }
    }

    private void handleSave() {
        String name = inputDeckName.getText().toString().trim();
        if (validate(name)) {
            if (deckId == -1) {
                deckPersistence.insertDeck(new Deck(name));
                showToast("Deck created");
            } else {
                updateExistingDeck(name);
            }
            finish();
        }
    }

    private boolean validate(String name) {
        if (name.isEmpty()) {
            inputDeckName.setError("Name is required");
            return false;
        }
        return true;
    }

    private void updateExistingDeck(String name) {
        Deck deck = deckPersistence.getDeckById(deckId);
        if (deck != null) {
            deck.setName(name);
            deckPersistence.updateDeck(deck);
            showToast("Deck updated");
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
