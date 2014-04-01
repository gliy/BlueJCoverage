package bluej.codecoverage.utils.serial;

import java.util.Arrays;

import org.jacoco.core.analysis.IMethodCoverage;

import junit.framework.TestCase;

public class CoverageBridgeTest extends CoverageTestCase {

   private TestBuilder tester;

   @Override
   protected void setUp() throws Exception {
      tester = create();
   }

   public void testToClass() throws Exception {

   }

   public void testfillBase() throws Exception {

   }

   public void testToMethod() throws Exception {
      IMethodCoverage from = tester.methodCoverage();
      CoverageMethod result = CoverageBridge.toMethods(Arrays.asList(from)).iterator().next();
      tester.validator().validate(result);
   }

}
