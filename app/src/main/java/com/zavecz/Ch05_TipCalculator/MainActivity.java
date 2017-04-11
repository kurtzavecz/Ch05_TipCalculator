package com.zavecz.Ch05_TipCalculator;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity  implements TextView.OnEditorActionListener, View.OnClickListener {

    private static final String TAG = "MainActivity";
    //define widgets
    private EditText mSubtotalET;
    private TextView mPercentTV;
    private TextView mTipAmountTV;
    private TextView mTotalTV;
    private Button mApplyButton;
    private SeekBar mPercentSeekBar;
    private int split = 1;

    //define SharedPrefferences object
    private SharedPreferences mSavedValues;

    //define instance variables that should be saved
    private String mBillAmountString = "";
    private float mTipPercent = .15f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get refference to the widgets
        mSubtotalET = (EditText) findViewById(R.id.mSubTotalET);
        mPercentTV = (TextView) findViewById(R.id.mPercentTV);
        mTipAmountTV = (TextView) findViewById(R.id.mTipAmountTV);
        mTotalTV = (TextView) findViewById(R.id.mTotalTV);
        mApplyButton = (Button) findViewById(R.id.mApplyButton);
        mPercentSeekBar = (SeekBar) findViewById(R.id.seekBar);

        //set the listeners
        mSubtotalET.setOnEditorActionListener(this);
        mApplyButton.setOnClickListener(this);

        //get SharedPreferences object
        mSavedValues = getSharedPreferences("savedValues", MODE_PRIVATE);

    }

    private void calculateAndDisplay() {

        //get bill amount from user
        mBillAmountString = mSubtotalET.getText().toString();
        float billAmount;
        if(mBillAmountString.equals("")){
            billAmount = 0;
        } else{
            billAmount = Float.parseFloat(mBillAmountString);
        }

        //get discount percent
        mTipPercent = mPercentSeekBar.getProgress() * .01f;
        float discountPercent = mTipPercent / 100;
        if(billAmount >= 200) {
            discountPercent = .2f;
        }else if(billAmount >= 100){
            discountPercent = .1f;
        }else {
            discountPercent = 0;
        }

        //calculate discount
        float tipAmount = billAmount * mTipPercent;
        float total = billAmount + tipAmount;

        //dispaly data on layout
        NumberFormat percent = NumberFormat.getPercentInstance();
        mPercentTV.setText(percent.format(mTipPercent));

        NumberFormat curency = NumberFormat.getCurrencyInstance();
        mTipAmountTV.setText(curency.format(tipAmount));
        mTotalTV.setText(curency.format(total));
    }

    @Override
    protected void onPause() {

        //save the instance variables
        SharedPreferences.Editor editor = mSavedValues.edit();
        editor.putString("billAmountString", mBillAmountString);
        editor.putFloat("tipPercent", mTipPercent);
        editor.putInt("split", split);
        editor.apply();

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //get instance variables
        mBillAmountString = mSavedValues.getString("billAmountString", "");
        mTipPercent = mSavedValues.getFloat("tipPercent", .15f);
        split = mSavedValues.getInt("split", 1);

        //set the bill amount on its widget
        mSubtotalET.setText(mBillAmountString);

        //call the calculate and display method
        calculateAndDisplay();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mApplyButton:
                calculateAndDisplay();
                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if(actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED){
            calculateAndDisplay();
        }

        Toast.makeText(getApplicationContext(), "ActionID" + actionId, Toast.LENGTH_LONG).show();
        return false;
    }
}
