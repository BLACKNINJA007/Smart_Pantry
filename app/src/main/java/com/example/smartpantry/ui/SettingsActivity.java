package com.example.smartpantry.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantry.R;
import com.example.smartpantry.logic.AppSettings;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;

/** Settings screen: one toggle for the expiring-soon highlight on the pantry list. */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SwitchMaterial switchExpiry = findViewById(R.id.switchExpiry);
        switchExpiry.setChecked(AppSettings.isExpiryAlertOn(this));
        switchExpiry.setOnCheckedChangeListener((button, isChecked) ->
                AppSettings.setExpiryAlertOn(this, isChecked));

        BottomNavigationView nav = findViewById(R.id.bottomNav);
        NavHelper.setup(this, nav, R.id.nav_settings);
        NavHelper.applyInsets(findViewById(R.id.root), findViewById(R.id.appBar), nav);
    }
}
