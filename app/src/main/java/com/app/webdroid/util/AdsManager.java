package com.app.webdroid.util;

import static com.solodroid.ads.sdk.util.Constant.IRONSOURCE;

import android.app.Activity;

import com.app.webdroid.BuildConfig;
import com.app.webdroid.Config;
import com.app.webdroid.R;
import com.app.webdroid.database.prefs.AdsPref;
import com.app.webdroid.database.prefs.SharedPref;
import com.app.webdroid.model.Ads;
import com.app.webdroid.model.App;
import com.app.webdroid.model.Placement;
import com.solodroid.ads.sdk.format.AdNetwork;
import com.solodroid.ads.sdk.format.AppOpenAd;
import com.solodroid.ads.sdk.format.BannerAd;
import com.solodroid.ads.sdk.format.InterstitialAd;
import com.solodroid.ads.sdk.format.NativeAd;
import com.solodroid.ads.sdk.gdpr.GDPR;
import com.solodroid.ads.sdk.gdpr.LegacyGDPR;
import com.solodroid.ads.sdk.util.OnShowAdCompleteListener;

public class AdsManager {

    Activity activity;
    AdNetwork.Initialize adNetwork;
    AppOpenAd.Builder appOpenAd;
    BannerAd.Builder bannerAd;
    InterstitialAd.Builder interstitialAd;
    InterstitialAd.Builder interstitialAd2;
    NativeAd.Builder nativeAd;
    SharedPref sharedPref;
    AdsPref adsPref;
    LegacyGDPR legacyGDPR;
    GDPR gdpr;

    public AdsManager(Activity activity) {
        this.activity = activity;
        this.sharedPref = new SharedPref(activity);
        this.adsPref = new AdsPref(activity);
        this.legacyGDPR = new LegacyGDPR(activity);
        this.gdpr = new GDPR(activity);
        adNetwork = new AdNetwork.Initialize(activity);
        appOpenAd = new AppOpenAd.Builder(activity);
        bannerAd = new BannerAd.Builder(activity);
        interstitialAd = new InterstitialAd.Builder(activity);
        interstitialAd2 = new InterstitialAd.Builder(activity);
        nativeAd = new NativeAd.Builder(activity);
    }

    public void initializeAd() {
        if (adsPref.getAdStatus()) {
            adNetwork.setAdStatus("1")
                    .setAdNetwork(adsPref.getMainAds())
                    .setBackupAdNetwork(adsPref.getBackupAds())
                    .setStartappAppId(adsPref.getStartappAppId())
                    .setUnityGameId(adsPref.getUnityGameId())
                    .setIronSourceAppKey(adsPref.getIronSourceAppKey())
                    .setWortiseAppId(adsPref.getWortiseAppId())
                    .setDebug(BuildConfig.DEBUG)
                    .build();
        }
    }

    public void loadAppOpenAd(boolean placement, OnShowAdCompleteListener onShowAdCompleteListener) {
        if (placement) {
            if (adsPref.getAdStatus()) {
                appOpenAd = new AppOpenAd.Builder(activity)
                        .setAdStatus("1")
                        .setAdNetwork(adsPref.getMainAds())
                        .setBackupAdNetwork(adsPref.getBackupAds())
                        .setAdMobAppOpenId(adsPref.getAdMobAppOpenAdId())
                        .setAdManagerAppOpenId(adsPref.getAdManagerAppOpenAdId())
                        .setApplovinAppOpenId(adsPref.getAppLovinAppOpenAdUnitId())
                        .setWortiseAppOpenId(adsPref.getWortiseAppOpenAdUnitId())
                        .build(onShowAdCompleteListener);
            } else {
                onShowAdCompleteListener.onShowAdComplete();
            }
        } else {
            onShowAdCompleteListener.onShowAdComplete();
        }
    }

    public void loadAppOpenAd(boolean placement) {
        if (placement) {
            if (adsPref.getAdStatus()) {
                appOpenAd = new AppOpenAd.Builder(activity)
                        .setAdStatus("1")
                        .setAdNetwork(adsPref.getMainAds())
                        .setBackupAdNetwork(adsPref.getBackupAds())
                        .setAdMobAppOpenId(adsPref.getAdMobAppOpenAdId())
                        .setAdManagerAppOpenId(adsPref.getAdManagerAppOpenAdId())
                        .setApplovinAppOpenId(adsPref.getAppLovinAppOpenAdUnitId())
                        .setWortiseAppOpenId(adsPref.getWortiseAppOpenAdUnitId())
                        .build();
            }
        }
    }

