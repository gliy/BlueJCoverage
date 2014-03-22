package bluej.codecoverage.utils.serial;

import javax.swing.ImageIcon;

import bluej.codecoverage.pref.PreferenceManager.CurrentPreferences;

/**
 * Joins Coverage information for a branch with icons to indicate coverage.
 * <p>
 * The icons used are the red,yellow,and green diamonds seen on a typically code
 * coverage report.
 * 
 * @author Ian
 * 
 */
public enum CoverageBranch {
   BRANCH_FC, BRANCH_NC, BRANCH_PC, NONE;

   private ImageIcon img;

   private CoverageBranch() {
      this.img = CurrentPreferences.getImage(this, "gif");
   }

   public ImageIcon getImg() {
      return img;
   }

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
