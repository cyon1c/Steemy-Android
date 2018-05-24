package io.steemapp.steemy.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import io.steemapp.steemy.SteemyGlobals;
import io.steemapp.steemy.events.AccountsEvent;
import io.steemapp.steemy.events.AddedFollowersEvent;
import io.steemapp.steemy.events.BroadcastCommentEvent;
import io.steemapp.steemy.events.BroadcastFollowEvent;
import io.steemapp.steemy.events.BroadcastTransferEvent;
import io.steemapp.steemy.events.BroadcastVoteEvent;
import io.steemapp.steemy.events.CategoriesEvent;
import io.steemapp.steemy.events.CommentsEvent;
import io.steemapp.steemy.events.DiscussionsEvent;
import io.steemapp.steemy.events.FollowEvent;
import io.steemapp.steemy.events.GlobalPropsEvent;
import io.steemapp.steemy.events.HtmlToMarkdownEvent;
import io.steemapp.steemy.events.MyPostsEvent;
import io.steemapp.steemy.events.NoNetworkEvent;
import io.steemapp.steemy.events.NotLoggedInEvent;
import io.steemapp.steemy.events.ReceivedImageLinkEvent;
import io.steemapp.steemy.events.RepliesEvent;
import io.steemapp.steemy.events.RequestGlobalStateEvent;
import io.steemapp.steemy.events.NetworkErrorEvent;
import io.steemapp.steemy.events.SuccessfulPostEvent;
import io.steemapp.steemy.events.VoteEvent;
import io.steemapp.steemy.models.Account;
import io.steemapp.steemy.models.AccountManager;
import io.steemapp.steemy.models.AccountResult;
import io.steemapp.steemy.models.CategoryList;
import io.steemapp.steemy.models.CommentTree;
import io.steemapp.steemy.models.Discussion;
import io.steemapp.steemy.models.DiscussionList;
import io.steemapp.steemy.models.Follower;
import io.steemapp.steemy.models.FollowerList;
import io.steemapp.steemy.models.GlobalResults;
import io.steemapp.steemy.multithreaded.CommentParseManager;

import io.steemapp.steemy.network.rpc.RPCRequest;
import io.steemapp.steemy.transactions.FollowOperation;
import io.steemapp.steemy.transactions.Transaction;
import io.steemapp.steemy.transactions.TransferOperation;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static io.steemapp.steemy.SteemyGlobals.byteArrayToHexString;

/**
 * Created by John on 7/28/2016.
 */
public class SteemyAPIService {

    private static SteemyAPIService instance;

    private SteemyRetrofitService mService;
    private AccountManager mManager;
    private Context mContext;
    private Bus mEventBus;

    private final int FOLLOWER_REQUEST_SIZE = 51;
    private ConnectivityManager mConnectivityManager;

    private Callback<FollowerList> mFollowersCallback;
    private Callback<FollowerList> mFollowingCallback;
    private Callback<FollowerList> mIgnoredCallback;

    public static SteemyAPIService getService(Context appContext, Bus appBus){
        if(instance == null) {
            instance = new SteemyAPIService(appContext, appBus);
            return instance;
        }

        return instance;
    }

    private SteemyAPIService(Context appContext, Bus appBus) {
        mContext = appContext;
        mEventBus = appBus;
        mEventBus.register(this);
        mManager = AccountManager.get(appContext, appBus);
        mService = SteemyNetworkAdapter.getNewService();
    }

