package com.app.webdroid.listener;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;

public class WebViewOnTouchListener implements View.OnTouchListener {

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_UP:
                if (!v.hasFocus()) {
                    v.requestFocus();
                }
                break;
        }
        return false;
    }
}
