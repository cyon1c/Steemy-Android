package io.steemapp.steemy.network;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

import io.steemapp.steemy.models.AccountResult;
import io.steemapp.steemy.models.CategoryList;
import io.steemapp.steemy.models.ChainProperties;
import io.steemapp.steemy.models.ChainResult;
import io.steemapp.steemy.models.Discussion;
import io.steemapp.steemy.models.DiscussionList;
import io.steemapp.steemy.models.FollowerList;
import io.steemapp.steemy.models.GlobalResults;
import io.steemapp.steemy.network.rpc.RPCRequest;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;

/**
 * Created by John on 7/28/2016.
 */
public interface SteemyRetrofitService {

    @POST("/gLF4bZDk")
    Call<JsonObject> getDynamicGlobalProperties(@Body RPCRequest globalPropsRequest);

    @GET("get_categories/{sort}/{after}/{limit}")
    Call<CategoryList> getCategories(@Path("sort") String sort,
                                     @Path("after") String afterThisPermlink,
                                     @Path("limit") int numPosts);

    @GET("get_discussions/{sort}/{tag}/{author}/{title}/{limit}")
    Call<DiscussionList> getDiscussions(@Path("sort") String sort,
                                        @Path("tag") String tag,
                                        @Path("author") String author,
                                        @Path("title") String permlink,
                                        @Path("limit") int numPosts);

    @GET("get_discussions_by_author/{author}/{start_permlink}/{start_date}/{limit}")
    Call<DiscussionList> getDiscussionsByAuthor(@Path("author") String author,
                                                @Path("start_permlink") String permlink,
                                                @Path(value = "start_date") String startDate,
                                                @Path("limit") int numPosts);

    @GET("get_recent_replies/{account}/{start_permlink}/{limit}")
    Call<DiscussionList> getReplies(@Path("account") String account,
                                    @Path("start_permlink") String permlink,
                                    @Path("limit") int limit);

    @GET("get_account/{username}")
    Call<AccountResult> getAccount(@Path("username") String user);

    @GET("get_comments/{tag}/{author}/{permlink}")
    Call<JsonObject> getComments(@Path("tag") String category,
                                 @Path("author") String author,
                                 @Path("permlink") String permlink);

    @GET("get_all_following/{account}/blog")
    Call<FollowerList> getFollowing(@Path("account")String accountName);

    @GET("get_all_following/{account}/ignore")
    Call<FollowerList> getIgnored(@Path("account")String accountName);

    @GET("get_all_followers/{account}/blog")
    Call<FollowerList> getFollowers(@Path("account")String accountName);

    @Multipart
    @POST("/upload_image/{account}")
    Call<JsonObject> uploadImage(@Path("account") String account,
                                 @Part MultipartBody.Part image);

    @POST("vote/{ref_block_num}/{ref_block_prefix}/{date}/{voter}/{author}/{permlink}/{vote_weight}/{signature}")
    Call<JsonObject> vote(@Path("ref_block_num") int refBlockNum,
                          @Path("ref_block_prefix") int refBlockPrefix,
                          @Path("date") String date,
                          @Path("voter") String votingAccount,
                          @Path("author") String author,
                          @Path("permlink") String permlink,
                          @Path("vote_weight") int voteWeight,
                          @Path("signature") String signature);

    @Multipart
    @POST("html2markdown")
    Call<JsonObject> htmlToMarkdown(@Part MultipartBody.Part bodyText);

    @Multipart
    @POST("comment/{ref_block_num}/{ref_block_prefix}/{date}/{signature}")
    Call<JsonObject> comment(@Path("ref_block_num") int refBlockNum,
                             @Path("ref_block_prefix") int refBlockPrefix,
                             @Path("date") String date,
                             @Path("signature") String signature,
                             @PartMap Map<String, RequestBody> params);

    @Multipart
    @POST("follow")
    Call<JsonObject> follow(@PartMap Map<String, RequestBody> params);

    @Multipart
    @POST("transfer/{ref_block_num}/{ref_block_prefix}/{date}/{signature}")
    Call<JsonObject> transfer(@PartMap Map<String, RequestBody> params,
                              @Path("ref_block_num") int refBlockNum,
                              @Path("ref_block_prefix") int refBlockPrefix,
                              @Path("date") String date,
                              @Path("signature") String signature);


    @GET("get_chain_properties")
    Call<ChainResult> getChainProperties();

    @GET("get_global_properties")
    Call<GlobalResults> getGlobals();
}
