package bluej.codecoverage;

import java.awt.Frame;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.List;

import javax.swing.JFrame;

import bluej.codecoverage.main.CodeCoverageModule;
import bluej.codecoverage.ui.main.CoverageReportFrame;
import bluej.codecoverage.utils.CoverageUtilities;
import bluej.codecoverage.utils.join.BCoverageBridge;
import bluej.codecoverage.utils.join.BCoveragePackage;
import bluej.codecoverage.utils.join.CoverageBundleManager.CoverageBundle;
import bluej.codecoverage.utils.serial.CoveragePackage;
import bluej.extensions.BlueJ;

/**
 * Starts and Ends a Coverage Sessions triggered by button presses.
 * <p>
 * Only a single instance of this class may exist at one time.
 * 
 * @author Ian
 * 
 */
public class CoverageAction implements ItemListener {

   private CodeCoverageModule module;

   /**
    * Creates a new instance of the class.
    * 
    * @param module
    *           the module to use when loading preferences, or determining
    *           window location
    */
   public CoverageAction(CodeCoverageModule module) {
      super();
      this.module = module;

   }

   private void endCoverage() {
      try {
         Frame location = module.getBlueJFrame();
         File dir = module.getCoverageDirectory();
         List<CoveragePackage> coverage = module.getCoverageUtilities()
               .getResults(dir);
         if (coverage != null) {
            List<BCoveragePackage> bcoverage = BCoverageBridge.toBCoverage(
                  coverage, dir);
            CoverageReportFrame report = module.getReportFrame();
            CoverageBundle bundle = module.getBundleManager().createBundle(bcoverage);
            report.create(bundle, location);
            report.setVisible(true);
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

   }

   /**
    * Reset the previous coverage information to start over from scratch.
    */
   private void startCoverage() {

      module.getCoverageUtilities().clearResults();
   }

   @Override
   public void itemStateChanged(ItemEvent e) {
      boolean selected = e.getStateChange() == ItemEvent.SELECTED;
      if (selected) {
         startCoverage();
      } else {
         endCoverage();
      }

   }

}
