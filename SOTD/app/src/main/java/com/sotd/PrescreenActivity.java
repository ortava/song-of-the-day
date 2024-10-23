package com.sotd;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.sotd.databinding.ActivityPrescreenBinding;

public class PrescreenActivity extends AppCompatActivity {

    private ActivityPrescreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPrescreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final Button buttonConnectSpotify = binding.buttonConnectSpotify;
        buttonConnectSpotify.setOnClickListener(v -> startAuthorizeActivity());
    }

    private void startAuthorizeActivity() {
        Intent newIntent = new Intent(this, AuthorizeActivity.class);
        startActivity(newIntent);
        finish();
    }
}