    public void getDynamicGlobalProperties(){
        RPCRequest request = RPCRequest.simpleRequest("condenser_api", "get_dynamic_global_properties");
        mService.getDynamicGlobalProperties(request).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.i("Success!", response.body().getAsString());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.i("Failure!", t.getMessage());
            }
        });
    }

    public void getDiscussions(String sortMethod, String tag, String author, String title, final int limit) {
        if(checkForNetwork()) {
            mService.getDiscussions(sortMethod, tag, author, title, limit).enqueue(new Callback<DiscussionList>() {
                @Override
                public void onResponse(Call<DiscussionList> discussions, Response<DiscussionList> response) {
                    if(response.isSuccessful()) {
                        DiscussionList discussionList = response.body();
                        discussionList.processList(limit);
                        mEventBus.post(new DiscussionsEvent(discussionList));
                    }else{
                        try {
                            mEventBus.post(new NetworkErrorEvent(response.errorBody().string()));
                        }catch (IOException e){
                            //ignored
                        }
                    }
                }

                @Override
                public void onFailure(Call<DiscussionList> call, Throwable t) {
                    mEventBus.post(new NetworkErrorEvent(t.getMessage()));
                }
            });
        }else{
            mEventBus.post(new NoNetworkEvent());
        }
    }

    public void getCategories(String sortMethod, String after, int limit) {
        if(checkForNetwork()) {
            if (after == null) {
                after = "none";
            }
            mService.getCategories(sortMethod, after, limit).enqueue(new Callback<CategoryList>() {
                @Override
                public void onResponse(Call<CategoryList> list, Response<CategoryList> response) {
                    if(response.isSuccessful()) {
                        CategoryList categoryList = response.body();
                        categoryList.processCategories();
                        mEventBus.post(new CategoriesEvent(categoryList));
                    }else{
                        try {
                            mEventBus.post(new NetworkErrorEvent(response.errorBody().string()));
                        }catch (IOException e){
                            //ignored
                        }
                    }
                }

                @Override
                public void onFailure(Call<CategoryList> call, Throwable t) {
                    mEventBus.post(new NetworkErrorEvent(t.getMessage()));
                }
            });
        }else{
            mEventBus.post(new NoNetworkEvent());
        }
    }

    public void getAccount(String username){
        if(checkForNetwork()) {
            mService.getAccount(username).enqueue(new Callback<AccountResult>() {
                @Override
                public void onResponse(Call<AccountResult> result, Response<AccountResult> response) {
                    if(response.isSuccessful()){
                        mEventBus.post(new AccountsEvent(response.body()));
                    }else{
                        try {
                            mEventBus.post(new NetworkErrorEvent(response.errorBody().string()));
                        }catch (IOException e){
                            //ignored
                        }
                    }
                }

                @Override
                public void onFailure(Call<AccountResult> call, Throwable t) {
                    mEventBus.post(new NetworkErrorEvent(t.getMessage()));
                }
            });
        }else{
            mEventBus.post(new NoNetworkEvent());
        }
    }

    public void uploadImage(String username, Bitmap bitmap){
        if(checkForNetwork()) {
            File imageFile = bitmapToFile(username, bitmap);
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpg"), imageFile);
            MultipartBody.Part body = MultipartBody.Part.createFormData("fileName", "imageFileName.jpg", requestFile);


            mService.uploadImage(username, body).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> response, Response<JsonObject> response2) {
                    if(response2.isSuccessful()) {
                        String json = response2.body().toString();
                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            if (jsonObject.has("link")) {
                                mEventBus.post(new ReceivedImageLinkEvent(jsonObject.getString("link")));
                            }
                        } catch (JSONException e) {

                        }
                    }else{
                        try {
                            mEventBus.post(new NetworkErrorEvent(response2.errorBody().string()));
                        }catch (IOException e){
                            //ignored
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    mEventBus.post(new NetworkErrorEvent(t.getMessage()));
                }
            });
        }else{
            mEventBus.post(new NoNetworkEvent());
        }
    }

    private File bitmapToFile(String user, Bitmap bitmap){
        try {
            File file = new File(mContext.getFilesDir(),new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + "_android_jpeg_" + user);
            FileOutputStream fos = new FileOutputStream(file);
            float resize = 1f;
            if(bitmap.getWidth() > 1080 || bitmap.getHeight() > 1080){
                int largestSide = bitmap.getHeight() > bitmap.getWidth() ? bitmap.getHeight() : bitmap.getWidth();
                resize = 1080f/(float)largestSide;
                bitmap = Bitmap.createScaledBitmap(bitmap, (int)(bitmap.getWidth()*resize), (int)(bitmap.getHeight()*resize), false);
            }

            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, fos);
            fos.close();
            return file;
        }catch (Exception e){
            return null;
        }
    }

    public void getMyPosts(String username){
        if(checkForNetwork()) {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dtf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            String date = dtf.format(calendar.getTime());

            mService.getDiscussionsByAuthor(username, "none", date, 11).enqueue(new Callback<DiscussionList>() {
                @Override
                public void onResponse(Call<DiscussionList> list, Response<DiscussionList> response) {
                    if(response.isSuccessful()){
                        DiscussionList discussionList = response.body();
                        discussionList.processList(11);
                        mEventBus.post(new MyPostsEvent(discussionList));
                    }else{
                        try {
                            mEventBus.post(new NetworkErrorEvent(response.errorBody().string()));
                        }catch (IOException e){
                            //ignored
                        }
                    }

                }

                @Override
                public void onFailure(Call<DiscussionList> call, Throwable t) {
                    mEventBus.post(new NetworkErrorEvent(t.getMessage()));
                }
            });
        }else{
            mEventBus.post(new NoNetworkEvent());
        }
    }

    public void htmlToMarkdown(String body){
        MultipartBody.Part html = MultipartBody.Part.createFormData("html", body);

        if(checkForNetwork()){
            mService.htmlToMarkdown(html).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if(response.isSuccessful()){
                        mEventBus.post(new HtmlToMarkdownEvent(response.body().get("result").getAsString()));
                    }else{
                        try {
                            mEventBus.post(new NetworkErrorEvent(response.errorBody().string()));
                        }catch (IOException e){
                            //ignored
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    mEventBus.post(new NetworkErrorEvent(t.getMessage()));
                }
            });
        }else{
            mEventBus.post(new NoNetworkEvent());
        }
    }

    public void getReplies(String username){
        if(checkForNetwork()) {
            mService.getReplies(username, "none", 50).enqueue(new Callback<DiscussionList>() {
                @Override
                public void onResponse(Call<DiscussionList> list, Response<DiscussionList> response) {
                    if(response.isSuccessful()){
                        DiscussionList discussionList = response.body();
                        discussionList.processList(50);
                        mEventBus.post(new RepliesEvent(discussionList));
                    }else{
                        try {
                            mEventBus.post(new NetworkErrorEvent(response.errorBody().string()));
                        }catch (IOException e){
                            //ignored
                        }
                    }

                }

                @Override
                public void onFailure(Call<DiscussionList> call, Throwable t) {
                    mEventBus.post(new NetworkErrorEvent(t.getMessage()));
                }
            });
        }else{
            mEventBus.post(new NoNetworkEvent());
        }
    }

    public void getComments(String category, String author, String permlink){
        if(checkForNetwork()) {
            mService.getComments(category, author, permlink).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if(response.isSuccessful()){
                        HashMap<String, Discussion> commentList;
                        Gson gson = new Gson();
                        String json = response.body().toString();
                        commentList = gson.fromJson(json, new TypeToken<HashMap<String, Discussion>>() {}.getType());
                        int counter = 0;

                        ArrayList<Discussion> mComments = new ArrayList<>(commentList.values());
                        getProcessedComments(commentList, CommentParseManager.processComments(mComments));
                    }else{
                        try {
                            mEventBus.post(new NetworkErrorEvent(response.errorBody().string()));
                        }catch (IOException e){
                            //ignored
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {

                }
            });
        }else{
            mEventBus.post(new NoNetworkEvent());
        }
    }

    public void vote(JSONObject tx){
        if(checkForNetwork()) {
            if(mManager.isLoggedIn()){
                try {
                    JSONObject op = tx.getJSONArray("operations").getJSONArray(0).getJSONObject(1);
                    mService.vote(tx.getInt("ref_block_num"),
                            tx.getInt("ref_block_prefix"),
                            tx.getString("expiration"),
                            op.getString("voter"),
                            op.getString("author"),
                            op.getString("permlink"),
                            op.getInt("weight"),
                            tx.getJSONArray("signatures").getString(0)).enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            if (response.isSuccessful()){
                                mEventBus.post(new VoteEvent(true, true));
                            } else {
                                mEventBus.post(new VoteEvent(false, true));
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            mEventBus.post(new VoteEvent(false, true));
                        }
                    });
                } catch (JSONException e) {
                    mEventBus.post(new VoteEvent(false, true));
                }
            }else{
                mEventBus.post(new NotLoggedInEvent());
            }
        }else{
            mEventBus.post(new NoNetworkEvent());
        }
    }

    public void comment(JSONObject tx){
        if(checkForNetwork()){
            if(mManager.isLoggedIn()) {
                try {
                    JSONObject op = tx.getJSONArray("operations").getJSONArray(0).getJSONObject(1);
                    final String permlink;
                    final String category = op.getString("category");
                    final String author;
                    if (category.equalsIgnoreCase(op.getString("parent_permlink"))) {
                        permlink = op.getString("permlink");
                        author = op.getString("author");
                    } else {
                        permlink = op.getString("parent_permlink");
                        author = op.getString("parent_author");
                    }

                    Map<String, RequestBody> params = new HashMap<>();
                    params.put("parent_author", RequestBody.create(MediaType.parse("text/plain"), op.getString("parent_author")));
                    params.put("parent_permlink", RequestBody.create(MediaType.parse("text/plain"), op.getString("parent_permlink")));
                    params.put("author", RequestBody.create(MediaType.parse("text/plain"), op.getString("author")));
                    params.put("permlink", RequestBody.create(MediaType.parse("text/plain"), op.getString("permlink")));
                    params.put("title", RequestBody.create(MediaType.parse("text/plain"), op.getString("title")));
                    params.put("body", RequestBody.create(MediaType.parse("text/plain"), op.getString("body")));
                    params.put("json_metadata", RequestBody.create(MediaType.parse("text/plain"), op.getString("json_metadata")));
                    mService.comment(tx.getInt("ref_block_num"), tx.getInt("ref_block_prefix"), tx.getString("expiration"), tx.getJSONArray("signatures").getString(0), params).enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            if (response.isSuccessful()) {
                                if (response.body().has("result"))
                                    mEventBus.post(new SuccessfulPostEvent(permlink, author, category));
                                else
                                    mEventBus.post(new NetworkErrorEvent("Something broke trying to make this comment."));
                            } else {
                                try {
                                    mEventBus.post(new NetworkErrorEvent(response.errorBody().string()));
                                }catch (IOException e){
                                    //ignored
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            mEventBus.post(new NetworkErrorEvent(t.getMessage()));
                        }
                    });
                } catch (JSONException e) {
                    mEventBus.post(new NetworkErrorEvent(e.getMessage()));
                }
            }else{
                mEventBus.post(new NotLoggedInEvent());
            }
        }else{
            mEventBus.post(new NoNetworkEvent());
        }
    }

    public void follow(final Transaction follow){
        if(checkForNetwork()) {
            if(mManager.isLoggedIn()){
                Map<String, RequestBody> params = new HashMap<>();
                params.put("block_num", RequestBody.create(MediaType.parse("text/plain"), Integer.toString(follow.getRefBlockNum())));
                params.put("block_ref", RequestBody.create(MediaType.parse("text/plain"), Long.toString(follow.getRefBlockPrefix())));
                params.put("expiration", RequestBody.create(MediaType.parse("text/plain"), follow.getExp().second));
                params.put("id", RequestBody.create(MediaType.parse("text/plain"), "follow"));
                params.put("follower", RequestBody.create(MediaType.parse("text/plain"), ((FollowOperation)follow.getOperation()).mFollower));
                params.put("custom_json", RequestBody.create(MediaType.parse("text/plain"), ((FollowOperation)follow.getOperation()).json));
                params.put("signature", RequestBody.create(MediaType.parse("text/plain"), byteArrayToHexString(follow.getSignature())));
                Log.i("Follows", byteArrayToHexString(follow.getSerializedMessage()));

                mService.follow(params).enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful()){
                            if(((FollowOperation)follow.getOperation()).mFollowType.contains("blog"))
                                mEventBus.post(new FollowEvent(SteemyGlobals.FOLLOW_STATE.FOLLOWED));
                            else if(((FollowOperation)follow.getOperation()).mFollowType.contains("ignore"))
                                mEventBus.post(new FollowEvent(SteemyGlobals.FOLLOW_STATE.IGNORED));
                            else if(((FollowOperation)follow.getOperation()).mFollowType.equals(""))
                                mEventBus.post(new FollowEvent(SteemyGlobals.FOLLOW_STATE.NONE));
                        } else {
//                            mEventBus.post(new FollowEvent(((FollowOperation)follow.getOperation()).mFollowing, false));
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
//                        mEventBus.post(new VoteEvent(false, true));
                    }
                });
            }else{
                mEventBus.post(new NotLoggedInEvent());
            }
        }else{
            mEventBus.post(new NoNetworkEvent());
        }
    }

    protected void transfer(final Transaction transfer){
        if(checkForNetwork()) {
            if(mManager.isLoggedIn()){
                Map<String, RequestBody> params = new HashMap<>();
                params.put("from", RequestBody.create(MediaType.parse("text/plain"), ((TransferOperation)transfer.getOperation()).mSender));
                params.put("to", RequestBody.create(MediaType.parse("text/plain"), ((TransferOperation)transfer.getOperation()).mReceiver));
                params.put("amount", RequestBody.create(MediaType.parse("text/plain"), ((TransferOperation)transfer.getOperation()).mAmount));
                params.put("memo", RequestBody.create(MediaType.parse("text/plain"), ((TransferOperation)transfer.getOperation()).mMemo));
                Log.i("Follows", byteArrayToHexString(transfer.getSerializedMessage()));

                mService.transfer(params, transfer.getRefBlockNum(), (int)transfer.getRefBlockPrefix(), transfer.getExp().second, byteArrayToHexString(transfer.getSignature())).enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful()){
//                            if(((TransferOperation)transfer.getOperation()).mFollowType.contains("blog"))
//                                mEventBus.post(new FollowEvent(SteemyGlobals.FOLLOW_STATE.FOLLOWED));
//                            else if(((FollowOperation)follow.getOperation()).mFollowType.contains("ignore"))
//                                mEventBus.post(new FollowEvent(SteemyGlobals.FOLLOW_STATE.IGNORED));
//                            else if(((FollowOperation)follow.getOperation()).mFollowType.equals(""))
//                                mEventBus.post(new FollowEvent(SteemyGlobals.FOLLOW_STATE.NONE));
                        } else {
//                            mEventBus.post(new FollowEvent(((FollowOperation)follow.getOperation()).mFollowing, false));
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
//                        mEventBus.post(new VoteEvent(false, true));
                    }
                });
            }else{
                mEventBus.post(new NotLoggedInEvent());
            }
        }else{
            mEventBus.post(new NoNetworkEvent());
        }
    }

