package edu.mobile.ravelryknit;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;


public class Main extends ActionBarActivity {
    private static final String TAG = "Main";
    private OAuthConsumer consumer;
    private String currentUser;
    private ArrayList<loadedImage> loadedImages;
    private android.view.Display display;
    private Point size;

    public class ImageAdapter extends BaseAdapter {
        private Context mContext;

        private ArrayList<loadedImage> mImages;

        public ImageAdapter(Context c, ArrayList<loadedImage> loadedIs) {
            mContext = c;
            mImages = loadedIs;
        }

        public int getCount() {
            return mImages.size();
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
                size = new Point();
                display.getSize(size);
                int width = size.x;
                imageView = new ImageView(Main.this);
                imageView.setPadding(8, 0, 8, 0);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, width/3));
            } else {
                imageView = (ImageView) convertView;
            }
            imageView.setImageBitmap(mImages.get(position).getImage());
            imageView.setTag(mImages.get(position).getTag());
            return imageView;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent incomingIntent = getIntent();
        consumer = (OAuthConsumer) incomingIntent.getSerializableExtra("Consumer");
        currentUser = incomingIntent.getStringExtra("CurrentUser");

        loadedImages = new ArrayList<loadedImage>();
        display = getWindowManager().getDefaultDisplay();

        HttpGet request = new HttpGet("https://api.ravelry.com/projects/search.json?craft=knitting&sort=best&page_size=12");
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
        StringBuilder decoded = new StringBuilder();
        InputStream content = null;
        try {
            content = response.getEntity().getContent();
        } catch (IOException ex) {
            Log.e(TAG, "HTTPResponse IO Exception", ex);
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(content));
        String line;
        try {
            while((line = reader.readLine()) != null){
                decoded.append(line);
            }
        } catch (IOException ex) {
            Log.e(TAG, "HTTPResponse IO Exception", ex);
        }

        int statusCode = response.getStatusLine().getStatusCode();
        Log.v(TAG, Integer.toString(statusCode));
        JSONObject projectsResponse = null;
        try {
            projectsResponse = new JSONObject(decoded.toString());
        } catch (JSONException ex) {
            Log.e(TAG, "JSON Exception", ex);
        }
        JSONArray projects = null;
        try {
            projects = projectsResponse.getJSONArray("projects");
        } catch (JSONException|NullPointerException ex) {
            Log.e(TAG, "JSON Exception", ex);
        }
        for (int i = 0; i<projects.length(); i++) {
            JSONObject project = null;
            try {
                project = projects.getJSONObject(i);
            } catch (JSONException ex) {
                Log.e(TAG, "JSON Exception", ex);
            }
            JSONObject firstPhoto = null;
            try{
                firstPhoto = project.getJSONObject("first_photo");
            } catch (JSONException|NullPointerException ex) {
                Log.e(TAG, "JSON Exception", ex);
            }
            loadedImage li = new loadedImage();
            try {
                li.setURL(firstPhoto.getString("medium_url"));
            } catch (JSONException|NullPointerException ex) {
                Log.e(TAG, "JSON Exception", ex);
            }
            try {
                li.setTag(project.toString());
            } catch (NullPointerException ex) {
                Log.e(TAG, "Null Pointer Exception", ex);
            }
            loadedImages.add(li);
        }



        GridView gridview = (GridView) findViewById(R.id.gridView);
        ImageAdapter imageAd = new ImageAdapter(this,loadedImages);
        gridview.setAdapter(imageAd);

        for (loadedImage li : loadedImages){
            li.loadImage(imageAd);
        }

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Intent launchDisplay = new Intent(Main.this, Display.class);
                launchDisplay.putExtra("Consumer", consumer);
                launchDisplay.putExtra("CurrentUser", (Serializable) currentUser);
                String productTag = v.getTag().toString();
                launchDisplay.putExtra("product", productTag);
                startActivity(launchDisplay);
            }
        });

    }
    private class loadedImage {
        private Bitmap image;

        private ImageAdapter ia;

        private String imgUrl;

        private String tag;

        public void setURL(String url) {
            imgUrl = url;
        }

        public void setTag(String tagString) {
            tag = tagString;
        }

        public String getTag() {
            return tag;
        }

        public Bitmap getImage() {
            return image;
        }

        public void loadImage(ImageAdapter imageA) {
            this.ia = imageA;
            if (imgUrl != null && !imgUrl.equals("")) {
                new ImageLoadTask().execute(imgUrl);
            }
        }
        private class ImageLoadTask extends AsyncTask<String, String, Bitmap> {

            @Override
            protected void onPreExecute() {
                Log.v(TAG, "Loading image...");
            }

            // PARAM[0] IS IMG URL
            protected Bitmap doInBackground(String... param) {
                Log.v(TAG, "Attempting to load image URL: " + param[0]);
                URL url = null;
                try {
                    url = new URL(param[0]);
                } catch (MalformedURLException|NullPointerException ex) {
                    Log.e(TAG, "Malformed URL Exception", ex);
                }
                try {
                    Bitmap b = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    return b;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            protected void onProgressUpdate(String... progress) {
                // NO OP
            }

            protected void onPostExecute(Bitmap ret) {
                if (ret != null) {
                    image = ret;
                    if (ia != null) {
                        // WHEN IMAGE IS LOADED NOTIFY THE ADAPTER
                        ia.notifyDataSetChanged();
                    }
                } else {
                    Log.e("ImageLoadTask", "Failed to load image");
                }
            }
        }

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
}
