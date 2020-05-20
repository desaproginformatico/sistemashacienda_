package com.example.sistemashacienda;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

public class WebViewJSInterface {
    Context mContext;
    WebView webView;

    /** Instantiate the interface and set the context */
    WebViewJSInterface(Context c) {
        mContext = c;
        webView = (WebView) ((Activity) mContext).findViewById(R.id.idWebView);
    }

    /** Show a toast from the web page */
    @JavascriptInterface
    public String GetFirebaseToken(){
        return ((Webview) mContext).mToken;
    }

    @JavascriptInterface
    public String GetDeviceName(){
        return Utils.getDeviceName();
    }

}
