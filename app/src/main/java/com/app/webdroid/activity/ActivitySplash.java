package com.app.webdroid.activity;

import static com.solodroid.ads.sdk.util.Constant.ADMOB;
import static com.solodroid.ads.sdk.util.Constant.APPLOVIN;
import static com.solodroid.ads.sdk.util.Constant.APPLOVIN_MAX;
import static com.solodroid.ads.sdk.util.Constant.GOOGLE_AD_MANAGER;
import static com.solodroid.ads.sdk.util.Constant.WORTISE;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.app.webdroid.BuildConfig;
import com.app.webdroid.Config;
import com.app.webdroid.R;
import com.app.webdroid.callback.CallbackConfig;
import com.app.webdroid.database.prefs.AdsPref;
import com.app.webdroid.database.prefs.SharedPref;
import com.app.webdroid.database.sqlite.DbNavigation;
import com.app.webdroid.model.Ads;
import com.app.webdroid.model.App;
import com.app.webdroid.model.Navigation;
import com.app.webdroid.rest.RestAdapter;
import com.app.webdroid.util.AdsManager;
import com.app.webdroid.util.Tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivitySplash extends AppCompatActivity {

    public static final String TAG = "SplashActivity";
    Call<CallbackConfig> callbackConfigCall = null;
    ProgressBar progressBar;
    AdsManager adsManager;
    SharedPref sharedPref;
    AdsPref adsPref;
    App app;
    Ads ads;
    List<Navigation> navigationList = new ArrayList<>();
    DbNavigation dbNavigation;
    ImageView imgSplash;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tools.getTheme(this);
        setContentView(R.layout.activity_splash);
        Tools.setNavigation(this);

        dbNavigation = new DbNavigation(this);
        adsManager = new AdsManager(this);
        adsManager.initializeAd();

        sharedPref = new SharedPref(this);
        adsPref = new AdsPref(this);

        imgSplash = findViewById(R.id.imgSplash);
        if (sharedPref.getIsDarkTheme()) {
            imgSplash.setImageResource(R.drawable.splash_dark);
        } else {
            imgSplash.setImageResource(R.drawable.splash_default);
        }

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        requestConfig();

    }

    private void requestConfig() {
        if (adsPref.getAdStatus() && adsPref.getIsAppOpenAdOnStart()) {
            if (!Config.FORCE_TO_SHOW_APP_OPEN_AD_ON_START) {
                Tools.postDelayed(() -> {
                    switch (adsPref.getMainAds()) {
                        case ADMOB:
                            if (!adsPref.getAdMobAppOpenAdId().equals("0")) {
                                ((MyApplication) getApplication()).showAdIfAvailable(ActivitySplash.this, this::validateAccessKey);
                            } else {
                                validateAccessKey();
                            }
                            break;
                        case GOOGLE_AD_MANAGER:
                            if (!adsPref.getAdManagerAppOpenAdId().equals("0")) {
                                ((MyApplication) getApplication()).showAdIfAvailable(ActivitySplash.this, this::validateAccessKey);
                            } else {
                                validateAccessKey();
                            }
                            break;
                        case APPLOVIN:
                        case APPLOVIN_MAX:
                            if (!adsPref.getAppLovinAppOpenAdUnitId().equals("0")) {
                                ((MyApplication) getApplication()).showAdIfAvailable(ActivitySplash.this, this::validateAccessKey);
                            } else {
                                validateAccessKey();
                            }
                            break;
                        case WORTISE:
                            if (!adsPref.getWortiseAppOpenAdUnitId().equals("0")) {
                                ((MyApplication) getApplication()).showAdIfAvailable(ActivitySplash.this, this::validateAccessKey);
                            } else {
                                validateAccessKey();
                            }
                            break;
                        default:
                            validateAccessKey();
                            break;
                    }
                }, Config.DELAY_SPLASH);
            } else {
                validateAccessKey();
            }
        } else {
            validateAccessKey();
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void validateAccessKey() {
        if (Config.ACCESS_KEY.contains("XXXXX")) {
            new AlertDialog.Builder(this)
                    .setTitle("App not configured")
                    .setMessage("Please put your Access Key in your admin panel to Config, you can see the documentation for more detailed instructions.")
                    .setPositiveButton(getString(R.string.dialog_option_ok), (dialogInterface, i) -> showAppOpenAdIfAvailable(false))
                    .setCancelable(false)
                    .show();
        } else {
            String data = com.solodroid.ads.sdk.util.Tools.decode(Config.ACCESS_KEY);
            String[] results = data.split("_applicationId_");
            String remoteUrl = results[0];
            String applicationId = results[1];

            if (applicationId.equals(BuildConfig.APPLICATION_ID)) {
                requestAPI(remoteUrl);
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("Whoops! invalid access key or applicationId, please check your configuration")
                        .setPositiveButton(getString(R.string.dialog_option_ok), (dialog, which) -> finish())
                        .setCancelable(false)
                        .show();
            }
            Log.d(TAG, "Start request config");
        }
    }

    private void requestAPI(String remoteUrl) {
        if (remoteUrl.startsWith("http://") || remoteUrl.startsWith("https://")) {
            if (remoteUrl.contains("https://drive.google.com")) {
                String driveUrl = remoteUrl.replace("https://", "").replace("http://", "");
                List<String> data = Arrays.asList(driveUrl.split("/"));
                String googleDriveFileId = data.get(3);
                callbackConfigCall = RestAdapter.createApi().getDriveJsonFileId(googleDriveFileId);
            } else {
                callbackConfigCall = RestAdapter.createApi().getJsonUrl(remoteUrl);
            }
        } else {
            callbackConfigCall = RestAdapter.createApi().getDriveJsonFileId(remoteUrl);
        }
        callbackConfigCall.enqueue(new Callback<CallbackConfig>() {
            public void onResponse(@NonNull Call<CallbackConfig> call, @NonNull Response<CallbackConfig> response) {
                CallbackConfig resp = response.body();
                displayApiResults(resp);
            }

            public void onFailure(@NonNull Call<CallbackConfig> call, @NonNull Throwable th) {
                Log.e(TAG, "initialize failed");
                showAppOpenAdIfAvailable(false);
            }
        });
    }

    private void displayApiResults(CallbackConfig resp) {

        if (resp != null) {
            app = resp.app;
            ads = resp.ads;
            navigationList = resp.menus;

            if (app.status) {
                adsManager.saveConfig(sharedPref, app);
                adsManager.saveAds(adsPref, ads);
                adsManager.saveAdsPlacement(adsPref, ads.placement);

                dbNavigation.truncateTableMenu(DbNavigation.TABLE_MENU);
                Tools.postDelayed(() -> {
                    dbNavigation.addListCategory(navigationList, DbNavigation.TABLE_MENU);
                    showAppOpenAdIfAvailable(adsPref.getIsAppOpen());
                }, 100);
                Log.d(TAG, "App status is live");
            } else {
                Intent intent = new Intent(getApplicationContext(), ActivityRedirect.class);
                startActivity(intent);
                finish();
                Log.d(TAG, "App status is suspended");
            }
            Log.d(TAG, "initialize success");
        } else {
            Log.d(TAG, "initialize failed");
            showAppOpenAdIfAvailable(false);
        }

    }

    private void showAppOpenAdIfAvailable(boolean showAd) {
        Tools.postDelayed(() -> {
            if (showAd) {
                if (Config.FORCE_TO_SHOW_APP_OPEN_AD_ON_START) {
                    adsManager.loadAppOpenAd(adsPref.getIsAppOpenAdOnStart(), this::startMainActivity);
                } else {
                    startMainActivity();
                }
            } else {
                startMainActivity();
            }
        }, 100);
    }

    private void startMainActivity() {
        Tools.postDelayed(() -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }, Config.DELAY_SPLASH);
    }

}
