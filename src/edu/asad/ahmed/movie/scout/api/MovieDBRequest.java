/*
 * File: MovieDBRequest.java
 * Author: Asad Ahmed
 * Description: Class responsible for fetching results from The Movie DB service and parsing them into movie objects.
 */

package edu.asad.ahmed.movie.scout.api;

import edu.asad.ahmed.movie.scout.model.MovieInfo;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;

public class MovieDBRequest implements MovieInfoFetcherListener
{
    private MovieDBRequestListener mListener;
    
    // API Usage constants
    private static final String MAIN_URL = "http://api.themoviedb.org/3/";
    private static final String API_KEY = "2a888e02edd08043185889ba862cb073";
    
    // Movie Data Request URL's
    private static final String NOW_SHOWING_URL = "movie/now_playing?api_key=";
    private static final String POPULAR_URL = "movie/popular?api_key=";
    private static final String TOP_RATED_URL = "movie/top_rated?api_key=";
    private static final String UPCOMING_URL = "movie/upcoming?api_key=";
    private static final String MOVIE_SEARCH_URL = "search/movie?api_key=";
    private static final String GENRE_LIST_URL = "genre/movie/list?api_key=";
    
    // Movie Data Keys
    private static final String kMOVIE_TITLE = "title";
    private static final String kMOVIE_RELEASE_DATE = "release_date";
    private static final String kMOVIE_ID = "id";
    private static final String kMOVIE_GENRES = "genre_ids";
    private static final String kMOVIE_SUMMARY = "overview";
    private static final String kMOVIE_RATING = "vote_average";
    private static final String kMOVIE_BACKDROP_PATH = "backdrop_path";
    private static final String kMOVIE_POSTER_PATH = "poster_path";
    
    
    public enum MoviesRequestType
    {
        NOW_SHOWING,
        POPULAR,
        TOP_RATED,
        UPCOMING
    }
    
    public MovieDBRequest(MovieDBRequestListener listener)
    {
        mListener = listener;
        
        // Check if config is needed
        checkIfConfigNeeded();
    }
    
    /**
     * Begins the movie request fetch on another thread.
     * The listener will be called when the data is fetched and parsed into a list of movies.
     * @param type The request type to begin the fetch for over the internet.
     */
    public void beginMovieRequest(MoviesRequestType type)
    {
        String requestURL = MovieDBRequest.MAIN_URL;
        switch (type)
        {
            case NOW_SHOWING:
                requestURL += MovieDBRequest.NOW_SHOWING_URL + MovieDBRequest.API_KEY;
                break;
                
            case POPULAR:
                requestURL += MovieDBRequest.POPULAR_URL + MovieDBRequest.API_KEY;
                break;
                
            case TOP_RATED:
                requestURL += MovieDBRequest.TOP_RATED_URL + MovieDBRequest.API_KEY;
                break;
                
            case UPCOMING:
                requestURL += MovieDBRequest.UPCOMING_URL + MovieDBRequest.API_KEY;
                break;
                
            default:
                requestURL = null;
        }
        
        fetchJSONData(requestURL);
    }

    /**
     * Begin a movie search request to search for movies with the matching title.
     * @param movieTitle The title to search for.
     */
    public void beginSearchRequest(String movieTitle)
    {
        try
        {
            String url = MAIN_URL + MOVIE_SEARCH_URL + API_KEY + "&query=" + URLEncoder.encode(movieTitle, "UTF-8");
            fetchJSONData(url);
        }
        catch (UnsupportedEncodingException ex){
            ex.printStackTrace();
        }
    }

