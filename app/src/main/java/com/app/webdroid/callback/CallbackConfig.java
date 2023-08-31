package com.app.webdroid.callback;

import com.app.webdroid.model.Ads;
import com.app.webdroid.model.App;
import com.app.webdroid.model.Navigation;
import com.app.webdroid.model.Notification;

import java.util.ArrayList;
import java.util.List;

public class CallbackConfig {

    public App app = null;
    public List<Navigation> menus = new ArrayList<>();
    public Notification notification = null;
    public Ads ads = null;

}