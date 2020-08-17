package com.aston.blindfitness.api;

import com.aston.blindfitness.Model.News;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("top-headlines")
    Call<News> getNews(
            @Query("description") String description,
            @Query("category") String category,
            @Query("country") String country,
            @Query("apiKey") String apiKey
    );

    @GET("everything")
    Call<News> getNewsSearch(
            @Query("q") String keyWord,
            @Query("language") String language,
            @Query("sortBy") String sortBy,
            @Query("apiKey") String apiKey
    );
}