    // JSON data was fetched by the fetcher
    @Override
    public void fetchedMoviesJSON(String json) 
    {
        // If null string returned then there was a lack of internet connection
        if (json == null){
            mListener.requestFailed();
            return;
        }

        // Parse received movies
        JSONParser parser = new JSONParser();
        JSONObject movieData;
        try
        {
            movieData = (JSONObject)parser.parse(json);
            
            JSONArray movies = (JSONArray)movieData.get("results");
            ArrayList<MovieInfo> parsedMovies = new ArrayList(20);
            
            for (int i = 0; i < movies.size(); i++)
            {
                parsedMovies.add(parseMovieJSON((JSONObject)movies.get(i)));
            }
            
        // Notify Listener
        mListener.requestCompleted(parsedMovies);
        } 
        catch (org.json.simple.parser.ParseException ex) 
        {
            Logger.getLogger(MovieDBRequest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // The fetcher reported a connection time out. Notify the request listener.
    @Override
    public void connectionTimedOut()
    {
        mListener.requestTimedOut();
    }

    private void fetchJSONData(String URLString)
    {
        Thread fetchThread = null;
        try 
        {
            fetchThread = new Thread(new MovieInfoFetcher(new URL(URLString), this));
            fetchThread.start();
        } 
        catch (MalformedURLException ex) 
        {
            Logger.getLogger(MovieDBRequest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // Parses the given JSON string for a movie into a MovieInfo object
    private MovieInfo parseMovieJSON(JSONObject movieData)
    {
        long ID = (long)movieData.get(kMOVIE_ID);
        String title = (String)movieData.get(kMOVIE_TITLE);
        double rating = 0.0;
        try
        {
            rating = (double)movieData.get(kMOVIE_RATING);
        }
        catch (ClassCastException ex)
        {
            // the rating was parsed with a long value, cast to double (issue in simple json library)
            Long longRating = (Long)movieData.get(kMOVIE_RATING);
            rating = longRating.doubleValue();
        }

        String summary = (String)movieData.get(kMOVIE_SUMMARY);

        // Parse genre id array
        JSONArray genreIDsJsonArray = (JSONArray)movieData.get(kMOVIE_GENRES);
        long[] genreIDs = new long[genreIDsJsonArray.size()];
        for (int i = 0; i < genreIDs.length; i++){
            genreIDs[i] = (long)genreIDsJsonArray.get(i);
        }
        
        // Parse date string from json
        DateFormat formatter = new SimpleDateFormat("YYYY-MM-DD");
        Date releaseDate = null;
        
        try
        {
            String releaseDateString = (String)movieData.get(kMOVIE_RELEASE_DATE);

            if (releaseDateString != null) {
                releaseDate = formatter.parse(releaseDateString);
            }
        }
        catch (ParseException ex)
        {
            releaseDate = null;
        }
        
        // Get poster and backdrop paths
        String posterPath = (String)movieData.get(kMOVIE_POSTER_PATH);
        String backdropPath = (String)movieData.get(kMOVIE_BACKDROP_PATH);
        
        MovieInfo movieInfo = new MovieInfo(ID, title, releaseDate, summary, rating, genreIDs, posterPath, backdropPath);
        
        // If the base url was fetched and loaded, set the root path and poster size
        if(mImageBaseURL != null){
            movieInfo.setPosterRootPath(mImageBaseURL, mPosterSizes[mPosterSizes.length - 3], mBackdropSizes[mBackdropSizes.length - 1]);
        }
        
        return movieInfo;
    }
    
    // Parses the given string into a date. Returns null if the parse failed
    private Date parseDateString(String dateString)
    {
        DateFormat formatter = new SimpleDateFormat("YYYY-MM-DD");
        Date date = null;
        
        try
        {
            date = formatter.parse(dateString);
        }
        catch (ParseException ex)
        {
            date = null;
        }
        
        return date;
    }
    
    // Config URL
    private final static String CONFIG_URL = MAIN_URL + "configuration?api_key=" + API_KEY;
    
    // Config constants
    private static final int DAYS_TILL_RECACHE = 5;
    private static final String CONFIG_FILE_NAME = "config.dat";
    
    // Config Keys
    private static final String kCONFIG_BASE_URL = "base_url";
    private static final String kCONFIG_SECURE_BASE_URL = "secure_base_url";
    private static final String kCONFIG_BACKDROP_SIZES = "backdrop_sizes";
    private static final String kCONFIG_POSTER_SIZES = "poster_sizes";
    
    // Config values
    private static Date mLastConfigCacheDate;
    private static String mImageBaseURL;
    private static String mImageSecureBaseURL;
    private static String[] mPosterSizes;
    private static String[] mBackdropSizes;
    private boolean mConfigWasRead;
    
    // Checks if API config data needs to be recached
    private void checkIfConfigNeeded()
    {
        boolean configNeeded = true;
        
        // Get last cache date and reconfig if more than 5 days passed
        File configFile = new File(CONFIG_FILE_NAME);

        if (configFile.exists() && !configFile.isDirectory()) {
            readConfigData();

            // Parse date and if more than 5 days passed, a recache is required
            Date now = new Date();
            int diffInDays = (int) (now.getTime() - mLastConfigCacheDate.getTime()) / (1000 * 3600 * 24);

            if (diffInDays < DAYS_TILL_RECACHE) {
                configNeeded = false;
            }
        }
        
        if (configNeeded || !mConfigWasRead){
            //System.out.println("Config recache needed");
            reCacheConfigData();
        }
        else{
            // No config needed - read cached data from config file
            //System.out.println("Found a cache and config was not required");
            readConfigData();
        }
    }
    
    // Reads in the config data from disk
    private void readConfigData()
    {       
        try 
        {
            ObjectInputStream file = new ObjectInputStream(new FileInputStream(CONFIG_FILE_NAME));
            mLastConfigCacheDate = (Date)file.readObject();
            mImageBaseURL = file.readUTF();
            mImageSecureBaseURL = file.readUTF();
            mPosterSizes = (String[])file.readObject();
            mBackdropSizes = (String[])file.readObject();
            
            mConfigWasRead = true;
            file.close();
        } 
        catch (FileNotFoundException ex) 
        {
            // No file found, config will be recached
            mConfigWasRead = false;
        }
        catch (IOException ex)
        {
            // Error reading - config will be recached
            mConfigWasRead = false;
        }
        catch (ClassNotFoundException ex)
        {
            mConfigWasRead = false;
        }
    }
    
    // Writes out the config data to file
    // NOTE: Only call after all config data was recached
    private void writeConfigData()
    {
        try
        {
            ObjectOutputStream file = new ObjectOutputStream(new FileOutputStream(CONFIG_FILE_NAME));
            file.writeObject(new Date());
            file.writeUTF(mImageBaseURL);
            file.writeUTF(mImageSecureBaseURL);
            file.writeObject(mPosterSizes);
            file.writeObject(mBackdropSizes);
            file.close();
        } 
        catch (IOException ex) 
        {
            // Failed to write, data will be not be cached and will be recached on next run
            System.err.println("Error: Unable to cache config data: \n" + ex.getMessage());
        }
    }
    
    // Re-caches the config data to the binary config file
    private void reCacheConfigData()
    {
        try
        {
            // Download the config data and parse
            //System.out.println("Config URL is: " + CONFIG_URL);
            String configJSON = URLRequest.readURLContentsAsString(new URL(CONFIG_URL));
            JSONObject configRootData = null;
            
            if (configJSON != null)
            {
                JSONParser parser = new JSONParser();
                try 
                {
                    configRootData = (JSONObject)parser.parse(configJSON);
                    JSONObject imageConfigData = (JSONObject)configRootData.get("images");
                    
                    // Get the base url data
                    mImageBaseURL = (String)imageConfigData.get(kCONFIG_BASE_URL);
                    mImageSecureBaseURL = (String)imageConfigData.get(kCONFIG_SECURE_BASE_URL);
                    
                    // Get the string arrays for the poster and backdrop size strings
                    JSONArray posterSizesData = (JSONArray)imageConfigData.get(kCONFIG_POSTER_SIZES);
                    JSONArray backdropSizesData = (JSONArray)imageConfigData.get(kCONFIG_BACKDROP_SIZES);
                    mPosterSizes = Arrays.copyOf(posterSizesData.toArray(), posterSizesData.toArray().length, String[].class);
                    mBackdropSizes = Arrays.copyOf(backdropSizesData.toArray(), backdropSizesData.toArray().length, String[].class);
                    
                    writeConfigData();
                } 
                catch (org.json.simple.parser.ParseException ex) 
                {
                    // Failed to parse... TODO: Call listener and notify error
                }
            }
        }
        catch (IOException ex)
        {
            // Failed to download config data...
            // TODO: Call listener and notify error
        }
    }

    /**
     * Fetches the strings for the genres for the given move. Note: This is operation is NOT asynchronous.
     * @param movie The movie for which the genre strings need to be fetched.
     * @return A string array for the movie genre strings.
     */
    public static String[] getGenreStrings(MovieInfo movie)
    {
        try
        {
            String jsonResult = URLRequest.readURLContentsAsString(new URL(MAIN_URL + GENRE_LIST_URL + API_KEY));

            JSONParser parser = new JSONParser();
            JSONObject jsonData = (JSONObject)parser.parse(jsonResult);
            JSONArray genres = (JSONArray)jsonData.get("genres");

            String[] genreStrings = new String[movie.getGenreIDs().length];
            for (int i = 0; i < movie.getGenreIDs().length; i++){
                genreStrings[i] = getGenreStringForID(movie.getGenreIDs()[i], genres);
            }

            return genreStrings;
        }
        catch (MalformedURLException ex){
            ex.printStackTrace();
        }
        catch (org.json.simple.parser.ParseException ex){
            ex.printStackTrace();
        }
        catch (IOException ex){
            ex.printStackTrace();
        }

        return null;
    }

    // Returns the genre string for the given genre ID using the given JSONArray of dictionaries of {ID: Genre String}
    private static String getGenreStringForID(long genreID, JSONArray genreList)
    {
        String genre = null;

        for (int i = 0; i < genreList.size(); i++)
        {
            JSONObject genrePair = (JSONObject)genreList.get(i);
            if ((long)genrePair.get("id") == genreID){
                genre = (String)genrePair.get("name");
            }
        }

        return genre;
    }
}