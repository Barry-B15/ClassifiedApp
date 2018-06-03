package com.example.barry.classifiedapp.util;

/**
 * Created by barry on 12/17/2017.
 */

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.barry.classifiedapp.R;
import com.example.barry.classifiedapp.SearchActivity;
import com.example.barry.classifiedapp.SearchFragment;
import com.example.barry.classifiedapp.WatchListFragment;
import com.example.barry.classifiedapp.models.Post;

import java.util.ArrayList;

/**
 *  replace imageView with square image view to square the images in the ViewHolder
    and do same in the layout_view_post xml file

 Create another class to help get spacing between items in the list
 create that in the util dir, call it "RecyclerViewMargin"
 */

// have the class extend RecyclerView

public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.ViewHolder>{

    private static final String TAG = "PostListAdapter";
    private static final int NUM_GRID_COLUMNS = 3;

    private ArrayList<Post> mPosts;
    private Context mContext;

    public class ViewHolder extends RecyclerView.ViewHolder{

        SquareImageView mPostImage;

        public ViewHolder(View itemView) {
            super(itemView);
            mPostImage = (SquareImageView) itemView.findViewById(R.id.post_image);

            int gridWidth = mContext.getResources().getDisplayMetrics().widthPixels;
            int imageWidth = gridWidth/NUM_GRID_COLUMNS;
            mPostImage.setMaxHeight(imageWidth);
            mPostImage.setMaxWidth(imageWidth);
        }
    }

    public PostListAdapter(Context context, ArrayList<Post> posts) {
        mPosts = posts;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_view_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UniversalImageLoader.setImage(mPosts.get(position).getImage(), holder.mPostImage);

        final int pos = position;
        holder.mPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: selected a post");
                //TODO

                // https://www.youtube.com/watch?v=_iYbbyDyE20&t=7s
                // view the post in more detail
                // code to send data to the ViewPostFragment
                // whether we're in searchFragment or watchListFragment, it will figure it out and send
                Fragment fragment = (Fragment)((SearchActivity)mContext).getSupportFragmentManager()
                        .findFragmentByTag("android:switcher:" + R.id.viewPager_container + ":" +
                                ((SearchActivity)mContext).mViewPager.getCurrentItem());

                // check that the fragment isn't empty
                if (fragment != null) { // if in SearchFragment aka #0
                    if (fragment.getTag().equals("android:switcher:"
                            + R.id.viewPager_container + ":0")) {
                        Log.d(TAG, "onClick: switching to: " + mContext.getString(R.string.fragment_view_post));

                        // ref the fragment
                        // part 23 do this and next after the viewPost() in SearchFragment()
                        SearchFragment searchFragment = (SearchFragment) ((SearchActivity)mContext).getSupportFragmentManager()
                                .findFragmentByTag("android:switcher:" + R.id.viewPager_container + ":" +
                                        ((SearchActivity)mContext).mViewPager.getCurrentItem());

                        // ref the method
                        // get the post id to inflate the fragment on the old fragment
                        searchFragment.viewPost(mPosts.get(pos).getPost_id());

                    } // or in WatchListFragment aka #1
                    else if (fragment.getTag().equals("android:switcher:"
                            + R.id.viewPager_container + ":1")) {
                        Log.d(TAG, "onClick: switching to: " + mContext.getString(R.string.fragment_watch_list));
                        // go to search fragment to inflate this data on top of the first data

                        // part 26
                        WatchListFragment watchListFragment = (WatchListFragment) ((SearchActivity)mContext).getSupportFragmentManager()
                                .findFragmentByTag("android:switcher:" + R.id.viewPager_container + ":" +
                                        ((SearchActivity)mContext).mViewPager.getCurrentItem());

                        // ref the method
                        // get the post id to inflate the fragment on the old fragment
                        watchListFragment.viewPost(mPosts.get(pos).getPost_id());

                    }

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }


}

/*public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.ViewHolder> {

    private static final String TAG = "PostListAdapter";
    private static final int NUM_GRID_COLUMNS = 3; // no of grid columns

    private ArrayList<Post> mPosts;
    private Context mContext;

    // create the view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        //ImageView mPostImage; // replace this with the below one
        SquareImageView mPostImage; // using the square image view to square the images

        public ViewHolder(View itemView) {
            super(itemView);
            mPostImage = (SquareImageView) itemView.findViewById(R.id.post_image);
            // replace Imageview here with SquareImageView, do same in the layout_view_post xml file

            //manually set the width, that way the width and height will be the same
            // if this is not added, it slows down the perf
            int gridWidth = mContext.getResources().getDisplayMetrics().widthPixels;
            int imageWidth = gridWidth/NUM_GRID_COLUMNS;
            mPostImage.setMaxHeight(imageWidth);
            mPostImage.setMaxWidth(imageWidth);
        }
    }

    // insert the default constructor
    //  re-arrange to have the Context first in the Adapter

    public PostListAdapter(Context context, ArrayList<Post> posts) {
        mPosts = posts;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflate the viewHolder            // create the layout_view_post.xml
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_view_post, parent, false);
        return new ViewHolder(view); // return the new viewHolder and pass that view
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // set the imageView widget
        UniversalImageLoader.setImage(mPosts.get(position).getImage(), holder.mPostImage); // attach the pic

        // set onClick listener
        holder.mPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: selected a post");
                // TO DO
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPosts.size(); // this returns all items in the post
    }


}*/
