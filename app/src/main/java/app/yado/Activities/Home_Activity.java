package app.yado.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import app.yado.Adapters.HomeTaskListAdapter;
import app.yado.EndlessRecyclerViewScrollListener;
import app.yado.Models.TaskInfo;
import app.yado.Models.userModel;
import app.yado.R;
import app.yado.Utils.Utils;

public class Home_Activity extends AppCompatActivity implements View.OnClickListener {

    // Global Variables
    private final static String TAG = Home_Activity.class.getSimpleName();

    //vars
    private Context mContext;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView.Adapter mRecyclerViewAdapter;
    private ArrayList<TaskInfo> mTaskList;
    private ArrayList<userModel> mUserList;
    private userModel mUserModel;
    private EndlessRecyclerViewScrollListener endlessScrollListener;
    private String mLastTaskKey;
    private int resID;

    Toolbar mToolBar;

    // Views
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton mFab;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private TextView mToolBarGreeting, mToolbarUserName, mToolbarCurrentDate;
    private ImageView mToolbarUserImage, mToolbarCurrentWeather;
    private LinearLayout mNoTaskMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_);

        mContext = getApplicationContext();

        // Find views
        bottomNavigationView = findViewById(R.id.bottom_nav);
        mFab = findViewById(R.id.floatingActionButton);
        mRecyclerView = findViewById(R.id.id_recyclerView);
        mToolBar = findViewById(R.id.my_toolbar);
        swipeRefreshLayout = findViewById(R.id.id_home_swipe_refresh);
        mToolbarUserImage = findViewById(R.id.id_user_profile_img);
        mToolbarUserName = findViewById(R.id.id_user_name);
        mToolBarGreeting = findViewById(R.id.id_user_greeting);
        mToolbarCurrentDate = findViewById(R.id.id_current_date);
        mToolbarCurrentWeather = findViewById(R.id.id_current_weather);
        resID = R.anim.layout_animation_fall_down;
        mNoTaskMessage = findViewById(R.id.id_notTaskMessage);

        setSupportActionBar(mToolBar);
        init();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.floatingActionButton:
                Intent intent = new Intent(mContext, Create_Task.class);
                startActivity(intent);
                //finish();
                break;
        }
    }

    private void init() {
        Log.d(TAG, "Init: initializing");

        // set toolbar
        setupHomeToolBar();

        // generate access token
        //generateAccessToken();


        //Navigation Bar Activity Switcher
        Utils.navigationItemSelector(mContext, bottomNavigationView, TAG);

        // Set onClicks
        mFab.setOnClickListener(this);

        mTaskList = new ArrayList<>();

        mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);


        mRecyclerViewAdapter = new HomeTaskListAdapter(mContext, mTaskList);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        loadTasksFromDatabase();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // refresh home screen

                mTaskList.clear();

                mRecyclerViewAdapter.notifyDataSetChanged();

                loadTasksFromDatabase();

                swipeRefreshLayout.setRefreshing(false);
            }
        });

        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // Endless Scroller
        endlessScrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("taskList");

                Query query = reference
                        .orderByKey()
                        .startAt(mLastTaskKey+1)
                        .limitToFirst(10);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                if (dataSnapshot1.exists()) {
                                    TaskInfo taskInfo = dataSnapshot1.getValue(TaskInfo.class);

                                    if (taskInfo.getTaskStatus().equals("open")) {
                                        mLastTaskKey = dataSnapshot1.getKey();
                                        mTaskList.add(taskInfo);
                                        runLayoutAnimation(mRecyclerView);
                                        mRecyclerViewAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        };

        mRecyclerView.addOnScrollListener(endlessScrollListener);
    }

    /**
     * Rerun entrance animation
     * @param recyclerView
     */
    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.scheduleLayoutAnimation();
    }

    private void setupHomeToolBar() {

        FirebaseUser  firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser!=null) { // user present
            // get user image and name
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

            Query query = databaseReference
                    .orderByChild("userID")
                    .equalTo(firebaseUser.getUid());

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            if (dataSnapshot1.exists()) {
                                userModel userModel = dataSnapshot1.getValue(userModel.class);

                                if (userModel != null) {
                                    Glide.with(mContext)
                                            .load(userModel.getUserPhotoUrl())
                                            .apply(RequestOptions.circleCropTransform())
                                            .into(mToolbarUserImage);

                                    mToolbarUserName.setText(userModel.getUserName());
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        // Set Current time with greeting
        Calendar cal = Calendar.getInstance();
        int timeOfDay = cal.get(Calendar.HOUR_OF_DAY);

        if(timeOfDay >= 0 && timeOfDay < 12) {
            mToolBarGreeting.setText("Good Morning,");
        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            mToolBarGreeting.setText("Good Afternoon,");
        } else if (timeOfDay >=16 && timeOfDay < 21) {
            mToolBarGreeting.setText("Good Evening,");
        } else if (timeOfDay >=21 && timeOfDay < 24) {
            mToolBarGreeting.setText("Good Night,");
        }

        // set current date
        int month = cal.get(Calendar.MONTH);
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

        String fullDate = findMonthName(month)  + ", " + dayOfMonth;

        mToolbarCurrentDate.setText(fullDate);

        // set weather icon

    }


    private void generateAccessToken() {
        // Get token
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast

                        Toast.makeText(Home_Activity.this, token, Toast.LENGTH_SHORT).show();
                    }
                });

    }


    /**
     * Function that find the month name
     * @param month month index provided
     * @return month short name
     */
    private String findMonthName (int month) {
        Log.d(TAG, "findMonthName: finding month name");

        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        return months[month];
    }

    private void loadTasksFromDatabase() {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("taskList");
        reference.keepSynced(true);

        reference.keepSynced(true);

        Query query = reference
                .orderByChild("taskStatus")
                .limitToFirst(10)
                .equalTo("open");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        if (dataSnapshot1.exists()) {
                            TaskInfo taskInfo = dataSnapshot1.getValue(TaskInfo.class);
                            mLastTaskKey = dataSnapshot1.getKey();
                            mTaskList.add(taskInfo);
                            runLayoutAnimation(mRecyclerView);
                            mRecyclerViewAdapter.notifyDataSetChanged();

                            if (mNoTaskMessage.getVisibility() == View.VISIBLE) {
                                mNoTaskMessage.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                } else {
                    if (mTaskList.size() == 0) {
                        mNoTaskMessage.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOption: inflating home_toolbar_options");

        getMenuInflater().inflate(R.menu.home_toolbar_options, menu);

        return true;
    }

    @Override
    protected void onResume() {
//        // update UI by loading task information again
//        mTaskList.clear();
//
//        mRecyclerViewAdapter.notifyDataSetChanged();
//
//        loadTasksFromDatabase();
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.id_homeSearch) {
            Toast.makeText(mContext, "Search", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);

    }
}
