package com.newalitraders.oos16theme;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {

    private final Map<String, Integer> iconMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureWindow();
        buildIconMap();
        setContentView(buildHome());
        applyWallpaperIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent() != null && Intent.ACTION_MAIN.equals(getIntent().getAction())) {
            // refresh launcher list in case apps changed
            setContentView(buildHome());
        }
    }

    private void configureWindow() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        if (android.os.Build.VERSION.SDK_INT >= 30) {
            WindowInsetsController c = getWindow().getInsetsController();
            if (c != null) c.setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS);
        }
    }

    private View buildHome() {
        FrameLayout frame = new FrameLayout(this);

        ImageView wallpaper = new ImageView(this);
        wallpaper.setImageResource(R.drawable.wallpaper_home);
        wallpaper.setScaleType(ImageView.ScaleType.CENTER_CROP);
        frame.addView(wallpaper, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));

        LinearLayout overlay = new LinearLayout(this);
        overlay.setOrientation(LinearLayout.VERTICAL);
        overlay.setPadding(dp(18), dp(42), dp(18), dp(18));
        overlay.setGravity(Gravity.CENTER_HORIZONTAL);
        frame.addView(overlay, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));

        TextClock clock = new TextClock(this);
        clock.setFormat12Hour("h:mm");
        clock.setFormat24Hour("HH:mm");
        clock.setTextSize(58);
        clock.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        clock.setTextColor(Color.WHITE);
        clock.setShadowLayer(8f, 0f, 3f, 0x66000000);
        overlay.addView(clock, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        TextClock date = new TextClock(this);
        date.setFormat12Hour("EEE, d MMM");
        date.setFormat24Hour("EEE, d MMM");
        date.setTextSize(16);
        date.setTypeface(Typeface.DEFAULT_BOLD);
        date.setTextColor(Color.WHITE);
        date.setShadowLayer(6f, 0f, 2f, 0x66000000);
        overlay.addView(date);

        TextView brand = new TextView(this);
        brand.setText("NEW ALI TRADERS");
        brand.setTextSize(18);
        brand.setTypeface(Typeface.DEFAULT_BOLD);
        brand.setTextColor(Color.WHITE);
        brand.setGravity(Gravity.CENTER);
        brand.setPadding(0, dp(8), 0, dp(12));
        brand.setShadowLayer(5f, 0f, 2f, 0x66000000);
        overlay.addView(brand);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f
        );
        overlay.addView(scroll, sp);

        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(4);
        grid.setUseDefaultMargins(false);
        grid.setPadding(dp(2), dp(8), dp(2), dp(8));
        scroll.addView(grid, new ScrollView.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT,
                ScrollView.LayoutParams.WRAP_CONTENT
        ));

        List<ResolveInfo> apps = getLauncherApps();
        for (ResolveInfo info : apps) {
            grid.addView(buildAppTile(info), tileParams());
        }

        LinearLayout dock = new LinearLayout(this);
        dock.setGravity(Gravity.CENTER);
        dock.setOrientation(LinearLayout.HORIZONTAL);
        dock.setPadding(dp(6), dp(6), dp(6), dp(4));
        dock.setBackgroundColor(0x55FFFFFF);
        overlay.addView(dock, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        addDockShortcut(dock, "Phone", "com.google.android.dialer", R.drawable.phone);
        addDockShortcut(dock, "Messages", "com.google.android.apps.messaging", R.drawable.messages);
        addDockShortcut(dock, "Camera", "com.google.android.GoogleCamera", R.drawable.camera);
        addDockShortcut(dock, "WhatsApp", "com.whatsapp", R.drawable.whatsapp);

        return frame;
    }

    private List<ResolveInfo> getLauncherApps() {
        PackageManager pm = getPackageManager();
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> list = pm.queryIntentActivities(i, PackageManager.MATCH_ALL);
        List<ResolveInfo> filtered = new ArrayList<>();
        for (ResolveInfo r : list) {
            if (r.activityInfo == null) continue;
            if (getPackageName().equals(r.activityInfo.packageName)) continue;
            filtered.add(r);
        }
        Collections.sort(filtered, Comparator.comparing(
                x -> x.loadLabel(pm).toString().toLowerCase()
        ));
        return filtered;
    }

    private View buildAppTile(ResolveInfo info) {
        PackageManager pm = getPackageManager();
        String pkg = info.activityInfo.packageName;
        String label = info.loadLabel(pm).toString();

        LinearLayout tile = new LinearLayout(this);
        tile.setOrientation(LinearLayout.VERTICAL);
        tile.setGravity(Gravity.CENTER);
        tile.setPadding(dp(3), dp(8), dp(3), dp(8));

        ImageView icon = new ImageView(this);
        Drawable d = getThemedIcon(pkg, info);
        icon.setImageDrawable(d);
        icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        tile.addView(icon, new LinearLayout.LayoutParams(dp(62), dp(62)));

        TextView name = new TextView(this);
        name.setText(label);
        name.setSingleLine(true);
        name.setTextSize(11);
        name.setGravity(Gravity.CENTER);
        name.setTextColor(Color.WHITE);
        name.setShadowLayer(5f, 0f, 1f, 0x99000000);
        LinearLayout.LayoutParams np = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        np.topMargin = dp(4);
        tile.addView(name, np);

        tile.setOnClickListener(v -> launch(info));
        tile.setOnLongClickListener(v -> {
            try {
                Intent x = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                x.setData(android.net.Uri.parse("package:" + pkg));
                startActivity(x);
            } catch (Exception ignored) {}
            return true;
        });
        return tile;
    }

    private GridLayout.LayoutParams tileParams() {
        GridLayout.LayoutParams p = new GridLayout.LayoutParams();
        p.width = 0;
        p.height = dp(100);
        p.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        p.setMargins(dp(3), dp(3), dp(3), dp(3));
        return p;
    }

    private Drawable getThemedIcon(String pkg, ResolveInfo info) {
        Integer res = iconMap.get(pkg);
        if (res == null) {
            if (pkg.contains("dialer")) res = R.drawable.phone;
            else if (pkg.contains("contacts")) res = R.drawable.contacts;
            else if (pkg.contains("messag")) res = R.drawable.messages;
            else if (pkg.contains("camera")) res = R.drawable.camera;
            else if (pkg.contains("photo") || pkg.contains("gallery")) res = R.drawable.gallery;
            else if (pkg.contains("settings")) res = R.drawable.settings;
            else if (pkg.contains("calculator")) res = R.drawable.calculator;
            else if (pkg.contains("calendar")) res = R.drawable.calendar;
            else if (pkg.contains("clock")) res = R.drawable.clock;
            else if (pkg.contains("file")) res = R.drawable.files;
            else if (pkg.contains("weather")) res = R.drawable.weather;
            else if (pkg.contains("music")) res = R.drawable.music;
            else if (pkg.contains("video")) res = R.drawable.videos;
        }
        if (res != null) {
            try { return getDrawable(res); } catch (Exception ignored) {}
        }
        return info.loadIcon(getPackageManager());
    }

    private void buildIconMap() {
        iconMap.put("com.google.android.dialer", R.drawable.phone);
        iconMap.put("com.android.dialer", R.drawable.phone);
        iconMap.put("com.google.android.contacts", R.drawable.contacts);
        iconMap.put("com.google.android.apps.messaging", R.drawable.messages);
        iconMap.put("com.whatsapp", R.drawable.whatsapp);
        iconMap.put("com.google.android.GoogleCamera", R.drawable.camera);
        iconMap.put("com.google.android.apps.photos", R.drawable.gallery);
        iconMap.put("com.android.settings", R.drawable.settings);
        iconMap.put("com.android.chrome", R.drawable.chrome);
        iconMap.put("com.android.vending", R.drawable.play_store);
        iconMap.put("com.google.android.youtube", R.drawable.youtube);
        iconMap.put("com.google.android.gm", R.drawable.gmail);
        iconMap.put("com.google.android.apps.maps", R.drawable.maps);
        iconMap.put("com.google.android.calendar", R.drawable.calendar);
        iconMap.put("com.google.android.deskclock", R.drawable.clock);
        iconMap.put("com.google.android.calculator", R.drawable.calculator);
        iconMap.put("com.google.android.keep", R.drawable.keep_notes);
        iconMap.put("com.google.android.apps.nbu.files", R.drawable.files);
        iconMap.put("com.google.android.apps.docs", R.drawable.drive);
        iconMap.put("com.google.android.apps.youtube.music", R.drawable.youtube_music);
        iconMap.put("com.google.android.apps.recorder", R.drawable.recorder);
        iconMap.put("com.google.android.apps.translate", R.drawable.translate);
        iconMap.put("com.google.android.apps.walletnfcrel", R.drawable.wallet);
        iconMap.put("com.google.android.googlequicksearchbox", R.drawable.voice_assistant);
        iconMap.put("com.google.android.apps.tachyon", R.drawable.meet);
        iconMap.put("com.oneplus.calculator", R.drawable.calculator);
        iconMap.put("com.oneplus.deskclock", R.drawable.clock);
        iconMap.put("com.oneplus.filemanager", R.drawable.files);
        iconMap.put("com.oneplus.calendar", R.drawable.calendar);
        iconMap.put("net.oneplus.weather", R.drawable.weather);
        iconMap.put("com.oneplus.note", R.drawable.notes);
    }

    private void launch(ResolveInfo info) {
        try {
            ComponentName c = new ComponentName(
                    info.activityInfo.packageName,
                    info.activityInfo.name
            );
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            i.setComponent(c);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            startActivity(i);
        } catch (Exception e) {
            Toast.makeText(this, "Unable to open app", Toast.LENGTH_SHORT).show();
        }
    }

    private void addDockShortcut(LinearLayout dock, String label, String pkg, int iconRes) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.VERTICAL);
        item.setGravity(Gravity.CENTER);

        ImageView icon = new ImageView(this);
        icon.setImageResource(iconRes);
        item.addView(icon, new LinearLayout.LayoutParams(dp(58), dp(58)));

        TextView t = new TextView(this);
        t.setText(label);
        t.setTextSize(10);
        t.setTextColor(Color.WHITE);
        t.setGravity(Gravity.CENTER);
        t.setShadowLayer(4f, 0f, 1f, 0x99000000);
        item.addView(t);

        item.setOnClickListener(v -> {
            Intent launch = getPackageManager().getLaunchIntentForPackage(pkg);
            if (launch != null) startActivity(launch);
            else Toast.makeText(this, label + " not found", Toast.LENGTH_SHORT).show();
        });

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        dock.addView(item, p);
    }

    private void applyWallpaperIfNeeded() {
        // The launcher itself always renders the bundled wallpaper.
        // Also attempt to set the real home wallpaper once, so other surfaces match.
        getSharedPreferences("theme", MODE_PRIVATE);
        if (!getSharedPreferences("theme", MODE_PRIVATE).getBoolean("wallpaper_set", false)) {
            if (applyWallpaperInternal(R.drawable.wallpaper_home, WallpaperManager.FLAG_SYSTEM)) {
                getSharedPreferences("theme", MODE_PRIVATE)
                        .edit().putBoolean("wallpaper_set", true).apply();
            }
        }
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
