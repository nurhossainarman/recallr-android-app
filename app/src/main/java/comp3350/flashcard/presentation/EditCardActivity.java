package comp3350.flashcard.presentation;
//Controller for activity_edit_card screen
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import comp3350.flashcard.R;
import comp3350.flashcard.objects.Flashcard;
import comp3350.flashcard.persistence.FlashcardPersistence;
import comp3350.flashcard.persistence.stubs.FlashcardPersistenceStub;

public class EditCardActivity extends AppCompatActivity {
    // UI elements
    private TextInputEditText inputCardFront;
    private TextInputEditText inputCardBack;
    private FlashcardPersistence flashcardPersistence;
    private int deckId = -1;
    private int cardId = -1;
    /** Called when the activity is first created.
     * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_card);
        // Initialize the persistence layer
        flashcardPersistence = new FlashcardPersistenceStub();

        //Find the views
        inputCardFront = findViewById(R.id.inputCardFront);
        inputCardBack = findViewById(R.id.inputCardBack);
        Button btnSaveCard = findViewById(R.id.btnSaveCard);

        // Check if an existing card is being edited
        if (getIntent().hasExtra("DECK_ID")) {
            deckId = getIntent().getIntExtra("DECK_ID", -1);
        }

        if (getIntent().hasExtra("CARD_ID")) {
            cardId = getIntent().getIntExtra("CARD_ID", -1);
            Flashcard card = flashcardPersistence.getFlashcardById(cardId);
            if (card != null) {
                inputCardFront.setText(card.getFront());
                inputCardBack.setText(card.getBack());
                deckId = card.getDeckId();
            }
        }
        // Set a click listener for the save button
        btnSaveCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCard();
            }
        });
    }

    private void saveCard() {
        // Take input from user
        String front = inputCardFront.getText().toString().trim();
        String back = inputCardBack.getText().toString().trim();
        //TODO: Validate input from logic layer
        // Check if input is valid
        if (front.isEmpty() || back.isEmpty()) {
            Toast.makeText(this, "Both sides are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cardId == -1) {
            Flashcard newCard = new Flashcard(front, back, deckId);
            flashcardPersistence.insertFlashcard(newCard);
            Toast.makeText(this, "Card created", Toast.LENGTH_SHORT).show();
        } else {
            Flashcard existingCard = flashcardPersistence.getFlashcardById(cardId);
            if (existingCard != null) {
                existingCard.setFront(front);
                existingCard.setBack(back);
                flashcardPersistence.updateFlashcard(existingCard);
                Toast.makeText(this, "Card updated", Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }
}
