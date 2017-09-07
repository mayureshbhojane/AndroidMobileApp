package com.androiddevelopment.mayureshbhojane.todolist.utils;

import android.text.method.PasswordTransformationMethod;
import android.view.View;

/**
 * Created by Mayuresh Bhojane on 9/6/2017.
 */

public class ClearPasswordTransformationMethod extends PasswordTransformationMethod {

    @Override
    public CharSequence getTransformation(CharSequence source, View view) {
        return new SimpleCharSequence(source);
    }

    private class SimpleCharSequence implements CharSequence {
        private CharSequence mSource;
        public SimpleCharSequence(CharSequence source) {
            mSource = source; // Store char sequence
        }
        public char charAt(int index) {
            return mSource.charAt(index); // This is the important part
        }
        public int length() {
            return mSource.length(); // Return default
        }
        public CharSequence subSequence(int start, int end) {
            return mSource.subSequence(start, end); // Return default
        }
    }

}
