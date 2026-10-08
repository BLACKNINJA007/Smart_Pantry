package com.example.smartpantry.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantry.R;
import com.example.smartpantry.data.AppDatabase;
import com.example.smartpantry.data.RecipeIngredient;
import com.example.smartpantry.data.RecipeWithIngredients;
import com.google.android.material.appbar.MaterialToolbar;

/** Recipe Detail screen: full ingredient list and method for the recipe that was tapped. */
public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "recipe_id";

    private MaterialToolbar toolbar;
    private TextView textName, textIngredients, textSteps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        textName = findViewById(R.id.textName);
        textIngredients = findViewById(R.id.textIngredients);
        textSteps = findViewById(R.id.textSteps);

        // The Intent from the list screen carries the id of the recipe to show
        long recipeId = getIntent().getLongExtra(EXTRA_RECIPE_ID, -1);

        AppDatabase.DB_EXECUTOR.execute(() -> {
            RecipeWithIngredients result =
                    AppDatabase.getInstance(this).recipeDao().getById(recipeId);
            runOnUiThread(() -> {
                if (result == null) {
                    finish();
                } else {
                    show(result);
                }
            });
        });
    }

    private void show(RecipeWithIngredients result) {
        toolbar.setTitle(result.recipe.name);
        textName.setText(result.recipe.name);

        StringBuilder ingredients = new StringBuilder();
        for (RecipeIngredient ri : result.ingredients) {
            ingredients.append("\u2022 ")
                    .append(capitalise(ri.name))
                    .append(": ")
                    .append(formatQuantity(ri.quantity))
                    .append(" ")
                    .append(ri.unit)
                    .append("\n");
        }
        textIngredients.setText(ingredients.toString().trim());

        // Steps are stored one per line, so number them here
        String[] steps = result.recipe.steps.split("\n");
        StringBuilder method = new StringBuilder();
        for (int i = 0; i < steps.length; i++) {
            method.append(i + 1).append(". ").append(steps[i]).append("\n");
        }
        textSteps.setText(method.toString().trim());
    }

    private String capitalise(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    private String formatQuantity(double q) {
        return (q == Math.floor(q)) ? String.valueOf((long) q) : String.valueOf(q);
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
