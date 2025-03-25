package com.example.ojsmobileapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ojsmobileapp.adapters.JournalAdapter;
import com.example.ojsmobileapp.api.ApiService;
import com.example.ojsmobileapp.api.RetrofitClient;
import com.example.ojsmobileapp.models.Journal;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView errorTextView;
    private JournalAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            loadJournals();
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

    private void loadJournals() {
        showLoading();

        ApiService apiService = RetrofitClient.getApiService();
        Call<List<Journal>> call = apiService.getJournals();

        call.enqueue(new Callback<List<Journal>>() {
            @Override
            public void onResponse(Call<List<Journal>> call, Response<List<Journal>> response) {
                runOnUiThread(() -> {
                    hideLoading();

                    if (response.isSuccessful() && response.body() != null) {
                        List<Journal> journals = response.body();
                        if (journals == null || journals.isEmpty()) {
                            showEmptyView();
                        } else {
                            showJournals(journals);
                        }
                    } else {
                        showError("Error al obtener datos del servidor. Código: " + response.code());
                    }
                });
            }

            @Override
            public void onFailure(Call<List<Journal>> call, Throwable t) {
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
        errorTextView.setText("No se encontraron revistas disponibles");
        errorTextView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    private void showJournals(List<Journal> journals) {
        errorTextView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        adapter = new JournalAdapter(this, journals, journal -> {
            Toast.makeText(MainActivity.this,
                    "Seleccionado: " + journal.getName(),
                    Toast.LENGTH_SHORT).show();
            // Aquí puedes abrir una nueva actividad con los detalles
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter = null;
        }
    }
}