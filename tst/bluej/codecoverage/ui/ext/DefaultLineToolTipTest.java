package bluej.codecoverage.ui.ext;

import bluej.codecoverage.utils.serial.CoverageCounter;
import bluej.codecoverage.utils.serial.CoverageCounterValue;
import bluej.codecoverage.utils.serial.CoverageLine;
import junit.framework.TestCase;

public class DefaultLineToolTipTest extends TestCase {

   public void testSuccess() throws Exception {
      DefaultLineToolTip tip = new DefaultLineToolTip();
      CoverageCounter partial =    new CoverageCounter(10, 10, 20,
               CoverageCounterValue.PARTLY_COVERED.getIntValue(), 0, 0);
      CoverageCounter empty = new CoverageCounter(0, 0, 0, 0, 0, 0);
      CoverageCounter full = new CoverageCounter(10, 0, 10, CoverageCounterValue.FULLY_COVERED.getIntValue(), 0, 0);
      tip.registerLine(1, new CoverageLine(CoverageCounterValue.PARTLY_COVERED.getIntValue(),
            partial));
      tip.registerLine(2, new CoverageLine(CoverageCounterValue.EMPTY.getIntValue(), empty));
      tip.registerLine(3, new CoverageLine(CoverageCounterValue.FULLY_COVERED.getIntValue(), full));
      
      assertNull(tip.getToolTip(4));
      assertNull(tip.getToolTip(2));
      assertEquals("All 10 covered", tip.getToolTip(3));
      assertEquals("10/20 branches missed", tip.getToolTip(1));
     
      
  }
}
