package com.example.smartpantry.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PantryDao {

    @Insert
    long insert(PantryItem item);

    @Update
    void update(PantryItem item);

    @Delete
    void delete(PantryItem item);

    /** Observed by the list screen - refreshes automatically when the table changes. */
    @Query("SELECT * FROM pantry_items ORDER BY name COLLATE NOCASE")
    LiveData<List<PantryItem>> getAll();

    /** Plain (non-LiveData) version for background work such as recipe matching. */
    @Query("SELECT * FROM pantry_items")
    List<PantryItem> getAllNow();

    @Query("SELECT * FROM pantry_items WHERE id = :id")
    PantryItem getById(long id);
}
