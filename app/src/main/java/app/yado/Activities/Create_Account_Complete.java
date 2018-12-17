package app.yado.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import app.yado.R;

public class Create_Account_Complete extends AppCompatActivity {

    // Globals

    // Views
    Button mContinueBtn;
    Toolbar toolbar;

    // Vars
    private static final String TAG = Create_Account_Complete.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create__account__complete);

        //Find views
        toolbar = findViewById(R.id.my_toolbar);
        mContinueBtn = findViewById(R.id.formContinueBtn);

        init();
    }

    private void init() {
        Log.d(TAG, "init: initializing");

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Create an Account");
        }

        mContinueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
