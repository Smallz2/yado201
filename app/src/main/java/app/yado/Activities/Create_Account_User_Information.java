package app.yado.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import app.yado.Models.userModel;
import app.yado.R;
import app.yado.Utils.Utils;

public class Create_Account_User_Information extends AppCompatActivity {

    // Globals

    // Views
    Toolbar toolbar;
    TextInputLayout mFirstNameInputLayout, mLastNameInputLayout, mAboutInputLayout;
    EditText mFirstNameEditText, mLastNameEditText, mAboutEditText;
    Button mContinueBtn;
    RelativeLayout mAddProfileImg;
    ImageView mUserProfileImg;

    // Vars
    private static final String TAG = Create_Account_User_Information.class.getSimpleName();
    private String userEmail, userPassword, userPhoneNumber, userFirstName, userLastName, userAbout;
    private FirebaseAuth mFireBaseAuth;
    private FirebaseAuth.AuthStateListener mFireBaseAuthListener;
    private FirebaseUser user;
    private static final int REQUEST_IMAGE_CAPTURE = 0;
    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int REQUEST_CAMERA = 0;
    private static final int REQUEST_STORAGE = 1;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create__account__user__information);

        //get intent
        Intent intent = getIntent();

        userEmail = intent.getStringExtra("userEmail");
        userPassword = intent.getStringExtra("userPassword");
        userPhoneNumber = intent.getStringExtra("userPhoneNumber");

        //Find views
        toolbar = findViewById(R.id.my_toolbar);
        mFirstNameInputLayout = findViewById(R.id.formFirstNameInputWrapper);
        mFirstNameEditText = findViewById(R.id.formFirstName);
        mLastNameInputLayout = findViewById(R.id.formLastNameInputWrapper);
        mLastNameEditText = findViewById(R.id.formLastNameEditText);
        mAboutInputLayout = findViewById(R.id.formAboutInputWrapper);
        mAboutEditText = findViewById(R.id.formAboutEditText);
        mContinueBtn = findViewById(R.id.formContinueBtn);
        mUserProfileImg = findViewById(R.id.id_user_profile_img);
        mAddProfileImg = findViewById(R.id.id_user_profile_rel_layout);

        init();
    }

    private void init() {
        Log.d(TAG, "init: initializing");

        mFireBaseAuth = FirebaseAuth.getInstance();

        // set supportActionToolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Create an Account");

            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        // continue button onclick
        mContinueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userFirstName = mFirstNameEditText.getText().toString().trim();
                userLastName = mLastNameEditText.getText().toString().trim();
                userAbout = mAboutEditText.getText().toString();


                String[] strings = {userFirstName, userLastName};

                if (inputTextErrorHanding(strings)) {
                    createFirebaseUser();

                    //FirebaseUser user = mFireBaseAuth.getCurrentUser();


                    //addUserToDatabase(user);
                    if (user != null) {

                    }
                }
            }
        });

        // add profile image rel layout onclick
        mAddProfileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // launch camera app

                imageOptionDialog();
                //dispatchTakePictureIntent();
            }
        });

        // FireBase Auth Listener
        mFireBaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            }
        };


    }


    /**
     * handles errors for inputTextLayout
     * @param array : contains user's firstName, lastName and aboutUser
     * @return : true of false
     */

    private boolean inputTextErrorHanding(String[] array) {
        Log.d(TAG, "inputTextErrorHanding: handing input error from user");

        int true_count = 0;

        TextInputLayout[] inputLayouts = {mFirstNameInputLayout, mLastNameInputLayout};

        // FirstName Field
        if(Utils.isStringNull(TAG, array[0])) {
            inputLayouts[0].setError("This field can't be blank");
        } else {
            inputLayouts[0].setErrorEnabled(false);
            true_count = true_count + 1;
        }

        // Last Name Field
        if (Utils.isStringNull(TAG, array[1])) {
            inputLayouts[1].setError("This field can't be blank");
        } else {
            inputLayouts[1].setErrorEnabled(false);
            true_count = true_count + 1;
        }

        return true_count == 2;
    }

    /**
     * Creates firebase user from email and password entered by user
     */
    private void createFirebaseUser() {
        Log.d(TAG, "createFirebaseUser: creating firebase user");

        mFireBaseAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mFireBaseAuth.getCurrentUser();

                            addUserToDatabase(user);

                            Intent intent = new Intent(getApplicationContext(), Create_Account_Complete.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Create_Account_User_Information.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    /**
     * add non-existent user to fireBase user database
     * @param user : fireBase user
     */
    private void addUserToDatabase(FirebaseUser user) {
        Log.d(TAG, "addUserToDatabase: adding user to database");
        // send user information to user database
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final userModel mUserModel = new userModel();

        mUserModel.setUserID(user.getUid());
        mUserModel.setUserName(userFirstName + "" + userLastName);
        mUserModel.setUserEmail(userEmail);
        mUserModel.setUserAbout(userAbout);

        // check if userSelected Image is null
        if (selectedImageUri != null) {
            uploadUserImage(user.getUid(), mUserModel, database, user);

            // get download url for uploaded image

            // set image to user database
            //mUserModel.setUserPhotoUrl("");
        } else {
            // set default image
            mUserModel.setUserPhotoUrl("https://firebasestorage.googleapis.com/v0/b/com.yado.ultron.yado201-224b0.appspot.com/o/default-user.png?alt=media&token=a4559c44-beef-4788-a2f1-275b036afc02");
        }

    }

    /**
     * Showing user options for image selection
     */
    private void imageOptionDialog() {
        Log.d(TAG, "imageOptionDialog: showing user options for image selection");

        String[] imageOptions = {"Camera", "Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Upload an image");
        builder.setItems(imageOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        dispatchTakePictureIntent();
                        break;
                    case 1:
                        dispatchPickPictureIntent();
                        break;
                }
            }
        });
        builder.show();
    }

    /**
     * Launch camera to take picture
     */

    private void dispatchTakePictureIntent() {
        Log.d(TAG, "dispatchTakePictureIntent: taking picture with camera if permission is granted");

        // only from M and greater;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // check if the camera permission is already available
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                // Camera permission is already available, show camera.
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            } else {
                // Camera permission has not been granted.

                // Provide user additional information about the use of the permission
//                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
//                    Toast.makeText(this, "Camera permission is needed to take a picture", Toast.LENGTH_SHORT).show();
//                }

                // Request Camera permission
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA);
            }
        }
    }

    /**
     * Launch gallery to select an image
     */
    private void dispatchPickPictureIntent() {
        Log.d(TAG, "dispatchPickPictureIntent: taking picture from gallery if permission is granted");

        // only from M and greater
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // permission granted, show gallery
                Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickPhotoIntent.setType("image/*");
                startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_PICK);
            } else {
                // Camera permission has not been granted.

//                // Provide user additional information about the use of the permission
//                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
//                    Toast.makeText(this, "Storage permission is needed to select a picture", Toast.LENGTH_SHORT).show();
//                }

                // Request storage read permissions
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE);
            }
        }
    }

    /**
     * Uploading user selected image to fireBase storage
     * @param userID : uploader userID
     */
    private void uploadUserImage(final String userID, final userModel userModel, final FirebaseDatabase database, final FirebaseUser user) {
        Log.d(TAG, "uploadUserImage: uploading user selected image");

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
                                    userModel.setUserPhotoUrl(uri.toString());

                                    // add user to database
                                    Map<String, Object> childUpdates = new HashMap<>();
                                    childUpdates.put(user.getUid(), userModel.toFirebase());

                                    database.getReference("users").updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 0:
                // Received permission result for camera permission.

                //check if the only required permission has been granted
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Camera permission has been granted, user can take photo
                    dispatchTakePictureIntent();
                } else {
                    // Camera permission was denied, so we cannot use this feature.
                    Toast.makeText(this, "Permissions was not granted", Toast.LENGTH_SHORT).show();
                }
                break;
            case 1:
                // Receive permission result from storage permission.

                // check if storage permission has been granted
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // storage permission has been granted, user can select a photo from gallery
                    dispatchPickPictureIntent();
                }
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mFireBaseAuth.addAuthStateListener(mFireBaseAuthListener);

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mFireBaseAuthListener != null ) {
            mFireBaseAuth.removeAuthStateListener(mFireBaseAuthListener);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return  true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (data != null) {

              Bundle extras = data.getExtras();
              Bitmap imageBitmap = (Bitmap) extras.get("data");

              Glide.with(getApplicationContext())
                      .load(imageBitmap)
                      .apply(RequestOptions.circleCropTransform().centerCrop())
                      .into(mUserProfileImg);

                if (imageBitmap != null) {
                    selectedImageUri = Utils.getImageUri(this, imageBitmap);
                }
            }
        }

        if (requestCode == REQUEST_IMAGE_PICK) {
            if (data != null) {

                selectedImageUri = data.getData();

                Glide.with(getApplicationContext())
                        .load(selectedImageUri)
                        .apply(RequestOptions.circleCropTransform())
                        .into(mUserProfileImg);
            }
        }
    }
}
