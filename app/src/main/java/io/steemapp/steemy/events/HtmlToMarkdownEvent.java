package io.steemapp.steemy.events;

/**
 * Created by John on 9/8/2016.
 */
public class HtmlToMarkdownEvent {
    public String body;

    public HtmlToMarkdownEvent(String body){
        this.body = body;
    }
}
