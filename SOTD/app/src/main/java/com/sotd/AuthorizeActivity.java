package com.sotd;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;
import com.sotd.spotify.SpotifyAuthorizationService;
import com.sotd.spotify.VolleyCallBack;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class AuthorizeActivity extends AppCompatActivity {
    private static final String CLIENT_ID = "b1a4a0e63d4745198ab789e13e42314d";
    private static final String REDIRECT_URI = "https://sotd.com/callback";
    private static final String SCOPES =
            "app-remote-control" + " " +    // "Remote control playback of Spotify." (for playing songs remotely via SpotifyAppRemote)
            "user-read-private";            // "Read access to user’s subscription details (type of user account)." (check for premium)

    private SharedPreferences mSharedPreferences;
    private SpotifyAuthorizationService mSpotifyAuthorizationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSharedPreferences = this.getSharedPreferences("SPOTIFY", 0);
        mSpotifyAuthorizationService = new SpotifyAuthorizationService(getApplicationContext());
    }

    @Override
    protected void onStart(){
        super.onStart();

        if(getIntent().getData() == null) {
            openSpotifyBrowserLogin();
        } else {
            String authCode = getIntent().getData().getQueryParameter("code");
            mSpotifyAuthorizationService.acquireAuthTokens(authCode, REDIRECT_URI, new VolleyCallBack() {
                @Override
                public void onSuccess() {
                    // Auth tokens acquired, code verifier no longer needed.
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.remove("code_verifier");
                    editor.apply();
                    startMainActivity();
                }

                @Override
                public void onError(VolleyError error) {
                    clearSpotifyPreferences();  // Clear any stored authorization data before prompting login.
                    startPrescreenActivity();
                }
            });
        }
    }

    private void openSpotifyBrowserLogin() {
        String codeVerifier = getCodeVerifier();

        // Build URI for Auth Code request.
        StringBuilder endpoint = new StringBuilder();
        endpoint.append("https://accounts.spotify.com/authorize");
        endpoint.append("?client_id=").append(CLIENT_ID);
        endpoint.append("&response_type=code");
        endpoint.append("&redirect_uri=").append(REDIRECT_URI);
        endpoint.append("&scope=").append(SCOPES);
        endpoint.append("&code_challenge_method=S256");
        endpoint.append("&code_challenge=").append(getCodeChallenge(codeVerifier));
        endpoint.append("&show_dialog=true");

        // Save code verifier used for acquiring auth code so we can use it again when acquiring auth tokens.
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("code_verifier", codeVerifier);
        editor.apply();

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(endpoint.toString()));
        startActivity(browserIntent);
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

    private void clearSpotifyPreferences() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    /// METHODS THAT HELP WITH THE PKCE AUTHORIZATION FLOW (see: https://developer.spotify.com/documentation/web-api/tutorials/code-pkce-flow)
    // WITH GREAT HELP FROM THIS POST ON STACKOVERFLOW: https://stackoverflow.com/questions/68750229/how-to-impement-spotifys-authorization-code-with-pkce-in-kotlin
    private String getCodeVerifier() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] code = new byte[64];
        secureRandom.nextBytes(code);
        return Base64.encodeToString(
                code,
                Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING
        );
    }

    private String getCodeChallenge(String verifier) {
        byte[] bytes = verifier.getBytes();
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(bytes, 0, bytes.length);
            byte[] digest = messageDigest.digest();
            return Base64.encodeToString(
                    digest,
                    Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING
            );
        } catch(NoSuchAlgorithmException e) {
            Log.e("AuthorizeActivity", "NoSuchAlgorithmException message: " + e.getMessage());
            return "";
        }
    }
}