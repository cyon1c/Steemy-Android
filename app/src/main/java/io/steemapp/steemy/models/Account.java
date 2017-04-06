package io.steemapp.steemy.models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import io.steemapp.steemy.SteemyGlobals;


public class Account {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("owner")
    @Expose
    private OwnerKeyAuth ownerKeyAuth;
    @SerializedName("active")
    @Expose
    private ActiveKeyAuth activeKeyAuth;
    @SerializedName("posting")
    @Expose
    private PostingKeyAuth postingKeyAuth;
    @SerializedName("memo_key")
    @Expose
    private String memoKey;
    @SerializedName("json_metadata")
    @Expose
    private String jsonMetadata;
    @SerializedName("proxy")
    @Expose
    private String proxy;
    @SerializedName("last_owner_update")
    @Expose
    private String lastOwnerUpdate;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("reputation")
    @Expose
    private Integer reputation;
    @SerializedName("mined")
    @Expose
    private Boolean mined;
    @SerializedName("owner_challenged")
    @Expose
    private Boolean ownerChallenged;
    @SerializedName("active_challenged")
    @Expose
    private Boolean activeChallenged;
    @SerializedName("last_owner_proved")
    @Expose
    private String lastOwnerProved;
    @SerializedName("last_active_proved")
    @Expose
    private String lastActiveProved;
    @SerializedName("recovery_account")
    @Expose
    private String recoveryAccount;
    @SerializedName("last_account_recovery")
    @Expose
    private String lastAccountRecovery;
    @SerializedName("comment_count")
    @Expose
    private Integer commentCount;
    @SerializedName("lifetime_vote_count")
    @Expose
    private Integer lifetimeVoteCount;
    @SerializedName("post_count")
    @Expose
    private Integer postCount;
    @SerializedName("voting_power")
    @Expose
    private Integer votingPower;
    @SerializedName("last_vote_time")
    @Expose
    private String lastVoteTime;
    @SerializedName("balance")
    @Expose
    private String balance;
    @SerializedName("sbd_balance")
    @Expose
    private String sbdBalance;
    @SerializedName("sbd_seconds")
    @Expose
    private String sbdSeconds;
    @SerializedName("sbd_seconds_last_update")
    @Expose
    private String sbdSecondsLastUpdate;
    @SerializedName("sbd_last_interest_payment")
    @Expose
    private String sbdLastInterestPayment;
    @SerializedName("vesting_shares")
    @Expose
    private String vestingShares;
    @SerializedName("vesting_withdraw_rate")
    @Expose
    private String vestingWithdrawRate;
    @SerializedName("next_vesting_withdrawal")
    @Expose
    private String nextVestingWithdrawal;
    @SerializedName("withdrawn")
    @Expose
    private Integer withdrawn;
    @SerializedName("to_withdraw")
    @Expose
    private Integer toWithdraw;
    @SerializedName("withdraw_routes")
    @Expose
    private Integer withdrawRoutes;
    @SerializedName("curation_rewards")
    @Expose
    private Integer curationRewards;
    @SerializedName("posting_rewards")
    @Expose
    private Integer postingRewards;
    @SerializedName("proxied_vsf_votes")
    @Expose
    private List<Integer> proxiedVsfVotes = new ArrayList<Integer>();
    @SerializedName("witnesses_voted_for")
    @Expose
    private Integer witnessesVotedFor;
    @SerializedName("average_bandwidth")
    @Expose
    private Long averageBandwidth;
    @SerializedName("lifetime_bandwidth")
    @Expose
    private String lifetimeBandwidth;
    @SerializedName("last_bandwidth_update")
    @Expose
    private String lastBandwidthUpdate;
    @SerializedName("average_market_bandwidth")
    @Expose
    private Integer averageMarketBandwidth;
    @SerializedName("last_market_bandwidth_update")
    @Expose
    private String lastMarketBandwidthUpdate;
    @SerializedName("last_post")
    @Expose
    private String lastPost;
    @SerializedName("last_root_post")
    @Expose
    private String lastRootPost;
    @SerializedName("post_bandwidth")
    @Expose
    private Integer postBandwidth;
    @SerializedName("last_active")
    @Expose
    private String lastActive;
    @SerializedName("activity_shares")
    @Expose
    private String activityShares;
    @SerializedName("last_activity_payout")
    @Expose
    private String lastActivityPayout;
    @SerializedName("vesting_balance")
    @Expose
    private String vestingBalance;
    @SerializedName("transfer_history")
    @Expose
    private List<Object> transferHistory = new ArrayList<Object>();
    @SerializedName("market_history")
    @Expose
    private List<Object> marketHistory = new ArrayList<Object>();
    @SerializedName("post_history")
    @Expose
    private List<Object> postHistory = new ArrayList<Object>();
    @SerializedName("vote_history")
    @Expose
    private List<Object> voteHistory = new ArrayList<Object>();
    @SerializedName("other_history")
    @Expose
    private List<Object> otherHistory = new ArrayList<Object>();
    @SerializedName("witness_votes")
    @Expose
    private List<Object> witnessVotes = new ArrayList<Object>();
    @SerializedName("blog_category")
    @Expose
    private BlogCategory blogCategory;