//    public void getFollowers(String accountName, String startFollower, String follower_type, int limit){
//        mService.
//    }

    public void getFollowersList(Account account, SteemyGlobals.FOLLOWER_TYPE followerType){
        if(followerType == SteemyGlobals.FOLLOWER_TYPE.FOLLOWERS){
            getFollowers(account);
        }else if(followerType == SteemyGlobals.FOLLOWER_TYPE.FOLLOWING){
            getFollowing(account);
        }else if(followerType == SteemyGlobals.FOLLOWER_TYPE.IGNORED){
            getIgnored(account);
        }
    }

    private void getFollowers(final Account account) {
        if(checkForNetwork()) {
            mService.getFollowers(account.getName()).enqueue(new Callback<FollowerList>() {
                @Override
                public void onResponse(Call<FollowerList> call, Response<FollowerList> response) {
                    if(response.isSuccessful()) {
                        FollowerList followerList = response.body();
                        account.setmFollowers(new ArrayList<Follower>(followerList.getFollowers()));
                        mEventBus.post(new AddedFollowersEvent(account, SteemyGlobals.FOLLOWER_TYPE.FOLLOWERS));
                    }else{
                        try {
                            mEventBus.post(new NetworkErrorEvent(response.errorBody().string()));
                        }catch (IOException e){
                            //ignored
                        }
                    }
                }

                @Override
                public void onFailure(Call<FollowerList> call, Throwable t) {
                    mEventBus.post(new NetworkErrorEvent(t.getMessage()));
                }
            });
        }else{
            mEventBus.post(new NoNetworkEvent());
        }
    }

    private void getFollowing(final Account account) {
        if(checkForNetwork()) {
            mService.getFollowing(account.getName()).enqueue(new Callback<FollowerList>() {
                @Override
                public void onResponse(Call<FollowerList> call, Response<FollowerList> response) {
                    if(response.isSuccessful()) {
                        FollowerList followerList = response.body();
                        account.setmFollowing(new ArrayList<>(followerList.getFollowers()));
                        mEventBus.post(new AddedFollowersEvent(account, SteemyGlobals.FOLLOWER_TYPE.FOLLOWING));
                    }else{
                        try {
                            mEventBus.post(new NetworkErrorEvent(response.errorBody().string()));
                        }catch (IOException e){
                            //ignored
                        }
                    }
                }

                @Override
                public void onFailure(Call<FollowerList> call, Throwable t) {
                    mEventBus.post(new NetworkErrorEvent(t.getMessage()));
                }
            });
        }else{
            mEventBus.post(new NoNetworkEvent());
        }
    }

    private void getIgnored(final Account account){
        if(checkForNetwork()) {
            mService.getIgnored(account.getName()).enqueue(new Callback<FollowerList>() {
                @Override
                public void onResponse(Call<FollowerList> call, Response<FollowerList> response) {
                    if(response.isSuccessful()) {
                        FollowerList followerList = response.body();
                        account.setmIgnored(new ArrayList<>(followerList.getFollowers()));
                        mEventBus.post(new AddedFollowersEvent(account, SteemyGlobals.FOLLOWER_TYPE.IGNORED));
                    }else{
                        try {
                            mEventBus.post(new NetworkErrorEvent(response.errorBody().string()));
                        }catch (IOException e){
                            //ignored
                        }
                    }
                }

                @Override
                public void onFailure(Call<FollowerList> call, Throwable t) {
                    mEventBus.post(new NetworkErrorEvent(t.getMessage()));
                }
            });
        }else{
            mEventBus.post(new NoNetworkEvent());
        }
    }

