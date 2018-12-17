package app.yado.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import app.yado.Models.userModel;
import app.yado.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    /* Global Variables */


    // FireBase and Google
    private FirebaseAuth mFireBaseAuth;
    private FirebaseUser mFireBaseUser;
    private GoogleApiClient mGoogleSignApiClient;
    private static final int RC_SIGN_IN = 1;

    // Buttons
    SignInButton mGoogleBtn;
    TextView signUpButton;
    Button formLoginButton;

    // TextInputLayout
    TextInputLayout mEmailWrapper, mPasswordWrapper;

    // Edit Text
    EditText mUserEmailEditText, mUserPasswordEditText;


    //Text Views
    TextView mFormPasswordReset;

    // Others
    private static final String TAG = "LoginActivity";
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFireBaseAuth = FirebaseAuth.getInstance();
        mGoogleBtn  = findViewById(R.id.sign_in_button);
        mGoogleBtn.setSize(SignInButton.SIZE_WIDE);

        // Find views;
        signUpButton = findViewById(R.id.signUpBtn);
        formLoginButton = findViewById(R.id.formLoginButton);
        mEmailWrapper = findViewById(R.id.formEmailWrapper);
        mUserEmailEditText = findViewById(R.id.formEmail);
        mPasswordWrapper = findViewById(R.id.formPasswordWrapper);
        mUserPasswordEditText = findViewById(R.id.formPassword);
        mFormPasswordReset = findViewById(R.id.formForgetPassword);

        mContext = getApplicationContext();


        init();
    }

    @Override
    public void onClick (View view) {
        switch (view.getId()) {
            case R.id.sign_in_button:
                googleSignIn();
                break;
            case R.id.signUpBtn:
                signUpWithEmail();
                break;
            case R.id.formLoginButton:
                signInWithEmail();
                break;
            case R.id.formForgetPassword:
                passwordReset();
                break;
        }

    }

    private void init() {
        Log.d(TAG, "init: initializing");


        // Configure Google Sign-in to request the user's ID, Email, and basic
        // profile. ID and basic profile included in DEFAULT_SIGN_IN;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("779076553124-97c0pq4apb0uqdcti462rpofrm3ee250.apps.googleusercontent.com") // todo: put this a better way
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-in Api and the
        // options specified by gso.sig
        mGoogleSignApiClient = new GoogleApiClient.Builder(getApplicationContext())
//                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {  // todo:  issue with fragment : look into
//                    @Override
//                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//                        Toast.makeText(getApplicationContext(), "Error: Couldn't Sign-in", Toast.LENGTH_SHORT).show();
//                        Log.e(TAG, "Couldn't Sign-in User");
//                    }
//                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Set onClick Listener to elements
        mGoogleBtn.setOnClickListener(this);
        signUpButton.setOnClickListener(this);
        formLoginButton.setOnClickListener(this);
        mFormPasswordReset.setOnClickListener(this);

    }

    private void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleSignApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mFireBaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = mFireBaseAuth.getCurrentUser();
                        // Update UI for success
                        updateFireBaseUserDatabase(user);

                        Intent intent = new Intent (LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        // Update UI for fail
                    }
                }
            });
    }

    // SignUp with email function
    private void signUpWithEmail() {
        Intent intent = new Intent(mContext, Registration.class);
        startActivity(intent);
    }

    //Signin with Email
    private void signInWithEmail() {
        Log.d(TAG, "signInWithEmail: signing user in with email");

        String mUserEmail, mUserPassword;

        mUserEmail = mUserEmailEditText.getText().toString();
        mUserPassword = mUserPasswordEditText.getText().toString();

        if (inputTextErrorHanding(mUserEmail, mUserPassword)) {
            mFireBaseAuth.signInWithEmailAndPassword(mUserEmail, mUserPassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //SignIn success
                                mFireBaseUser = mFireBaseAuth.getCurrentUser();


                                Intent intent = new Intent(mContext, MainActivity.class);
                                startActivity(intent);
                                finish();

//                                if (mFireBaseUser.isEmailVerified()) {
//                                    Log.d(TAG, "SignInWithEmail:success");
//                                    updateFireBaseUserDatabase(mFireBaseUser);
//
//                                    Intent intent = new Intent(mContext, MainActivity.class);
//                                    startActivity(intent);
//                                    finish();
//                                } else {
//                                    Intent intent = new Intent(mContext, Registration.class);
//                                    startActivity(intent);
//                                    finish();
//                                }
                            } else {
                                //SignIn fail
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(mContext, "Either your email of password is incorrect", Toast.LENGTH_SHORT).show();
                                //TODO: send report feedback on why sign in failed
                            }
                        }
                    });
        }

    }

    private boolean inputTextErrorHanding(String email, String password) {
        Log.d(TAG, "inputTextErrorHanding: handling errors");

        int nonErrorCount = 0;

        if(isStringNull(email)) {
            mEmailWrapper.setError("This field can't be blank");
        } else {
            mEmailWrapper.setErrorEnabled(false);
            nonErrorCount = nonErrorCount + 1;
        }

        if(isStringNull(password)) {
           mPasswordWrapper.setError("This field can't be blank");
        } else {
            mPasswordWrapper.setErrorEnabled(false);
            nonErrorCount = nonErrorCount + 1;
        }

        return nonErrorCount == 2;
    }

    private boolean isStringNull(String string) {
        Log.d(TAG, "isStringNull: checking if string is null");
        return string.equals("");
    }

    private void passwordReset() {
        Intent intent = new Intent(mContext, reset_password.class);
        startActivity(intent);
        finish();
    }

    private void updateFireBaseUserDatabase(final FirebaseUser firebaseUser) {
        Log.d(TAG, "updateFireBaseUserDatabase: checking if user is already present in database and update accordingly");

        final String userID = firebaseUser.getUid();

        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("users");

        Query query = mRef.child("").orderByChild("userID").equalTo(userID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // userID was found
                } else {
                    // add user information to fireBase

                    userModel userModel = new userModel();

                    userModel.setUserID(userID);
                    userModel.setUserEmail(firebaseUser.getEmail());
                    userModel.setUserPhotoUrl(firebaseUser.getPhotoUrl().toString());
                    userModel.setUserName(firebaseUser.getDisplayName());
                    userModel.setUserAbout("");

                    Map<String, Object> childUpdate = new HashMap<>();
                    childUpdate.put(userID, userModel.toFirebase());

                    mRef.updateChildren(childUpdate, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError == null ) {
                                Log.d(TAG, "onComplete: added user");
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



    @Override
    public void onBackPressed() {
        // not the best way to do this but onBack button just relaunch current activity
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

}

