package edu.mobile.ravelryknit;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    private TextView user;
    private TextView projectName;
    private TextView craft;
    private TextView patternName;
    private TextView yarn;
    private TextView created;
    private TextView completed;

    private String username = "None";
    private String name = "None";
    private String craftString = "None";
    private String patternString = "None";
    private String yarnString = "None";
    private Date createdDate;
    private boolean finished = false;
    private String unFinished = "Not Finished Yet";
    private Date completedDate;

    JSONObject jsonObj;

    private String currentUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        user = (TextView) findViewById(R.id.displayUserView);
        projectName = (TextView) findViewById(R.id.displayNameView);
        craft = (TextView) findViewById(R.id.displayCraftView);
        patternName = (TextView) findViewById(R.id.displayPatternView);
        yarn = (TextView) findViewById(R.id.displayYarnView);
        created = (TextView) findViewById(R.id.displayCreatedView);
        completed = (TextView) findViewById(R.id.displayCompletedView);
        Intent intent = getIntent();
        mConsumer = (OAuthConsumer) intent.getSerializableExtra("Consumer");
        currentUser = intent.getStringExtra("CurrentUser");

        try {
            jsonObj = new JSONObject(intent.getStringExtra("product"));
            Log.d("Display", intent.getStringExtra("product"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //getting username
        try {
            JSONObject userObject = jsonObj.getJSONObject("user");
            String usernameHelper = userObject.getString("username");
            if (usernameHelper != null){
                username = usernameHelper;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        user.setText(username);

        //getting project name
        try {
            name = jsonObj.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        projectName.setText(name);

        //getting craft
        try {
            craftString = jsonObj.getString("craft_name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        craft.setText(craftString);

        //getting pattern name
        try {
            patternString = jsonObj.getString("pattern_name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        patternName.setText(patternString);

        //getting yarn
        try {
            JSONArray packsObject = jsonObj.getJSONArray("packs");
            JSONObject yarnObject = packsObject.getJSONObject(0);
            yarnString = yarnObject.getString("yarn_name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        yarn.setText(yarnString);

        //getting created date
        try {
            String createdDateString = jsonObj.getString("created_at");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            createdDate = sdf.parse(createdDateString);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        created.setText((CharSequence) createdDate);

        //getting completed date
        try {
            finished = jsonObj.getBoolean("completed_day_set");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (finished) {
            try {
                String completedDateString = jsonObj.getString("created_at");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                completedDate = sdf.parse(completedDateString);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            completed.setText((CharSequence) completedDate);
        } else {
            completed.setText(unFinished);
        }


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

        if (item.getItemId() == android.R.id.home) {
            Intent result = new Intent((String) null);
            super.onPause();
            Intent intent = new Intent();
            result.putExtra("Consumer", mConsumer);
            result.putExtra("CurrentUser", currentUser);
            setResult(RESULT_OK, intent);
            finish();
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }
}
