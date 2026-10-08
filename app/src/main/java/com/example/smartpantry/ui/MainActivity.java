package com.example.smartpantry.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.R;
import com.example.smartpantry.data.AppDatabase;
import com.example.smartpantry.data.PantryItem;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

        // LiveData: this callback fires on first load AND every time the table changes.
        AppDatabase.getInstance(this).pantryDao().getAll().observe(this, items -> {
            adapter.setItems(items);
            textEmpty.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
        });

        FloatingActionButton fab = findViewById(R.id.fabAdd);
        // Placeholder - replaced by an Intent to AddEditActivity on Day 2
        fab.setOnClickListener(v ->
                Toast.makeText(this, "Add screen comes next", Toast.LENGTH_SHORT).show());
    }

    private void onItemTapped(PantryItem item) {
        // Placeholder - becomes the Edit flow on Day 2
        Toast.makeText(this, item.name, Toast.LENGTH_SHORT).show();
    }
}
