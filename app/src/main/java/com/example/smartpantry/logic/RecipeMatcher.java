package com.example.smartpantry.logic;

import com.example.smartpantry.data.PantryItem;
import com.example.smartpantry.data.RecipeIngredient;
import com.example.smartpantry.data.RecipeWithIngredients;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The strict-matching rule (assignment section 2.3).
 * A recipe is suggested ONLY if every ingredient is in the pantry in at least the required
 * quantity. One missing or too-small ingredient excludes the whole recipe.
 */
public class RecipeMatcher {

    /** "Tomatoes" -> "tomato", "Eggs" -> "egg", "Berries" -> "berry". Applied to BOTH sides. */
    public static String normalise(String name) {
        String n = name == null ? "" : name.trim().toLowerCase(Locale.ROOT);
        if (n.endsWith("ies")) {
            n = n.substring(0, n.length() - 3) + "y";
        } else if (n.endsWith("oes")) {
            n = n.substring(0, n.length() - 2);
        } else if (n.endsWith("s") && !n.endsWith("ss")) {
            n = n.substring(0, n.length() - 1);
        }
        return n;
    }

    private static String cleanUnit(String unit) {
        return unit == null ? "" : unit.trim().toLowerCase(Locale.ROOT);
    }

    /** Converts to a base unit (g, ml or count) so that 1 kg == 1000 g. */
    static double toBase(double qty, String unit) {
        switch (cleanUnit(unit)) {
            case "kg":   return qty * 1000;
            case "l":    return qty * 1000;
            case "cup":
            case "cups": return qty * 240;
            case "tbsp": return qty * 15;
            case "tsp":  return qty * 5;
            default:     return qty;   // g, ml, pieces
        }
    }

    /** Units only compare within the same family (weight / volume / count). */
    static String family(String unit) {
        switch (cleanUnit(unit)) {
            case "g":
            case "kg":
                return "WEIGHT";
            case "ml":
            case "l":
            case "cup":
            case "cups":
            case "tbsp":
            case "tsp":
                return "VOLUME";
            default:
                return "COUNT";
        }
    }

    private static String key(String name, String unit) {
        return normalise(name) + "|" + family(unit);
    }

    /** Returns only the recipes the user can cook right now. */
    public static List<RecipeWithIngredients> getSuggested(
            List<RecipeWithIngredients> recipes, List<PantryItem> pantry) {

        // 1. Total stock per (ingredient, unit family). Duplicate rows are added together.
        Map<String, Double> stock = new HashMap<>();
        for (PantryItem p : pantry) {
            stock.merge(key(p.name, p.unit), toBase(p.quantity, p.unit), Double::sum);
        }

        // 2. Keep a recipe only if every ingredient is covered.
        List<RecipeWithIngredients> result = new ArrayList<>();
        for (RecipeWithIngredients r : recipes) {
            if (canMake(r, stock)) {
                result.add(r);
            }
        }
        return result;
    }

    private static boolean canMake(RecipeWithIngredients recipe, Map<String, Double> stock) {
        for (RecipeIngredient ri : recipe.ingredients) {
            double have = stock.getOrDefault(key(ri.name, ri.unit), 0.0);
            double need = toBase(ri.quantity, ri.unit);
            if (have < need) {
                return false;   // one miss = excluded
            }
        }
        return true;
    }
}
