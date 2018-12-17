package app.yado.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import app.yado.R;

public class Create_Account_Phone_Verification_Code extends AppCompatActivity {

    // Globals


    // view
    Toolbar toolbar;
    TextView mPhoneNumberText;
    Button mContinueButton;


    // vars
    private String validPhoneNumber;
    private String userEmail;
    private String userPassword;
    private PhoneAuthProvider.ForceResendingToken mResenToken;
    private String mVerificationId;

    private static final String TAG = Create_Account_Phone_Verification_Code.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create__account__phone__verification__code);

        //get intent
        Intent intent = getIntent();
        validPhoneNumber = "+1664" + intent.getStringExtra("validPhoneNumber");
        userEmail = intent.getStringExtra("userEmail");
        userPassword = intent.getStringExtra("userPassword");


        // Find views
        toolbar = findViewById(R.id.my_toolbar);
        mPhoneNumberText = findViewById(R.id.id_phoneNumber);
        mContinueButton = findViewById(R.id.formContinueBtn);

        init();
    }

    private void init() {
        Log.d(TAG, "initializing");

        // set supportAction toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Create an Account");
        }

        // set phone number
        mPhoneNumberText.setText(validPhoneNumber);

        sendVerificationCode();

        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo : check if code is correct before continuing
                Intent intent = new Intent(getApplicationContext(), Create_Account_User_Information.class);
                Bundle bundle = new Bundle();
                bundle.putString("userEmail", userEmail);
                bundle.putString("userPassword", userPassword);
                bundle.putString("userPhoneNumber", validPhoneNumber);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

    }

    private void sendVerificationCode() {
        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;

        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                // sign in user?

                Log.d(TAG, "onVerificationCompleted: phone verified");
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.w(TAG, "onVerificationFailed:" + e);

                // more error checks
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota has been exceeded , increase plan
                }
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent: " + verificationId);
                Log.d(TAG, "validNumber:" +validPhoneNumber);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResenToken = token;
            }
        };

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                validPhoneNumber,
                60,
                TimeUnit.SECONDS,
                Create_Account_Phone_Verification_Code.this,
                mCallback
        );


    }
}
