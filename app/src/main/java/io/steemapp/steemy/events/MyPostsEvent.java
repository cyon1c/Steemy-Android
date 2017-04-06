package io.steemapp.steemy.events;

import io.steemapp.steemy.models.DiscussionList;

/**
 * Created by John on 8/20/2016.
 */
public class MyPostsEvent {
    public DiscussionList discussion;

    public MyPostsEvent(DiscussionList result){
        discussion = result;
    }
}
