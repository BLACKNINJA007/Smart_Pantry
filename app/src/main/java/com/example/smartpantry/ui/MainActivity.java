package com.example.smartpantry.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.R;
import com.example.smartpantry.data.AppDatabase;
import com.example.smartpantry.data.PantryDao;
import com.example.smartpantry.data.PantryItem;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

/** Pantry List screen: shows every ingredient from the database. */
public class MainActivity extends AppCompatActivity {

    private PantryAdapter adapter;
    private TextView textEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textEmpty = findViewById(R.id.textEmpty);

        RecyclerView recycler = findViewById(R.id.recyclerPantry);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PantryAdapter(this::onItemTapped);
        recycler.setAdapter(adapter);
        enableSwipeToDelete(recycler);

        // LiveData: this callback fires on first load AND every time the table changes.
        AppDatabase.getInstance(this).pantryDao().getAll().observe(this, items -> {
            adapter.setItems(items);
            textEmpty.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
        });

        FloatingActionButton fab = findViewById(R.id.fabAdd);
        // Explicit Intent: open the Add/Edit screen with no extras = "add mode"
        fab.setOnClickListener(v ->
                startActivity(new Intent(this, AddEditActivity.class)));
    }

    private void onItemTapped(PantryItem item) {
        // Explicit Intent carrying the row id = "edit mode"
        Intent intent = new Intent(this, AddEditActivity.class);
        intent.putExtra(AddEditActivity.EXTRA_ITEM_ID, item.id);
        startActivity(intent);
    }

    /** Swipe a row left or right to delete it, with an Undo option. */
    private void enableSwipeToDelete(RecyclerView recycler) {
        PantryDao dao = AppDatabase.getInstance(this).pantryDao();

        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView rv,
                                  @NonNull RecyclerView.ViewHolder from,
                                  @NonNull RecyclerView.ViewHolder to) {
                return false; // we only care about swipes, not drag-to-reorder
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder holder, int direction) {
                int position = holder.getBindingAdapterPosition();
                if (position == RecyclerView.NO_POSITION) return;

                PantryItem removed = adapter.getItemAt(position);
                AppDatabase.DB_EXECUTOR.execute(() -> dao.delete(removed));       // Delete

                Snackbar.make(recycler, removed.name + " deleted", Snackbar.LENGTH_LONG)
                        .setAction("Undo", v ->
                                AppDatabase.DB_EXECUTOR.execute(() -> dao.insert(removed)))
                        .show();
            }
        };
        new ItemTouchHelper(callback).attachToRecyclerView(recycler);
    }

    // Toolbar button that opens the Suggested Recipes screen
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_suggested) {
            startActivity(new Intent(this, SuggestedActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
