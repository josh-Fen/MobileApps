package edu.mobile.ravelryknit;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.view.View.OnClickListener;

/**
 * Created by Kaitlyn on 3/31/2015.
 */
public class Submit extends Activity implements OnItemSelectedListener, OnClickListener {
    private static final String TAG = "Submit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);
        Log.v(TAG, "onCreate");
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        //Project Name
        EditText projectName = (EditText) findViewById(R.id.project_name);

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

        //Pattern Location Choices
        Spinner projectPatternSource = (Spinner) findViewById(R.id.project_pattern_source);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> locationAdapter = ArrayAdapter.createFromResource(this,
                R.array.pattern_source_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        projectCraft.setAdapter(locationAdapter);
        projectCraft.setOnItemSelectedListener(this);

        //Pattern Name
        EditText projectPatternName = (EditText) findViewById(R.id.project_pattern_name);

        //Pattern Source Name
        EditText projectPatternSourceName = (EditText) findViewById(R.id.project_pattern_source_name);

        //Picture
        Button takePicture = (Button) findViewById(R.id.project_picture);

        //Notes
        EditText projectNotes = (EditText) findViewById(R.id.project_notes);
    }

    public void onClick(View v){

    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

}
