package io.steemapp.steemy.transactions;

import io.steemapp.steemy.SteemyGlobals;

/**
 * Created by John on 10/13/2016.
 */

public class OperationFactory {

    public static Operation createOperation(SteemyGlobals.Tx txType){
        switch (txType){
            case vote:
                return new VoteOperation();
            case comment:
                return new CommentOperation();
            case transfer:
                return new TransferOperation();
            case follow:
                return new FollowOperation();
        }
        return null;
    }
}
