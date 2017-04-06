package io.steemapp.steemy.fragments;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.steemapp.steemy.R;
import io.steemapp.steemy.views.SteemyTextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class WalletFragment extends Fragment {

    private String mSteem;
    private String mSteemPower;
    private String mSteemDollars;

    private static final String STEEM_ARG = "steem";
    private static final String STEEM_POWER_ARG = "steempower";
    private static final String STEEM_DOLLARS_ARG = "steemdollars";

    private View mRootView;
    private SteemyTextView mSteemView;
    private SteemyTextView mSteemPowerView;
    private SteemyTextView mSteemDollarsView;

    public WalletFragment() {
    }

    public static WalletFragment getNewInstance(String steem, String steempower, String steemdollars){
        WalletFragment fragment = new WalletFragment();
        Bundle b = new Bundle();
        b.putString(STEEM_ARG, steem);
        b.putString(STEEM_POWER_ARG, steempower);
        b.putString(STEEM_DOLLARS_ARG, steemdollars);
        fragment.setArguments(b);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b;
        if((b = getArguments()) != null){
            mSteem = b.getString(STEEM_ARG);
            mSteemPower = b.getString(STEEM_POWER_ARG);
            mSteemDollars = b.getString(STEEM_DOLLARS_ARG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_wallet, container, false);

        initViews();

        return mRootView;
    }

    private void initViews(){
        mSteemView = (SteemyTextView) mRootView.findViewById(R.id.steem_amount);
        mSteemPowerView = (SteemyTextView)mRootView.findViewById(R.id.steem_power_amount);
        mSteemDollarsView = (SteemyTextView)mRootView.findViewById(R.id.steem_dollars_amount);

        mSteemView.setText(mSteem);
        mSteemPowerView.setText(mSteemPower);
        mSteemDollarsView.setText(mSteemDollars);
    }
}
