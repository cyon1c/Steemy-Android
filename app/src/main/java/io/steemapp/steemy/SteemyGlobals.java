package io.steemapp.steemy;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;

import java.util.Calendar;
import java.util.HashMap;

import io.steemapp.steemy.models.BlockchainGlobals;
import io.steemapp.steemy.models.CategoryList;
import io.steemapp.steemy.views.SteemyTextView;

/**
 * Created by John on 7/31/2016.
 */
public class SteemyGlobals {

    public static HashMap<String, Typeface> typefaces = new HashMap<>();
    public enum SORTS{
        TRENDING{public String toString(){return "trending";}},
        HOT{public String toString(){return "hot";}},
        PAYOUT_TIME{public String toString(){return "payout_time";}},
        NEW{public String toString(){return "new";}},
        ACTIVE{public String toString(){return "active";}},
        RESPONSES{public String toString(){return "responses";}},
        POPULAR{public String toString(){return "popular";}}
    }
    public enum FOLLOWER_TYPE{
        FOLLOWING{public String toString(){return "following";}},
        FOLLOWERS{public String toString(){return "followers";}},
        IGNORED{public String toString(){return "ignored";}},
    }

    public enum FOLLOW_STATE{
        FOLLOWED,
        IGNORED,
        NONE
    }
    public enum KEY_TYPE{
        MASTER(4){public String toString(){return "master";}},
        OWNER(3){public String toString(){return "owner";}},
        ACTIVE(2){public String toString(){return "active";}},
        POSTING(1){public String toString(){return "posting";}},
        MEMO(0){public String toString(){return "memo";}};

        private final int value;

        KEY_TYPE(final int newValue){
            value = newValue;
        }

        public int getValue(){return value;}
    }

    public enum Tx {
        vote(0) {
            public String toString() {
                return "vote";
            }
        },
        comment(1) {
            public String toString() {
                return "comment";
            }
        },
        transfer(3) {
            public String toString(){ return "transfer"; }
        },
        follow(18) {
            public String toString(){ return "follow"; }
        };

        private final int value;

        Tx(final int newValue) {
            value = newValue;
        }

        public int getValue() {
            return value;
        }
    }

//    public static String getTrending(){return SORTS.TRENDING.toString();}
//    public static String getHot(){return SORTS.HOT.toString();}
//    public static String getPayout(){return SORTS.PAYOUT_TIME.toString();}
    public static String getNew(){return SORTS.NEW.toString();}
//    public static String getActive(){return SORTS.ACTIVE.toString();}
//    public static String getResponses(){return SORTS.RESPONSES.toString();}
//    public static String getPopular(){return SORTS.POPULAR.toString();}

    public static AlertDialog buildSteemyErrorDialog(final Activity activity, String title, String message){
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View customView = inflater.inflate(R.layout.dialog_error, null);
        builder.setView(customView);
        if (message == null) {
            message = "There was a problem. Please make sure you have a working internet connection and restart the app. If the problem persists, please contact us at contact@steemapp.io.";
        }
        ((SteemyTextView) customView.findViewById(R.id.error_title)).setText(title);
        ((SteemyTextView) customView.findViewById(R.id.error_message)).setText(message);
        Linkify.addLinks(((SteemyTextView) customView.findViewById(R.id.error_message)), Linkify.ALL);
        ((SteemyTextView) customView.findViewById(R.id.error_message)).setLinkTextColor(activity.getResources().getColor(R.color.colorPrimaryDark));

        return builder.create();
    }

    public static BlockchainGlobals BLOCKCHAIN_GLOBALS;
    public static CategoryList steemyCategories;
    private static Calendar lastCategorySync;

    public static void setCategories(CategoryList categories){
        lastCategorySync = Calendar.getInstance();
        steemyCategories = categories;
    }

    public static CategoryList getCategories(){
        return steemyCategories;
    }

    public static boolean areCategoriesInSync(){
        if(steemyCategories != null) {
            Calendar now = Calendar.getInstance();
            now.set(Calendar.MINUTE, -10);
            return !lastCategorySync.before(now);
        }else
            return false;
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static String byteArrayToHexString(byte[] bytes){
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}
