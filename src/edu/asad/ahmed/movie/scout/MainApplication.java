package edu.asad.ahmed.movie.scout;

/**
 * MainApplication.java
 * The main application class for handling application wide tasks and services.
 * Author: Asad Ahmed
 */

import edu.asad.ahmed.movie.scout.model.MovieInfo;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.StageStyle;

import java.io.IOException;

public class MainApplication extends Application
{
    private Stage mWindow;
    private Scene mMoviesScene;
    private BorderPane mRootLayout;

    // Start JavaFx application entry point
    public static void main(String[] args)
    {
        Application.launch(args);
    }

    // Called to start the javaFx application
    public void start(Stage primaryStage)
    {
        mWindow = primaryStage;

        // Setup the main root layout
        initRootLayout();

        // Display window and the root scene
        mMoviesScene = new Scene(mRootLayout);
        mWindow.setTitle("Movie Scout");
        mWindow.setScene(mMoviesScene);
        mWindow.show();
    }

    // Initialises the main root layout node and sets the scene for the window
    private void initRootLayout()
    {
        try
        {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApplication.class.getResource("MainView.fxml"));
            mRootLayout = loader.load();

            // setup the controller's window and reference to this main application class
            MoviesController controller = loader.getController();
            controller.setMainApplication(this);
            controller.setWindow(mWindow);
        }
        catch (IOException ex)
        {
            System.err.println("Unable to init root layout from Main Application");
            ex.printStackTrace();
        }
    }

    // Request control to be passed to the movie info controller to view movie info
    public void requestTransitionToMovieInfoController(MovieInfo movie)
    {
        try
        {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApplication.class.getResource("MovieInfoView.fxml"));
            Pane layout = loader.load();

            // setup controller
            MovieInfoController controller = loader.getController();
            controller.setWindow(mWindow);
            controller.setMainApplication(this);
            controller.setMovie(movie);

            mWindow.setScene(new Scene(layout));
        }
        catch (IOException ex)
        {
            System.err.println("Unable to load movie info layout from fxml file in main application.");
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    // Request control to be passed back to the movies controller
    public void requestTransitionBackToMoviesController()
    {
        mWindow.setScene(mMoviesScene);
    }

    // Request for about dialog to be displayed
    public void requestAboutDialog()
    {
        try
        {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApplication.class.getResource("AboutView.fxml"));
            Pane pane = loader.load();

            Scene scene = new Scene(pane);
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setScene(scene);
            stage.setTitle("Movie Scout");

            stage.showAndWait();
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }
}