package app.yado.Fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import java.util.Calendar;

public class Date_Picker_Fragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = Date_Picker_Fragment.class.getSimpleName() ;
    private datePickerDialogListener listener;
    int year;
    int month;
    int day;
    Calendar c;

    // user set values
    int mSetYear;
    int mSetMonth;
    int mSetDay;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // get data from bundle
        Bundle arg = getArguments();
        c = Calendar.getInstance();
        if (arg != null) { // check to see if any bundle arguments was sent
            mSetYear = getArguments().getInt("Year");
            mSetMonth = getArguments().getInt("Month");
            mSetDay = getArguments().getInt("Day");
        }

        if (mSetDay == 0 || mSetMonth == 0 || mSetYear == 0 ) { //set date to current date is the uses didn't set a date b4
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        } else { // set date user selected to the current date on calender
            year = mSetYear;
            month = mSetMonth;
            day = mSetDay;
        }

        Calendar c2 = Calendar.getInstance(); // to just get a difference instance

        // Allowed to set task for oneMonthAhead
        c2.add(Calendar.MONTH, +1);
        long oneMonthAhead = c2.getTimeInMillis();

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);

        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis()); // to disable days before current date
        datePickerDialog.getDatePicker().setMaxDate(oneMonthAhead);
        Log.d(TAG, "Time:" + c.getTimeInMillis() );

        return datePickerDialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (datePickerDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
            "must implement dataPickerDialogListener" );
        }
    }

    @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            listener.applyDate(year, month, dayOfMonth);
        }

        // not the best place to put this : may should make it separate java file
        public interface datePickerDialogListener {
            void applyDate(int year, int month, int dayOfMonth);
        }
}
