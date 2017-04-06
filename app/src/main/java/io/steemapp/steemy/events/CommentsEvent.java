package io.steemapp.steemy.events;

import io.steemapp.steemy.models.CommentTree;

/**
 * Created by John on 8/9/2016.
 */
public class CommentsEvent {
    public CommentTree tree;
    public CommentsEvent(CommentTree tree){
        this.tree = tree;
    }
}
