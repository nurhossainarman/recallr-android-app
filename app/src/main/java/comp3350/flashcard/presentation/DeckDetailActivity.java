package comp3350.flashcard.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;
import comp3350.flashcard.R;
import comp3350.flashcard.application.Services;
import comp3350.flashcard.logic.DeckManager;
import comp3350.flashcard.logic.FlashcardManager;
import comp3350.flashcard.objects.Deck;
import comp3350.flashcard.objects.Flashcard;

/**
 * DeckDetailActivity - Displays all cards inside a specific deck.
 */
public class DeckDetailActivity extends AppCompatActivity {

    private RecyclerView rvCards;
    private Adapter adapter;
    private DeckManager deckManager;
    private FlashcardManager flashcardManager;
    private int deckId = -1;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deck_detail);

        deckManager = Services.getDeckManager();
        flashcardManager = Services.getFlashcardManager();

        initUI();
    }

    private void initUI() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        rvCards = findViewById(R.id.rvCards);
        rvCards.setLayoutManager(new LinearLayoutManager(this));

        deckId = getIntent().getIntExtra("DECK_ID", -1);
        setupDeckInfo();

        findViewById(R.id.fabAddCard).setOnClickListener(v -> navigateToEditCard(-1));
        findViewById(R.id.btnStudy).setOnClickListener(v -> Toast.makeText(this, "Starting study...", Toast.LENGTH_SHORT).show());

        loadCards();
    }

    private void setupDeckInfo() {
        Deck deck = deckManager.getDeck(deckId);
        if (deck != null) {
            toolbar.setTitle(deck.getName());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCards();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void loadCards() {
        List<Flashcard> cards = flashcardManager.getFlashcardsByDeck(deckId);
        
        if (adapter == null) {
            // Setup the generic adapter for Flashcards
            adapter = new Adapter(cards, R.layout.item_card, (view, item) -> {
                Flashcard card = (Flashcard) item;
                ((TextView) view.findViewById(R.id.tvCardFront)).setText(card.getFront());
                ((TextView) view.findViewById(R.id.tvCardBack)).setText(card.getBack());

                view.findViewById(R.id.btnEditCard).setOnClickListener(v -> navigateToEditCard(card.getId()));
                view.findViewById(R.id.btnDeleteCard).setOnClickListener(v -> {
                    flashcardManager.deleteFlashcard(card.getId());
                    loadCards();
                });
            });
            rvCards.setAdapter(adapter);
        } else {
            adapter.updateItems(cards);
        }
    }

    private void navigateToEditCard(int cardId) {
        Intent intent = new Intent(this, EditCardActivity.class).putExtra("DECK_ID", deckId);
        if (cardId != -1) intent.putExtra("CARD_ID", cardId);
        startActivity(intent);
    }
}
