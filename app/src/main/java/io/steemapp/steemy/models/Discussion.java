
package io.steemapp.steemy.models;

import android.content.Context;
import android.text.Html;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.util.Log;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.commonsware.cwac.anddown.AndDown;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import io.steemapp.steemy.utils.DetectHtml;

public class Discussion {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("author")
    @Expose
    private String author;
    @SerializedName("permlink")
    @Expose
    private String permlink;
    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("parent_author")
    @Expose
    private String parentAuthor;
    @SerializedName("parent_permlink")
    @Expose
    private String parentPermlink;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("body")
    @Expose
    private String body;
    @SerializedName("json_metadata")
    @Expose
    private String jsonMetadata;
    @SerializedName("last_update")
    @Expose
    private String lastUpdate;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("active")
    @Expose
    private String active;
    @SerializedName("last_payout")
    @Expose
    private String lastPayout;
    @SerializedName("depth")
    @Expose
    private Integer depth;
    @SerializedName("children")
    @Expose
    private Integer children;
    @SerializedName("children_rshares2")
    @Expose
    private String childrenRshares2;
    @SerializedName("net_rshares")
    @Expose
    private Long netRshares;
    @SerializedName("abs_rshares")
    @Expose
    private Long absRshares;
    @SerializedName("vote_rshares")
    @Expose
    private Long voteRshares;
    @SerializedName("children_abs_rshares")
    @Expose
    private Long childrenAbsRshares;
    @SerializedName("cashout_time")
    @Expose
    private String cashoutTime;
    @SerializedName("max_cashout_time")
    @Expose
    private String maxCashoutTime;
    @SerializedName("total_vote_weight")
    @Expose
    private String totalVoteWeight;
    @SerializedName("reward_weight")
    @Expose
    private Integer rewardWeight;
    @SerializedName("total_payout_value")
    @Expose
    private String totalPayoutValue;
    @SerializedName("curator_payout_value")
    @Expose
    private String curatorPayoutValue;
    @SerializedName("author_rewards")
    @Expose
    private Integer authorRewards;
    @SerializedName("net_votes")
    @Expose
    private Integer netVotes;
    @SerializedName("root_comment")
    @Expose
    private String rootComment;
    @SerializedName("max_accepted_payout")
    @Expose
    private String maxAcceptedPayout;
    @SerializedName("percent_steem_dollars")
    @Expose
    private Integer percentSteemDollars;
    @SerializedName("allow_replies")
    @Expose
    private Boolean allowReplies;
    @SerializedName("allow_votes")
    @Expose
    private Boolean allowVotes;
    @SerializedName("allow_curation_rewards")
    @Expose
    private Boolean allowCurationRewards;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("root_title")
    @Expose
    private String rootTitle;
    @SerializedName("pending_payout_value")
    @Expose
    private String pendingPayoutValue;
    @SerializedName("total_pending_payout_value")
    @Expose
    private String totalPendingPayoutValue;
    @SerializedName("active_votes")
    @Expose
    private List<ActiveVote> activeVotes = new ArrayList<ActiveVote>();
    @SerializedName("replies")
    @Expose
    private List<String> replies = new ArrayList<String>();

    private String mTeaserImage;
    private String currentPayout;
    private String mTimeAgo;

    private HashSet<String> voters = new HashSet<>();

    public boolean processed = false;

    public void formatDiscussion(){
        extractTeaserImage();
        formatCurrentPayout();
        formatTime();
        if(depth == 0)
            formatBodyMarkdown();
        else
            formatCommentMarkdown();
        processed = true;
        voters = new HashSet<>();
        for(ActiveVote v : activeVotes)
            voters.add(v.getVoter());
    }

    public boolean hasVoted(String name){
        return voters.contains(name);
    }

    protected void extractTeaserImage(){
        try{
            JSONObject metadata = new JSONObject(jsonMetadata);
            if(metadata.has("image"))
                mTeaserImage = metadata.getJSONArray("image").getString(0);
            else
                mTeaserImage = null;
        }catch (JSONException e){

        }
    }

