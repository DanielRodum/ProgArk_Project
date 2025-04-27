package com.mygdx.game.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RelativeLayout;

public class TutorialActivity extends Activity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RelativeLayout layout = new RelativeLayout(this);

        // WebView setup
        webView = new WebView(this);
        webView.setWebViewClient(new WebViewClient());
        WebSettings ws = webView.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setMediaPlaybackRequiresUserGesture(false);
        try {
            webView.loadUrl("https://youtu.be/Fj2hThCv_dI");
        } catch (Exception e) {
            Log.e("TutorialActivity", "WebView load failed", e);
        }

        // Back button
        Button back = new Button(this);
        back.setText("Back to Main Menu");
        back.setOnClickListener(v -> {
            setResult(Activity.RESULT_OK);
            finish();
        });

        // Layout params
        RelativeLayout.LayoutParams wParams =
            new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
            );
        layout.addView(webView, wParams);

        RelativeLayout.LayoutParams bParams =
            new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            );
        bParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        bParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        bParams.setMargins(0,0,0,50);
        layout.addView(back, bParams);

        setContentView(layout);
    }

    @Override
    protected void onDestroy() {
        if (webView != null) webView.destroy();
        super.onDestroy();
    }
}
