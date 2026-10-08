package com.example.smartpantry.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = {PantryItem.class, Recipe.class, RecipeIngredient.class},
        version = 1,
        exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract PantryDao pantryDao();
    public abstract RecipeDao recipeDao();

    private static volatile AppDatabase INSTANCE;

    /** Room forbids DB writes on the main thread, so writes go through this. */
    public static final ExecutorService DB_EXECUTOR = Executors.newFixedThreadPool(2);

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    final Context appContext = context.getApplicationContext();
                    INSTANCE = Room.databaseBuilder(appContext, AppDatabase.class, "smart_pantry.db")
                            .addCallback(new RoomDatabase.Callback() {
                                @Override
                                public void onOpen(@NonNull SupportSQLiteDatabase db) {
                                    super.onOpen(db);
                                    // Seed on a background thread; does nothing if recipes exist.
                                    DB_EXECUTOR.execute(() ->
                                            RecipeSeeder.seedIfEmpty(getInstance(appContext)));
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
