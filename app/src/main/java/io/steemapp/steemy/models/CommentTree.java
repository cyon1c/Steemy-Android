package io.steemapp.steemy.models;

import android.util.Log;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import io.steemapp.steemy.SteemyGlobals;

/**
 * Created by John on 8/9/2016.
 */
public class CommentTree {

    private HashMap<String, Discussion> mTreeContents;
    private ArrayList<Discussion> mSortedTree;

    private String mRoot;


    public CommentTree(HashMap<String, Discussion> comments){
        mTreeContents = new HashMap<>(comments);
        for(Map.Entry<String, Discussion> entity : comments.entrySet()){
            if(entity.getValue().getDepth() == 0)
                mRoot = entity.getValue().getCommentKey();
        }
    }

    public ArrayList<Discussion> getSortedList(SteemyGlobals.SORTS sortMethod) throws ParseException{
        mSortedTree = new ArrayList<>();
        mSortedTree.add(mTreeContents.get(mRoot));
        switch (sortMethod){
            case TRENDING:
                sortTrending(new ArrayList(mTreeContents.get(mRoot).getReplies()));
                break;
            case NEW:
                sortNew(new ArrayList(mTreeContents.get(mRoot).getReplies()));
                break;
            default:
                mSortedTree = null;
                break;
        }
        mSortedTree = new ArrayList<>(mSortedTree.subList(1, mSortedTree.size()-1));
        return mSortedTree;
    }

    private void sortTrending(ArrayList<String> children){
        TreeMap<Integer, ArrayList<String>> orderedChildren = new TreeMap<>(Collections.reverseOrder());
        ArrayList<String> bucket = new ArrayList<>();
        for(String c : children){
            int score = 2*mTreeContents.get(c).getActiveVotes().size() + mTreeContents.get(c).getReplies().size();
            if(!orderedChildren.containsKey(score)){
                bucket = new ArrayList<>();
                bucket.add(c);
                orderedChildren.put(score, bucket);
            }else{
                bucket = orderedChildren.get(score);
                bucket.add(c);
                orderedChildren.put(score, bucket);
            }
        }

        for(Map.Entry<Integer, ArrayList<String>> e : orderedChildren.entrySet()){
            ArrayList<String> entries = e.getValue();
            for(int i = 0; i < e.getValue().size(); i++){
                if(i == e.getValue().size()-1){
                    mTreeContents.get(entries.get(i)).closeCommentDiv();
                }
                mSortedTree.add(mTreeContents.get(entries.get(i)));
                if(mTreeContents.get(entries.get(i)).getReplies().size() > 0)
                    sortTrending(new ArrayList(mTreeContents.get(entries.get(i)).getReplies()));
            }
        }
    }

    private void sortNew(ArrayList<String> children) throws ParseException{
        TreeMap<Long, ArrayList<String>> orderedChildren = new TreeMap<>();
        ArrayList<String> bucket = new ArrayList<>();
        for(String c : children){
            long score = mTreeContents.get(c).getHowOld();
            if(!orderedChildren.containsKey(score)){
                bucket = new ArrayList<>();
                bucket.add(c);
                orderedChildren.put(score, bucket);
            }else{
                bucket = orderedChildren.get(score);
                bucket.add(c);
                orderedChildren.put(score, bucket);
            }
        }

        for(Map.Entry<Long, ArrayList<String>> e : orderedChildren.entrySet()){
            ArrayList<String> entries = e.getValue();
            for(int i = 0; i < e.getValue().size(); i++){
                if(i == e.getValue().size()-1){
                    mTreeContents.get(entries.get(i)).closeCommentDiv();
                }
                mSortedTree.add(mTreeContents.get(entries.get(i)));
                if(mTreeContents.get(entries.get(i)).getReplies().size() > 0)
                    sortNew(new ArrayList(mTreeContents.get(entries.get(i)).getReplies()));
            }
        }
    }

    public Discussion getRoot(){
        return mTreeContents.get(mRoot);
    }

}
