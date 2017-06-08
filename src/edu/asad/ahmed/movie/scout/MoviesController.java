
/**
 * File: MoviesController.java
 * Description: Main controller class for displaying and handling movies.
 * Author: Asad Ahmed
 */

package edu.asad.ahmed.movie.scout;

import edu.asad.ahmed.movie.scout.model.MovieInfo;
import edu.asad.ahmed.movie.scout.api.MovieDBRequest;
import edu.asad.ahmed.movie.scout.api.MovieDBRequestListener;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;

import java.io.IOException;
import java.util.ArrayList;

public class MoviesController extends Controller implements MovieDBRequestListener
{
    @FXML
    private ScrollPane mMoviesScrollPane;

    @FXML
    private VBox mMovieTypeVBox;

    private FlowPane mMoviesFlowPane;

    @FXML
    private ListView<String> mMovieTypeListView;

    @FXML
    private Label mStatusLabel;

    @FXML
    private ProgressBar mProgressBar;

    @FXML
    private TextField mSearchTextField;

    @FXML
    private Button mSearchButton;

    @FXML
    private Button mClearSearchButton;

    private ArrayList<MovieInfo> mMovies;
    private double[] mImagesLoadingProgress;
    private MovieDBRequest mMovieRequest;

    // Called by JavaFx runtime when view is loaded
    @FXML public void initialize()
    {
        mMovieRequest = new MovieDBRequest(this);

        // Load the movie types into the movie type list view
        mMovieTypeListView.getItems().addAll("Now Showing", "Popular", "Top Rated", "Upcoming");
        mMovieTypeListView.getSelectionModel().select(0);

        // Begin the movie request to fetch movie data from the internet
        mMovieRequest.beginMovieRequest(MovieDBRequest.MoviesRequestType.NOW_SHOWING);

        // Bind search and clear search button to be enabled if there is some text in the search field
        mSearchButton.disableProperty().bind(mSearchTextField.textProperty().isEmpty());
        mClearSearchButton.disableProperty().bind(mSearchTextField.textProperty().isEmpty());

        // Register for event notifications for changes in the list view selection
        mMovieTypeListView.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> moviesTypeSelectionChanged(oldValue.intValue(), newValue.intValue()));
    }

    // Called when the fetch request for the movie data is completed
    @Override
    public void requestCompleted(ArrayList<MovieInfo> moviesInfo)
    {
        // Build the Movie poster views and add to the flow pane on the main thread
        mMovies = moviesInfo;
        mImagesLoadingProgress = new double[mMovies.size()];
        Platform.runLater(() -> buildMoviesFlowPane(moviesInfo));
    }

    // Called when the request to fetch movie data timed out.
    @Override
    public void requestTimedOut()
    {
        Platform.runLater(() -> showDownloadFailureAlert("Request timed out"));
    }

    // Called when the request fails due to an invalid internet connection
    @Override
    public void requestFailed()
    {
        Platform.runLater(() -> showDownloadFailureAlert("No internet connection"));
    }

    // Displays an alert dialog to alert the user that there was a failure in downloading the movies
    private void showDownloadFailureAlert(String headerText)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Failed to download data");
        alert.setHeaderText(headerText);
        alert.setContentText("There was error downloading the movie data. Please ensure you have an active internet connection.");
        alert.showAndWait();
    }

    // Builds the movie poster flow pane by populating with movie poster views
    private void buildMoviesFlowPane(ArrayList<MovieInfo> movies)
    {
        // Setup progress bar and status label
        mProgressBar.setProgress(0.0);
        mProgressBar.setVisible(true);
        mStatusLabel.setText("Loading Movies");

        // Build a flow pane layout with the width and size of the
        mMoviesFlowPane = new FlowPane(Orientation.HORIZONTAL);
        mMoviesFlowPane.setHgap(4);
        mMoviesFlowPane.setVgap(10);
        mMoviesFlowPane.setPadding(new Insets(10, 8, 4, 8));
        mMoviesFlowPane.prefWrapLengthProperty().bind(mMoviesScrollPane.widthProperty());   // bind to scroll pane width

        for (MovieInfo movie : movies)
        {
            AnchorPane posterPane = buildMoviePosterPane(movie);
            mMoviesFlowPane.getChildren().add(posterPane);
        }

        mMoviesScrollPane.setContent(mMoviesFlowPane);
    }

    // Builds the movie poster pane for the given movie by loading the FXML file
    private AnchorPane buildMoviePosterPane(MovieInfo movie)
    {
        try
        {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MoviesController.class.getResource("MoviePosterView.fxml"));
            AnchorPane posterView = loader.load();
            posterView.setOnMouseClicked((mouseEvent) -> moviePosterClicked(movie));

            // set the movie info
            MoviePosterController controller = loader.getController();
            Image posterImage = new Image(movie.getFullPosterPath(), true);
            posterImage.progressProperty().addListener((observable, oldValue, newValue) -> updateProgressBar(movie, newValue.doubleValue()));

            controller.getMovieTitleLabel().setText(movie.getTitle());
            controller.getPosterImageView().setImage(posterImage);

            return posterView;
        }
        catch (IOException ex){
            System.err.println("Failed to load Movie Poster View in MoviesController");
            ex.printStackTrace();
        }

        return null;
    }

    // Updates the progress bar for the progress for the given movie and its image loading progress
    private void updateProgressBar(MovieInfo movie, double progress)
    {
        // update the progress for that movie in the array
        int index = mMovies.indexOf(movie);
        if (index >= 0){
            mImagesLoadingProgress[index] = progress;
        }

        // collate the total progress of loading the movies
        double currentTotalProgress = 0.0;
        for (double value : mImagesLoadingProgress){
            currentTotalProgress += value;
        }

        //System.out.println("Current total progress: " + currentTotalProgress);
        mProgressBar.setProgress((currentTotalProgress / mMovies.size()));

        if(currentTotalProgress >= mMovies.size()){
            mProgressBar.setVisible(false);
            mStatusLabel.setText("Done Loading Movies");
        }
    }

    // movieTypeSelectionChanged event - Reload the movies for the new movies request type
    private void moviesTypeSelectionChanged(int oldIndex, int newIndex)
    {
        if(newIndex >= 0 && (newIndex != oldIndex)){

            if (mMoviesFlowPane != null) {
                mMoviesFlowPane.getChildren().clear();
            }

            // switch on the selected index and place a request to fetch that type of movie data
            switch (newIndex)
            {
                case 0:
                    mMovieRequest.beginMovieRequest(MovieDBRequest.MoviesRequestType.NOW_SHOWING);
                    break;

                case 1:
                    mMovieRequest.beginMovieRequest(MovieDBRequest.MoviesRequestType.POPULAR);
                    break;

                case 2:
                    mMovieRequest.beginMovieRequest(MovieDBRequest.MoviesRequestType.TOP_RATED);
                    break;

                case 3:
                    mMovieRequest.beginMovieRequest(MovieDBRequest.MoviesRequestType.UPCOMING);
                    break;

                default:
                    break;
            }
        }
    }

    // User clicks on a movie poster
    private void moviePosterClicked(MovieInfo movie)
    {
        // Request main application to set new scene to hand over control to the movie info controller
        mMainApplication.requestTransitionToMovieInfoController(movie);
    }

    // User clicks on the search button
    @FXML private void searchButtonClicked()
    {
        if (!mSearchTextField.getText().isEmpty()) {
            mMovieRequest.beginSearchRequest(mSearchTextField.getText());
        }
    }

    // User clicks the clear button to clear the search query
    @FXML private void clearSearchButtonClicked()
    {
        mSearchTextField.clear();
    }

    // Menu item events
    @FXML public void exitMenuItemClicked()
    {
        System.exit(0);
    }

    @FXML public void aboutMenuItemClicked()
    {
        mMainApplication.requestAboutDialog();
    }
}