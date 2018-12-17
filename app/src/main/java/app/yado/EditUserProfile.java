package app.yado;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import app.yado.Models.userModel;
import app.yado.Utils.Utils;

public class EditUserProfile extends AppCompatActivity {

    // Globals

    // views
    Toolbar toolbar;
    EditText mUserName, mUserAbout;
    Button  mUpdateBtn;
    ImageView mUserProfileImage;

    // vars
    boolean isChange = false;
    FirebaseUser mFireBaseUser;
    private static final int REQUEST_IMAGE_CAPTURE = 0;
    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int REQUEST_CAMERA = 0;
    private static final int REQUEST_STORAGE = 1;
    private Uri selectedImageUri;
    private String userProfilePictureUrl;
    private ProgressDialog progressDialog;

    private static final String TAG = EditUserProfile.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);

        // find views
        toolbar = findViewById(R.id.my_toolbar);
        mUserName = findViewById(R.id.id_user_user_name_editText);
        mUserAbout = findViewById(R.id.id_user_about_editText);
        mUpdateBtn = findViewById(R.id.id_update_button);
        mUserProfileImage = findViewById(R.id.id_user_photo);

        init();
    }

    private void init() {
        Log.d(TAG, "init: initializing");

        // set Toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Edit Profile");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                toolbar.setTitleTextColor(getColor(R.color.colorWhite));
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                toolbar.setBackgroundColor(getColor(R.color.purpleAccent));
            }

            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        mFireBaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // set user information
        setUserProfileInformation();

        mUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfileInformation();
            }
        });
    }


    private void setUserProfileInformation () {
        Log.d(TAG, "setUserProfileInformation: setting user profile information");

        final String userID = mFireBaseUser.getUid();

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
                            userModel user = dataSnapshot1.getValue(userModel.class);

                            // set user information

                            if (user!=null) { // if user exist then set information
                                Glide.with(getApplicationContext())
                                        .load(user.getUserPhotoUrl())
                                        .apply(RequestOptions.circleCropTransform())
                                        .into(mUserProfileImage);

                                mUserName.setText(user.getUserName());

                                mUserAbout.setText(user.getUserAbout());

                                if (user.getUserAbout().isEmpty()) {
                                    mUserAbout.setHint("Tell people about yourself, this may increase your chances selected to do a task.");
                                }
                            }

                            // todo : set about
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void event_clickUserName(View view) {
        mUserName.setFocusable(true);
        mUserName.requestFocus();

        showSoftKeyboard(mUserName);
    }

    public void event_clickAbout(View view) {
        mUserAbout.setFocusable(true);
        mUserAbout.requestFocus();

        showSoftKeyboard(mUserAbout);
    }

    private void updateProfileInformation() {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

        final String userID = mFireBaseUser.getUid();

        // Start progress bar
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("updating");
        progressDialog.show();

        Query query = databaseReference
                .orderByChild("userID")
                .equalTo(userID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        if (dataSnapshot1.exists()) {
                            userModel user = new userModel();

                            user.setUserName(mUserName.getText().toString());
                            user.setUserAbout(mUserAbout.getText().toString());

                            Map<String, Object> childUpdate = new HashMap<>();
                            childUpdate.put("userName", user.getUserName());
                            childUpdate.put("userAbout", user.getUserAbout());

                            DatabaseReference directLink = FirebaseDatabase.getInstance().getReference("users/" + userID);

                            directLink.updateChildren(childUpdate, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    if (databaseError  == null) {
                                        Log.d(TAG, "onComplete: sending updated data to firebase user database");
                                    }
                                }
                            });

                            uploadUserImage(userID);

                            // end progress bar : user profile is updated already
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    Toast.makeText(EditUserProfile.this, "profile updated", Toast.LENGTH_SHORT).show();

                                    finish();
                                }
                            },350);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private boolean checkForChange() {
        Log.d(TAG, "checkForChange: checking if user change profile information");


        mUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                isChange = true;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isChange = true;
            }

            @Override
            public void afterTextChanged(Editable s) {
                isChange = true;
            }
        });

        mUserAbout.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // todo : check is phone number was changed

        return isChange;
    }

    private void showSoftKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public void event_ChangeProfileImage(View view) {
        Log.d(TAG, "event_ChangeProfileImage: changing user profile image");

        // Launch image picker fragment
        View view1 = getLayoutInflater().inflate(R.layout.bottom_sheet_image_option_dialog, null);

        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(view1);
        dialog.show();
    }

    public void event_taskPictureFromCamera(View view) {
        dispatchTakePictureIntent();
    }

    public void event_choosePictureFromGallery(View view) {
        dispatchPickPictureIntent();
    }

    /**
     * Launch camera to take picture
     */
    private void dispatchTakePictureIntent() {
        Log.d(TAG, "dispatchPictureIntent: taking picture with camera if permission is granted");

        // only from M and greater
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // check if camera permission is available
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                // Camera permission is granted, show camera
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            } else {
                // Camera permissions has not been granted

                // Request camera permission
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA);
            }
        }
    }


    /**
     * Launch gallery to select an image
     */
    private void dispatchPickPictureIntent() {
        Log.d(TAG, "dispatchPickPictureIntent: tasking picture from gallery if permission is granted");

        // only from M and greater
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // permission granted, show gallery;
                Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickPhotoIntent.setType("image/*");
                startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_PICK);
            } else {
                // Camera permission has not been granted

                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE);
            }
        }
    }

    /**
     * Uploading user selected image to fireBase storage
     * @param userID : uploader userID
     */
    private void uploadUserImage(final String userID) {
        Log.d(TAG, "uploadUserImage: uploading user selected image");

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageReference = storage.getReference();

        if (selectedImageUri != null) {
            final StorageReference ref = storageReference.child("profileImages/" + userID);

            ref.putFile(selectedImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.d(TAG, "uploadUserImage: onSuccess : uploaded image");

                            //get path for uploaded image
                            storageReference.child("profileImages/" + userID).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    userProfilePictureUrl = uri.toString();

                                    // add update picture to database
                                    Map<String, Object> childUpdates = new HashMap<>();
                                    childUpdates.put("userPhotoUrl", userProfilePictureUrl);

                                    database.getReference("users/" + userID).updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                            if(databaseError == null) {
                                                Log.d(TAG, "onComplete: sending data to fireBase user database");
                                            }
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Failed to get image
                                    Log.w(TAG, "onFailed: failed to get download url from fireBase storage");
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "uploadUserImage: onFailure : Failed :" + e.getMessage());
                        }
                    });
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 0:
                // Received permission result for camera permission.

                // check if the permissions has been granted
                if (grantResults[0]  == PackageManager.PERMISSION_GRANTED) {
                    // Camera permission has been granted, user can take photo
                    dispatchTakePictureIntent();
                } else {
                    // Camera permission was denied, so we cannot use this feature
                    Toast.makeText(this, "Permissions was not granted", Toast.LENGTH_SHORT).show();
                }
                break;
            case 1:
                // Receive permission result from storage permission

                // check if storage permission has been granted
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // storage permission granted
                    dispatchPickPictureIntent();
                }

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (data != null) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");

                // set image in view
                Glide.with(this)
                        .load(imageBitmap)
                        .apply(RequestOptions.circleCropTransform())
                        .into(mUserProfileImage);

                selectedImageUri = Utils.getImageUri(this, imageBitmap);
            }
        }

        if (requestCode == REQUEST_IMAGE_PICK) {
            if (data != null) {

                selectedImageUri = data.getData();

                // set image in view
                Glide.with(this)
                        .load(selectedImageUri)
                        .apply(RequestOptions.circleCropTransform())
                        .into(mUserProfileImage);
            }
        }
    }
 }
