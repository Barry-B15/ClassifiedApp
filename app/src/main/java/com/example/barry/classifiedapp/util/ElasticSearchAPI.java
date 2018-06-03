package com.example.barry.classifiedapp.util;

/**
 * Created by barry on 12/11/2017.
 */

import com.example.barry.classifiedapp.models.HitsObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Query;

/**
 * Inside here is where we def the method we're going to use to get the data from elastic search
 *
 * For more info on retrofit, search Mitch Taiban's Youtube channel for "retrofit"
 */

public interface ElasticSearchAPI {
   // def the method we're going to use to get the data from elastic search
    // use the GET method as we are using GET in Postman
    // We are essentially design the URL that we want to send the request to

    // the URL from Postman (this is the one we are building)
    // http://....//elasticsearch/posts/_search?default_operator=AND&q=*+city:Akishima+country:japan

    @GET("_search/")
    Call<HitsObject> search(
            @HeaderMap Map<String, String> headers,   // the Headers in Postman
            @Query("default_operator") String operator, //1st query (prepends '?')
            @Query("q") String query  // 2nd query (prepends the '&')
            );
}
