package app.yado.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.ByteArrayOutputStream;

import androidx.annotation.NonNull;
import app.yado.Activities.Home_Activity;
import app.yado.Activities.Notification_Activity;
import app.yado.Activities.User_Profile_Activity;
import app.yado.Activities.User_Task_Activity;
import app.yado.R;

public class Utils {
    private static final String PREFERENCES_FILE = "yado_settings";

    public static String readSharedSetting(Context ctx, String settingName, String defaultValue) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        return sharedPref.getString(settingName, defaultValue);
    }

    public static void saveSharedSetting(Context ctx, String settingName, String settingValue) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(settingName, settingValue);
        editor.apply();
    }

    /**
    * Used to disable the shifting for navBav (Shifting happens when the nav items exceed 3;
     * @param view : BottomNavigationView
     */
//    @SuppressLint("RestrictedApi")
//    public static void disableShiftMode(BottomNavigationView view) {
//        BottomNavigationView menuView = (BottomNavigationView) view.getChildAt(0);
//        try {
//            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
//            shiftingMode.setAccessible(true);
//            shiftingMode.setBoolean(menuView, false);
//            shiftingMode.setAccessible(false);
//            if(menuView.getChildCount()<6)
//            {
//                for (int i = 0; i < menuView.getChildCount(); i++) {
//                    BottomNavigationView item = (BottomNavigationView) menuView.getChildAt(i);
//                    //noinspection RestrictedApi
//                    item.setShiftingMode(false);
//                    // set once again checked value, so view will be updated
//                    //noinspection RestrictedApi
//                    item.setChecked(item.getItemData().isChecked());
//                }
//            }
//        } catch (NoSuchFieldException e) {
//            Log.e("BNVHelper", "Unable to get shift mode field", e);
//        } catch (IllegalAccessException e) {
//            Log.e("BNVHelper", "Unable to change value of shift mode", e);
//        }
//    }

    /**
     * Function that start the various activity from the bottom navigation bar.
     * @param mContext : context of activity that uses the function.
     * @param view: bottom navigation view of the activity that use the function.
     * @param currentActivity : to check which activity the function gets called in.
     */
    public static void navigationItemSelector(final Context mContext, BottomNavigationView view, final String currentActivity) {

        int topPadding = 20;
        view.findViewById(R.id.action_nav_home).setPadding(0, topPadding, 0, 0);
        view.findViewById(R.id.action_nav_user_task).setPadding(0, topPadding, 0, 0);
  //      view.findViewById(R.id.action_nav_notification).setPadding(0, topPadding, 0, 0);
        view.findViewById(R.id.action_nav_profile).setPadding(0, topPadding, 0, 0);
//        view.findViewById(R.id.action_nav_messages).setPadding(0, topPadding, 0, 0);

        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_nav_home:
                        //Home_Activity
                        if (!currentActivity.equals("Home_Activity")) {
                            Intent intent = new Intent(mContext, Home_Activity.class);
                                mContext.startActivity(intent);
                        }
                        break;
                    case R.id.action_nav_user_task:
                        //User_Task_Activity
                        if(!currentActivity.equals("User_Task_Activity")) {
                            Intent intent2 = new Intent(mContext, User_Task_Activity.class);
                            mContext.startActivity(intent2);
                        }
                        break;
                    case R.id.action_nav_notification:
                        //Notification_Activity
                        if (!currentActivity.equals("Notification_Activity")) {
                            Intent intent3 = new Intent(mContext, Notification_Activity.class);
                            mContext.startActivity(intent3);
                        }
                        break;
//                    case R.id.action_nav_messages:
//                        // Messaging Activity
//                        if (!currentActivity.equals("Messaging_Activity")) {
//                            Intent intent4 = new Intent(mContext, Messages_Activity.class);
//                            Toast.makeText(mContext, "test", Toast.LENGTH_SHORT).show();
//                            mContext.startActivity(intent4);
//                        }
//                        break;
                    case R.id.action_nav_profile:
                        //Profile_Activity
                        if(!currentActivity.equals("User_Profile_Activity")) {
                            Intent intent5 = new Intent(mContext, User_Profile_Activity.class);
                            mContext.startActivity(intent5);
                        }
                        break;
                }
                return true;
            }
        });
    }

    /**
     * isStringNull : check to see whether a still is null(empty)
     * @param TAG : Tag of calling activity
     * @param string : string to be checked
     * @return : true, if the string is null ; false is the string is not null
     */
    public static boolean isStringNull (String TAG, String string) {
        Log.d(TAG, "isStringNull: checking is string is null");

        return string.equals("");
    }

    /**
     * getImageUri : convert a bitmap to a uri format
     * @param imageBitmap : bitmap to be converted
     * @param context : context of calling activity
     * @return : new uri from imageBitmap
     */
    public static Uri getImageUri(Context context, Bitmap imageBitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), imageBitmap, "profileImage", null);
        return Uri.parse(path);
    }


}
