package edu.mobile.ravelryknit;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;


public class Display extends ActionBarActivity {
    private OAuthConsumer mConsumer = new CommonsHttpOAuthConsumer("B53DF0B7F0AAB1AC65C4", "pYx+Ks/8up8wVVWgov2AsR7HSym89hWbNLclIzrJ");
    private OAuthProvider mProvider = new CommonsHttpOAuthProvider(
            "https://www.ravelry.com/oauth/request_token",
            "https://www.ravelry.com/oauth/access_token",
            "https://www.ravelry.com/oauth/authorize");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        Intent intent = getIntent();
        int projectID = intent.getIntExtra(Search.EXTRA_INT, (int)-1); //getting projectID or -1 if error
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display, menu);
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
