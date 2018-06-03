package com.example.barry.classifiedapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.barry.classifiedapp.util.SectionsPagerAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * https://www.youtube.com/watch?v=a4Dks1Y_vY8
 * https://www.elastic.co/guide/index.html
 * https://firebase.google.com/docs/functions/get-started?authuser=0
 *
 * After designing the login and register new user to the app, next is to add a collapsing toolbar
 * 1. Add design support library to the dependencies
 * 2. change search_activity from RelativeLayout to use coordinatorLayout
 *
 * 3. scroll|exitUntilCollapsed, this in our xml file will make the toolbar collapse
 *      and paralax is what makes it collapsible
 *      anything we put below it, will not collapse
 *      Can text is by adding a RelativeLayout below and put some text in it
 *
 * 4. To use Tabs, we need section pager adapter, go ahead and create one
 *  create a new java class in the util package, call it SectionspagerAdapter,
 *  have it extend FragmentPagerAdapter
 *
 *  5. Back to search.java, create a TAG, then widget, var sections
 *  6. To make a collapsing toolbar we need an AppBar, a Collapsing ToolBar
 *  and whatever we need inside the collapsing Toolbar then
 *  beneath that we need a nested ScrollView for all the scrollable content and we
 *  need to implement the AppBarScrolling view behavior
 *
 *  source: https://www.youtube.com/watch?v=Qv4cYwfb360&t=2s
 *  1. For the tabs, create Fragment layouts: fragment_post, fragment_search,
 *          fragment_view_post, fragment_watch_list, fragment_account
 *  2. create the actual fragment classes:
 *      WatchListFragment, have it extent Fragments, import the fragment library,
 *      ctrl+o to insert onCreateView, remove the super and replace with View view = inflater()
 *      and return view;
 *
 *  3. create the next 4 (PostFragment, WatchFragment, SearchFragment, AccountFragment) by copying
 *      and pasting and editing the names as needed
 *  4. Setup the fragments with the tab
 *      add the Fragments to the TabLayout in SearchActivity, here
 *
 *  https://www.youtube.com/watch?v=5lItVuLRolY
 *  Sign Out
 *  1. Go to fragment_account, add a button to sign users out
 *  2. Go to add code to the AccountFragment
 *      add FirebaseAuth, widget.
 *
 * https://www.youtube.com/watch?v=cBS_qL3BUnM&index=7&list=PLgCYzUzKIBE-G0tuxjKGkl_keIW2FFwKX
 * Part 7: https://www.youtube.com/watch?v=cBS_qL3BUnM
 *      PERMISSIONS
 * Get the ff permissions in the MaNIFEST
 * <uses-permission android:name="android.permission.INTERNET"/>
 * <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
 * <uses-permission android:name="android.permission.CAMERA"/>
 *
 * 3 ASK USER FOR PERMISSSIONS IN sEARCHaCTIVITY
 *
 * Part 8: Sending data from SelectPhoto Dialog, images from the camera to Fragments
 *
 * 1. Go to PostFragment, create the dialog in init(), under mPostImage.setOnClickListener
 *
 *  Part 13: Finished with PostFragment, come back here / Go to SearchFragment
 *  Saving Search Preferences
 *  https://www.youtube.com/watch?v=qjGUaJe4sOY&t=65s
 *  https://www.youtube.com/watch?v=qjGUaJe4sOY&t=73s
 *  saving filters like state province etc is not a good way to go for a production app but
 *  this app is focused on elastic search so we are going to do this filters just to get going.
 *  A better way would be to add google map where people ca select their state or city but
 *  that will take time for coding
 *
 *  1. Go to fragment_search.xml and add codes
 *         get the drawable icons
 *  2. create the drawable grey_border_bottom
 *      add items
 *  3. Add recyclerView dependencies to gradle
 *  4. Go build the layout for the filters so that when we click the magnifying glass,
 *      it brings out the search activity
 *
 *   5. Create the FiltersActivity and dont forget to add it to the Manifest
 *
 *   6. Download the drawable ic_back_arrow
 *      right-clack drawable > new > image asset > select action bar and tab icons >
 *          back arror > add paddings to reduce the size > ok > finish
 *
 *   7. Add views to the filters.xml
 *   8. Now go to SearchFragment.java and create init() method
 *
 *   Part 14: Deploying Elastic Search - To do
 *   https://www.youtube.com/watch?v=pR0-c17rfq8&index=14&list=PLgCYzUzKIBE-G0tuxjKGkl_keIW2FFwKX
 *
 *  1. Go to Fb and select this app
 *      Elastic search is gonna be a copy of our fb
 *  2. Click on Upgrade plan (left panel)
 *      select "pay as you go" to upgrade to the Blaze plan
 *      Click "Purchase" and add credit card details. With the amt of data we will be generating
 *      it should be free but gg just want to make sure we can pay if we overuse
 *  3. Integrate elastic search tool into our project,
 *      go to: console.cloud.google.com  (this is where we can add tools to our project)
 *  4. Select the project
 *          make sure you select the correct project on the top panel
 *          may need to navigate to All, to see all projects
 *  5. Open a new tab, go to: console.cloud.google.com/freetrial
 *      GCP says I am not new so not eligible for free trial
 *  6. Click the hamburger 3 lines on the teft panel
 *          go to Cloud Launcher
 *          search : elastic search
 *          select: Elastic Search certified by Bitnami (click on it)
 *          click Launch on COMPUTE ENGINE
 *  7. Decrease the price by clicking on machine type and select micro
 *      the click DEPLOY
 *
 *  Part 15: https://www.youtube.com/watch?v=Wv2EoMQ-5vQ&index=15&list=PLgCYzUzKIBE-G0tuxjKGkl_keIW2FFwKX
 *  Creating an ElasticSearch Index with Postman
 *  1. To go to the elastic Search server, go to
 *      https://console.cloud.google.com/deployments/details/elasticsearch-1?.......
 *      Get details like password, user id, ----
 *      click on the server address ( top link), you will be prompted to log in.
 *      All we need is to enter the provided "user" and enter the password to log in
 *      see the json, we don't really need that right away as we will be creating one
 *
 *  2.    Google elastic search documentation
 *      see: elastic.co.guide
 *
 *  3. We will be using a Google Chrome extension, Postman, to communicate with Elastic Search
 *      to "send" and "retrieve" data, or to create index
 *      so download and Instal "Postman" from Chrome Store
 *      create an account and log in
 *
 *  4. In Elastic search Documentation, search for "Create Index"
 *      go to Create mapping, see the json, this is what we will be creating
 *      example uses "Put" but we will be creating "Post" with properties
 *  5. Open up Postman, expand Get and select Post
 *      We want to create a post for elastic search so go back to elastic search,
 *      open the json file copy the link and paste in Postman, in the provided space next to Post
 *      Since we will be creating posts, add ".../posts" to the end of the link
 *          http://35.192.62.195//elasticsearch/posts
 *      That means, We are connecting to our elastic search server,
 *      and creating an index called posts.
 *  6. Next let's create the body structure,
 *      In Postman, click on Body, the raw,
 *      expand "text", select json and type in the body
 *      {
             "mappings": {
                 "post": {
                     "properties": {

                     }
                 }
            }
        }
    this is the actual mapping of our data
        here we omitted the PUT {} because that will be created automatically for us by Postman
        and will also set the no_of_shards to 1 for us
    ###Change POST back to PUT or it will give error###
 *  7. Add the properties: city, contact_email, description, post_id, ---- as needed
 *      "city": {
 *          "type": "text"
 *      },
 *      Repeat this for all the properties and edit as necessary (remember to remove comma after the last"}")
 *      we had 10 properties in our app/fb node so we should have the same properties here.
 *      we dont need price here so we have 9, the price parameter will be filtered on the
 *      client side thats why we are not saving price to our index.
 *  8. We need to add proper authorization so that everyone cannot send data to our server
 *      In Postman, click "Headers", in new Key, type in "Authorization and add the "Authorization Token"
 *  9. Get the token, refer to the elastic search documentation
 *      > configuring clients and integration > Using elastic http/rest with Shield
 *      https://www.elastic.co/guide/en/shield/2.4/_using_elasticsearch_http_rest_clients_with_shield.html
 *      Says we need to put this:
 *          Authorization: Basic <TOKEN>
 *
 *      And:
 *      The <TOKEN> is computed as base64(USERNAME:PASSWORD)
 *      That is our Elastic Search password given earlier
 *      To convert that to Base64, just google Base64 encoder or go to
 *      https://www.base64encode.org/
 *      > click on Encode > type in: user.pasteThePasswordHere
 *      > then click encode
 *      > copy the encoded pw and paste in Postman:
 *      type in Basic and paste encoded PW next "Basic pasteEncoded PW"
 *      > Send
 *
 *      We get the ff for success:
 *          {
                 "acknowledged": true,
                 "shards_acknowledged": true,
                 "index": "posts"
             }
 *EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE ENABLE CLOUD FUNCTION eeEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
 * Part 16: Enabling Firebase Cloud Functions
 * Using Firebase Cloud functions to push data to Elastic search index
 * https://www.youtube.com/watch?v=MgBL-oqq4bo&index=16&list=PLgCYzUzKIBE-G0tuxjKGkl_keIW2FFwKX
 *
 * Info: Firebase getting started at:
 * https://firebase.google.com/docs/functions/get-started#build_the_sample_in_your_firebase_project
 *
 * 1. download nodejs with npm at //https://nodejs.org/
 * 2. nav to wherever this app is stored on the computer
 *      open the command window there, inside the dir, in a blank space
 *      ctrl + shft > Right-click > open command window
 *
 *  3. Copy from fb : npm install -g firebase-tools
 *      and paste in the command window
 *      couldn't paste so I typed it in
 *
 *  4. Will take a while but when this is done, log into firebase by typing in:
 *          firebase login
 *          follow the dialog to complete request
 *
 *  5. Configure the project to allow fb func into it, add to the command:
 *          firebase init functions
 *
 *  6. Choose the project to init, use the arrow key to point the correct project
 *      press the up down arrow to the correct project and push enter
 *
 *  7. Do you want to install dependencies? Y and enter. It will take some time here to complete
 *      task so we just wait for things to complete
 *
 *  8. see that  new folder "functions" has been created with 2 firebase files (I saw one)
 *
 *  9. Open the functions folder > open index.js with Notepad.
 *      This is the index that the app will be run by and coding will be by js,
 *      that was why we installed node.js system
 *
 *  10. we need to install another dependency to help us request http to the server
 *         See fb doc: Access environment configuration in a function
 *         const function = require('firebase-functions');
 *         const request = require('request-promise);
 *    Lets install that dependency
 *    type in:
 *      cd functions enter
 *      > npm install --save request request-promise enter
 *
 * 11. Specify the configuration for our elastic search server so that our fb server can
 *      communicate with our elastic search server
 *
 *      firebase functions:config:set someservice.key="THE API KEY" someservice.id="THE CLIENT ID"
 *      change to
 *      firebase functions:config:set elasticsearch.username="user" elasticsearch.password="enter pw" elasticsearch.url="paste url"
 *      my elasticsearch url = http://35.192.62.195//elasticsearch/
 *
 *  Part 17 Elastic Search Firebase Cloud Function - to do
 *      https://www.youtube.com/watch?v=lUYXAyQm4e8&index=17&list=PLgCYzUzKIBE-G0tuxjKGkl_keIW2FFwKX
 * 1. In the project functions file, open index.js with notepad or notepad++
 *      delete old content, leave only the const functions = .... line
 *  2. add new code
 *      const request = require('request.promise')
 *      add other codes to the nodejs
 *      save
 *  3. Go to the command console to deploy (NO DON'T! MUST GO TO FIREBASE > FUNCTION)
 *      > click thru the deployment requirements before you deploy for the first time.
 *      I was following the video and there was no mention of this. I kept having error no matter
 *      how much I followed instructions from online sources until I accidentally came to functions,
 *      was trying to see what was there after seeing that the video deployed to functions
 *      and clicked thru,
 *      Go back to the command shell then deploy
 *
 *          firebase deploy --only functions
 *          enter
 *          This will deploy any functions found in the folder
 *
 *    I have saved the code in sublime3
 *    C:\Program Files\Sublime Text 3\firebase_deployment_index
 *
 *    4. Now let's test by creating a new post
 *          open up the app > create a new post > image > post
 *
 *   5. Check for the new post on Firebase > Function under LOGS
 *          Yeah... it works
 *
 *   6. we can test it by using Postman and searching index
 *      - open Postman
 *      - copy the url then open new tab by clicking the + next to the url on top of page
 *      - next to GET: paste the url : http://35.192.62.195//elasticsearch/posts
 *      - Then edit to: http://35.192.62.195//elasticsearch/posts/_search?q=*
 *              this will search for anything/everything in the index
 *      - To get the header, copy the authorization from the originat/1st tab
 *              come back to the new tab,
 *              Content-Type: application/json
 *              Authorization: paste in the auth
 *      - Send
 *          this sends our request to the server, see that the post we just made is returned
 *
 * Part 18: Querying ElasticSearch Server
 *  How to make query to server
 *  1. Set the default url to AND
 *      http://35.192.62.195//elasticsearch/posts/_search?default_operator=AND&q=awsome+city:abbotsford
 *   Otherwise it will default to 'OR'
 *   We want to search for keywords and we want to search for other things
 *          so "&q=awsome" will look for the parameter "awsome"
 *              "+city" will further refine our search to look for "awsome and city"
 *              "+state_province:Edmonton" will look for: awsome cityabbotsford in state province Edmonton
 *    If we do this search, we will get nothing since we don't have this keywords in our server
 *    But if we change to:
 *          http://35.192.62.195//elasticsearch/posts/_search?default_operator=AND&q=*+city:Akishima
 *          This searches and returns everything with the city Akishima
 *          we get a result since our last post has city Akishima in it
 *
 *  2. Add retrofit
 *      Go to square.github.io/retrofit
 *      copy the dependency under GRADLe and paste in app gradle
 *      compile 'com.squareup.retrofit2:retrofit:2.3.0'
 *
 *      then copy the Gson library form there too and paste in gradle
 *      - make sure to match the retrofit version to gson
 *      compile 'com.squareup.retrofit2:converter-gson:2.3.0'
 *
 *      sync
 *  3. When we're passing data in we need to specify what the data will look like
 *      In retrofit, we build object classes like in firebase, then retrieve those obj classes
 *      in the form of json data or convert it to json data, or covert json data into obj classes
 *
 *      We are going to look at our data and do some reverse engineering
 *      Looking at the Postman data returned, we see that:
 *          - we get and object "hits {}"
 *          - gives us some totals, max_score
 *          - then gives us another list of "hits[]"
 *          - the list of hits[] gives us the "posts" object
 *          The "posts" object is what we are interested in so we're going
 *          to build object classes that represent what this data looks like:
 *          ###- build a "hits{}" object with a "hits[]" list inside it and
 *          inside the "hits[]" list we put the "posts{}" object #####
 *
 * 4. create 3 new classes: HitsList, HitsObject, PostSource in the models directory
 *  The PostSource is what is "_source" in our Postman retrieved data
 *
 *  Part 19: Retrofit Setup in Android
 *  https://www.youtube.com/watch?v=YgKcVBbvy2U
 *  Set up from the innermost object to the outermost, that is from the _source obj to the hits obj
 *  1. Start from the PostSource class
 *      add ignoreExtraProperty at the top of the class so that retrofit knows to ignore the
 *      extra props. otherwise it doesnt know that we want only the _sorce items from the hits() list
 *
 * 2. define the Post post
 *      but on top of that add
 *      @Expose and
 *      @SerializeName (this references the _source in our Postman)
 *
 * 3. Add the getter and setter methods
 * 4. Next is the HitsList class, then the HitsObject, do the same as in 1 to 3 above
 *
 * 5. Now that get got everything we need to look for (hitlist, postsource, hitsobj)
 *      Create an interface in utils for retrofit,
 *      right-click util > new class > change class to interface >
 *      name it ElasticSearchAPI > ok
 *
 * 6. Next we go to the SearchFragment to construct our query
 *
 * Part 20: Elastic Search Query Prep - to do
 *https://www.youtube.com/watch?v=_NRF8RNl3xs
 *
 * 1. the first thing we need to do to make request to the elastic search server is to:
 *      - retrieve the filters ie city, country, state_province etc
 *      - dont store pw in elastic seearch server, store it on fb server then query it for pw
 *          everytime we want to use it so:
 *              - in Firebase > database > create a node "elastic search"
 *              - inside that node click + to create one field "password"
 *              - stick the password in the "value"
 *              Now to get the password, go to
 *              https://console.cloud.google.com/home/dashboard?project=data2firebase >
 *              click on the app we are dealing with, >
 *              scroll to "compute engine" , > click on the elasticsearch instance > copy the password. you can see it:
 *                  "bitnami-based-password": -------
 *              copy this and paste in the fb field as a "string"
 * 2. Create a method to get the pw in SearchFragment
 * 3. get the Filters, create the getFilter in the Searchfragment
 *
 * Part 21: Build Retrofit Query
 * https://www.youtube.com/watch?v=-kDHPW7woYU
 *
 * 1. create ArrayList and init it in mSearchTesxt
 * 2. Build the elasticsearch api
 *  create a global var to take the Base UrL
 *  get the headers
 *  type of auth: base
 *  create the search string
 *
 *  Part 22: RecyclerView grid - to do (pull the data into a grid and display to user)
 *  https://www.youtube.com/watch?v=Z_DooPH6Aio&t=29s
 *
 *  1. Create the adapter in SearchFragment
 *      Create the PostListAdapter in util dir
 *
 *  2. create a method for setting up the adapter in the SearchFragment
 *
 * Part 23 - Inflating a framelayout
 * https://www.youtube.com/watch?v=_iYbbyDyE20&t=7s
 * When user clicks a pic, it takes the user to a new page where details for the image is shown.
 * 1. Create a new layout to handle the fragment_view_post.xml (if you don't already have it)
 *      Then copy and paste content from github
 *      also get the drawable teal_onClick_red from there. this changes color if the button is clicked
 * 2. Create the class "ViewPostFragment" (if its not already created) to handle the layout we just created
 *
 * Part 24: Viewing Posts for sale
 * https://www.youtube.com/watch?v=Gx9eWIGi7Pw
 * see ViewPostFragment
 *
 * part 25: Saving Posts to WatchList
 * https://www.youtube.com/watch?v=tuD1Oaxt0a8
 *  Here we need to create a new node in the database whic we will call WatchList
 *  Go to ViewPostFragment, create 2 new methods. one to add post to watchlist
 *  another to remove post from watchlist
 *
 * Part 26: Deleting Posts from watchlist Todo
 * https://www.youtube.com/watch?v=uMrcfHasQw0&t=14s
 * 1. Create a layout for the fragment_watch_list and add recyclerView and framelayout into it
 * 2. Go and code the WatchListFragment, it is similar to what we did in SearchFragment
 *      start by copying the 2 int at the top to it
 *
 *##### Run WORKS! ######################
 *
 *
 * EEEEEEEEEH
 *      Days Later, app started crashing but adding an if statement around the error fixed it.
 *      Don't know how, but it works. I merely clicked on a gray link in the logcat, after the
 *      error, that opened up a Fragment manager where I saw a lookalike with an if, then tried it
 *      See mReference = ..., and onDestroy() at WatchListFragment.java
 * EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEH
 *
 */
public class SearchActivity extends AppCompatActivity {//implements View.OnClickListener{

    //Create a String TAG
    private final static String TAG = "SearchActivity";
    private static final int REQUEST_CODE = 1;

    // widgets
    private TabLayout mTabLayout;
    public ViewPager mViewPager;

    // vars
    public SectionsPagerAdapter mPagerAdapter;


    //private TextView txtRegister;
    //private Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // get the views for the Tab and ViewPager
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.viewPager_container);

        // I was getting error to init ImageLoader, found this on stackoverflow, works magic
        //https://stackoverflow.com/questions/17737858/android-imageloader-must-be-init-with-configuration-before-using-in-uil
        // Initialize ImageLoader
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(SearchActivity.this));

        //init button and text views
        //loginBtn = (Button) findViewById(R.id.btnLogin);
        //txtRegister = (TextView) findViewById(R.id.link_register);

        //loginBtn.setOnClickListener(this);
        //txtRegister.setOnClickListener(this);

        //setupViewPager(); // replace this with verifyPermissions()
        verifyPermissions();
    }

    // setUp the viewPagers
    private void setupViewPager() {
        // get the supportFragmentManager
        mPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // add the tab fragments
        // redlining: spent hours to get this fixed. Turned out that when using
        // when using android.support.v4.view.ViewPager,
        // must use: getSupportFragmentManager NOt getFragmentmanager and
        // all fragments must extent android.support.v4.app.Fragment, NOT Fragment
        mPagerAdapter.addFragment(new SearchFragment());
        mPagerAdapter.addFragment(new WatchListFragment());
        mPagerAdapter.addFragment(new PostFragment());
        mPagerAdapter.addFragment(new AccountFragment());

        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        // now call this Viewpager in the onCreate()

        //set text to our tabs so users know which to click
        mTabLayout.getTabAt(0).setText(getString(R.string.fragment_search));
        mTabLayout.getTabAt(1).setText(getString(R.string.fragment_watch_list));
        mTabLayout.getTabAt(2).setText(getString(R.string.fragment_post));
        mTabLayout.getTabAt(3).setText(getString(R.string.fragment_account));

        //verifyPermissions();
    }

    // =========== ASKING FOR PERMISSIONS ===================
    // Ask for user permissions
    private void verifyPermissions() {
        Log.d(TAG, "verifyPermissions: ask user for permissions");

        String[] permissions = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        };

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[0]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[1]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[2]) == PackageManager.PERMISSION_GRANTED) {
            // if all permissions granted, setUpViewpager
            setupViewPager();
        }
        else { // if not, then verify the permissions
            // ask for the permissions
            ActivityCompat.requestPermissions(SearchActivity.this,
                    permissions,
                    REQUEST_CODE); // caching the results from onPermissionsResult()
        }

        }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
         verifyPermissions();
    }
    // =========== END ASKING FOR PERMISSIONS ===================
/*
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnLogin:
                Intent loginIntent = new Intent(this, LoginActivity.class);
                startActivity(loginIntent);
                break;

            case R.id.link_register:
                Intent registerIntent = new Intent(this, RegisterActivity.class);
                startActivity(registerIntent);
                break;
        }

    }*/
}
