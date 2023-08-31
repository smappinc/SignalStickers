package com.app.webdroid;

public class Config {

    //generate your access key using the link below or check the documentation for more detailed instructions
    //https://services.solodroid.co.id/access-key/generate
    public static final String ACCESS_KEY = "WVVoU01HTklUVFpNZVRreVkwYzFlbHBYVGpGamJWWjNZMjA0ZFZreU9YUk1NazUyWW0xYWNGcDVOWEZqTWpsMVdESkdkMk5IZUhCWk1rWXdZVmM1ZFZOWFVtWlpNamwwVEc1T2NGb3lOV2hpU0U0d1lWZE9jbHBZU25vPQ==";

    //RTL Direction for Arabic Language
    public static final boolean ENABLE_RTL_MODE =  false;

    public static final boolean ENABLE_LINEAR_PROGRESS_INDICATOR = true;

    //GDPR EU Consent
    public static final boolean ENABLE_GDPR_UMP_SDK = true;

    //Show exit dialog when user want to close the app
    public static final boolean SHOW_EXIT_DIALOG = true;

    //Enable it with true value if want to the app will force to display open ads first before start the main menu
    //Longer duration to start the app may occur depending on internet connection or open ad response time itself
    public static final boolean FORCE_TO_SHOW_APP_OPEN_AD_ON_START = false;

    //delay splash when remote config finish loading
    public static final int DELAY_SPLASH = 100;

}