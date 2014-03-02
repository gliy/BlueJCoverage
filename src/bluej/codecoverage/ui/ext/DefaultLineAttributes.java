package bluej.codecoverage.ui.ext;

import java.awt.Color;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;

import bluej.codecoverage.pref.CoveragePrefManager;
import bluej.codecoverage.pref.CoveragePrefManager.CurrentPreferences;
import bluej.codecoverage.pref.CoveragePrefManager.PrefKey;
import bluej.codecoverage.utils.serial.CoverageCounterValue;
import bluej.codecoverage.utils.serial.CoverageLine;

public class DefaultLineAttributes implements LineAttributes {
   @Override
   public void setStyle(MutableAttributeSet style, CoverageLine line) {
      CoverageCounterValue value = CoverageCounterValue.from(line.getStatus());
      CurrentPreferences current = CoveragePrefManager.getPrefs().get();

      switch (value) {
      case FULLY_COVERED:
         StyleConstants.setBackground(style,
               (Color) current.getPref(PrefKey.FULLY_COVERED_COLOR));
         break;
      case NOT_COVERED:
         StyleConstants.setBackground(style,
               (Color) current.getPref(PrefKey.NOT_COVERED_COLOR));
         break;
      case EMPTY:
         break;
      case PARTLY_COVERED:
         StyleConstants.setBackground(style,
               (Color) current.getPref(PrefKey.PARTIALLY_COVERED_COLOR));
         break;
      case UNKNOWN:
         break;
      }
   }
}
