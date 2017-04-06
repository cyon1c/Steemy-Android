package io.steemapp.steemy.fragments;

import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import io.steemapp.steemy.R;
import io.steemapp.steemy.SteemyGlobals;
import io.steemapp.steemy.views.SteemyButton;
import io.steemapp.steemy.views.SteemyTextInputEditText;
import io.steemapp.steemy.views.SteemyTextView;

/**
 * Created by John on 10/3/2015.
 */
public class LogInFragment extends Fragment {

    OnFragmentInteractionListener mListener;

    View rootView;

    SteemyButton mLoginButton;

    SteemyTextInputEditText mUserField;

    SteemyTextInputEditText mPassField;


    TextInputLayout mUserInputField;

    TextInputLayout mPassInputField;

    ViewFlipper mKeyTypeFlipper;
    ImageView mLeftArrow, mRightArrow;
    int mCurrentKeyType = 0;

    public static LogInFragment newInstance(){
        return new LogInFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_login, container, false);

        mLoginButton = (SteemyButton)rootView.findViewById(R.id.login_button);
        mUserInputField = (TextInputLayout)rootView.findViewById(R.id.username_input_group);
        mPassInputField = (TextInputLayout) rootView.findViewById(R.id.password_input_group);

        mUserField = (SteemyTextInputEditText)rootView.findViewById(R.id.username_field);
        mUserField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean handled = false;
                if (i == EditorInfo.IME_ACTION_NEXT) {
                    mPassField.requestFocus();
                    handled = true;
                }
                return handled;
            }
        });
        mPassField = (SteemyTextInputEditText)rootView.findViewById(R.id.password_field);
        mPassField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean handled = false;
                if (i == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                    handled = true;
                }
                return handled;
            }
        });

        mUserInputField.setHint(getString(R.string.label_username));

        mPassInputField.setHint(getString(R.string.label_posting_key));

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onLoginClicked(mUserField.getText().toString(), mPassField.getText().toString(), mCurrentKeyType);
            }
        });

        initializeKeyTypeStates();

        return rootView;
    }

    protected void initializeKeyTypeStates(){
        mKeyTypeFlipper = (ViewFlipper)rootView.findViewById(R.id.key_type_flipper);
        mLeftArrow = (ImageView)rootView.findViewById(R.id.reverse_key_type_button);
        mRightArrow = (ImageView)rootView.findViewById(R.id.forward_key_type_button);

        mLeftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentKeyType--;
                updateKeyTypeState();
            }
        });

        mRightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentKeyType++;
                updateKeyTypeState();
            }
        });
    }

    private void updateKeyTypeState(){
        if(mCurrentKeyType > 2){
            mCurrentKeyType = 0;
        }else if(mCurrentKeyType < 0){
            mCurrentKeyType = 2;
        }

        mKeyTypeFlipper.setDisplayedChild(mCurrentKeyType);
        mPassInputField.setHint(((SteemyTextView)mKeyTypeFlipper.getCurrentView()).getText().toString());
    }

    private void updateKeyTypeState(int state){
        mCurrentKeyType = state;

        mKeyTypeFlipper.setDisplayedChild(mCurrentKeyType);
        mPassInputField.setHint(((SteemyTextView)mKeyTypeFlipper.getCurrentView()).getText().toString());
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setUsername(String user){
        mUserField.setText(user);
    }

    public void setImport(int key_type){
        if(key_type == SteemyGlobals.KEY_TYPE.POSTING.getValue()){
            updateKeyTypeState(0);
        }else if(key_type == SteemyGlobals.KEY_TYPE.ACTIVE.getValue()){
            updateKeyTypeState(1);
        }
    }

    public interface OnFragmentInteractionListener {

        void onLoginClicked(String user, String pass, int keyType);
    }

    public void failedLoginCredentials(){
        mPassInputField.setError(getString(R.string.label_bad_creds));
    }

    public void emptyUserField(){
        mUserInputField.setError(getString(R.string.label_empty_user));
    }

    public void emptyPasswordField(){
        mPassInputField.setError(getString(R.string.label_empty_pass));
    }

    protected class KeyTypeAdapter extends RecyclerView.Adapter<KeyTypeViewHolder>{

        String[] keyTypes = {"Posting", "Master", "Active"};

        public KeyTypeAdapter() {
            super();
        }

        @Override
        public KeyTypeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.key_type, parent, false);
            return new KeyTypeViewHolder(view);
        }

        @Override
        public void onBindViewHolder(KeyTypeViewHolder holder, int position) {
            holder.text.setText(keyTypes[position]);
        }

        @Override
        public int getItemCount() {
            return keyTypes.length;
        }
    }

    protected class KeyTypeViewHolder extends RecyclerView.ViewHolder{

        SteemyTextView text;

        public KeyTypeViewHolder(View itemView) {
            super(itemView);
            text = (SteemyTextView)itemView;
        }
    }
}
