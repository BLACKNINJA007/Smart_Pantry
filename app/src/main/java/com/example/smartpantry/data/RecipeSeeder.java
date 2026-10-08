package com.example.smartpantry.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Fills the recipes table with 20 starter recipes the first time the app runs.
 * Unit convention used for every recipe: solids in g, liquids in ml, spoons in tbsp/tsp,
 * and countable things (eggs, onions, ...) in "pieces".
 */
public class RecipeSeeder {

    /** Only seeds when the table is empty, so recipes are never duplicated. */
    public static void seedIfEmpty(AppDatabase db) {
        if (db.recipeDao().count() > 0) return;
        db.runInTransaction(() -> seedAll(db.recipeDao()));
    }

    private static void seedAll(RecipeDao dao) {
        add(dao, "Scrambled Eggs",
                "Whisk the eggs with salt.\nMelt butter in a pan on low heat.\nPour in eggs and stir gently until just set.",
                i("eggs", 3, "pieces"), i("butter", 10, "g"), i("salt", 1, "tsp"));

        add(dao, "Cheese Omelette",
                "Whisk eggs with salt.\nCook in buttered pan until almost set.\nAdd cheese, fold and serve.",
                i("eggs", 2, "pieces"), i("cheese", 30, "g"), i("butter", 10, "g"), i("salt", 1, "tsp"));

        add(dao, "Pancakes",
                "Mix flour, milk and eggs into a smooth batter.\nPour small rounds into a hot oiled pan.\nFlip when bubbles form and cook until golden.",
                i("flour", 200, "g"), i("milk", 300, "ml"), i("eggs", 2, "pieces"), i("sugar", 20, "g"));

        add(dao, "Egg Fried Rice",
                "Fry chopped onion in oil.\nAdd cooked rice and stir fry.\nPush aside, scramble the eggs, then mix together with salt.",
                i("rice", 200, "g"), i("eggs", 2, "pieces"), i("onion", 1, "pieces"),
                i("oil", 1, "tbsp"), i("salt", 1, "tsp"));

        add(dao, "Cheese Toastie",
                "Butter the outside of the bread.\nPut cheese between the slices.\nToast in a pan until golden on both sides.",
                i("bread", 2, "pieces"), i("cheese", 40, "g"), i("butter", 10, "g"));

        add(dao, "French Toast",
                "Whisk eggs, milk and sugar.\nDip the bread slices in the mixture.\nFry in butter until golden.",
                i("bread", 4, "pieces"), i("eggs", 2, "pieces"), i("milk", 100, "ml"),
                i("sugar", 10, "g"), i("butter", 10, "g"));

        add(dao, "Tomato Pasta",
                "Boil the pasta in salted water.\nFry garlic in oil, add chopped tomatoes and simmer.\nToss the pasta through the sauce.",
                i("pasta", 200, "g"), i("tomatoes", 4, "pieces"), i("garlic", 2, "pieces"),
                i("oil", 2, "tbsp"), i("salt", 1, "tsp"));

        add(dao, "Garlic Butter Pasta",
                "Boil the pasta.\nMelt butter and fry crushed garlic gently.\nToss pasta in the garlic butter and season.",
                i("pasta", 200, "g"), i("butter", 30, "g"), i("garlic", 3, "pieces"), i("salt", 1, "tsp"));

        add(dao, "Mashed Potatoes",
                "Peel and boil the potatoes until soft.\nDrain and mash with butter and milk.\nSeason with salt.",
                i("potatoes", 4, "pieces"), i("butter", 30, "g"), i("milk", 50, "ml"), i("salt", 1, "tsp"));

        add(dao, "Roast Potatoes",
                "Heat the oven to 200C.\nToss chopped potatoes in oil and salt.\nRoast for 40 minutes, turning once.",
                i("potatoes", 6, "pieces"), i("oil", 3, "tbsp"), i("salt", 1, "tsp"));

        add(dao, "Chicken Rice Bowl",
                "Cook the rice.\nFry onion and diced chicken in oil until cooked through.\nServe the chicken over the rice.",
                i("chicken", 300, "g"), i("rice", 200, "g"), i("onion", 1, "pieces"), i("oil", 1, "tbsp"));

        add(dao, "Chicken Stir-Fry",
                "Slice the chicken, carrots and onion.\nStir fry chicken in hot oil until cooked.\nAdd vegetables and salt, fry for 5 minutes.",
                i("chicken", 300, "g"), i("carrots", 2, "pieces"), i("onion", 1, "pieces"),
                i("oil", 2, "tbsp"), i("salt", 1, "tsp"));

        add(dao, "Banana Oat Pancakes",
                "Mash the bananas.\nMix with eggs and oats.\nFry spoonfuls in a pan until golden on both sides.",
                i("bananas", 2, "pieces"), i("eggs", 2, "pieces"), i("oats", 60, "g"));

        add(dao, "Creamy Porridge",
                "Put oats and milk in a pot.\nStir over medium heat for 5 minutes.\nServe with a drizzle of honey.",
                i("oats", 80, "g"), i("milk", 300, "ml"), i("honey", 1, "tbsp"));

        add(dao, "Tuna Pasta",
                "Boil the pasta.\nFry onion in oil, then add the tuna.\nMix with the drained pasta.",
                i("pasta", 200, "g"), i("tuna", 1, "pieces"), i("onion", 1, "pieces"), i("oil", 1, "tbsp"));

        add(dao, "Tomato Soup",
                "Fry onion and garlic in butter.\nAdd chopped tomatoes and simmer for 20 minutes.\nBlend until smooth and season.",
                i("tomatoes", 6, "pieces"), i("onion", 1, "pieces"), i("garlic", 2, "pieces"),
                i("butter", 20, "g"), i("salt", 1, "tsp"));

        add(dao, "Veggie Omelette",
                "Chop the tomato and onion.\nSoften them in a pan, add spinach until wilted.\nPour in whisked eggs and salt, cook until set.",
                i("eggs", 3, "pieces"), i("tomatoes", 1, "pieces"), i("onion", 1, "pieces"),
                i("spinach", 30, "g"), i("salt", 1, "tsp"));

        add(dao, "Bean Stew",
                "Fry onion and garlic in oil.\nAdd chopped tomatoes and beans.\nSimmer for 20 minutes and season with salt.",
                i("beans", 400, "g"), i("onion", 1, "pieces"), i("tomatoes", 2, "pieces"),
                i("garlic", 2, "pieces"), i("oil", 1, "tbsp"), i("salt", 1, "tsp"));

        add(dao, "Peanut Butter Banana Toast",
                "Toast the bread.\nSpread with peanut butter.\nTop with sliced banana.",
                i("bread", 2, "pieces"), i("peanut butter", 2, "tbsp"), i("bananas", 1, "pieces"));

        add(dao, "Spaghetti Bolognese",
                "Brown the mince with onion and garlic.\nAdd chopped tomatoes and simmer for 30 minutes.\nServe over boiled pasta.",
                i("mince", 300, "g"), i("pasta", 200, "g"), i("tomatoes", 4, "pieces"),
                i("onion", 1, "pieces"), i("garlic", 2, "pieces"));
    }

    /** Inserts one recipe, then its ingredient rows linked by the new recipe id. */
    private static void add(RecipeDao dao, String name, String steps, RecipeIngredient... ingredients) {
        long recipeId = dao.insertRecipe(new Recipe(name, steps));
        List<RecipeIngredient> rows = new ArrayList<>();
        for (RecipeIngredient ri : ingredients) {
            ri.recipeId = recipeId;
            rows.add(ri);
        }
        dao.insertIngredients(rows);
    }

    private static RecipeIngredient i(String name, double quantity, String unit) {
        return new RecipeIngredient(0, name, quantity, unit);
    }
}
