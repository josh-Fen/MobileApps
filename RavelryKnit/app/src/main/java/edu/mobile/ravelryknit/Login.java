package edu.mobile.ravelryknit;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.net.Uri;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;


public class Login extends Activity implements View.OnClickListener {
    private EditText username;
    private EditText password;

    private OAuthConsumer mConsumer = new CommonsHttpOAuthConsumer("B53DF0B7F0AAB1AC65C4", "pYx+Ks/8up8wVVWgov2AsR7HSym89hWbNLclIzrJ");
    private OAuthProvider mProvider = new CommonsHttpOAuthProvider(
            "https://www.ravelry.com/oauth/request_token",
            "https://www.ravelry.com/oauth/access_token",
            "https://www.ravelry.com/oauth/authorize");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        View btnLogin = (Button) findViewById(R.id.submit);
        btnLogin.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // extract the token if it exists
        Uri uri = this.getIntent().getData();
        String token = uri.getQueryParameter("oauth_token");
        String verifier = uri.getQueryParameter("oauth_verifier");
        mProvider.retrieveAccessToken(mConsumer, verifier);
        String accessToken[] = new String[] { mConsumer.getToken(), mConsumer.getTokenSecret() };

        OAuthConsumer consumer = new CommonsHttpOAuthConsumer(accessToken[0],
                accessToken[1]);
        HttpGet request = new HttpGet(url);
        // sign the request
        consumer.sign(request);
        // send the request
        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse response = httpClient.execute(request);
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.submit:
                String mCallbackUrl;
                mProvider.setOAuth10a(true);
                mCallbackUrl = ("login://cse.osu.edu" == null ? OAuth.OUT_OF_BAND :"login://cse.osu.edu");
                String authUrl = mProvider.retrieveRequestToken(mConsumer, mCallbackUrl);
                startActivity(new Intent("android.intent.action.VIEW", Uri.parse(authUrl)));
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
