package com.example.ojsmobileapp;

import android.content.Context;
import android.content.Intent;
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

import com.example.ojsmobileapp.adapters.IssueAdapter;
import com.example.ojsmobileapp.api.ApiService;
import com.example.ojsmobileapp.api.RetrofitClient;
import com.example.ojsmobileapp.models.Issue;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IssuesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView errorTextView;
    private IssueAdapter adapter;
    private String journalId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issues);

        // Obtener el ID de la revista seleccionada
        journalId = getIntent().getStringExtra("JOURNAL_ID");
        if (journalId == null || journalId.isEmpty()) {
            Toast.makeText(this, "No se ha seleccionado una revista válida", Toast.LENGTH_SHORT).show();
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
            loadIssues();
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

    private void loadIssues() {
        showLoading();

        ApiService apiService = RetrofitClient.getApiService();
        Call<List<Issue>> call = apiService.getIssues(journalId);

        call.enqueue(new Callback<List<Issue>>() {
            @Override
            public void onResponse(Call<List<Issue>> call, Response<List<Issue>> response) {
                runOnUiThread(() -> {
                    hideLoading();

                    if (response.isSuccessful() && response.body() != null) {
                        List<Issue> issues = response.body();
                        if (issues == null || issues.isEmpty()) {
                            showEmptyView();
                        } else {
                            showIssues(issues);
                        }
                    } else {
                        showError("Error al obtener datos del servidor. Código: " + response.code());
                    }
                });
            }

            @Override
            public void onFailure(Call<List<Issue>> call, Throwable t) {
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
        errorTextView.setText("No se encontraron volúmenes disponibles");
        errorTextView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    private void showIssues(List<Issue> issues) {
        errorTextView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        adapter = new IssueAdapter(this, issues, issue -> {
            // Cuando se selecciona un volumen, abrir la actividad de artículos
            Intent intent = new Intent(IssuesActivity.this, PublicationsActivity.class);
            intent.putExtra("ISSUE_ID", issue.getIssue_id());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
    }
}