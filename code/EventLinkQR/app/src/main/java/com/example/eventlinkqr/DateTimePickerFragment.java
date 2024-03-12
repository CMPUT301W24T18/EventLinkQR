package com.example.eventlinkqr;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

//https://developer.android.com/develop/ui/views/components/pickers
public class DateTimePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private DateTimePickerListener listener;
    private Calendar selectedDateTime;

    interface DateTimePickerListener {
        void addDateTime(Calendar dateAndtime);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof DateTimePickerListener) {
            listener = (DateTimePickerListener) context;
        } else {
            throw new RuntimeException(context + " must implement DateTimePickerListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Use the current date as the default date in the picker.
        selectedDateTime = Calendar.getInstance();
        int year = selectedDateTime.get(Calendar.YEAR);
        int month = selectedDateTime.get(Calendar.MONTH);
        int day = selectedDateTime.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it.
        return new DatePickerDialog(requireContext(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        selectedDateTime.set(year, month, dayOfMonth);
        int hour = selectedDateTime.get(Calendar.HOUR);
        int minute = selectedDateTime.get(Calendar.MINUTE);
        TimePickerDialog timepicker = new TimePickerDialog(requireContext(), this, hour, minute, true);
        timepicker.show();
    }


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        selectedDateTime.set(Calendar.MINUTE, minute);
        listener.addDateTime(selectedDateTime);
    }

}

