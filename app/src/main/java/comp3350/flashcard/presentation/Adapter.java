package comp3350.flashcard.presentation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

/**
 * And adapter to show any list (Decks or Cards).
 * @param <T> The type of object.
 */
public class Adapter<T> extends RecyclerView.Adapter<Adapter.ViewHolder> {

    /**
     * A binder interface that tells the adapter how to show the data.
     * @param <T> The type of object to bind.
     */
    public interface Binder<T> {
        void bind(View view, T item);
    }

    private List<T> items;
    private final int layoutId;
    private final Binder<T> binder;

    public Adapter(List<T> items, int layoutId, Binder<T> binder) {
        this.items = items;
        this.layoutId = layoutId;
        this.binder = binder;
    }

    public void updateItems(List<T> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        binder.bind(holder.itemView, items.get(position));
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
