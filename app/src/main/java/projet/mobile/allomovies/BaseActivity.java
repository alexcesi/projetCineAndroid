// BaseActivity.java
package projet.mobile.allomovies;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

    protected abstract void onSearchButtonClick();

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
                onSearchButtonClick();
            }
        });
    }
}