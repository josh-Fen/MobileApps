package edu.mobile.ravelryknit;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
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
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build(); StrictMode.setThreadPolicy(policy);
        }
        btnLogin = (Button) findViewById(R.id.submit);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.submit:
                        mProvider.setOAuth10a(true);
                        String authUrl = "";
                        try {
                            authUrl = mProvider.retrieveRequestToken(mConsumer, OAuth.OUT_OF_BAND);
                        } catch (OAuthMessageSignerException | OAuthNotAuthorizedException | OAuthExpectationFailedException | OAuthCommunicationException ex) {
                            Log.e(TAG, "OAuth Retrieve Request Token Exception", ex);
                        }
                        startActivity(new Intent("android.intent.action.VIEW", Uri.parse(authUrl)));
                        break;
                }
            }});
    }

    @Override
    protected void onResume() {
        super.onResume();
        // extract the token if it exists
        Log.v(TAG,"onResume");
        Uri uri = this.getIntent().getData();
        if (uri == null) {
            setContentView(R.layout.activity_login);
            btnLogin = (Button) findViewById(R.id.submit);
            btnLogin.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.submit:
                            mProvider.setOAuth10a(true);
                            String authUrl = "";
                            try {
                                authUrl = mProvider.retrieveRequestToken(mConsumer, OAuth.OUT_OF_BAND);
                            } catch (OAuthMessageSignerException | OAuthNotAuthorizedException | OAuthExpectationFailedException | OAuthCommunicationException ex) {
                                Log.e(TAG, "OAuth Retrieve Request Token Exception", ex);
                            }
                            startActivity(new Intent("android.intent.action.VIEW", Uri.parse(authUrl)));
                            break;
                    }
                }
            });
        } else {
            Log.d(TAG, uri.toString());
            String token = uri.getQueryParameter("oauth_token");
            String verifier = uri.getQueryParameter("oauth_verifier");
            try {
                mProvider.retrieveAccessToken(mConsumer, verifier);
            } catch (OAuthMessageSignerException | OAuthNotAuthorizedException | OAuthExpectationFailedException | OAuthCommunicationException ex) {
                Log.e(TAG, "OAuth Get Token Exception", ex);
            }
            OAuthConsumer consumer = new CommonsHttpOAuthConsumer(mConsumer.getToken(), mConsumer.getTokenSecret());
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
            } catch (IOException ex) {
                Log.e(TAG, "HTTP Client/IO Exception", ex);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG,"onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.v(TAG,"onRestart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG,"onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG,"onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG,"onDestroy");
    }
}
