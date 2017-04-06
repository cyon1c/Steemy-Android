package io.steemapp.steemy.activities;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.steemapp.steemy.R;
import io.steemapp.steemy.SteemyGlobals;
import io.steemapp.steemy.adapter.TagListAdapter;
import io.steemapp.steemy.events.CategoriesEvent;
import io.steemapp.steemy.events.HtmlToMarkdownEvent;
import io.steemapp.steemy.events.NoNetworkEvent;
import io.steemapp.steemy.events.NetworkErrorEvent;
import io.steemapp.steemy.events.NotLoggedInEvent;
import io.steemapp.steemy.events.ReceivedImageLinkEvent;
import io.steemapp.steemy.events.SuccessfulPostEvent;
import io.steemapp.steemy.fragments.ComposePostFragment;
import io.steemapp.steemy.models.Account;
import io.steemapp.steemy.models.Discussion;
import io.steemapp.steemy.views.SteemyAutoCompleteTextView;
import io.steemapp.steemy.views.SteemyEditText;
import jp.wasabeef.richeditor.RichEditor;

public class ComposePostActivity extends AbstractActivity implements ComposePostFragment.ComposePostFragmentListener{

    protected ComposePostFragment mFragment;
    protected Account mAuthor;
    protected FloatingActionButton mFAB;

    protected Uri mPhotoURI;

    protected ArrayList<String> mTagList;

    private View mSubmitDialogView;
    private SteemyEditText mTitleEditText;
    private SteemyAutoCompleteTextView mCategoryAutoComplete;
    private SteemyEditText mTagsEditText;
    private RecyclerView mTagRecycler;
    private TagListAdapter mTagAdapter;
    private AlertDialog mSubmitDialog;
    private RichEditor mRichEditor;

    private View mLinkDialogView;
    private SteemyEditText mLinkTitleEditText;
    private SteemyEditText mLinkEditText;
    private AlertDialog mLinkDialog;

    private static final int PHOTO_REQUEST_CODE = 3000;
    private static final int REQUEST_READ_STORAGE = 5000;

    private boolean mIsReply = false;
    private String mParentAuthor;
    private String mParentPermlink;
    private String mCategory;
    private Discussion mDiscussion;

    protected ArrayList<String> mImageURLs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFragment = ComposePostFragment.newInstance();

        mEventBus.register(this);

        mAuthor = mAccountManager.getCurrentAccount();
        mTagList = new ArrayList<>();

        mFAB = (FloatingActionButton) findViewById(R.id.fab);
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mIsReply)
                    showSubmitDialog();
                else
                    assemblePost();
            }
        });

        mSubmitDialog = buildSubmitDialog();
        mLinkDialog = buildLinkDialog();
        initializeRichEditor();

        initDiscussion(getIntent());
        if(SteemyGlobals.areCategoriesInSync()){
            mCategoryAdapter.updateItemList(mCategoryData.getCategoryList());
            mCategoryAdapter.notifyDataSetChanged();
        }
    }

    private void initDiscussion(Intent i){
        Bundle b = i.getExtras();

        mDiscussion = new Discussion();

        mIsReply = b.getBoolean("reply");
        if(mIsReply){
            mParentAuthor = b.getString("parentAuthor");
            mDiscussion.setParentAuthor(mParentAuthor);
            mParentPermlink = b.getString("parentPermlink");
            mDiscussion.setParentPermlink(mParentPermlink);
            mCategory = b.getString("category");
            mDiscussion.setCategory(mCategory);
            getSupportActionBar().setTitle("Reply to " + mParentAuthor);
        }

        if(mAccountManager.isLoggedIn())
            mDiscussion.setAuthor(mAuthor.getName());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PHOTO_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                if(data.getData() != null) {
                    mPhotoURI = data.getData();
                }
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mPhotoURI);
                    mService.uploadImage(mAuthor.getName(), bitmap);
                }catch (IOException e){

                }

            }
        }
