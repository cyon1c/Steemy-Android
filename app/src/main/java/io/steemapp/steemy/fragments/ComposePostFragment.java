package io.steemapp.steemy.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.steemapp.steemy.R;
import io.steemapp.steemy.listeners.OnDiscussionListInteractionListener;
import io.steemapp.steemy.views.SteemyAutoCompleteTextView;
import io.steemapp.steemy.views.SteemyEditText;
import jp.wasabeef.richeditor.RichEditor;

/**
 * A placeholder fragment containing a simple view.
 */
public class ComposePostFragment extends Fragment {

    private View mRootView;
    private TextView mPreview;
    private RichEditor mRichEditor;

    private ComposePostFragmentListener mListener;


    public ComposePostFragment() {
    }

    public static ComposePostFragment newInstance(){
        return new ComposePostFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_compose_post, container, false);

        initializeRichEditor();
//        mTitleEditText = (SteemyEditText)mRootView.findViewById(R.id.compose_post_title);
//        mCategoryAutocomplete = (SteemyAutoCompleteTextView)mRootView.findViewById(R.id.new_post_category_chooser);
//        mCategoryAutocomplete.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });

        return  mRootView;
    }

    public void initializeRichEditor(){
        mRichEditor = (RichEditor)mRootView.findViewById(R.id.editor);
        mRichEditor.setEditorHeight(200);
        mRichEditor.setEditorFontSize(22);
        mRichEditor.setPadding(10, 10, 10, 10);
        mRichEditor.setEditorFontColor(android.R.color.black);
        mRichEditor.focusEditor();
        mRichEditor.setPlaceholder("Insert text here...");
//        mPreview = (TextView) mRootView.findViewById(R.id.preview);
        mRichEditor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
            @Override public void onTextChange(String text) {
//                mPreview.setText(text);
            }
        });

        mRootView.findViewById(R.id.action_undo).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mRichEditor.undo();
            }
        });

        mRootView.findViewById(R.id.action_redo).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mRichEditor.redo();
            }
        });

        mRootView.findViewById(R.id.action_bold).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mRichEditor.setBold();
            }
        });

        mRootView.findViewById(R.id.action_italic).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mRichEditor.setItalic();
            }
        });

        mRootView.findViewById(R.id.action_underline).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mRichEditor.setUnderline();
            }
        });

        mRootView.findViewById(R.id.action_heading1).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mRichEditor.setHeading(1);
            }
        });

        mRootView.findViewById(R.id.action_heading2).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mRichEditor.setHeading(2);
            }
        });

        mRootView.findViewById(R.id.action_heading3).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mRichEditor.setHeading(3);
            }
        });

        mRootView.findViewById(R.id.action_blockquote).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mRichEditor.setBlockquote();
            }
        });

        mRootView.findViewById(R.id.action_insert_image).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mListener.onInsertImageClicked();
//                insertImage(null);
            }
        });

        mRootView.findViewById(R.id.action_insert_link).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mListener.onInsertLinkClicked();
            }
        });
//        mRichEditor.setBullets();
//        mRichEditor.setNumbers();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ComposePostFragmentListener) {
            mListener = (ComposePostFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ComposePostFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface ComposePostFragmentListener{
        void onInsertLinkClicked();
        void onInsertImageClicked();
    }


    public void insertLink(String link, String title){
        mRichEditor.focusEditor();
        mRichEditor.insertLink(link, title);
    }

    public void insertImage(String link){
        mRichEditor.insertImage("https://i.imgur.com/p6tFOWW.jpg", "image posted from steemy");
    }

    @Override
    public void onResume() {
        super.onResume();
        mRichEditor.focusEditor();
    }
}
