package edu.mobile.ravelryknit;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.net.Uri;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

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
                        Log.v(TAG, authUrl);
                        webview.setWebViewClient(new WebViewClient() {

                            Intent resultIntent = new Intent();
                            boolean authComplete = false;

                            /* !!!!!!!!!need to make result intent to either try to login again or send to my activity
                            Intent intent = new Intent(this, DisplayMessageActivity.class);
                            EditText editText = (EditText) findViewById(R.id.edit_message);
                            String message = editText.getText().toString();
                            intent.putExtra(EXTRA_MESSAGE, message);
                            startActivity(intent);*/

                            @Override
                            public void onPageFinished(WebView view, String url) {
                                super.onPageFinished(view, url);
                                Uri callback = Uri.parse(url);
                                Log.v(TAG,url);
                                if (callback.toString().startsWith("http://localhost/oauth_callback") && !authComplete) {
                                    authComplete = true;
                                    String oauthVerifier = callback.getQueryParameter("oauth_verifier");
                                    Log.v(TAG,oauthVerifier);
                                    try {
                                        mProvider.retrieveAccessToken(mConsumer,oauthVerifier);
                                    } catch (OAuthMessageSignerException | OAuthNotAuthorizedException | OAuthExpectationFailedException | OAuthCommunicationException ex) {
                                        Log.e(TAG, "OAuth Get Access Token Exception", ex);
                                    }
                                    OAuthConsumer consumer = new CommonsHttpOAuthConsumer(mConsumer.getConsumerKey(),mConsumer.getConsumerSecret());
                                    consumer.setTokenWithSecret(mConsumer.getToken(), mConsumer.getTokenSecret());

                                    HttpGet request = new HttpGet("https://api.ravelry.com/current_user.json");
                                    // sign the request
                                    try {
                                        consumer.sign(request);
                                    } catch (OAuthMessageSignerException | OAuthExpectationFailedException | OAuthCommunicationException ex) {
                                        Log.e(TAG, "OAuth Sign Exception", ex);
                                    }

                                    // send the request
                                    HttpClient httpClient = new DefaultHttpClient();
                                    try {
                                        HttpResponse response = httpClient.execute(request);
                                        byte[] buffer = new byte[(int)response.getEntity().getContentLength()];
                                        int responseBytes = response.getEntity().getContent().read(buffer);
                                        int statusCode = response.getStatusLine().getStatusCode();
                                        Log.v(TAG,Integer.toString(statusCode));
                                        Log.v(TAG,Integer.toString(responseBytes));
                                        String decoded = new String(buffer, "UTF-8");
                                        Log.v(TAG,decoded);
                                    } catch (IOException ex) {
                                        Log.e(TAG, "HTTP Client/IO Exception", ex);
                                    }
                                }else if(url.contains("401")){
                                    Log.v(TAG, "Access Denied by Ravelry OAuth");
                                    authComplete = false;
                                }
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
                        Log.v(TAG, authUrl);
                        webview.setWebViewClient(new WebViewClient() {

                            Intent resultIntent = new Intent();
                            boolean authComplete = false;

                            @Override
                            public void onPageFinished(WebView view, String url) {
                                super.onPageFinished(view, url);
                                Uri callback = Uri.parse(url);
                                Log.v(TAG,url);
                                if (callback.toString().startsWith("http://localhost/oauth_callback") && !authComplete) {
                                    authComplete = true;
                                    String oauthVerifier = callback.getQueryParameter("oauth_verifier");
                                    Log.v(TAG,oauthVerifier);
                                    try {
                                        mProvider.retrieveAccessToken(mConsumer,oauthVerifier);
                                    } catch (OAuthMessageSignerException | OAuthNotAuthorizedException | OAuthExpectationFailedException | OAuthCommunicationException ex) {
                                        Log.e(TAG, "OAuth Get Access Token Exception", ex);
                                    }
                                    OAuthConsumer consumer = new CommonsHttpOAuthConsumer(mConsumer.getConsumerKey(),mConsumer.getConsumerSecret());
                                    consumer.setTokenWithSecret(mConsumer.getToken(), mConsumer.getTokenSecret());

                                    HttpGet request = new HttpGet("https://api.ravelry.com/current_user.json");
                                    // sign the request
                                    try {
                                        consumer.sign(request);
                                    } catch (OAuthMessageSignerException | OAuthExpectationFailedException | OAuthCommunicationException ex) {
                                        Log.e(TAG, "OAuth Sign Exception", ex);
                                    }

                                    // send the request
                                    HttpClient httpClient = new DefaultHttpClient();
                                    try {
                                        HttpResponse response = httpClient.execute(request);
                                        byte[] buffer = new byte[(int)response.getEntity().getContentLength()];
                                        int responseBytes = response.getEntity().getContent().read(buffer);
                                        int statusCode = response.getStatusLine().getStatusCode();
                                        Log.v(TAG,Integer.toString(statusCode));
                                        Log.v(TAG,Integer.toString(responseBytes));
                                        String decoded = new String(buffer, "UTF-8");
                                        Log.v(TAG,decoded);
                                    } catch (IOException ex) {
                                        Log.e(TAG, "HTTP Client/IO Exception", ex);
                                    }
                                }else if(url.contains("401")){
                                    Log.v(TAG, "Access Denied by Ravelry OAuth");
                                    authComplete = false;
                                }
                            }
                        });
                        webview.loadUrl(authUrl);
                        break;
                    }
                }
        });
    }
}