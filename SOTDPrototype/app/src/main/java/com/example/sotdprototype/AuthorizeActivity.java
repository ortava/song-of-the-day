package com.example.sotdprototype;

import static com.spotify.sdk.android.auth.AuthorizationResponse.Type.TOKEN;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

public class AuthorizeActivity extends AppCompatActivity {
    private static final String CLIENT_ID = "b1a4a0e63d4745198ab789e13e42314d";
    private static final int REQUEST_CODE = 1337;
    private static final String REDIRECT_URI = "http://localhost:3000";
    private static final String[] SCOPES =
            {"app-remote-control",  // "Remote control playback of Spotify." (play/go-to tracks and get track info)
            "user-top-read",        // "Read access to a user's top artists and tracks." (for recommendations)
            "user-read-private"};   // "Read access to user’s subscription details (type of user account)." (check for premium)


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart(){
        super.onStart();

        openSpotifyLoginActivity();
    }

    private void openSpotifyLoginActivity() {
        AuthorizationRequest.Builder builder =
                new AuthorizationRequest.Builder(CLIENT_ID, TOKEN, REDIRECT_URI);

        builder.setScopes(SCOPES);
        AuthorizationRequest request = builder.build();

        AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if(requestCode == REQUEST_CODE){
            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, intent);

            switch(response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    // do stuff with successful response
                    Log.i("Info:", "Authorization token acquired!");
                    startMainActivity();
                    finish();
                    break;

                case ERROR:
                    // do stuff with error response
                    Log.e("Error:", "Received Error token response.");
                    startPrescreenActivity();
                    finish();
                    break;

                default:
                    // do stuff in default case
                    Log.i("Info:", "DEFAULT - Did not receive auth token nor error token upon attempt to authorize.");
                    startPrescreenActivity();
                    finish();
            }
        }
    }

    private void startMainActivity() {
        Intent newIntent = new Intent(AuthorizeActivity.this, MainActivity.class);
        startActivity(newIntent);
    }

    private void startPrescreenActivity() {
        Intent newIntent = new Intent(AuthorizeActivity.this, PrescreenActivity.class);
        startActivity(newIntent);
    }
}