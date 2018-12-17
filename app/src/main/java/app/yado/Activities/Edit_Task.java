package app.yado.Activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import app.yado.Fragments.Date_Picker_Fragment;
import app.yado.Fragments.Time_Picker_Fragment;
import app.yado.Models.TaskInfo;
import app.yado.R;

public class Edit_Task extends Activity implements View.OnClickListener, Date_Picker_Fragment.datePickerDialogListener, Time_Picker_Fragment.timePickerDialogListener  {

    // Globals
    final static String TAG = Create_Task.class.getSimpleName(); // tag for logs
    public static final String DATE_FORMAT = "d/M/yyyy"; // date format
    private static final int ERROR_DIALOG_REQUEST = 9001;

    // vars
    boolean isDateEntered = false; // to make sure user enter date before setting time
    boolean isDateCurrent = false; // check is user set the start day as current date
    String mYear; // year from applyDate interface
    String mDayOfMonth; // dayOfMont from appleDate interface
    int mMonthIndex;
    String mTaskTitle;
    String mTaskDescription;
    String mTaskStartDate;
    String mTaskStartTime;
    String mTaskAmountAwarded;
    String mTaskLocationAddress;
    String mTaskLocationLatLong;
    int REQUEST_CODE = 1;
    int RESULT_OK = 1;
    FirebaseUser mFireBaseUser;
    TaskInfo taskInfo;
    String key;

    Context mContext;

    String mHour;
    String mMinute;

    // Views
    TextInputEditText mAmountAwardedEditText;
    TextInputLayout mAmountAwardedWrapper;

    LinearLayout mPriceErrorLinearLayout;
    LinearLayout mDescriptionLinearLayout;

    EditText mDescriptionEditText;
    EditText mTaskTitleEditText;

    RelativeLayout mAmountAwardedCard;
    RelativeLayout mStartDateCard;
    RelativeLayout mStartTimeCard;
    RelativeLayout mLocationCard;

    TextView mStartDateText;
    TextView mStartTimeText;
    TextView mLocationText;

    Button mCreateTaskButton;

    /**
     * Interface Function from datePickerDialogListener : used to get date set by user
     * @param year user selected year
     * @param month user selected month
     * @param dayOfMonth user selected day
     */
    @Override
    public void applyDate(int year, int month, int dayOfMonth) {
        Log.d(TAG, "applyDate: Applying date");

        mYear = String.valueOf(year); // year from applyDate interface
        mMonthIndex = month;
        String  mMonth = findMonthName(month); // month from applyDate interface
        mDayOfMonth = String.valueOf(dayOfMonth); // dayOfMont from appleDate interface
        String mEndDate = mDayOfMonth + " " + month  + " " + mYear; // end date set by year
        String mStartDate; // current date


        // Get Current Date
        Calendar mCalender = Calendar.getInstance();
        int mCurrentYear = mCalender.get(Calendar.YEAR);
        int mCurrentMonth = mCalender.get(Calendar.MONTH);
        int mCurrentDayOfMonth = mCalender.get(Calendar.DAY_OF_MONTH);

        // Date Selected by user to price out to screen
        String finalSelectedStartDate = mDayOfMonth + " " + mMonth + ", " + mYear;

        // Start Date (current date)
        mStartDate = String.valueOf(mCurrentDayOfMonth) + " " + String.valueOf( mCurrentMonth) + " " + String.valueOf(mCurrentYear);


        mStartDateText.setText(finalSelectedStartDate);
        mStartDateText.setTextColor(getResources().getColor(R.color.fontColorPrimary));
        isDateEntered = true;
        mTaskStartDate = finalSelectedStartDate;

        // set is date current
        isDateCurrent = mStartDate.equals(mEndDate);
    }

