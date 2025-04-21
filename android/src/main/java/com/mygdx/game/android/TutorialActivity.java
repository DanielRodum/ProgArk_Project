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
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        RelativeLayout layout = new RelativeLayout(this);

        webView = new WebView(this);
        webView.setWebViewClient(new WebViewClient());

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);

        try {
            webView.loadUrl("https://www.youtube.com/watch?v=dQw4w9WgXcQ");
        }catch (Exception e){
            Log.e("TutorialActivity", "WebView failed to load video", e);
        }


        Button backButton = new Button(this);
        backButton.setText("Back to Main Menu");
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        RelativeLayout.LayoutParams webViewParams = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT
        );
        layout.addView(webView, webViewParams);

        RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        buttonParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        buttonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        buttonParams.setMargins(0,0,0,50);
        layout.addView(backButton, buttonParams);

        setContentView(layout);
    }

    @Override
    protected void onDestroy(){
        if (webView != null){
            webView.destroy();
        }
        super.onDestroy();
    }
}