    @SerializedName("followers")
    @Expose
    private ArrayList<Follower> mFollowers = new ArrayList<>();
    @SerializedName("following")
    @Expose
    private ArrayList<Follower> mFollowing = new ArrayList<>();
    @SerializedName("ignored")
    @Expose
    private ArrayList<Follower> mIgnored = new ArrayList<>();

    public Integer getReputation() {
        return reputation;
    }

    public void setReputation(Integer reputation) {
        this.reputation = reputation;
    }

    public ArrayList<Follower> getFollowers() {
        return mFollowers;
    }

    public void setmFollowers(ArrayList<Follower> mFollowers) {
        this.mFollowers = mFollowers;
    }

    public ArrayList<Follower> getFollowing() {
        return mFollowing;
    }

    public void setmFollowing(ArrayList<Follower> mFollowing) {
        this.mFollowing = mFollowing;
    }

    public ArrayList<Follower> getmIgnored() {
        return mIgnored;
    }

    public void setmIgnored(ArrayList<Follower> mIgnored) {
        this.mIgnored = mIgnored;
    }

    public void addFollowers(List<Follower> followers){
        mFollowers.addAll(followers);
    }

    public void addFollowing(List<Follower> followers){
        mFollowing.addAll(followers);
    }

    public void addIgnored(List<Follower> followers){
        mIgnored.addAll(followers);
    }

    public String getChromedUsername(){return "@"+name+" ("+Integer.toString(reputation)+")";};

    public boolean checkIfFollowing(String accountName){
        HashSet<String> followSet = new HashSet<>(mFollowing.size());
        for(Follower f : mFollowing){
            followSet.add(f.getFollowing());
        }
        return followSet.contains(accountName);
    }

    public boolean checkIfMuted(String accountName){
        HashSet<String> ignoreSet = new HashSet<>(mIgnored.size());
        for(Follower f : mIgnored){
            ignoreSet.add(f.getFollowing());
        }
        return ignoreSet.contains(accountName);
    }

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
     * The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     * The owner
     */
    public OwnerKeyAuth getOwnerKeyAuth() {
        return ownerKeyAuth;
    }

    /**
     *
     * @param ownerKeyAuth
     * The owner
     */
    public void setOwnerKeyAuth(OwnerKeyAuth ownerKeyAuth) {
        this.ownerKeyAuth = ownerKeyAuth;
    }

    /**
     *
     * @return
     * The active
     */
    public ActiveKeyAuth getActiveKeyAuth() {
        return activeKeyAuth;
    }

    /**
     *
     * @param activeKeyAuth
     * The active
     */
    public void setActiveKeyAuth(ActiveKeyAuth activeKeyAuth) {
        this.activeKeyAuth = activeKeyAuth;
    }

    /**
     *
     * @return
     * The posting
     */
    public PostingKeyAuth getPostingKeyAuth() {
        return postingKeyAuth;
    }

    /**
     *
     * @param postingKeyAuth
     * The posting
     */
    public void setPostingKeyAuth(PostingKeyAuth postingKeyAuth) {
        this.postingKeyAuth = postingKeyAuth;
    }