    /**
     * Interface function from timePickerDialogListener : used to get time set by user in 24h format
     * @param hour hour set by user
     * @param minute minute set by user
     */
    @Override
    public void applyTime(int hour, int minute) {
        Log.d(TAG, "applyTime: Applying time");

        mHour = String.valueOf(hour);
        mMinute = String.valueOf(minute);
        String mTime = mHour + ":" + mMinute;
        SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm  a");
        int maxStartTime = 5; // Max start time AM in 24h
        int maxEndTime = 20; // Max end time PM in 24h
        boolean checkValidTime = false;
//        if (hour > maxStartTime && hour < maxEndTime) { // to make sure user sets their time within the hours of 5am to 7pm
        // Change time format from 24h to 12h with parsing it
        try {
            final SimpleDateFormat _24HourSDF = new SimpleDateFormat("H:mm");
            Date _24HourDt = _24HourSDF.parse(mTime);

            checkValidTime = true;

            if (isDateCurrent) {
                Calendar mCalendar = Calendar.getInstance();
                int mCurrentHour = mCalendar.get(Calendar.HOUR_OF_DAY);

                if (hour < mCurrentHour) {
                    Toast.makeText(this, "Can't go back in time", Toast.LENGTH_SHORT).show();
                    checkValidTime = false;
                }
            }

            if (checkValidTime) {
                mStartTimeText.setText(_12HourSDF.format(_24HourDt));
                mTaskStartTime = _12HourSDF.format(_24HourDt);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        } else {
//            Toast.makeText(this, "Please set your time within the hours of 5am to 7pm", Toast.LENGTH_SHORT).show();
//        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create__task);

        mContext = getApplicationContext();

        // Find Views;
        mAmountAwardedWrapper = findViewById(R.id.id_amountAwardedInputWrapper);
        mAmountAwardedEditText = findViewById(R.id.id_amountAwardedInput);
        mPriceErrorLinearLayout = findViewById(R.id.id_priceErrorMessage);
        mDescriptionEditText = findViewById(R.id.id_taskDescriptionInput);
        mTaskTitleEditText = findViewById(R.id.id_TaskTitle);
        mDescriptionLinearLayout = findViewById(R.id.id_descriptionErrorMessage);
        mAmountAwardedCard = findViewById(R.id.id_amountAwardedCard);
        mStartDateCard = findViewById(R.id.id_startDateCard);
        mStartDateText = findViewById(R.id.id_startDateTextView);
        mStartTimeText = findViewById(R.id.id_startTimeTextView);
        mStartTimeCard = findViewById(R.id.id_startTimeCard);
        mLocationCard = findViewById(R.id.id_locationCard);
        mLocationText = findViewById(R.id.id_locationTextView);
        mCreateTaskButton = findViewById(R.id.id_createTaskButton);

        init();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_amountAwardedCard:
                // Focus on  amount awarded edit text when click on card to allow for a better user experience
                mAmountAwardedEditText.requestFocus();
                break;

            case R.id.id_startDateCard:
                startDatePickerEvent();
                break;

            case R.id.id_startTimeCard:
                if (isDateEntered) {
                    startTimePickerEvent();
                } else {
                    Toast.makeText(this, "Please select start date first", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.id_createTaskButton:
                saveTaskInformation();
                break;
        }
    }

    /**
     * Function that initializes
     */

    private void init() {
        Log.d(TAG, "initializing");

        // get data from intent
        taskInfo = (TaskInfo) getIntent().getSerializableExtra("taskInformation");

        // set views information from intent
        mTaskTitleEditText.setText(taskInfo.getTaskTitle());
        mDescriptionEditText.setText(taskInfo.getTaskDescription());
        mAmountAwardedEditText.setText(taskInfo.getTaskAwardAmount());
        mStartDateText.setText(taskInfo.getTaskStartDate());
        mStartTimeText.setText(taskInfo.getTaskStartTime());
        mLocationText.setText(taskInfo.getTaskLocationAddress());

        mTaskTitle = taskInfo.getTaskTitle();
        mTaskDescription = taskInfo.getTaskDescription();
        mTaskAmountAwarded = taskInfo.getTaskAwardAmount();
        mTaskStartDate = taskInfo.getTaskStartDate();
        mTaskStartTime = taskInfo.getTaskStartTime();
        mTaskLocationAddress = taskInfo.getTaskLocationAddress();
        mTaskLocationLatLong = taskInfo.getTaskLocationLatLong();

        key = taskInfo.getTaskKey();



        mFireBaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // Set On Click Listener
        mAmountAwardedCard.setOnClickListener(this);
        mStartDateCard.setOnClickListener(this);
        mStartTimeCard.setOnClickListener(this);
        mCreateTaskButton.setOnClickListener(this);

        // Amount awarded onChangeTextListener
        amountAwardedChangeListener(mAmountAwardedEditText);

        // Description onChangeTextListener
        // descriptionChangeListener(mDescriptionEditText);

        // checking to see if google play services is working on user's device
        if(isServiceOK()) {  // if ok user can use maps for location
            mLocationCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, Map_Activity.class);
                    startActivityForResult(intent, REQUEST_CODE);
                }
            });
        }

    }

    /**
     * Function that listens to the amount awarded to user puts in to see it is according to the set rules.
     * @param inputEditText : TextInputEditText;
     */

    private void amountAwardedChangeListener (final TextInputEditText inputEditText) {
        Log.d(TAG, "amountAwardedChangeListener: listening to amount change");

        inputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                inputEditText.setTextColor(getResources().getColor(R.color.fontColorPrimary));


                if (!inputEditText.getText().toString().isEmpty()) {
                    int amount = Integer.parseInt(inputEditText.getText().toString());

                    if (amount > 2000 || amount < 10 ) {
                        mPriceErrorLinearLayout.setVisibility(View.VISIBLE);
                        mAmountAwardedWrapper.setErrorEnabled(true);
                    } else {
                        mPriceErrorLinearLayout.setVisibility(View.INVISIBLE);
                        mAmountAwardedWrapper.setErrorEnabled(false);
                        mTaskAmountAwarded = String.valueOf(amount);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * Function that listens to the amount of words that the user puts in to the description
     * @param editText : EditText
     */

    private void descriptionChangeListener(final EditText editText) {
        Log.d(TAG, "descriptionChangeListener: listening to descriptionChange");

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String words = editText.getText().toString().trim().replaceAll("\n",""); //fix for new line

                if (!words.isEmpty()) {
                    String [] wordCount =  words.split("\\s+");

                    if (!(wordCount.length > 4)) {
                        mDescriptionLinearLayout.setVisibility(View.VISIBLE);
                    } else {
                        mDescriptionLinearLayout.setVisibility(View.INVISIBLE);
                        mTaskDescription = words;
                    }
                }
            }
        });
    }

    /**
     * Function that launches Date_Picker_Fragment Dialog
     */

    private void startDatePickerEvent () {
        Log.d(TAG, "startDatePickerEvent: launch datePicker dialog to pick date");

        DialogFragment mDialogFragment = new Date_Picker_Fragment();
        mDialogFragment.show(getFragmentManager(), "datePicker");

        // Send set values back to fragment
        if (!(mYear == null|| mMonthIndex == 0 || mDayOfMonth == null)) {
            Bundle args = new Bundle();
            args.putInt("Year", Integer.parseInt(mYear));
            args.putInt("Month", mMonthIndex);
            args.putInt("Day", Integer.parseInt(mDayOfMonth));
            mDialogFragment.setArguments(args);
        }
    }

    /**
     * Function that launches Time_Picker_Fragment Dialog
     */
    private void startTimePickerEvent() {
        Log.d(TAG, "startTimePickerEvent: launch timePicker dialog to pick time");

        DialogFragment mDialogFragment = new Time_Picker_Fragment();
        mDialogFragment.show(getFragmentManager(), "timePicker");

        // Send set values back to fragment
        if (!(mHour == null || mMinute == null )) {
            Bundle args = new Bundle();
            args.putInt("Hour", Integer.parseInt(mHour));
            args.putInt("Minute", Integer.parseInt(mMinute));
            mDialogFragment.setArguments(args);
        }
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

    private boolean isServiceOK() {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mContext);

        if (available == ConnectionResult.SUCCESS) {
            // everything is fine and user can make map request
            Log.d(TAG, "isServicesOk: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(Edit_Task.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(mContext, "Can't make map request", Toast.LENGTH_SHORT).show();
        }
        return false;
    }


    private void saveTaskInformation() {
        Log.d(TAG, "saveTaskInformation: saving task information");


        mTaskTitle = mTaskTitleEditText.getText().toString().trim();
        mTaskDescription = mDescriptionEditText.getText().toString().trim();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        TaskInfo mTaskInfo = new TaskInfo();

        if ( mTaskTitle!=null && mTaskAmountAwarded!=null && mTaskDescription!=null
                && mTaskStartDate!=null && mTaskStartTime!=null  && mTaskLocationAddress!=null
                && mTaskLocationLatLong !=null ) {
            mTaskInfo.setTaskTitle(mTaskTitle);
            mTaskInfo.setTaskAwardAmount(mTaskAmountAwarded);
            mTaskInfo.setTaskDescription(mTaskDescription);
            mTaskInfo.setTaskStartDate(mTaskStartDate);
            mTaskInfo.setTaskStartTime(mTaskStartTime);
            mTaskInfo.setTaskLocationAddress(mTaskLocationAddress);
            mTaskInfo.setTaskLocationLatLong(mTaskLocationLatLong);
            mTaskInfo.setTaskKey(key);
            mTaskInfo.setTaskStatus("open");
            mTaskInfo.setAuthorUID(mFireBaseUser.getUid());

            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put(key, mTaskInfo.toFireBaseObject());

            database.getReference("taskList").updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError == null) {
                        Log.d(TAG, "onComplete: sending data to fireBase database");
                        // database updated so end activity
                        finish();
                    }
                }
            });
        } else {
            Toast.makeText(mContext, "Please fill in all the information", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (REQUEST_CODE == requestCode) {
            if (RESULT_OK == resultCode ) {
                // Get task location information from Map_Activity
                mTaskLocationAddress = data.getStringExtra("taskLocationAddress");
                mTaskLocationLatLong = data.getStringExtra("taskLocationLatLong");

                mLocationText.setText(mTaskLocationAddress.toString().trim());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
