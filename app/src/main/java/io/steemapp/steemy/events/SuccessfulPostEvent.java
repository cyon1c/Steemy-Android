package io.steemapp.steemy.events;

/**
 * Created by John on 9/9/2016.
 */
public class SuccessfulPostEvent {
    public String permlink;
    public String author;
    public String category;

    public SuccessfulPostEvent(String permlink, String author, String category){
        this.permlink = permlink;
        this.author = author;
        this.category = category;
    }
}
