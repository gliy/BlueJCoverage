package base;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.Color;
import java.awt.Frame;
import java.io.File;

import bluej.codecoverage.main.CodeCoverageModule;
import bluej.codecoverage.pref.PreferenceManager;
import bluej.codecoverage.pref.PreferenceStore;
import bluej.codecoverage.pref.option.ColorPreferences;
import bluej.codecoverage.ui.ext.LineAttributes;
import bluej.codecoverage.ui.main.CoverageReportFrame;
import bluej.codecoverage.utils.CoverageUtilities;

public class MockCodeCoverageModule implements CodeCoverageModule {
   private PreferenceStore preferenceStore;
   private PreferenceManager preferenceManager;
   private Frame blueJFrame;
   private CoverageUtilities coverageUtilities;
   private File coverageDirectory;
   private CoverageReportFrame reportFrame;
   
   public MockCodeCoverageModule() {
      this.preferenceStore = mock(PreferenceStore.class);
      this.preferenceManager = new PreferenceManager(this);
      this.blueJFrame = mock(Frame.class);
      this.coverageUtilities = mock(CoverageUtilities.class);
      this.coverageDirectory = new File("");
      this.reportFrame = mock(CoverageReportFrame.class);
   }
   @Override
   public PreferenceStore getPreferenceStore() {
      return preferenceStore;
   }

   @Override
   public PreferenceManager getPreferenceManager() {
      return preferenceManager;
   }

   @Override
   public Frame getBlueJFrame() {
      return blueJFrame;
   }

   @Override
   public CoverageUtilities getCoverageUtilities() {
      return coverageUtilities;
   }

   @Override
   public File getCoverageDirectory() {
      return coverageDirectory;
   }

   @Override
   public CoverageReportFrame getReportFrame() {
      return reportFrame;
   }
   
   public void setupManager() {
     // ColorPreferences colorPrefs = mock(ColorPreferences.class);
     // when(preferenceManager.getColorPrefs()).thenReturn(colorPrefs);
     // when(colorPrefs.getFullyCovered()).thenReturn(Color.GREEN);
     // when(colorPrefs.getNotCovered()).thenReturn(Color.red);
     // when(colorPrefs.getPartiallyCovered()).thenReturn(Color.YELLOW);
      
   }

}
