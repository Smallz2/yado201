package app.yado.Activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import app.yado.R;
import app.yado.Utils.Utils;

public class Notification_Activity extends AppCompatActivity {

    // Global Variables
    private final static String TAG = Notification_Activity.class.getSimpleName();
    Context mContext;

    // Views
    BottomNavigationView bottomNavigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_);


        mContext = getApplicationContext();

        // Find views
        bottomNavigationView = findViewById(R.id.bottom_nav);
        toolbar = findViewById(R.id.my_toolbar);

        init();
    }

    private void init() {
        Log.d(TAG, "init: initializing");

        // set toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Notification");
        }

        // Disable navbar shifting. navBar starts to shift with more than 3 items.
       // Utils.disableShiftMode(bottomNavigationView);

        // Navigation Bar Activity Switcher
        Utils.navigationItemSelector(mContext, bottomNavigationView, TAG);
        bottomNavigationView.setSelectedItemId(R.id.action_nav_notification); // not the best way to do this
    }
}
