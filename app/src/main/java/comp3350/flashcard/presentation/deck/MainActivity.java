package comp3350.flashcard.presentation.deck;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import comp3350.flashcard.R;
import comp3350.flashcard.application.Services;
import comp3350.flashcard.logic.IDeckManager;
import comp3350.flashcard.objects.Deck;
import comp3350.flashcard.presentation.Adapter;
import comp3350.flashcard.presentation.viewmodel.DeckViewModel;

/**
 * MainActivity - Shows a list of all decks.
 */
public class MainActivity extends AppCompatActivity {

    private RecyclerView rvDecks;
    private Adapter<DeckViewModel> adapter;
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

        // Initialize adapter once with an empty list and the binder method
        adapter = new Adapter<>(new ArrayList<>(), R.layout.item_deck, this::bindDeckItem);
        rvDecks.setAdapter(adapter);

        loadDecks();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDecks();
    }

    private void loadDecks() {
        List<Deck> decks = deckManager.getAllDecks();
        List<DeckViewModel> viewModels = new ArrayList<>();

        for (Deck deck : decks) {
            int count = deckManager.getFlashcardCount(deck.getId());
            viewModels.add(new DeckViewModel(deck, count));
        }

        adapter.updateItems(viewModels);
    }

    /**
     * Binds a DeckViewModel to its UI representation.
     */
    private void bindDeckItem(View view, DeckViewModel deckVM) {
        ((TextView) view.findViewById(R.id.tvDeckTitle)).setText(deckVM.getName());
        ((TextView) view.findViewById(R.id.tvCardCount)).setText(deckVM.getCardCount() + " cards");

        // Action: Edit
        view.findViewById(R.id.btnEditDeck).setOnClickListener(v ->
                startActivity(new Intent(this, EditDeckActivity.class).putExtra("DECK_ID", deckVM.getId())));

        // Action: Delete
        view.findViewById(R.id.btnDeleteDeck).setOnClickListener(v -> {
            deckManager.deleteDeck(deckVM.getId());
            loadDecks();
        });

        // Action: View Details
        view.setOnClickListener(v ->
                startActivity(new Intent(this, DeckDetailActivity.class).putExtra("DECK_ID", deckVM.getId())));
    }
}
