package com.example.sotdprototype.spotify;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class SpotifyHelper {
    public static void openTrackInSpotify(String uri, String packageName, Context context) {
        final String branchLink = "https://spotify.link/content_linking?~campaign=" + packageName + "&$deeplink_path=" + uri + "&$fallback_url=" + uri;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(branchLink));
        startActivity(context, intent, null);
    }
}
