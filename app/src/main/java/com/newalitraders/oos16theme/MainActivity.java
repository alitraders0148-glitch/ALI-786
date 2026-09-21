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
        root.setPadding(dp(24), dp(40), dp(24), dp(24));
        root.setBackgroundColor(Color.rgb(246, 243, 239));

        TextView title = new TextView(this);
        title.setText("NEW ALI TRADERS");
        title.setTextSize(28);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setTextColor(Color.rgb(23, 50, 77));
        title.setGravity(Gravity.CENTER);
        root.addView(title, matchWrap());

        TextView subtitle = new TextView(this);
        subtitle.setText("OxygenOS 16 • Soft 3D Matte Theme Pack\nAndroid 16 / API 36");
        subtitle.setTextSize(16);
        subtitle.setTextColor(Color.rgb(95, 113, 130));
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0, dp(8), 0, dp(28));
        root.addView(subtitle, matchWrap());

        root.addView(button("Apply Home Wallpaper", v ->
                applyWallpaper(R.drawable.wallpaper_home, WallpaperManager.FLAG_SYSTEM)), matchWrap());
        root.addView(spacer());
        root.addView(button("Apply Lock Wallpaper", v ->
                applyWallpaper(R.drawable.wallpaper_lock, WallpaperManager.FLAG_LOCK)), matchWrap());
        root.addView(spacer());
        root.addView(button("Apply Home + Lock", v -> applyBothWallpapers()), matchWrap());
        root.addView(spacer());
        root.addView(button("Open Home / Icon Settings", v -> openHomeSettings()), matchWrap());

        TextView note = new TextView(this);
        note.setText("48 approved soft 3D matte system/app icons.\nNo building-material product icons.\nAOD artwork is bundled, but Android does not expose a public API for third-party apps to replace the system AOD theme automatically.");
        note.setTextSize(14);
        note.setTextColor(Color.rgb(95, 113, 130));
        note.setGravity(Gravity.CENTER);
        note.setPadding(0, dp(28), 0, 0);
        root.addView(note, matchWrap());

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
                home && lock ? "Home + Lock wallpapers applied" : "One or more wallpapers could not be applied",
                Toast.LENGTH_LONG).show();
    }

    private void applyWallpaper(int drawableRes, int flag) {
        boolean ok = applyWallpaperInternal(drawableRes, flag);
        Toast.makeText(this,
                ok ? "Wallpaper applied" : "Could not apply wallpaper",
                ok ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
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

    private void openHomeSettings() {
        try {
            startActivity(new Intent(Settings.ACTION_HOME_SETTINGS));
        } catch (Exception e) {
            startActivity(new Intent(Settings.ACTION_SETTINGS));
        }
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
