package com.sotd.spotify;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class SpotifyAuthorizationService {
    //TODO: Find a better place to store CLIENT_ID (config file?) (Currently needed for both this class and AuthorizeActivity).
    private static final String CLIENT_ID = "b1a4a0e63d4745198ab789e13e42314d";
    private SharedPreferences sharedPreferences;
    private RequestQueue queue;

    public SpotifyAuthorizationService(Context context) {
        sharedPreferences = context.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(context);
    }

    public void acquireAuthTokens(String authCode, String redirectURI, final VolleyCallBack callBack) {
        String endpoint = "https://accounts.spotify.com/api/token";

        // Getting Status 400 (Bad Request) "Invalid code_verifier" when using JsonObjectRequest.
        // Works with StringRequest though...
        StringRequest stringRequest = new StringRequest
                (Request.Method.POST, endpoint, response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String refreshToken = jsonResponse.getString("refresh_token");
                        String accessToken = jsonResponse.getString("access_token");
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("access_token", accessToken);
                        editor.putString("refresh_token", refreshToken);
                        editor.apply();
                    } catch(JSONException e) {
                        e.printStackTrace();
                    }

                    callBack.onSuccess();
                }, error -> {
                    Log.e("API ERROR", "Could not get authorization tokens.");
                    Log.e("API ERROR", new String(error.networkResponse.data, StandardCharsets.UTF_8));
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("content-type", "application/x-www-form-urlencoded");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("grant_type", "authorization_code");
                params.put("code", authCode);
                params.put("redirect_uri", redirectURI);
                params.put("client_id", CLIENT_ID);
                params.put("code_verifier", sharedPreferences.getString("code_verifier", ""));

                return params;
            }
        };

        queue.add(stringRequest);
    }

    public void refreshAccessToken() {
        String endpoint = "https://accounts.spotify.com/api/token";

        StringRequest stringRequest = new StringRequest
                (Request.Method.POST, endpoint, response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String refreshToken = jsonResponse.getString("refresh_token");
                        String accessToken = jsonResponse.getString("access_token");
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("access_token", accessToken);
                        editor.putString("refresh_token", refreshToken);
                        editor.apply();
                        Log.d("API SUCCESS", "Successfully refreshed access token!");
                    } catch(JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
                    Log.e("API ERROR", "Could not refresh access token.");
                    Log.e("API ERROR", new String(error.networkResponse.data, StandardCharsets.UTF_8));
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("content-type", "application/x-www-form-urlencoded");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("grant_type", "refresh_token");
                params.put("refresh_token", sharedPreferences.getString("refresh_token", ""));
                params.put("client_id", CLIENT_ID);

                return params;
            }
        };

        queue.add(stringRequest);
    }
}
