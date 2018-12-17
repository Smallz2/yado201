package app.yado.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import app.yado.EditUserProfile;
import app.yado.Models.userModel;
import app.yado.R;
import app.yado.Utils.Utils;

public class User_Profile_Activity extends AppCompatActivity {

    // Global Variables
    private final static String TAG = User_Profile_Activity.class.getSimpleName();
    Context mContext;

    // Views
    BottomNavigationView bottomNavigationView;
    Toolbar toolbar;
    ImageView mUserProfileImg;
    TextView mUserEmail, mUserDisplayName, mUserProfileEdit, mUserAbout;
    ImageView mUserEmailConfirm, mUserPhoneConfirm;
    Button mUserConfirmBtn;
    LinearLayout mAboutUserWrapper;

    // vars
    FirebaseAuth mFireBaseAuth;
    FirebaseUser mFireBaseUser;


    //todo set default location


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__profile_);

        mContext = getApplicationContext();

        // Find views
        bottomNavigationView = findViewById(R.id.bottom_nav);
        toolbar = findViewById(R.id.toolbar);
        mUserProfileImg = findViewById(R.id.id_userImg);
        mUserDisplayName = findViewById(R.id.id_user_name);
        mUserEmail = findViewById(R.id.id_user_email);
        mUserEmailConfirm = findViewById(R.id.id_email_confirm);
        mUserPhoneConfirm = findViewById(R.id.id_phone_confirm);
        mUserProfileEdit = findViewById(R.id.id_profileEdit);
        mUserConfirmBtn = findViewById(R.id.id_confirm_accounts);
        mUserAbout = findViewById(R.id.id_aboutUserText);
        mAboutUserWrapper  = findViewById(R.id.id_aboutUserWrapper);


        init();
    }

    private void init() {
        Log.d(TAG, "init: Initializing");

        // get current user;
        mFireBaseAuth = FirebaseAuth.getInstance();
        mFireBaseUser = mFireBaseAuth.getCurrentUser();

        // Set ToolBar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Profile");
        }
      //  mToolBarTitle.setText("Profile");

        // Disable navbar shifting. navBar starts to shift with more than 3 items.
        //Utils.disableShiftMode(bottomNavigationView);

        // Navigation Bar Activity Switcher
        Utils.navigationItemSelector(mContext, bottomNavigationView, TAG);
        bottomNavigationView.setSelectedItemId(R.id.action_nav_profile); // not the best way to do this

        // set profile information
        setUserProfileInformation(mFireBaseUser);

        mUserProfileEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EditUserProfile.class);
                startActivity(intent);
            }
        });

        mUserConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Confirm accounts", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setUserProfileInformation(final FirebaseUser user) {

        String userID = user.getUid();

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

        Query query = databaseReference
                .orderByChild("userID")
                .equalTo(userID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        if (dataSnapshot1.exists()) {
                            userModel userInfo = dataSnapshot1.getValue(userModel.class);

                            // set user information

                            mUserDisplayName.setText(userInfo.getUserName());
                            mUserEmail.setText(userInfo.getUserEmail());

                            // check if about user is empty : if it is then don't show user about
                            if (userInfo.getUserAbout().equals("")) {
                                mAboutUserWrapper.setVisibility(View.INVISIBLE);
                            } else {
                                mUserAbout.setText(userInfo.getUserAbout());
                            }

                            Glide.with(mContext)
                                    .load(userInfo.getUserPhotoUrl())
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(mUserProfileImg);

                            if (isEmailVerified(mFireBaseUser)) {
                                mUserEmailConfirm.setVisibility(View.VISIBLE);
                            }

                            // todo : check if phone is verified
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private Boolean isEmailVerified(FirebaseUser user) {
        return user.isEmailVerified();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionMenu: inflating profile_toolbar_menu");

        getMenuInflater().inflate(R.menu.profile_toolbar_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.id_appSettings) {
            Intent intent = new Intent(getApplicationContext(), App_Setting.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        // update UI on activity resume
        setUserProfileInformation(mFireBaseUser);

        super.onResume();
    }
}
