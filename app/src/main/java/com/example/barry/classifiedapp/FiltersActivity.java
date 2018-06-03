package com.example.barry.classifiedapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * i define the views
 * 2. get the views
 * 3. define an init() and call it in the onCreate()
 * 4. Define the shared preferences to save user preferences
 * 5. Add some definitions/ values to string.xml
 * 6. Add on click listener to on save button in our init()
 *
 */
public class FiltersActivity extends AppCompatActivity {

    private final static String TAG = "FiltersActivity";

    // widgets
    private Button mSave;
    private EditText mCity, mStateProvince, mCountry;
    private ImageView mBackArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);

        mSave = (Button) findViewById(R.id.btnSave);
        mCity = (EditText) findViewById(R.id.input_city);
        mStateProvince = (EditText) findViewById(R.id.input_state_province);
        mCountry = (EditText) findViewById(R.id.input_country);
        mBackArrow = (ImageView) findViewById(R.id.backArrow);

        init();

    }

    private void init() {
        getFilterPreferences();

        // set onClickListener to msave button
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "onClick: saving..." );

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(FiltersActivity.this);
                SharedPreferences.Editor editor = preferences.edit();

                Log.d(TAG, "onClick: city: " + mCity.getText());
                editor.putString(getString(R.string.preferences_city), mCity.getText().toString());
                editor.commit();

                Log.d(TAG, "onClick: state/province: " + mStateProvince.getText());
                editor.putString(getString(R.string.preferences_state_province), mStateProvince.getText().toString());
                editor.commit();

                Log.d(TAG, "onClick: country: " + mCountry.getText());
                editor.putString(getString(R.string.preferences_country), mCountry.getText().toString());
                editor.commit();
            }
        });

        // set on click listener to the back arrow button
        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClock: navigating back");
                finish();
            }
        });
    }

    private void getFilterPreferences() {
        Log.d(TAG, "getFilterPreferences: retrieving saved preferences.");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // def the strings
        String country = preferences.getString(getString(R.string.preferences_country), "");
        String state_province = preferences.getString(getString(R.string.preferences_state_province), "");
        String city = preferences.getString(getString(R.string.preferences_city), "");

        mCountry.setText(country);
        mStateProvince.setText(state_province);
        mCity.setText(city);
    }
}
