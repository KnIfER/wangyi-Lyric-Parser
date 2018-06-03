package com.knizha.wangYiLP.ui;

import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.NumberKeyListener;


/**
 * Filter for accepting only valid indices or prefixes of the string
 * representation of valid indices.
 */
public class InputTextFilter extends NumberKeyListener {

    private static final char[] DIGIT_CHARACTERS = new char[] {
            // Latin digits are the common case
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9','-'};

    // XXX This doesn't allow for range limits when controlled by a
    // soft input method!
    public int getInputType() {
        return InputType.TYPE_CLASS_TEXT;
    }

    @Override
    protected char[] getAcceptedChars() {
        return DIGIT_CHARACTERS;
    }

    @Override
    public CharSequence filter(
            CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            CharSequence filtered = super.filter(source, start, end, dest, dstart, dend);
            if (filtered == null) {
                filtered = source.subSequence(start, end);
            }

            String result = String.valueOf(dest.subSequence(0, dstart)) + filtered
                    + dest.subSequence(dend, dest.length());

            if ("".equals(result)) {
                return result;
            }


            /*
             * Ensure the user can type in a value greater than the max
             * allowed. We have to allow less than min as the user might
             * want to delete some numbers and then type a new number.
             * And prevent multiple-"0" that exceeds the length of upper
             * bound number.
             */

            return filtered;

    }
}

