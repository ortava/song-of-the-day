package com.example.sotdprototype;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.sotdprototype.databinding.ActivityMainBinding;
import com.example.sotdprototype.databinding.ActivityPrescreenBinding;

public class PrescreenActivity extends AppCompatActivity {

    private ActivityPrescreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPrescreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final Button buttonLoginSpotify = binding.buttonLoginSpotify;
        buttonLoginSpotify.setOnClickListener(v -> startAuthorizeActivity());
    }

    private void startAuthorizeActivity() {
        Intent newIntent = new Intent(this, AuthorizeActivity.class);
        startActivity(newIntent);
        finish();
    }
}