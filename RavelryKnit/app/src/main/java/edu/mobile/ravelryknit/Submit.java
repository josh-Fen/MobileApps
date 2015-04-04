package edu.mobile.ravelryknit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.view.View.OnClickListener;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

/**
 * Created by Kaitlyn on 3/31/2015.
 */
public class Submit extends Activity implements OnItemSelectedListener, OnClickListener {
    private static final String TAG = "Submit";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private OAuthConsumer mConsumer = new CommonsHttpOAuthConsumer("B53DF0B7F0AAB1AC65C4", "pYx+Ks/8up8wVVWgov2AsR7HSym89hWbNLclIzrJ");
    private OAuthProvider mProvider = new CommonsHttpOAuthProvider(
            "https://www.ravelry.com/oauth/request_token",
            "https://www.ravelry.com/oauth/access_token",
            "https://www.ravelry.com/oauth/authorize");

    private OAuthConsumer consumer;
    private String apiAccessKey;
    private String urlBase = "https://api.ravelry.com";
    private String username;

    private String projectCraft = "";
    private String yarnColor = "";
    private String yarnWeight = "";

    private EditText projectName;
    private EditText projectPatternName;
    private EditText projectNotes;
    private EditText projectYarn;

    private File picture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);
        Log.v(TAG, "onCreate");
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        consumer = (OAuthConsumer) this.getIntent().getSerializableExtra("Consumer");


        //Project Name -- can't be blank
        projectName = (EditText) findViewById(R.id.project_name);

        //Craft Choices
        Spinner projectCraft = (Spinner) findViewById(R.id.project_craft);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> craftAdapter = ArrayAdapter.createFromResource(this,
                R.array.craft_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        craftAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        projectCraft.setAdapter(craftAdapter);
        projectCraft.setOnItemSelectedListener(this);

        //Pattern Name -- can't be blank
        projectPatternName = (EditText) findViewById(R.id.project_pattern_name);

        //Picture
        Button takePicture = (Button) findViewById(R.id.project_picture);
        takePicture.setOnClickListener(this);

        //Notes
        projectNotes = (EditText) findViewById(R.id.project_notes);

        //Yarn Name
        projectYarn = (EditText) findViewById(R.id.project_yarn);

        //Yarn Color
        Spinner projectYarnColor = (Spinner) findViewById(R.id.project_yarn_color);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> colorAdapter = ArrayAdapter.createFromResource(this,
                R.array.yarn_color_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        projectYarnColor.setAdapter(colorAdapter);
        projectYarnColor.setOnItemSelectedListener(this);

        //Yarn Weight
        Spinner projectYarnWeight = (Spinner) findViewById(R.id.project_yarn_weight);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> weightAdapter = ArrayAdapter.createFromResource(this,
                R.array.yarn_weight_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        weightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        projectYarnWeight.setAdapter(weightAdapter);
        projectYarnWeight.setOnItemSelectedListener(this);

        //Submit
        Button submitButton = (Button) findViewById(R.id.project_submit);
        submitButton.setOnClickListener(this);
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.project_picture:
                takePicture();
            case R.id.project_submit:
                boolean complete = validateSubmission();
                if(complete) {
                    submitProject();
                } else{
                    Toast.makeText(getApplicationContext(), R.string.fix_submit, Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //when an image is captured, get its uri
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ImageView imageView = (ImageView) findViewById(R.id.project_image);
            imageView.setImageBitmap(imageBitmap);
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // An item was selected in a spinner
        switch (parent.getId()){
            case R.id.project_craft:
                projectCraft = (String) parent.getItemAtPosition(pos);
            case R.id.project_yarn_color:
                yarnColor = (String) parent.getItemAtPosition(pos);
            case R.id.project_yarn_weight:
                yarnWeight = (String) parent.getItemAtPosition(pos);
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback -- I think blank is ok??
    }

    public void takePicture(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e(TAG, "IO Exception while creating image file", ex);
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    public boolean validateSubmission(){
        String projectNameString = projectName.getText().toString();
        String patternNameString = projectPatternName.getText().toString();
        //valid if project name and pattern name are filled in
        return !(projectNameString.equals("") || patternNameString.equals(""));
    }

    public void submitProject(){
        JSONObject projectObject = new JSONObject();
        JSONObject yarnObject = new JSONObject();

        //get values from EditText boxes
        String yarnNameString = projectYarn.getText().toString();
        String patternNameString = projectPatternName.getText().toString();
        String projectNameString = projectName.getText().toString();
        String projectNotesString = projectNotes.getText().toString();

        //Make yarn JSON Object
        try {
            if(!yarnColor.equals("")){
                //get yarn color array-- GET /color_families.json
                HttpGet yarnColorGet = new HttpGet(urlBase + "/color_families.json");
                HttpResponse yarnColorResponse = sendGet(yarnColorGet);
                JSONObject yarnColorObject = getJSONObject(yarnColorResponse);
                String yarnColorId = "";
                try {
                    JSONArray yarnColorArray = yarnColorObject.getJSONArray("color_families");
                    for (int i = 0; i < yarnColorArray.length(); i++) {
                        JSONObject obj = yarnColorArray.getJSONObject(i);
                        if (obj.get("name").equals(yarnColor)) {
                            yarnColorId = obj.getString("id");
                        }
                    }
                } catch (JSONException e){
                    Log.e(TAG, "JSON Exception at find yarn color id", e);
                }
                yarnObject.put("color_family_id", yarnColorId);

            }
            if(!yarnNameString.equals("")){
                yarnObject.put("personal_name", yarnNameString);
            }
            if(!yarnWeight.equals("")){
                //get yarn weight array -- GET /yarn_weights.json
                HttpGet yarnWeightGet = new HttpGet(urlBase + "/yarn_weights.json");
                HttpResponse yarnWeightResponse = sendGet(yarnWeightGet);
                JSONObject yarnWeightObject = getJSONObject(yarnWeightResponse);
                //TODO:remove later
                Log.v(TAG, yarnWeightObject.toString());
                //has id, name, ply, wpi
                //Pattern to get just the name of the yarn weight
                Pattern pattern = Pattern.compile("([A-Za-z])+(\\s([A-Za-z])+)*");
                Matcher matcher = pattern.matcher(yarnWeight);
                matcher.find();
                String yarnWeightString = matcher.group();
                String yarnWeightId = "";
                try {
                    JSONArray yarnWeightArray = yarnWeightObject.getJSONArray("yarn_weights");
                    for (int i = 0; i < yarnWeightArray.length(); i++) {
                        JSONObject obj = yarnWeightArray.getJSONObject(i);
                        if (obj.get("name").equals(yarnWeightString)) {
                            yarnWeightId = obj.getString("id");
                        }
                    }
                } catch (JSONException e){
                    Log.e(TAG, "JSON Exception at find yarn color id", e);
                }
                yarnObject.put("personal_yarn_weight_id", yarnWeightId);
            }
        } catch (JSONException e){
            Log.e(TAG, "JSON Exception at creating yarn object", e);
        }

        //Make project JSON Object
        try {
            //Project Name
            projectObject.put("name", projectNameString);

            //Craft
            if(!projectCraft.equals("")){
                //get craft array -- POST /projects/crafts.json
                HttpPost craftArrayPost = new HttpPost(urlBase + "/projects/crafts.json");
                HttpResponse craftArrayResponse = sendPost(craftArrayPost);
                JSONObject craftArrayObject = getJSONObject(craftArrayResponse);
                //TODO:remove later
                //Log.v(TAG, craftArrayObject.toString());
                //find craft id from array - has id, name

                String craftId = "";
                try {
                    JSONArray yarnColorArray = craftArrayObject.getJSONArray("crafts");
                    for (int i = 0; i < yarnColorArray.length(); i++) {
                        JSONObject obj = yarnColorArray.getJSONObject(i);
                        if (obj.get("name").equals(projectCraft)) {
                            craftId = obj.getString("id");
                        }
                    }
                } catch (JSONException e){
                    Log.e(TAG, "JSON Exception at find yarn color id", e);
                }
                projectObject.put("craft_id", craftId);
            }

            //Notes
            if(!projectNotesString.equals("")){
                projectObject.put("notes", projectNotesString);
            }

            //Pattern Name
            projectObject.put("personal_pattern_name", patternNameString);

            //Yarn
            projectObject.put("packs", yarnObject);

        } catch (JSONException e) {
            Log.e(TAG, "JSON Exception while creating project object", e);
        }

        //Submit the project
        HttpPost projectPost = new HttpPost(urlBase + "/projects/" + username + "/create.json");
        //add params to the post
        List<NameValuePair> projectParams = new ArrayList<NameValuePair>();
        projectParams.add(new BasicNameValuePair("data", projectObject.toString()));
        try {
            projectPost.setEntity(new UrlEncodedFormEntity(projectParams));
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "Encoding Error with project post", e);
        }
        HttpResponse projectResponse = sendPost(projectPost);
        JSONObject completedProjectObject = getJSONObject(projectResponse);
        //Get the project ID from the object in the response
        int projectId = 0;
        try {
            projectId = completedProjectObject.getInt("id");
        } catch (JSONException e) {
            Log.e(TAG, "JSON Exception at get project ID", e);
        }

        //add photo

        //Get upload Token
        HttpPost requestTokenPost = new HttpPost(urlBase + "/upload/request_token.json");
        HttpResponse requestTokenResponse = sendPost(requestTokenPost);
        String uploadToken = (String) requestTokenResponse.getParams().getParameter("upload_token");

        //Upload the image
        HttpPost uploadImagePost = new HttpPost(urlBase + "/upload/image.json");
        //create the multipart image
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addBinaryBody("file0", picture, ContentType.create("image/jpeg"), picture.getName());
        HttpEntity multipart = builder.build(); //THIS IS AN ENTITY
        //add params to the post
        List<NameValuePair> uploadImageParams = new ArrayList<NameValuePair>();
        uploadImageParams.add(new BasicNameValuePair("upload_token", uploadToken));//CANT ADD OTHER PARAMS TO IT? I THINK
        uploadImageParams.add(new BasicNameValuePair("access_key", apiAccessKey));
        uploadImageParams.add(new BasicNameValuePair("file0", "multipart.tostring???"));//CANT ADD IT TO PARAMS HERE
        try {
            uploadImagePost.setEntity(new UrlEncodedFormEntity(uploadImageParams));//PRETTY SURE YOU CANT HAVE TWO ENTITIES SET EITHER
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "Encoding Error with upload image post", e);
        }
        HttpResponse uploadImageResponse = sendPost(projectPost);
        //TODO:fix this
        JSONObject uploadImageResponseObject = (JSONObject) uploadImageResponse.getParams().getParameter("uploads");
        int imageId = 0;
        try {
            imageId = uploadImageResponseObject.getInt("image_id");
        } catch (JSONException e) {
            Log.e(TAG, "JSON Exception with image ID", e);
        }

        //Add the picture to the project
        HttpPost picturePost = new HttpPost(urlBase + "/projects/" + username + "/" + projectId + "/create_photo.json");
        List<NameValuePair> pictureParams = new ArrayList<NameValuePair>();
        pictureParams.add(new BasicNameValuePair("image_id", Integer.toString(imageId)));
        try {
            picturePost.setEntity(new UrlEncodedFormEntity(pictureParams));
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "Encoding Error with picture post", e);
        }
        sendPost(picturePost);
    }

    private HttpResponse sendPost(HttpPost request){
        // sign the request
        try {
            consumer.sign(request);
        } catch (OAuthMessageSignerException | OAuthExpectationFailedException | OAuthCommunicationException ex) {
            Log.e(TAG, "OAuth Sign Exception", ex);
        }
        // send the request
        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse response = null;
        try {
            response = httpClient.execute(request);
        } catch (IOException ex) {
            Log.e(TAG, "HTTP Client/IO Exception", ex);
        }
        return response;
    }

    private HttpResponse sendGet(HttpGet request){
        // sign the request
        try {
            consumer.sign(request);
        } catch (OAuthMessageSignerException | OAuthExpectationFailedException | OAuthCommunicationException ex) {
            Log.e(TAG, "OAuth Sign Exception", ex);
        }
        // send the request
        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse response = null;
        try {
            response = httpClient.execute(request);
        } catch (IOException ex) {
            Log.e(TAG, "HTTP Client/IO Exception", ex);
        }
        return response;
    }

    private JSONObject getJSONObject(HttpResponse response){
        String json = "";
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            json = reader.readLine();
        } catch (IOException e){
            Log.e(TAG, "IO Exception while reading HTTP response", e);
        }
        JSONTokener tokener = new JSONTokener(json);
        JSONObject object = null;
        try {
            object = new JSONObject(tokener);
        } catch (JSONException e) {
            Log.e(TAG, "JSON Exception while reading HTTP response", e);
        }
        return object;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        picture = image;

        // Save a file: path for use with ACTION_VIEW intents
        //photoUri = "file:" + image.getAbsolutePath();
        return image;
    }
}
