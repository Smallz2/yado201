package app.yado.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.yado.Models.TaskDoer;
import app.yado.Models.TaskInfo;
import app.yado.Models.userModel;
import app.yado.R;
import app.yado.view_user_profile;

public class TaskApplicantListAdapter extends RecyclerView.Adapter<TaskApplicantListAdapter.ViewHolder> implements View.OnClickListener {

    // globals

    // vars
    private ArrayList<TaskDoer> mUserList;
    private ArrayList<userModel> mUserInfo;
    private Context mContext;
    private View v;
    private static String taskID;
    private TaskDoer mTaskDoers;
    private userModel mTaskDoerInfor;
    private static final String TAG = TaskApplicantListAdapter.class.getSimpleName();

    //views

    public TaskApplicantListAdapter(Context context, ArrayList<TaskDoer> userModel, ArrayList<userModel> userInformation, String taskID) {
        this.mUserList = userModel;
        this.mContext = context;
        this.mUserInfo = userInformation;
        TaskApplicantListAdapter.taskID = taskID;
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    @Override
    public void onClick(View v) {

    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        ImageView applicantPhotoImg;
        TextView applicantName;
        Button acceptBtn, declineBtn, contactBtn;

        ViewHolder(View itemView) {
            super(itemView);

            cv = itemView.findViewById(R.id.id_applicantCardView);
            applicantPhotoImg = itemView.findViewById(R.id.id_applicantPhoto);
            applicantName = itemView.findViewById(R.id.id_applicantName);
            acceptBtn = itemView.findViewById(R.id.id_acceptApplicant);
            declineBtn = itemView.findViewById(R.id.id_cancelApplicant);
            contactBtn = itemView.findViewById(R.id.id_contactApplicant);

            // check if task is in progress and allow user to contact applicant
            checkIfInProcess(acceptBtn, declineBtn, contactBtn);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_applicant_card, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        mTaskDoerInfor = mUserInfo.get(position); // todo : this causes app to crash : look into it

        holder.applicantName.setText(mTaskDoerInfor.getUserName());

        Glide.with(mContext)
                .load(mTaskDoerInfor.getUserPhotoUrl())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.applicantPhotoImg);


        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create bottom sheet to show user profile
                createViewProfileBottomSheet();
            }
        });

        holder.acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Accept applicant
                acceptApplicantDialog(holder.itemView.getContext());
            }
        });

        holder.declineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Decline applicant
                declineApplicantDialog(holder.itemView.getContext());
            }
        });
    }

    /**
     * Create bottom sheet to show clicked on user profile information
     */
    private void createViewProfileBottomSheet() {
        Log.d(TAG, "createViewProfileBottomSheet: showing clicked on user profile information");

        Intent intent =  new Intent(mContext, view_user_profile.class);
        intent.putExtra("userID", mTaskDoerInfor.getUserID());
        mContext.startActivity(intent);


    }

    private void acceptApplicantDialog(Context context) {
        Log.d(TAG, "acceptApplicantDialog: launching accept dialog");

        // launch accept dialog

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Accept applicant");
        alertDialog.setMessage("Do you want to accept this applicant?");
        alertDialog.setPositiveButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.setNegativeButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                acceptApplicant();
            }
        });

        AlertDialog dialog = alertDialog.create();
        dialog.show();
    }

    private void acceptApplicant() {
        Log.d(TAG, "acceptApplicant: accepting applicant");

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("taskList/" + taskID + "/Doers");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        if (dataSnapshot1.exists()) {
                            // delete all other applicants except accepted applicant
                            TaskDoer taskDoer = dataSnapshot1.getValue(TaskDoer.class);

                            if (taskDoer != null ) {
                                DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("taskList/" + taskID + "/Doers/" + taskDoer.getUID());
                                changeTaskStatus();

                                if (!taskDoer.getUID().equals(mTaskDoerInfor.getUserID())) {
                                   ref2.removeValue();

                                    Toast.makeText(mContext, "accepted", Toast.LENGTH_SHORT).show();
                                    TaskApplicantListAdapter.this.notify();
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // handle error message
            }
        });
    }

    private void declineApplicantDialog(Context context) {
        Log.d(TAG, "acceptApplicantDialog: launching decline dialog");

        // launch accept dialog

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Decline applicant");
        alertDialog.setMessage("Do you want to decline this applicant?");
        alertDialog.setPositiveButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.setNegativeButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                declineApplicant();

            }
        });

        AlertDialog dialog = alertDialog.create();
        dialog.show();
    }

    private void declineApplicant() {
        Log.d(TAG, "declineApplicant: declining applicant");


        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("taskList/" + taskID + "/Doers");

        final Query query = ref
                .orderByChild("UID")
                .equalTo(mTaskDoerInfor.getUserID());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        if (dataSnapshot1.exists()) {
                            TaskDoer taskDoer = dataSnapshot1.getValue(TaskDoer.class);

                            if (taskDoer != null) {
                                DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("taskList/" + taskID + "/Doers/" + taskDoer.getUID());

                                ref2.removeValue();

                                Toast.makeText(mContext, "declined", Toast.LENGTH_SHORT).show();
                                TaskApplicantListAdapter.this.notify();
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // write error message
            }
        });
    }

    private void changeTaskStatus() {
        Log.d(TAG, "changeTaskStatus: changing task status");

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("taskList/" + taskID);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        if (dataSnapshot1.exists()) {
                            // changing status
                            Map<String, Object> map = new HashMap<>();
                            map.put("taskStatus", "progress");
                            databaseReference.updateChildren(map);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // write error message
            }
        });
    }

    private static void checkIfInProcess(final Button acceptBtn, final Button declineBtn, final Button contactBtn) {
        Log.d(TAG, "checkIfProcess: checking if task is in process");

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("taskList");

        Query query = databaseReference
                .orderByChild("taskKey")
                .equalTo(taskID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        if (dataSnapshot1.exists()) {
                            TaskInfo taskInfo = dataSnapshot1.getValue(TaskInfo.class);

                            if (taskInfo != null) {
                                if (taskInfo.getTaskStatus().equals("progress")) {
                                    // allow user to contact applicant
                                    acceptBtn.setVisibility(View.INVISIBLE);
                                    declineBtn.setVisibility(View.INVISIBLE);
                                    contactBtn.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // handle error message
            }
        });

    }
}
