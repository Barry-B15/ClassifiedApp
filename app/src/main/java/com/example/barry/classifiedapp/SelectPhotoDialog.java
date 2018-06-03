package com.example.barry.classifiedapp;

/**
 * Created by barry on 11/13/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * https://www.youtube.com/watch?v=HhaSEVr6AOw&index=6&list=PLgCYzUzKIBE-G0tuxjKGkl_keIW2FFwKX
 * 1. declare the constant
 * 2. we have only 2 widgets so just go ahead and add them
 * 3. create the intents
 * 4. create the onActivityFor Result() method
 *      right-click > generate > override > select onACtivityResult() > ok
 *      or ctrl + o > onActivityResult > ok
 */

public class SelectPhotoDialog extends android.support.v4.app.DialogFragment {

    private static final String TAG = "SelectPhotoDialog";
    private static final int PICKFILE_REQUEST_CODE = 111;
    private static final int CAMERA_REQUEST_CODE = 333;

    public interface OnPhotoSelectedListener{
        void getImagePath(Uri imagePath);
        void getImageBitmap(Bitmap bitmap);
    }
    OnPhotoSelectedListener mOnPhotoSelectedListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_selectphoto, container, false);

        TextView selectPhoto = (TextView) view.findViewById(R.id.dialogChoosePhoto);
        selectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: accessing phones memory.");
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, PICKFILE_REQUEST_CODE);
            }
        });

        TextView takePhoto = (TextView) view.findViewById(R.id.dialogOpenCamera);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: starting camera.");
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*
            Results when selecting a new image from memory
         */
        if(requestCode == PICKFILE_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            Uri selectedImageUri = data.getData();
            Log.d(TAG, "onActivityResult: image uri: " + selectedImageUri);

            //send the uri to PostFragment & dismiss dialog
            mOnPhotoSelectedListener.getImagePath(selectedImageUri);
            getDialog().dismiss();
        }
        /*
            Results when taking a new photo with camera
         */
        else if(requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            Log.d(TAG, "onActivityResult: done taking new photo");
            Bitmap bitmap;
            bitmap = (Bitmap) data.getExtras().get("data");

            //send the bitmap to PostFragment and dismiss dialog
            mOnPhotoSelectedListener.getImageBitmap(bitmap);
            getDialog().dismiss();
        }
    }

    @Override
    public void onAttach(Context context) {
        try{
            mOnPhotoSelectedListener = (OnPhotoSelectedListener) getTargetFragment();
        }catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage() );
        }
        super.onAttach(context);
    }

    /*private static final String TAG = "SelectPhotoDialog";
    private static final int PICKFILE_REQUEST_CODE = 333;
    private static final int CAMERA_REQUEST_CODE = 111;

    // create an interface to send data to the PostFragment
    public interface OnPhotoSelectedListener {
        void getImagePath(Uri imagePath);
        void getImageBitmap(Bitmap bitmap);
    }
    // create the interface object
    OnPhotoSelectedListener mOnPhotoSelectedListener;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_selectphoto, container, false);

        TextView selectPhoto = (TextView) view.findViewById(R.id.dialogChoosePhoto);
        selectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //select photo from memory
                Log.d(TAG, "onClick: accessing phones memory,");
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT); // browse photo
                intent.setType("image*//*");
                startActivityForResult(intent, PICKFILE_REQUEST_CODE);
            }
        });

        TextView takePhoto = (TextView) view.findViewById(R.id.dialogOpenCamera);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //select photo from memory
                Log.d(TAG, "onClick: starting camera,");
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // result when selecting a new image from memory
        if (requestCode == PICKFILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri selectedImageUri = data.getData();
            Log.d(TAG, "onActivityResult: image uri: " + selectedImageUri);

            // send the uri to PostFragment and dismiss dialog
            mOnPhotoSelectedListener.getImagePath(selectedImageUri);
            getDialog().dismiss();
        }
        // result when selecting a new image from memory
        else if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "onActivityResult: done taking new photo");
            Bitmap bitmap;
            bitmap = (Bitmap) data.getExtras().get("data"); // retrieve the bitmap we just took

            // send the data to PostFragment and dismiss dialog
            mOnPhotoSelectedListener.getImageBitmap(bitmap);
            getDialog().dismiss();

            // then create an interface to send the bitmap to PostFragment
            // Go up and create the interface onPhotoSelectedListener()
            // and insert an onAttach() and instantiate the interface in onAttach()
            // Dont forget to create the interface object
        }

        //Insert an onAttach

    }

    // insert an on attach
    // right-click > type in onAttach > select onattach > ok
    @Override
    public void onAttach(Context context) {
        // init our listener
        try {
            mOnPhotoSelectedListener = (OnPhotoSelectedListener) getTargetFragment();
            // works with setTarget() in PostFragment init()
        }
        catch (ClassCastException e) {

            Log.e(TAG, "onAttach: ClassCatchException: " + e.getMessage() );
        }
        super.onAttach(context);
    }*/
}
