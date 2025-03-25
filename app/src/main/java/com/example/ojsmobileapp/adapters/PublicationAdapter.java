package com.example.ojsmobileapp.adapters;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ojsmobileapp.R;
import com.example.ojsmobileapp.models.Publication;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class PublicationAdapter extends RecyclerView.Adapter<PublicationAdapter.PublicationViewHolder> {
    private static final int STORAGE_PERMISSION_CODE = 1001;
    private Context context;
    private List<Publication> publications;
    private PermissionRequestListener permissionListener;

    public interface PermissionRequestListener {
        void onPermissionRequested();
    }

    public PublicationAdapter(Context context, List<Publication> publications, PermissionRequestListener listener) {
        this.context = context;
        this.publications = publications;
        this.permissionListener = listener;
    }

    @NonNull
    @Override
    public PublicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_publication, parent, false);
        return new PublicationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PublicationViewHolder holder, int position) {
        if (publications == null || position < 0 || position >= publications.size()) {
            return;
        }

        Publication publication = publications.get(position);
        if (publication == null) {
            return;
        }

        // Configurar datos básicos
        holder.title.setText(publication.getTitle() != null ? publication.getTitle() : "");
        holder.section.setText(publication.getSection() != null ? publication.getSection() : "");
        holder.date.setText(publication.getDate_published() != null ? publication.getDate_published() : "");

        // Construir cadena de autores
        if (publication.getAuthors() != null && !publication.getAuthors().isEmpty()) {
            StringBuilder authorsBuilder = new StringBuilder();
            for (Publication.Author author : publication.getAuthors()) {
                if (author != null && author.getNombres() != null) {
                    authorsBuilder.append(author.getNombres()).append(", ");
                }
            }
            if (authorsBuilder.length() > 0) {
                authorsBuilder.setLength(authorsBuilder.length() - 2);
                holder.authors.setText(authorsBuilder.toString());
            } else {
                holder.authors.setText("");
            }
        } else {
            holder.authors.setText("");
        }

        // Configurar botones PDF y HTML
        if (publication.getGaleys() != null) {
            for (Publication.Galley galley : publication.getGaleys()) {
                if (galley == null || galley.getLabel() == null || galley.getUrlViewGalley() == null) {
                    continue;
                }

                if (galley.getLabel().equalsIgnoreCase("PDF")) {
                    holder.pdfButton.setOnClickListener(v -> {
                        if (checkStoragePermission()) {
                            new DownloadTask(context).execute(galley.getUrlViewGalley());
                        } else {
                            requestStoragePermission();
                        }
                    });
                } else if (galley.getLabel().equalsIgnoreCase("HTML")) {
                    holder.htmlButton.setOnClickListener(v -> {
                        try {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse(galley.getUrlViewGalley()));
                            context.startActivity(browserIntent);
                        } catch (Exception e) {
                            Toast.makeText(context, "No se pudo abrir el enlace", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return publications != null ? publications.size() : 0;
    }

    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission() {
        if (permissionListener != null) {
            permissionListener.onPermissionRequested();
        }
    }

    public static class PublicationViewHolder extends RecyclerView.ViewHolder {
        TextView title, section, date, authors, pdfButton, htmlButton;

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

    private static class DownloadTask extends AsyncTask<String, Void, String> {
        private Context context;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... urls) {
            if (urls == null || urls.length == 0 || urls[0] == null) {
                return null;
            }

            String fileUrl = urls[0];
            try {
                URL url = new URL(fileUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // Obtener nombre del archivo
                String fileName = "article_" + System.currentTimeMillis() + ".pdf";
                if (fileUrl.contains("/")) {
                    String temp = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
                    if (!temp.isEmpty()) {
                        fileName = temp;
                    }
                }

                File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File outputFile = new File(downloadsDir, fileName);

                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(outputFile);

                byte[] data = new byte[1024];
                int count;
                while ((count = input.read(data)) != -1) {
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
                return outputFile.getAbsolutePath();

            } catch (Exception e) {
                Log.e("DownloadTask", "Error downloading file", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String filePath) {
            if (filePath != null) {
                Toast.makeText(context, "PDF descargado en: Descargas/" + filePath.substring(filePath.lastIndexOf("/") + 1),
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Error al descargar el PDF", Toast.LENGTH_SHORT).show();
            }
        }
    }
}