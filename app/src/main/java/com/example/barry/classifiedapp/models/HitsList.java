package com.example.barry.classifiedapp.models;

/**
 * Created by barry on 12/10/2017.
 */

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * * 1. 2nd, here
 *      add ignoreExtraProperty at the top of the class so that retrofit knows to ignore the
 *      extra props. otherwise it doesnt know that we want only the _sorce items from the hits() list
 *
 * 2. define the List
 *      but on top of that add
 *      @Expose and
 *      @SerializeName (this references the _source in our Postman)
 * 3. Add the getter, and setter methods
 *
 */

// declare the ignore extra prop
@IgnoreExtraProperties
public class HitsList {

    // def the List but on top of it declare the @Expose (1st), then @serialize (2nd)

    @SerializedName("hits") // reference the hits in our Postman
    @Expose
    private List<PostSource> postIndex;

    // add the getters and setters methods
    public List<PostSource> getPostIndex() {
        return postIndex;
    }

    public void setPostIndex(List<PostSource> postIndex) {
        this.postIndex = postIndex;
    }
}
