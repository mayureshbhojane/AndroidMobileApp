package com.androiddevelopment.mayureshbhojane.todolist;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

/**
 * Created by Mayuresh Bhojane on 7/15/2017.
 */

/**
 * This class is a custom OnItemSelectedListener for spinner view
 */
public class SpinnerTaskOntemSelectedListener implements AdapterView.OnItemSelectedListener {

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Toast.makeText(adapterView.getContext(),
                "OnItemSelectedListener : " + adapterView.getItemAtPosition(i).toString(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

}
