package app.yado.Activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import app.yado.R;
import app.yado.Utils.Utils;

public class Messages_Activity extends AppCompatActivity {

    // Global Variables
    private final static String TAG = Messages_Activity.class.getSimpleName();
    Context mContext;

    // Views
    BottomNavigationView bottomNavigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_messages_);

        mContext = getApplicationContext();

        // Find views
        bottomNavigationView = findViewById(R.id.bottom_nav);
//        bottomNavigationView.setSelectedItemId(R.id.action_nav_messages);

        init();
    }

    private void init() {
        Log.d(TAG, "init: initializing");

        // set toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Messages");
        }

        //Navigation Bar Activity Switcher
        Utils.navigationItemSelector(mContext, bottomNavigationView, TAG);

    }
}
