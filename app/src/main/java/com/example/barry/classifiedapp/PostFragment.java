package com.example.barry.classifiedapp;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.barry.classifiedapp.models.Post;
import com.example.barry.classifiedapp.util.RotateBitmap;
import com.example.barry.classifiedapp.util.UniversalImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by barry on 11/5/2017.
 */

/**
 * https://www.youtube.com/watch?v=HhaSEVr6AOw&index=6&list=PLgCYzUzKIBE-G0tuxjKGkl_keIW2FFwKX
 * 1. create the layout
 * 2. in the java class, declare and init the views
 * 3. Create the dialog layout to select the photos
 * 4. Create the dialog java class
 *      Right click java > new > java class > SelectPhotoDialog
 *      have it extend DialogFragment
 * 5. alt+insert > Override > onCreateView
 *     define the view, inflate and return the view
 *
 *  Part 8: https://www.youtube.com/watch?v=miUPzUmaENo&t=132s
 *
 *  Sending data from SelectPhoto Dialog, images from the camera to Fragments
 *
 *  1. Create DialogFirst, create the dialog here in init, under mPostImage.setOnClickListener
 *  2. Show the dialog where to send data: setTargetFragment
 *  3. have the class implement OnPhotoSelectedListener and implement the methods
 *
 *  Part 9: https://www.youtube.com/watch?v=j4h_L2pzZs0&t=81s
 *          Compressing Images in Android
 *
 *   1. Add an onClick() in init() and test that all fields are filled
 *   2. Create 2 methods to handle newphotoUpload bitmap and uri
 *   3. create method to handle compression in background: BackgroundImageResize()
 *   4. In 3 above, override the onPreExecution(), doInBackground(), onPostExecution() methods
 *   5. create a static byte array to compress bitmap to bytes
 *
 *   Part 10: Uploading an image to Firebase
 *   https://www.youtube.com/watch?v=MXQmHSutu1o&index=10&list=PLgCYzUzKIBE-G0tuxjKGkl_keIW2FFwKX
 *   1. Let's printout the size b4 and after the compression to show the differences in size
 *      We can do that in the doInbackground()
 *   2. Create a method to execute uploadTask
 *          ## taskSnapshot.getDownloadUrl();  in onSuccess() and
 *          ## taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount(); in onProgress
 *          ## were redlining, I read on stackoverflow its a lint bug and recommend we suppress
 *          ## so I suppressed inspection for class and it all went away
 *
 *   Part 11: Post items for sale:
 *   https://www.youtube.com/watch?v=nbvlSJB-onA&list=PLgCYzUzKIBE-G0tuxjKGkl_keIW2FFwKX&index=11
 *
 *   To get the downloaded url and put it into the db we need to
 *
 *   1. Create a Post() object
 *      Models > New > Java class > name it Post
 *
 *   2. Insert the Post object we just created in executeUploadTask()
 *   3. Code the uploadNewPhoto Uri and uploadNewPhoto Bitmap to handle background compressing
 *   4. Call executeUploadTask in onPostExecute()
 *
 *   Part 12 Rotating Images (to do)
 *   Some devices do rotate uploaded images when they display. we don't want that to happen in our app
 *   https://www.youtube.com/watch?v=AxYiosk3GQQ&index=12&list=PLgCYzUzKIBE-G0tuxjKGkl_keIW2FFwKX&t=28s
 *
 *   1. Create a new class RotateBitmap in the utils directory
 *   2. Copy and paste code from Github
 *   https://github.com/mitchtabian/ForSale/blob/85628ecb4a1d50eab06d1152ed94f74b269403b1/app/src/main/java/codingwithmitch/com/forsale/util/RotateBitmap.java
 *   (coach feels its boring to write the codes here)
 *          paste the codes in the newly created RotateBitmap class
 *   3. Add the exif dependencies to library in gradle
 *          compile 'com.android.support:exifinterface:26.+'
 *
 *   4. Replace this line
 *        mBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), params[0]);
 *   in our doInBackground try/catch with
 *          RotateBitmap rotateBitmap = new RotateBitmap();
 *           mBitmap = rotateBitmap.HandleSamplingAndRotationBitmap(getActivity(), params[0]);
 *     which will both convert bitmap to uri and also rotate rotated images
 *  NEXT WE WANT TO CODE THE SEARCH, GO TO SEARCH
 */

