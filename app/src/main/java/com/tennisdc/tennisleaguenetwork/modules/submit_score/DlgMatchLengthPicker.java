package com.tennisdc.tennisleaguenetwork.modules.submit_score;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import com.tennisdc.tennisleaguenetwork.BaseDialog;
import com.tennisdc.tln.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created  on 2015-06-06.
 */
public abstract class DlgMatchLengthPicker extends BaseDialog {

    private final static int TIME_PICKER_INTERVAL = 5;

    @Override
    protected int getCustomContentLayout() {
        return R.layout.dlg_match_length;
    }

    private int minHour = 0;
    private int minMinute = 0;
    private int maxHour = 24;
    private int maxMinute = 59;
    private int stepHour = 1;
    private int stepMinute = 1;

    private NumberPicker HoursNumberPicker;
    private NumberPicker MinutesNumberPicker;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStepHour(1);
        setStepMinute(5);

        setMinHour(0);
        setMaxHour(9);

        setMinMinute(0);
        setMaxMinute(59);
    }

    @Override
    protected void preCreateViewSetup() {
        super.preCreateViewSetup();

        setPositiveButton("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTimeSet(HoursNumberPicker.getValue() * stepHour, MinutesNumberPicker.getValue() * stepMinute);
                dismiss();
            }
        });

        setNegativeButton("Cancel", null);
    }

    @Override
    protected void postCreateViewSetup(View v) {
        super.postCreateViewSetup(v);
        try {
            HoursNumberPicker = (NumberPicker) v.findViewById(R.id.numPkrHours);
            MinutesNumberPicker = (NumberPicker) v.findViewById(R.id.numPkrMinutes);

            HoursNumberPicker.setMinValue(minHour);
            HoursNumberPicker.setMaxValue(((maxHour - minHour) / stepHour));

            List<String> hourValues = new ArrayList<>();
            for (int i = minMinute; i <= maxHour; i += stepHour) {
                hourValues.add(String.format("%02d", i));
            }

            HoursNumberPicker.setDisplayedValues(hourValues.toArray(new String[0]));

            MinutesNumberPicker.setMinValue(minMinute);
            MinutesNumberPicker.setMaxValue(((maxMinute - minMinute) / stepMinute));

            List<String> minuteValues = new ArrayList<>();
            for (int i = minMinute; i <= maxMinute; i += stepMinute) {
                minuteValues.add(String.format("%02d", i));
            }

            MinutesNumberPicker.setDisplayedValues(minuteValues.toArray(new String[0]));

            HoursNumberPicker.setOnValueChangedListener(valueChangeListener);
            MinutesNumberPicker.setOnValueChangedListener(valueChangeListener);


            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(getTime());

            HoursNumberPicker.setValue(cal.get(Calendar.HOUR_OF_DAY) / stepHour);
            MinutesNumberPicker.setValue(cal.get(Calendar.MINUTE) / stepMinute);

            setTitle(cal.get(Calendar.HOUR_OF_DAY) + " Hour(s), " + cal.get(Calendar.MINUTE) + " Minute(s)");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    NumberPicker.OnValueChangeListener valueChangeListener = new NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            setTitle((HoursNumberPicker.getValue() * stepHour ) + " Hour(s), " + (MinutesNumberPicker.getValue() * stepMinute) + " Minute(s)");
        }
    };

    public abstract long getTime();

    public abstract void onTimeSet(int hourOfDay, int minute);
}
