/*
 * File: MovieInfoFetcherListener.java
 * Author: Asad Ahmed
 * Description: Interface for client classes to be notified about a fetch request by MovieInfoFetcher objects.
 */

package edu.asad.ahmed.movie.scout.api;

public interface MovieInfoFetcherListener 
{
    void fetchedMoviesJSON(String json);
    void connectionTimedOut();
}
