package com.sotd.spotify;

import com.android.volley.VolleyError;

public interface VolleyCallBack {
    void onSuccess();

    void onError(VolleyError error);
}
