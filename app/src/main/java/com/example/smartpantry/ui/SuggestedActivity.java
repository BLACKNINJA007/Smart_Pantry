package com.example.smartpantry.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.R;
import com.example.smartpantry.data.AppDatabase;
import com.example.smartpantry.data.PantryItem;
import com.example.smartpantry.data.RecipeWithIngredients;
import com.example.smartpantry.logic.RecipeMatcher;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

/** Suggested Recipes screen: only shows recipes the pantry can fully cover (strict match). */
public class SuggestedActivity extends AppCompatActivity {

    private RecipeAdapter adapter;
    private TextView textEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        textEmpty = findViewById(R.id.textEmpty);

        RecyclerView recycler = findViewById(R.id.recyclerSuggested);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecipeAdapter(this::onRecipeTapped);
        recycler.setAdapter(adapter);
    }

    /** Re-run the matching every time the screen comes back, so changes to the pantry show up. */
    @Override
    protected void onResume() {
        super.onResume();
        loadSuggestions();
    }

    private void loadSuggestions() {
        AppDatabase db = AppDatabase.getInstance(this);
        AppDatabase.DB_EXECUTOR.execute(() -> {
            List<PantryItem> pantry = db.pantryDao().getAllNow();
            List<RecipeWithIngredients> recipes = db.recipeDao().getAllWithIngredientsNow();
            List<RecipeWithIngredients> suggested = RecipeMatcher.getSuggested(recipes, pantry);

            runOnUiThread(() -> {
                adapter.setItems(suggested);
                // Empty state: show a message instead of a blank screen
                textEmpty.setVisibility(suggested.isEmpty() ? View.VISIBLE : View.GONE);
            });
        });
    }

    private void onRecipeTapped(RecipeWithIngredients recipe) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.recipe.id);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
