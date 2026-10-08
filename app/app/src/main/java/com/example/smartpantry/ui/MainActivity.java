package com.example.smartpantry.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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
}
