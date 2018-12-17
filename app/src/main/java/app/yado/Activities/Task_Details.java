package app.yado.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import app.yado.Adapters.TaskApplicantListAdapter;
import app.yado.Models.TaskDoer;
import app.yado.Models.TaskInfo;
import app.yado.Models.userModel;
import app.yado.R;
import app.yado.view_user_profile;

public class Task_Details extends AppCompatActivity {
    private static final String TAG = Task_Details.class.getSimpleName();

    // Globals

    private TextView mTaskStatus;
    private TextView mTaskApplyBtn;
    private TextView mTaskAuthorUserName;
    private RecyclerView mTaskApplicantRecyclerView;
    private RecyclerView.Adapter mRecyclerViewAdapter;
    private ImageView mTaskAuthorImg;

    //vars
    ArrayList<TaskInfo> taskList;
    private ArrayList<TaskDoer> mTaskDoerList;
    private ArrayList<userModel> mUserList;
    int taskPosition;
    private Menu optionMenu;
    TaskInfo mTaskInfo;
    FirebaseUser mFireBaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task__details);

        init();
    }

    private void init() {
        Log.d(TAG, "init: initializing");

        mTaskDoerList = new ArrayList<>(); // init taskDoer object
        mUserList = new ArrayList<>(); // init userModel object

        // Find ViewsTask
        //views
        Toolbar mToolBar = findViewById(R.id.my_toolbar);
        setSupportActionBar(mToolBar);


        if (getSupportActionBar() != null ) {
            getSupportActionBar().setTitle("Task Details");

            mToolBar.setOverflowIcon(getResources().getDrawable(R.drawable.ic_more_vert_black_24dp));

            mToolBar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
            mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        taskList = (ArrayList<TaskInfo>) getIntent().getSerializableExtra("taskList");
        taskPosition = getIntent().getIntExtra("taskPosition", 0);

        mTaskInfo = taskList.get(taskPosition);
        String taskAwardPrice = "$ " + mTaskInfo.getTaskAwardAmount();

        mFireBaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // find views
        TextView mTaskTitle = findViewById(R.id.id_taskTitle);
        TextView mTaskDetails = findViewById(R.id.id_taskDescription);
        mTaskApplyBtn = findViewById(R.id.id_taskApplyBtn);
        TextView mTaskStartDateText = findViewById(R.id.id_taskStartDate);
        TextView mTaskStartTimeText = findViewById(R.id.id_taskStartTime);
       // mTaskLocationText =    findViewById(R.id.id_taskLocation);
        TextView mTaskAmountAwarded = findViewById(R.id.task_amountAwarded);
        mTaskApplicantRecyclerView = findViewById(R.id.id_taskApplicantRecyclerView);
        mTaskAuthorImg = findViewById(R.id.id_user_profile_img);
        mTaskAuthorUserName = findViewById(R.id.id_user_user_name);
        RelativeLayout mApplicantsContainer = findViewById(R.id.id_applicantContainer);
        TextView mTaskLocation = findViewById(R.id.id_taskLocation);
        RelativeLayout mAuthorProfileWrapper = findViewById(R.id.id_authorProfileWrapper);

        // set task details information
        mTaskAmountAwarded.setText(taskAwardPrice);
        mTaskTitle.setText(mTaskInfo.getTaskTitle());
        mTaskDetails.setText(mTaskInfo.getTaskDescription());
        mTaskStartDateText.setText(mTaskInfo.getTaskStartDate());
        mTaskStartTimeText.setText(mTaskInfo.getTaskStartTime());
        mTaskLocation.setText(mTaskInfo.getTaskLocationAddress());

        // set task author information
        getTaskAuthorInformation(mTaskInfo);

        // get task doers : if any
        getTaskDoers();

        // setup recycler view
        setUpRecyclerView();

        // check is current user is a doer of the task
        isDoer(mFireBaseUser.getUid());

        // if user is author don't show apply btn
        if (!(mFireBaseUser.getUid().equals(mTaskInfo.getAuthorUID()))) {
            mTaskApplyBtn.setVisibility(View.VISIBLE);
            //mTaskApplicantRecyclerView.setVisibility(View.VISIBLE);
            mApplicantsContainer.setVisibility(View.INVISIBLE);

            mTaskApplyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    applyForTask();
                }
            });
        } else {
            mTaskApplicantRecyclerView.setVisibility(View.VISIBLE);
        }

        // check if task is in progress
        checkIFInProgress(mTaskInfo);

        // allows users to go to task author's profile screen
        mAuthorProfileWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (getApplicationContext(), view_user_profile.class);
                intent.putExtra("userID", mTaskInfo.getAuthorUID());
                startActivity(intent);
            }
        });
    }

    /**
     * Function to setup recycler view
     */
    private void setUpRecyclerView () {
        // setup recycler view
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mTaskApplicantRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerViewAdapter = new TaskApplicantListAdapter(getApplicationContext(), mTaskDoerList, mUserList, mTaskInfo.getTaskKey());
        mTaskApplicantRecyclerView.setAdapter(mRecyclerViewAdapter);
    }

    private void applyForTask() {
        Log.d(TAG, "applyForTask: setting current user as one of the applicants for task");

        // todo: send notification to task owner that someone applied;
        // todo: update screen for user;

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("taskList/" + mTaskInfo.getTaskKey() + "/Doers");
        String key = databaseReference.push().getKey();

        String timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));

        TaskDoer taskDoer = new TaskDoer();
        taskDoer.setUID(mFireBaseUser.getUid());
        taskDoer.setTimeStamp(timeStamp);

        Map<String, Object> childrenUpdate = new HashMap<>();
        childrenUpdate.put(taskDoer.getUID(), taskDoer.toFireBase()); // todo: find a way to do this better

        databaseReference.updateChildren(childrenUpdate, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError == null) {
                    Log.d(TAG, "onComplete: doer saved to fireBase");
                    // database updated

                    //notificationRequest();
                }
            }
        });

        mTaskApplyBtn.setVisibility(View.INVISIBLE);
    }

