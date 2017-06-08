
/**
 * File: Controller.java
 * Description: Abstract controller class with basic data required for all controllers. This includes the window and contructors.
 * Author: Asad Ahmed
 */

package edu.asad.ahmed.movie.scout;

import javafx.stage.Stage;

public abstract class Controller
{
    protected Stage mWindow;
    protected MainApplication mMainApplication;

    // Sets the window for this controller
    public void setWindow(Stage window)
    {
        mWindow = window;
    }

    // Sets the main application for this controller
    public void setMainApplication(MainApplication mainApplication)
    {
        mMainApplication = mainApplication;
    }
}
