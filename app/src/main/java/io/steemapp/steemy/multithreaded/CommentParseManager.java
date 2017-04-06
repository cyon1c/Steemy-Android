package io.steemapp.steemy.multithreaded;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.steemapp.steemy.models.Discussion;

/**
 * Created by John on 8/11/2016.
 */
public class CommentParseManager {

    private static final int SUCCESS = 0;
    private static final int TIME_OUT = 1;

    static ThreadPoolExecutor mExecutor;

    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    private static final int KEEP_ALIVE = 1;
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;

    private final BlockingQueue<Runnable> mWorkQueue;

    private static CommentParseManager instance;

    static {
        instance = new CommentParseManager();
    }

    private CommentParseManager(){

        mWorkQueue = new LinkedBlockingQueue<>();

        mExecutor = new ThreadPoolExecutor(
                NUMBER_OF_CORES*2,
                NUMBER_OF_CORES*2,
                KEEP_ALIVE,
                TIME_UNIT,
                mWorkQueue);
    }

    static public ArrayList<Future<ArrayList<Discussion>>> processComments(List<Discussion> d){
        ArrayList<Future<ArrayList<Discussion>>> futures = new ArrayList<>();
        for(int i = 0; i < d.size(); i=i+5) {
            if(i+5 >= d.size()){
                futures.add(instance.mExecutor.submit(new CommentParsingCallable(new ParseTask(d.subList(i, d.size()-1)))));
            }else
                futures.add(instance.mExecutor.submit(new CommentParsingCallable(new ParseTask(d.subList(i, i+5)))));
        }

        return futures;
    }
}
