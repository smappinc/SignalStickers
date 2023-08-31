package com.app.webdroid.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.app.webdroid.BuildConfig;
import com.app.webdroid.R;
import com.app.webdroid.database.prefs.SharedPref;
import com.app.webdroid.fragment.FragmentWebView;
import com.app.webdroid.util.Tools;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.snackbar.Snackbar;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.text.DecimalFormat;

public class ActivitySettings extends AppCompatActivity {

    SharedPref sharedPref;
    LinearLayout parentView;
    MaterialSwitch switchTheme;
    RelativeLayout btnSwitchTheme;
    TextView txtCacheSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tools.getTheme(this);
        setContentView(R.layout.activity_settings);
        sharedPref = new SharedPref(this);
        Tools.setNavigation(this);
        initView();
        setupToolbar();
    }

    public void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        Tools.setupToolbar(this, toolbar, getString(R.string.txt_title_settings), true);
    }

    private void initView() {
        parentView = findViewById(R.id.parent_view);

        switchTheme = findViewById(R.id.switch_theme);
        switchTheme.setChecked(sharedPref.getIsDarkTheme());
        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPref.setIsDarkTheme(isChecked);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }, 250);
        });

        btnSwitchTheme = findViewById(R.id.btn_switch_theme);
        btnSwitchTheme.setOnClickListener(v -> {
            if (switchTheme.isChecked()) {
                sharedPref.setIsDarkTheme(false);
                switchTheme.setChecked(false);
            } else {
                sharedPref.setIsDarkTheme(true);
                switchTheme.setChecked(true);
            }
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }, 250);
        });

        findViewById(R.id.btn_notification).setOnClickListener(v -> {
            Intent intent = new Intent();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, BuildConfig.APPLICATION_ID);
            } else {
                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                intent.putExtra("app_package", BuildConfig.APPLICATION_ID);
                intent.putExtra("app_uid", getApplicationInfo().uid);
            }
            startActivity(intent);
        });

        txtCacheSize = findViewById(R.id.txt_cache_size);
        initializeCache();

        findViewById(R.id.lyt_clear_cache).setOnClickListener(v -> clearCache());

        findViewById(R.id.btn_privacy_policy).setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ActivityWebView.class);
            intent.putExtra("title", getString(R.string.menu_privacy));
            intent.putExtra("link", sharedPref.getPrivacyPolicyUrl());
            startActivity(intent);
        });

        findViewById(R.id.btn_share).setOnClickListener(v -> Tools.shareApp(this, getString(R.string.share_text)));

        findViewById(R.id.btn_rate).setOnClickListener(v -> Tools.rateApp(this));

        findViewById(R.id.btn_more).setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(sharedPref.getMoreAppsUrl()))));

        findViewById(R.id.btn_about).setOnClickListener(v -> Tools.showAboutDialog(this));

    }

    public void loadWebPage(String name, String type, String url) {
        new Handler().postDelayed(() -> {
            FragmentWebView argumentFragment = new FragmentWebView();
            Bundle data = new Bundle();
            data.putString("name", name);
            data.putString("type", type);
            data.putString("url", url);
            argumentFragment.setArguments(data);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, argumentFragment).commit();
        }, 250);
    }

    private void clearCache() {
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(ActivitySettings.this);
        dialog.setMessage(R.string.msg_clear_cache);
        dialog.setPositiveButton(R.string.dialog_option_yes, (dialogInterface, i) -> {
            FileUtils.deleteQuietly(getCacheDir());
            FileUtils.deleteQuietly(getExternalCacheDir());
            txtCacheSize.setText(getString(R.string.sub_setting_clear_cache_start) + " 0 Bytes " + getString(R.string.sub_setting_clear_cache_end));
            Snackbar.make(findViewById(android.R.id.content), getString(R.string.msg_cache_cleared), Snackbar.LENGTH_SHORT).show();
        });
        dialog.setNegativeButton(R.string.dialog_option_cancel, null);
        dialog.show();
    }

    private void initializeCache() {
        txtCacheSize.setText(getString(R.string.sub_setting_clear_cache_start) + " " + readableFileSize((0 + getDirSize(getCacheDir())) + getDirSize(getExternalCacheDir())) + " " + getString(R.string.sub_setting_clear_cache_end));
    }

    public long getDirSize(File dir) {
        long size = 0;
        for (File file : dir.listFiles()) {
            if (file != null && file.isDirectory()) {
                size += getDirSize(file);
            } else if (file != null && file.isFile()) {
                size += file.length();
            }
        }
        return size;
    }

    public static String readableFileSize(long size) {
        if (size <= 0) {
            return "0 Bytes";
        }
        String[] units = new String[]{"Bytes", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10((double) size) / Math.log10(1024.0d));
        StringBuilder stringBuilder = new StringBuilder();
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.#");
        double d = (double) size;
        double pow = Math.pow(1024.0d, (double) digitGroups);
        Double.isNaN(d);
        stringBuilder.append(decimalFormat.format(d / pow));
        stringBuilder.append(" ");
        stringBuilder.append(units[digitGroups]);
        return stringBuilder.toString();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

}
