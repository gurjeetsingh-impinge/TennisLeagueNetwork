package com.tennisdc.tennisleaguenetwork.common;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * Created  on 17-03-2015.
 */
public class PartialRegexInputFilter implements InputFilter {

    @Override
    public CharSequence filter(CharSequence source, int sourceStart, int sourceEnd, Spanned destination, int destinationStart, int destinationEnd) {
        String textToCheck = destination.subSequence(0, destinationStart).
                toString() + source.subSequence(sourceStart, sourceEnd) +
                destination.subSequence(destinationEnd, destination.length()).toString();

        String numbers = textToCheck.replaceAll("[^0-9]", "");
        if (numbers.length() > 10) return "";
        return null;
    }

}