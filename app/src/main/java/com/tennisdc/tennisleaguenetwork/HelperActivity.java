package com.tennisdc.tennisleaguenetwork;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.tennisdc.tln.R;

import com.tennisdc.tennisleaguenetwork.common.ExceptionHandler;
import com.tennisdc.tennisleaguenetwork.common.FontUtils;
import com.tennisdc.tennisleaguenetwork.ui.BackHandledFragment;
import androidx.appcompat.app.AppCompatActivity;

public class HelperActivity extends AppCompatActivity implements BackHandledFragment.BackHandlerInterface {
    private BackHandledFragment selectedFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
    }

    @Override
    public View onCreateView(String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        View v = super.onCreateView(name, context, attrs);
        if (v == null) {
            v = tryInflate(name, context, attrs);
            if (v instanceof TextView) {
                TypedArray values = context.obtainStyledAttributes(attrs, R.styleable.AppTypeface);
                if (values.hasValue(R.styleable.AppTypeface_appTypeface)) {
                    int typefaceValue = values.getInt(R.styleable.AppTypeface_appTypeface, -1);
                    if (typefaceValue != -1)
                        ((TextView) v).setTypeface(FontUtils.obtainTypeface(this, typefaceValue));
                }
                values.recycle();
            }
        }
        return v;
    }

    private View tryInflate(String name, Context context, AttributeSet attrs) {
        LayoutInflater li = LayoutInflater.from(context);
        View v = null;
        try {
            v = li.createView(name, null, attrs);
        } catch (Exception e) {
            try {
                v = li.createView("android.widget." + name, null, attrs);
            } catch (Exception e1) {
                Log.d("DEBUG", e.getLocalizedMessage());
            }
        }
        return v;
    }

    @Override
    public View onCreateView(View parent, String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        View v = super.onCreateView(parent, name, context, attrs);
        if (v == null) {
            v = tryInflate(name, context, attrs);
            if (v instanceof TextView) {
                TypedArray values = context.obtainStyledAttributes(attrs, R.styleable.AppTypeface);
                if (values.hasValue(R.styleable.AppTypeface_appTypeface)) {
                    int typefaceValue = values.getInt(R.styleable.AppTypeface_appTypeface, -1);
                    if (typefaceValue != -1)
                        ((TextView) v).setTypeface(FontUtils.obtainTypeface(this, typefaceValue));
                }
                values.recycle();
            }
        }
        return v;
    }

    @Override
    public void onBackPressed() {
        if (selectedFragment == null || !selectedFragment.onBackPressed()) {
            // Selected fragment did not consume the back press event.
            super.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void setSelectedFragment(BackHandledFragment selectedFragment) {
        this.selectedFragment = selectedFragment;
    }
}
