package edu.mobile.ravelryknit;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.net.Uri;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;


public class Login extends Activity {
    private static final String TAG = "Login";
    private OAuthConsumer mConsumer = new CommonsHttpOAuthConsumer("B53DF0B7F0AAB1AC65C4", "pYx+Ks/8up8wVVWgov2AsR7HSym89hWbNLclIzrJ");
    private OAuthProvider mProvider = new CommonsHttpOAuthProvider(
            "https://www.ravelry.com/oauth/request_token",
            "https://www.ravelry.com/oauth/access_token",
            "https://www.ravelry.com/oauth/authorize");
    Button btnLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.v(TAG, "onCreate");
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        btnLogin = (Button) findViewById(R.id.submit);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.submit:
                        mProvider.setOAuth10a(true);
                        String authUrl = "";
                        try {
                            authUrl = mProvider.retrieveRequestToken(mConsumer, "http://localhost/oauth_callback");
                        } catch (OAuthMessageSignerException | OAuthNotAuthorizedException | OAuthExpectationFailedException | OAuthCommunicationException ex) {
                            Log.e(TAG, "OAuth Retrieve Request Token Exception", ex);
                        }
                        WebView webview = new WebView(Login.this);
                        setContentView(webview);
                        webview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                        webview.setWebViewClient(new WebViewClient() {

                            boolean authComplete = false;

                            @Override
                            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                Uri callback = Uri.parse(url);
                                if (callback.toString().startsWith("http://localhost/oauth_callback") && !authComplete) {
                                    authComplete = true;
                                    String oauthVerifier = callback.getQueryParameter("oauth_verifier");
                                    try {
                                        mProvider.retrieveAccessToken(mConsumer, oauthVerifier);
                                    } catch (OAuthMessageSignerException | OAuthNotAuthorizedException | OAuthExpectationFailedException | OAuthCommunicationException ex) {
                                        Log.e(TAG, "OAuth Get Access Token Exception", ex);
                                    }
                                    OAuthConsumer consumer = new CommonsHttpOAuthConsumer(mConsumer.getConsumerKey(), mConsumer.getConsumerSecret());
                                    consumer.setTokenWithSecret(mConsumer.getToken(), mConsumer.getTokenSecret());
                                    Intent launchMain = new Intent(Login.this, Main.class);
                                    launchMain.putExtra("Consumer", consumer);
                                    startActivity(launchMain);
                                } else if (url.contains("401")) {
                                    Log.v(TAG, "Access Denied by Ravelry OAuth");
                                    authComplete = false;
                                    setContentView(R.layout.activity_login);
                                }
                                return super.shouldOverrideUrlLoading(view, url);
                            }
                        });
                        webview.loadUrl(authUrl);
                        break;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_login);
        btnLogin = (Button) findViewById(R.id.submit);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.submit:
                        mProvider.setOAuth10a(true);
                        String authUrl = "";
                        try {
                            authUrl = mProvider.retrieveRequestToken(mConsumer, "http://localhost/oauth_callback");
                        } catch (OAuthMessageSignerException | OAuthNotAuthorizedException | OAuthExpectationFailedException | OAuthCommunicationException ex) {
                            Log.e(TAG, "OAuth Retrieve Request Token Exception", ex);
                        }
                        WebView webview = new WebView(Login.this);
                        setContentView(webview);
                        webview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                        webview.setWebViewClient(new WebViewClient() {

                            boolean authComplete = false;

                            @Override
                            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                Uri callback = Uri.parse(url);
                                if (callback.toString().startsWith("http://localhost/oauth_callback") && !authComplete) {
                                    authComplete = true;
                                    String oauthVerifier = callback.getQueryParameter("oauth_verifier");
                                    try {
                                        mProvider.retrieveAccessToken(mConsumer,oauthVerifier);
                                    } catch (OAuthMessageSignerException | OAuthNotAuthorizedException | OAuthExpectationFailedException | OAuthCommunicationException ex) {
                                        Log.e(TAG, "OAuth Get Access Token Exception", ex);
                                    }
                                    OAuthConsumer consumer = new CommonsHttpOAuthConsumer(mConsumer.getConsumerKey(),mConsumer.getConsumerSecret());
                                    consumer.setTokenWithSecret(mConsumer.getToken(), mConsumer.getTokenSecret());
                                    Intent launchMain = new Intent(Login.this, Main.class);
                                    launchMain.putExtra("Consumer", consumer);
                                    startActivity(launchMain);
                                } else if (url.contains("401")) {
                                    Log.v(TAG, "Access Denied by Ravelry OAuth");
                                    authComplete = false;
                                    setContentView(R.layout.activity_login);
                                }
                                return super.shouldOverrideUrlLoading(view, url);
                            }
                        });
                        webview.loadUrl(authUrl);
                        break;
                    }
                }
        });
    }
}