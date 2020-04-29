package com.example.sistemashacienda;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

public class WebViewJSInterface {
    Context mContext;
    WebView webView;

    /** Instantiate the interface and set the context */
    WebViewJSInterface(Context c) {
        mContext = c;
        webView = (WebView) ((Activity) mContext).findViewById(R.id.webView);
    }

    /** Show a toast from the web page */
    @JavascriptInterface
    public void showToast(String toast) {
        webView.setVisibility(View.INVISIBLE);
        webView.getSettings().setJavaScriptEnabled(true);
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
    }
}