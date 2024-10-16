package com.sotd.spotify;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class SpotifyHelper {
    /**
        Opens track with the given URI in the user's Spotify app.
        @param uri          URI identifying the Spotify track to be opened.
        @param packageName  Name of the application package from which the application is making the request.
        @param context      Application context from which the application is making the request.
     */
    public static void openTrackInSpotify(String uri, String packageName, Context context) {
        final String branchLink = "https://spotify.link/content_linking?~campaign=" + packageName + "&$deeplink_path=" + uri + "&$fallback_url=" + uri;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(branchLink));
        startActivity(context, intent, null);
    }
}
