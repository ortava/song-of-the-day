package com.sotd;

import static com.spotify.sdk.android.auth.AuthorizationResponse.Type.TOKEN;

import android.content.Intent;
import android.content.SharedPreferences;
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

    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSharedPreferences = this.getSharedPreferences("SPOTIFY", 0);
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

        if(requestCode == REQUEST_CODE){
            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, intent);

            switch(response.getType()) {
                case TOKEN:
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putString("token", response.getAccessToken());
                    editor.apply();
                    Log.i("INFO", "TOKEN - Authorization token acquired!");
                    startMainActivity();
                    break;

                case ERROR:
                    Log.e("ERROR", "ERROR - Received Error token response.");
                    startPrescreenActivity();
                    break;

                default:
                    Log.d("DEBUG", "DEFAULT - Did not receive auth token nor error token upon attempt to authorize.");
                    startPrescreenActivity();
            }
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(AuthorizeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void startPrescreenActivity() {
        Intent intent = new Intent(AuthorizeActivity.this, PrescreenActivity.class);
        startActivity(intent);
        finish();
    }
}