/**
 * MovieInfo.java
 * Model class for representing a movie and its basic information.
 * Author: Asad Ahmed
 */

package edu.asad.ahmed.movie.scout.model;

import java.util.Date;

/**
 * Model class for representing a movie and its basic information.
 * @author asad
 */
public class MovieInfo 
{
    private long mID;
    private String mTitle;
    private Date mReleaseDate;
    private String mSummary;
    private String mPosterPath;
    private String mFullPosterPath;
    private String mBackdropPath;
    private String mFullBackdropPath;
    private double mRating;
    private long[] mGenreIDs;
    
    /**
     * Construct movie info with minimal required information.
     * @param ID The ID of the movie
     * @param title The title of the movie
     * @param date The date the movie was released
     * @param summary The summary of the movie plot
     * @param rating The average vote rating for this movie
     * @param genreIDs The array of the ID numbers for the genres for this movie.
     * @param posterPath The file path for the movie poster 
     * @param backdropPath The file path for the backdrop poster
     */
    public MovieInfo(long ID, String title, Date date, String summary, double rating, long[] genreIDs, String posterPath, String backdropPath)
    {
        mID = ID;
        mTitle = title;
        mReleaseDate = date;
        mSummary = summary;
        mRating = rating;
        mPosterPath = posterPath;
        mBackdropPath = backdropPath;
        mGenreIDs = genreIDs;
    }
    
    /**
     * Sets the root path for the movie posters and backdrop posters.
     * @param rootPath The root path for the poster images.
     * @param posterSize A string representing the size variant of the posters to download
     * @param backdropSize A string representing the size variant of the backdrop images to download.
     */
    public void setPosterRootPath(String rootPath, String posterSize, String backdropSize)
    {
        mFullPosterPath = String.format("%s%s%s", rootPath, posterSize, mPosterPath);
        mFullBackdropPath = String.format("%s%s%s", rootPath, posterSize, mBackdropPath);
    }

    /**
     * Returns the ID of the movie
     * @return The ID of the movie
     */
    public long getID() 
    {
        return mID;
    }

    /**
     * Returns the title of the movie
     * @return The title of the movie
     */
    public String getTitle() 
    {
        return mTitle;
    }

    /**
     * Returns the release date of the movie
     * @return A date representing the date the movie was released
     */
    public Date getReleaseDate() 
    {
        return mReleaseDate;
    }

    /**
     * Returns the plot summary of the movie
     * @return The plot summary of the movie
     */
    public String getSummary() 
    {
        return mSummary;
    }
    
    /**
     * Returns the rating for the movie.
     * @return The average rating for the movie.
     */
    public double getRating()
    {
        return mRating;
    }

    /**
     * Returns the IDs of the genres for this movie.
     * @return An array with integers representing the genres of the movie.
     */
    public long[] getGenreIDs()
    {
        return mGenreIDs;
    }

    /**
     * Returns the full path for the movie poster if it has been set
     * @return A string representing the full path to locate and find the poster image
     */
    public String getFullPosterPath() 
    {
        return mFullPosterPath;
    }

    /**
     * Returns the full path for the backdrop poster if it has been set
     * @return A string representing the full path to locate and find the poster image
     */
    public String getFullBackdropPath() 
    {
        return mFullBackdropPath;
    } 
}