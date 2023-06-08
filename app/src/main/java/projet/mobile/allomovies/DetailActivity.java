package projet.mobile.allomovies;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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

public class DetailActivity extends BaseActivity {
    private TextView textViewOriginalTitle;
    private TextView textViewOverview;
    private ImageView imageViewPoster;
    private RecyclerView recyclerViewActors;

    private EditText searchEditText; // Champ de recherche

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        setHomeButtonClickListener();
        setBackButtonClickListener();

        searchEditText = findViewById(R.id.searchEditText);
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String searchText = searchEditText.getText().toString().trim();
                    performSearch(searchText);
                    hideKeyboard(searchEditText);
                    return true;
                }
                return false;
            }
        });

        ImageButton searchButton = findViewById(R.id.btnSearch);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = searchEditText.getText().toString().trim();
                if (searchEditText.getVisibility() == View.GONE) {
                    // Afficher le champ de recherche et ouvrir le clavier virtuel
                    searchEditText.setVisibility(View.VISIBLE);
                    searchEditText.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT);
                    }
                } else if (!searchText.isEmpty()) {
                    // Fermer le clavier virtuel et effectuer la recherche
                    hideKeyboard(searchEditText);
                    searchButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String searchText = searchEditText.getText().toString().trim();
                            if (searchEditText.getVisibility() == View.GONE) {
                                // Afficher le champ de recherche et ouvrir le clavier virtuel
                                searchEditText.setVisibility(View.VISIBLE);
                                searchEditText.requestFocus();
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                if (imm != null) {
                                    imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT);
                                }
                            } else if (!searchText.isEmpty()) {
                                // Fermer le clavier virtuel et effectuer la recherche
                                hideKeyboard(searchEditText);
                                Intent intent = new Intent(DetailActivity.this, MainActivity.class);
                                intent.putExtra("search_text", searchText);
                                startActivity(intent);
                            }
                        }
                    });
                }
            }
        });

        // Récupérer les références des vues dans le layout
        textViewOriginalTitle = findViewById(R.id.detail_original_title);
        textViewOverview = findViewById(R.id.detail_overview);
        imageViewPoster = findViewById(R.id.detail_poster_path);
        recyclerViewActors = findViewById(R.id.detail_actors);

        // Configurer le RecyclerView pour afficher les acteurs horizontalement
        recyclerViewActors.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Récupérer les données passées depuis MainActivity
        Intent intent = getIntent();

        if (intent != null && intent.getExtras() != null) {
            String originalTitle = intent.getStringExtra("original_title");
            String overview = intent.getStringExtra("overview");
            int movieId = intent.getIntExtra("movie_id", 0);

            // Utiliser les données pour afficher les informations de base
            textViewOriginalTitle.setText(originalTitle);
            textViewOverview.setText(overview);

            // Charger les détails du film (à partir de l'API)
            fetchMovieDetails(movieId);

            // Charger les acteurs du film (à partir de l'API)
            fetchMovieActors(movieId);
        }

    }

    private void fetchMovieDetails(int movieId) {
        OkHttpClient client = new OkHttpClient();

        String url = "https://api.themoviedb.org/3/movie/" + movieId + "?api_key=9701cb9919bdf284985fae99ae807582";

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("fetchMovieDetails", "Erreur de requête : " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    //Log.d("fetchMovieDetails", "Réponse JSON : " + responseBody);

                    try {
                        JSONObject tvShowJson = new JSONObject(responseBody);
                        String original_title = tvShowJson.optString("original_title");
                        String overview = tvShowJson.getString("overview");
                        String poster_path = tvShowJson.optString("poster_path");
                        // Autres informations du film...

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Charger l'image du film dans ImageView
                                String imageUrl = "https://image.tmdb.org/t/p/w500" + poster_path;
                                Picasso.get().load(imageUrl).into(imageViewPoster);

                                // Mise à jour des autres vues avec les informations du film
                                float voteAverage = (float) tvShowJson.optDouble("vote_average", 0);
                                String voteAverageText = "Note moyenne : " + voteAverage;
                                TextView textViewVoteAverage = findViewById(R.id.detail_vote_average);
                                textViewVoteAverage.setText(voteAverageText);
                                textViewOriginalTitle.setText(original_title);
                                textViewOverview.setText(overview);
                                // ...
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void fetchMovieActors(int movieId) {
        OkHttpClient client = new OkHttpClient();

        String url = "http://api.themoviedb.org/3/movie/" + movieId + "/credits?api_key=9701cb9919bdf284985fae99ae807582";

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("fetchMovieActors", "Erreur de requête : " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d("fetchMovieActors", "Réponse JSON : " + responseBody);

                    try {
                        JSONObject responseJson = new JSONObject(responseBody);
                        JSONArray castArray = responseJson.getJSONArray("cast");

                        final List<Actor> actors = new ArrayList<>();
                        for (int i = 0; i < castArray.length(); i++) {
                            JSONObject castObject = castArray.getJSONObject(i);
                            int actorId = castObject.optInt("id");
                            String actorName = castObject.optString("name");
                            String profilePath = castObject.optString("profile_path");
                            Actor actor = new Actor(actorId, actorName, profilePath);
                            actors.add(actor);
                            Log.d("Actor Info", "ID: " + actor.getId() + ", Name: " + actor.getName() + ", Profile Path: " + actor.getProfilePath());
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Créer une instance de l'adaptateur personnalisé pour les acteurs
                                ActorAdapter actorAdapter = new ActorAdapter(actors);

                                // Définir l'adaptateur sur le RecyclerView
                                recyclerViewActors.setAdapter(actorAdapter);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void performSearch(String searchText) {
        Intent intent = new Intent(DetailActivity.this, MainActivity.class);
        intent.putExtra("search_text", searchText.trim());
        startActivity(intent);
    }
}