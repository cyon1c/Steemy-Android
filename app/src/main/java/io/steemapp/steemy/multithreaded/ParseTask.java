package io.steemapp.steemy.multithreaded;

import java.util.List;

import io.steemapp.steemy.models.Discussion;

/**
 * Created by John on 8/11/2016.
 */
public class ParseTask {

    public List<Discussion> mComments;

    private Thread mTaskThread;

    public ParseTask(List<Discussion> d){
        mComments = d;
    }

    public void setParsingThread(Thread thread){
        mTaskThread = thread;
    }

    public List<Discussion> doTask(){
        for(Discussion d : mComments){
            d.formatDiscussion();
        }

        return mComments;
    }
}
