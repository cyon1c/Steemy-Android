package io.steemapp.steemy.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class BlockchainGlobals {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("head_block_number")
    @Expose
    private Integer headBlockNumber;
    @SerializedName("head_block_id")
    @Expose
    private String headBlockId;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("current_witness")
    @Expose
    private String currentWitness;
    @SerializedName("total_pow")
    @Expose
    private Integer totalPow;
    @SerializedName("num_pow_witnesses")
    @Expose
    private Integer numPowWitnesses;
    @SerializedName("virtual_supply")
    @Expose
    private String virtualSupply;
    @SerializedName("current_supply")
    @Expose
    private String currentSupply;
    @SerializedName("confidential_supply")
    @Expose
    private String confidentialSupply;
    @SerializedName("current_sbd_supply")
    @Expose
    private String currentSbdSupply;
    @SerializedName("confidential_sbd_supply")
    @Expose
    private String confidentialSbdSupply;
    @SerializedName("total_vesting_fund_steem")
    @Expose
    private String totalVestingFundSteem;
    @SerializedName("total_vesting_shares")
    @Expose
    private String totalVestingShares;
    @SerializedName("total_reward_fund_steem")
    @Expose
    private String totalRewardFundSteem;
    @SerializedName("total_reward_shares2")
    @Expose
    private String totalRewardShares2;
    @SerializedName("total_activity_fund_steem")
    @Expose
    private String totalActivityFundSteem;
    @SerializedName("total_activity_fund_shares")
    @Expose
    private String totalActivityFundShares;
    @SerializedName("sbd_interest_rate")
    @Expose
    private Integer sbdInterestRate;
    @SerializedName("average_block_size")
    @Expose
    private Integer averageBlockSize;
    @SerializedName("maximum_block_size")
    @Expose
    private Integer maximumBlockSize;
    @SerializedName("current_aslot")
    @Expose
    private Integer currentAslot;
    @SerializedName("recent_slots_filled")
    @Expose
    private String recentSlotsFilled;
    @SerializedName("participation_count")
    @Expose
    private Integer participationCount;
    @SerializedName("last_irreversible_block_num")
    @Expose
    private Integer lastIrreversibleBlockNum;
    @SerializedName("max_virtual_bandwidth")
    @Expose
    private String maxVirtualBandwidth;
    @SerializedName("current_reserve_ratio")
    @Expose
    private Integer currentReserveRatio;

    public double getSteemtoVestsRate(){
        double totalVestingFund = Double.parseDouble(totalVestingFundSteem.substring(0, totalVestingFundSteem.length()-6));
        double totalVestingSharesD = Double.parseDouble(totalVestingShares.substring(0, totalVestingShares.length()-6));

        double number = (totalVestingFund/(totalVestingSharesD/1000000.0));
        return number;
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
     * The headBlockNumber
     */
    public Integer getHeadBlockNumber() {
        return headBlockNumber;
    }

    /**
     *
     * @param headBlockNumber
     * The head_block_number
     */
    public void setHeadBlockNumber(Integer headBlockNumber) {
        this.headBlockNumber = headBlockNumber;
    }

    /**
     *
     * @return
     * The headBlockId
     */
    public String getHeadBlockId() {
        return headBlockId;
    }

    /**
     *
     * @param headBlockId
     * The head_block_id
     */
    public void setHeadBlockId(String headBlockId) {
        this.headBlockId = headBlockId;
    }

    /**
     *
     * @return
     * The time
     */
    public String getTime() {
        return time;
    }

    /**
     *
     * @param time
     * The time
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     *
     * @return
     * The currentWitness
     */
    public String getCurrentWitness() {
        return currentWitness;
    }

    /**
     *
     * @param currentWitness
     * The current_witness
     */
    public void setCurrentWitness(String currentWitness) {
        this.currentWitness = currentWitness;
    }

    /**
     *
     * @return
     * The totalPow
     */
    public Integer getTotalPow() {
        return totalPow;
    }

    /**
     *
     * @param totalPow
     * The total_pow
     */
    public void setTotalPow(Integer totalPow) {
        this.totalPow = totalPow;
    }

    /**
     *
     * @return
     * The numPowWitnesses
     */
    public Integer getNumPowWitnesses() {
        return numPowWitnesses;
    }

    /**
     *
     * @param numPowWitnesses
     * The num_pow_witnesses
     */
    public void setNumPowWitnesses(Integer numPowWitnesses) {
        this.numPowWitnesses = numPowWitnesses;
    }

    /**
     *
     * @return
     * The virtualSupply
     */
    public String getVirtualSupply() {
        return virtualSupply;
    }

    /**
     *
     * @param virtualSupply
     * The virtual_supply
     */
    public void setVirtualSupply(String virtualSupply) {
        this.virtualSupply = virtualSupply;
    }

    /**
     *
     * @return
     * The currentSupply
     */
    public String getCurrentSupply() {
        return currentSupply;
    }

    /**
     *
     * @param currentSupply
     * The current_supply
     */
    public void setCurrentSupply(String currentSupply) {
        this.currentSupply = currentSupply;
    }

    /**
     *
     * @return
     * The confidentialSupply
     */
    public String getConfidentialSupply() {
        return confidentialSupply;
    }

    /**
     *
     * @param confidentialSupply
     * The confidential_supply
     */
    public void setConfidentialSupply(String confidentialSupply) {
        this.confidentialSupply = confidentialSupply;
    }

    /**
     *
     * @return
     * The currentSbdSupply
     */
    public String getCurrentSbdSupply() {
        return currentSbdSupply;
    }

    /**
     *
     * @param currentSbdSupply
     * The current_sbd_supply
     */
    public void setCurrentSbdSupply(String currentSbdSupply) {
        this.currentSbdSupply = currentSbdSupply;
    }

    /**
     *
     * @return
     * The confidentialSbdSupply
     */
    public String getConfidentialSbdSupply() {
        return confidentialSbdSupply;
    }

    /**
     *
     * @param confidentialSbdSupply
     * The confidential_sbd_supply
     */
    public void setConfidentialSbdSupply(String confidentialSbdSupply) {
        this.confidentialSbdSupply = confidentialSbdSupply;
    }

    /**
     *
     * @return
     * The totalVestingFundSteem
     */
    public String getTotalVestingFundSteem() {
        return totalVestingFundSteem;
    }

    /**
     *
     * @param totalVestingFundSteem
     * The total_vesting_fund_steem
     */
    public void setTotalVestingFundSteem(String totalVestingFundSteem) {
        this.totalVestingFundSteem = totalVestingFundSteem;
    }

    /**
     *
     * @return
     * The totalVestingShares
     */
    public String getTotalVestingShares() {
        return totalVestingShares;
    }

    /**
     *
     * @param totalVestingShares
     * The total_vesting_shares
     */
    public void setTotalVestingShares(String totalVestingShares) {
        this.totalVestingShares = totalVestingShares;
    }

    /**
     *
     * @return
     * The totalRewardFundSteem
     */
    public String getTotalRewardFundSteem() {
        return totalRewardFundSteem;
    }

    /**
     *
     * @param totalRewardFundSteem
     * The total_reward_fund_steem
     */
    public void setTotalRewardFundSteem(String totalRewardFundSteem) {
        this.totalRewardFundSteem = totalRewardFundSteem;
    }

    /**
     *
     * @return
     * The totalRewardShares2
     */
    public String getTotalRewardShares2() {
        return totalRewardShares2;
    }

    /**
     *
     * @param totalRewardShares2
     * The total_reward_shares2
     */
    public void setTotalRewardShares2(String totalRewardShares2) {
        this.totalRewardShares2 = totalRewardShares2;
    }

    /**
     *
     * @return
     * The totalActivityFundSteem
     */
    public String getTotalActivityFundSteem() {
        return totalActivityFundSteem;
    }

    /**
     *
     * @param totalActivityFundSteem
     * The total_activity_fund_steem
     */
    public void setTotalActivityFundSteem(String totalActivityFundSteem) {
        this.totalActivityFundSteem = totalActivityFundSteem;
    }

    /**
     *
     * @return
     * The totalActivityFundShares
     */
    public String getTotalActivityFundShares() {
        return totalActivityFundShares;
    }

    /**
     *
     * @param totalActivityFundShares
     * The total_activity_fund_shares
     */
    public void setTotalActivityFundShares(String totalActivityFundShares) {
        this.totalActivityFundShares = totalActivityFundShares;
    }

    /**
     *
     * @return
     * The sbdInterestRate
     */
    public Integer getSbdInterestRate() {
        return sbdInterestRate;
    }

    /**
     *
     * @param sbdInterestRate
     * The sbd_interest_rate
     */
    public void setSbdInterestRate(Integer sbdInterestRate) {
        this.sbdInterestRate = sbdInterestRate;
    }

    /**
     *
     * @return
     * The averageBlockSize
     */
    public Integer getAverageBlockSize() {
        return averageBlockSize;
    }

    /**
     *
     * @param averageBlockSize
     * The average_block_size
     */
    public void setAverageBlockSize(Integer averageBlockSize) {
        this.averageBlockSize = averageBlockSize;
    }

    /**
     *
     * @return
     * The maximumBlockSize
     */
    public Integer getMaximumBlockSize() {
        return maximumBlockSize;
    }

    /**
     *
     * @param maximumBlockSize
     * The maximum_block_size
     */
    public void setMaximumBlockSize(Integer maximumBlockSize) {
        this.maximumBlockSize = maximumBlockSize;
    }

    /**
     *
     * @return
     * The currentAslot
     */
    public Integer getCurrentAslot() {
        return currentAslot;
    }

    /**
     *
     * @param currentAslot
     * The current_aslot
     */
    public void setCurrentAslot(Integer currentAslot) {
        this.currentAslot = currentAslot;
    }

    /**
     *
     * @return
     * The recentSlotsFilled
     */
    public String getRecentSlotsFilled() {
        return recentSlotsFilled;
    }

    /**
     *
     * @param recentSlotsFilled
     * The recent_slots_filled
     */
    public void setRecentSlotsFilled(String recentSlotsFilled) {
        this.recentSlotsFilled = recentSlotsFilled;
    }

    /**
     *
     * @return
     * The participationCount
     */
    public Integer getParticipationCount() {
        return participationCount;
    }

    /**
     *
     * @param participationCount
     * The participation_count
     */
    public void setParticipationCount(Integer participationCount) {
        this.participationCount = participationCount;
    }

    /**
     *
     * @return
     * The lastIrreversibleBlockNum
     */
    public Integer getLastIrreversibleBlockNum() {
        return lastIrreversibleBlockNum;
    }

    /**
     *
     * @param lastIrreversibleBlockNum
     * The last_irreversible_block_num
     */
    public void setLastIrreversibleBlockNum(Integer lastIrreversibleBlockNum) {
        this.lastIrreversibleBlockNum = lastIrreversibleBlockNum;
    }

    /**
     *
     * @return
     * The maxVirtualBandwidth
     */
    public String getMaxVirtualBandwidth() {
        return maxVirtualBandwidth;
    }

    /**
     *
     * @param maxVirtualBandwidth
     * The max_virtual_bandwidth
     */
    public void setMaxVirtualBandwidth(String maxVirtualBandwidth) {
        this.maxVirtualBandwidth = maxVirtualBandwidth;
    }

    /**
     *
     * @return
     * The currentReserveRatio
     */
    public Integer getCurrentReserveRatio() {
        return currentReserveRatio;
    }

    /**
     *
     * @param currentReserveRatio
     * The current_reserve_ratio
     */
    public void setCurrentReserveRatio(Integer currentReserveRatio) {
        this.currentReserveRatio = currentReserveRatio;
    }

}
