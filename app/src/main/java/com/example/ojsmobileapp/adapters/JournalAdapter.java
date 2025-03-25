package com.example.ojsmobileapp.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ojsmobileapp.R;
import com.example.ojsmobileapp.models.Journal;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.JournalViewHolder> {

    private Context context;
    private List<Journal> journals;
    private OnItemClickListener listener;
    private int lastPosition = -1;

    public interface OnItemClickListener {
        void onItemClick(Journal journal);
    }

    public JournalAdapter(Context context, List<Journal> journals, OnItemClickListener listener) {
        this.context = context;
        this.journals = journals;
        this.listener = listener;
    }

    @NonNull
    @Override
    public JournalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_journal, parent, false);
        return new JournalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JournalViewHolder holder, int position) {
        try {
            Journal journal = journals.get(position);
            if (journal == null) return;

            // Cargar imagen con manejo de errores
            loadImage(holder, journal);

            // Configurar texto
            holder.journalName.setText(journal.getName());

            // Procesar HTML
            setDescriptionText(holder, journal);

            // Configurar abreviación
            setAbbreviation(holder, journal);

            // Animación
            setAnimation(holder.itemView, position);

            // Click listener
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(journal);
                }
            });

        } catch (Exception e) {
            Log.e("JournalAdapter", "Error en onBindViewHolder", e);
        }
    }

    private void loadImage(JournalViewHolder holder, Journal journal) {
        if (!journal.getLogo().isEmpty()) {
            Picasso.get()
                    .load(journal.getLogo())
                    .placeholder(R.drawable.ic_default_journal)
                    .error(R.drawable.ic_default_journal)
                    .fit()
                    .centerCrop()
                    .into(holder.journalLogo, new Callback() {
                        @Override
                        public void onSuccess() {
                            // Imagen cargada correctamente
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.e("Picasso", "Error al cargar imagen: " + e.getMessage());
                            holder.journalLogo.setImageResource(R.drawable.ic_default_journal);
                        }
                    });
        } else {
            holder.journalLogo.setImageResource(R.drawable.ic_default_journal);
        }
    }

    private void setDescriptionText(JournalViewHolder holder, Journal journal) {
        try {
            if (!journal.getDescription().isEmpty()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    holder.journalDescription.setText(Html.fromHtml(journal.getDescription(), Html.FROM_HTML_MODE_COMPACT));
                } else {
                    holder.journalDescription.setText(Html.fromHtml(journal.getDescription()));
                }
            } else {
                holder.journalDescription.setText("No hay descripción disponible");
            }
        } catch (Exception e) {
            Log.e("JournalAdapter", "Error al procesar HTML", e);
            holder.journalDescription.setText(journal.getDescription());
        }
    }

    private void setAbbreviation(JournalViewHolder holder, Journal journal) {
        if (!journal.getAbbreviation().isEmpty()) {
            holder.journalAbbreviation.setText(journal.getAbbreviation());
            holder.journalAbbreviation.setVisibility(View.VISIBLE);
        } else {
            holder.journalAbbreviation.setVisibility(View.GONE);
        }
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            try {
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.intem_animation);
                if (animation != null) {
                    viewToAnimate.startAnimation(animation);
                    lastPosition = position;

                    // Limpiar la animación después de que termine
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {}

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            viewToAnimate.clearAnimation();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {}
                    });
                }
            } catch (Resources.NotFoundException e) {
                Log.e("JournalAdapter", "Error al cargar animación", e);
            }
        }
    }

    @Override
    public int getItemCount() {
        return journals != null ? journals.size() : 0;
    }

    public static class JournalViewHolder extends RecyclerView.ViewHolder {
        ImageView journalLogo;
        TextView journalName;
        TextView journalDescription;
        TextView journalAbbreviation;

        public JournalViewHolder(@NonNull View itemView) {
            super(itemView);
            journalLogo = itemView.findViewById(R.id.journalLogo);
            journalName = itemView.findViewById(R.id.journalName);
            journalDescription = itemView.findViewById(R.id.journalDescription);
            journalAbbreviation = itemView.findViewById(R.id.journalAbbreviation);
        }
    }
}