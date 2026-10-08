package com.example.smartpantry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.example.smartpantry.data.PantryItem;
import com.example.smartpantry.data.Recipe;
import com.example.smartpantry.data.RecipeIngredient;
import com.example.smartpantry.data.RecipeWithIngredients;
import com.example.smartpantry.logic.RecipeMatcher;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecipeMatcherTest {

    private RecipeWithIngredients recipe(String name, RecipeIngredient... ingredients) {
        RecipeWithIngredients r = new RecipeWithIngredients();
        r.recipe = new Recipe(name, "steps");
        r.ingredients = Arrays.asList(ingredients);
        return r;
    }

    private RecipeIngredient need(String name, double qty, String unit) {
        return new RecipeIngredient(0, name, qty, unit);
    }

    private PantryItem have(String name, double qty, String unit) {
        return new PantryItem(name, qty, unit, null);
    }

    private List<RecipeWithIngredients> run(RecipeWithIngredients r, PantryItem... pantry) {
        List<RecipeWithIngredients> all = new ArrayList<>();
        all.add(r);
        return RecipeMatcher.getSuggested(all, Arrays.asList(pantry));
    }

    @Test
    public void allIngredientsPresent_isSuggested() {
        RecipeWithIngredients r = recipe("Omelette", need("eggs", 2, "pieces"), need("salt", 1, "tsp"));
        assertEquals(1, run(r, have("eggs", 6, "pieces"), have("salt", 3, "tsp")).size());
    }

    @Test
    public void oneMissingIngredient_isExcluded() {
        RecipeWithIngredients r = recipe("Omelette", need("eggs", 2, "pieces"), need("salt", 1, "tsp"));
        assertTrue(run(r, have("eggs", 6, "pieces")).isEmpty());
    }

    @Test
    public void notEnoughQuantity_isExcluded() {
        RecipeWithIngredients r = recipe("Omelette", need("eggs", 3, "pieces"));
        assertTrue(run(r, have("eggs", 2, "pieces")).isEmpty());
    }

    @Test
    public void pluralAndSingularNamesMatch() {
        RecipeWithIngredients r = recipe("Soup", need("tomatoes", 2, "pieces"));
        assertEquals(1, run(r, have("Tomato", 3, "pieces")).size());
    }

    @Test
    public void unitConversionKgToG() {
        RecipeWithIngredients r = recipe("Rice bowl", need("rice", 200, "g"));
        assertEquals(1, run(r, have("Rice", 1, "kg")).size());
    }

    @Test
    public void duplicatePantryRowsAreAddedTogether() {
        RecipeWithIngredients r = recipe("Rice bowl", need("rice", 300, "g"));
        assertEquals(1, run(r, have("Rice", 200, "g"), have("rice", 200, "g")).size());
    }

    @Test
    public void incompatibleUnitFamilies_areExcluded() {
        RecipeWithIngredients r = recipe("Rice bowl", need("rice", 200, "g"));
        assertTrue(run(r, have("Rice", 5, "pieces")).isEmpty());
    }
}
