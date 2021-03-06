package com.example.test_h5;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.test_h5.databinding.ActivityMainBinding;
import com.mihe.MiHeAuthAndroid.AuthorizeManger;
import com.mihe.MiHeAuthAndroid.AuthHandler;
import com.mihe.MiHeAuthAndroid.MiheUserModel;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {


    private ActivityMainBinding binding;
    String referer = "https://mihepay.ruwii.com/";
    boolean[] firstVisitWXH5PayUrl = {true};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        WebView webView = findViewById(R.id.web);
        WebSettings webSettings = webView.getSettings();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setSavePassword(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSupportZoom(false);
        webSettings.setDatabaseEnabled(true);
        webSettings.setDomStorageEnabled(true);

        webSettings.setAllowFileAccess(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setDefaultTextEncodingName("utf-8");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("weixin://")) {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                        return true;
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "???????????????????????????", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                } else if (url.startsWith("alipays://") || url.startsWith("alipay")) {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                        return true;
                    } catch (Exception e) {
                        return true;
                    }
                }
                if (!(url.startsWith("http") || url.startsWith("https"))) {
                    return true;
                }
                if (url.contains("wx.tenpay.com")) {
                    // ?????? Android 4.4.3 ??? 4.4.4 ???????????????????????? referer ???????????????
                    if (("4.4.3".equals(android.os.Build.VERSION.RELEASE))
                            || ("4.4.4".equals(android.os.Build.VERSION.RELEASE))) {
                        if (firstVisitWXH5PayUrl[0]) {
                            view.loadDataWithBaseURL(referer, "<script>window.location.href=\"" + url + "\";</script>",
                                    "text/html", "utf-8", null);
                            // ??????????????????????????????????????????
                            // ??????????????????H5????????????????????????????????? firstVisitWXH5PayUrl = true
                            firstVisitWXH5PayUrl[0] = false;
                        }
                        // ?????? false ????????? WebView ??????????????? url
                        return false;
                    } else {
                        HashMap<String, String> map = new HashMap<>(1);
                        map.put("Referer", referer);
                        view.loadUrl(url, map);
                        return true;
                    }
                }
                return false;
            }
        });
        AuthorizeManger manager = new AuthorizeManger();
        manager.initWithHandler(this, webView, handler);

        webView.loadUrl("https://t57.ruwii.com/mihe/#/");
    }

    AuthHandler handler = passedData -> {//passedData ???????????????json??????
        //get the specified user with data passed from h5 page
        MiheUserModel user = getUser();
        return user;
    };

    MiheUserModel getUser() {
        MiheUserModel user = new MiheUserModel();
        user.uid = "8098908908908";//??????id
        user.cert="cert666";//????????????????????????token sessionid ?????????
        user.img="img333";
        user.mobile = "17898888888";
        user.nickName = "nickname666";
        return user;
    }

}