//    public void getId(String account){
//        if(checkForNetwork()){
//            mService.getFollowerCount(account).enqueue(new Callback<JsonObject>() {
//                @Override
//                public void onResponse(Call<JsonObject> response, Response<JsonObject> response2) {
//                    getFollowCountFromResponse(response2.body());
//                }
//
//                @Override
//                public void onFailure(Call<JsonObject> call, Throwable t) {
//                    mEventBus.post(new NetworkErrorEvent(t.getMessage()));
//                }
//            });
//        }else{
//            mEventBus.post(new NoNetworkEvent());
//        }
//    }
//
//    public void getNumFollowing(String account){
//        if(checkForNetwork()){
//            mService.getFollowingCount(account).enqueue(new Callback<JsonObject>() {
//                @Override
//                public void onResponse(Call<JsonObject> response, Response<JsonObject> response2) {
//                    getFollowCountFromResponse(response2.body());
//                }
//
//                @Override
//                public void onFailure(Call<JsonObject> call, Throwable t) {
//                    mEventBus.post(new NetworkErrorEvent(t.getMessage()));
//                }
//            });
//        }else{
//            mEventBus.post(new NoNetworkEvent());
//        }
//    }
//
//    private void getFollowCountFromResponse(JsonObject response){
//        String result = response.toString();
//
//        try{
//            JSONObject json = new JSONObject(result);
//            if(json.has("num_followers")){
//                mEventBus.post(new FollowerCountEvent(json.getInt("num_followers"), true));
//            }else{
//                mEventBus.post(new FollowerCountEvent(json.getInt("num_following"), false));
//            }
//        }catch (JSONException e){
//
//        }
//    }


    private void getProcessedComments(final HashMap<String, Discussion> comments, final ArrayList<Future<ArrayList<Discussion>>> futures){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < futures.size(); i++){
                    if(futures.get(i).isDone()){
                        try {
                            ArrayList<Discussion> processed = futures.get(i).get();
                            for(Discussion d : processed) {
                                comments.put(d.getCommentKey(), d);
                            }
                        }catch (Exception e){
                        }
                    }else{
                        Log.i("Futures", "future " + i + " is not done.");
                    }
                }
                mEventBus.post(new CommentsEvent(new CommentTree(comments)));
            }
        }, 500);
    }

    @Subscribe
    public void onEvent(RequestGlobalStateEvent event) {
        if(checkForNetwork()) {
            if (event.force || SteemyGlobals.BLOCKCHAIN_GLOBALS == null) {
                mService.getGlobals().enqueue(new Callback<GlobalResults>() {
                    @Override
                    public void onResponse(Call<GlobalResults> globalResults, Response<GlobalResults> response) {
                        if(response.isSuccessful()) {
                            SteemyGlobals.BLOCKCHAIN_GLOBALS = response.body().getBlockchainGlobals();
                            mEventBus.post(new GlobalPropsEvent());
                        }


//                        TransactionBuilder.signOperation(null, null);
                    }

                    @Override
                    public void onFailure(Call<GlobalResults> call, Throwable t) {
                        mEventBus.post(new NetworkErrorEvent(t.getMessage()));
                    }
                });
            }
        }else{
            mEventBus.post(new NoNetworkEvent());
        }
    }

    @Subscribe
    public void onEvent(BroadcastVoteEvent event){
        vote(event.vote);
    }

    @Subscribe
    public void onEvent(BroadcastCommentEvent event){
        comment(event.comment);
    }

    @Subscribe
    public void onEvent(BroadcastFollowEvent event){follow(event.followTx);}

    @Subscribe
    public void onEvent(BroadcastTransferEvent event){transfer(event.transferTx);}

    public boolean checkForNetwork(){
        if(mConnectivityManager == null){
            mConnectivityManager = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        }

        NetworkInfo activeNetwork = mConnectivityManager.getActiveNetworkInfo();
        return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
    }
}