    /**
     *
     * @return
     * The memoKey
     */
    public String getMemoKey() {
        return memoKey;
    }

    /**
     *
     * @param memoKey
     * The memo_key
     */
    public void setMemoKey(String memoKey) {
        this.memoKey = memoKey;
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
     * The proxy
     */
    public String getProxy() {
        return proxy;
    }

    /**
     *
     * @param proxy
     * The proxy
     */
    public void setProxy(String proxy) {
        this.proxy = proxy;
    }

    /**
     *
     * @return
     * The lastOwnerUpdate
     */
    public String getLastOwnerUpdate() {
        return lastOwnerUpdate;
    }

    /**
     *
     * @param lastOwnerUpdate
     * The last_owner_update
     */
    public void setLastOwnerUpdate(String lastOwnerUpdate) {
        this.lastOwnerUpdate = lastOwnerUpdate;
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
     * The mined
     */
    public Boolean getMined() {
        return mined;
    }

    /**
     *
     * @param mined
     * The mined
     */
    public void setMined(Boolean mined) {
        this.mined = mined;
    }

    /**
     *
     * @return
     * The ownerChallenged
     */
    public Boolean getOwnerChallenged() {
        return ownerChallenged;
    }

    /**
     *
     * @param ownerChallenged
     * The owner_challenged
     */
    public void setOwnerChallenged(Boolean ownerChallenged) {
        this.ownerChallenged = ownerChallenged;
    }

    /**
     *
     * @return
     * The activeChallenged
     */
    public Boolean getActiveChallenged() {
        return activeChallenged;
    }

    /**
     *
     * @param activeChallenged
     * The active_challenged
     */
    public void setActiveChallenged(Boolean activeChallenged) {
        this.activeChallenged = activeChallenged;
    }

    /**
     *
     * @return
     * The lastOwnerProved
     */
    public String getLastOwnerProved() {
        return lastOwnerProved;
    }

    /**
     *
     * @param lastOwnerProved
     * The last_owner_proved
     */
    public void setLastOwnerProved(String lastOwnerProved) {
        this.lastOwnerProved = lastOwnerProved;
    }

    /**
     *
     * @return
     * The lastActiveProved
     */
    public String getLastActiveProved() {
        return lastActiveProved;
    }

    /**
     *
     * @param lastActiveProved
     * The last_active_proved
     */
    public void setLastActiveProved(String lastActiveProved) {
        this.lastActiveProved = lastActiveProved;
    }

    /**
     *
     * @return
     * The recoveryAccount
     */
    public String getRecoveryAccount() {
        return recoveryAccount;
    }

    /**
     *
     * @param recoveryAccount
     * The recovery_account
     */
    public void setRecoveryAccount(String recoveryAccount) {
        this.recoveryAccount = recoveryAccount;
    }

    /**
     *
     * @return
     * The lastAccountRecovery
     */
    public String getLastAccountRecovery() {
        return lastAccountRecovery;
    }

    /**
     *
     * @param lastAccountRecovery
     * The last_account_recovery
     */
    public void setLastAccountRecovery(String lastAccountRecovery) {
        this.lastAccountRecovery = lastAccountRecovery;
    }

    /**
     *
     * @return
     * The commentCount
     */
    public Integer getCommentCount() {
        return commentCount;
    }

    /**
     *
     * @param commentCount
     * The comment_count
     */
    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    /**
     *
     * @return
     * The lifetimeVoteCount
     */
    public Integer getLifetimeVoteCount() {
        return lifetimeVoteCount;
    }

    /**
     *
     * @param lifetimeVoteCount
     * The lifetime_vote_count
     */
    public void setLifetimeVoteCount(Integer lifetimeVoteCount) {
        this.lifetimeVoteCount = lifetimeVoteCount;
    }

    /**
     *
     * @return
     * The postCount
     */
    public Integer getPostCount() {
        return postCount;
    }

    /**
     *
     * @param postCount
     * The post_count
     */
    public void setPostCount(Integer postCount) {
        this.postCount = postCount;
    }

    /**
     *
     * @return
     * The votingPower
     */
    public Integer getVotingPower() {
        return votingPower;
    }

    /**
     *
     * @param votingPower
     * The voting_power
     */
    public void setVotingPower(Integer votingPower) {
        this.votingPower = votingPower;
    }

    /**
     *
     * @return
     * The lastVoteTime
     */
    public String getLastVoteTime() {
        return lastVoteTime;
    }

    /**
     *
     * @param lastVoteTime
     * The last_vote_time
     */
    public void setLastVoteTime(String lastVoteTime) {
        this.lastVoteTime = lastVoteTime;
    }

    /**
     *
     * @return
     * The balance
     */
    public String getBalance() {
        return balance;
    }

    /**
     *
     * @param balance
     * The balance
     */
    public void setBalance(String balance) {
        this.balance = balance;
    }

    /**
     *
     * @return
     * The sbdBalance
     */
    public String getSbdBalance() {
        return sbdBalance;
    }

    /**
     *
     * @param sbdBalance
     * The sbd_balance
     */
    public void setSbdBalance(String sbdBalance) {
        this.sbdBalance = sbdBalance;
    }

    /**
     *
     * @return
     * The sbdSeconds
     */
    public String getSbdSeconds() {
        return sbdSeconds;
    }

    /**
     *
     * @param sbdSeconds
     * The sbd_seconds
     */
    public void setSbdSeconds(String sbdSeconds) {
        this.sbdSeconds = sbdSeconds;
    }

    /**
     *
     * @return
     * The sbdSecondsLastUpdate
     */
    public String getSbdSecondsLastUpdate() {
        return sbdSecondsLastUpdate;
    }

    /**
     *
     * @param sbdSecondsLastUpdate
     * The sbd_seconds_last_update
     */
    public void setSbdSecondsLastUpdate(String sbdSecondsLastUpdate) {
        this.sbdSecondsLastUpdate = sbdSecondsLastUpdate;
    }

    /**
     *
     * @return
     * The sbdLastInterestPayment
     */
    public String getSbdLastInterestPayment() {
        return sbdLastInterestPayment;
    }

    /**
     *
     * @param sbdLastInterestPayment
     * The sbd_last_interest_payment
     */
    public void setSbdLastInterestPayment(String sbdLastInterestPayment) {
        this.sbdLastInterestPayment = sbdLastInterestPayment;
    }

    /**
     *
     * @return
     * The vestingShares
     */
    public String getVestingShares() {
        return vestingShares;
    }

    /**
     *
     * @param vestingShares
     * The vesting_shares
     */
    public void setVestingShares(String vestingShares) {
        this.vestingShares = vestingShares;
    }

    /**
     *
     * @return
     * The vestingWithdrawRate
     */
    public String getVestingWithdrawRate() {
        return vestingWithdrawRate;
    }

    /**
     *
     * @param vestingWithdrawRate
     * The vesting_withdraw_rate
     */
    public void setVestingWithdrawRate(String vestingWithdrawRate) {
        this.vestingWithdrawRate = vestingWithdrawRate;
    }

    /**
     *
     * @return
     * The nextVestingWithdrawal
     */
    public String getNextVestingWithdrawal() {
        return nextVestingWithdrawal;
    }

    /**
     *
     * @param nextVestingWithdrawal
     * The next_vesting_withdrawal
     */
    public void setNextVestingWithdrawal(String nextVestingWithdrawal) {
        this.nextVestingWithdrawal = nextVestingWithdrawal;
    }

    /**
     *
     * @return
     * The withdrawn
     */
    public Integer getWithdrawn() {
        return withdrawn;
    }

    /**
     *
     * @param withdrawn
     * The withdrawn
     */
    public void setWithdrawn(Integer withdrawn) {
        this.withdrawn = withdrawn;
    }

    /**
     *
     * @return
     * The toWithdraw
     */
    public Integer getToWithdraw() {
        return toWithdraw;
    }

    /**
     *
     * @param toWithdraw
     * The to_withdraw
     */
    public void setToWithdraw(Integer toWithdraw) {
        this.toWithdraw = toWithdraw;
    }

    /**
     *
     * @return
     * The withdrawRoutes
     */
    public Integer getWithdrawRoutes() {
        return withdrawRoutes;
    }

    /**
     *
     * @param withdrawRoutes
     * The withdraw_routes
     */
    public void setWithdrawRoutes(Integer withdrawRoutes) {
        this.withdrawRoutes = withdrawRoutes;
    }

    /**
     *
     * @return
     * The curationRewards
     */
    public Integer getCurationRewards() {
        return curationRewards;
    }

    /**
     *
     * @param curationRewards
     * The curation_rewards
     */
    public void setCurationRewards(Integer curationRewards) {
        this.curationRewards = curationRewards;
    }

    /**
     *
     * @return
     * The postingRewards
     */
    public Integer getPostingRewards() {
        return postingRewards;
    }

    /**
     *
     * @param postingRewards
     * The posting_rewards
     */
    public void setPostingRewards(Integer postingRewards) {
        this.postingRewards = postingRewards;
    }

    /**
     *
     * @return
     * The proxiedVsfVotes
     */
    public List<Integer> getProxiedVsfVotes() {
        return proxiedVsfVotes;
    }

    /**
     *
     * @param proxiedVsfVotes
     * The proxied_vsf_votes
     */
    public void setProxiedVsfVotes(List<Integer> proxiedVsfVotes) {
        this.proxiedVsfVotes = proxiedVsfVotes;
    }

    /**
     *
     * @return
     * The witnessesVotedFor
     */
    public Integer getWitnessesVotedFor() {
        return witnessesVotedFor;
    }

    /**
     *
     * @param witnessesVotedFor
     * The witnesses_voted_for
     */
    public void setWitnessesVotedFor(Integer witnessesVotedFor) {
        this.witnessesVotedFor = witnessesVotedFor;
    }

    /**
     *
     * @return
     * The averageBandwidth
     */
    public Long getAverageBandwidth() {
        return averageBandwidth;
    }

    /**
     *
     * @param averageBandwidth
     * The average_bandwidth
     */
    public void setAverageBandwidth(Long averageBandwidth) {
        this.averageBandwidth = averageBandwidth;
    }

    /**
     *
     * @return
     * The lifetimeBandwidth
     */
    public String getLifetimeBandwidth() {
        return lifetimeBandwidth;
    }

    /**
     *
     * @param lifetimeBandwidth
     * The lifetime_bandwidth
     */
    public void setLifetimeBandwidth(String lifetimeBandwidth) {
        this.lifetimeBandwidth = lifetimeBandwidth;
    }

    /**
     *
     * @return
     * The lastBandwidthUpdate
     */
    public String getLastBandwidthUpdate() {
        return lastBandwidthUpdate;
    }

    /**
     *
     * @param lastBandwidthUpdate
     * The last_bandwidth_update
     */
    public void setLastBandwidthUpdate(String lastBandwidthUpdate) {
        this.lastBandwidthUpdate = lastBandwidthUpdate;
    }

    /**
     *
     * @return
     * The averageMarketBandwidth
     */
    public Integer getAverageMarketBandwidth() {
        return averageMarketBandwidth;
    }

    /**
     *
     * @param averageMarketBandwidth
     * The average_market_bandwidth
     */
    public void setAverageMarketBandwidth(Integer averageMarketBandwidth) {
        this.averageMarketBandwidth = averageMarketBandwidth;
    }

    /**
     *
     * @return
     * The lastMarketBandwidthUpdate
     */
    public String getLastMarketBandwidthUpdate() {
        return lastMarketBandwidthUpdate;
    }

    /**
     *
     * @param lastMarketBandwidthUpdate
     * The last_market_bandwidth_update
     */
    public void setLastMarketBandwidthUpdate(String lastMarketBandwidthUpdate) {
        this.lastMarketBandwidthUpdate = lastMarketBandwidthUpdate;
    }

    /**
     *
     * @return
     * The lastPost
     */
    public String getLastPost() {
        return lastPost;
    }

    /**
     *
     * @param lastPost
     * The last_post
     */
    public void setLastPost(String lastPost) {
        this.lastPost = lastPost;
    }

    /**
     *
     * @return
     * The lastRootPost
     */
    public String getLastRootPost() {
        return lastRootPost;
    }

    /**
     *
     * @param lastRootPost
     * The last_root_post
     */
    public void setLastRootPost(String lastRootPost) {
        this.lastRootPost = lastRootPost;
    }

    /**
     *
     * @return
     * The postBandwidth
     */
    public Integer getPostBandwidth() {
        return postBandwidth;
    }

    /**
     *
     * @param postBandwidth
     * The post_bandwidth
     */
    public void setPostBandwidth(Integer postBandwidth) {
        this.postBandwidth = postBandwidth;
    }

    /**
     *
     * @return
     * The lastActive
     */
    public String getLastActive() {
        return lastActive;
    }

    /**
     *
     * @param lastActive
     * The last_active
     */
    public void setLastActive(String lastActive) {
        this.lastActive = lastActive;
    }

    /**
     *
     * @return
     * The activityShares
     */
    public String getActivityShares() {
        return activityShares;
    }

    /**
     *
     * @param activityShares
     * The activity_shares
     */
    public void setActivityShares(String activityShares) {
        this.activityShares = activityShares;
    }

    /**
     *
     * @return
     * The lastActivityPayout
     */
    public String getLastActivityPayout() {
        return lastActivityPayout;
    }

    /**
     *
     * @param lastActivityPayout
     * The last_activity_payout
     */
    public void setLastActivityPayout(String lastActivityPayout) {
        this.lastActivityPayout = lastActivityPayout;
    }

    /**
     *
     * @return
     * The vestingBalance
     */
    public String getVestingBalance() {
        return vestingBalance;
    }

    /**
     *
     * @param vestingBalance
     * The vesting_balance
     */
    public void setVestingBalance(String vestingBalance) {
        this.vestingBalance = vestingBalance;
    }

    /**
     *
     * @return
     * The transferHistory
     */
    public List<Object> getTransferHistory() {
        return transferHistory;
    }

    /**
     *
     * @param transferHistory
     * The transfer_history
     */
    public void setTransferHistory(List<Object> transferHistory) {
        this.transferHistory = transferHistory;
    }

    /**
     *
     * @return
     * The marketHistory
     */
    public List<Object> getMarketHistory() {
        return marketHistory;
    }

    /**
     *
     * @param marketHistory
     * The market_history
     */
    public void setMarketHistory(List<Object> marketHistory) {
        this.marketHistory = marketHistory;
    }

    /**
     *
     * @return
     * The postHistory
     */
    public List<Object> getPostHistory() {
        return postHistory;
    }

    /**
     *
     * @param postHistory
     * The post_history
     */
    public void setPostHistory(List<Object> postHistory) {
        this.postHistory = postHistory;
    }

    /**
     *
     * @return
     * The voteHistory
     */
    public List<Object> getVoteHistory() {
        return voteHistory;
    }

    /**
     *
     * @param voteHistory
     * The vote_history
     */
    public void setVoteHistory(List<Object> voteHistory) {
        this.voteHistory = voteHistory;
    }

    /**
     *
     * @return
     * The otherHistory
     */
    public List<Object> getOtherHistory() {
        return otherHistory;
    }

    /**
     *
     * @param otherHistory
     * The other_history
     */
    public void setOtherHistory(List<Object> otherHistory) {
        this.otherHistory = otherHistory;
    }

    /**
     *
     * @return
     * The witnessVotes
     */
    public List<Object> getWitnessVotes() {
        return witnessVotes;
    }

    /**
     *
     * @param witnessVotes
     * The witness_votes
     */
    public void setWitnessVotes(List<Object> witnessVotes) {
        this.witnessVotes = witnessVotes;
    }

    /**
     *
     * @return
     * The blogCategory
     */
    public BlogCategory getBlogCategory() {
        return blogCategory;
    }

    /**
     *
     * @param blogCategory
     * The blog_category
     */
    public void setBlogCategory(BlogCategory blogCategory) {
        this.blogCategory = blogCategory;
    }


    public String toJson(){
        return new Gson().toJson(this);
    }

    public static Account fromJson(String json){
        Gson gson = new Gson();
        Account account = gson.fromJson(json, Account.class);
        return account;
    }
}