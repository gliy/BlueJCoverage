package bluej.codecoverage.main;

import java.awt.Frame;
import java.io.File;

import bluej.codecoverage.pref.PreferenceManager;
import bluej.codecoverage.pref.PreferenceStore;
import bluej.codecoverage.ui.main.CoverageReportFrame;
import bluej.codecoverage.utils.CoverageUtilities;

/**
 * Represents the information controller for any data needed from an BlueJ.
 * <p>
 * Instances of this interface should be singleton, and only 1 should be used
 * during a plugin's lifetime.
 * 
 * @see BlueJCodeCoverageModule
 * @author Ian
 * 
 */
public interface CodeCoverageModule {
   /**
    * Preference wrapper that allows for saving and load preferences.
    * 
    * @return the current extension's preference storage.
    */
   public PreferenceStore getPreferenceStore();

   /**
    * Manager for all specific preferences, like what the line colors for
    * coverage should be.
    * 
    * @return the current preference manager.
    */
   public PreferenceManager getPreferenceManager();

   /**
    * The active BlueJ Frame.
    * <p>
    * This is used for centering dialogs and opening windows in the proper
    * location.
    * 
    * @return the currently active BlueJ frame.
    */
   public Frame getBlueJFrame();

   /**
    * Utilities used by the extensions.
    * <p>
    * All management of the Jacoco agent occurs in this class, including
    * starting the server, starting coverage sessions, and ending coverage
    * sessions.
    * 
    * @return the current utilities instance.
    */
   public CoverageUtilities getCoverageUtilities();

   /**
    * Directory that the coverage agent should scan when looking for source
    * files.
    * 
    * @return base directory for coverage source files.
    */
   public File getCoverageDirectory();

   /**
    * The current coverage JFrame.
    * 
    * @return coverage JFrame.
    */
   public CoverageReportFrame getReportFrame();
}
