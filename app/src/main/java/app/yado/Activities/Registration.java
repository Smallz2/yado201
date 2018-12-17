package app.yado.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;

import java.util.Timer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import app.yado.R;

public class Registration extends AppCompatActivity implements View.OnClickListener {

    // Global Variables

    //vars
    boolean isEmailValid;
    boolean isPasswordValid;
    boolean canUserContinue;
    private Timer _timer = new Timer();

    // FireBase and Google
    private FirebaseAuth mFireBaseAuth;
    public FirebaseUser mFireBaseUser;

    Toolbar toolbar;

    // Text Views
    TextView signInBtn;

    // Buttons
    Button formContinueBtn;

    // TextInputLayout
    TextInputLayout mEmailWrapper, mPasswordWrapper;

    // EditText
    EditText  mEmail, mPassword;

    //Check Box
    CheckBox mTermsCheckBox;

    // Others
    Context mContext;
    final static  String TAG = Registration.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mFireBaseAuth = FirebaseAuth.getInstance();

        // Get Views
        signInBtn = findViewById(R.id.signInButton);
        formContinueBtn = findViewById(R.id.formContinueBtn);
        mEmailWrapper = findViewById(R.id.formEmailWrapper);
        mPasswordWrapper = findViewById(R.id.formPasswordWrapper);
        mEmail = findViewById(R.id.formEmail);
        mPassword = findViewById(R.id.formPassword);
        toolbar = findViewById(R.id.my_toolbar);
        mTermsCheckBox = findViewById(R.id.id_terms_check_box);

        mContext = getApplicationContext();

        init();
    }

    private void init() {
        Log.d(TAG, "init: initializing");

        // Setting onClickListener to elements
        signInBtn.setOnClickListener(this);
        formContinueBtn.setOnClickListener(this);

        // Set ToolBar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Create Account");

            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        userInformationActiveChecking(); // checks user email input
    }

    /**
     * Checking actively to see if user information is valid, email, password and accepting terms & agreement
     */
    private void userInformationActiveChecking() {
        isEmailValid = false;
        isPasswordValid = false;
        canUserContinue = false;

        // Email Authentication Check
        mEmail.addTextChangedListener(new TextWatcher() {
            // check is email matches the email regex email standard
            String EMAIL_PATTERN = "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$";
            Pattern pattern = Pattern.compile(EMAIL_PATTERN);
            Matcher matcher;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 5) {
                    matcher = pattern.matcher(mEmail.getText().toString());
                    if (!matcher.matches()) {
                        // email not valid
                        mEmail.setCompoundDrawables( null, null, null, null );
                        mEmailWrapper.setError("Email not valid");
                        isEmailValid = false;
                    } else {
                        Drawable img = getResources().getDrawable( R.drawable.correct_tick );
                        img.setBounds( 0, 0, 60, 60 );
                        mEmail.setCompoundDrawables( null, null, img, null );
                        mEmailWrapper.setErrorEnabled(false);
                        isEmailValid = true;
                    }

                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (isEmailValid && isPasswordValid) {
                    canUserContinue = true;
                    formContinueBtn.setClickable(true);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        formContinueBtn.setBackground(getResources().getDrawable(R.drawable.material_button_enable));
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        formContinueBtn.setBackground(getResources().getDrawable(R.drawable.material_button_disable));
                    }
                }
            }
        });


        // Password Authentication Check
        mPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() <= 4) {
                    mPasswordWrapper.setError("Password must be 5 characters or more");
                    isPasswordValid = false;
                } else {
                    mPasswordWrapper.setErrorEnabled(false);
                    isPasswordValid = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isEmailValid && isPasswordValid) {
                    canUserContinue = true;
                    formContinueBtn.setClickable(true);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        formContinueBtn.setBackground(getResources().getDrawable(R.drawable.material_button_enable));
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        formContinueBtn.setBackground(getResources().getDrawable(R.drawable.material_button_disable));
                    }
                }
            }
        });
    }


    /**
     * checks to see if a string is empty
     * @param string: string to be checked
     * @return true of false
     */
    private boolean isStringNull(String string) {
        Log.d(TAG, "isStringNull: checking if string is null");

        return string.equals("");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signInButton:
                Intent intent = new Intent (mContext, LoginActivity.class);
                startActivity(intent);
                break;

            case R.id.formContinueBtn:
                if (canUserContinue && mTermsCheckBox.isChecked()) {
                    formContinue();
                }
                break;
        }
    }

    public void formContinue() {
        Log.d(TAG, "formContinue: continuing user registration, checking if email is available");

        final  String email = mEmail.getText().toString();
        final String password = mPassword.getText().toString();

        // Create new user if non-existing already
        mFireBaseAuth.fetchProvidersForEmail(email)
                .addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                        boolean result = !task.getResult().getProviders().isEmpty();
                        if (!result) {
                            // Email Not found
                            Log.d(TAG, "onComplete: start phone verification activity");
                            // continue registration
                            //Intent intent = new Intent(mContext, Create_Account_Phone_Verification.class);
                            Intent intent = new Intent(mContext, Create_Account_User_Information.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("userEmail", email);
                            bundle.putString("userPassword", password);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        } else {
                            //Email Found
                            mEmailWrapper.setError("Email Already In Use");
                            mEmail.setCompoundDrawables( null, null, null, null );
                        }
                    }
                });
    }

    /**
     * handles errors for inputTextLayout
     * @param array: contains user's information
     */

    public boolean inputTextErrorHanding(String[] array) {
        Log.d(TAG, "inputTextErrorHanding: handling errors");

        int true_count = 0;

        // Email Matching
        String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher;

        TextInputLayout[] inputWrapperArray = {mEmailWrapper, mPasswordWrapper};

        // Email Field
        if(isStringNull(array[0])) {
            inputWrapperArray[0].setError("This field can't be blank");
        } else {
            inputWrapperArray[0].setErrorEnabled(false);
            matcher = pattern.matcher(array[0]);
            if (!matcher.matches()) {
                inputWrapperArray[0].setError("Not a valid email");
            } else {
                inputWrapperArray[0].setErrorEnabled(false);
                true_count = true_count + 1;
            }
        }

        // Password Field
        if(isStringNull(array[1])) {
            inputWrapperArray[1].setError("This field can't be blank");
        } else {
            inputWrapperArray[1].setErrorEnabled(false);
            if (array[1].length() < 5) {
                inputWrapperArray[1].setError("Password too short");
            } else {
                inputWrapperArray[1].setErrorEnabled(false);
                true_count = true_count + 1;
            }
        }
        return true_count == 2;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public boolean clickTermsAgreeEvent(View view) {
        return true;
    }
}
