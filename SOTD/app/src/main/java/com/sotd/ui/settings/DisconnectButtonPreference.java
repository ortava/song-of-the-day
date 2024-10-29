package com.sotd.ui.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.sotd.PrescreenActivity;
import com.sotd.R;

public class DisconnectButtonPreference extends Preference {
    public DisconnectButtonPreference(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public DisconnectButtonPreference(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DisconnectButtonPreference(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DisconnectButtonPreference(@NonNull Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(@NonNull PreferenceViewHolder viewHolder) {
        super.onBindViewHolder(viewHolder);

        Button button = (Button) viewHolder.findViewById(R.id.button_disconnect_spotify);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Remove Spotify Access Token.
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("SPOTIFY", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("token");
                editor.apply();

                // Redirect to login prompt.
                Intent intent = new Intent(getContext(), PrescreenActivity.class);
                getContext().startActivity(intent);
                ((Activity)getContext()).finish();
            }
        });
    }
}
