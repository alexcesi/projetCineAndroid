package projet.mobile.allomovies;

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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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


public class ActorDetailActivity extends BaseActivity {
    private List<TVShow> tvShows = new ArrayList<>();
    private TextView textViewActorName;
    private TextView textViewActorCharacter;
    private TextView textViewActorBio;
    private TextView textViewMoviesTitle;
    private ImageView imageViewActorProfile;
    private RecyclerView recyclerViewTVShows;

    private EditText searchEditText; // Champ de recherche

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actor_detail);

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
                            if (!searchText.isEmpty()) {
                                // Fermer le clavier virtuel et effectuer la recherche
                                hideKeyboard(searchEditText);
                                performSearch(searchText);
                            }
                        }
                    });
                }
            }
        });

        // Initialize the views
        textViewActorName = findViewById(R.id.actor_detail_name);
        textViewActorCharacter = findViewById(R.id.actor_detail_character);
        textViewActorBio = findViewById(R.id.actor_detail_bio);
        textViewMoviesTitle = findViewById(R.id.actor_detail_movies_title);
        imageViewActorProfile = findViewById(R.id.actor_profile_image);
        recyclerViewTVShows = findViewById(R.id.actor_detail_movies);

        // Configure the RecyclerView for displaying the TV shows
        recyclerViewTVShows.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Récupérer les données passées depuis DetailActivity
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            // Récupérer l'ID de l'acteur
            int actorId = intent.getIntExtra("actor_id", 0);

            // Utiliser l'ID de l'acteur pour charger les informations de l'acteur (à partir de l'API)
            fetchActorDetails(actorId);
            fetchActorMovieCredits(actorId);
        }
    }

    private void fetchActorDetails(int actorId) {
        OkHttpClient client = new OkHttpClient();

        String url = "https://api.themoviedb.org/3/person/" + actorId + "?api_key=9701cb9919bdf284985fae99ae807582";

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("fetchActorDetails", "Erreur de requête : " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d("fetchActorDetails", "Réponse JSON : " + responseBody);

                    try {
                        JSONObject actorJson = new JSONObject(responseBody);
                        String actorName = actorJson.optString("name");
                        String actorCharacter = actorJson.optString("character");
                        String actorBio = actorJson.optString("biography");
                        String profileImage = actorJson.optString("profile_path");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Set the actor details in the views
                                textViewActorName.setText(actorName);
                                textViewActorCharacter.setText(actorCharacter);
                                textViewActorBio.setText(actorBio);

                                // Load the actor profile image using Picasso or another image loading library
                                String profileImageUrl = "https://image.tmdb.org/t/p/w500" + profileImage;
                                Picasso.get().load(profileImageUrl).into(imageViewActorProfile);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void fetchActorMovieCredits(int actorId) {
        OkHttpClient client = new OkHttpClient();

        String url = "https://api.themoviedb.org/3/person/" + actorId + "/movie_credits?api_key=9701cb9919bdf284985fae99ae807582";

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("fetchActorMovieCredits", "Erreur de requête : " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d("fetchActorMovieCredits", "Réponse JSON : " + responseBody);

                    try {
                        JSONObject responseJson = new JSONObject(responseBody);
                        JSONArray movieCreditsArray = responseJson.getJSONArray("cast");

                        tvShows.clear();
                        for (int i = 0; i < movieCreditsArray.length(); i++) {
                            JSONObject movieCreditObject = movieCreditsArray.getJSONObject(i);
                            int id = movieCreditObject.optInt("id");
                            String movieTitle = movieCreditObject.optString("title");
                            String releaseDate = movieCreditObject.optString("release_date");
                            String overview = movieCreditObject.optString("overview");
                            String posterPath = movieCreditObject.optString("poster_path");

                            TVShow tvShow = new TVShow(id, movieTitle, overview, posterPath);
                            tvShows.add(tvShow);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Set the movie titles in the adapter and update the RecyclerView
                                TVShowAdapter tvShowAdapter = new TVShowAdapter(ActorDetailActivity.this, tvShows);
                                recyclerViewTVShows.setAdapter(tvShowAdapter);
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
        Intent intent = new Intent(ActorDetailActivity.this, MainActivity.class);
        intent.putExtra("search_text", searchText.trim());
        startActivity(intent);
    }
}
