package io.steemapp.steemy.fragments;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;

import io.steemapp.steemy.R;
import io.steemapp.steemy.SteemyGlobals;
import io.steemapp.steemy.adapter.CommentsListAdapter;
import io.steemapp.steemy.models.AccountManager;
import io.steemapp.steemy.models.CommentTree;
import io.steemapp.steemy.models.Discussion;
import io.steemapp.steemy.views.SteemyButton;
import io.steemapp.steemy.views.SteemyTextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class DiscussionFragment extends Fragment {

    private View mRootView;

    private CommentTree mComments;

    private WebView mDiscussionWebView;
    private WebView mCommentsWebView;
    private ViewFlipper mWebFlipper;
    private SteemyTextView mDiscussionTitle;
    private SteemyTextView mDiscussionAuthor;
    private SteemyTextView mDiscussionTimeStamp;
    private SteemyTextView mDiscussionPayout;
    private ImageView mUpvoteButton;
    private SteemyButton mCommentsButton;
    private SteemyButton mReplyButton;

    private CommentsListAdapter mAdapter;
    private RecyclerView mCommentsRecycler;

    private Discussion mRootDiscussion;

    private DiscussionListener mListener;

    private static DiscussionFragment instance;

    public static DiscussionFragment instance(){
        if(instance == null){
            instance = new DiscussionFragment();
        }

        return instance;
    }

    public DiscussionFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_discussion, container, false);

        WebView.setWebContentsDebuggingEnabled(true);
        mDiscussionWebView = (WebView)mRootView.findViewById(R.id.discussion_webview);
        generateWebViewSettings(mDiscussionWebView);
        mCommentsWebView = (WebView)mRootView.findViewById(R.id.comments_webview);
        generateWebViewSettings(mCommentsWebView);
        mWebFlipper = (ViewFlipper)mRootView.findViewById(R.id.discussion_flipper);
        mWebFlipper.setDisplayedChild(0);

        mDiscussionTitle = (SteemyTextView)mRootView.findViewById(R.id.discussion_title);
        mDiscussionAuthor = (SteemyTextView)mRootView.findViewById(R.id.discussion_author);
        mDiscussionTimeStamp = (SteemyTextView)mRootView.findViewById(R.id.discussion_time_stamp);
        mUpvoteButton = (ImageView)mRootView.findViewById(R.id.upvote_icon);
        mCommentsButton = (SteemyButton)mRootView.findViewById(R.id.comments_button);
        mReplyButton = (SteemyButton)mRootView.findViewById(R.id.reply_button);
        mReplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.replyClicked(mRootDiscussion.getAuthor(), mRootDiscussion.getPermlink());
            }
        });
        mDiscussionPayout = (SteemyTextView)mRootView.findViewById(R.id.discussion_payout);

        mCommentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mWebFlipper.getDisplayedChild() == 0) {
                    mCommentsButton.setText("View Post");
                    mWebFlipper.setDisplayedChild(1);
                }else{
                    mCommentsButton.setText("View Comments");
                    mWebFlipper.setDisplayedChild(0);
                }
            }
        });

        mUpvoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mUpvoteButton.isSelected()) {
                    mListener.vote(mRootDiscussion, 10000);
                    mUpvoteButton.setSelected(true);
                }else {
                    mListener.vote(mRootDiscussion, 0);
                    mUpvoteButton.setSelected(false);
                }
            }
        });

        mUpvoteButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mListener.customVoteRequested(mRootDiscussion, true);
                return true;
            }
        });
//        mCommentsRecycler = (RecyclerView)mRootView.findViewById(R.id.comments_recyclerview);

        return mRootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DiscussionListener) {
            mListener = (DiscussionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement DiscussionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void updateComments(CommentTree comments){
        mComments = comments;
        mRootDiscussion = mComments.getRoot();

        WebViewArgs args = createDiscussionPage("index.html", "style.css", mRootDiscussion.getBody());
        mDiscussionWebView.loadData(args.html, args.typeEncoding, args.historyUrl);

        mDiscussionTitle.setText(mRootDiscussion.getTitle());
        mDiscussionAuthor.setText(mRootDiscussion.getAuthor());
        mDiscussionAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.profileClicked(mRootDiscussion.getAuthor());
            }
        });
        if(mRootDiscussion.getTimeAgo() != null)
            mDiscussionTimeStamp.setText(mRootDiscussion.getTimeAgo().substring("posted ".length()));

        mDiscussionPayout.setText(mRootDiscussion.getHumanReadablePayout());

        String currentAccount = AccountManager.get(null, null).getAccountName();
        if(currentAccount != null && mRootDiscussion.hasVoted(currentAccount))
            mUpvoteButton.setSelected(true);
        else
            mUpvoteButton.setSelected(false);

        ArrayList<Discussion> sortedComments = null;
        if(mComments.getRoot().getChildren() > 0) {
            try {
                sortedComments = mComments.getSortedList(SteemyGlobals.SORTS.TRENDING);
                WebViewArgs commentArgs = createCommentsPage("comments.html", "comments.css", sortedComments);
                mCommentsWebView.loadData(commentArgs.html, commentArgs.typeEncoding, commentArgs.historyUrl);
            } catch (ParseException e) {

            }
        }
