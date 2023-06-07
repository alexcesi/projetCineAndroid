package projet.mobile.allomovies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends BaseActivity {

    private TVShowAdapter tvShowAdapter;
    private List<TVShow> tvShows; // Liste complète des films non filtrés
    private Spinner spinnerYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvShows = new ArrayList<>();

        tvShowAdapter = new TVShowAdapter(MainActivity.this, tvShows);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(tvShowAdapter);

        spinnerYear = findViewById(R.id.spinner_year);
        setupYearSpinner();

        fetchTVShows("");

        setHomeButtonClickListener();
        setBackButtonClickListener();
        setSearchButtonClickListener();
    }

    private void setupYearSpinner() {
        List<String> yearOptions = new ArrayList<>();
        yearOptions.add("Toutes les années"); // Option pour afficher tous les films sans filtre par année
        yearOptions.add("2023");
        yearOptions.add("2022");
        yearOptions.add("2021");
        yearOptions.add("2020");
        yearOptions.add("2019");
        yearOptions.add("2018");
        yearOptions.add("2017");
        yearOptions.add("2016");
        yearOptions.add("2015");
        yearOptions.add("2014");
        yearOptions.add("2013");
        yearOptions.add("2012");
        // ... Ajoutez d'autres années si nécessaire

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, yearOptions);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearAdapter);

        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedYear = parent.getItemAtPosition(position).toString();
                fetchTVShows(selectedYear);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Ne rien faire lorsque rien n'est sélectionné
            }
        });
    }

    private void fetchTVShows(String selectedYear) {
        OkHttpClient client = new OkHttpClient();

        String url;
        if (selectedYear.equals("Toutes les années")) {
            url = "https://api.themoviedb.org/3/movie/popular?api_key=4e2c4790a1f78ff4fcc8260201e02446";
        } else {
            url = "https://api.themoviedb.org/3/discover/movie?api_key=4e2c4790a1f78ff4fcc8260201e02446&primary_release_year=" + selectedYear;
        }

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("fetchTVShows", "Erreur de requête : " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d("fetchTVShows", "Réponse JSON : " + responseBody);

                    List<TVShow> tvShows = extractTVShowsFromResponse(responseBody);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Mettez à jour la liste des séries télévisées dans l'adaptateur
                            tvShowAdapter.setTVShows(tvShows);
                            tvShowAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }

    private List<TVShow> extractTVShowsFromResponse(String responseBody) {
        List<TVShow> tvShows = new ArrayList<>();

        try {
            JSONObject responseJson = new JSONObject(responseBody);

            JSONArray resultsArray = responseJson.getJSONArray("results");

            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject tvShowJson = resultsArray.getJSONObject(i);

                int id = tvShowJson.getInt("id");
                String original_title = tvShowJson.optString("original_title");
                String overview = tvShowJson.getString("overview");
                String poster_path = tvShowJson.optString("poster_path");

                TVShow tvShow = new TVShow(id, original_title, overview, poster_path);
                tvShows.add(tvShow);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tvShows;
    }

    @Override
    protected void performSearch(String searchText) {
        // Ajoutez le code pour effectuer une recherche
    }
}
