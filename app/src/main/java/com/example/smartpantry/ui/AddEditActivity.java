package com.example.smartpantry.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantry.R;
import com.example.smartpantry.data.AppDatabase;
import com.example.smartpantry.data.PantryDao;
import com.example.smartpantry.data.PantryItem;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Add / Edit Ingredient screen.
 * Started with an Intent. If the Intent carries EXTRA_ITEM_ID we are editing that row,
 * otherwise we are adding a new one.
 */
public class AddEditActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_ID = "item_id";

    private TextInputLayout layoutName, layoutQuantity;
    private TextInputEditText inputName, inputQuantity, inputExpiry;
    private Spinner spinnerUnit;
    private ArrayAdapter<CharSequence> unitAdapter;

    private PantryDao dao;
    private long itemId = -1;          // -1 means "adding a new item"
    private Long expiryMillis = null;  // null means "no expiry date"

    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        dao = AppDatabase.getInstance(this).pantryDao();

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        layoutName = findViewById(R.id.layoutName);
        layoutQuantity = findViewById(R.id.layoutQuantity);
        inputName = findViewById(R.id.inputName);
        inputQuantity = findViewById(R.id.inputQuantity);
        inputExpiry = findViewById(R.id.inputExpiry);
        spinnerUnit = findViewById(R.id.spinnerUnit);
        Button buttonSave = findViewById(R.id.buttonSave);
        Button buttonClearDate = findViewById(R.id.buttonClearDate);
        Button buttonDelete = findViewById(R.id.buttonDelete);

        unitAdapter = ArrayAdapter.createFromResource(
                this, R.array.units, android.R.layout.simple_spinner_item);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnit.setAdapter(unitAdapter);

        inputExpiry.setOnClickListener(v -> showDatePicker());
        buttonClearDate.setOnClickListener(v -> {
            expiryMillis = null;
            inputExpiry.setText("");
        });
        buttonSave.setOnClickListener(v -> save());

        // Did the caller pass an id? Then this is the Edit flow.
        itemId = getIntent().getLongExtra(EXTRA_ITEM_ID, -1);
        if (itemId != -1) {
            toolbar.setTitle("Edit ingredient");
            buttonDelete.setVisibility(View.VISIBLE);   // only makes sense when editing
            buttonDelete.setOnClickListener(v -> confirmDelete());
            loadItem();
        }
    }

    /** Reads the row on a background thread, then fills the form on the UI thread. */
    private void loadItem() {
        AppDatabase.DB_EXECUTOR.execute(() -> {
            PantryItem item = dao.getById(itemId);
            runOnUiThread(() -> {
                if (item == null) {
                    finish();
                    return;
                }
                inputName.setText(item.name);
                inputQuantity.setText(formatQuantity(item.quantity));
                spinnerUnit.setSelection(Math.max(0, unitAdapter.getPosition(item.unit)));
                expiryMillis = item.expiryDate;
                if (expiryMillis != null) {
                    inputExpiry.setText(dateFormat.format(new Date(expiryMillis)));
                }
            });
        });
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        if (expiryMillis != null) {
            cal.setTimeInMillis(expiryMillis);
        }
        new DatePickerDialog(this, (view, year, month, day) -> {
            Calendar picked = Calendar.getInstance();
            picked.set(year, month, day, 0, 0, 0);
            expiryMillis = picked.getTimeInMillis();
            inputExpiry.setText(dateFormat.format(new Date(expiryMillis)));
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    /** Validates the form. Returns true only if every field is acceptable. */
    private boolean validate(String name, String quantityText) {
        layoutName.setError(null);
        layoutQuantity.setError(null);
        boolean valid = true;

        if (name.isEmpty()) {
            layoutName.setError("Enter an ingredient name");
            valid = false;
        } else if (name.length() > 40) {
            layoutName.setError("Name is too long (max 40 characters)");
            valid = false;
        } else if (!name.matches("[\\p{L} '\\-]+")) {
            layoutName.setError("Use letters only");
            valid = false;
        }

        if (quantityText.isEmpty()) {
            layoutQuantity.setError("Enter a quantity");
            valid = false;
        } else {
            try {
                double qty = Double.parseDouble(quantityText);
                if (qty <= 0) {
                    layoutQuantity.setError("Quantity must be more than 0");
                    valid = false;
                }
            } catch (NumberFormatException e) {
                layoutQuantity.setError("Enter a valid number");
                valid = false;
            }
        }
        return valid;
    }

    private void save() {
        String name = String.valueOf(inputName.getText()).trim();
        String quantityText = String.valueOf(inputQuantity.getText()).trim();

        if (!validate(name, quantityText)) {
            return; // stay on the screen, errors are shown under the fields
        }

        double quantity = Double.parseDouble(quantityText);
        String unit = String.valueOf(spinnerUnit.getSelectedItem());

        AppDatabase.DB_EXECUTOR.execute(() -> {
            if (itemId == -1) {
                dao.insert(new PantryItem(name, quantity, unit, expiryMillis));   // Create
            } else {
                PantryItem item = new PantryItem(name, quantity, unit, expiryMillis);
                item.id = itemId;
                dao.update(item);                                                 // Update
            }
            runOnUiThread(this::finish);
        });
    }

    /** Asks before deleting so a mis-tap does not lose data. */
    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete ingredient?")
                .setMessage("This will remove it from your pantry.")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) ->
                        AppDatabase.DB_EXECUTOR.execute(() -> {
                            PantryItem item = new PantryItem();
                            item.id = itemId;
                            dao.delete(item);                                    // Delete
                            runOnUiThread(this::finish);
                        }))
                .show();
    }

    private String formatQuantity(double q) {
        return (q == Math.floor(q)) ? String.valueOf((long) q) : String.valueOf(q);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
