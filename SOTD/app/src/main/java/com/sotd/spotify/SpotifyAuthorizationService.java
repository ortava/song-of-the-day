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

// The methods in this class follow the PKCE authorization flow.
// (see: https://developer.spotify.com/documentation/web-api/tutorials/code-pkce-flow)
public class SpotifyAuthorizationService {
    //TODO: Find a better place to store CLIENT_ID (config file?) (Currently needed for this class, HomeFragment, and AuthorizeActivity).
    private static final String CLIENT_ID = "b1a4a0e63d4745198ab789e13e42314d";
    private SharedPreferences sharedPreferences;
    private RequestQueue queue;

    public SpotifyAuthorizationService(Context context) {
        sharedPreferences = context.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(context);
    }

    /**
     *  Makes a request to the Spotify Web API for authorization tokens and saves them in sharedPreferences.
     *  There are two types of tokens: Access tokens and refresh tokens
     *      Access tokens are required to access Spotify Web API functionality (get recommendations for example).
     *      Refresh tokens are required to get new access tokens.
     *      Access tokens expire after 1 hour. Refresh tokens don't expire.
     *  @param  authCode    The authorization code returned from the previous request [in the PKCE authorization flow].
     *  @param  redirectURI This parameter is used for validation only (there is no actual redirection).
     *                          The value of this parameter must exactly match the value of redirect_uri supplied when requesting the authorization code.
     *  @param  callBack    Volley callback function.
     */
    public void acquireAuthTokens(String authCode, String redirectURI, final VolleyCallBack callBack) {
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
                    } catch(JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("SpotifyAuthorizationService", "Successfully acquired authorization tokens!");
                    callBack.onSuccess();
                }, error -> {
                    Log.e("SpotifyAuthorizationService", "Could not get authorization tokens.");
                    if(error.networkResponse.data != null) {
                        Log.e("SpotifyAuthorizationService", new String(error.networkResponse.data, StandardCharsets.UTF_8));
                    }
                    callBack.onError(error);
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

    /**
     *  Makes a request to the Spotify Web API to refresh the access token.
     *  As we are following the PKCE authorization flow,
     *  we receive a new refresh token along with the new access token, so both types of token will be saved/updated.
     */
    public void refreshAccessToken(VolleyCallBack callBack) {
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
                    } catch(JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("SpotifyAuthorizationService", "Successfully refreshed access token!");
                    callBack.onSuccess();
                }, error -> {
                    Log.e("SpotifyAuthorizationService", "Could not refresh access token.");
                    if(error.networkResponse.data != null) {
                        Log.e("SpotifyAuthorizationService", new String(error.networkResponse.data, StandardCharsets.UTF_8));
                    }
                    callBack.onError(error);
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
