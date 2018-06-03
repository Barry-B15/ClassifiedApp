package com.example.barry.classifiedapp;

/**
 * Created by barry on 11/5/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.barry.classifiedapp.models.Post;
import com.example.barry.classifiedapp.util.UniversalImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * Get the post from SearchFragment to ViewPostFragment
 * 1st get the post id
 * We attach an onclick listener in PostListAdapter and send the id of the post into ViewPostFragment
 * We retrieve the data by overriding the onCreate method in ViewPostFragment()
 *
 * 2. Create a global var mPostId
 * Note: The onCreate method gets called b4 the onCreateView method in a fragment class
 *
 * 3. create the string arg_post_id in the string.xml
 *
 * Part 24
 * https://www.youtube.com/watch?v=Gx9eWIGi7Pw
 * 1. Create the widgets and init them
 * 2. Create the hideSoftKeyboard method to close the keyboard when we arrive on the fragment
 * 3. we need to retrieve the data using the post-id we get from the onCreate method
 *      so create an init() method
 *      call the init() in the onCreateView()
 * 4. create a getPostInfo() method
 *      call this in the init() method
 *
 * 5. Add the contact seller intent
 *
 * 6. close the post Post
 *
 * 7. Fix the graphics for the save post
 * First create the Bitmapoutline at the bottom of the codes
 * the use it in the save post
 *
 * Part 25: saving Posts to WatchList
 * https://www.youtube.com/watch?v=tuD1Oaxt0a8
 * 1. add a new node "watch_list" to the string.xml
 *      while there, also create fields user_id and post_id
 * 2. create 2 new methods; one to add post to watchlist
 *      another to remove post from watchlist
 *
 * 3. In the init method, differentiate what happens when in search fragment
 *  from what happens when in watchList fragment
 */

// when using android.support.v4.view.ViewPager,
// all fragments must extent android.support.v4.app.Fragment, NOT Fragment
public class ViewPostFragment extends android.support.v4.app.Fragment {

    private static final String TAG = "ViewPostFragment";

    //widgets
    private TextView mContactSeller, mTitle, mDescription, mPrice, mLocation, mSavePost;
    private ImageView mClose, mWatchList, mPostImage;

