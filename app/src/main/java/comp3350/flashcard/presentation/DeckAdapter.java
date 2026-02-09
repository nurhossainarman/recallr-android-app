package comp3350.flashcard.presentation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import comp3350.flashcard.R;
import comp3350.flashcard.objects.Deck;

public class DeckAdapter extends RecyclerView.Adapter<DeckAdapter.DeckViewHolder> {

    private List<Deck> decks;
    private final DeckClickListener listener;

    public interface DeckClickListener {
        void onEditClick(Deck deck);
        void onDeleteClick(Deck deck);
        void onItemClick(Deck deck);
    }

    public DeckAdapter(List<Deck> decks, DeckClickListener listener) {
        this.decks = decks;
        this.listener = listener;
    }

    public void setDecks(List<Deck> decks) {
        this.decks = decks;
    }

    @NonNull
    @Override
    public DeckViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_deck, parent, false);
        return new DeckViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeckViewHolder holder, int viewType) {
        Deck deck = decks.get(holder.getAdapterPosition());
        holder.tvDeckTitle.setText(deck.getName());
        holder.tvCardCount.setText(deck.getCardCount() + " cards");

        holder.ibEditDeck.setOnClickListener(v -> listener.onEditClick(deck));
        holder.ibDeleteDeck.setOnClickListener(v -> listener.onDeleteClick(deck));
        holder.itemView.setOnClickListener(v -> listener.onItemClick(deck));
    }

    @Override
    public int getItemCount() {
        return decks != null ? decks.size() : 0;
    }

    static class DeckViewHolder extends RecyclerView.ViewHolder {
        TextView tvDeckTitle, tvCardCount;
        ImageButton ibEditDeck, ibDeleteDeck;

        public DeckViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDeckTitle = itemView.findViewById(R.id.tvDeckTitle);
            tvCardCount = itemView.findViewById(R.id.tvCardCount);
            ibEditDeck = itemView.findViewById(R.id.btnEditDeck);
            ibDeleteDeck = itemView.findViewById(R.id.btnDeleteDeck);
        }
    }
}
