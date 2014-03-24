package bluej.codecoverage.ui.ext;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;

import bluej.codecoverage.pref.option.ColorPreferences;
import bluej.codecoverage.utils.serial.CoverageCounterValue;
import bluej.codecoverage.utils.serial.CoverageLine;

public class DefaultLineAttributes implements LineAttributes {
   private ColorPreferences colors;
   
   public DefaultLineAttributes(ColorPreferences colors) {
      super();
      this.colors = colors;
   }

   @Override
   public void setStyle(MutableAttributeSet style, CoverageLine line) {
      CoverageCounterValue value = CoverageCounterValue.from(line.getStatus());

      switch (value) {
      case FULLY_COVERED:
         StyleConstants.setBackground(style,
               colors.getFullyCovered());
         break;
      case NOT_COVERED:
         StyleConstants.setBackground(style,
              colors.getNotCovered());
         break;
  
      case PARTLY_COVERED:
         StyleConstants.setBackground(style,colors.getPartiallyCovered());
         break;
      case EMPTY:
      case UNKNOWN:
         break;
      }
   }
}
