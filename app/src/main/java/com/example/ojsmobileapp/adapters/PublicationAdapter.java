package com.example.ojsmobileapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ojsmobileapp.R;
import com.example.ojsmobileapp.models.Publication;

import java.util.List;

public class PublicationAdapter extends RecyclerView.Adapter<PublicationAdapter.PublicationViewHolder> {
    private Context context;
    private List<Publication> publications;

    public PublicationAdapter(Context context, List<Publication> publications) {
        this.context = context;
        this.publications = publications;
    }

    @NonNull
    @Override
    public PublicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_publication, parent, false);
        return new PublicationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PublicationViewHolder holder, int position) {
        Publication publication = publications.get(position);

        holder.title.setText(publication.getTitle());
        holder.section.setText(publication.getSection());
        holder.date.setText(publication.getDate_published());

        // Construir cadena de autores
        StringBuilder authorsBuilder = new StringBuilder();
        for (Publication.Author author : publication.getAuthors()) {
            authorsBuilder.append(author.getNombres()).append(", ");
        }
        if (authorsBuilder.length() > 0) {
            authorsBuilder.setLength(authorsBuilder.length() - 2); // Eliminar la última coma
        }
        holder.authors.setText(authorsBuilder.toString());

        // Configurar los botones PDF y HTML
        for (Publication.Galley galley : publication.getGaleys()) {
            if (galley.getLabel().equalsIgnoreCase("PDF")) {
                holder.pdfButton.setOnClickListener(v -> {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(galley.getUrlViewGalley()));
                    context.startActivity(browserIntent);
                });
            } else if (galley.getLabel().equalsIgnoreCase("HTML")) {
                holder.htmlButton.setOnClickListener(v -> {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(galley.getUrlViewGalley()));
                    context.startActivity(browserIntent);
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return publications != null ? publications.size() : 0;
    }

    public static class PublicationViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView section;
        TextView date;
        TextView authors;
        TextView pdfButton;
        TextView htmlButton;

        public PublicationViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.publicationTitle);
            section = itemView.findViewById(R.id.publicationSection);
            date = itemView.findViewById(R.id.publicationDate);
            authors = itemView.findViewById(R.id.publicationAuthors);
            pdfButton = itemView.findViewById(R.id.pdfButton);
            htmlButton = itemView.findViewById(R.id.htmlButton);
        }
    }
}