package bluej.codecoverage.main;

import java.awt.Frame;
import java.io.File;
import java.io.IOException;

import lombok.Getter;
import bluej.codecoverage.pref.AbstractPreferenceStore;
import bluej.codecoverage.pref.PreferenceManager;
import bluej.codecoverage.pref.PreferenceStore;
import bluej.codecoverage.ui.main.CoverageReportFrame;
import bluej.codecoverage.utils.CoverageUtilities;
import bluej.extensions.BlueJ;
import bluej.extensions.ProjectNotOpenException;

/**
 * Adapter for BlueJ so that it can be used as a CodeCoverageModule.
 * 
 * @author Ian
 * 
 */
@Getter
public class BlueJCodeCoverageModule implements CodeCoverageModule {
   /** BlueJ instance to delegate calls to */
   private BlueJ bluej;
   /** Utilites class that will be used */
   private CoverageUtilities coverageUtilities;
   /** Class to manage all UI or user chosen preferences */
   private PreferenceManager preferenceManager;
   /** Frame to display the coverage information in */
   private CoverageReportFrame reportFrame;
   /**
    * Underlying preference store used by CoverageUtilties and PreferenceManager
    */
   private PreferenceStore preferenceStore;

   /**
    * Constructs a new BlueJCodeCoverageModule and initializes all field
    * variables.
    * 
    * @param bluej
    *           BlueJ instance to delegate calls to.
    * @throws IOException
    *            If an exception is thrown during file I/O
    */
   public BlueJCodeCoverageModule(final BlueJ bluej) throws IOException {
      this.bluej = bluej;
      // create a preference store that wraps bluej
      this.preferenceStore = new AbstractPreferenceStore(
            bluej.getUserConfigDir()) {

         @Override
         public void setPreference(String key, String value) {
            bluej.setExtensionPropertyString(key, value);
         }

         @Override
         public String getPreference(String key, String def) {
            return bluej.getExtensionPropertyString(key, def);
         }
      };
      this.preferenceManager = new PreferenceManager(this);
      this.coverageUtilities = new CoverageUtilities(this);
      this.reportFrame = new CoverageReportFrame(this);

   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Frame getBlueJFrame() {
      return bluej.getCurrentFrame();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public File getCoverageDirectory() {
      try {
         return bluej.getCurrentPackage().getProject().getDir();
      } catch (ProjectNotOpenException e) {
         e.printStackTrace();
         return null;
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CoverageReportFrame getReportFrame() {
      return reportFrame;
   }

}
