package edu.mobile.ravelryknit;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;


public class Main extends ActionBarActivity {
    private static final String TAG = "Main";
    private OAuthConsumer consumer;
    private String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        Intent incomingIntent = getIntent();
        consumer = (OAuthConsumer) incomingIntent.getSerializableExtra("Consumer");
        currentUser = incomingIntent.getStringExtra("CurrentUser");

        HttpGet request = new HttpGet("https://api.ravelry.com/projects/search.json?query=#craft=knitting&sort=recently-popular");
        // sign the request
        try {
            consumer.sign(request);
        } catch (OAuthMessageSignerException | OAuthExpectationFailedException | OAuthCommunicationException ex) {
            Log.e(TAG, "OAuth Sign Exception", ex);
        }

        HttpResponse response = null;
        // send the request
        HttpClient httpClient = new DefaultHttpClient();
        try {
            response = httpClient.execute(request);
        } catch (IOException ex) {
            Log.e(TAG, "HTTP Client/IO Exception", ex);
        }
        //do stuff with the response
        byte[] buffer = new byte[(int) response.getEntity().getContentLength()];
        try {
            response.getEntity().getContent().read(buffer);
        } catch (IOException ex) {
            Log.e(TAG, "HTTPResponse IO Exception", ex);
        }
        int statusCode = response.getStatusLine().getStatusCode();
        Log.v(TAG, Integer.toString(statusCode));
        String decoded = "";
        try {
            decoded = new String(buffer, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Log.e(TAG, "UnsupportedEncodingException on buffer", ex);
        }
        Log.v(TAG,decoded);

        GridView gridview = (GridView) findViewById(R.id.gridView);
        //gridview.setAdapter(new ImageAdapter(this));
    }

    public class ImageAdapter extends BaseAdapter {
        private Context mContext;

        public ImageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return mThumbIds.length;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }

            imageView.setImageResource(mThumbIds[position]);
            return imageView;
        }

        // references to our images
        private Integer[] mThumbIds = {

        };
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.action_submit) {
            Intent launchSubmit = new Intent(Main.this, Submit.class);
            launchSubmit.putExtra("Consumer", consumer);
            launchSubmit.putExtra("CurrentUser", (Serializable) currentUser);
            startActivity(launchSubmit);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);



            return rootView;
        }
    }
}
