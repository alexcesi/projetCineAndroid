package projet.mobile.allomovies;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {
    private TVShowAdapter tvShowAdapter;
    private List<TVShow> tvShows;

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
        fetchTVShows();

        setHomeButtonClickListener();
        setBackButtonClickListener();
        setSearchButtonClickListener();
    }

    private void fetchTVShows() {
        OkHttpClient client = new OkHttpClient();

        String url = "https://api.themoviedb.org/3/movie/popular?api_key=9701cb9919bdf284985fae99ae807582";

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
    protected void onSearchButtonClick() {

    }
}
