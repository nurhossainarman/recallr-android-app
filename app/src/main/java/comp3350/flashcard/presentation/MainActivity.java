package comp3350.flashcard.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;
import comp3350.flashcard.R;
import comp3350.flashcard.application.Services;
import comp3350.flashcard.logic.IDeckManager;
import comp3350.flashcard.objects.Deck;

/**
 * MainActivity - Shows a list of all decks.
 */
public class MainActivity extends AppCompatActivity {

    private RecyclerView rvDecks;
    private Adapter adapter;
    private IDeckManager deckManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Services.setContext(this);

        setContentView(R.layout.activity_main);

        deckManager = Services.getDeckManager();
        initUI();
    }

    private void initUI() {
        rvDecks = findViewById(R.id.rvDecks);
        rvDecks.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton btnAddDeck = findViewById(R.id.btnAddDeck);
        btnAddDeck.setOnClickListener(v -> startActivity(new Intent(this, EditDeckActivity.class)));

        loadDecks();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDecks();
    }

    private void loadDecks() {
        List<Deck> decks = deckManager.getAllDecks();
        for (Deck deck : decks) {
            deck.setCardCount(deckManager.getFlashcardCount(deck.getId()));
        }

        if (adapter == null) {
            // Setup the generic adapter for Decks
            adapter = new Adapter(decks, R.layout.item_deck, (view, item) -> {
                Deck deck = (Deck) item;
                ((TextView) view.findViewById(R.id.tvDeckTitle)).setText(deck.getName());
                ((TextView) view.findViewById(R.id.tvCardCount)).setText(deck.getCardCount() + " cards");
                
                view.findViewById(R.id.btnEditDeck).setOnClickListener(v -> 
                    startActivity(new Intent(this, EditDeckActivity.class).putExtra("DECK_ID", deck.getId())));
                
                view.findViewById(R.id.btnDeleteDeck).setOnClickListener(v -> {
                    deckManager.deleteDeck(deck.getId());
                    loadDecks();
                });

                view.setOnClickListener(v -> 
                    startActivity(new Intent(this, DeckDetailActivity.class).putExtra("DECK_ID", deck.getId())));
            });
            rvDecks.setAdapter(adapter);
        } else {
            adapter.updateItems(decks);
        }
    }
}
