package io.steemapp.steemy.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;

import io.steemapp.steemy.R;

/**
 * Created by john.white on 10/22/16.
 */
public class SteemyKeyPad extends LinearLayout {

    private SteemyButton zeroKey, oneKey, twoKey, threeKey, fourKey, fiveKey, sixKey, sevenKey, eightKey, nineKey, decimalKey, backKey;
    private SteemyTextView mAmountPreview;
    private ViewFlipper mAmountFlipper;
    private SteemyButton mSBD, mSTM;
    private boolean mCurrencyIsSTM;

    private final static String dollar = "$%s";
    private final static int MAX_DIGITS = 6;

    private StringBuilder mAmountBuilder = new StringBuilder();

    private OnClickListener keyListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.key_pad_zero:
                    if(!isEmpty())
                        addValue("0");
                    break;
                case R.id.key_pad_one:
                    addValue("1");
                    break;
                case R.id.key_pad_two:
                    addValue("2");
                    break;
                case R.id.key_pad_three:
                    addValue("3");
                    break;
                case R.id.key_pad_four:
                    addValue("4");
                    break;
                case R.id.key_pad_five:
                    addValue("5");
                    break;
                case R.id.key_pad_six:
                    addValue("6");
                    break;
                case R.id.key_pad_seven:
                    addValue("7");
                    break;
                case R.id.key_pad_eight:
                    addValue("8");
                    break;
                case R.id.key_pad_nine:
                    addValue("9");
                    break;
                case R.id.key_pad_decimal:
                    if(!isThereADecimal())
                        addValue(".");
                    break;
                case R.id.key_pad_back:
                    if(!isEmpty())
                        backspace();
                    break;
            }
        }
    };

    public SteemyKeyPad(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.widget_keypad, this);

        zeroKey = (SteemyButton)findViewById(R.id.key_pad_zero);
        zeroKey.setOnClickListener(keyListener);
        oneKey = (SteemyButton)findViewById(R.id.key_pad_one);
        oneKey.setOnClickListener(keyListener);
        twoKey = (SteemyButton)findViewById(R.id.key_pad_two);
        twoKey.setOnClickListener(keyListener);
        threeKey = (SteemyButton)findViewById(R.id.key_pad_three);
        threeKey.setOnClickListener(keyListener);
        fourKey = (SteemyButton)findViewById(R.id.key_pad_four);
        fourKey.setOnClickListener(keyListener);
        fiveKey = (SteemyButton)findViewById(R.id.key_pad_five);
        fiveKey.setOnClickListener(keyListener);
        sixKey = (SteemyButton)findViewById(R.id.key_pad_six);
        sixKey.setOnClickListener(keyListener);
        sevenKey = (SteemyButton)findViewById(R.id.key_pad_seven);
        sevenKey.setOnClickListener(keyListener);
        eightKey = (SteemyButton)findViewById(R.id.key_pad_eight);
        eightKey.setOnClickListener(keyListener);
        nineKey = (SteemyButton)findViewById(R.id.key_pad_nine);
        nineKey.setOnClickListener(keyListener);
        decimalKey = (SteemyButton)findViewById(R.id.key_pad_decimal);
        decimalKey.setOnClickListener(keyListener);
        backKey = (SteemyButton)findViewById(R.id.key_pad_back);
        backKey.setOnClickListener(keyListener);

        mAmountPreview = (SteemyTextView)findViewById(R.id.transfer_amount);
        mAmountFlipper = (ViewFlipper)findViewById(R.id.amount_flipper);
        mSTM = (SteemyButton)findViewById(R.id.stm_button);
        mSTM.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mSTM.setBackground(getResources().getDrawable(R.drawable.button_color_primary_stroke));
                mSTM.setTextColor(getResources().getColor(android.R.color.white));
                mCurrencyIsSTM = true;
                mSBD.setBackground(null);
                mSBD.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            }
        });
        mSBD = (SteemyButton)findViewById(R.id.sbd_button);
        mSBD.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mSBD.setBackground(getResources().getDrawable(R.drawable.button_color_primary_stroke));
                mSBD.setTextColor(getResources().getColor(android.R.color.white));
                mCurrencyIsSTM = false;
                mSTM.setBackground(null);
                mSTM.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            }
        });


        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }



    private void backspace(){
        mAmountBuilder.deleteCharAt(mAmountBuilder.length()-1);
        mAmountPreview.setText(String.format(dollar, mAmountBuilder.toString()));
        if(mAmountBuilder.toString().isEmpty())
            mAmountFlipper.setDisplayedChild(0);
    }

    private boolean isThereADecimal(){
        return mAmountBuilder.toString().contains(".");
    }

    private void addValue(String value){
        if(!isPrecisionMaxed()) {
            if(!tooManyDigits() || value.equalsIgnoreCase(".")) {
                mAmountFlipper.setDisplayedChild(1);

                if (value.equalsIgnoreCase(".") && isEmpty())
                    mAmountBuilder.append("0");
                mAmountBuilder.append(value);
                mAmountPreview.setText(String.format(dollar, mAmountBuilder.toString()));
            }
        }
    }

    private boolean isEmpty(){
        return mAmountBuilder.toString().isEmpty();
    }

    private boolean tooManyDigits(){
        if(isThereADecimal())
            return false;
        else{
            return(mAmountBuilder.toString().length() >= MAX_DIGITS);
        }
    }

    private boolean isPrecisionMaxed(){
        if(!isThereADecimal())
            return false;
        else {
            int index = mAmountBuilder.indexOf(".");
            int length = mAmountBuilder.length()-1;
            return ((length-index)>2);
        }
    }

    public String getValue(){
        return mAmountBuilder.toString();
    }

    public String getCurrencyType(){
        if(mCurrencyIsSTM)
            return "STEEM";
        else
            return "SBD";
    }
}
