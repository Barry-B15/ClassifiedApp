package com.example.barry.classifiedapp;

/**
 * Created by barry on 11/5/2017.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.barry.classifiedapp.models.HitsList;
import com.example.barry.classifiedapp.models.HitsObject;
import com.example.barry.classifiedapp.models.Post;
import com.example.barry.classifiedapp.util.ElasticSearchAPI;
import com.example.barry.classifiedapp.util.PostListAdapter;
import com.example.barry.classifiedapp.util.RecyclerViewMargin;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Part 13:
 * 1. create init() and call it in onCreate()
 * 2. next, go and code the Filters Activity
 *
 * Part 19: set up for retrofit
 *      To construct the query, declare the edtText, and init it
 *
 * Part 2o:
 *  1. create a method to get the pw
 *  2. create a string for ElasticSearchPassword
 *  3. In strings.xml create node_elasticsearch, and password field name
 *  4. create the getFilter
 *
 *  Part 21;
 *  Create an arrayList to hold the values we are looking for
 *  Init the arrayList inside the msearchText
 *  Build the retrofil
 *  create a global var to hold the URL
 *
 *  Part 22
 *  1. Declare and init the recyclerView
 *      And add the dependency if not already add
 *      compile 'com.android.support:recyclerview-v7:26.+'
 * 2. Build the recycler view adapter, create a "PostListAdapter" class in the util dir
 *      create a view holder inside the new class
 *      also set the image view widget in the viewholder
 *
 * 3. Create a method for setting up the adapter "PostListAdapter()"
 *
 * 4. Create another class "RecyclerViewMargin()" to space the items on the list
 *
 * 5. now add this decorators from RecyclerViewMargin() to the searchFragment here to space things out
 *
 * Part 23
 * Inflate a new fragment on top of the visible fragment,
 * create that widget "FrameLayout" and init it
 * 2. Create a public void viewPost() method
 */
// when using android.support.v4.view.ViewPager,
// all fragments must extent android.support.v4.app.Fragment, NOT Fragment
public class SearchFragment extends android.support.v4.app.Fragment {

    private static final String TAG = "SearchFragment";
    // global var to hold the url
    private static final String BASE_URL = "http://35.192.62.195//elasticsearch/posts/post/";

    private static final int NUM_GRID_COLUMNS = 3; // no of grid columns
    private static final int GRID_ITEM_MARGIN = 5; // add amrgin to grid

    //widgets
    private ImageView mFilters;
    private EditText mSearchText;
    private FrameLayout mFrameLayout;

    //vars
    private String mElasticSearchPassword;
    private String mPrefCity;
    private String mPrefStateProv;
    private String mPrefCountry;
    private ArrayList<Post> mPosts;
    private RecyclerView mRecyclerView;
    private PostListAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        mFilters = (ImageView) view.findViewById(R.id.ic_search);
        mSearchText = (EditText) view.findViewById(R.id.input_search);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mFrameLayout = (FrameLayout) view.findViewById(R.id.container);

        getElasticSearchPassword();
        init();

