package app.yado.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import app.yado.R;

public class Create_Account_Phone_Verification extends AppCompatActivity {

    // Globals
    private static final String TAG = Create_Account_Phone_Verification.class.getSimpleName();

    // Views
    EditText mPhoneNumberEditText;
    Button mContinueButton;
    Toolbar toolbar;
    RelativeLayout mErrorInvalidNumber;

    // vars
    private String validPhoneNumber;
    private String userEmail;
    private String userPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create__account__phone__verification);

        // get intent
        Intent intent = getIntent();
        userEmail = intent.getStringExtra("userEmail");
        userPassword = intent.getStringExtra("userPassword");

        //Find views
        mPhoneNumberEditText = findViewById(R.id.id_phone_number_input);
        mContinueButton = findViewById(R.id.formContinueBtn);
        toolbar = findViewById(R.id.my_toolbar);
        mErrorInvalidNumber = findViewById(R.id.id_error_invalid_number);

        init();
    }

    private void init() {
        Log.d(TAG, "init: initializing");


        // set toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null ) {
            getSupportActionBar().setTitle("Create an Account");
        }

        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNumberValid()) {
                    Intent intent = new Intent(getApplicationContext(), Create_Account_Phone_Verification_Code.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("validPhoneNumber", validPhoneNumber);
                    bundle.putString("userEmail", userEmail);
                    bundle.putString("userPassword", userPassword);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
    }

    private boolean isNumberValid() {
        Boolean isTrue = false;

        Log.d(TAG, "isNumberValid: checking to see if phone number entered is valid");
        String number = mPhoneNumberEditText.getText().toString();

        if(number.isEmpty() || number.length() != 7 ) {
            mErrorInvalidNumber.setVisibility(View.VISIBLE);
        } else {
            if (checkIFValid(number)) {
                mErrorInvalidNumber.setVisibility(View.INVISIBLE);
                isTrue = true;
                validPhoneNumber = number;
            } else {
                mErrorInvalidNumber.setVisibility(View.VISIBLE);
            }
        }


        Toast.makeText(getApplicationContext(), "+664" + number, Toast.LENGTH_SHORT).show();

        return isTrue;
    }

    private boolean checkIFValid(String number) {
        String MNINUMBER_PATTERN = "([2-4][9][2-6])(\\d{4})$";
        Pattern pattern = Pattern.compile(MNINUMBER_PATTERN);
        Matcher matcher;

        matcher = pattern.matcher(number);

        return matcher.matches();
    }
}
