package com.app.webdroid.fragment;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.webdroid.Config;
import com.app.webdroid.R;
import com.app.webdroid.activity.MainActivity;
import com.app.webdroid.database.prefs.SharedPref;
import com.app.webdroid.listener.DrawerStateListener;
import com.app.webdroid.listener.LoadUrlListener;
import com.app.webdroid.listener.WebViewOnTouchListener;
import com.app.webdroid.util.MyFragment;
import com.app.webdroid.util.Tools;
import com.app.webdroid.webview.AdvancedWebView;
import com.app.webdroid.webview.VideoEnabledWebChromeClient;
import com.app.webdroid.webview.VideoEnabledWebView;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.lang.ref.WeakReference;

public class FragmentWebView extends MyFragment implements AdvancedWebView.Listener {

    private MainActivity activity;
    private Toolbar toolbar;
    TextView toolbarTitle;
    private int mStoredActivityRequestCode;
    private int mStoredActivityResultCode;
    private Intent mStoredActivityResultIntent;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout lytProgress;
    private LinearProgressIndicator progressBar;
    private View lytNoNetwork;
    private View lytNoPage;
    View rootView, parentView;
    AdvancedWebView webView;
    String pageType;
    String pageUrl;
    String pageTitle;
    Button btnRetry;
    SharedPref sharedPref;

    public FragmentWebView() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        if (getArguments() != null) {
            pageTitle = getArguments().getString("name");
            pageType = getArguments().getString("type");
            if (pageType.equals("assets")) {
                pageUrl = "file:///android_asset/" + getArguments().getString("url");
            } else {
                pageUrl = getArguments().getString("url");
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_webview, container, false);
        parentView = activity.findViewById(R.id.main_non_video_layout);
        sharedPref = new SharedPref(activity);
        initView();
        setupToolbar();
        return rootView;
    }

