package com.example.barry.classifiedapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.barry.classifiedapp.models.Post;
import com.example.barry.classifiedapp.util.PostListAdapter;
import com.example.barry.classifiedapp.util.RecyclerViewMargin;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by barry on 11/5/2017.
 */

// when using android.support.v4.view.ViewPager,
// all fragments must extent android.support.v4.app.Fragment, NOT Fragment
public class WatchListFragment extends android.support.v4.app.Fragment {

    private static final String TAG = "WatchListFragment";
    private static final int NUM_GRID_COLUMNS = 3;
    private static final int GRID_ITEM_MARGIN = 5;

    //widgets
    private RecyclerView mRecyclerView;
    private FrameLayout mFrameLayout;

    //vars
    private PostListAdapter mAdapter;
    private ArrayList<Post> mPosts;
    private ArrayList<String> mPostsIds;
    private DatabaseReference mReference;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_watch_list, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.watchListRecyclerView);
        mFrameLayout = (FrameLayout) view.findViewById(R.id.watch_list_container);

        init();

        return view;
    }

    private void init(){
        Log.d(TAG, "init: initializing.");
        mPosts = new ArrayList<>();
        setupPostsList();

        //reference for listening when items are added or removed from the watch list
        mReference = FirebaseDatabase.getInstance().getReference()
                .child(getString(R.string.node_watch_list))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        //set the listener to the reference
        mReference.addValueEventListener(mListener);

    }

    private void getWatchListIds(){
        Log.d(TAG, "getWatchListIds: getting users watch list.");
        if(mPosts != null){
            mPosts.clear();
        }
        if(mPostsIds != null){
            mPostsIds.clear();
        }

        mPostsIds = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference.child(getString(R.string.node_watch_list))
                .orderByKey()
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildren().iterator().hasNext()){
                    DataSnapshot singleSnapshot = dataSnapshot.getChildren().iterator().next();
                    for(DataSnapshot snapshot: singleSnapshot.getChildren()){
                        String id = snapshot.child(getString(R.string.field_post_id)).getValue().toString();
                        Log.d(TAG, "onDataChange: found a post id: " + id);
                        mPostsIds.add(id);
                    }
                    getPosts();
                }else{
                    getPosts();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getPosts(){
        if(mPostsIds.size() > 0){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

            for(int i  = 0; i < mPostsIds.size(); i++){
                Log.d(TAG, "getPosts: getting post information for: " + mPostsIds.get(i));

                Query query = reference.child(getString(R.string.node_posts))
                        .orderByKey()
                        .equalTo(mPostsIds.get(i));

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        DataSnapshot singleSnapshot = dataSnapshot.getChildren().iterator().next();
                        Post post = singleSnapshot.getValue(Post.class);
                        Log.d(TAG, "onDataChange: found a post: " + post.getTitle());
                        mPosts.add(post);
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }else{
            mAdapter.notifyDataSetChanged(); //still need to notify the adapter if the list is empty
        }
    }

    public void viewPost(String postId){
        ViewPostFragment fragment = new ViewPostFragment();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

        Bundle args = new Bundle();
        args.putString(getString(R.string.arg_post_id), postId);
        fragment.setArguments(args);

        transaction.replace(R.id.watch_list_container, fragment, getString(R.string.fragment_view_post));
        transaction.addToBackStack(getString(R.string.fragment_view_post));
        transaction.commit();

        mFrameLayout.setVisibility(View.VISIBLE);
    }

    private void setupPostsList(){
        RecyclerViewMargin itemDecorator = new RecyclerViewMargin(GRID_ITEM_MARGIN, NUM_GRID_COLUMNS);
        mRecyclerView.addItemDecoration(itemDecorator);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), NUM_GRID_COLUMNS);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mAdapter = new PostListAdapter(getActivity(), mPosts);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mReference.removeEventListener(mListener);
    }

    ValueEventListener mListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Log.d(TAG, "onDataChange: a change was made to this users watch lits node.");
            getWatchListIds();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    /*// Create a log  (String TAG)
    // logt > enter created the following String
    private static final String TAG = "WatchListFragment";

    private static final int NUM_GRID_COLUMNS = 3; // no of grid columns
    private static final int GRID_ITEM_MARGIN = 5; // add amrgin to grid

    // widgets
    // the 2 views in the fragment_watch_lis
    private RecyclerView mRecyclerView;
    private FrameLayout mFrameLayout;

    // vars
    private PostListAdapter mAdapter;
    private ArrayList<Post> mPosts; // obj for list of posts
    private ArrayList<String> mPostIds; // for post_ids
    private DatabaseReference mReference; // global for all changes to the db

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_watch_list, container, false);

        // part 26
        // assign the widgets
        mRecyclerView = (RecyclerView) view.findViewById(R.id.watchListRecyclerView);
        mFrameLayout = (FrameLayout) view.findViewById(R.id.watch_list_container);
        
        // call the init
        init();  // we haven't made this yet, so make one
        return view;
    }

    private void init() {
        Log.d(TAG, "init: initializing.");
        mPosts = new ArrayList<>();

        // call a method for setting up the post list
        setupPostsList();

        // reference for listening when items are added or removed from the watch list
        // EEEEEEEEEH app started crashing but adding an if statement around the error fixed it.
        // Don't know how, but it works. EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEH
        if (mReference != null) {
            mReference = FirebaseDatabase.getInstance().getReference()
                    .child(getString(R.string.node_watch_list))   // the node we want to watch
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            // set the listener to the reference
            mReference.addValueEventListener(mListener); // make the mListener at the bottom
        }

    }

    // create a method for retrieving the post Ids
    private void getWatchListIds() {
        Log.d(TAG, "getWatchListIds: getting users watch lists.");

        // 1st clear everything b4 proceeding
        if (mPosts != null) {
            mPosts.clear(); // clear if it has a value
        }
        if (mPostIds != null) {
            mPostIds.clear(); // also clear the post ids
        }

        mPostIds = new ArrayList<>(); // instantiate the mPostIds

        // ref the db
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference.child(getString(R.string.node_watch_list))
                .orderByKey()
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());

        // add listener for single event
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) { // get user snapshot and iterate thru the post
                // check if data exist
                // surround with "if" so data doesn't crash when last item is removed
                if (dataSnapshot.getChildren().iterator().hasNext()) {
                    DataSnapshot singleSnapshot = dataSnapshot.getChildren().iterator().next();
                    for (DataSnapshot snapshot: singleSnapshot.getChildren()) {
                        String id = snapshot.child(getString(R.string.field_post_id)).getValue().toString();
                        Log.d(TAG, "onDataChange: found a post id: " + id);
                        mPostIds.add(id);
                    }
                    // call the method for getting posts
                    getPosts(); // hasn't been created so create
                }
                else { // add this else so the last item is deleted
                    getPosts();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getPosts() {
        if (mPostIds.size() > 0) {
            // if > 0, then we have data so get the db ref
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

            // Then iterate thru the post ids
            for (int i = 0; i < mPostIds.size(); i++) {
                Log.d(TAG, "getPosts: getting post info for: " + mPostIds.get(i));

                Query query = reference.child(getString(R.string.node_posts))
                        .orderByKey()
                        .equalTo(mPostIds.get(i));

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        DataSnapshot singleSnapshot = dataSnapshot.getChildren().iterator().next(); // get the snapshot
                        Post post = singleSnapshot.getValue(Post.class); // assign it
                        Log.d(TAG, "onDataChange: found a post: " + post.getTitle());  // log the title
                        mPosts.add(post);
                        mAdapter.notifyDataSetChanged(); // tell adapter to update data change
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

        }
        else {
            // even when no change or empty we still need to notify the Adapter
            mAdapter.notifyDataSetChanged();
        }
    }

    // viewpost
    // copied from FragmentSearch, change "container" to "watch_list_container" in transaction
    // end of part 26 this was not called, so we had to code "WatchListFragment watchListFragment" in PostListAdapter
    public void viewPost(String postId) {
        ViewPostFragment fragment = new ViewPostFragment();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

        Bundle args = new Bundle();
        args.putString(getString(R.string.arg_post_id), postId);
        fragment.setArguments(args);

        transaction.replace(R.id.watch_list_container, fragment, getString(R.string.fragment_view_post));
        transaction.addToBackStack(getString(R.string.fragment_view_post));
        transaction.commit();

        // our frame is currently set to invisible, so make it visible
        mFrameLayout.setVisibility(View.VISIBLE);
    }

    // setupPostList, identical to the one we did in fragmentSearch
    // also get the ViewPost() from FragmentSearch, paste it above this and edit accordingly
    private void setupPostsList(){
        //add grid-column to image display on screen
        RecyclerViewMargin itemDecorator = new RecyclerViewMargin(GRID_ITEM_MARGIN, NUM_GRID_COLUMNS);
        mRecyclerView.addItemDecoration(itemDecorator);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), NUM_GRID_COLUMNS);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mAdapter = new PostListAdapter(getActivity(), mPosts);
        mRecyclerView.setAdapter(mAdapter);
    }

    // override onDestroy method
    // use this to remove the mListener otherwise it will continue listening even when the
    // app is closed and we don't want that to happen
    @Override
    public void onDestroy() {
        super.onDestroy();
        // EEEEEEEEEH app was crashing but adding an if statement around the error fixed it.
        // Don't know how, but it works. EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEH
        if (mReference != null) {
        mReference.removeEventListener(mListener);
        }
    }

    // make the mListener
    // this a little different from the way we have been making our listeners
    // we make this listener this way so that we will be able to distroy it when we are not using it,
    // otherwise it will keep listening and tire our resources
    ValueEventListener mListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Log.d(TAG, "onDataChange: a change was made to this users watch list ");
            getWatchListIds(); // this will initiate the process
            // now create an onDestry method as above and remove this mListener
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };*/

}
