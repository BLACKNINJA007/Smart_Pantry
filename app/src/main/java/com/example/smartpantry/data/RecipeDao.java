package com.example.smartpantry.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface RecipeDao {

    @Insert
    long insertRecipe(Recipe recipe);

    @Insert
    void insertIngredients(List<RecipeIngredient> ingredients);

    @Query("SELECT COUNT(*) FROM recipes")
    int count();

    @Transaction
    @Query("SELECT * FROM recipes ORDER BY name")
    LiveData<List<RecipeWithIngredients>> getAllWithIngredients();

    @Transaction
    @Query("SELECT * FROM recipes")
    List<RecipeWithIngredients> getAllWithIngredientsNow();

    @Transaction
    @Query("SELECT * FROM recipes WHERE id = :id")
    RecipeWithIngredients getById(long id);
}