    public void showAppOpenAd(boolean placement) {
        if (placement) {
            if (adsPref.getAdStatus()) {
                appOpenAd.show();
            }
        }
    }

    public void destroyAppOpenAd(boolean placement) {
        if (placement) {
            if (adsPref.getAdStatus()) {
                appOpenAd.destroyOpenAd();
            }
        }
    }

    public void loadBannerAd(boolean placement) {
        if (placement) {
            if (adsPref.getAdStatus()) {
                bannerAd.setAdStatus("1")
                        .setAdNetwork(adsPref.getMainAds())
                        .setBackupAdNetwork(adsPref.getBackupAds())
                        .setAdMobBannerId(adsPref.getAdMobBannerId())
                        .setGoogleAdManagerBannerId(adsPref.getAdManagerBannerId())
                        .setFanBannerId(adsPref.getFanBannerUnitId())
                        .setUnityBannerId(adsPref.getUnityBannerPlacementId())
                        .setAppLovinBannerId(adsPref.getAppLovinBannerAdUnitId())
                        .setAppLovinBannerZoneId(adsPref.getAppLovinBannerZoneId())
                        .setIronSourceBannerId(adsPref.getIronSourceBannerId())
                        .setWortiseBannerId(adsPref.getWortiseBannerAdUnitId())
                        .setDarkTheme(sharedPref.getIsDarkTheme())
                        .setPlacementStatus(1)
                        .build();
            }
        }
    }

    public void loadInterstitialAd(boolean placement, int interval) {
        if (placement) {
            if (adsPref.getAdStatus()) {
                interstitialAd.setAdStatus("1")
                        .setAdNetwork(adsPref.getMainAds())
                        .setBackupAdNetwork(adsPref.getBackupAds())
                        .setAdMobInterstitialId(adsPref.getAdMobInterstitialId())
                        .setGoogleAdManagerInterstitialId(adsPref.getAdManagerInterstitialId())
                        .setFanInterstitialId(adsPref.getFanInterstitialUnitId())
                        .setUnityInterstitialId(adsPref.getUnityInterstitialPlacementId())
                        .setAppLovinInterstitialId(adsPref.getAppLovinInterstitialAdUnitId())
                        .setAppLovinInterstitialZoneId(adsPref.getAppLovinInterstitialZoneId())
                        .setIronSourceInterstitialId(adsPref.getIronSourceInterstitialId())
                        .setWortiseInterstitialId(adsPref.getWortiseInterstitialAdUnitId())
                        .setInterval(interval)
                        .setPlacementStatus(1)
                        .build();
            }
        }
    }

    public void loadInterstitialAd2(boolean placement, int interval) {
        if (placement) {
            if (adsPref.getAdStatus()) {
                interstitialAd2.setAdStatus("1")
                        .setAdNetwork(adsPref.getMainAds())
                        .setBackupAdNetwork(adsPref.getBackupAds())
                        .setAdMobInterstitialId(adsPref.getAdMobInterstitialId())
                        .setGoogleAdManagerInterstitialId(adsPref.getAdManagerInterstitialId())
                        .setFanInterstitialId(adsPref.getFanInterstitialUnitId())
                        .setUnityInterstitialId(adsPref.getUnityInterstitialPlacementId())
                        .setAppLovinInterstitialId(adsPref.getAppLovinInterstitialAdUnitId())
                        .setAppLovinInterstitialZoneId(adsPref.getAppLovinInterstitialZoneId())
                        .setIronSourceInterstitialId(adsPref.getIronSourceInterstitialId())
                        .setWortiseInterstitialId(adsPref.getWortiseInterstitialAdUnitId())
                        .setInterval(interval)
                        .setPlacementStatus(1)
                        .build();
            }
        }
    }

