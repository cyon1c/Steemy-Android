package io.steemapp.steemy.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.steemapp.steemy.R;
import io.steemapp.steemy.views.SteemyButton;

/**
 * A placeholder fragment containing a simple view.
 */
public class SettingsFragment extends Fragment {

    SettingsFragmentInteractionListener mListener;

    private View mRootView;
    private SteemyButton mLogoutButton;
    private SteemyButton mTOSButton;
    private SteemyButton mPrivacyButton;
    private SteemyButton mOSSButton;
    private SteemyButton mManageAuthButton;

    public SettingsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_settings, container, false);

        mLogoutButton = (SteemyButton)mRootView.findViewById(R.id.logout_button);
        mTOSButton = (SteemyButton)mRootView.findViewById(R.id.tos);
        mPrivacyButton = (SteemyButton)mRootView.findViewById(R.id.privacy_policy);
        mOSSButton = (SteemyButton)mRootView.findViewById(R.id.open_source);

        mTOSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onTOSClicked();
            }
        });

        mPrivacyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onPrivacyClicked();
            }
        });

        mOSSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onOSSClicked();
            }
        });

        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onLogoutClicked();
            }
        });

        mManageAuthButton = (SteemyButton)mRootView.findViewById(R.id.manage_auth_button);
        mManageAuthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onManageAuthClicked();
            }
        });

        return mRootView;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            mListener = (SettingsFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement SettingsFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface SettingsFragmentInteractionListener {
        public void onLogoutClicked();
        public void onTOSClicked();
        public void onPrivacyClicked();
        public void onOSSClicked();
        public void onManageAuthClicked();
    }
}
