package app.yado.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

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

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import app.yado.Activities.Task_Details;
import app.yado.Models.TaskInfo;
import app.yado.Models.userModel;
import app.yado.R;

public class HomeTaskListAdapter extends RecyclerView.Adapter<HomeTaskListAdapter.ViewHolder> {


    //Globals
    private ArrayList<TaskInfo> taskList;
    private Context mContext;
    private TaskInfo mTaskInfo;
    private userModel mUserInfo;
    private static final String TAG = HomeTaskListAdapter.class.getSimpleName();
    private View v;

    //Vars


    //FireBase
    FirebaseAuth mFireBaseAuth = FirebaseAuth.getInstance();
    FirebaseUser mFireBaseUser = mFireBaseAuth.getCurrentUser();

    public HomeTaskListAdapter(Context context, ArrayList<TaskInfo> taskList) {
        this.taskList = taskList;
        this.mContext = context;
    }


    @Override
    public int getItemCount() {
        return taskList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView taskTitle, taskDetails, taskAmountAwarded, taskStartDate, taskStartTime, taskAuthorName;
        ImageView taskAuthorImg;

        ViewHolder(View itemView) {
            super(itemView);

            cv = itemView.findViewById(R.id.id_cardView);
            taskTitle = itemView.findViewById(R.id.id_taskTitle);
            taskDetails = itemView.findViewById(R.id.id_taskDetails);
            taskAmountAwarded = itemView.findViewById(R.id.id_amountAwarded);
            taskStartDate = itemView.findViewById(R.id.id_taskStartDate);
            taskStartTime = itemView.findViewById(R.id.id_startTime);
            taskAuthorImg = itemView.findViewById(R.id.id_taskAuthorImg);
            taskAuthorName = itemView.findViewById(R.id.id_taskAuthorName);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_card_layout, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        mTaskInfo = taskList.get(position);
        String taskAmount  = mTaskInfo.getTaskAwardAmount();

        //setFadeAnimation(holder.itemView);

        getTaskAuthor(new userModel.userObjectInterface() {
            @Override
            public void onCallBack(userModel userModel) {
                mUserInfo = userModel;

                Log.d(TAG, "onCallBack: set author information to viewHolder");

                // set user info to viewHolder
                holder.taskAuthorName.setText(mUserInfo.getUserName());

                Glide.with(mContext)
                        .load(mUserInfo.getUserPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(holder.taskAuthorImg);
            }
        });

        holder.taskTitle.setText(mTaskInfo.getTaskTitle());
        holder.taskDetails.setText(mTaskInfo.getTaskDescription());
        holder.taskAmountAwarded.setText(taskAmount);
        holder.taskStartDate.setText(mTaskInfo.getTaskStartDate());
        holder.taskStartTime.setText(mTaskInfo.getTaskStartTime());

       // Glide.with(holder.taskAuthorImg.getContext()).load(mTaskInfo.g)

        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, Task_Details.class);
                intent.putExtra("taskPosition", position);
                intent.putExtra("taskList", taskList);
                mContext.startActivity(intent);
            }
        });
    }

    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(500);
        view.startAnimation(anim);
    }


    private void getTaskAuthor(final userModel.userObjectInterface userObjectInterface) {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.keepSynced(true);

        final String userID = mTaskInfo.getAuthorUID();

        Query query = databaseReference
                .orderByChild("userID")
                .equalTo(userID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    if (dataSnapshot1.exists()) {
                        Log.d(TAG, "onDataChange: author found");

                        userModel user = dataSnapshot1.getValue(userModel.class);

                        userObjectInterface.onCallBack(user);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void clear () {
        taskList.clear();
        notifyDataSetChanged();
    }
}
