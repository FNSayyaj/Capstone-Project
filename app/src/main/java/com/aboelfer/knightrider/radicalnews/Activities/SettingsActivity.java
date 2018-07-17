package com.aboelfer.knightrider.radicalnews.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.aboelfer.knightrider.radicalnews.Fragments.SettingsFragment;
import com.aboelfer.knightrider.radicalnews.R;

public class SettingsActivity extends AppCompatActivity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getFragmentManager().beginTransaction()
                .replace(R.id.settings_container, new SettingsFragment())
                .commit();
    }
}
