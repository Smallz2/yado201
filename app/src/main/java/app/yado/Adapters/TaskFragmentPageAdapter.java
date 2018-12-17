//package app.yado.Adapters;
//
//import android.app.FragmentManager;
//import android.content.Context;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentPagerAdapter;
//
//import app.yado.Fragments.Task_Details_Fragment;
//import app.yado.Fragments.Task_applicants_Fragment;
//import app.yado.Models.TaskInfo;
//
//import androidx.fragment.app.FragmentPagerAdapter;
// todo: fragment manager problem
//public class TaskFragmentPageAdapter  extends FragmentPagerAdapter {
//    private static int NUM_ITEMS = 2;
//    private String tabTitle[] = new String[] {"Details", "Applicants"};
//    private TaskInfo taskInfo1;
//    private static Context mContext;
//
//    public TaskFragmentPageAdapter(FragmentManager fragmentManager, TaskInfo taskInfo, Context context) {
//        super(fragmentManager);
//
//        taskInfo1 = taskInfo;
//        mContext = context;
//    }
//
//    // return number of pages
//    @Override
//    public int getCount(){
//        return NUM_ITEMS;
//    }
//
//    // return the fragment to display pages
//    @Override
//    public Fragment getItem(int position) {
//
//        switch (position) {
//            case 0:
//                return Task_Details_Fragment.newInstance(position, taskInfo1);
//            case 1:
//                return Task_applicants_Fragment.newInstance(position, mContext, taskInfo1);
//            default:
//                return null;
//        }
//    }
//
//    @Override
//    public CharSequence getPageTitle(int position) {
//       return tabTitle[position];
//    }
//}
