package com.tennisdc.tln.modules.DateYearPicker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;
import com.tennisdc.tln.R;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;


/**
 * Created by Inderjeet Kumar on 13/01/18.
 */

public class YearMonthPickerDialog implements Dialog.OnClickListener {
    private static final int MIN_YEAR = Calendar.getInstance().get(Calendar.YEAR) - 87;
    private static final int MAX_YEAR = Calendar.getInstance().get(Calendar.YEAR) - 17;
    private static final String[] MONTHS_LIST = new String[]
            {
                    "January", "February", "March",
                    "April", "May", "June", "July",
                    "August", "September", "October",
                    "November", "December"
            };
    private OnDateSetListener mOnDateSetListener;
    private final Context mContext;
    private AlertDialog.Builder mDialogBuilder;
    private AlertDialog mDialog;
    private int mTheme;
    private int mTextTitleColor;
    private int mYear;
    private int mMonth;


    public YearMonthPickerDialog(Context context, OnDateSetListener onDateSetListener) {
        this(context, onDateSetListener, -1, -1);
    }


    public YearMonthPickerDialog(Context context, OnDateSetListener onDateSetListener, int theme) {
        this(context, onDateSetListener, theme, -1);
    }

    public YearMonthPickerDialog(Context context, OnDateSetListener onDateSetListener, int theme, int titleTextColor) {
        mContext = context;
        mOnDateSetListener = onDateSetListener;
        mTheme = theme;
        mTextTitleColor = titleTextColor;

        //Builds the dialog using listed parameters.
        build();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            //If user presses positive button
            case DialogInterface.BUTTON_POSITIVE:
                //Check if user gave us a listener
                if (mOnDateSetListener != null)
                    //Set picked year and month to the listener
                    mOnDateSetListener.onYearMonthSet(mYear, mMonth);
                break;

            //If user presses negative button
            case DialogInterface.BUTTON_NEGATIVE:
                //Exit the dialog
                if (mOnDateSetListener != null)
                    //Set picked year and month to the listener
                    mOnDateSetListener.onCancel();
                dialog.cancel();
                break;
        }
    }

    private void build() {
        //Applying user's theme
        int currentTheme = mTheme;
        //If there is no custom theme, using default.
        if (currentTheme == -1) currentTheme = R.style.MyDialogTheme;

        //Initializing dialog builder.
        mDialogBuilder = new AlertDialog.Builder(mContext, currentTheme);

        //Creating View inflater.
        final LayoutInflater layoutInflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Inflating custom title view.
        final View titleView = layoutInflater.inflate(R.layout.view_dialog_title, null, false);
        //Inflating custom content view.
        final View contentView = layoutInflater.inflate(R.layout.view_month_year_picker, null, false);

        //Initializing year and month pickers.
        final NumberPickerWithColor yearPicker =
                (NumberPickerWithColor) contentView.findViewById(R.id.year_picker);
        final NumberPickerWithColor monthPicker =
                (NumberPickerWithColor) contentView.findViewById(R.id.month_picker);

        //Initializing title text views
        final TextView monthName = (TextView) titleView.findViewById(R.id.ok);
        final TextView yearValue = (TextView) titleView.findViewById(R.id.cancel);

        //If there is user's title color,
        if(mTextTitleColor != -1)
        {
            //Then apply it.
            setTextColor(monthName);
            setTextColor(yearValue);
        }

        //Setting custom title view and content to dialog.
        mDialogBuilder.setCustomTitle(titleView);
        mDialogBuilder.setView(contentView);

        //Setting year's picker min and max value
        yearPicker.setMinValue(MIN_YEAR);
        yearPicker.setMaxValue(MAX_YEAR);

        //Setting month's picker min and max value
        monthPicker.setMinValue(0);
        monthPicker.setMaxValue(MONTHS_LIST.length - 1);

        //Setting month list.
        List<String> list = Arrays.asList(MONTHS_LIST);
        Collections.reverse(list);

        monthPicker.setDisplayedValues(list.toArray(new String[0]));

//        monthPicker. setWrapSelectorWheel(false);
//        yearPicker. setWrapSelectorWheel(false);

//        String [] years = new String[MAX_YEAR - MIN_YEAR + 1];
//
//        for (int i = MAX_YEAR; i >= MIN_YEAR; i--){
//            years[MAX_YEAR - i] = (String.valueOf(i));
//        }
//
//        yearPicker.setDisplayedValues(years);

        //Applying current date.
        setCurrentDate(yearPicker, monthPicker, monthName, yearValue);

        //Setting all listeners.
        setListeners(yearPicker, monthPicker, yearValue, monthName);

        //Setting titles and listeners for dialog buttons.
//        mDialogBuilder.setPositiveButton("OK", this);
//        mDialogBuilder.setNegativeButton("CANCEL", this);

        //Creating dialog.
        mDialog = mDialogBuilder.create();
    }

    private void setTextColor(TextView titleView)
    {
        titleView.setTextColor(ContextCompat.getColor(mContext, mTextTitleColor));
    }

    private void setCurrentDate(NumberPickerWithColor yearPicker, NumberPickerWithColor monthPicker, TextView monthName, TextView yearValue) {
        //Getting current date values from Calendar instance.
        Calendar calendar = Calendar.getInstance();
        mMonth = MONTHS_LIST.length - 1;
        mYear = MAX_YEAR;

        //Setting output format.
//        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM");

        //Setting current date values to dialog title views.
//        monthName.setText(monthFormat.format(calendar.getTime()));
//        yearValue.setText(Integer.toString(mYear));

        //Setting current date values to pickers.
        monthPicker.setValue(mMonth);
        yearPicker.setValue(mYear);
    }

    private void setListeners(final NumberPickerWithColor yearPicker, final NumberPickerWithColor monthPicker, final TextView Cancel, final TextView Ok) {
        //Setting listener to month name view.
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               mDialog.dismiss();
            }
        });

        //Setting listener to year value view.
        Ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                if (mOnDateSetListener != null)
                    mOnDateSetListener.onYearMonthSet(mYear, mMonth);
            }
        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                if (mOnDateSetListener != null)
                    mOnDateSetListener.onCancel();
            }
        });

        //Setting listener to month picker. So it can change title text value.
        monthPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mMonth = newVal;

                //Set title month text to picked month.
//                Cancel.setText(MONTHS_LIST[newVal]);
            }
        });

        //Setting listener to year picker. So it can change title text value.
        yearPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mYear = newVal;

                //Set title year text to picked year.
//                Ok.setText(Integer.toString(newVal));
            }
        });
    }

    public void show() {
        mDialog.show();
    }

    public interface OnDateSetListener {
        void onYearMonthSet(int year, int month);
        void onCancel();
    }
}
