/*
 * File: MovieDBRequestListener.java
 * Author: Asad Ahmed
 * Description: Interface for client classes to be notified about the status of a request from a MovieDBRequest object.
 */

package edu.asad.ahmed.movie.scout.api;

import edu.asad.ahmed.movie.scout.model.MovieInfo;
import java.util.ArrayList;

/**
 * Interface for listening to completed fetch requests
 * @author Asad Ahmed
 */
public interface MovieDBRequestListener 
{
    /**
     * Called when the URL request is completed and the following json data was fetched
     * @param moviesInfo A List of movie items
     */
    void requestCompleted(ArrayList<MovieInfo> moviesInfo);

    /**
     * Called when the URL request timed out due to a bad connection.
     */
    void requestTimedOut();

    /**
     * Called when the request fails due to the absence of an internet connection.
     */
    void requestFailed();
}