    protected void formatCurrentPayout(){
        double value = Double.parseDouble(pendingPayoutValue.substring(0, pendingPayoutValue.length()-4));
        DecimalFormat df = new DecimalFormat("#0.00");
        df.setRoundingMode(RoundingMode.CEILING);
        currentPayout = "$" + df.format(value);
    }

    private static AndDown mMarkdownParser;
    private final Pattern imgPattern = Pattern.compile("(?<![\\\"\\\\/])(https|http):.*?.(png|jpeg|jpg|gif)");
    private final Pattern htmlImgPattern = Pattern.compile("<img.*?src=\"([^\"]*)\".*?>");
    private final Pattern youtubePattern = Pattern.compile("<iframe.+?src=\"http(s)?://www.youtube.com/embed/([a-zA-Z0-9_-]{11})\"[^>]+?></iframe>");
    private final Pattern urlPattern = Pattern.compile("\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
    private Matcher mMatcher;

    protected void formatBodyMarkdown(){
        if(mMarkdownParser == null){
            mMarkdownParser = new AndDown();
        }
//        mMatcher = youtubePattern.matcher(body);
//        ArrayList<String> youtubeLinks = new ArrayList<>();
//        while(mMatcher.find()){
//            String result = mMatcher.group();
//            youtubeLinks.add(result);
//            body = body.replace(result, Integer.toString(result.hashCode()));
//        }
//
//        mMatcher = htmlImgPattern.matcher(body);
//        ArrayList<String> imageLinks = new ArrayList<>();
//        while(mMatcher.find()){
//            String result = mMatcher.group();
//            imageLinks.add(result);
//            body = body.replace(result, Integer.toString(result.hashCode()));
//        }

        if(!DetectHtml.isHtml(body)){
            while(body.contains("<") || body.contains(">")){
                body = body.replace("<", "&lt;");
                body = body.replace(">", "&gt;");
            }
            body = mMarkdownParser.markdownToHtml(body);
        }

        mMatcher = imgPattern.matcher(body);
        while(mMatcher.find()){
            String result = mMatcher.group();
            String replaced = "<img src=\"" + result + "\"></img>";
            body = body.replace(result, replaced);
        }
//
//        mMatcher = urlPattern.matcher(body);
//        while(mMatcher.find()){
//            String result = mMatcher.group();
//            String replaced = "<a href=\"" + result + "\">" + result + "</a>";
//            body = body.replace(result, replaced);
//        }

//        while(body.contains(Integer.toString("<".hashCode())) || body.contains(Integer.toString(">".hashCode()))){
//            body = body.replace(Integer.toString("<".hashCode()), "<");
//            body = body.replace(Integer.toString(">".hashCode()), ">");
//        }

//        for(String link : youtubeLinks){
//            body = body.replace(Integer.toString(link.hashCode()), link);
//        }

//        for(String link : imageLinks){
//            body = body.replace(Integer.toString(link.hashCode()), link);
//        }

        body = body.replaceAll("\\n\\n", " <br/> ");
        body = body.replaceAll("\\n", " <br/> ");

        SpannableString spannableString = new SpannableString(body);
        Linkify.addLinks(spannableString, Linkify.ALL);
        body = spannableString.toString();
    }

    protected void formatCommentMarkdown(){
        if(mMarkdownParser == null){
            mMarkdownParser = new AndDown();
        }
//        mMatcher = youtubePattern.matcher(body);
//        ArrayList<String> youtubeLinks = new ArrayList<>();
//        while(mMatcher.find()){
//            String result = mMatcher.group();
//            youtubeLinks.add(result);
//            body = body.replace(result, Integer.toString(result.hashCode()));
//        }
//
//        mMatcher = htmlImgPattern.matcher(body);
//        ArrayList<String> imageLinks = new ArrayList<>();
//        while(mMatcher.find()){
//            String result = mMatcher.group();
//            imageLinks.add(result);
//            body = body.replace(result, Integer.toString(result.hashCode()));
//        }

        if(!DetectHtml.isHtml(body)){
            while(body.contains("<") || body.contains(">")){
                body = body.replace("<", "&lt;");
                body = body.replace(">", "&gt;");
            }
            body = mMarkdownParser.markdownToHtml(body);
        }

//        mMatcher = imgPattern.matcher(body);
//        while(mMatcher.find()){
//            String result = mMatcher.group();
//            String replaced = "<img src=\"" + result + "\"></img>";
//            body = body.replace(result, Integer.toString(replaced.hashCode()));
//            imageLinks.add(replaced);
//        }

        mMatcher = urlPattern.matcher(body);
        while(mMatcher.find()){
            String result = mMatcher.group();
            String replaced = "<a href=\"" + result + "\">" + result + "</a>";
            body = body.replace(result, replaced);
        }

//        for(String link : youtubeLinks){
//            body = body.replace(Integer.toString(link.hashCode()), link);
//        }
//
//        for(String link : imageLinks){
//            body = body.replace(Integer.toString(link.hashCode()), link);
//        }

//        body = body.replaceAll("\\n", " \\\n ");

        SpannableString spannableString = new SpannableString(body);
        Linkify.addLinks(spannableString, Linkify.ALL);
        body = spannableString.toString();

        StringBuilder builder = new StringBuilder();
        String top = new String("<div class=\"comment_header\"><span class=\"user\"><a href=\"author://"+author+"\">" + author + "</a></span>&nbsp&nbsp&nbsp•&nbsp&nbsp&nbsp<span class=\"time\">"+mTimeAgo+"</span></div>");
        body = new String("<div class=\"comment\">"+body + " ");
        String bottom = new String("<p class=\"comment_footer\"><span class=\"button1\"><a href=\"payout://"+author+"/"+permlink+"\">" + currentPayout + "</a></span>&nbsp&nbsp&nbsp•&nbsp&nbsp&nbsp<span class=\"button1\"><a href=\"votes://"+author+"/"+permlink+"\">" + getActiveVotes().size() + " votes</a></span>&nbsp&nbsp&nbsp•&nbsp&nbsp&nbsp<span class=\"button1\"><a href=\"reply://"+author+"/"+permlink+"\">Reply</a></span></p>");
        builder.append(top);
        builder.append(body);
        builder.append(bottom);
        body = builder.toString();

    }

    public String getCommentKey(){
        return author + "/" + permlink;
    }

    public String getParentKey(){
        return parentAuthor + "/" + parentPermlink;
    }

    private static SimpleDateFormat mDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private static final int RIGHT_NOW = 1000*60*5;
    private static final int ONE_HOUR = 1000*60*60;
    private static final int ONE_DAY = 1000*60*60*24;
    private static final int ONE_MINUTE = 1000*60;

    protected void formatTime(){
        try {
            mDateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = mDateFormatter.parse(created);
            long millisCreated = date.getTime();
            long millisNow = Calendar.getInstance().getTimeInMillis();

            long deltaMillis = millisNow - millisCreated;
            if (deltaMillis < RIGHT_NOW) {
                mTimeAgo = "posted just now";
            } else if (deltaMillis < ONE_HOUR) {
                int minutes = (int) (deltaMillis / ONE_MINUTE);
                mTimeAgo = "posted " + Integer.toString(minutes) + " minutes ago";
            } else if (deltaMillis < ONE_DAY) {
                int hours = (int) (deltaMillis / ONE_HOUR);
                mTimeAgo = "posted " + Integer.toString(hours) + " hours ago";
            } else if (deltaMillis > ONE_DAY) {
                int days = (int) (deltaMillis / ONE_DAY);
                mTimeAgo = "posted " + Integer.toString(days) + " days ago";
            }
        }catch (ParseException e){
            mTimeAgo = "a while ago";
        }
    }

    public long getHowOld() throws ParseException{
        mDateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = mDateFormatter.parse(created);
        long millisCreated = date.getTime();
        long millisNow = Calendar.getInstance().getTimeInMillis();

        return millisNow - millisCreated;
    }

    protected void setTimeAgo(String time){mTimeAgo = time;}
    public String getTimeAgo(){return mTimeAgo;}
    public String getHumanReadablePayout(){
        return currentPayout;
    }

    public String getTeaserImage(){
        return mTeaserImage;
    }

    private ArrayList<String> childComments;

    public void addChildComment(String commentLink){
        if(childComments == null)
            childComments = new ArrayList<>();

        childComments.add(commentLink);
    }

    public void closeCommentDiv(){
        body += "</div>";
    }

    /**
     *
     * @return
     * The id
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The author
     */
    public String getAuthor() {
        return author;
    }

    /**
     *
     * @param author
     * The author
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     *
     * @return
     * The permlink
     */
    public String getPermlink() {
        return permlink;
    }

    /**
     *
     * @param permlink
     * The permlink
     */
    public void setPermlink(String permlink) {
        this.permlink = permlink;
    }

    private static SimpleDateFormat mPermlinkFormatter = new SimpleDateFormat("yyyyMMdd't'HHmmss");
    public void setPermlinkify(boolean reply, String title) {
        if(reply){
            StringBuilder b = new StringBuilder();
            String date = mPermlinkFormatter.format(new Date());
            b.append("re-").append(parentAuthor).append("-").append(parentPermlink).append("-").append(date).append("z-").append("stmy");
            title = b.toString();
        }

        permlink = title.replaceAll("[^\\d\\w]", "-");

    }

    /**
     *
     * @return
     * The category
     */
    public String getCategory() {
        return category;
    }

    /**
     *
     * @param category
     * The category
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     *
     * @return
     * The parentAuthor
     */
    public String getParentAuthor() {
        return parentAuthor;
    }

    /**
     *
     * @param parentAuthor
     * The parent_author
     */
    public void setParentAuthor(String parentAuthor) {
        this.parentAuthor = parentAuthor;
    }

    /**
     *
     * @return
     * The parentPermlink
     */
    public String getParentPermlink() {
        return parentPermlink;
    }

    /**
     *
     * @param parentPermlink
     * The parent_permlink
     */
    public void setParentPermlink(String parentPermlink) {
        this.parentPermlink = parentPermlink;
    }

    /**
     *
     * @return
     * The title
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title
     * The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @return
     * The body
     */
    public String getBody() {
        return body;
    }

    /**
     *
     * @param body
     * The body
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     *
     * @return
     * The jsonMetadata
     */
    public String getJsonMetadata() {
        return jsonMetadata;
    }

    /**
     *
     * @param jsonMetadata
     * The json_metadata
     */
    public void setJsonMetadata(String jsonMetadata) {
        this.jsonMetadata = jsonMetadata;
    }

    /**
     *
     * @return
     * The lastUpdate
     */
    public String getLastUpdate() {
        return lastUpdate;
    }

    /**
     *
     * @param lastUpdate
     * The last_update
     */
    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    /**
     *
     * @return
     * The created
     */
    public String getCreated() {
        return created;
    }

    /**
     *
     * @param created
     * The created
     */
    public void setCreated(String created) {
        this.created = created;
    }

    /**
     *
     * @return
     * The active
     */
    public String getActive() {
        return active;
    }

    /**
     *
     * @param active
     * The active
     */
    public void setActive(String active) {
        this.active = active;
    }

    /**
     *
     * @return
     * The lastPayout
     */
    public String getLastPayout() {
        return lastPayout;
    }

    /**
     *
     * @param lastPayout
     * The last_payout
     */
    public void setLastPayout(String lastPayout) {
        this.lastPayout = lastPayout;
    }

    /**
     *
     * @return
     * The depth
     */
    public Integer getDepth() {
        return depth;
    }

    /**
     *
     * @param depth
     * The depth
     */
    public void setDepth(Integer depth) {
        this.depth = depth;
    }

    /**
     *
     * @return
     * The children
     */
    public Integer getChildren() {
        return children;
    }

    /**
     *
     * @param children
     * The children
     */
    public void setChildren(Integer children) {
        this.children = children;
    }

    /**
     *
     * @return
     * The childrenRshares2
     */
    public String getChildrenRshares2() {
        return childrenRshares2;
    }

    /**
     *
     * @param childrenRshares2
     * The children_rshares2
     */
    public void setChildrenRshares2(String childrenRshares2) {
        this.childrenRshares2 = childrenRshares2;
    }

    /**
     *
     * @return
     * The netRshares
     */
    public Long getNetRshares() {
        return netRshares;
    }

    /**
     *
     * @param netRshares
     * The net_rshares
     */
    public void setNetRshares(Long netRshares) {
        this.netRshares = netRshares;
    }

    /**
     *
     * @return
     * The absRshares
     */
    public Long getAbsRshares() {
        return absRshares;
    }

    /**
     *
     * @param absRshares
     * The abs_rshares
     */
    public void setAbsRshares(Long absRshares) {
        this.absRshares = absRshares;
    }

    /**
     *
     * @return
     * The voteRshares
     */
    public Long getVoteRshares() {
        return voteRshares;
    }

    /**
     *
     * @param voteRshares
     * The vote_rshares
     */
    public void setVoteRshares(Long voteRshares) {
        this.voteRshares = voteRshares;
    }

    /**
     *
     * @return
     * The childrenAbsRshares
     */
    public Long getChildrenAbsRshares() {
        return childrenAbsRshares;
    }

    /**
     *
     * @param childrenAbsRshares
     * The children_abs_rshares
     */
    public void setChildrenAbsRshares(Long childrenAbsRshares) {
        this.childrenAbsRshares = childrenAbsRshares;
    }

    /**
     *
     * @return
     * The cashoutTime
     */
    public String getCashoutTime() {
        return cashoutTime;
    }

    /**
     *
     * @param cashoutTime
     * The cashout_time
     */
    public void setCashoutTime(String cashoutTime) {
        this.cashoutTime = cashoutTime;
    }

    /**
     *
     * @return
     * The maxCashoutTime
     */
    public String getMaxCashoutTime() {
        return maxCashoutTime;
    }

    /**
     *
     * @param maxCashoutTime
     * The max_cashout_time
     */
    public void setMaxCashoutTime(String maxCashoutTime) {
        this.maxCashoutTime = maxCashoutTime;
    }

    /**
     *
     * @return
     * The totalVoteWeight
     */
    public String getTotalVoteWeight() {
        return totalVoteWeight;
    }

    /**
     *
     * @param totalVoteWeight
     * The total_vote_weight
     */
    public void setTotalVoteWeight(String totalVoteWeight) {
        this.totalVoteWeight = totalVoteWeight;
    }

    /**
     *
     * @return
     * The rewardWeight
     */
    public Integer getRewardWeight() {
        return rewardWeight;
    }

    /**
     *
     * @param rewardWeight
     * The reward_weight
     */
    public void setRewardWeight(Integer rewardWeight) {
        this.rewardWeight = rewardWeight;
    }

    /**
     *
     * @return
     * The totalPayoutValue
     */
    public String getTotalPayoutValue() {
        return totalPayoutValue;
    }

    /**
     *
     * @param totalPayoutValue
     * The total_payout_value
     */
    public void setTotalPayoutValue(String totalPayoutValue) {
        this.totalPayoutValue = totalPayoutValue;
    }

    /**
     *
     * @return
     * The curatorPayoutValue
     */
    public String getCuratorPayoutValue() {
        return curatorPayoutValue;
    }

    /**
     *
     * @param curatorPayoutValue
     * The curator_payout_value
     */
    public void setCuratorPayoutValue(String curatorPayoutValue) {
        this.curatorPayoutValue = curatorPayoutValue;
    }

    /**
     *
     * @return
     * The authorRewards
     */
    public Integer getAuthorRewards() {
        return authorRewards;
    }

    /**
     *
     * @param authorRewards
     * The author_rewards
     */
    public void setAuthorRewards(Integer authorRewards) {
        this.authorRewards = authorRewards;
    }

    /**
     *
     * @return
     * The netVotes
     */
    public Integer getNetVotes() {
        return netVotes;
    }

    /**
     *
     * @param netVotes
     * The net_votes
     */
    public void setNetVotes(Integer netVotes) {
        this.netVotes = netVotes;
    }

    /**
     *
     * @return
     * The rootComment
     */
    public String getRootComment() {
        return rootComment;
    }

    /**
     *
     * @param rootComment
     * The root_comment
     */
    public void setRootComment(String rootComment) {
        this.rootComment = rootComment;
    }

    /**
     *
     * @return
     * The maxAcceptedPayout
     */
    public String getMaxAcceptedPayout() {
        return maxAcceptedPayout;
    }

    /**
     *
     * @param maxAcceptedPayout
     * The max_accepted_payout
     */
    public void setMaxAcceptedPayout(String maxAcceptedPayout) {
        this.maxAcceptedPayout = maxAcceptedPayout;
    }

    /**
     *
     * @return
     * The percentSteemDollars
     */
    public Integer getPercentSteemDollars() {
        return percentSteemDollars;
    }

    /**
     *
     * @param percentSteemDollars
     * The percent_steem_dollars
     */
    public void setPercentSteemDollars(Integer percentSteemDollars) {
        this.percentSteemDollars = percentSteemDollars;
    }

    /**
     *
     * @return
     * The allowReplies
     */
    public Boolean getAllowReplies() {
        return allowReplies;
    }

    /**
     *
     * @param allowReplies
     * The allow_replies
     */
    public void setAllowReplies(Boolean allowReplies) {
        this.allowReplies = allowReplies;
    }

    /**
     *
     * @return
     * The allowVotes
     */
    public Boolean getAllowVotes() {
        return allowVotes;
    }

    /**
     *
     * @param allowVotes
     * The allow_votes
     */
    public void setAllowVotes(Boolean allowVotes) {
        this.allowVotes = allowVotes;
    }

    /**
     *
     * @return
     * The allowCurationRewards
     */
    public Boolean getAllowCurationRewards() {
        return allowCurationRewards;
    }

    /**
     *
     * @param allowCurationRewards
     * The allow_curation_rewards
     */
    public void setAllowCurationRewards(Boolean allowCurationRewards) {
        this.allowCurationRewards = allowCurationRewards;
    }

    /**
     *
     * @return
     * The url
     */
    public String getUrl() {
        return url;
    }

    /**
     *
     * @param url
     * The url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     *
     * @return
     * The rootTitle
     */
    public String getRootTitle() {
        return rootTitle;
    }

    /**
     *
     * @param rootTitle
     * The root_title
     */
    public void setRootTitle(String rootTitle) {
        this.rootTitle = rootTitle;
    }

    /**
     *
     * @return
     * The pendingPayoutValue
     */
    public String getPendingPayoutValue() {
        return pendingPayoutValue;
    }

    /**
     *
     * @param pendingPayoutValue
     * The pending_payout_value
     */
    public void setPendingPayoutValue(String pendingPayoutValue) {
        this.pendingPayoutValue = pendingPayoutValue;
    }

    /**
     *
     * @return
     * The totalPendingPayoutValue
     */
    public String getTotalPendingPayoutValue() {
        return totalPendingPayoutValue;
    }

    /**
     *
     * @param totalPendingPayoutValue
     * The total_pending_payout_value
     */
    public void setTotalPendingPayoutValue(String totalPendingPayoutValue) {
        this.totalPendingPayoutValue = totalPendingPayoutValue;
    }

    /**
     *
     * @return
     * The activeVotes
     */
    public List<ActiveVote> getActiveVotes() {
        return activeVotes;
    }

    /**
     *
     * @param activeVotes
     * The active_votes
     */
    public void setActiveVotes(List<ActiveVote> activeVotes) {
        this.activeVotes = activeVotes;
    }

    /**
     *
     * @return
     * The replies
     */
    public List<String> getReplies() {
        return replies;
    }

    /**
     *
     * @param replies
     * The replies
     */
    public void setReplies(List<String> replies) {
        this.replies = replies;
    }

}
