package com.aston.blindfitness.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.aston.blindfitness.Adapter.NewsAdapter;
import com.aston.blindfitness.Model.Article;
import com.aston.blindfitness.Model.News;
import com.aston.blindfitness.Utils.NewsUtils;
import com.aston.blindfitness.R;
import com.aston.blindfitness.SharedPrefs;
import com.aston.blindfitness.api.ApiClient;
import com.aston.blindfitness.api.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * NewsActivity to display news and events to user
 */
public class NewsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    SharedPrefs userPreferences;

    public static final String API_KEY = "8916a842c6a04c8cb523dd5d9af66e10"; // API Key used for NewsAPI
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<Article> articles = new ArrayList<>();
    private NewsAdapter adapter;
    private String TAG = NewsActivity.class.getSimpleName();
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        userPreferences = new SharedPrefs(NewsActivity.this);
        if (userPreferences.loadDefaultTheme()) {
            userPreferences.setDarkTheme(false);
            userPreferences.setYellowTheme(false);
            userPreferences.setLightTheme(false);
            setTheme(R.style.AppTheme);
        } else if (userPreferences.loadDarkTheme()) {
            userPreferences.setLightTheme(false);
            userPreferences.setYellowTheme(false);
            userPreferences.setDefaultTheme(false);
            setTheme(R.style.DarkTheme);
        } else if (userPreferences.loadYellowTheme()) {
            userPreferences.setLightTheme(false);
            userPreferences.setDarkTheme(false);
            userPreferences.setDefaultTheme(false);
            setTheme(R.style.YellowTheme);
        } else if (userPreferences.loadLightTheme()) {
            userPreferences.setDarkTheme(false);
            userPreferences.setYellowTheme(false);
            userPreferences.setDefaultTheme(false);
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        recyclerView = findViewById(R.id.newsRecyclerView);
        layoutManager = new LinearLayoutManager(NewsActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        swipeRefreshLayout = findViewById(R.id.news_srl);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        onLoadingSwipeRefresh("");

        if (userPreferences.loadDarkTheme()) {
            getSupportActionBar().setTitle(Html.fromHtml("News and Events"));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            getSupportActionBar().setTitle(Html.fromHtml("News and Events"));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setDisplayUseLogoEnabled(true);

    }

    /**
     * Load news in JSON format
     *
     * @param keyWord
     */
    public void loadJson(final String keyWord) {
        swipeRefreshLayout.setRefreshing(true);
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        String country = NewsUtils.getCountry();
        String language = NewsUtils.getLanguage();
        String description = NewsUtils.getDescription();
        String category = NewsUtils.getCategory();

        Call<News> call;

        if (keyWord.length() > 0) {
            call = apiInterface.getNewsSearch(keyWord, language, "publishedAt", API_KEY);
        } else {
            call = apiInterface.getNews(description, category, country, API_KEY);
        }

        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {
                if (response.isSuccessful() && response.body().getArticle() != null) {

                    if (!articles.isEmpty()) {
                        articles.clear();
                    }

                    articles = response.body().getArticle();
                    adapter = new NewsAdapter(articles, NewsActivity.this);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    initListener();

                    swipeRefreshLayout.setRefreshing(false);

                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(NewsActivity.this, "No results found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {

            }
        });
    }

    /**
     * Initialise news adapter
     */
    private void initListener() {
        adapter.setOnItemClickListener(new NewsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(NewsActivity.this, NewsDetailActivity.class);

                Article article = articles.get(position);
                intent.putExtra("url", article.getUrl());
                intent.putExtra("title", article.getTitle());
                intent.putExtra("img", article.getUrlToImage());
                intent.putExtra("date", article.getPublishedAt());
                intent.putExtra("source", article.getSource().getName());
                intent.putExtra("author", article.getAuthor());
                startActivity(intent);

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_news_search, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.news_action_search).getActionView();
        MenuItem menuItem = menu.findItem(R.id.news_action_search);

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Search latest news");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (s.length() > 2) {
                    onLoadingSwipeRefresh(s);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        menuItem.getIcon().setVisible(false, false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onRefresh() {
        loadJson("");
    }

    /**
     * Refresh news when swiped
     * @param keyWord
     */
    private void onLoadingSwipeRefresh(final String keyWord) {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                loadJson(keyWord);
            }
        });
    }
}
