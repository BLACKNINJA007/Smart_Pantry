package com.example.smartpantry.data;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/** One required ingredient line of a recipe (one recipe has many of these). */
@Entity(
        tableName = "recipe_ingredients",
        foreignKeys = @ForeignKey(
                entity = Recipe.class,
                parentColumns = "id",
                childColumns = "recipeId",
                onDelete = CASCADE),
        indices = @Index("recipeId"))
public class RecipeIngredient {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long recipeId;
    public String name;
    public double quantity;
    public String unit;

    public RecipeIngredient() { }

    @Ignore
    public RecipeIngredient(long recipeId, String name, double quantity, String unit) {
        this.recipeId = recipeId;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }
}
