package app.yado.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import app.yado.R;

public class reset_password extends AppCompatActivity implements View.OnClickListener {

    // FireBase and Google
    private FirebaseAuth mFireBaseAuth;
    private FirebaseUser mFireBaseUser;

    //Buttons
    Button mPasswordResetButton;

    //TextInputLayout
    TextInputLayout mFormResetPasswordEmailWrapper;

    //TextView
    EditText mFormResetPasswordEmail;

    //others
    private static final String TAG = "Reset_Password_Activity";
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mContext = getApplicationContext();

        mFireBaseAuth = FirebaseAuth.getInstance();

        mFormResetPasswordEmailWrapper = findViewById(R.id.resetPassswordEmailWrapper);
        mFormResetPasswordEmail = findViewById(R.id.resetPassswordEmail);
        mPasswordResetButton = findViewById(R.id.passwordResetButton);

        mPasswordResetButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.passwordResetButton:
                resetPassword();
                break;
        }
    }

    private void resetPassword() {
        String mUserEmail = mFormResetPasswordEmail.getText().toString();

        if (inputTextErrorHanding(mUserEmail)) {
            mFireBaseAuth.sendPasswordResetEmail(mUserEmail)
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Log.d(TAG, "Password Reset Email Sent");
                                Intent intent = new Intent(mContext, reset_password_done.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Log.w(TAG, task.getException());

                                // No user found with the provided email
                                mFormResetPasswordEmailWrapper.setError("Email not found");
                            }
                        }
                    });
        }
    }

    private boolean inputTextErrorHanding(String email) {
        Boolean mCheckingBoolean = false;

        // Email Matching
        String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher;

        if (email.equals("")) {
            mFormResetPasswordEmailWrapper.setError("This field can't be blank");
        } else {
            mFormResetPasswordEmailWrapper.setErrorEnabled(false);
            matcher = pattern.matcher(email);
            if (!matcher.matches()) {
                mFormResetPasswordEmailWrapper.setError("Not a valid email");
            } else {
                mFormResetPasswordEmailWrapper.setErrorEnabled(false);
                mCheckingBoolean = true;
            }
        }
        return mCheckingBoolean;
    }
}
