//package app.yado.Fragments;
//
//import android.content.Context;
//import android.net.Uri;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.v4.app.Fragment;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import app.yado.Adapters.TaskApplicantListAdapter;
//import app.yado.Models.TaskDoer;
//import app.yado.Models.TaskInfo;
//import app.yado.Models.userModel;
//import app.yado.R;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.Query;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;
//
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//public class Task_applicants_Fragment extends Fragment {
//
//    //Vars
//    private static final String TAG = Task_applicants_Fragment.class.getSimpleName();
//    private int page;
//    private static Context mContext;
//    private static TaskInfo mTaskInfo;
//    private ArrayList<TaskDoer> mTaskDoerList;
//    private ArrayList<userModel> mUserList;
//
//
//    //Views
//    private RecyclerView recyclerView;
//    private RecyclerView.Adapter mRecyclerViewAdapter;
//    private LinearLayoutManager mLayoutManager;
//    private TextView mNoAppplicantText;
//
//    public Task_applicants_Fragment() {
//        // Required empty public constructor
//    }
// // todo : fragment manager problem
//    public static Task_applicants_Fragment newInstance(int page, Context context, TaskInfo taskInfo) {
//        Task_applicants_Fragment fragment = new Task_applicants_Fragment();
//        Bundle args = new Bundle();
//        args.putInt("int", page);
//        fragment.setArguments(args);
//
//        mContext = context;
//        mTaskInfo = taskInfo;
//
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            page = getArguments().getInt("int", 0);
//        }
//
//        mTaskDoerList = new ArrayList<>(); // init taskDoers object
//        mUserList = new ArrayList<>(); // init userModel object
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_task_applicants_, container, false);
//
//        recyclerView = view.findViewById(R.id.id_taskApplicantRecyclerView);
//        mNoAppplicantText = view.findViewById(R.id.id_taskNoApplicantText);
//
//        // setup recycler view
//        mLayoutManager = new LinearLayoutManager(mContext);
//        recyclerView.setLayoutManager(mLayoutManager);
//
////        mRecyclerViewAdapter = new TaskApplicantListAdapter(mContext, mTaskDoerList, mUserList);
////        recyclerView.setAdapter(mRecyclerViewAdapter);
//
//        getTaskDoers();
//
//        if (mTaskDoerList != null) {
//            mNoAppplicantText.setVisibility(View.INVISIBLE);
//        }
//
//        return view;
//    }
//
//
//    private void getTaskDoers() {
//        Log.d(TAG, "getTaskDoers: getting task doers");
//
//        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("taskList/" + mTaskInfo.getTaskKey() + "/Doers");
//
//        Query query = reference
//                .orderByChild("UID")
//                .limitToFirst(10);
//
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
//                        if (dataSnapshot1.exists()) {
//                            TaskDoer taskDoer = dataSnapshot1.getValue(TaskDoer.class);
//
//                            mTaskDoerList.add(taskDoer);
//
//                            getTaskDoerInfromation(taskDoer.getUID());
//
//                            Log.d(TAG, "" + mUserList);
//
//                           // mRecyclerViewAdapter.notifyDataSetChanged();
//
//                            // get user information from "users"
//
//
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
//
//    private void getTaskDoerInfromation(String UID) {
//        Log.d(TAG, "getTaskDoerInformation: getting task doer information");
//
//        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
//
//        Query  query = databaseReference
//                .orderByChild("userID")
//                .equalTo(UID);
//
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
//                        if (dataSnapshot1.exists()) {
//                            userModel userModel = dataSnapshot1.getValue(userModel.class);
//
//                            mUserList.add(userModel);
//                            mRecyclerViewAdapter.notifyDataSetChanged();
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
//}
