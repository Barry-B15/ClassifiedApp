package com.example.barry.classifiedapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.barry.classifiedapp.account.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by barry on 11/5/2017.
 */

// when using android.support.v4.view.ViewPager,
// all fragments must extent android.support.v4.app.Fragment, NOT Fragment
public class AccountFragment extends android.support.v4.app.Fragment {

    // Create a log  (String TAG)
    // logt > enter created the following String
    private static final String TAG = "AccountFragment";

    //1. firebase auth
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    //2. widget
    private Button mSignOut;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        //3. init the button
        mSignOut = (Button) view.findViewById(R.id.sign_out);

        //6. call setup Firebase Listener
        setupFirebaseListener();

        //3.
        mSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to sign out user.");
                FirebaseAuth.getInstance().signOut();
            }
        });

        return view;
    }

    //5. create setupFirebase method
    private void setupFirebaseListener() {
        Log.d(TAG, "setupFirebaseListener: setting up the auth state listener.");

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // get the user
                FirebaseUser user = firebaseAuth.getCurrentUser();
                //if user is signed in
                if (user != null){
                    Log.d(TAG, "onAuthStateChanged: Signed_in: " + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged: Signed_out");
                    Toast.makeText(getActivity(), "Signed out", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    // clear all activity so moving back will actually do nothing
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }

            }
        };
    }
    //4. set up firebase requires onStart, onStop
    @Override
    public void onStart() {
        super.onStart();
        //6.
        FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener);
    }

    // 4.
    @Override
    public void onStop() {
        super.onStop();
        //6.
        if (mAuthStateListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthStateListener);
        }
    }
    // override onStart, onStop



}
