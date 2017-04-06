package io.steemapp.steemy.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import io.steemapp.steemy.R;
import io.steemapp.steemy.views.SteemyButton;
import io.steemapp.steemy.views.SteemyKeyPad;
import io.steemapp.steemy.views.SteemyTextInputEditText;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TransferFragmentListener} interface
 * to handle interaction events.
 * Use the {@link TransferFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TransferFragment extends Fragment {

    private TransferFragmentListener mListener;

    public TransferFragment() {
        // Required empty public constructor
    }

    public static TransferFragment newInstance() {
        TransferFragment fragment = new TransferFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    protected View mRootView;
    protected Spinner mTransferSpinner;
    protected TextInputLayout mTransferAccountLayout;
    protected SteemyTextInputEditText mTransferAccountSelector;
    protected SteemyKeyPad mKeyPad;
    protected SteemyButton mSubmitButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_transfer, container, false);

        mTransferSpinner = (Spinner) mRootView.findViewById(R.id.transfer_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.transfer_spinner, R.layout.spinner_transfer_type);
        adapter.setDropDownViewResource(R.layout.spinner_transfer_type);
        mTransferSpinner.setAdapter(adapter);

        mKeyPad = (SteemyKeyPad)mRootView.findViewById(R.id.transfer_keypad);
        mTransferAccountLayout = (TextInputLayout)mRootView.findViewById(R.id.transfer_account_layout);
        mTransferAccountSelector = (SteemyTextInputEditText)mRootView.findViewById(R.id.transfer_account_edit_text);
        mSubmitButton = (SteemyButton)mRootView.findViewById(R.id.submit_transfer_button);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onSubmitClicked(mTransferAccountSelector.getText().toString());
            }
        });

        mTransferAccountLayout.setHint("Account Name");
        mTransferAccountSelector.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean handled = false;
                if (i == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                    handled = true;
                    mTransferAccountSelector.clearFocus();
                }
                return handled;
            }
        });

        return mRootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TransferFragmentListener) {
            mListener = (TransferFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void incorrectAccountName(){
        mTransferAccountLayout.setError("Account not found!");
    }

    public String getTransferAmount(){
        return mKeyPad.getValue();
    }

    public String getTransferCurrencyType(){
        return mKeyPad.getCurrencyType();
    }

    public interface TransferFragmentListener {
        void onSubmitClicked(String accountName);
    }
}
