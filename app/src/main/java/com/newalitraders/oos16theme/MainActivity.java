package com.newalitraders.oos16theme;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(dp(22), dp(42), dp(22), dp(28));
        root.setBackgroundColor(Color.rgb(246, 243, 239));

        TextView title = new TextView(this);
        title.setText("NEW ALI TRADERS");
        title.setTextSize(28);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setTextColor(Color.rgb(23, 50, 77));
        title.setGravity(Gravity.CENTER);
        root.addView(title, matchWrap());

        TextView subtitle = new TextView(this);
        subtitle.setText("OxygenOS 16 Soft 3D Matte Icon Pack\nAndroid 16 / API 36");
        subtitle.setTextSize(16);
        subtitle.setTextColor(Color.rgb(95, 113, 130));
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0, dp(8), 0, dp(26));
        root.addView(subtitle, matchWrap());

        TextView status = new TextView(this);
        status.setText("This build is an icon pack for the stock launcher — not a replacement HOME launcher.\nIt includes transparent 3D icons plus fallback styling for unmapped apps.");
        status.setTextSize(14);
        status.setTextColor(Color.rgb(65, 85, 102));
        status.setGravity(Gravity.CENTER);
        status.setPadding(dp(10), dp(14), dp(10), dp(22));
        root.addView(status, matchWrap());

        root.addView(button("Apply Home + Lock Wallpapers", v -> applyBothWallpapers()), matchWrap());
        root.addView(spacer());

        root.addView(button("Open Wallpaper / Style Settings", v -> {
            try {
                startActivity(new Intent(Intent.ACTION_SET_WALLPAPER));
            } catch (Exception e) {
                startActivity(new Intent(Settings.ACTION_SETTINGS));
            }
        }), matchWrap());

        root.addView(spacer());

        TextView help = new TextView(this);
        help.setText("For icons: long-press an empty area on the OxygenOS Home screen → Icons / Icon style → choose NEW ALI TRADERS Theme.\n\nIf this pack was already selected, installing this update should refresh the same selected pack.");
        help.setTextSize(14);
        help.setTextColor(Color.rgb(95, 113, 130));
        help.setGravity(Gravity.CENTER);
        help.setPadding(0, dp(24), 0, 0);
        root.addView(help, matchWrap());

        setContentView(root);
    }

    private Button button(String text, View.OnClickListener listener) {
        Button b = new Button(this);
        b.setText(text);
        b.setTextSize(16);
        b.setAllCaps(false);
        b.setOnClickListener(listener);
        return b;
    }

    private View spacer() {
        View v = new View(this);
        v.setLayoutParams(new LinearLayout.LayoutParams(1, dp(12)));
        return v;
    }

    private LinearLayout.LayoutParams matchWrap() {
        return new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
    }

    private void applyBothWallpapers() {
        boolean home = applyWallpaperInternal(R.drawable.wallpaper_home, WallpaperManager.FLAG_SYSTEM);
        boolean lock = applyWallpaperInternal(R.drawable.wallpaper_lock, WallpaperManager.FLAG_LOCK);
        Toast.makeText(this,
                home && lock ? "Home + Lock wallpapers applied" : "Wallpaper apply failed",
                Toast.LENGTH_LONG).show();
    }

    private boolean applyWallpaperInternal(int drawableRes, int flag) {
        WallpaperManager wm = WallpaperManager.getInstance(this);
        if (!wm.isWallpaperSupported()) return false;
        try (InputStream in = getResources().openRawResource(drawableRes)) {
            wm.setStream(in, null, true, flag);
            return true;
        } catch (IOException | SecurityException e) {
            return false;
        }
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
