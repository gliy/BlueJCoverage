package bluej.codecoverage.utils.serial;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Random;

import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.analysis.ICoverageNode;
import org.jacoco.core.analysis.ISourceNode;

import junit.framework.TestCase;

public class CoverageTestCase extends TestCase {
   private static final Random RANDOM = new Random();
  
   
   protected static int getRandomInt() {
      return RANDOM.nextInt(5000);
   }
   
   public static class TestBuilder{
      private TestBuilder() {
         
      }
      public ICounter counter() {
         ICounter counter = mock(ICounter.class);
         int covered = getRandomInt();
         int missed = getRandomInt();
         int total = getRandomInt();
         double missedRatio = getRandomInt();
         double coveredRatio = getRandomInt();
         when(counter.getCoveredCount()).thenReturn(covered);
         when(counter.getCoveredRatio()).thenReturn(coveredRatio);
         when(counter.getMissedCount()).thenReturn(missed);
         when(counter.getTotalCount()).thenReturn(total);
         when(counter.getMissedRatio()).thenReturn(missedRatio);
         when(counter.getStatus()).thenReturn(ICounter.PARTLY_COVERED);
         return counter;
      }
   }
}
