package bluej.codecoverage.main;

import java.awt.Frame;
import java.io.File;

import bluej.codecoverage.pref.PreferenceManager;
import bluej.codecoverage.pref.option.PreferenceStore;
import bluej.codecoverage.ui.main.CoverageReportFrame;
import bluej.codecoverage.utils.CoverageUtilities;

public interface CodeCoverageModule {
   public PreferenceStore getPreferenceStore();
   public PreferenceManager getPreferenceManager();
   public Frame getBlueJFrame();
   public CoverageUtilities getCoverageUtilities();
   public File getCoverageDirectory();
   public CoverageReportFrame getReportFrame();
}
