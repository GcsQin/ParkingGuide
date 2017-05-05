package com.parking.parkingguide;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.parking.parkingguide.customview.MaterialProgressDialog;

public class WebNewsActivity extends AppCompatActivity {
    private WebView webView;
    private MaterialProgressDialog materialProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent=getIntent();
        Bundle bundle=intent.getBundleExtra("webBundle");
        String url=bundle.getString("url");
        setContentView(R.layout.layout_webnews);
        webView= (WebView) findViewById(R.id.web_news);
        webView.loadUrl(url);
        final WebSettings webSettings=webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setBlockNetworkImage(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setSupportZoom(true);//
        //
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
//                materialProgressDialog = new MaterialProgressDialog(WebNewsActivity.this);
//                materialProgressDialog.show();
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
//                materialProgressDialog.dismiss();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                webView.loadUrl(url);
                return true;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.destroy();
        webView.setVisibility(View.GONE);
        webView=null;
    }
}
