package com.tennisdc.tln.model;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import java.util.List;

import io.realm.RealmObject;


/**
 * Created  on 2015-03-12.
 */
@Parcel(value = Parcel.Serialization.BEAN, analyze = {Comment.class})
public class Comment extends RealmObject {


    @ParcelConstructor
    public Comment(String content, String player_name, String comment_date) {
        this.content = content;
        this.player_name = player_name;
        this.comment_date = comment_date;
    }
    //@SerializedName("content")
    private String content;

   // @SerializedName("player_name")
    private String player_name;

   // @SerializedName("comment_date")
    private String comment_date;

    public Comment() {
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPlayer_name() {
        return player_name;
    }

    public void setPlayer_name(String player_name) {
        this.player_name = player_name;
    }

    public String getComment_date() {
        return comment_date;
    }

    public void setComment_date(String comment_date) {
        this.comment_date = comment_date;
    }
}
