/*
 * File: URLRequest.java
 * Author: Asad Ahmed
 * Description: Static utility class for performing IO to read and returns the contents of the given URL as a string
 */

package edu.asad.ahmed.movie.scout.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * Static utility class for performing IO to read and returns the contents of the given URL as a string
 * @author asad
 */
public class URLRequest 
{
    /**
     * Read and return the contents of the given URL as a string.
     * @param url The URL to read data from.
     * @return A string representing the contents of the url. Returns null if an error occurred while reading the data.
     * @throws java.io.IOException Throws if an error occurs when attempting to read url contents
     */
    public static String readURLContentsAsString(URL url) throws SocketTimeoutException
    {
        try
        {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(10 * 1000);

            int statusCode = connection.getResponseCode();
            String line = null;
            StringBuilder dataStr = null;

            // Status OK - get the data from the stream
            if (statusCode == 200)
            {
                dataStr = new StringBuilder();
                BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                line = input.readLine();
                while (line != null) {
                    dataStr.append(line);
                    line = input.readLine();
                }

                return dataStr.toString();
            }

            return null;
        }
        catch (SocketTimeoutException ex){
            throw new SocketTimeoutException();
        }
        catch (IOException ex){
            return null;
        }
    }
}
