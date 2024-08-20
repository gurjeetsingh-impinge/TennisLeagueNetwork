package com.tennisdc.tennisleaguenetwork.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tennisdc.tln.R;

import java.util.EnumMap;

import butterknife.ButterKnife;
import butterknife.BindView;

/**
 * Created  on 2015-03-14.
 */
public class NumberPickerHorizontal extends LinearLayout {

    private int mMin = 0, mMax = 99, mValue;

    @BindView(R.id.imgBtnMinus)
    ImageView mMinusImageView;

    @BindView(R.id.txtValue)
    TextView mValueTextView;

    @BindView(R.id.imgBtnPlus)
    ImageView mPlusImageView;

    public NumberPickerHorizontal(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumberPickerHorizontal(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        View view = LayoutInflater.from(context).inflate(R.layout.view_numberpicker_horizontal, this, true);

        ButterKnife.bind(this, view);

        setValue(0);

        mMinusImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mValue--;
                mValueTextView.setText(String.valueOf(mValue));

                if(mValue == mMin)
                    mMinusImageView.setEnabled(false);

                if(mValue == (mMax - 1))
                    mPlusImageView.setEnabled(true);
            }
        });

        mPlusImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mValue++;
                mValueTextView.setText(String.valueOf(mValue));

                if(mValue == mMax)
                    mPlusImageView.setEnabled(false);

                if(mValue == (mMin + 1))
                    mMinusImageView.setEnabled(true);
            }
        });
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    public int getValue() {
        return mValue;
    }

    public void setValue(int value) {
        this.mValue = value;
        mValueTextView.setText(String.valueOf(mValue));
    }

    public int getMax() {
        return mMax;
    }

    public void setMax(int mMax) {
        this.mMax = mMax;
    }

    public int getMin() {
        return mMin;
    }

    public void setMin(int mMin) {
        this.mMin = mMin;
    }

}
