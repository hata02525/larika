package com.example.fluper.larika_user_app.utils;

/**
 * Created by rohit on 4/7/17.
 */

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.example.fluper.larika_user_app.R;

import java.util.Calendar;

/**
 * Created by rohit on 18/1/17.
 */

public class MonthYearDialog extends DialogFragment {

    private DatePickerDialog.OnDateSetListener listener;
    private EditText editText;
    public void setListener(DatePickerDialog.OnDateSetListener listener) {
        this.listener = listener;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        Calendar cal = Calendar.getInstance();
        final int currentMonth=(cal.get(Calendar.MONTH) + 1);
        final int currentYear=(cal.get(Calendar.YEAR));


        View dialog = inflater.inflate(R.layout.dialog_month_year, null);
        final NumberPicker monthPicker = (NumberPicker) dialog.findViewById(R.id.picker_month);
        final NumberPicker yearPicker = (NumberPicker) dialog.findViewById(R.id.picker_year);

        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        yearPicker.setMinValue(currentYear);
        yearPicker.setMaxValue(2030);
        monthPicker.setValue(cal.get(Calendar.MONTH) + 1);







        builder.setView(dialog)
                // Add action buttons
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if(yearPicker.getValue()==currentYear && monthPicker.getValue()<currentMonth)
                        {
                            MonthYearDialog.this.getDialog().cancel();
                            listener.onDateSet(null, 0, -1, 0);
                        }

                        else if(yearPicker.getValue()==currentYear && monthPicker.getValue()<=currentMonth)
                        {
                            MonthYearDialog.this.getDialog().cancel();
//                            listener.onDateSet(null, 0, 0, 0);
                            listener.onDateSet(null, yearPicker.getValue(), monthPicker.getValue(), 0);
                        }
                        else{
                            listener.onDateSet(null, yearPicker.getValue(), monthPicker.getValue(), 0);

                        }

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MonthYearDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}