package app.yado.Fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TimePicker;

import java.util.Calendar;

public class Time_Picker_Fragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private timePickerDialogListener listener;

    // User set values
    int mHour;
    int mMinute;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        int hour, minute;

        final Calendar c = Calendar.getInstance();

        Bundle arg = getArguments();

        if (arg != null) { // check to see if any bundle arguments was sent
            mHour = arg.getInt("Hour");
            mMinute = arg.getInt("Minute");
        }

        if (mHour == 0 || mMinute == 0) { //set time to current time is the uses didn't set a date b4
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);
        } else {  // set time user selected to the current time on timePicker
            hour = mHour;
            minute = mMinute;
        }


        return new TimePickerDialog(getActivity(), this, hour, minute, false);

    }

    @Override
    public void onAttach(Context context) {

        try {
            listener = (timePickerDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement timePickerDialogLister");
        }

        super.onAttach(context);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        listener.applyTime(hourOfDay, minute);

    }

    // not the best place to put this : may should make it separate java file
    public interface timePickerDialogListener {
        void applyTime(int hour, int minute);
    }

}
