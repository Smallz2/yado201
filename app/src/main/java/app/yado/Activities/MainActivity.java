package app.yado.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import app.yado.Models.userModel;
import app.yado.Utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import app.yado.Utils.FirebaseMessagingService;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    /* Global Variables   */

    // FireBase
    private FirebaseAuth mFireBaseAuth;
    private FirebaseUser mFireBaseUsers;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Context mContext;

    // Others
    private static final String TAG = MainActivity.class.getSimpleName();
    Boolean isUserFirstTime = false;
    public static final String PREF_USER_FIRST_TIME = "user_first_time";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //FirebaseDatabase.getInstance().setPersistenceEnabled(true); // firebase persistence causing app to crash

       // Get shared preference value
        isUserFirstTime = Boolean.valueOf(Utils.readSharedSetting(MainActivity.this, PREF_USER_FIRST_TIME, "true"));

        Intent introIntent = new Intent(MainActivity.this, On_Boarding_Page_Activity.class);
        introIntent.putExtra(PREF_USER_FIRST_TIME, isUserFirstTime);

        // Check to see if it the user's first time using the app
        if (isUserFirstTime) {
            startActivity(introIntent);
            finish();
        }


       setupFireBaseAuth();

        mContext = getApplicationContext();
    }

    public void googleSignOut(View view) {
        FirebaseAuth.getInstance().signOut();
    }

    /*
    ----------------------------------- FireBase -----------------------------------------
     */

    private void saveUserDataToFireBase() {
        userModel mUserModel = new userModel();

        mUserModel.setUserName(mFireBaseUsers.getDisplayName());
        mUserModel.setUserID(mFireBaseUsers.getUid());
        mUserModel.setUserEmail(mFireBaseUsers.getEmail());
        mUserModel.setUserPhotoUrl(mFireBaseUsers.getPhotoUrl().toString());

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String key = database.getReference("userList").push().getKey();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key, mUserModel.toFirebase());

        database.getReference("userList").updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if(databaseError == null) {
                    Log.d(TAG, "onComplete: user information sent to firebase");
                }
            }
        });

    }

    /**
     * checks to see if the @param 'user' is logged in
     * @param user: fireBase User
     */
    private void checkCurrentUser(FirebaseUser user) {
        Log.d(TAG, "checkCurrentUser: checking if user is logged in");

        if (user == null) {
            Intent intent = new Intent(mContext, LoginActivity.class);
            startActivity(intent);
        } else {
            checkForAccessToken(user);

            Intent intent = new Intent(mContext, Home_Activity.class);
            startActivity(intent);
        }
    }

    private void checkForAccessToken(FirebaseUser user) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users/" + user.getUid());

        Query query = databaseReference
                .child("AccessToken");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d(TAG, "No AccessToken Found ");
                } else {
                    // Get token
                    FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if (!task.isSuccessful()) {
                                Log.d(TAG, "getInstance failed:", task.getException());
                            } else {
                                String token = task.getResult().getToken();

                                FirebaseMessagingService.sendRegistrationToServer(token);
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

    /**
     * Setup the fireBase auth object
     */

    private void setupFireBaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth");

        mFireBaseAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                //check if the user is logged
                checkCurrentUser(user);

                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthSateChange:signed_in:" + user.getUid());

                    // todo : give the user the power to do this in settings
//                    //check is email is verified
//                    if(!user.isEmailVerified()) {
//                       Intent intent = new Intent(mContext, userEmailVerification.class);
//                       startActivity(intent);
//                       finish();
//                    }
                 } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    private void generateAccessToken() {
        // Get token
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast

                        Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                    }
                });

    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        mFireBaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mFireBaseAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