//        if(error){
//            showErrorDialog("Image Error", "It seems there was a problem getting your photo ready. If this problem persists, please contact us at contact@steemapp.io.");
    }

    @Override
    public void onInsertImageClicked() {
        checkForPermission();
    }

    @Subscribe
    public void onEvent(ReceivedImageLinkEvent event){
        mImageURLs.add(event.link);
        mRichEditor.insertImage(event.link, "image posted from steemy");
    }

    @Subscribe
    public void onEvent(HtmlToMarkdownEvent event){
        mDiscussion.setBody(event.body);
        mTransactor.comment(mDiscussion);
    }

    @Subscribe
    public void onEvent(final SuccessfulPostEvent event){
        new AlertDialog.Builder(this)
                .setTitle("Success")
                .setMessage("Post successfully created")
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ComposePostActivity.this.finish();
                    }
                }).create().show();
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        image.deleteOnExit();
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    @Override
    public void onInsertLinkClicked() {
        mLinkDialog.show();
    }

    public static Intent intent(boolean reply, Activity activity, String category, String parentAuthor, String parentPermlink){
        Intent i = new Intent(activity, ComposePostActivity.class);

        Bundle b = new Bundle();
        b.putBoolean("reply", reply);
        if(reply){
            b.putString("category", category);
            b.putString("parentAuthor", parentAuthor);
            b.putString("parentPermlink", parentPermlink);
        }
        i.putExtras(b);

        return i;
    }

    protected void checkForPermission(){
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_STORAGE);
        }else{
            launchGetImage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case REQUEST_READ_STORAGE:
                if(grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    launchGetImage();
                }else{
                    showErrorDialog("Permissions Error", "We will not be able to load any images from your phone without that permission!");
                }
        }
    }

    protected void launchGetImage(){
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        Intent pi = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (pi.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                mPhotoURI = FileProvider.getUriForFile(this,
                        "io.steemapp.steemy.fileprovider",
                        photoFile);
                pi.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoURI);
            }
        }

        Intent chooser = Intent.createChooser(i, "Select or take a picture");
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pi});
        startActivityForResult(chooser, PHOTO_REQUEST_CODE);
    }

    @Override
    public int getContentView() {
        return R.layout.activity_compose_post;
    }

    @Override
    public void setActivity() {
        activity = this;
    }

    @Subscribe
    public void onEvent(CategoriesEvent event){
        SteemyGlobals.setCategories(event.mList);
        mCategoryData = SteemyGlobals.steemyCategories;
        mCategoryAdapter.updateItemList(mCategoryData.getCategoryList());
        mCategoryAdapter.notifyDataSetChanged();
    }

    private AlertDialog buildSubmitDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        mSubmitDialogView = ((LayoutInflater)activity.getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.dialog_post, null, false);

        mTitleEditText = (SteemyEditText) mSubmitDialogView.findViewById(R.id.compose_post_title);

        mCategoryAutoComplete = (SteemyAutoCompleteTextView) mSubmitDialogView.findViewById(R.id.new_post_category_chooser);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.category_list_item, R.id.category_title, mCategoryData.getCategoryNames());
        mCategoryAutoComplete.setAdapter(adapter);
        mCategoryAutoComplete.setThreshold(1);

        mTagsEditText = (SteemyEditText) mSubmitDialogView.findViewById(R.id.compose_post_tags);
        mTagsEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == 5){
                    addTag(textView.getText().toString());
                    mTagsEditText.setText("");
                    mTagsEditText.requestFocus();
                }
                return false;
            }
        });

        mTagRecycler = (RecyclerView) mSubmitDialogView.findViewById(R.id.tag_recycler);
        mTagAdapter = new TagListAdapter();
        mTagRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mTagRecycler.setAdapter(mTagAdapter);

        builder.setView(mSubmitDialogView);
        builder.setTitle("Post Title, Category, and Tags");
        builder.setPositiveButton("Submit Post", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                assemblePost();
            }
        });

        return builder.create();
    }

    private AlertDialog buildLinkDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        mLinkDialogView = ((LayoutInflater)activity.getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.dialog_insert_link, null, false);

        mLinkEditText = (SteemyEditText)mLinkDialogView.findViewById(R.id.insert_link_edittext);
        mLinkTitleEditText = (SteemyEditText)mLinkDialogView.findViewById(R.id.link_title_edittext);

        builder.setView(mLinkDialogView);
        builder.setPositiveButton("Insert Link", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mRichEditor.insertLink(mLinkEditText.getText().toString(), mLinkTitleEditText.getText().toString());
            }
        });

        return builder.create();
    }

    private void showSubmitDialog(){
        if(mSubmitDialog != null)
            mSubmitDialog.show();
    }

    protected void addTag(String tag){
        mTagAdapter.addTag(tag);
    }

    protected void assemblePost(){
        if(mAccountManager.isLoggedIn()) {
            try {
                mDiscussion.setTitle(mTitleEditText.getText().toString());
                mDiscussion.setCategory(mCategoryAutoComplete.getText().toString());
                if (!mIsReply) {
                    mDiscussion.setParentPermlink(mDiscussion.getCategory());
                    mDiscussion.setParentAuthor("");
                }
                JSONObject jsonMetadata = new JSONObject();
                JSONArray tagsArray = new JSONArray(mTagAdapter.getData());
                jsonMetadata.put("tags", tagsArray);
                if (mImageURLs.size() > 0) {
                    JSONArray imageArray = new JSONArray(mImageURLs);
                    jsonMetadata.put("image", imageArray);
                }
                mDiscussion.setJsonMetadata(jsonMetadata.toString());
                mDiscussion.setPermlinkify(mIsReply, mTitleEditText.getText().toString());
                mService.htmlToMarkdown(mRichEditor.getHtml());
            } catch (JSONException e) {

            }
        }
    }

    public void initializeRichEditor(){
        mRichEditor = (RichEditor)findViewById(R.id.editor);
        mRichEditor.setEditorHeight(200);
        mRichEditor.setEditorFontSize(22);
        mRichEditor.setPadding(10, 10, 10, 10);
        mRichEditor.setEditorFontColor(android.R.color.black);
        mRichEditor.focusEditor();
        mRichEditor.setPlaceholder("Insert text here...");
//        mPreview = (TextView) findViewById(R.id.preview);
        mRichEditor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
            @Override public void onTextChange(String text) {
//                mPreview.setText(text);
            }
        });

        findViewById(R.id.action_undo).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mRichEditor.undo();
            }
        });

        findViewById(R.id.action_redo).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mRichEditor.redo();
            }
        });

        findViewById(R.id.action_bold).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mRichEditor.setBold();
            }
        });

        findViewById(R.id.action_italic).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mRichEditor.setItalic();
            }
        });

        findViewById(R.id.action_underline).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mRichEditor.setUnderline();
            }
        });

        findViewById(R.id.action_heading1).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mRichEditor.setHeading(1);
            }
        });

        findViewById(R.id.action_heading2).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mRichEditor.setHeading(2);
            }
        });

        findViewById(R.id.action_heading3).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mRichEditor.setHeading(3);
            }
        });

        findViewById(R.id.action_blockquote).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mRichEditor.setBlockquote();
            }
        });

        findViewById(R.id.action_insert_image).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onInsertImageClicked();
//                insertImage(null);
            }
        });

        findViewById(R.id.action_insert_link).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onInsertLinkClicked();
            }
        });
//        mRichEditor.setBullets();
//        mRichEditor.setNumbers();
    }

    //ERROR HANDLING HERE
    @Subscribe
    public void onEvent(NetworkErrorEvent event){
        showErrorDialog("Network Error", event.error);
    }

    @Subscribe
    public void onEvent(NotLoggedInEvent event){
        showErrorDialog("Error", "You must be logged in to do that.");
    }

    @Subscribe
    public void onEvent(NoNetworkEvent event){
        showErrorDialog("No Network", null);
    }

    private boolean mIsDialogShowing = false;
    private void showErrorDialog(String title, String message){
        if(!mIsDialogShowing) {
            AlertDialog dialog = SteemyGlobals.buildSteemyErrorDialog(this, title, message);

            dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Okay >", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mIsDialogShowing = false;
                    dialogInterface.dismiss();
                }
            });
            dialog.show();
        }
    }
}
