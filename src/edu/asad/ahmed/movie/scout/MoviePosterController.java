package edu.asad.ahmed.movie.scout;

/**
 * File: MoviePosterController.java
 * Description: Simple controller for setting and getting the controls in the Movie Poster View
 * Author: Asad Ahmed
 */

import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.fxml.FXML;

public class MoviePosterController
{
    @FXML
    private Label mMovieTitleLabel;

    @FXML
    private ImageView mPosterImageView;

    public Label getMovieTitleLabel() {
        return mMovieTitleLabel;
    }

    public void setMovieTitleLabel(Label mMovieTitleLabel) {
        mMovieTitleLabel = mMovieTitleLabel;
    }

    public ImageView getPosterImageView() {
        return mPosterImageView;
    }

    public void setPosterImageView(ImageView mPosterImageView) {
        mPosterImageView = mPosterImageView;
    }
}