        return view;
    }

    private void setupPostsList(){
        //add grid-column to image display on screen
        RecyclerViewMargin itemDecorator = new RecyclerViewMargin(GRID_ITEM_MARGIN, NUM_GRID_COLUMNS);
        mRecyclerView.addItemDecoration(itemDecorator);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), NUM_GRID_COLUMNS);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mAdapter = new PostListAdapter(getActivity(), mPosts);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void init(){
        mFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to filters activity.");
                Intent intent = new Intent(getActivity(), FiltersActivity.class);
                startActivity(intent);
            }
        });

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // this was working before, but I saw my key changed to next not return,
                // app started crashing. Turns out add more options is a fix
                // I added IME_ACTION_NEXT, IME_ACTION_GO and it works
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        ||actionId == EditorInfo.IME_ACTION_DONE
                        ||actionId == EditorInfo.IME_ACTION_GO
                        ||actionId == EditorInfo.IME_ACTION_NEXT
                        ||actionId == EditorInfo.IME_ACTION_NONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getKeyCode() == KeyEvent.KEYCODE_ENTER){

                    mPosts = new ArrayList<Post>();

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    ElasticSearchAPI searchAPI = retrofit.create(ElasticSearchAPI.class);

                    // create the header
                    HashMap<String, String> headerMap = new HashMap<String, String>();
                    headerMap.put("Authorization", Credentials.basic("user", mElasticSearchPassword));

                    // create the search string to handle diff params
                    String searchString = "";

                    if(!mSearchText.equals("")){
                        searchString = searchString + mSearchText.getText().toString() + "*"; // " * " makes search more robust
                    }
                    if(!mPrefCity.equals("")){
                        searchString = searchString + " city:" + mPrefCity;
                    }
                    if(!mPrefStateProv.equals("")){
                        searchString = searchString + " state_province:" + mPrefStateProv;
                    }
                    if(!mPrefCountry.equals("")){
                        searchString = searchString + " country:" + mPrefCountry;
                    }

                    // make the request and soecify the data type we are looking for "hits"
                    Call<HitsObject> call = searchAPI.search(headerMap, "AND", searchString);

                    call.enqueue(new Callback<HitsObject>() {
                        @Override
                        public void onResponse(Call<HitsObject> call, Response<HitsObject> response) {

                            HitsList hitsList = new HitsList();
                            String jsonResponse = "";
                            try{
                                Log.d(TAG, "onResponse: server response: " + response.toString());

                                if(response.isSuccessful()){ // if something is wrong with HitsList
                                    hitsList = response.body().getHits();
                                }else{ // respond with error
                                    jsonResponse = response.errorBody().string();
                                }

                                Log.d(TAG, "onResponse: hits: " + hitsList);

                                for(int i = 0; i < hitsList.getPostIndex().size(); i++){
                                    Log.d(TAG, "onResponse: data: " + hitsList.getPostIndex().get(i).getPost().toString());
                                    mPosts.add(hitsList.getPostIndex().get(i).getPost());
                                }

                                Log.d(TAG, "onResponse: size: " + mPosts.size());
                                //setup the list of posts
                                setupPostsList();

                            }catch (NullPointerException e){
                                Log.e(TAG, "onResponse: NullPointerException: " + e.getMessage() );
                            }
                            catch (IndexOutOfBoundsException e){
                                Log.e(TAG, "onResponse: IndexOutOfBoundsException: " + e.getMessage() );
                            }
                            catch (IOException e){
                                Log.e(TAG, "onResponse: IOException: " + e.getMessage() );
                            }
                        }

                        @Override
                        public void onFailure(Call<HitsObject> call, Throwable t) {
                            Log.e(TAG, "onFailure: " + t.getMessage() );
                            Toast.makeText(getActivity(), "search failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                return false;
            }
        });
    }

    // Do a fragment transaction to put the frame on top of the old one
    // part 23
    public void viewPost(String postId) {
        ViewPostFragment fragment = new ViewPostFragment();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

        Bundle args = new Bundle();
        args.putString(getString(R.string.arg_post_id), postId);
        fragment.setArguments(args);

        transaction.replace(R.id.container, fragment, getString(R.string.fragment_view_post));
        transaction.addToBackStack(getString(R.string.fragment_view_post));
        transaction.commit();

        // our frame is currently set to invisible, so make it visible
        mFrameLayout.setVisibility(View.VISIBLE);

        // Now go and call this method inside the PostListAdapter
    }

    @Override
    public void onResume() {
        super.onResume();
        getFilters();
    }

    private void getElasticSearchPassword(){
        Log.d(TAG, "getElasticSearchPassword: retrieving elasticsearch password.");

        Query query = FirebaseDatabase.getInstance().getReference()
                .child(getString(R.string.node_elasticsearch))
                .orderByValue();

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot singleSnapshot = dataSnapshot.getChildren().iterator().next();
                mElasticSearchPassword = singleSnapshot.getValue().toString();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getFilters(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mPrefCity = preferences.getString(getString(R.string.preferences_city), "");
        mPrefStateProv = preferences.getString(getString(R.string.preferences_state_province), "");
        mPrefCountry = preferences.getString(getString(R.string.preferences_country), "");

        Log.d(TAG, "getFilters: got filters: \ncity: " + mPrefCity + "\nState/Prov: " + mPrefStateProv
                + "\nCountry: " + mPrefCountry);
    }
    // The following are my codes with notes, using the above to check error
    /*// Create a log  (String TAG)
    // logt > enter created the following String
    
    private static final String TAG = "SearchFragment";

    // part 22
    private static final int NUM_GRID_COLUMNS = 3; // no of grid columns
    private static final int GRID_ITEM_MARGIN = 5; // add amrgin to grid

    // global var to hold the url
    private static final String BASE_URL = "http://35.192.62.195//elasticsearch/posts/post/";

    // widgets
    private ImageView mFilters;

    // part 19: set up to construct the query
    private EditText mSearchText;

    //var
    //20:
    private String mElasticSearchPassword; //string for ElasticSearchPassword
    private String mPrefCity;      // city, state, country preferences
    private String mPrefStateProv;
    private String mPrefCountry;
    // 21
    // Create an arrayList to hold the values
    private ArrayList<Post> mPosts;

    //Part: 22 Add recycler view
    private RecyclerView mRecyclerView;
    private PostListAdapter mAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // get the search view
        mFilters = (ImageView) view.findViewById(R.id.ic_search);

        // init the EditText Part19
        // this is the SearchText that is actually in the top in the bar
        mSearchText = (EditText) view.findViewById(R.id.input_search);

        // instantiate the recycler view
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        // calls
        getElasticSearchPassword();
        init();
        return view;
    }

    // part22: method for setting up the adapter
    private void setupPostsList() {
        // add the decorator to space list items and run
        RecyclerViewMargin itemDecorator = new RecyclerViewMargin(GRID_ITEM_MARGIN, NUM_GRID_COLUMNS);
        mRecyclerView.addItemDecoration(itemDecorator);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), NUM_GRID_COLUMNS);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mAdapter = new PostListAdapter(getActivity(), mPosts);
        mRecyclerView.setAdapter(mAdapter);
        // now call the PostList after we got all our posts in init > try() then Run
        // recyclerView does not have a way of setting distance btw cols, we will need create a class for that
        // Go to the utils directory > create "SquareImageView" to handle Images


    }
    private void init() {
        // add onClick so that when we click, it takes us to the Filters Activity
        mFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to filters activity.");
                Intent intent = new Intent(getActivity(), FiltersActivity.class);
                startActivity(intent); // next go and code the Filters Activity
            }
        });

        // override the return key on the keyboard so that users  click
        // the return key when they want to make a search, this will execute the search
        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                // add some conditions
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        ||actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getKeyCode() == KeyEvent.KEYCODE_ENTER){

                    //init the arrayList
                    mPosts = new ArrayList<Post>();

                    // lets construct everything we need for retrofit
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL) // this url is the one contained in the GET of our Postman for search so copy up to post/ and paste here
                            .addConverterFactory(GsonConverterFactory.create()) // we're using the gson converter
                            .build();

                    // create the Elastic Search API object
                    ElasticSearchAPI searchAPI = retrofit.create(ElasticSearchAPI.class);

                    // we need the header, default_operator and query tto execute our search
                    // let's do them one by one
                    // Header
                    HashMap<String, String> headerMap = new HashMap<String, String>();
                    headerMap.put("Authorization", Credentials.basic("user", mElasticSearchPassword));
                    // retrofit uses okhttp Credentials so choose that,
                    // .basic means we are using the basic auth, the one from out Postman, no-auth will also work

                    // createthe search string
                    String searchString = "";
                    // check if string is empty
                    if (!mSearchText.equals("")) {
                        searchString = searchString + mSearchText.getText().toString() + "*"; // * = search every keyword
                    }
                    if (!mPrefCity.equals("")) { //check the city
                        searchString = searchString + " city: " + mPrefCity; // assign the city
                    }
                    if (!mPrefStateProv.equals("")) { //check the state province
                        searchString = searchString + " state_province: " + mPrefStateProv; // assign the city
                    }
                    if (!mPrefCountry.equals("")) { //check the country
                        searchString = searchString + " country: " + mPrefCountry; // assign the city
                    }

                    // make the actual request, create the Call object and specify the type of data we're looking for
                    // the ff appends the search string to the end of the header with a +(AND)
                    Call<HitsObject>  call = searchAPI.search(headerMap, "AND", searchString);

                    // now execute the search
                    call.enqueue(new Callback<HitsObject>() {
                        @Override
                        public void onResponse(Call<HitsObject> call, Response<HitsObject> response) {

                            // ref out HitsList obj
                            HitsList hitsList = new HitsList();
                            String jsonResponse = "";
                            try {
                                // print response to log
                                Log.d(TAG, "onResponse: server response: " + response.toString());
                                // if response is ok
                                if (response.isSuccessful()) { // get the response body
                                    hitsList = response.body().getHits();
                                }
                                else { //otherwise, get the error body
                                    jsonResponse = response.errorBody().string();
                                }

                                Log.d(TAG, "onResponse: hits: " + hitsList);

                                // iterate thru the list of results
                                // print out all the posts data we saw in Postman
                                for (int i = 0; i < hitsList.getPostIndex().size(); i++) {
                                    Log.d(TAG, "onResponse: data: " + hitsList.getPostIndex().get(i).getPost().toString());
                                    mPosts.add(hitsList.getPostIndex().get(i).getPost());
                                }

                                // write out how big the post is, how many results we had
                                Log.d(TAG, "onResponse: size: " + mPosts.size());

                                // set up the list of posts, in this case by adding the post to recycler view
                                // part 22
                                setupPostsList();


                            } // catch exceptions
                            catch (NullPointerException e) {
                                Log.e(TAG, "onResponses: NullPointerException: " + e.getMessage());
                            }
                            catch (IndexOutOfBoundsException e) {
                                Log.e(TAG, "onResponses: IndexOutOfBoundsException: " + e.getMessage());
                            }
                            catch (IOException e) {
                                Log.e(TAG, "onResponses: IOException: " + e.getMessage());
                            }
                        }

                        // tell the user what happened
                        @Override
                        public void onFailure(Call<HitsObject> call, Throwable t) {
                            Log.e(TAG, "onFailure: " + t.getMessage());
                            Toast.makeText(getActivity(), "search failed", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                return false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // call getFilter
        getFilters();
    }

    // create a method to get the pw
    // also at the top create a string ElasticSearchPassword
    private void getElasticSearchPassword() {
        Log.d(TAG, "getElasticSearchPassword: retrieving elasticsearchpassword.");

        // create the query, error: choose the fb query to fix
        Query query = FirebaseDatabase.getInstance().getReference()
                .child(getString(R.string.node_elasticsearch)) // create this string in strings.xml
                //.child(getString(R.string.field_password)); // create this string in strings.xml
                .orderByValue();
        // execute the query
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot singleSnapshot = dataSnapshot.getChildren().iterator().next();
                mElasticSearchPassword = singleSnapshot.getValue().toString(); // we dont need to loop since its a single value
                // now go call this method inside onCreate()
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    // create the getFilter
    private void getFilters() {
        // get the preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // assign the prefs
        mPrefCity = preferences.getString(getString(R.string.preferences_city), "");
        mPrefStateProv = preferences.getString(getString(R.string.preferences_state_province), "");
        mPrefCountry = preferences.getString(getString(R.string.preferences_country), "");

        // print to log
        Log.d(TAG, "getFilters: got filters: \nCity: " + mPrefCity
                + "\nState/Prov: " + mPrefStateProv
                + "\nCountry: " +mPrefCountry);
        // now call getFilters() in onResume() so user filtered when user returns after leaving the app
    }*/
}
