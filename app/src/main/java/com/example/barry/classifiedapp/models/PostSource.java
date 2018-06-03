package com.example.barry.classifiedapp.models;

/**
 * Created by barry on 12/10/2017.
 */

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 1. Start from the PostSource class here
 *      add ignoreExtraProperty at the top of the class so that retrofit knows to ignore the
 *      extra props. otherwise it doesnt know that we want only the _sorce items from the hits() list
 *
 * 2. define the Post post
 *      but on top of that add
 *      @Expose and
 *      @SerializeName (this references the _source in our Postman)
 * 3. Add the getter, and setter methods
 *
 */

@IgnoreExtraProperties
public class PostSource {

    @SerializedName("_source")  // ref the _source in Postman
    @Expose
    private Post post;  // define the Post

    // add the getter and setter methods
    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
}
