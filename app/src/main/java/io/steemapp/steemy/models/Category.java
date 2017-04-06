package io.steemapp.steemy.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Category {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("abs_rshares")
    @Expose
    private String absRshares;
    @SerializedName("total_payouts")
    @Expose
    private String totalPayouts;
    @SerializedName("discussions")
    @Expose
    private Integer discussions;
    @SerializedName("last_update")
    @Expose
    private String lastUpdate;
    private String mTimeAgo;

    private static SimpleDateFormat mDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private static final int RIGHT_NOW = 1000*60*5;
    private static final int ONE_HOUR = 1000*60*60;
    private static final int ONE_DAY = 1000*60*60*24;
    private static final int ONE_MINUTE = 1000*60;

    protected void formatTime() {
        try {
            mDateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = mDateFormatter.parse(lastUpdate);
            long millisCreated = date.getTime();
            long millisNow = Calendar.getInstance().getTimeInMillis();

            long deltaMillis = millisNow - millisCreated;
            if (deltaMillis < RIGHT_NOW) {
                mTimeAgo = "updated just now";
            } else if (deltaMillis < ONE_HOUR) {
                int minutes = (int) (deltaMillis / ONE_MINUTE);
                mTimeAgo = "updated " + Integer.toString(minutes) + " minutes ago";
            } else if (deltaMillis < ONE_DAY) {
                int hours = (int) (deltaMillis / ONE_HOUR);
                mTimeAgo = "updated " + Integer.toString(hours) + " hours ago";
            } else if (deltaMillis > ONE_DAY) {
                int days = (int) (deltaMillis / ONE_DAY);
                mTimeAgo = "updated " + Integer.toString(days) + " days ago";
            }
        }catch (ParseException e){
            mTimeAgo = "updated a while ago";
        }

    }

    public String getTime(){return mTimeAgo;}

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
     * The absRshares
     */
    public String getAbsRshares() {
        return absRshares;
    }

    /**
     *
     * @param absRshares
     * The abs_rshares
     */
    public void setAbsRshares(String absRshares) {
        this.absRshares = absRshares;
    }

    /**
     *
     * @return
     * The totalPayouts
     */
    public String getTotalPayouts() {
        return totalPayouts;
    }

    /**
     *
     * @param totalPayouts
     * The total_payouts
     */
    public void setTotalPayouts(String totalPayouts) {
        this.totalPayouts = totalPayouts;
    }

    /**
     *
     * @return
     * The discussions
     */
    public Integer getDiscussions() {
        return discussions;
    }

    /**
     *
     * @param discussions
     * The discussions
     */
    public void setDiscussions(Integer discussions) {
        this.discussions = discussions;
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

}
