package app.yado.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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
import app.yado.Models.userModel;
import app.yado.R;

public class App_Setting extends AppCompatActivity implements View.OnClickListener {

    // Globals
    private static final String TAG = App_Setting.class.getSimpleName();

    //views
    Toolbar toolbar;
    RelativeLayout mUpdateAccountBtn, mChangePasswordBtn,
            mLogOutBtn, mRateBtn, mFollowFacebookBtn, mFollowInstragramBtn,
            mHelpBtn, mFeedbackBtn, mTermBtn, mPrivacyBtn;
    ImageView mUserProfileImg;
    TextView mUserEmail;


    //vars


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app__setting);

        // Find views
        toolbar = findViewById(R.id.my_toolbar);
        mUpdateAccountBtn = findViewById(R.id.id_upgradeAccountRel);
        mChangePasswordBtn = findViewById(R.id.id_changePasswordRel);
        mLogOutBtn = findViewById(R.id.id_logOutRel);
        mRateBtn = findViewById(R.id.id_rateUsRel);
        mFollowFacebookBtn = findViewById(R.id.id_followFacebookRel);
        mFollowInstragramBtn = findViewById(R.id.id_followInstragramRel);
        mHelpBtn = findViewById(R.id.id_helpRel);
        mFeedbackBtn = findViewById(R.id.id_feedbackRel);
        mTermBtn = findViewById(R.id.id_termsRel);
        mPrivacyBtn = findViewById(R.id.id_privacyRel);
        mUserProfileImg = findViewById(R.id.id_user_img);
        mUserEmail = findViewById(R.id.id_user_email);

        init();
    }

    private void init() {
        Log.d(TAG, "init: initializing");

        // set toolbar with back button
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Settings");

            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        setUserAccountInformation();

        mUpdateAccountBtn.setOnClickListener(this);
        mChangePasswordBtn.setOnClickListener(this);
        mLogOutBtn.setOnClickListener(this);
        mRateBtn.setOnClickListener(this);
        mFollowFacebookBtn.setOnClickListener(this);
        mFollowInstragramBtn.setOnClickListener(this);
        mHelpBtn.setOnClickListener(this);
        mFeedbackBtn.setOnClickListener(this);
        mTermBtn.setOnClickListener(this);
        mPrivacyBtn.setOnClickListener(this);
    }

    private void setUserAccountInformation() {
        Log.d(TAG, "setUserAccountInformation: setting user account information");

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");



        if (user != null) {
            Query query = ref
                    .orderByChild("userID")
                    .equalTo(user.getUid());

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            if (dataSnapshot1.exists()) {

                                userModel userModel = dataSnapshot1.getValue(userModel.class);

                                // set user profile image
                                if (userModel != null) {
                                    Glide.with(getApplicationContext())
                                            .load(userModel.getUserPhotoUrl())
                                            .apply(RequestOptions.circleCropTransform())
                                            .into(mUserProfileImg);

                                    // set user email
                                    mUserEmail.setText(userModel.getUserEmail());
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_upgradeAccountRel:
                Toast.makeText(this, "Hi", Toast.LENGTH_SHORT).show();

                break;
            case R.id.id_changePasswordRel:
                Toast.makeText(this, "Hi", Toast.LENGTH_SHORT).show();

                break;
            case R.id.id_logOutRel:
                logOutCurrentUser();
                break;
            case R.id.id_rateUsRel:
                Toast.makeText(this, "Hi", Toast.LENGTH_SHORT).show();

                break;
            case R.id.id_followFacebookRel:
                Toast.makeText(this, "Hi", Toast.LENGTH_SHORT).show();

                break;
            case R.id.id_followInstragramRel:
                Toast.makeText(this, "Hi", Toast.LENGTH_SHORT).show();

                break;
            case R.id.id_helpRel:
                Toast.makeText(this, "Hi", Toast.LENGTH_SHORT).show();

                break;
            case R.id.id_feedbackRel:
                Toast.makeText(this, "Hi", Toast.LENGTH_SHORT).show();

                break;
            case R.id.id_termsRel:
                Toast.makeText(this, "Hi", Toast.LENGTH_SHORT).show();

                break;
            case R.id.id_privacyRel:
                Toast.makeText(this, "Hi", Toast.LENGTH_SHORT).show();

                break;
        }
    }

    private void logOutCurrentUser() {
        Log.d(TAG, "logOutCurrentUser: logging out current user");

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:

                        Log.d(TAG, "onClick: sign out current user");
                        FirebaseAuth.getInstance().signOut();

                        // send user back to main screen
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Log out");
        builder.setMessage("Are you sure you want to log out?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
