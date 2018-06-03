package com.example.barry.classifiedapp.models;

/**
 * Created by barry on 12/10/2017.
 */

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * * 1. 3rd, here
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

//ignore the extras
@IgnoreExtraProperties
public class HitsObject {

    @SerializedName("hits") // ref the hits obj in Postman
    @Expose
    private HitsList hits;

    // add the getter and setter methods

    public HitsList getHits() {
        return hits;
    }

    public void setHits(HitsList hits) {
        this.hits = hits;
    }
}
