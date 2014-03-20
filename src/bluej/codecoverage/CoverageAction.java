package bluej.codecoverage;

import java.awt.Frame;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.List;

import javax.swing.JFrame;

import bluej.codecoverage.ui.CoverageReportFrame;
import bluej.codecoverage.utils.CoverageUtilities;
import bluej.codecoverage.utils.join.BCoverageBridge;
import bluej.codecoverage.utils.join.BCoveragePackage;
import bluej.codecoverage.utils.serial.CoveragePackage;
import bluej.extensions.BlueJ;

/**
 * Starts and Ends a Coverage Sessions trigged by button presses.
 * <p>
 * Only a single instance of this class may exist at one time.
 * 
 * @author Ian
 * 
 */
public class CoverageAction implements ItemListener {

   private BlueJ bluej;
   private static CoverageAction action;

   private CoverageAction(BlueJ bluej) {
      super();
      this.bluej = bluej;

   }

   private void endCoverage() {
      try {
         Frame location = bluej.getCurrentFrame();
         File dir = bluej.getCurrentPackage().getProject().getDir();
         List<CoveragePackage> coverage = CoverageUtilities.get().getResults(
                  dir);
         if (coverage != null) {
            List<BCoveragePackage> bcoverage = BCoverageBridge.toBCoverage(
                     coverage, dir);
            JFrame report = CoverageReportFrame.create(bcoverage, location);
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
      CoverageUtilities utils = CoverageUtilities.get();
      utils.clearResults();
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

   /**
    * Creates a single instance of this class.
    * TODO:add a null extension.
    * @param bluej
    *           the BlueJ instance to use when load preferences, or determining
    *           how to layout information.
    */
   public static void init(BlueJ bluej) {
      action = new CoverageAction(bluej);
   }

   /**
    * Returns this class as a Listener that can be attached to buttons.
    * 
    * @return this class as an {@link ItemListener}
    */
   public static ItemListener get() {
      return action;
   }

}
