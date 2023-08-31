package com.app.webdroid.database.prefs;

import android.content.Context;
import android.content.SharedPreferences;

public class AdsPref {

    Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public AdsPref(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("ads_setting", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveAds(boolean adStatus, String adType, String backupAds, String adMobPublisherId, String adMobBannerId, String adMobInterstitialId, String adMobNativeId, String adMobAppOpenId, String adManagerBannerUnitId, String adManagerInterstitialUnitId, String adManagerNativeUnitId, String adManagerAppOpenAdUnitId, String fanBannerUnitId, String fanInterstitialUnitId, String fanNativeUnitId, String startAppId, String unityGameId, String unityBannerId, String unityInterstitialId, String appLovinBannerId, String appLovinInterstitialId, String applovinNativeAdManualUnitId, String applovinAppOpenAdUnitId, String applovinBannerZoneId, String applovinBannerMrecZoneId, String applovinInterstitialZoneId, String ironSourceAppKey, String ironSourceBannerId, String ironSourceInterstitialId, String wortiseAppId, String wortiseBannerAdUnitId, String wortiseInterstitialAdUnitId, String wortiseNativeAdUnitId, String wortiseAppOpenAdUnitId, int interstitialAdIntervalOnDrawerMenu, int interstitialAdIntervalOnWebPageLink, String nativeAdStyleDrawerMenu, String nativeAdStyleExitDialog) {
        editor.putBoolean("ad_status", adStatus);
        editor.putString("ad_type", adType);
        editor.putString("backup_ads", backupAds);
        editor.putString("admob_publisher_id", adMobPublisherId);
        editor.putString("admob_banner_unit_id", adMobBannerId);
        editor.putString("admob_interstitial_unit_id", adMobInterstitialId);
        editor.putString("admob_native_unit_id", adMobNativeId);
        editor.putString("admob_app_open_ad_unit_id", adMobAppOpenId);
        editor.putString("ad_manager_banner_unit_id", adManagerBannerUnitId);
        editor.putString("ad_manager_interstitial_unit_id", adManagerInterstitialUnitId);
        editor.putString("ad_manager_native_unit_id", adManagerNativeUnitId);
        editor.putString("ad_manager_app_open_ad_unit_id", adManagerAppOpenAdUnitId);
        editor.putString("fan_banner_unit_id", fanBannerUnitId);
        editor.putString("fan_interstitial_unit_id", fanInterstitialUnitId);
        editor.putString("fan_native_unit_id", fanNativeUnitId);
        editor.putString("startapp_app_id", startAppId);
        editor.putString("unity_game_id", unityGameId);
        editor.putString("unity_banner_placement_id", unityBannerId);
        editor.putString("unity_interstitial_placement_id", unityInterstitialId);
        editor.putString("applovin_banner_ad_unit_id", appLovinBannerId);
        editor.putString("applovin_interstitial_ad_unit_id", appLovinInterstitialId);
        editor.putString("applovin_native_ad_manual_unit_id", applovinNativeAdManualUnitId);
        editor.putString("applovin_app_open_ad_unit_id", applovinAppOpenAdUnitId);
        editor.putString("applovin_banner_zone_id", applovinBannerZoneId);
        editor.putString("applovin_banner_mrec_zone_id", applovinBannerMrecZoneId);
        editor.putString("applovin_interstitial_zone_id", applovinInterstitialZoneId);
        editor.putString("ironsource_app_key", ironSourceAppKey);
        editor.putString("ironsource_banner_id", ironSourceBannerId);
        editor.putString("ironsource_interstitial_id", ironSourceInterstitialId);
        editor.putString("wortise_app_id", wortiseAppId);
        editor.putString("wortise_banner_ad_unit_id", wortiseBannerAdUnitId);
        editor.putString("wortise_interstitial_ad_unit_id", wortiseInterstitialAdUnitId);
        editor.putString("wortise_native_ad_unit_id", wortiseNativeAdUnitId);
        editor.putString("wortise_app_open_ad_unit_id", wortiseAppOpenAdUnitId);
        editor.putInt("interstitial_ad_interval_on_drawer_menu", interstitialAdIntervalOnDrawerMenu);
        editor.putInt("interstitial_ad_interval_on_web_page_link", interstitialAdIntervalOnWebPageLink);
        editor.putString("native_ad_style_drawer_menu", nativeAdStyleDrawerMenu);
        editor.putString("native_ad_style_exit_dialog", nativeAdStyleExitDialog);
        editor.apply();
    }

    public boolean getAdStatus() {
        return sharedPreferences.getBoolean("ad_status", true);
    }

    public String getMainAds() {
        return sharedPreferences.getString("ad_type", "0");
    }

    public String getBackupAds() {
        return sharedPreferences.getString("backup_ads", "none");
    }

    public String getAdMobPublisherId() {
        return sharedPreferences.getString("admob_publisher_id", "0");
    }

    public String getAdMobBannerId() {
        return sharedPreferences.getString("admob_banner_unit_id", "0");
    }

    public String getAdMobInterstitialId() {
        return sharedPreferences.getString("admob_interstitial_unit_id", "0");
    }

    public String getAdMobNativeId() {
        return sharedPreferences.getString("admob_native_unit_id", "0");
    }

    public String getAdMobAppOpenAdId() {
        return sharedPreferences.getString("admob_app_open_ad_unit_id", "0");
    }

    public String getAdManagerBannerId() {
        return sharedPreferences.getString("ad_manager_banner_unit_id", "0");
    }

    public String getAdManagerInterstitialId() {
        return sharedPreferences.getString("ad_manager_interstitial_unit_id", "0");
    }

    public String getAdManagerNativeId() {
        return sharedPreferences.getString("ad_manager_native_unit_id", "0");
    }

    public String getAdManagerAppOpenAdId() {
        return sharedPreferences.getString("ad_manager_app_open_ad_unit_id", "0");
    }

    public String getFanBannerUnitId() {
        return sharedPreferences.getString("fan_banner_unit_id", "0");
    }

    public String getFanInterstitialUnitId() {
        return sharedPreferences.getString("fan_interstitial_unit_id", "0");
    }

    public String getFanNativeUnitId() {
        return sharedPreferences.getString("fan_native_unit_id", "0");
    }

    public String getStartappAppId() {
        return sharedPreferences.getString("startapp_app_id", "0");
    }

    public String getUnityGameId() {
        return sharedPreferences.getString("unity_game_id", "0");
    }

    public String getUnityBannerPlacementId() {
        return sharedPreferences.getString("unity_banner_placement_id", "banner");
    }

    public String getUnityInterstitialPlacementId() {
        return sharedPreferences.getString("unity_interstitial_placement_id", "video");
    }

    public String getAppLovinBannerAdUnitId() {
        return sharedPreferences.getString("applovin_banner_ad_unit_id", "0");
    }

    public String getAppLovinInterstitialAdUnitId() {
        return sharedPreferences.getString("applovin_interstitial_ad_unit_id", "0");
    }

    public String getAppLovinNativeAdManualUnitId() {
        return sharedPreferences.getString("applovin_native_ad_manual_unit_id", "0");
    }

    public String getAppLovinAppOpenAdUnitId() {
        return sharedPreferences.getString("applovin_app_open_ad_unit_id", "0");
    }

    public String getAppLovinBannerZoneId() {
        return sharedPreferences.getString("applovin_banner_zone_id", "0");
    }

    public String getAppLovinBannerMrecZoneId() {
        return sharedPreferences.getString("applovin_banner_mrec_zone_id", "0");
    }

    public String getAppLovinInterstitialZoneId() {
        return sharedPreferences.getString("applovin_interstitial_zone_id", "0");
    }

    public String getIronSourceAppKey() {
        return sharedPreferences.getString("ironsource_app_key", "0");
    }

    public String getIronSourceBannerId() {
        return sharedPreferences.getString("ironsource_banner_id", "0");
    }

    public String getIronSourceInterstitialId() {
        return sharedPreferences.getString("ironsource_interstitial_id", "0");
    }

    public String getWortiseAppId() {
        return sharedPreferences.getString("wortise_app_id", "0");
    }

    public String getWortiseBannerAdUnitId() {
        return sharedPreferences.getString("wortise_banner_ad_unit_id", "0");
    }

    public String getWortiseInterstitialAdUnitId() {
        return sharedPreferences.getString("wortise_interstitial_ad_unit_id", "0");
    }

    public String getWortiseNativeAdUnitId() {
        return sharedPreferences.getString("wortise_native_ad_unit_id", "0");
    }

    public String getWortiseAppOpenAdUnitId() {
        return sharedPreferences.getString("wortise_app_open_ad_unit_id", "0");
    }

    public int getInterstitialAdIntervalOnDrawerMenu() {
        return sharedPreferences.getInt("interstitial_ad_interval_on_drawer_menu", 3);
    }

    public int getInterstitialAdIntervalOnWebPageLink() {
        return sharedPreferences.getInt("interstitial_ad_interval_on_web_page_link", 5);
    }

    public String getNativeAdStyleDrawerMenu() {
        return sharedPreferences.getString("native_ad_style_drawer_menu", "medium");
    }

    public String getNativeAdStyleExitDialog() {
        return sharedPreferences.getString("native_ad_style_exit_dialog", "medium");
    }

    public void setAdPlacements(boolean bannerHome, boolean interstitialDrawerMenu, boolean interstitialWebPageLink,  boolean nativeDrawerMenu, boolean nativeExitDialog, boolean appOpenAdOnStart, boolean appOpenAdOnResume) {
        editor.putBoolean("banner_home", bannerHome);
        editor.putBoolean("interstitial_drawer_menu", interstitialDrawerMenu);
        editor.putBoolean("interstitial_web_page_link", interstitialWebPageLink);
        editor.putBoolean("native_drawer_menu", nativeDrawerMenu);
        editor.putBoolean("native_exit_dialog", nativeExitDialog);
        editor.putBoolean("app_open_ad_on_start", appOpenAdOnStart);
        editor.putBoolean("app_open_ad_on_resume", appOpenAdOnResume);
        editor.apply();
    }

    public boolean getIsBannerHome() {
        return sharedPreferences.getBoolean("banner_home", true);
    }

    public boolean getIsInterstitialDrawerMenu() {
        return sharedPreferences.getBoolean("interstitial_drawer_menu", true);
    }

    public boolean getIsInterstitialWebPageLink() {
        return sharedPreferences.getBoolean("interstitial_web_page_link", true);
    }

    public boolean getIsNativeDrawerMenu() {
        return sharedPreferences.getBoolean("native_drawer_menu", true);
    }

    public boolean getIsNativeExitDialog() {
        return sharedPreferences.getBoolean("native_exit_dialog", true);
    }

    public boolean getIsAppOpenAdOnStart() {
        return sharedPreferences.getBoolean("app_open_ad_on_start", true);
    }

    public boolean getIsAppOpenAdOnResume() {
        return sharedPreferences.getBoolean("app_open_ad_on_resume", true);
    }

    public void setIsAppOpen(boolean isAppOpen) {
        editor.putBoolean("open_ads", isAppOpen);
        editor.apply();
    }

    public boolean getIsAppOpen() {
        return sharedPreferences.getBoolean("open_ads", false);
    }

}