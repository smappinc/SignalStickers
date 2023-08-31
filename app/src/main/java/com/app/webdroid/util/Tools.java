package com.app.webdroid.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.MailTo;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.app.webdroid.BuildConfig;
import com.app.webdroid.Config;
import com.app.webdroid.R;
import com.app.webdroid.database.prefs.SharedPref;
import com.app.webdroid.fragment.FragmentWebView;
import com.app.webdroid.listener.OnCompleteListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class Tools {

    private static final String TAG = "utils";
    private Uri mCapturedImageUri;
    private static final int LOCATION_SETTINGS_PROMPT_DURATION = 10000;
    public static final int REQUEST_PERMISSION_READ_EXTERNAL_STORAGE_AND_CAMERA = 101;
    public static final int REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 102;
    public static final int REQUEST_PERMISSION_ACCESS_LOCATION = 103;
    public static final String[] DOWNLOAD_FILE_TYPES = {".*zip$", ".*rar$", ".*pdf$", ".*doc$", ".*xls$", ".*mp3$", ".*wma$", ".*ogg$", ".*m4a$", ".*wav$", ".*avi$", ".*mov$", ".*mp4$", ".*mpg$", ".*3gp$", ".*drive.google.com.*download.*", ".*dropbox.com/s/.*"};
    public static final String[] LINKS_OPENED_IN_EXTERNAL_BROWSER = {"target=blank", "target=external", "play.google.com/store", "youtube.com/watch", "facebook.com/sharer", "twitter.com/share", "plus.google.com/share"};
    public static final String[] LINKS_OPENED_IN_INTERNAL_WEBVIEW = {"target=webview", "target=internal"};
    public static boolean isAppOpen;
    public Tools() {

    }

    public static void getTheme(Context context) {
        SharedPref sharedPref = new SharedPref(context);
        if (sharedPref.getIsDarkTheme()) {
            context.setTheme(R.style.AppDarkTheme);
        } else {
            context.setTheme(R.style.AppTheme);
        }
    }

    public static void setupToolbar(AppCompatActivity activity, Toolbar toolbar, String title, boolean backButton) {
        SharedPref sharedPref = new SharedPref(activity);
        activity.setSupportActionBar(toolbar);
        if (sharedPref.getIsDarkTheme()) {
            toolbar.setBackgroundColor(activity.getResources().getColor(R.color.color_dark_toolbar));
        } else {
            toolbar.setBackgroundColor(activity.getResources().getColor(R.color.color_light_primary));
        }
        final ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(backButton);
            activity.getSupportActionBar().setHomeButtonEnabled(backButton);
            activity.getSupportActionBar().setTitle(title);
        }
    }

    public static void setNavigation(Activity activity) {
        SharedPref sharedPref = new SharedPref(activity);
        if (sharedPref.getIsDarkTheme()) {
            Tools.darkNavigation(activity);
        } else {
            Tools.lightNavigation(activity);
        }
        setLayoutDirection(activity);
    }

    public static void setLayoutDirection(Activity activity) {
        if (Config.ENABLE_RTL_MODE) {
            activity.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
    }

    public static void darkNavigation(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity.getWindow().setNavigationBarColor(ContextCompat.getColor(activity, R.color.color_dark_status_bar));
            activity.getWindow().setStatusBarColor(ContextCompat.getColor(activity, R.color.color_dark_status_bar));
            activity.getWindow().getDecorView().setSystemUiVisibility(0);
        }
    }

    public static void lightNavigation(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity.getWindow().setNavigationBarColor(ContextCompat.getColor(activity, R.color.color_white));
            activity.getWindow().setStatusBarColor(ContextCompat.getColor(activity, R.color.color_light_status_bar));
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }
    }

    public static void dialogStatusBarNavigationColor(Activity activity, boolean isDarkTheme) {
        if (isDarkTheme) {
            activity.getWindow().setNavigationBarColor(ContextCompat.getColor(activity, R.color.color_dialog_navigation_bar_dark));
            activity.getWindow().setStatusBarColor(ContextCompat.getColor(activity, R.color.color_dialog_status_bar_dark));
        } else {
            activity.getWindow().setNavigationBarColor(ContextCompat.getColor(activity, R.color.color_dialog_navigation_bar_light));
            activity.getWindow().setStatusBarColor(ContextCompat.getColor(activity, R.color.color_dialog_status_bar_light));
        }
    }

    public static boolean isDownloadableFile(String url) {
        url = url.toLowerCase();
        for (String type : DOWNLOAD_FILE_TYPES) {
            if (url.matches(type)) return true;
        }
        return false;
    }

    public static String getFileName(String url) {
        int idx = url.indexOf("?");
        if (idx > -1) {
            url = url.substring(0, idx);
        }
        url = url.toLowerCase();
        idx = url.lastIndexOf("/");
        if (idx > -1) {
            return url.substring(idx + 1);
        } else {
            return Long.toString(System.currentTimeMillis());
        }
    }

    public static void downloadFile(@NonNull Context context, @NonNull String url, @NonNull String fileName) {
        try {
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri downloadUri = Uri.parse(url);
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false)
                    .setTitle(fileName)
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                    .allowScanningByMediaScanner();
            downloadManager.enqueue(request);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }

    public void setupChooserIntent(Intent chooserIntent) {
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{createImageCaptureIntent()});
    }

    public Uri getCapturedImageUri() {
        Uri uri = null;
        if (mCapturedImageUri != null) {
            uri = mCapturedImageUri;
            mCapturedImageUri = null;
        }
        return uri;
    }

    public Uri[] getCapturedImageUris() {
        Uri[] uris = null;
        if (mCapturedImageUri != null) {
            uris = new Uri[]{mCapturedImageUri};
            mCapturedImageUri = null;
        }
        return uris;
    }

    private Intent createImageCaptureIntent() {
        mCapturedImageUri = getImageUri();
        Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageUri);
        return imageCaptureIntent;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private Uri getImageUri() {
        File externalDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File cameraDir = new File(externalDir.getAbsolutePath() + File.separator + "WebViewExample");
        cameraDir.mkdirs();
        String filePath = cameraDir.getAbsolutePath() + File.separator + System.currentTimeMillis() + ".jpg";
        return Uri.fromFile(new File(filePath));
    }

    public static boolean startIntentActivity(Context context, String url) {
        if (url != null && url.startsWith("mailto:")) {
            MailTo mailTo = MailTo.parse(url);
            Tools.startEmailActivity(context, mailTo.getTo(), mailTo.getSubject(), mailTo.getBody());
            return true;
        } else if (url != null && url.startsWith("tel:")) {
            Tools.startCallActivity(context, url);
            return true;
        } else if (url != null && url.startsWith("sms:")) {
            Tools.startSmsActivity(context, url);
            return true;
        } else if (url != null && url.startsWith("geo:")) {
            Tools.startMapSearchActivity(context, url);
            return true;
        } else if (url != null && url.startsWith("fb://")) {
            Tools.startWebActivity(context, url);
            return true;
        } else if (url != null && url.startsWith("twitter://")) {
            Tools.startWebActivity(context, url);
            return true;
        } else if (url != null && url.startsWith("whatsapp://")) {
            Tools.startWebActivity(context, url);
            return true;
        } else {
            return false;
        }
    }

    public static void startWebActivity(Context context, String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("StringBufferReplaceableByString")
    public static void startEmailActivity(Context context, String email, String subject, String text) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("mailto:");
            builder.append(email);
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(builder.toString()));
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(Intent.EXTRA_TEXT, text);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void startCallActivity(Context context, String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void startSmsActivity(Context context, String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void startMapSearchActivity(Context context, String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void startIntentChooserActivity(Context context, String url) {
        try {
            String[] results = url.split("target=");
            String type = results[1];
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.setDataAndType(Uri.parse(url), type + "/*");
            context.startActivity(Intent.createChooser(intent, context.getString(R.string.complete_action)));
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void startExternalApplication(Context context, String url) {
        try {
            String[] results = url.split("package=");
            String packageName = results[1];
            boolean isAppInstalled = appInstalledOrNot(context, packageName);
            if (isAppInstalled) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.setPackage(packageName);
                intent.setData(Uri.parse(url));
                context.startActivity(intent);
                Log.i(TAG, "Application is already installed.");
            } else {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
                Log.i(TAG, "Application is not currently installed.");
            }
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Log.i(TAG, "error : " + e.getMessage());
            Toast.makeText(context, context.getString(R.string.msg_error_handler), Toast.LENGTH_SHORT).show();
        }
    }

    private static boolean appInstalledOrNot(Context context, String uri) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG, "NameNotFoundException");
        }
        return false;
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode;
        try {
            locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return locationMode != Settings.Secure.LOCATION_MODE_OFF;
    }

    @SuppressLint("WrongConstant")
    public static void showLocationSettingsPrompt(final View view) {
        Snackbar.make(view, "Device location is disabled", LOCATION_SETTINGS_PROMPT_DURATION)
                .setAction("Settings", v -> {
                    final Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    view.getContext().startActivity(intent);
                })
                .show();
    }

    public static boolean isOnline(@NonNull Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected());
    }

    public static int getType(@NonNull Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            return networkInfo.getType();
        } else {
            return -1;
        }
    }

    public static String getTypeName(@NonNull Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null) {
            return networkInfo.getTypeName();
        } else {
            return null;
        }
    }

    public static boolean checkPermissionReadExternalStorageAndCamera(final Fragment fragment) {
        return checkPermissions(fragment, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                new int[]{R.string.permission_read_external_storage, R.string.permission_camera},
                REQUEST_PERMISSION_READ_EXTERNAL_STORAGE_AND_CAMERA);
    }

    public static boolean checkPermissionWriteExternalStorage(final Fragment fragment) {
        return checkPermissions(fragment, R.string.permission_write_external_storage);
    }

    @SuppressWarnings("UnusedReturnValue")
    public static boolean checkPermissionAccessLocation(final Fragment fragment) {
        return checkPermissions(fragment, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                new int[]{R.string.permission_access_location, R.string.permission_access_location},
                REQUEST_PERMISSION_ACCESS_LOCATION);
    }

    private static boolean checkPermissions(final Fragment fragment, final int explanation) {
        final int result = ContextCompat.checkSelfPermission(fragment.requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result != PackageManager.PERMISSION_GRANTED) {
            if (fragment.shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Snackbar.make(fragment.requireView(), explanation, Snackbar.LENGTH_INDEFINITE)
                        .setAction(android.R.string.ok, v -> fragment.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                Tools.REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE)).show();
            } else {
                fragment.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Tools.REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE);
            }
        }
        return result == PackageManager.PERMISSION_GRANTED;
    }

    @SuppressWarnings("ToArrayCallWithZeroLengthArrayArgument")
    private static boolean checkPermissions(final Fragment fragment, final String[] permissions, final int[] explanations, final int requestCode) {
        final int[] results = new int[permissions.length];
        for (int i = 0; i < permissions.length; i++) {
            results[i] = ContextCompat.checkSelfPermission(fragment.requireActivity(), permissions[i]);
        }
        final List<String> deniedPermissions = new ArrayList<>();
        for (int i = 0; i < results.length; i++) {
            if (results[i] != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(permissions[i]);
            }
        }
        if (!deniedPermissions.isEmpty()) {
            final String[] params = deniedPermissions.toArray(new String[deniedPermissions.size()]);
            boolean isShown = false;
            for (int i = 0; i < permissions.length; i++) {
                if (fragment.shouldShowRequestPermissionRationale(permissions[i])) {
                    Snackbar.make(fragment.requireView(), explanations[i], Snackbar.LENGTH_INDEFINITE)
                            .setAction(android.R.string.ok, v -> fragment.requestPermissions(params, requestCode)).show();
                    isShown = true;
                    break;
                }
            }
            if (!isShown) {
                fragment.requestPermissions(params, requestCode);
            }
        }
        return deniedPermissions.isEmpty();
    }

    public static void shareApp(Activity activity, String title) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, title + "\n\n" + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
        sendIntent.setType("text/plain");
        activity.startActivity(sendIntent);
    }

    public static void shareContent(Activity activity, String title, String message) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, title + "\n\n" + message + "\n\n" + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
        sendIntent.setType("text/plain");
        activity.startActivity(sendIntent);
    }

    public static void rateApp(Activity activity) {
        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)));
    }

    public static void moreApps(Activity activity, String moreAppsUrl) {
        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(moreAppsUrl)));
    }

    public static void showAboutDialog(Activity activity) {
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View view = layoutInflater.inflate(R.layout.dialog_about, null);
        TextView txtAppVersion = view.findViewById(R.id.txt_app_version);
        txtAppVersion.setText(activity.getString(R.string.msg_about_version) + " " + BuildConfig.VERSION_CODE + " (" + BuildConfig.VERSION_NAME + ")");
        final MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(activity);
        alert.setView(view);
        alert.setPositiveButton(R.string.dialog_option_ok, (dialog, which) -> dialog.dismiss());
        alert.show();
    }

    public static void showPrivacyPolicyDialog(Activity activity) {

        final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity);

        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View view = layoutInflater.inflate(R.layout.dialog_privacy, null);

        SharedPref sharedPref = new SharedPref(activity);

        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        ImageButton btnClose = view.findViewById(R.id.btn_close);

        WebView webView = view.findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                new Handler().postDelayed(() -> progressBar.setVisibility(View.INVISIBLE), 500);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url != null && (url.startsWith("http") || url.startsWith("https"))) {
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                }
                return true;
            }
        });
        webView.loadUrl(sharedPref.getPrivacyPolicyUrl());
        builder.setCancelable(false);
        builder.setView(view);

        final AlertDialog dialog = builder.create();
        btnClose.setOnClickListener(v -> new Handler().postDelayed(dialog::dismiss, 250));

        dialog.show();
    }

    @SuppressLint("SourceLockedOrientationActivity")
    public static void fullScreenMode(AppCompatActivity activity, boolean show) {
        SharedPref sharedPref = new SharedPref(activity);
        if (show) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().hide();
            }
            //activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            if (!sharedPref.getIsDarkTheme()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
            }
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().show();
            }
            //activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    public static void hideSystemUI(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @SuppressLint("SourceLockedOrientationActivity")
    public static void showSystemUI(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        SharedPref sharedPref = new SharedPref(activity);
        if (!sharedPref.getIsDarkTheme()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            }
        }
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public static void slideUp(Activity activity, View view) {
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(0, 0, activity.findViewById(R.id.main_content).getHeight(), 0);
        animate.setDuration(300);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    public static void slideDown(Activity activity, View view) {
        TranslateAnimation animate = new TranslateAnimation(0, 0, 0, activity.findViewById(R.id.main_content).getHeight());
        animate.setDuration(300);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    public static void setNativeAdStyle(Activity activity, LinearLayout nativeAdView, String style) {
        switch (style) {
            case "small":
            case "radio":
                nativeAdView.addView(View.inflate(activity, com.solodroid.ads.sdk.R.layout.view_native_ad_radio, null));
                break;
            case "news":
            case "medium":
                nativeAdView.addView(View.inflate(activity, com.solodroid.ads.sdk.R.layout.view_native_ad_news, null));
                break;
            default:
                nativeAdView.addView(View.inflate(activity, com.solodroid.ads.sdk.R.layout.view_native_ad_medium, null));
                break;
        }
    }

    public static String baseUrl = "";

    public static void urlChecker(Activity activity, FragmentManager fragmentManager) {
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View view = layoutInflater.inflate(R.layout.dialog_launch, null);

        EditText edtUrl = view.findViewById(R.id.edt_url);

        RadioGroup radioGroup = view.findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton radioButton = view.findViewById(checkedId);
            baseUrl = radioButton.getText().toString();
        });

        final AlertDialog.Builder alert = new MaterialAlertDialogBuilder(activity);
        alert.setView(view);
        alert.setCancelable(false);
        alert.setPositiveButton("Go", (dialog, which) -> {
            if (edtUrl.getText().toString().equals("")) {
                Toast.makeText(activity, "Url cannot be empty", Toast.LENGTH_SHORT).show();
            } else {
                new Handler().postDelayed(() -> {
                    String url = baseUrl + "://" + edtUrl.getText().toString();
                    FragmentWebView argumentFragment = new FragmentWebView();
                    Bundle data = new Bundle();
                    data.putString("name", edtUrl.getText().toString());
                    data.putString("type", "url");
                    data.putString("url", url);
                    data.putBoolean("toolbar", true);
                    argumentFragment.setArguments(data);
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, argumentFragment).commit();
                }, 250);
            }
        });
        alert.setNegativeButton("Cancel", null);
        alert.show();
    }

    public static void postDelayed(OnCompleteListener onCompleteListener, int millisecond) {
        new Handler(Looper.getMainLooper()).postDelayed(onCompleteListener::onComplete, millisecond);
    }

}
