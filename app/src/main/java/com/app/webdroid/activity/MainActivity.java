package com.app.webdroid.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.webdroid.BuildConfig;
import com.app.webdroid.Config;
import com.app.webdroid.R;
import com.app.webdroid.adapter.AdapterNavigation;
import com.app.webdroid.database.prefs.AdsPref;
import com.app.webdroid.database.prefs.SharedPref;
import com.app.webdroid.database.sqlite.DbNavigation;
import com.app.webdroid.fragment.FragmentWebView;
import com.app.webdroid.listener.DrawerStateListener;
import com.app.webdroid.listener.LoadUrlListener;
import com.app.webdroid.model.Navigation;
import com.app.webdroid.util.AdsManager;
import com.app.webdroid.util.Tools;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;
import com.solodroid.ads.sdk.format.AppOpenAd;
import com.solodroid.push.sdk.provider.OneSignalPush;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements LoadUrlListener, DrawerStateListener, DefaultLifecycleObserver {

    private long exitTime = 0;
    private final static String COLLAPSING_TOOLBAR_FRAGMENT_TAG = "collapsing_toolbar";
    private final static String SELECTED_TAG = "selected_index";
    private static int selectedIndex;
    private final static int COLLAPSING_TOOLBAR = 0;
    private DrawerLayout drawerLayout;
    private FragmentManager fragmentManager;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    RecyclerView recyclerView;
    DbNavigation dbNavigation;
    List<Navigation> items;
    AdsManager adsManager;
    SharedPref sharedPref;
    AdsPref adsPref;
    CoordinatorLayout parentView;
    public static final int IMMEDIATE_APP_UPDATE_REQ_CODE = 124;
    private AppUpdateManager appUpdateManager;
    View lytDialogExit;
    LinearLayout lytPanelView;
    LinearLayout lytPanelDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tools.getTheme(this);
        setContentView(R.layout.activity_main);
        Tools.setNavigation(this);

        fragmentManager = getSupportFragmentManager();

        sharedPref = new SharedPref(this);

        adsPref = new AdsPref(this);
        adsManager = new AdsManager(this);
        adsManager.initializeAd();
        adsManager.updateConsentStatus();
        adsManager.loadBannerAd(adsPref.getIsBannerHome());
        adsManager.loadInterstitialAd(adsPref.getIsInterstitialWebPageLink(), adsPref.getInterstitialAdIntervalOnWebPageLink());
        adsManager.loadInterstitialAd2(adsPref.getIsInterstitialDrawerMenu(), adsPref.getInterstitialAdIntervalOnDrawerMenu());
        adsManager.loadAppOpenAd(adsPref.getIsAppOpenAdOnResume());
        adsPref.setIsAppOpen(true);
        initExitDialog();

        parentView = findViewById(R.id.parent_view);
        navigationView = findViewById(R.id.navigationView);

        if (sharedPref.getIsDarkTheme()) {
            navigationView.setBackgroundColor(getResources().getColor(R.color.color_dark_navigation_drawer));
        } else {
            navigationView.setBackgroundColor(getResources().getColor(R.color.color_light_navigation_drawer));
        }

        drawerLayout = findViewById(R.id.drawer_layout);
        if (!sharedPref.getNavigationDrawer()) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }

        selectedIndex = COLLAPSING_TOOLBAR;
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new FragmentWebView(), COLLAPSING_TOOLBAR_FRAGMENT_TAG).commit();

        dbNavigation = new DbNavigation(this);
        items = dbNavigation.getAllMenu(DbNavigation.TABLE_MENU);

        appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());
        if (!BuildConfig.DEBUG) {
            inAppUpdate();
            inAppReview();
        }

        loadWebPage();
        Tools.postDelayed(this::notificationOpenHandler, 500);

        new OneSignalPush.Builder(this).requestNotificationPermission();

    }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onStart(owner);
        Tools.postDelayed(() -> {
            if (AppOpenAd.isAppOpenAdLoaded) {
                adsManager.showAppOpenAd(adsPref.getIsAppOpenAdOnResume());
            }
        }, 100);
    }

    private void notificationOpenHandler() {
        String title = getIntent().getStringExtra(OneSignalPush.EXTRA_TITLE);
        String link = getIntent().getStringExtra(OneSignalPush.EXTRA_LINK);
        if (getIntent().hasExtra("id") || getIntent().hasExtra("unique_id")) {
            if (link != null && !link.equals("")) {
                if (!link.equals("0")) {
                    if (link.contains("play.google.com") || link.contains("?target=external")) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
                    } else {
                        Intent intent = new Intent(getApplicationContext(), ActivityWebView.class);
                        intent.putExtra("title", title);
                        intent.putExtra("link", link);
                        startActivity(intent);
                    }
                }
            }
        }
    }

    private void loadWebPage() {
        loadWebPage(items.get(0).name, items.get(0).type, items.get(0).url, items.get(0).url_dark);
        loadNavigationMenu();
        sharedPref.setLastItemPosition(0);
    }

    private void loadNavigationMenu() {

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        AdapterNavigation adapterNavigation = new AdapterNavigation(this, new ArrayList<>());
        adapterNavigation.setListData(items);

        recyclerView.setAdapter(adapterNavigation);

        recyclerView.postDelayed(() -> {
            if (adsPref.getIsNativeDrawerMenu()) {
                Objects.requireNonNull(recyclerView.findViewHolderForAdapterPosition(1)).itemView.performClick();
            } else {
                Objects.requireNonNull(recyclerView.findViewHolderForAdapterPosition(0)).itemView.performClick();
            }
            AdapterNavigation.isFirstItemClicked = true;
        }, 10);

        adapterNavigation.setOnItemClickListener((v, obj, position) -> drawerLayout.closeDrawer(GravityCompat.START));

    }

    public void loadWebPage(String name, String type, String url, String urlDark) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            FragmentWebView argumentFragment = new FragmentWebView();
            Bundle data = new Bundle();
            data.putString("name", name);
            data.putString("type", type);
            if (sharedPref.getIsDarkTheme()) {
                if (urlDark != null && !urlDark.equals("")) {
                    data.putString("url", urlDark);
                } else {
                    data.putString("url", url);
                }
            } else {
                data.putString("url", url);
            }
            argumentFragment.setArguments(data);
            fragmentManager.beginTransaction().replace(R.id.fragment_container, argumentFragment).commit();
        }, 250);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_TAG, selectedIndex);
    }

    @Override
    public void onLoadUrl(String url) {
        showInterstitialAd();
    }

    public void showInterstitialAd() {
        adsManager.showInterstitialAd();
    }

    public void showInterstitialAd2() {
        adsManager.showInterstitialAd2();
    }

    public void setupNavigationDrawer(Toolbar toolbar) {
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            exitApp();
        }
    }

    @Override
    public boolean isDrawerOpen() {
        return drawerLayout.isDrawerOpen(GravityCompat.START);
    }

    @Override
    public void onBackButtonPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            exitApp();
        }
    }

    public void exitApp() {
        if (Config.SHOW_EXIT_DIALOG) {
            if (lytDialogExit.getVisibility() != View.VISIBLE) {
                showDialog(true);
            }
        } else {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                showSnackBar(getString(R.string.exit_msg));
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                destroyBannerAd();
                destroyAppOpenAd();
            }
        }
    }

    public void initExitDialog() {

        lytDialogExit = findViewById(R.id.lyt_dialog_exit);
        lytPanelView = findViewById(R.id.lyt_panel_view);
        lytPanelDialog = findViewById(R.id.lyt_panel_dialog);

        if (sharedPref.getIsDarkTheme()) {
            lytPanelView.setBackgroundColor(getResources().getColor(R.color.color_dialog_background_dark_overlay));
            lytPanelDialog.setBackgroundResource(R.drawable.bg_rounded_dark);
        } else {
            lytPanelView.setBackgroundColor(getResources().getColor(R.color.color_dialog_background_light));
            lytPanelDialog.setBackgroundResource(R.drawable.bg_rounded_default);
        }

        lytPanelView.setOnClickListener(view -> {
            //empty state
        });

        Tools.setNativeAdStyle(this, findViewById(R.id.native_ad_view_exit_dialog), adsPref.getNativeAdStyleExitDialog());
        adsManager.loadNativeAd(adsPref.getIsNativeExitDialog(), adsPref.getNativeAdStyleExitDialog());

        Button btnCancel = findViewById(R.id.btn_cancel);
        Button btnExit = findViewById(R.id.btn_exit);

        FloatingActionButton btnRate = findViewById(R.id.btn_rate);
        FloatingActionButton btnShare = findViewById(R.id.btn_share);

        btnCancel.setOnClickListener(view -> showDialog(false));

        btnExit.setOnClickListener(view -> {
            showDialog(false);
            Tools.postDelayed(() -> {
                finish();
                destroyBannerAd();
                destroyAppOpenAd();
            }, 300);
        });

        btnRate.setOnClickListener(v -> {
            Tools.rateApp(this);
            showDialog(false);
        });

        btnShare.setOnClickListener(v -> {
            Tools.shareApp(this, getString(R.string.share_text));
            showDialog(false);
        });
    }

    private void showDialog(boolean show) {
        if (show) {
            lytDialogExit.setVisibility(View.VISIBLE);
            Tools.slideUp(this, lytPanelDialog);
            Tools.dialogStatusBarNavigationColor(this, sharedPref.getIsDarkTheme());
        } else {
            Tools.slideDown(this, lytPanelDialog);
            Tools.postDelayed(() -> {
                lytDialogExit.setVisibility(View.GONE);
                Tools.setNavigation(this);
            }, 300);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyBannerAd();
        destroyAppOpenAd();
    }

    @Override
    public void onResume() {
        super.onResume();
        adsManager.resumeBannerAd(adsPref.getIsBannerHome());
    }

    public void destroyBannerAd() {
        adsManager.destroyBannerAd();
    }

    public void destroyAppOpenAd() {
        if (Config.FORCE_TO_SHOW_APP_OPEN_AD_ON_START) {
            adsManager.destroyAppOpenAd(adsPref.getIsAppOpenAdOnResume());
            ProcessLifecycleOwner.get().getLifecycle().removeObserver(this);
        }
        Tools.isAppOpen = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public AssetManager getAssets() {
        return getResources().getAssets();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                startActivity(new Intent(getApplicationContext(), ActivitySettings.class));
                return true;

            case R.id.menu_share:
                Tools.shareApp(this, getString(R.string.share_text));
                return true;

            case R.id.menu_rate:
                Tools.rateApp(this);
                return true;

            case R.id.menu_more:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(sharedPref.getMoreAppsUrl())));
                return true;

            case R.id.menu_privacy:
                Intent intent = new Intent(getApplicationContext(), ActivityWebView.class);
                intent.putExtra("title", getString(R.string.menu_privacy));
                intent.putExtra("link", sharedPref.getPrivacyPolicyUrl());
                startActivity(intent);
                return true;

            case R.id.menu_about:
                Tools.showAboutDialog(this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void inAppReview() {
        if (sharedPref.getInAppReviewToken() <= 3) {
            sharedPref.updateInAppReviewToken(sharedPref.getInAppReviewToken() + 1);
        } else {
            ReviewManager manager = ReviewManagerFactory.create(this);
            Task<ReviewInfo> request = manager.requestReviewFlow();
            request.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    ReviewInfo reviewInfo = task.getResult();
                    manager.launchReviewFlow(MainActivity.this, reviewInfo).addOnFailureListener(e -> {
                    }).addOnCompleteListener(complete -> {
                            }
                    ).addOnFailureListener(failure -> {
                    });
                }
            }).addOnFailureListener(failure -> Log.d("In-App Review", "In-App Request Failed " + failure));
        }
    }

    private void inAppUpdate() {
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                startUpdateFlow(appUpdateInfo);
            } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                startUpdateFlow(appUpdateInfo);
            }
        });
    }

    private void startUpdateFlow(AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, this, IMMEDIATE_APP_UPDATE_REQ_CODE);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, intent);
        }
        if (requestCode == IMMEDIATE_APP_UPDATE_REQ_CODE) {
            if (resultCode == RESULT_CANCELED) {
                showSnackBar(getString(R.string.msg_cancel_update));
            } else if (resultCode == RESULT_OK) {
                showSnackBar(getString(R.string.msg_success_update));
            } else {
                showSnackBar(getString(R.string.msg_failed_update));
                inAppUpdate();
            }
        }
    }

    public void showSnackBar(String message) {
        Snackbar.make(parentView, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration configuration) {
        super.onConfigurationChanged(configuration);
    }

    public void urlChecker() {
        Tools.urlChecker(this, fragmentManager);
    }

}
