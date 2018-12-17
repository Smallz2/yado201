//package app.yado.Fragments;
//
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import app.yado.Models.TaskInfo;
//import app.yado.R;
//
//import java.util.zip.Inflater;
//
//public class Task_Details_Fragment extends Fragment {
//    // vars
//
//    private int page;
//    private static TaskInfo mTaskInfo;
//  todo: fragment manager problem
//
//    // views
//    TextView mTaskAmountAwardText, mTaskStartDateText, mTaskStartTimeText, mTaskLocationText;
//
//    public Task_Details_Fragment() {
//        // Required empty public constructor
//    }
//
//    public static Task_Details_Fragment newInstance(int page, TaskInfo taskInfo) {
//        Task_Details_Fragment fragment = new Task_Details_Fragment();
//        Bundle args = new Bundle();
//        args.putInt("int", page);
//        fragment.setArguments(args);
//
//        mTaskInfo = taskInfo;
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
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View inflater1 = inflater.inflate(R.layout.fragment_task__details_, container, false);
//
//        // find view
//        mTaskAmountAwardText = inflater1.findViewById(R.id.id_taskAmountAwarded);
//        mTaskStartDateText = inflater1.findViewById(R.id.id_taskStartDate);
//        mTaskStartTimeText = inflater1.findViewById(R.id.id_taskStartTime);
//        mTaskLocationText = inflater1.findViewById(R.id.id_taskLocation);
//
//        // set view's text
//        mTaskAmountAwardText.setText(mTaskInfo.getTaskAwardAmount());
//        mTaskStartDateText.setText(mTaskInfo.getTaskStartDate());
//        mTaskStartTimeText.setText(mTaskInfo.getTaskStartTime());
//        mTaskLocationText.setText(mTaskInfo.getTaskLocationAddress());
//
//        return inflater1;
//
//    }
//}
