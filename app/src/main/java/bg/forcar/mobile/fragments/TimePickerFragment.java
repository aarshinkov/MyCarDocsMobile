package bg.forcar.mobile.fragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;

import androidx.fragment.app.DialogFragment;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment {

    private final TimePickerDialog.OnTimeSetListener timeSetListener;

    public TimePickerFragment(TimePickerDialog.OnTimeSetListener timeSetListener) {
        this.timeSetListener = timeSetListener;
    }

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), timeSetListener, hour, minute, DateFormat.is24HourFormat(getActivity()));
    }
}