// when using android.support.v4.view.ViewPager,
// all fragments must extent android.support.v4.app.Fragment, NOT Fragment
@SuppressWarnings("VisibleForTests")
public class PostFragment extends android.support.v4.app.Fragment
        implements SelectPhotoDialog.OnPhotoSelectedListener {

    private static final String TAG = "PostFragment";

    @Override
    public void getImagePath(Uri imagePath) {
        Log.d(TAG, "getImagePath: setting the image to imageview");
        UniversalImageLoader.setImage(imagePath.toString(), mPostImage);
        //assign to global variable
        mSelectedBitmap = null;
        mSelectedUri = imagePath;
    }

    @Override
    public void getImageBitmap(Bitmap bitmap) {
        Log.d(TAG, "getImageBitmap: setting the image to imageview");
        mPostImage.setImageBitmap(bitmap);
        //assign to a global variable
        mSelectedUri = null;
        mSelectedBitmap = bitmap;
    }

    //widgets
    private ImageView mPostImage;
    private EditText mTitle, mDescription, mPrice, mCountry, mStateProvince, mCity, mContactEmail;
    private Button mPost;
    private ProgressBar mProgressBar;

    //vars
    private Bitmap mSelectedBitmap;
    private Uri mSelectedUri;
    private byte[] mUploadBytes;
    private double mProgress = 0;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        mPostImage = view.findViewById(R.id.post_image);
        mTitle = view.findViewById(R.id.input_title);
        mDescription = view.findViewById(R.id.input_description);
        mPrice = view.findViewById(R.id.input_price);
        mCountry = view.findViewById(R.id.input_country);
        mStateProvince = view.findViewById(R.id.input_state_province);
        mCity = view.findViewById(R.id.input_city);
        mContactEmail = view.findViewById(R.id.input_email);
        mPost = view.findViewById(R.id.btn_post);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        init();

        return view;
    }

    private void init(){

        mPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: opening dialog to choose new photo");
                SelectPhotoDialog dialog = new SelectPhotoDialog();
                dialog.show(getFragmentManager(), getString(R.string.dialog_select_photo));
                dialog.setTargetFragment(PostFragment.this, 1);
            }
        });

        mPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to post...");
                if(!isEmpty(mTitle.getText().toString())
                        && !isEmpty(mDescription.getText().toString())
                        && !isEmpty(mPrice.getText().toString())
                        && !isEmpty(mCountry.getText().toString())
                        && !isEmpty(mStateProvince.getText().toString())
                        && !isEmpty(mCity.getText().toString())
                        && !isEmpty(mContactEmail.getText().toString())){

                    //we have a bitmap and no Uri
                    if(mSelectedBitmap != null && mSelectedUri == null){
                        uploadNewPhoto(mSelectedBitmap);
                    }
                    //we have no bitmap and a uri
                    else if(mSelectedBitmap == null && mSelectedUri != null){
                        uploadNewPhoto(mSelectedUri);
                    }
                }else{
                    Toast.makeText(getActivity(), "You must fill out all the fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadNewPhoto(Bitmap bitmap){
        Log.d(TAG, "uploadNewPhoto: uploading a new image bitmap to storage");
        BackgroundImageResize resize = new BackgroundImageResize(bitmap);
        Uri uri = null;
        resize.execute(uri);
    }

    private void uploadNewPhoto(Uri imagePath){
        Log.d(TAG, "uploadNewPhoto: uploading a new image uri to storage.");
        BackgroundImageResize resize = new BackgroundImageResize(null);
        resize.execute(imagePath);
    }

    public class BackgroundImageResize extends AsyncTask<Uri, Integer, byte[]>{

        Bitmap mBitmap;

        public BackgroundImageResize(Bitmap bitmap) {
            if(bitmap != null){
                this.mBitmap = bitmap;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getActivity(), "compressing image", Toast.LENGTH_SHORT).show();
            showProgressBar();
        }

        @Override
        protected byte[] doInBackground(Uri... params) {
            Log.d(TAG, "doInBackground: started.");

            if(mBitmap == null){
                try{
                    RotateBitmap rotateBitmap = new RotateBitmap();
                    mBitmap = rotateBitmap.HandleSamplingAndRotationBitmap(getActivity(), params[0]);
                }catch (IOException e){
                    Log.e(TAG, "doInBackground: IOException: " + e.getMessage());
                }
            }
            byte[] bytes = null;
            Log.d(TAG, "doInBackground: megabytes before compression: " + mBitmap.getByteCount() / 1000000 );
            bytes = getBytesFromBitmap(mBitmap, 100);
            Log.d(TAG, "doInBackground: megabytes before compression: " + bytes.length / 1000000 );
            return bytes;
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
            mUploadBytes = bytes;
            hideProgressBar();
            //execute the upload task
            executeUploadTask();
        }
    }

    private void executeUploadTask(){
        Toast.makeText(getActivity(), "uploading image", Toast.LENGTH_SHORT).show();

        final String postId = FirebaseDatabase.getInstance().getReference().push().getKey();

        final StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("posts/users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() +
                        "/" + postId + "/post_image");

        UploadTask uploadTask = storageReference.putBytes(mUploadBytes);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getActivity(), "Post Success", Toast.LENGTH_SHORT).show();

                //insert the download url into the firebase database
                Uri firebaseUri = taskSnapshot.getDownloadUrl();

                Log.d(TAG, "onSuccess: firebase download url: " + firebaseUri.toString());
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

                Post post = new Post();
                post.setImage(firebaseUri.toString());
                post.setCity(mCity.getText().toString());
                post.setContact_email(mContactEmail.getText().toString());
                post.setCountry(mCountry.getText().toString());
                post.setDescription(mDescription.getText().toString());
                post.setPost_id(postId);
                post.setPrice(mPrice.getText().toString());
                post.setState_province(mStateProvince.getText().toString());
                post.setTitle(mTitle.getText().toString());
                post.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());

                reference.child(getString(R.string.node_posts))
                        .child(postId)
                        .setValue(post);

                resetFields();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "could not upload photo", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double currentProgress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                if( currentProgress > (mProgress + 15)){
                    mProgress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    Log.d(TAG, "onProgress: upload is " + mProgress + "& done");
                    Toast.makeText(getActivity(), mProgress + "%", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static byte[] getBytesFromBitmap(Bitmap bitmap, int quality){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality,stream);
        return stream.toByteArray();
    }


    private void resetFields(){
        UniversalImageLoader.setImage("", mPostImage);
        mTitle.setText("");
        mDescription.setText("");
        mPrice.setText("");
        mCountry.setText("");
        mStateProvince.setText("");
        mCity.setText("");
        mContactEmail.setText("");
    }

    private void showProgressBar(){
        mProgressBar.setVisibility(View.VISIBLE);

    }

    private void hideProgressBar(){
        if(mProgressBar.getVisibility() == View.VISIBLE){
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Return true if the @param is null
     * @param string
     * @return
     */
    private boolean isEmpty(String string){
        return string.equals("");
    }

    /*// Create a log  (String TAG)
    // logt > enter created the following String
    private static final String TAG = "PostFragment";

    // Following are auto created from our onPhotoselected implimentations

    //=======For Sending data, images from SelectPhoto Dialog  to Fragments ===============
    @Override
    public void getImagePath(Uri imagePath) {

        Log.d(TAG, "getImagePath: setting the image to imageview");
        UniversalImageLoader.setImage(imagePath.toString(), mPostImage);
        //assign to a global variable
        mSelectedBitmap = null;
        mSelectedUri = imagePath;
    }

    @Override
    public void getImageBitmap(Bitmap bitmap) {

        Log.d(TAG, "getImageBitmap: setting the image to imageview");
        mPostImage.setImageBitmap(bitmap);
        //assign to a global variable
        mSelectedUri = null;
        mSelectedBitmap = bitmap;
    }
    //=======End Sending data, images from SelectPhoto Dialog  to Fragments ===============

    //widgets
    private ImageView mPostImage;
    private EditText mTitle, mDescription, mPrice, mCountry, mStateProvince, mCity, mContactEmail;
    private Button mPost;
    private ProgressBar mProgressBar;

    //vars for our Uri and Bitmap, assign them in the get() methods above
    private Bitmap mSelectedBitmap;
    private Uri mSelectedUri;
    private byte[] mUploadBytes; // this is what we will actually upload
    private double mProgress = 0; // control how frequent it prints progress


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        mPostImage = view.findViewById(R.id.post_image);
        mTitle = view.findViewById(R.id.input_title);
        mDescription = view.findViewById(R.id.input_description);
        mPrice = view.findViewById(R.id.input_price);
        mCountry = view.findViewById(R.id.input_country);
        mStateProvince = view.findViewById(R.id.input_state_province);
        mCity = view.findViewById(R.id.input_city);
        mContactEmail = view.findViewById(R.id.input_email);
        mPost = view.findViewById(R.id.btn_post);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        init();

        // App crashes whenever I selected a pic, an error that says I must initialize ImageLoader
        // which was not mentioned in the video. Searched and found on starkoverflow that says
        // I should add ff line to my onCreateView, works like magic

        //ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getActivity()));

        return view;
    }

    // when a user opens to add a photo, open the dialog_select_photo
    private void init(){

        mPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //======= Sending data, images from SelectPhoto Dialog  to Fragments ===============

                Log.d(TAG, "onClick: opening dialog to choose new photo");

                // create dialog here
                SelectPhotoDialog dialog = new SelectPhotoDialog();

                // dialog.show(getFragmentManager(), blah)  and
                // dialog.setTargetFragment(PostFragment.this, 1); redlining
                // hours later, after much research, came across this fix:
                // add getActivity() to have dialog.show(getActivity().getFragmentmanager())
                // Even getSupportFragmentManager didn't work
                // But (PostFragment.this) still redlining,
                // Fix (finally): adding android.support.v4.app.(to DialogFragment) in SelectPhotoDialog class
                dialog.show(getFragmentManager(), getString(R.string.dialog_select_photo));
                // create the string in string.xml

                //  took hours to fix
                // just adding android.support.v4.app.(to DialogFragment) in SelectPhotoDialog
                // fragment fixed it along with the show(getfragmentManager) above
                dialog.setTargetFragment(PostFragment.this, 1); // set targetFragment here
                //###Must get the Target Fragment in SelectPhoto onAttach()
                // so it knows where to send the data
                // Go to SelectPhotoDialog, onAttach, change getActivity() to getTargetFragment() ####

            }
        });
        //=======End Sending data, images from SelectPhoto Dialog  to Fragments ===============

        //======= Compressing images before uploading ===============
        // https://www.youtube.com/watch?v=j4h_L2pzZs0&t=81s

        // add a post listener
        mPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to post...");

                // check that the views are not empty
                if (!isEmpty(mTitle.getText().toString())
                        && !isEmpty(mDescription.getText().toString())
                        && !isEmpty(mPrice.getText().toString())
                        && !isEmpty(mCountry.getText().toString())
                        && !isEmpty(mStateProvince.getText().toString())
                        && !isEmpty(mCity.getText().toString())
                        && !isEmpty(mContactEmail.getText().toString())) {

                    // we have a bitmap and no Uri
                    if (mSelectedBitmap != null) {
                        uploadNewPhoto(mSelectedBitmap); // upload the bitmap
                    } // we have a Uri and no bitmap
                    else if (mSelectedBitmap == null && mSelectedUri != null) {
                        uploadNewPhoto(mSelectedUri);  // upload the uri
                    }
                }
                else {
                    Toast.makeText(getActivity(), "You must fill out all the fields", 
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void uploadNewPhoto(Bitmap bitmap) {
        Log.d(TAG, "uploadnewPhoto: uploading a new image bitmap to storage.");
        // do background resize
        BackgroundImageResize resize = new BackgroundImageResize(null);
        Uri uri = null;
        resize.execute(uri);
    }

    private void uploadNewPhoto(Uri imagePath) {
        Log.d(TAG, "uploadnewPhoto: uploading a new image uri to storage.");
        // do background resize
        BackgroundImageResize resize = new BackgroundImageResize(null);
        resize.execute(imagePath);
    }

    // We need to compress our image before upload as android doesn't do any compressing
    // when we upload images. It take a lot of space so it is necessary to compress.
    // And we need to do it in the background so it doesn't slow down the app
    public class BackgroundImageResize extends AsyncTask<Uri, Integer, byte[]> {
        Bitmap mBitmap;

        // add a constructor: right click > generate > constructor > ok
        public BackgroundImageResize(Bitmap bitmap) {
            // surround the bitmap with an if to handle when we have a bitmap
            if (bitmap != null) {
                this.mBitmap = bitmap;
            }
        }

        // override method to handle the asycTask
        // select doInBackground, onPreExecute and onPostExecute
        // Rearrange the order to onPreExecute, doInBackground, and onPostExecute
        @Override
        protected void onPreExecute() { // this runs first
            super.onPreExecute();
            Toast.makeText(getActivity(), "Compressing image", Toast.LENGTH_SHORT).show();
            showProgressBar();
        }

        @Override
        protected byte[] doInBackground(Uri... params) { // put action in background
            Log.d(TAG, "doInBackground: started.");

            // if we have a bitmap, convert it to bytes; if already in bytes, just compress, then upload

            if (mBitmap == null) {
                try { // this line of code can slow down the ui, so we should run it i the bakgrd
                    // in some devices samsung, it cause the image to rotate,
                    // we will need to handle that with some code b4 we upload
                    *//*
                    mBitmap = MediaStore.Images.Media.getBitmap(getActivity()
                            .getContentResolver(), params[0]); // params[0] cos we'd be passing only 1 parameter

                    // we used the above line to convert uri to bitmap but now want to both convert
                    // and rotate the image, so replace the above with
                    *//*
                    RotateBitmap rotateBitmap = new RotateBitmap();
                    mBitmap = rotateBitmap.HandleSamplingAndRotationBitmap(getActivity(), params[0]);
                }
                catch (IOException e) {
                    Log.e(TAG, "doInBackground: IOException: " + e.getMessage());
                }
            }
            // we need to convert the bitmap to byte array, then compress it b4 we upload,
            // so we need one more method. let's create getBytesFromBitmap() below our asynctask
            // and use it here
            byte[] bytes = null;
            // bytes b4 compression
            Log.d(TAG, "doInBackground: meganytes before compression: " + mBitmap.getByteCount() / 1000000); // 1m bytes = 1mg
            bytes = getBytesFromBitmap(mBitmap, 100); // 100% for now for testing
            // bytes after compression
            Log.d(TAG, "doInBackground: meganytes after compression: " + bytes.length / 1000000); // 1m bytes = 1mg
            return bytes;
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
            mUploadBytes = bytes; // assign the bytes to mUploadBytes to upload
            hideProgressBar();  // hide progress bar after upload completes

            // execute the upload task
            executeUploadTask();
        }
    }

    // method to handle upload which takes as global var mUploadBytes we def earlier
    private void executeUploadTask() {
        Toast.makeText(getActivity(), "uploading image", Toast.LENGTH_SHORT).show();

        // create an id for the image
        final String postId = FirebaseDatabase.getInstance().getReference().push().getKey();

        // create a fb reference to where the image is being stored
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("posts/users/" + FirebaseAuth.getInstance().getCurrentUser().getUid()
                + "/" + postId + "post_image"); // this is the dir path to the image

        // now upload the file by creating the upload task
        UploadTask uploadTask = storageReference.putBytes(mUploadBytes); //

        // add an onSuccess Listener, onFailure Listener and onProgress listener
        // to listen for success, failure and progress
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // inform the user of post success
                Toast.makeText(getActivity(), "Post Success", Toast.LENGTH_SHORT).show();

                // get the uri from the upload and store in the db. a pointer to where the image is stored
                // insert the download into the firebase db
                //Uri firebaseUri = taskSnapshot.getDownloadUrl();
                Uri firebaseUri = taskSnapshot.getDownloadUrl();
                // taskSnapshot.getDownloadUrl(); was redlining, I read on stackoverflow
                // its a bug so I suppressed inspection for class and it went away

                Log.d(TAG, "onSuccess: firebase download url: " + firebaseUri.toString());
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

                // Insert the Post object
                Post post = new Post();
                // get the fields
                post.setImage(firebaseUri.toString());
                post.setCity(mCity.getText().toString());
                post.setContact_email(mContactEmail.getText().toString());
                post.setCountry(mCountry.getText().toString());
                post.setDescription(mDescription.getText().toString());
                post.setPost_id(postId);
                post.setPrice(mPrice.getText().toString());
                post.setState_province(mStateProvince.getText().toString());
                post.setTitle(mTitle.getText().toString());
                post.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());

                // get the reference and reference where we will be inserting our string
                reference.child(getString(R.string.node_posts))
                        .child(postId) // another child for the post id
                        .setValue(post); // this will insert our value into the db

                // reset the field cos our post is complete
                resetFields();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "could not upload photo", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                // get how far along we are in the upload process
                double currentProgress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                if (currentProgress > (mProgress + 15)) // print only when progress is greater than x15
                {
                  mProgress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    // taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount(); was redlining,
                    // I read on stackoverflow its a bug so I suppressed inspection for class and it went away

                    Log.d(TAG, "onProgress: upload is " + mProgress +  "& done");
                    Toast.makeText(getActivity(), mProgress + "%", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    // compress to bytes
    public static byte[] getBytesFromBitmap(Bitmap bitmap, int quality) { // quality = quality of image as needed

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        return stream.toByteArray();
        // now use this method in doInbackground()
    }

    //=======End Compressing images before uploading ===============

    private void resetFields(){
        UniversalImageLoader.setImage("", mPostImage);
        mTitle.setText("");
        mDescription.setText("");
        mPrice.setText("");
        mCountry.setText("");
        mStateProvince.setText("");
        mCity.setText("");
        mContactEmail.setText("");
    }

    private void showProgressBar(){
        mProgressBar.setVisibility(View.VISIBLE);

    }

    private void hideProgressBar(){
        if(mProgressBar.getVisibility() == View.VISIBLE){
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    *//**
     * Return true if the @param is null
     * @param string
     * @return
     *//*
    private boolean isEmpty(String string){
        return string.equals("");
    }
*/
}


