package app.yado.Activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import app.yado.Adapters.HomeTaskListAdapter;
import app.yado.Models.TaskInfo;
import app.yado.R;
import app.yado.Utils.Utils;

public class User_Task_Activity extends AppCompatActivity {

    // Global Variables
    private final static String TAG = User_Task_Activity.class.getSimpleName();
    Context mContext;
    private ArrayList<TaskInfo> mTaskList;
    FirebaseUser mFireBaseUser;
    private String currentUserID;

    // Views
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout mNoTaskMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__task_);

        mContext = getApplicationContext();

        // Find views
        bottomNavigationView = findViewById(R.id.bottom_nav);
        toolbar = findViewById(R.id.my_toolbar);
        mRecyclerView = findViewById(R.id.id_userTaskRecyclerView);
        swipeRefreshLayout = findViewById(R.id.id_swipe_refresh);
        mNoTaskMessage = findViewById(R.id.id_notTaskMessage);

        init();
    }

    private void init(){
        Log.d(TAG, "init: initializing");

        mFireBaseUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUserID = mFireBaseUser.getUid();

        // set toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My Tasks");
        }

        // Navigation Bar Activity Switcher
        Utils.navigationItemSelector(mContext, bottomNavigationView, TAG);
        bottomNavigationView.setSelectedItemId(R.id.action_nav_user_task); // not the best way to do this

        mTaskList = new ArrayList<>();

        mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new HomeTaskListAdapter(mContext, mTaskList);
        mRecyclerView.setAdapter(mAdapter);

        // swipe to refresh
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // refresh home screen

                mTaskList.clear();
                mAdapter.notifyDataSetChanged();

                getUserTaskCreated();
                getUserTaskedAppliedFor();

                swipeRefreshLayout.setRefreshing(false);
            }
        });

        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // get user created and user applied for tasks
        getUserTaskCreated();
        getUserTaskedAppliedFor();

    }

    private void getUserTaskCreated() {
        Log.d(TAG, "getUserTask: getting user created tasks" );

        String userID = mFireBaseUser.getUid();

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("taskList");

        Query query = databaseReference
                .orderByChild("authorUID")
                .equalTo(userID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        if (dataSnapshot1.exists()) {
                            TaskInfo taskInfo = dataSnapshot1.getValue(TaskInfo.class);

                            mTaskList.add(taskInfo);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // todo: look over this function : its kinda inefficient;
    private void getUserTaskedAppliedFor() {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("taskList");

        Query query = reference
                .orderByChild("Doers")
                .limitToFirst(5);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        if (dataSnapshot1.exists()) {
                            final String taskKey = dataSnapshot1.getKey();


                            String doerLocation = "/taskList/" + taskKey + "/Doers";
                            final DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference(doerLocation);

                            final TaskInfo taskInfo = dataSnapshot1.getValue(TaskInfo.class);

                            Query query1 = reference1
                                    .orderByChild("UID")
                                    .equalTo(currentUserID);

                            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()) {
                                            if (dataSnapshot2.exists()) {
                                                mTaskList.add(taskInfo);

                                                if (mNoTaskMessage.getVisibility() == View.VISIBLE) {
                                                    mNoTaskMessage.setVisibility(View.INVISIBLE);
                                                }
                                            }
                                        }
                                    } else {
                                        if (mTaskList.size() == 0 ) {
                                            mNoTaskMessage.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
