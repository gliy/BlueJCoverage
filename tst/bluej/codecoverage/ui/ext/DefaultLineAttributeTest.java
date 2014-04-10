package bluej.codecoverage.ui.ext;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.Color;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;

import org.mockito.Mockito;

import bluej.codecoverage.pref.option.ColorPreferences;
import bluej.codecoverage.utils.serial.CoverageCounter;
import bluej.codecoverage.utils.serial.CoverageCounterValue;
import bluej.codecoverage.utils.serial.CoverageLine;
import junit.framework.TestCase;

public class DefaultLineAttributeTest extends TestCase {

   private ColorPreferences prefs;

   @Override
   protected void setUp() throws Exception {
      prefs = mock(ColorPreferences.class);
      when(prefs.getFullyCovered()).thenReturn(Color.GREEN);
      when(prefs.getNotCovered()).thenReturn(Color.RED);
      when(prefs.getPartiallyCovered()).thenReturn(Color.YELLOW);
   }

   public void testPartial() throws Exception {
      MutableAttributeSet attr = mock(MutableAttributeSet.class);

      DefaultLineAttributes tip = new DefaultLineAttributes(prefs);

      CoverageCounter partial = new CoverageCounter(10, 10, 20,
               CoverageCounterValue.PARTLY_COVERED.getIntValue(), 0, 0);
      tip.setStyle(
               attr,
               new CoverageLine(CoverageCounterValue.PARTLY_COVERED
                        .getIntValue(), partial));

      verify(attr, Mockito.only()).addAttribute(StyleConstants.Background,
               Color.YELLOW);
   }

   public void testFull() throws Exception {
      MutableAttributeSet attr = mock(MutableAttributeSet.class);

      DefaultLineAttributes tip = new DefaultLineAttributes(prefs);

      CoverageCounter partial = new CoverageCounter(10, 10, 20,
               CoverageCounterValue.FULLY_COVERED.getIntValue(), 0, 0);
      tip.setStyle(
               attr,
               new CoverageLine(CoverageCounterValue.FULLY_COVERED
                        .getIntValue(), partial));

      verify(attr, Mockito.only()).addAttribute(StyleConstants.Background,
               Color.GREEN);
   }

   public void testEmpty() throws Exception {
      MutableAttributeSet attr = mock(MutableAttributeSet.class);

      DefaultLineAttributes tip = new DefaultLineAttributes(prefs);

      CoverageCounter empty = new CoverageCounter(10, 10, 20,
               CoverageCounterValue.NOT_COVERED.getIntValue(), 0, 0);
      tip.setStyle(attr,
               new CoverageLine(CoverageCounterValue.NOT_COVERED.getIntValue(),
                        empty));

      verify(attr, Mockito.only()).addAttribute(StyleConstants.Background,
               Color.RED);
   }
}