    public void loadNativeAd(boolean placement, String nativeAdStyle) {
        if (placement) {
            if (adsPref.getAdStatus()) {
                nativeAd.setAdStatus("1")
                        .setAdNetwork(adsPref.getMainAds())
                        .setBackupAdNetwork(adsPref.getBackupAds())
                        .setAdMobNativeId(adsPref.getAdMobNativeId())
                        .setAdManagerNativeId(adsPref.getAdManagerNativeId())
                        .setFanNativeId(adsPref.getFanNativeUnitId())
                        .setAppLovinNativeId(adsPref.getAppLovinNativeAdManualUnitId())
                        .setAppLovinDiscoveryMrecZoneId(adsPref.getAppLovinBannerMrecZoneId())
                        .setWortiseNativeId(adsPref.getWortiseNativeAdUnitId())
                        .setPlacementStatus(1)
                        .setDarkTheme(sharedPref.getIsDarkTheme())
                        .setNativeAdBackgroundColor(R.color.color_light_native_ad_background, R.color.color_dark_native_ad_background)
                        .setNativeAdStyle(nativeAdStyle)
                        .build();
            }
        }
    }

    public void showInterstitialAd() {
        interstitialAd.show();
    }

    public void showInterstitialAd2() {
        interstitialAd2.show();
    }

    public void destroyBannerAd() {
        bannerAd.destroyAndDetachBanner();
    }

    public void resumeBannerAd(boolean placement) {
        if (adsPref.getAdStatus() && !adsPref.getIronSourceBannerId().equals("0")) {
            if (adsPref.getMainAds().equals(IRONSOURCE) || adsPref.getBackupAds().equals(IRONSOURCE)) {
                loadBannerAd(placement);
            }
        }
    }

    public void updateConsentStatus() {
        if (Config.ENABLE_GDPR_UMP_SDK) {
            gdpr.updateGDPRConsentStatus();
        }
    }

    public void saveAds(AdsPref adsPref, Ads ads) {
        adsPref.saveAds(
                ads.ad_status,
                ads.main_ads,
                ads.backup_ads,
                ads.admob_publisher_id,
                ads.admob_banner_unit_id,
                ads.admob_interstitial_unit_id,
                ads.admob_native_unit_id,
                ads.admob_app_open_ad_unit_id,
                ads.ad_manager_banner_unit_id,
                ads.ad_manager_interstitial_unit_id,
                ads.ad_manager_native_unit_id,
                ads.ad_manager_app_open_ad_unit_id,
                ads.fan_banner_unit_id,
                ads.fan_interstitial_unit_id,
                ads.fan_native_unit_id,
                ads.startapp_app_id,
                ads.unity_game_id,
                ads.unity_banner_placement_id,
                ads.unity_interstitial_placement_id,
                ads.applovin_banner_ad_unit_id,
                ads.applovin_interstitial_ad_unit_id,
                ads.applovin_native_ad_manual_unit_id,
                ads.applovin_app_open_ad_unit_id,
                ads.applovin_banner_zone_id,
                ads.applovin_banner_mrec_zone_id,
                ads.applovin_interstitial_zone_id,
                ads.ironsource_app_key,
                ads.ironsource_banner_placement_name,
                ads.ironsource_interstitial_placement_name,
                ads.wortise_app_id,
                ads.wortise_banner_ad_unit_id,
                ads.wortise_interstitial_ad_unit_id,
                ads.wortise_native_ad_unit_id,
                ads.wortise_app_open_ad_unit_id,
                ads.interstitial_ad_interval_on_drawer_menu,
                ads.interstitial_ad_interval_on_web_page_link,
                ads.native_ad_style_drawer_menu,
                ads.native_ad_style_exit_dialog
        );
    }

    public void saveAdsPlacement(AdsPref adsPref, Placement placement) {
        adsPref.setAdPlacements(
                placement.banner_home,
                placement.interstitial_drawer_menu,
                placement.interstitial_web_page_link,
                placement.native_drawer_menu,
                placement.native_exit_dialog,
                placement.app_open_ad_on_start,
                placement.app_open_ad_on_resume
        );
    }

    public void saveConfig(SharedPref sharedPref, App app) {
        sharedPref.saveConfig(
                app.toolbar,
                app.navigation_drawer,
                app.geolocation,
                app.cache,
                app.open_link_in_external_browser,
                app.zoom_controls,
                app.user_agent,
                app.privacy_policy_url,
                app.more_apps_url,
                app.redirect_url
        );
    }

}
