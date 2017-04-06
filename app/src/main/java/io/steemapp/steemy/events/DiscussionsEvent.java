package io.steemapp.steemy.events;

import java.util.List;

import io.steemapp.steemy.models.Discussion;
import io.steemapp.steemy.models.DiscussionList;

/**
 * Created by John on 7/28/2016.
 */
public class DiscussionsEvent {
    public DiscussionList discussion;

    public DiscussionsEvent(DiscussionList result){
        discussion = result;
    }
}