//    private void notificationRequest() {
//        String url ="";
//        HttpClient httpClient = new DefaulHttpClient()
//    }

    /**
     * Check if current user is a doer of the task
     * @param UID : User ID of the current user
     * @return : true : if the current users is a doer and false : if not
     */
    private boolean isDoer(String UID) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("taskList/" + mTaskInfo.getTaskKey() + "/Doers");

        Query query = databaseReference
                .orderByChild("UID")
                .equalTo(UID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (final DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        if (dataSnapshot1.exists()) {
                            // current user is a doer

                            mTaskApplyBtn.setVisibility(View.VISIBLE);
                            mTaskApplyBtn.setText("Cancel Application");

                            mTaskApplyBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dataSnapshot1.getRef().removeValue();

                                }
                            });
                        } else {
                            mTaskApplyBtn.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // todo : Handle error
            }
        });
        return true;
    }

    /**
     * Gets all the doers of the task
     */
    private void getTaskDoers() {
        Log.d(TAG, "getTaskDoers: getting task doers");

        final String taskKey = mTaskInfo.getTaskKey();

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("taskList/" + taskKey + "/Doers");
        reference.keepSynced(true);

        Query query = reference
                .orderByChild("UID")
                .limitToFirst(10);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        if (dataSnapshot1.exists()) {
                            TaskDoer taskDoer = dataSnapshot1.getValue(TaskDoer.class);

                            mTaskDoerList.add(taskDoer);

                            if (taskDoer != null) {
                                String UID = taskDoer.getUID();
                                getTaskDoerInfromation(UID);
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

    /**
     * Get task doers' information
     * @param UID : task doers' user ID
     */
    private void getTaskDoerInfromation(String UID) {
        Log.d(TAG, "getTaskDoerInformation: getting task doer information");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.keepSynced(true);

        Query query2 = databaseReference
                .orderByChild("userID")
                .equalTo(UID);


        query2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "working");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    /**
     * get task author's information
     * @param taskInfo : current task data model
     */
    private void getTaskAuthorInformation(TaskInfo taskInfo) {
        Log.d(TAG, "getTaskAuthorInformation: getting task author information");

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

        Query query = databaseReference
                .orderByChild("userID")
                .equalTo(taskInfo.getAuthorUID());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        if (dataSnapshot1.exists()) {
                            userModel userModel = dataSnapshot1.getValue(userModel.class);

                            if (userModel != null) {
                                mTaskAuthorUserName.setText(userModel.getUserName());

                                Glide.with(getApplicationContext())
                                        .load(userModel.getUserPhotoUrl())
                                        .apply(RequestOptions.circleCropTransform())
                                        .into(mTaskAuthorImg);
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

    /**
     * Check if task is in progress and disable editing and deleting
     */
    private void checkIFInProgress(TaskInfo taskInfo) {
        if (taskInfo != null) {
            if (taskInfo.getTaskStatus().equals("progress")) {
                if (optionMenu!=null) {
                    optionMenu.clear();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate the menu; this add items to the action bar if it is present
        Log.d(TAG, "onCreateOptionMenu: inflating task option menu");

        optionMenu = menu;

        //check if current user created the task
        String UID = mFireBaseUser.getUid();

        if (UID.equals(mTaskInfo.getAuthorUID()) && mTaskInfo.getTaskStatus().equals("open")) {
            getMenuInflater().inflate(R.menu.task_option_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle ActionBar item Click
        int id = item.getItemId();

        if (id == R.id.id_taskEdit) {
            Toast.makeText(this, "Edit", Toast.LENGTH_SHORT).show();

            // start edit activity and pass in task information
            Intent intent = new Intent(getApplicationContext(), Edit_Task.class);
            intent.putExtra("taskInformation", mTaskInfo);
            startActivity(intent);
        }

        if (id == R.id.id_deleteTask) {
            Toast.makeText(this, "Delete", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onOptionItemSelected: item delete");

            DatabaseReference fireBaseDatabase = FirebaseDatabase.getInstance().getReference("taskList");
            fireBaseDatabase.child(mTaskInfo.getTaskKey()).removeValue();

            finish();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}


