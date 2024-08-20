package com.tennisdc.tennisleaguenetwork.common;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import androidx.fragment.app.DialogFragment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created  on 19-12-2014.
 */
public class Pickers {

    public static abstract class MatchDatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();

            if (getTime() > 0) c.setTimeInMillis(getTime());

            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            RangedDatePickerDialog datePickerDialog = new RangedDatePickerDialog(getActivity(), this, year, month, day);

            c.setTimeInMillis(new Date().getTime());
            //c.add(Calendar.DAY_OF_MONTH, 1);

            datePickerDialog.setMaxDate(c.getTimeInMillis());

            return datePickerDialog;
        }

        public abstract long getTime();

        public abstract void onDateSet(DatePicker view, int year, int month, int day);
    }

    public static abstract class MatchLengthTimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        public abstract void onTimeSet(TimePicker view, int hourOfDay, int minute);

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(getTime());

            RangedTimePickerDialog dialog = new RangedTimePickerDialog(getActivity(), this, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true, 1, 5);

            dialog.setMinHour(0);
            dialog.setMaxHour(9);

            dialog.setMinMinute(0);
            dialog.setMaxMinute(59);

            dialog.setTitle(cal.get(Calendar.HOUR_OF_DAY) + " Hour(s), " + cal.get(Calendar.MINUTE) + " Minute(s)");
            return dialog;
        }

        public abstract long getTime();
    }

    public static class RangedDatePickerDialog extends DatePickerDialog {

        private long minDate = -1, maxDate = -1;

        private DatePicker datePicker;

        public RangedDatePickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
            super(context, callBack, year, monthOfYear, dayOfMonth);
        }

        public void setMaxDate(long maxDate) {
            this.maxDate = maxDate;
        }

        public void setMinDate(long minDate) {
            this.minDate = minDate;
        }

        @Override
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            try {
                Class<?> classForid = Class.forName("com.android.internal.R$id");
                Field timePickerField = classForid.getField("datePicker");
                this.datePicker = (DatePicker) findViewById(timePickerField.getInt(null));

                if (minDate > 0) datePicker.setMinDate(minDate);
                if (maxDate > 0) datePicker.setMaxDate(maxDate);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class RangedTimePickerDialog extends TimePickerDialog {

        private final static int TIME_PICKER_INTERVAL = 5;
        private final OnTimeSetListener callback;
        private int minHour = 0;
        private int minMinute = 0;
        private int maxHour = 24;
        private int maxMinute = 59;
        private int stepHour = 1;
        private int stepMinute = 1;
        private TimePicker timePicker;

        public RangedTimePickerDialog(Context context, OnTimeSetListener callBack, int hourOfDay, int minute, boolean is24HourView) {
            this(context, null, hourOfDay, minute, is24HourView, 1, 1);
        }

        public RangedTimePickerDialog(Context context, OnTimeSetListener callBack, int hourOfDay, int minute, boolean is24HourView, int stepHour, int stepMinute) {
            super(context, 0, null, hourOfDay / stepHour, minute / stepMinute, is24HourView);
            this.callback = callBack;

            this.stepMinute = stepMinute;
            this.stepHour = stepHour;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (callback != null && timePicker != null) {
                timePicker.clearFocus();
                if (Build.VERSION.SDK_INT < 21)
                    callback.onTimeSet(timePicker, timePicker.getCurrentHour() * stepHour, timePicker.getCurrentMinute() * stepMinute);
                else
                    callback.onTimeSet(timePicker, timePicker.getCurrentHour(), timePicker.getCurrentMinute());
            }
        }

        public void setMaxHour(int maxHour) {
            this.maxHour = maxHour;
        }

        public void setMinHour(int minHour) {
            this.minHour = minHour;
        }

        public void setMaxMinute(int maxMinute) {
            this.maxMinute = maxMinute;
        }

        public void setMinMinute(int minMinute) {
            this.minMinute = minMinute;
        }

        public void setStepHour(int stepHour) {
            this.stepHour = stepHour;
        }

        public void setStepMinute(int stepMinute) {
            this.stepMinute = stepMinute;
        }

        /*public void setCurrentHour(int currentHour) {
            this.currentHour = currentHour;
        }

        public void setCurrentMinute(int currentMinute) {
            this.currentMinute = currentMinute;
        }*/

        @Override
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            try {
                Class<?> classForid = Class.forName("com.android.internal.R$id");
                Field timePickerField = classForid.getField("timePicker");
                this.timePicker = (TimePicker) findViewById(timePickerField.getInt(null));

                if (Build.VERSION.SDK_INT < 21) {
                    NumberPicker mHourSpinner = (NumberPicker) timePicker.findViewById(classForid.getField("hour").getInt(null));
                    mHourSpinner.setMinValue(minHour);
                    mHourSpinner.setMaxValue(((maxHour - minHour) / stepHour));

                    List<String> hourValues = new ArrayList<>();
                    for (int i = minMinute; i <= maxHour; i += stepHour) {
                        hourValues.add(String.format("%02d", i));
                    }

                    mHourSpinner.setDisplayedValues(hourValues.toArray(new String[0]));

                    NumberPicker mMinuteSpinner = (NumberPicker) timePicker.findViewById(classForid.getField("minute").getInt(null));
                    mMinuteSpinner.setMinValue(minMinute);
                    mMinuteSpinner.setMaxValue(((maxMinute - minMinute) / stepMinute));

                    List<String> minuteValues = new ArrayList<>();
                    for (int i = minMinute; i <= maxMinute; i += stepMinute) {
                        minuteValues.add(String.format("%02d", i));
                    }

                    mMinuteSpinner.setDisplayedValues(minuteValues.toArray(new String[0]));
                }

                timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                    @Override
                    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                        setTitle(hourOfDay + " Hour(s), " + minute + " Minute(s)");
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}