    private void initView() {
        lytProgress = rootView.findViewById(R.id.lyt_progress);
        progressBar = rootView.findViewById(R.id.progressBar);
        if (sharedPref.getIsDarkTheme()) {
            progressBar.setIndicatorColor(ContextCompat.getColor(activity, R.color.color_dark_progress_indicator));
        } else {
            progressBar.setIndicatorColor(ContextCompat.getColor(activity, R.color.color_light_progress_indicator));
        }

        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.color_light_primary);
        swipeRefreshLayout.setRefreshing(!Config.ENABLE_LINEAR_PROGRESS_INDICATOR);
        webView = rootView.findViewById(R.id.webView);
        lytNoNetwork = rootView.findViewById(R.id.lyt_no_network);
        lytNoPage = rootView.findViewById(R.id.lyt_no_page);
        btnRetry = rootView.findViewById(R.id.btn_retry);
        toolbar = rootView.findViewById(R.id.toolbar);
        toolbarTitle = rootView.findViewById(R.id.toolbar_title);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (sharedPref.getNavigationDrawer()) {
            activity.setupNavigationDrawer(toolbar);
        }
        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState);
        }

        initComponents();

        if (sharedPref.getGeolocation()) {
            Tools.checkPermissionAccessLocation(this);
        }

        loadData();

        swipeRefreshLayout.setOnRefreshListener(this::refreshData);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (Tools.checkPermissionReadExternalStorageAndCamera(this)) {
            webView.onActivityResult(requestCode, resultCode, intent);
        } else {
            mStoredActivityRequestCode = requestCode;
            mStoredActivityResultCode = resultCode;
            mStoredActivityResultIntent = intent;
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        setUserVisibleHint(true);
        webView.saveState(outState);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Tools.REQUEST_PERMISSION_READ_EXTERNAL_STORAGE_AND_CAMERA:
            case Tools.REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE:
            case Tools.REQUEST_PERMISSION_ACCESS_LOCATION: {
                if (grantResults.length > 0) {
                    for (int grantResult : grantResults) {
                        if (grantResult == PackageManager.PERMISSION_GRANTED) {
                            if (requestCode == Tools.REQUEST_PERMISSION_READ_EXTERNAL_STORAGE_AND_CAMERA) {
                                if (mStoredActivityResultIntent != null) {
                                    webView.onActivityResult(mStoredActivityRequestCode, mStoredActivityResultCode, mStoredActivityResultIntent);
                                    mStoredActivityRequestCode = 0;
                                    mStoredActivityResultCode = 0;
                                    mStoredActivityResultIntent = null;
                                }
                            }
                        }
                    }
                }
                break;
            }
        }
    }

    @SuppressLint({"ClickableViewAccessibility", "SetJavaScriptEnabled"})
    private void initComponents() {
        webView.getSettings().setJavaScriptEnabled(true);

        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        //webView.getSettings().setSupportMultipleWindows(true);
        //webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        //webView cache
        if (sharedPref.getCache()) {
            webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
            webView.getSettings().setDomStorageEnabled(true);
            webView.getSettings().setDatabaseEnabled(true);
        }

        if (!sharedPref.getUserAgent().equals("")) {
            webView.getSettings().setUserAgentString(sharedPref.getUserAgent());
        }

        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(sharedPref.getBuiltInZoomControls());

        //advanced webView settings
        webView.setListener(activity, FragmentWebView.this);

        if (sharedPref.getGeolocation()) {
            webView.getSettings().setGeolocationEnabled(true);
            webView.setGeolocationEnabled(true);
        }

        //webView style
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        //webView hardware acceleration
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        //webView chrome client
        View nonVideoLayout = activity.findViewById(R.id.main_non_video_layout);
        ViewGroup videoLayout = activity.findViewById(R.id.main_video_layout);
        @SuppressLint("InflateParams") View progressView = activity.getLayoutInflater().inflate(R.layout.lyt_progress, null);
        VideoEnabledWebChromeClient webChromeClient = new VideoEnabledWebChromeClient(nonVideoLayout, videoLayout, progressView, (VideoEnabledWebView) webView, progressBar);
        webChromeClient.setOnToggledFullscreen(new WebViewToggledFullscreenCallback());
        webView.setWebChromeClient(webChromeClient);

        //webView client
        webView.setWebViewClient(new MyWebViewClient());

        //webView key listener
        webView.setOnKeyListener(new WebViewOnKeyDownListener(activity));

        //webView touch listener
        webView.requestFocus(View.FOCUS_DOWN);
        webView.setOnTouchListener(new WebViewOnTouchListener());

    }

    public class WebViewOnKeyDownListener implements View.OnKeyListener {

        private final WeakReference<DrawerStateListener> mDrawerStateListener;

        public WebViewOnKeyDownListener(DrawerStateListener drawerStateListener) {
            mDrawerStateListener = new WeakReference<>(drawerStateListener);
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                WebView webView = (WebView) v;
                DrawerStateListener drawerStateListener = mDrawerStateListener.get();
                if (drawerStateListener != null && keyCode == KeyEvent.KEYCODE_BACK) {
                    if (webView.canGoBack() && !drawerStateListener.isDrawerOpen()) {
                        webView.goBack();
                        if (Config.ENABLE_LINEAR_PROGRESS_INDICATOR) {
                            lytProgress.setVisibility(View.GONE);
                        }
                    } else {
                        drawerStateListener.onBackButtonPressed();
                    }
                    return true;
                }
            }
            return false;
        }
    }

    private void loadData() {
        if (activity != null) {
            if (Tools.isOnline(activity)) {
                webView.loadUrl(pageUrl);
            } else {
                showProgress(false);
                showSwipeRefresh(false);
                Tools.postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 1000);
                lytNoNetwork.setVisibility(View.VISIBLE);
                webView.setVisibility(View.GONE);
                retryData();
            }
        }
    }

    public void refreshData() {
        if (activity != null) {
            if (Tools.isOnline(activity)) {
                showProgress(true);
                showSwipeRefresh(true);
                lytNoNetwork.setVisibility(View.GONE);
                lytNoPage.setVisibility(View.GONE);
                String pageUrl = webView.getUrl();
                if (pageUrl == null || pageUrl.equals("")) pageUrl = this.pageUrl;
                webView.loadUrl(pageUrl);
            } else {
                showProgress(false);
                showSwipeRefresh(false);
                lytNoNetwork.setVisibility(View.VISIBLE);
                webView.setVisibility(View.GONE);
                retryData();
            }
        }
    }

    private void retryData() {
        btnRetry.setOnClickListener(v -> refreshData());
    }

    private class WebViewToggledFullscreenCallback implements VideoEnabledWebChromeClient.ToggledFullscreenCallback {
        @Override
        public void toggledFullscreen(boolean fullScreen) {
            if (fullScreen) {
                Tools.hideSystemUI(activity);
            } else {
                Tools.showSystemUI(activity);
            }
        }
    }

    private class MyWebViewClient extends WebViewClient {

        private boolean mSuccess = true;

        @SuppressWarnings("deprecation")
        @Override
        public void onPageFinished(final WebView view, final String url) {
            runTaskCallback(() -> {
                if (activity != null && mSuccess) {
                    showProgress(false);
                    Tools.postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 1000);
                    webView.setVisibility(View.VISIBLE);
                    CookieSyncManager.getInstance().sync();
                }
                mSuccess = true;
            });
        }

        @Override
        public void onReceivedError(final WebView view, final int errorCode, final String description, final String failingUrl) {
            runTaskCallback(() -> {
                if (activity != null) {
                    mSuccess = false;
                    showProgress(false);
                    Tools.postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 1000);
                    lytNoPage.setVisibility(View.VISIBLE);
                    webView.setVisibility(View.GONE);
                }
            });
        }

        @TargetApi(Build.VERSION_CODES.M)
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            showProgress(false);
            onReceivedError(view, error.getErrorCode(), error.getDescription().toString(), request.getUrl().toString());
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (Config.ENABLE_LINEAR_PROGRESS_INDICATOR) {
                lytProgress.setVisibility(View.VISIBLE);
            }
            if (Tools.isDownloadableFile(url) || url.contains("?target=download")) {
                if (Build.VERSION.SDK_INT > 28) {
                    activity.showSnackBar(getString(R.string.msg_download));
                    Tools.downloadFile(activity, url, Tools.getFileName(url));
                } else if (Tools.checkPermissionWriteExternalStorage(FragmentWebView.this)) {
                    activity.showSnackBar(getString(R.string.msg_download));
                    Tools.downloadFile(activity, url, Tools.getFileName(url));
                    return true;
                }
                return true;
            } else if (url.startsWith("http://") || url.startsWith("https://")) {
                if (url.contains("target=video") || url.contains("target=audio") || url.contains("target=image")) {
                    Tools.startIntentChooserActivity(activity, url);
                    return true;
                } else if (url.contains("target=url-checker")) {
                    activity.urlChecker();
                    return true;
                } else if (url.contains("package=")) {
                    Tools.startExternalApplication(activity, url);
                    return true;
                } else {
                    ((LoadUrlListener) activity).onLoadUrl(url);
                    boolean external = isLinkExternal(url);
                    boolean internal = isLinkInternal(url);
                    if (!external && !internal) {
                        external = sharedPref.getOpenLinkInExternalBrowser();
                    }
                    if (external) {
                        Tools.startWebActivity(activity, url);
                        return true;
                    } else {
                        showProgress(true);
                        showSwipeRefresh(true);
                        return false;
                    }
                }
            } else if (url.startsWith("file://")) {
                ((LoadUrlListener) activity).onLoadUrl(url);
                return false;
            } else {
                return Tools.startIntentActivity(getContext(), url);
            }
        }
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
    }

    @Override
    public void onPageFinished(String url) {
    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {
        if (Tools.checkPermissionWriteExternalStorage(FragmentWebView.this)) {
            activity.showSnackBar(getString(R.string.msg_download));
            Tools.downloadFile(activity, url, Tools.getFileName(url));
        }
    }

    @Override
    public void onExternalPageRequest(String url) {
    }

    private boolean isLinkExternal(String url) {
        for (String rule : Tools.LINKS_OPENED_IN_EXTERNAL_BROWSER) {
            if (url.contains(rule)) return true;
        }
        return false;
    }

    private boolean isLinkInternal(String url) {
        for (String rule : Tools.LINKS_OPENED_IN_INTERNAL_WEBVIEW) {
            if (url.contains(rule)) return true;
        }
        return false;
    }

    private void setupToolbar() {
        toolbar.setTitle("");
        toolbarTitle.setText(pageTitle);
        activity.setSupportActionBar(toolbar);
        if (sharedPref.getToolbar()) {
            toolbar.setVisibility(View.VISIBLE);
        } else {
            toolbar.setVisibility(View.GONE);
        }

        if (sharedPref.getIsDarkTheme()) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.color_dark_toolbar));
            toolbar.getContext().setTheme(androidx.appcompat.R.style.ThemeOverlay_AppCompat_Dark);
            toolbarTitle.setTextColor(ContextCompat.getColor(activity, R.color.color_dark_title_toolbar));
        } else {
            toolbar.setBackgroundColor(getResources().getColor(R.color.color_light_primary));
            toolbar.setPopupTheme(androidx.appcompat.R.style.ThemeOverlay_AppCompat_Light);
            toolbarTitle.setTextColor(ContextCompat.getColor(activity, R.color.color_light_title_toolbar));
        }

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        webView.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        rootView = null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void showProgress(boolean show) {
        if (Config.ENABLE_LINEAR_PROGRESS_INDICATOR) {
            if (show) {
                progressBar.setVisibility(View.VISIBLE);
            } else {
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    progressBar.setVisibility(View.INVISIBLE);
                }, 1000);
            }
        }
    }

    private void showSwipeRefresh(boolean show) {
        if (!Config.ENABLE_LINEAR_PROGRESS_INDICATOR) {
            swipeRefreshLayout.setRefreshing(show);
        }
    }

}