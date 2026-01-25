package com.burkina.gestiondesdettes.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.burkina.gestiondesdettes.R;
import com.burkina.gestiondesdettes.models.Dette;
import com.burkina.gestiondesdettes.utils.CurrencyUtils;
import com.burkina.gestiondesdettes.utils.DateUtils;
import java.util.List;

public class DetteAdapter extends RecyclerView.Adapter<DetteAdapter.DetteViewHolder> {

    private List<Dette> dettes;
    private OnDetteClickListener listener;

    public interface OnDetteClickListener {
        void onDetteClick(Dette dette);
        void onDetteLongClick(Dette dette);
    }

    public DetteAdapter(List<Dette> dettes, OnDetteClickListener listener) {
        this.dettes = dettes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DetteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dette, parent, false);
        return new DetteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetteViewHolder holder, int position) {
        Dette dette = dettes.get(position);
        holder.bind(dette, listener);
    }

    @Override
    public int getItemCount() {
        return dettes.size();
    }

    static class DetteViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDescription;
        private TextView tvDate;
        private TextView tvMontant;
        private TextView tvSoldeRestant;
        private TextView tvStatut;

        public DetteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDescription = itemView.findViewById(R.id.tvDetteDescription);
            tvDate = itemView.findViewById(R.id.tvDetteDate);
            tvMontant = itemView.findViewById(R.id.tvDetteMontant);
            tvSoldeRestant = itemView.findViewById(R.id.tvDetteSoldeRestant);
            tvStatut = itemView.findViewById(R.id.tvDetteStatut);
        }

        public void bind(Dette dette, OnDetteClickListener listener) {
            tvDescription.setText(dette.getDescription());
            tvDate.setText(DateUtils.getRelativeDate(dette.getDateDette()));
            tvMontant.setText("Initial: " + CurrencyUtils.format(dette.getMontant()));
            tvSoldeRestant.setText(CurrencyUtils.format(dette.getSoldeRestant()));

            // Statut et couleur
            if (dette.isPayee()) {
                tvStatut.setText("Payé");
                tvStatut.setBackgroundResource(R.drawable.status_badge_paid);
                tvSoldeRestant.setTextColor(itemView.getContext().getColor(R.color.success));
            } else {
                tvStatut.setText("En cours");
                tvStatut.setBackgroundResource(R.drawable.status_badge_ongoing);
                tvSoldeRestant.setTextColor(itemView.getContext().getColor(R.color.error));
            }

            // Click listeners
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDetteClick(dette);
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (listener != null) {
                    listener.onDetteLongClick(dette);
                }
                return true;
            });
        }
    }
}