package app.yado;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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

public class view_user_profile extends AppCompatActivity {

    // Views
    BottomNavigationView bottomNavigationView;
    Toolbar toolbar;
    ImageView mUserProfileImg;
    TextView mUserEmail, mUserDisplayName, mUserProfileEdit, mUserAbout;
    ImageView mUserEmailConfirm, mUserPhoneConfirm;
    Button mUserConfirmBtn;
    LinearLayout mAboutUserWrapper;

    // Vars
    private static final String TAG = view_user_profile.class.getSimpleName();
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_profile);

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
        Log.d(TAG, "init: initializing");

        // set toolbar
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Profile");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                toolbar.setTitleTextColor(getColor(R.color.fontColorPrimary));
            }

            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }

        Bundle bundle = getIntent().getExtras();
        String userID = null;
        if (bundle != null) {
            userID = bundle.getString("userID");
        }
        setUserProfileInformation(userID);
    }


    private void setUserProfileInformation(String userID) {

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

//                            if (isEmailVerified(mFireBaseUser)) {
//                                mUserEmailConfirm.setVisibility(View.VISIBLE);
//                            }

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

}
