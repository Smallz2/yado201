package app.yado.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import app.yado.R;

public class userEmailVerification extends Activity implements View.OnClickListener {

    //Global Variables
    private Context mContext;

    // FireBase and Google
    private FirebaseAuth mFireBaseAuth;
    private FirebaseUser mFireBaseUser;

    //Others
    String mUserEmail;
    String TAG = "user_Email_Verification_Activity";

    //TextViews
    TextView mEmailOnScreen;

    //Buttons
    Button mContinueButton;
    Button mSignOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_email_verification);

        //Initialize FireBase
        mFireBaseAuth = FirebaseAuth.getInstance();
        mFireBaseUser = mFireBaseAuth.getCurrentUser();
        mUserEmail = mFireBaseUser.getEmail();

        mContext = getApplicationContext();


        // Find views
        mContinueButton = findViewById(R.id.continueButton);
        mEmailOnScreen = findViewById(R.id.userEmailAddress);
        mEmailOnScreen.setText(mUserEmail);
        mSignOutButton = findViewById(R.id.signOutBtn);

        init();
    }

    private void init() {
        Log.d(TAG, "init: initializing");


        //Send Email Verification
        mEmailVerification();

        // Set on click events
        mContinueButton.setOnClickListener(this);
        mSignOutButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.continueButton:
                isEmailVerified();
                break;
            case R.id.signOutBtn:
                FirebaseAuth.getInstance().signOut();
                break;
        }
    }

    private void isEmailVerified() {
        //Refresh the user
        mFireBaseUser.reload();

        Boolean isVerified =  mFireBaseUser.isEmailVerified();
        Log.d(TAG, isVerified.toString());

       if (!isVerified) {
           // Email not verified
           Toast.makeText(mContext, "Please verify your email before you continue", Toast.LENGTH_SHORT).show();
           Log.w(TAG, "Email Not Verified:" + mFireBaseUser);
       } else {
           //email verified
           Log.d(TAG, "Email Verified:" + mFireBaseUser);

           mLogoutCurrentUser();

           Intent intent = new Intent(mContext, LoginActivity.class);
           startActivity(intent);
           finish();
       }
    }

    private void mEmailVerification() {
        mFireBaseUser.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "emailVerification:success");
                        } else {
                            Log.d(TAG, "emailVerification:fail");
                        }
                    }
                });
    }

    /**
     * Using this log out user because createUserWithEmailandPassword Automatically signs in the user.
     */
    private void mLogoutCurrentUser() {
        FirebaseAuth.getInstance().signOut();
    }
}
