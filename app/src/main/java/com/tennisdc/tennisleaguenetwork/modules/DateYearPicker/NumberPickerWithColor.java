package com.tennisdc.tennisleaguenetwork.modules.DateYearPicker;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.NumberPicker;

import com.tennisdc.tln.R;

import java.lang.reflect.Field;

/**
 * Created by Rajath.Akki on 13/01/18.
 */

public class NumberPickerWithColor extends NumberPicker {

    public NumberPickerWithColor(Context context, AttributeSet attrs) {
        super(context, attrs);

        Class<?> numberPickerClass = null;
        try {
            numberPickerClass = Class.forName("android.widget.NumberPicker");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Field selectionDivider = null;
        try {
            selectionDivider = numberPickerClass.getDeclaredField("mSelectionDivider");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        try {
            selectionDivider.setAccessible(true);
            final Drawable d;
            if (Build.VERSION.SDK_INT < 21) {
                d = context.getResources().getDrawable(R.drawable.picker_divider_color);
            } else {
                d = context.getDrawable(R.drawable.picker_divider_color);
            }
            selectionDivider.set(this, d);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
