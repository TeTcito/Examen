package com.example.ojsmobileapp.api;

import com.example.ojsmobileapp.models.Journal;
import com.example.ojsmobileapp.models.Publication;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("journals.php")
    Call<List<Journal>> getJournals();

    @GET("pubs.php")
    Call<List<Publication>> getPublications(@Query("i_id") String issueId);
}