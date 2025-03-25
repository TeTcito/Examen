package com.example.ojsmobileapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ojsmobileapp.adapters.PublicationAdapter;
import com.example.ojsmobileapp.api.ApiService;
import com.example.ojsmobileapp.api.RetrofitClient;
import com.example.ojsmobileapp.models.Publication;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PublicationsActivity extends AppCompatActivity
        implements PublicationAdapter.PermissionRequestListener {

    private static final int STORAGE_PERMISSION_CODE = 1001;

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView errorTextView;
    private PublicationAdapter adapter;
    private String issueId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publications);

        // Obtener el ID de la edición seleccionada
        issueId = getIntent().getStringExtra("ISSUE_ID");
        if (issueId == null || issueId.isEmpty()) {
            Toast.makeText(this, "No se ha seleccionado una edición válida", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        checkNetworkConnection();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        errorTextView = findViewById(R.id.errorTextView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
    }

    private void checkNetworkConnection() {
        if (isNetworkAvailable()) {
            loadPublications();
        } else {
            showError("No hay conexión a internet. Conéctate a una red y reinicia la aplicación.");
        }
    }

    private boolean isNetworkAvailable() {
        try {
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        } catch (Exception e) {
            Log.e("NetworkCheck", "Error al verificar conexión", e);
            return false;
        }
    }

    private void loadPublications() {
        showLoading();

        ApiService apiService = RetrofitClient.getApiService();
        Call<List<Publication>> call = apiService.getPublications(issueId);

        call.enqueue(new Callback<List<Publication>>() {
            @Override
            public void onResponse(Call<List<Publication>> call, Response<List<Publication>> response) {
                runOnUiThread(() -> {
                    hideLoading();

                    if (response.isSuccessful() && response.body() != null) {
                        List<Publication> publications = response.body();
                        if (publications == null || publications.isEmpty()) {
                            showEmptyView();
                        } else {
                            showPublications(publications);
                        }
                    } else {
                        showError("Error al obtener datos del servidor. Código: " + response.code());
                    }
                });
            }

            @Override
            public void onFailure(Call<List<Publication>> call, Throwable t) {
                runOnUiThread(() -> {
                    hideLoading();
                    Log.e("API_CALL", "Error en la llamada API", t);

                    if (call.isCanceled()) {
                        showError("La solicitud fue cancelada");
                    } else {
                        showError("Error de conexión: " + t.getMessage());
                    }
                });
            }
        });
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        errorTextView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    private void showError(String message) {
        errorTextView.setText(message);
        errorTextView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    private void showEmptyView() {
        errorTextView.setText("No se encontraron artículos disponibles");
        errorTextView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    private void showPublications(List<Publication> publications) {
        errorTextView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        adapter = new PublicationAdapter(this, publications, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onPermissionRequested() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, puedes volver a intentar la descarga
                Toast.makeText(this, "Permiso concedido, intenta descargar nuevamente",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permiso denegado, no se puede descargar",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter = null;
        }
    }
}