//        if(sortedComments != null) {
//            mAdapter = new CommentsListAdapter(sortedComments);
//            mCommentsRecycler.setLayoutManager(new LinearLayoutManager(this.getActivity()));
//            mCommentsRecycler.setAdapter(mAdapter);
//        }else{
//            Log.i("DiscussionFragment", "Something has gone horribly wrong in the Discussion object.");
//        }
    }

    public interface DiscussionListener {
        // TODO: Update argument type and name
        void update();
        void vote(Discussion discussion, int voteWeight);
        void customVoteRequested(Discussion discussion, boolean upvote);
        void profileClicked(String name);
        void payoutClicked(String author, String permlink);
        void votesClicked(String author, String permlink);
        void replyClicked(String author, String permlink);
    }

    protected void generateWebViewSettings(WebView webview){

        webview.getSettings().setJavaScriptEnabled(true);

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url.contains("author://")){
                    mListener.profileClicked(url.substring("author://".length()));
                    return true;
                }else if(url.contains("reply://")){
                    String permlink, author;
                    url = url.substring("reply://".length());
                    author = url.substring(0, url.indexOf("/"));
                    permlink = url.substring(url.indexOf("/")+1, url.length());
                    mListener.replyClicked(author, permlink);
                    return true;
                }else if(url.contains("votes://")){
                    String permlink, author;
                    url = url.substring("votes://".length());
                    author = url.substring(0, url.indexOf("/"));
                    permlink = url.substring(url.indexOf("/")+1, url.length());
                    mListener.votesClicked(author, permlink);
                }else if(url.contains("payout://")){
                    String permlink, author;
                    url = url.substring("payout://".length());
                    author = url.substring(0, url.indexOf("/"));
                    permlink = url.substring(url.indexOf("/")+1, url.length());
                    mListener.payoutClicked(author, permlink);
                }
                return false;
            }
        });
        webview.setWebChromeClient(new WebChromeClient());
        webview.getSettings().setLoadsImagesAutomatically(true);
    }

    public WebViewArgs createDiscussionPage(String htmlFileName, String cssFileName, String body){
        WebViewArgs args = new WebViewArgs();

        AssetManager assetManager = this.getActivity().getAssets();
        try {
            InputStream is = assetManager.open(htmlFileName);
            BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String str;
            StringBuilder builder = new StringBuilder();
            while((str = in.readLine()) != null){
                builder.append(str);
            }

            in.close();
            args.html = builder.toString();

            is = assetManager.open(cssFileName);
            in = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            builder = new StringBuilder();
            while((str = in.readLine()) != null){
                builder.append(str);
            }

            in.close();
            args.css = builder.toString();
        }catch (IOException e){
            return null;
        }
        args.html = args.html.replace("<contentgoeshere/>", body);
        args.html = args.html.replace("<cssgoeshere/>", args.css);
        return args;
    }

    public WebViewArgs createCommentsPage(String htmlFileName, String cssFileName, ArrayList<Discussion> comments){
        WebViewArgs args = new WebViewArgs();

        String body;
        StringBuilder builder = new StringBuilder();
        for(Discussion c : comments){
            builder.append(c.getBody());
        }
        body = builder.toString();

        AssetManager assetManager = this.getActivity().getAssets();
        try {
            InputStream is = assetManager.open(htmlFileName);
            BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String str;
            builder = new StringBuilder();
            while((str = in.readLine()) != null){
                builder.append(str);
            }

            in.close();
            args.html = builder.toString();

            is = assetManager.open(cssFileName);
            in = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            builder = new StringBuilder();
            while((str = in.readLine()) != null){
                builder.append(str);
            }

            in.close();
            args.css = builder.toString();
        }catch (IOException e){
            return null;
        }
        args.html = args.html.replace("<contentgoeshere/>", body);
        args.html = args.html.replace("<cssgoeshere/>", args.css);
        return args;
    }

    protected class WebViewArgs{
        public String css;
        public String html;
        public String typeEncoding;
        public String historyUrl;

        public  WebViewArgs(){
            typeEncoding = "text/html; charset=UTF-8";
            historyUrl = null;
        }

    }
}
