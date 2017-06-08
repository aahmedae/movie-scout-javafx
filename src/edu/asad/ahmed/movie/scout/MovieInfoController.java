package edu.asad.ahmed.movie.scout;

/**
 * File: MovieInfoController.java
 * Description: Controller for viewing info for a single movie.
 * Author: Asad Ahmed
 */

import edu.asad.ahmed.movie.scout.api.MovieDBRequest;
import edu.asad.ahmed.movie.scout.model.MovieInfo;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;

public class MovieInfoController extends Controller
{
    @FXML private Label mMovieTitleLabel;
    @FXML private Label mGenresLabel;
    @FXML private Label mReleaseDateLabel;
    @FXML private Label mRatingLabel;
    @FXML private Label mSummaryLabel;
    @FXML private ImageView mBackdropImageView;
    @FXML private ScrollPane mScrollPane;
    @FXML private VBox mMainVBox;
    @FXML private GridPane mGridPane;

    private MovieInfo mMovie;

    // Set the movie for this controller
    public void setMovie(MovieInfo movie)
    {
        mMovie = movie;
        initialize();
    }

    // Called when the view had been loaded
    @FXML public void initialize()
    {
        // Load the movie info if movie has been set
        if (mMovie != null)
        {
            mMovieTitleLabel.setText(mMovie.getTitle());
            mRatingLabel.setText(String.format("%.2f", mMovie.getRating()));

            if (mMovie.getReleaseDate() != null) {
                mReleaseDateLabel.setText(String.format("%s", mMovie.getReleaseDate().toString()));
            }
            else{
                mReleaseDateLabel.setText("N/A");
            }

            mSummaryLabel.setText(mMovie.getSummary());
            loadMovieBackdrop();
            loadGenres();
        }

        // Bind main vbox layout width to scroll pane width so it grows in width with the scroll pane
        mMainVBox.prefWidthProperty().bind(mScrollPane.widthProperty());

        // Setup the backdrop image view
        mBackdropImageView.setPreserveRatio(true);

        // Setup the gridpane column
        mGridPane.prefWidthProperty().bind(mScrollPane.widthProperty());
    }

    // User clicks on the back button to navigate back to the movies scene
    @FXML public void backToMoviesButtonClick()
    {
        mMainApplication.requestTransitionBackToMoviesController();
    }

    // Loads the movie backdrop image asynchronously
    private void loadMovieBackdrop()
    {
        if (mMovie.getFullBackdropPath() != null){
            Image backdropImage = new Image(mMovie.getFullBackdropPath(), true);
            mBackdropImageView.setImage(backdropImage);
        }
    }

    // Loads the genres label with a string concatenated with all the genres for the movie
    private void loadGenres()
    {
        mGenresLabel.setText("");

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                String[] genres = MovieDBRequest.getGenreStrings(mMovie);
                StringBuilder builder = new StringBuilder();

                for (String genre : genres){
                    builder.append(genre);

                    // if not last string in array, append a ,
                    if (!genres[genres.length - 1].equals(genre)){
                        builder.append(", ");
                    }
                }

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        mGenresLabel.setText(builder.toString());
                    }
                });
            }
        });

        t.start();
    }
}