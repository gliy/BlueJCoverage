package bluej.codecoverage.main;

import java.awt.Frame;
import java.io.File;
import java.io.IOException;

import bluej.codecoverage.pref.AbstractPreferenceStore;
import bluej.codecoverage.pref.PreferenceManager;
import bluej.codecoverage.pref.PreferenceStore;
import bluej.codecoverage.ui.main.CoverageReportFrame;
import bluej.codecoverage.utils.CoverageUtilities;
import bluej.extensions.BlueJ;
import bluej.extensions.ProjectNotOpenException;

/**
 * Adapter for BlueJ so that it can be used as a CodeCoverageModule.
 * <p>
 *  
 * @author Ian
 *
 */
public class BlueJCodeCoverageModule implements CodeCoverageModule {

   private BlueJ bluej;
   private CoverageUtilities utils;
   private PreferenceManager prefManager;
   private CoverageReportFrame reportFrame;
   private PreferenceStore prefStore;

   public BlueJCodeCoverageModule(final BlueJ bluej) throws IOException {
      this.bluej = bluej;
    
      this.prefStore = new AbstractPreferenceStore(bluej.getUserConfigDir()) {

         @Override
         public void setPreference(String key, String value) {
            bluej.setExtensionPropertyString(key, value);
         }

         @Override
         public String getPreference(String key, String def) {
            return bluej.getExtensionPropertyString(key, def);
         }
      };
      this.prefManager = PreferenceManager.init(this);
      this.utils = CoverageUtilities.create(this);
     
      this.reportFrame = new CoverageReportFrame(this);
     
   }

   @Override
   public PreferenceStore getPreferenceStore() {
      return prefStore;
   }

   @Override
   public PreferenceManager getPreferenceManager() {
      return prefManager;
   }

   @Override
   public Frame getBlueJFrame() {
      return bluej.getCurrentFrame();
   }

   @Override
   public CoverageUtilities getCoverageUtilities() {
      return utils;
   }

   @Override
   public File getCoverageDirectory() {
      try {
         return bluej.getCurrentPackage().getProject().getDir();
      } catch (ProjectNotOpenException e) {
         e.printStackTrace();
         return null;
      }
   }

   @Override
   public CoverageReportFrame getReportFrame() {
      return reportFrame;
   }

}
