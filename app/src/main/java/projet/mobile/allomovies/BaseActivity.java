// BaseActivity.java
package projet.mobile.allomovies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    protected void onHomeButtonClick() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        Log.d("test", "home button clicked");
    }

    protected void onBackButtonClick() {
        onBackPressed();
        Log.d("test", "back button clicked");
    }

    protected void onSearchButtonClick(String searchText) {
        EditText searchEditText = findViewById(R.id.searchEditText);
        ImageButton searchButton = findViewById(R.id.btnSearch);

        if (searchEditText.getVisibility() == View.VISIBLE) {
            // Si le EditText est déjà visible, effectuer la recherche
            performSearch(searchText);
        } else {
            // Sinon, afficher le EditText
            searchEditText.setVisibility(View.VISIBLE);
            searchEditText.requestFocus();

            // Afficher le clavier virtuel
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    protected abstract void performSearch(String searchText);

    protected void setHomeButtonClickListener() {
        ImageButton homeButton = findViewById(R.id.btnHome);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onHomeButtonClick();
            }
        });
    }

    protected void setBackButtonClickListener() {
        ImageButton backButton = findViewById(R.id.btnBack);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackButtonClick();
            }
        });
    }

    protected void setSearchButtonClickListener() {
        ImageButton searchButton = findViewById(R.id.btnSearch);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText searchEditText = findViewById(R.id.searchEditText);
                String searchText = searchEditText.getText().toString();
                onSearchButtonClick(searchText);
            }
        });
    }
}