    //vars
    private String mPostId;
    private Post mPost;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPostId = (String) getArguments().get(getString(R.string.arg_post_id));
        Log.d(TAG, "onCreate: got the post id: " + mPostId);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_post, container, false);
        mContactSeller = (TextView) view.findViewById(R.id.post_contact);
        mTitle = (TextView) view.findViewById(R.id.post_title);
        mDescription = (TextView) view.findViewById(R.id.post_description);
        mPrice = (TextView) view.findViewById(R.id.post_price);
        mLocation = (TextView) view.findViewById(R.id.post_location);
        mClose = (ImageView) view.findViewById(R.id.post_close);
        mWatchList = (ImageView) view.findViewById(R.id.add_watch_list);
        mPostImage = (ImageView) view.findViewById(R.id.post_image);
        mSavePost = (TextView) view.findViewById(R.id.save_post);

        init();

        hideSoftKeyboard();

        return view;
    }

    private void init(){
        getPostInfo();

        //view the post in more detail
        Fragment fragment = (Fragment)((SearchActivity)getActivity()).getSupportFragmentManager()
                .findFragmentByTag("android:switcher:" + R.id.viewPager_container + ":" +
                        ((SearchActivity)getActivity()).mViewPager.getCurrentItem());
        if(fragment != null){
            //SearchFragment (AKA #0)
            if(fragment.getTag().equals("android:switcher:" + R.id.viewPager_container + ":0")){
                Log.d(TAG, "onClick: switching to: " + getActivity().getString(R.string.fragment_view_post));

                mSavePost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addItemToWatchList();
                    }
                });

                mWatchList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addItemToWatchList();
                    }
                });

            }
            //WatchList Fragment (AKA #1)
            else if(fragment.getTag().equals("android:switcher:" + R.id.viewPager_container + ":1")){
                Log.d(TAG, "onClick: switching to: " + getActivity().getString(R.string.fragment_watch_list));

                mSavePost.setText("remove post");
                mSavePost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeItemFromWatchList();
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                });

                mWatchList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeItemFromWatchList();
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                });
            }
        }

        mContactSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("plain/text");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {mPost.getContact_email()});
                getActivity().startActivity(emailIntent);
            }
        });

        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing post.");
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        mSavePost.setShadowLayer(5, 0 , 0, Color.BLUE);
        mWatchList.setImageBitmap(createOutline(BitmapFactory.decodeResource(getResources(), R.drawable.ic_save_white)));
        mWatchList.setColorFilter(Color.BLUE);
        mClose.setImageBitmap(createOutline(BitmapFactory.decodeResource(getResources(), R.drawable.ic_x_white)));
        mClose.setColorFilter(Color.BLUE);
    }

    private void addItemToWatchList(){
        Log.d(TAG, "addItemToWatchList: adding item to watch list.");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.child(getString(R.string.node_watch_list))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(mPostId)
                .child(getString(R.string.field_post_id))
                .setValue(mPostId);

        Toast.makeText(getActivity(), "Added to watch list", Toast.LENGTH_SHORT).show();
    }

    private void removeItemFromWatchList(){
        Log.d(TAG, "removeItemFromWatchList: removing item from watch list.");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.child(getString(R.string.node_watch_list))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(mPostId)
                .removeValue();

        Toast.makeText(getActivity(), "Removed from watch list", Toast.LENGTH_SHORT).show();
    }

    private void getPostInfo(){
        Log.d(TAG, "getPostInfo: getting the post information.");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference.child(getString(R.string.node_posts))
                .orderByKey()
                .equalTo(mPostId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot singleSnapshot = dataSnapshot.getChildren().iterator().next();
                if(singleSnapshot != null){
                    mPost = singleSnapshot.getValue(Post.class);
                    Log.d(TAG, "onDataChange: found the post: " + mPost.getTitle());

                    mTitle.setText(mPost.getTitle());
                    mDescription.setText(mPost.getDescription());

                    String price = "FREE";
                    if(mPost.getPrice() != null){
                        price = "$" + mPost.getPrice();
                    }
                    mPrice.setText(price);
                    String location = mPost.getCity() + ", " + mPost.getState_province() + ", " +
                            mPost.getCountry();
                    mLocation.setText(location);
                    UniversalImageLoader.setImage(mPost.getImage(), mPostImage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void hideSoftKeyboard(){
        final Activity activity = getActivity();
        final InputMethodManager inputManager = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private Bitmap createOutline(Bitmap src){
        Paint p = new Paint();
        p.setMaskFilter(new BlurMaskFilter(2, BlurMaskFilter.Blur.OUTER));
        return src.extractAlpha(p, null);
    }
    /*// Create a log  (String TAG)
    // logt > enter created the following String
    private static final String TAG = "ViewPostFragment";

    // create the widgets
    private TextView mContactSeller, mTitle, mDescription, mPrice, mLocation, mSavePost;
    private ImageView mClose, mWatchList, mPostImage;

    // var
    private String mPostId;
    private Post mPost;

    // override the onCreate method to retrieve data sent from the Searchfragment

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPostId = (String) getArguments().get(getString(R.string.arg_post_id)); // retrieve data
        Log.d(TAG, "onCreate: get the post id: " + mPostId);
        // now go to PostListAdapter > onBindViewHolder() to write the required codes to send the post data
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_post, container, false);

        //assign widgets to the ids
        mContactSeller = (TextView) view.findViewById(R.id.post_contact);
        mTitle = (TextView) view.findViewById(R.id.post_title);
        mDescription = (TextView) view.findViewById(R.id.post_description);
        mPrice = (TextView) view.findViewById(R.id.post_price);
        mLocation = (TextView) view.findViewById(R.id.post_location);
        mClose = (ImageView) view.findViewById(R.id.post_close);
        mWatchList = (ImageView) view.findViewById(R.id.add_watch_list);
        mPostImage = (ImageView) view.findViewById(R.id.post_image);
        mSavePost = (TextView) view.findViewById(R.id.save_post);

        init();
        hideSoftKeyboard();
        return view;
    }
    
    private void init() {
        getPostInfo(); // part 24

        //part 25
        // view the post in more detail
        // code to send data to the ViewPostFragment
        // whether we're in searchFragment or watchListFragment, it will figure it out and send
        Fragment fragment = (Fragment)((SearchActivity)getActivity()).getSupportFragmentManager()
                .findFragmentByTag("android:switcher:" + R.id.viewPager_container + ":" +
                        ((SearchActivity)getActivity()).mViewPager.getCurrentItem());

        // check that the fragment isn't empty
        if (fragment != null) {
            // if in SearchFragment aka #0
            if (fragment.getTag().equals("android:switcher:"
                    + R.id.viewPager_container + ":0")) {
                Log.d(TAG, "onClick: switching to: " + getActivity().getString(R.string.fragment_view_post));

                // save post
                mSavePost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addItemToWatchList();
                    }
                });

                mWatchList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addItemToWatchList();
                    }
                });
            }
            // or in WatchListFragment aka #1
            // remove post
            else if (fragment.getTag().equals("android:switcher:"
                    + R.id.viewPager_container + ":1")) {
                Log.d(TAG, "onClick: switching to: " + getActivity().getString(R.string.fragment_watch_list));
                // go to search fragment to inflate this data on top of the first data

                // remove the post
                mSavePost.setText("remove post");
                mSavePost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeItemFromWatchList(); // remove the item
                        getActivity().getSupportFragmentManager().popBackStack(); // close the post
                    }
                });

                // also remove from the watchList
                mWatchList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeItemFromWatchList(); // remove the item
                        getActivity().getSupportFragmentManager().popBackStack(); // close the post
                    }
                });
            }

        }

        // part24 set up the contact seller
        mContactSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("plain/text");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{mPost.getContact_email()});
                getActivity().startActivity(emailIntent);
            }
        });

        // handle close post
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing post.");
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        // fix the graphic for save post
        // download the drawables icons > drawable > new image Asset > select
        mSavePost.setShadowLayer(5, 0, 0, Color.BLUE);
        mWatchList.setImageBitmap(createOutline(BitmapFactory.decodeResource(getResources(), R.drawable.ic_save_white)));
        mWatchList.setColorFilter(Color.BLUE);
        mClose.setImageBitmap(createOutline(BitmapFactory.decodeResource(getResources(), R.drawable.ic_x_white)));
        mClose.setColorFilter(Color.BLUE);

    }

    // To add Posts to watch list
    private void addItemToWatchList() {
        Log.d(TAG, "addItemToWatchList: adding item to watch list");

        // ref the database
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.child(getString(R.string.node_watch_list)) // watch_list created in string.xml
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(mPostId)
                .child(getString(R.string.field_post_id))
                .setValue(mPostId);

        Toast.makeText(getActivity(), "Added to watch list", Toast.LENGTH_SHORT).show();
    }

    // To remove Posts from watch list
    private void removeItemFromWatchList() {
        Log.d(TAG, "removeItemFromWatchList: removing item from watch list");

        // ref the database
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.child(getString(R.string.node_watch_list))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(mPostId)
                .removeValue();

        Toast.makeText(getActivity(), "Removed from watch list", Toast.LENGTH_SHORT).show();
    }


    // get post info
    private void getPostInfo() {
        Log.d(TAG, "getPostInfo: getting the post info.");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference.child(getString(R.string.node_posts))
                .orderByKey()
                .equalTo(mPostId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // we will be selecting single values each time so
                DataSnapshot singleSnapshot = dataSnapshot.getChildren().iterator().next();
                if (singleSnapshot != null) {
                    mPost = singleSnapshot.getValue(Post.class);
                    Log.d(TAG, "onDataChange: found the post: " + mPost.getTitle()); // .get here for whatever we want to get

                    // assign the fields
                    mTitle.setText(mPost.getTitle());
                    mDescription.setText(mPost.getDescription());

                    String price = "Free";
                    if (mPost.getPrice() != null) {
                        price = "$" + mPost.getPrice();
                    }
                    mPrice.setText(price);

                    String location = mPost.getCity() + ", " + mPost.getState_province() + ", " +
                            mPost.getCountry();
                    mLocation.setText(location);

                    UniversalImageLoader.setImage(mPost.getImage(), mPostImage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // close the keyboard when we navigate to this fragment
    private void hideSoftKeyboard() {
        final Activity activity = getActivity();
        final InputMethodManager inputManager = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    // create outline for the Bitmap
    private Bitmap createOutline(Bitmap src) {
        Paint p = new Paint();
        p.setMaskFilter(new BlurMaskFilter(2, BlurMaskFilter.Blur.OUTER)); // blur the outer part, radius 2
        return src.extractAlpha(p, null);
    }

*/
}
