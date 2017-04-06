package io.steemapp.steemy.listeners;

import io.steemapp.steemy.models.Discussion;
import io.steemapp.steemy.models.DiscussionList;

/**
 * Created by John on 8/19/2016.
 */
public interface OnDiscussionListInteractionListener {
        void onLoadMore(DiscussionList discussionList);
        void onDiscussionClicked(Discussion discussion);
        void onRefresh();
        void vote(Discussion discussion, short vote_weight);
        void customVoteRequested(Discussion discussion, boolean upvote);
}
