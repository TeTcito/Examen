package com.example.ojsmobileapp.api;

import com.example.ojsmobileapp.models.Journal;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("journals.php")
    Call<List<Journal>> getJournals();
}