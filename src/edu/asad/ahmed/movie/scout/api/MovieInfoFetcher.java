/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.asad.ahmed.movie.scout.api;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MovieInfoFetcher implements Runnable
{
    private URL mRequestURL;
    private MovieInfoFetcherListener mRequestListener;
    
    /**
     * Construct fetcher with given URL
     * @param requestURL The URL for sending the HTTP request
     * @param listener The listener to call when the fetch completes or fails
     */
    public MovieInfoFetcher(URL requestURL, MovieInfoFetcherListener listener) throws InvalidParameterException
    {
        if (requestURL == null){
            throw new InvalidParameterException("NULL URL passed to fetcher.");
        }
        
        mRequestListener = listener;
        mRequestURL = requestURL;
    }

    @Override
    /**
     * Begin the data fetch asynchronously.
     */
    public void run() 
    {
        try 
        {
            String json = URLRequest.readURLContentsAsString(mRequestURL);
            mRequestListener.fetchedMoviesJSON(json);
        } 
        catch (SocketTimeoutException ex)
        {
            // Notify the listener that a connection timed out.
            mRequestListener.connectionTimedOut();
        }      
    }
}