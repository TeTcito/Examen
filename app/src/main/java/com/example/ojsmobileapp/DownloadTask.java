package com.example.ojsmobileapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DownloadTask {
    private static final String TAG = "DownloadTask";
    private final Context context;
    private DownloadListener listener;

    public interface DownloadListener {
        void onDownloadStart();
        void onDownloadProgress(int progress);
        void onDownloadComplete(File file);
        void onDownloadError(String message);
    }

    public DownloadTask(Context context, DownloadListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void downloadFile(String fileUrl) {
        new AsyncTask<String, Integer, File>() {
            @Override
            protected void onPreExecute() {
                if (listener != null) {
                    listener.onDownloadStart();
                }
            }

            @Override
            protected File doInBackground(String... urls) {
                String fileUrl = urls[0];
                HttpURLConnection connection = null;
                InputStream input = null;
                OutputStream output = null;

                try {
                    // Verificar espacio de almacenamiento
                    File downloadsDir = ContextCompat.getExternalFilesDirs(context,
                            Environment.DIRECTORY_DOWNLOADS)[0];
                    if (downloadsDir == null) {
                        throw new Exception("No se pudo acceder al directorio de descargas");
                    }

                    if (!downloadsDir.exists() && !downloadsDir.mkdirs()) {
                        throw new Exception("No se pudo crear el directorio de descargas");
                    }

                    // Crear nombre de archivo seguro
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                            .format(new Date());
                    String fileName = "article_" + timeStamp + ".pdf";

                    // Extraer nombre de archivo de la URL si es posible
                    try {
                        String urlFileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
                        if (urlFileName.contains(".")) {
                            fileName = urlFileName;
                        }
                    } catch (Exception e) {
                        Log.w(TAG, "No se pudo extraer nombre de archivo de la URL", e);
                    }

                    File outputFile = new File(downloadsDir, fileName);

                    URL url = new URL(fileUrl);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();

                    // Verificar respuesta HTTP
                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        throw new Exception("Respuesta del servidor: " + connection.getResponseCode());
                    }

                    // Obtener tamaño del archivo para progreso
                    int fileLength = connection.getContentLength();

                    input = new BufferedInputStream(connection.getInputStream());
                    output = new FileOutputStream(outputFile);

                    byte[] data = new byte[4096];
                    long total = 0;
                    int count;
                    while ((count = input.read(data)) != -1) {
                        // Verificar si se canceló la tarea
                        if (isCancelled()) {
                            input.close();
                            return null;
                        }

                        total += count;
                        // Publicar progreso si el tamaño es conocido
                        if (fileLength > 0) {
                            publishProgress((int) (total * 100 / fileLength));
                        }
                        output.write(data, 0, count);
                    }

                    return outputFile;

                } catch (Exception e) {
                    Log.e(TAG, "Error downloading file", e);
                    return null;
                } finally {
                    try {
                        if (output != null) output.close();
                        if (input != null) input.close();
                        if (connection != null) connection.disconnect();
                    } catch (Exception e) {
                        Log.e(TAG, "Error cerrando recursos", e);
                    }
                }
            }

            @Override
            protected void onProgressUpdate(Integer... progress) {
                if (listener != null) {
                    listener.onDownloadProgress(progress[0]);
                }
            }

            @Override
            protected void onPostExecute(File file) {
                if (file != null && listener != null) {
                    listener.onDownloadComplete(file);
                } else if (listener != null) {
                    listener.onDownloadError("Error al descargar el archivo");
                }
            }
        }.execute(fileUrl);
    }
}