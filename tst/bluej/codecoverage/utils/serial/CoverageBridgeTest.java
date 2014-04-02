package bluej.codecoverage.utils.serial;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.IMethodCoverage;
import org.jacoco.core.analysis.IPackageCoverage;

import junit.framework.TestCase;

public class CoverageBridgeTest extends CoverageTestCase {

   private TestBuilder tester;

   @Override
   protected void setUp() throws Exception {
      tester = create();
   }

   public void testToClass() throws Exception {
      IClassCoverage expected = tester.classCoverage();
      CoverageClass actual = CoverageBridge.toClass(expected);
      
      tester.validator().validate(actual);
   }

   public void testfillBase() throws Exception {

   }

   public void testToMethod() throws Exception {
      IMethodCoverage from = tester.methodCoverage();
      CoverageMethod result = CoverageBridge.toMethods(Arrays.asList(from)).iterator().next();
      tester.validator().validate(result);
   }
   public void testFindName() throws Exception {
      String expected= "classname";
      String name = "packagename."+expected;
      
      CoverageClass mockClass = mock(CoverageClass.class);
      when(mockClass.getName()).thenReturn(name);
      when(mockClass.getInterfaces()).thenReturn(new String[0]);
      assertEquals(expected, CoverageBridge.findName(mockClass));
      
      when(mockClass.getName()).thenReturn(expected);
      assertEquals(expected, CoverageBridge.findName(mockClass));
   }
   public void testBasicToSerializable() throws Exception {
      IPackageCoverage expected = tester.packageCoverage(2, 0);
      CoveragePackage actual = CoverageBridge.toSerializable(expected);
      tester.validator().validate(actual);
   }

}
