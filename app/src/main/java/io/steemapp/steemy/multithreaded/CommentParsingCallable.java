package io.steemapp.steemy.multithreaded;

import android.os.Process;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import io.steemapp.steemy.models.Discussion;

/**
 * Created by John on 8/11/2016.
 */
public class CommentParsingCallable implements Callable<ArrayList<Discussion>> {

    ParseTask mTask;

    public CommentParsingCallable(ParseTask task){
        mTask = task;
    }

    @Override
    public ArrayList<Discussion> call() {
        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        mTask.setParsingThread(Thread.currentThread());
        return new ArrayList<Discussion>(mTask.doTask());
    }
}
