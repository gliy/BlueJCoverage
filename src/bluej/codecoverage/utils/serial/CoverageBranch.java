package bluej.codecoverage.utils.serial;

import javax.swing.ImageIcon;

import lombok.Getter;
import bluej.codecoverage.pref.PreferenceManager;

/**
 * Joins Coverage information for a branch with icons to indicate coverage.
 * <p>
 * The icons used are the red,yellow,and green diamonds seen on a typically code
 * coverage report.
 * 
 * @author Ian
 * 
 */
@Getter
public enum CoverageBranch {
   BRANCH_FC, BRANCH_NC, BRANCH_PC, NONE;
   /** Icon that represents the branch coverage. */
   private ImageIcon img;

   private CoverageBranch() {
      this.img = PreferenceManager.getImage(this, "gif");
   }

   /**
    * Determines the type of Branch Coverage for a counter.
    * <p>
    * If there were no branches then NONE is returned, otherwise, it compares
    * the number of missed branches to the number of total branches, to
    * determine if it was fully covered, partially covered, or not covered.
    * 
    * @param counter the counter to determine branch coverage for
    * @return An enum representing the counter's branch coverage
    */
   public static CoverageBranch fromCounter(CoverageCounter counter) {
      if (counter.getTotal() == 0) {
         return NONE;
      }
      if (counter.getMissed() == 0) {
         return BRANCH_FC;
      } else if (counter.getMissed() == counter.getTotal()) {
         return BRANCH_NC;
      } else {
         return BRANCH_PC;
      }

   }

}
