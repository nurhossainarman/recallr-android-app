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
import comp3350.flashcard.objects.Flashcard;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private List<Flashcard> cards;
    private final CardClickListener listener;

    public interface CardClickListener {
        void onEditClick(Flashcard card);
        void onDeleteClick(Flashcard card);
    }

    public CardAdapter(List<Flashcard> cards, CardClickListener listener) {
        this.cards = cards;
        this.listener = listener;
    }

    public void setCards(List<Flashcard> cards) {
        this.cards = cards;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        Flashcard card = cards.get(position);
        holder.tvCardFront.setText(card.getFront());
        holder.tvCardBack.setText(card.getBack());

        holder.btnEditCard.setOnClickListener(v -> listener.onEditClick(card));
        holder.btnDeleteCard.setOnClickListener(v -> listener.onDeleteClick(card));
    }

    @Override
    public int getItemCount() {
        return cards != null ? cards.size() : 0;
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {
        TextView tvCardFront, tvCardBack;
        ImageButton btnEditCard, btnDeleteCard;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCardFront = itemView.findViewById(R.id.tvCardFront);
            tvCardBack = itemView.findViewById(R.id.tvCardBack);
            btnEditCard = itemView.findViewById(R.id.btnEditCard);
            btnDeleteCard = itemView.findViewById(R.id.btnDeleteCard);
        }
    }
}
