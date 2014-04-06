package base;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.analysis.ICoverageNode;
import org.jacoco.core.analysis.ILine;
import org.jacoco.core.analysis.IMethodCoverage;
import org.jacoco.core.analysis.IPackageCoverage;

import bluej.codecoverage.utils.serial.Coverage;
import bluej.codecoverage.utils.serial.CoverageClass;
import bluej.codecoverage.utils.serial.CoverageCounter;
import bluej.codecoverage.utils.serial.CoverageLine;
import bluej.codecoverage.utils.serial.CoverageMethod;
import bluej.codecoverage.utils.serial.CoveragePackage;

public class Validator {
   private Map<String, IMethodCoverage> nameToMethod = new HashMap<String, IMethodCoverage>();
   private Map<String, IClassCoverage> nameToClass = new HashMap<String, IClassCoverage>();
   private Map<String, IPackageCoverage> nameToPackage = new HashMap<String, IPackageCoverage>();

   public void add(IMethodCoverage method) {
      nameToMethod.put(method.getName(), method);
   }

   public void add(IPackageCoverage pkg) {
      nameToPackage.put(pkg.getName(), pkg);
   }

   public void add(IClassCoverage clz) {
      nameToClass.put(clz.getName(), clz);
   }

   public void validate(CoverageMethod... methods) {
      for (CoverageMethod method : methods) {
         IMethodCoverage expected = nameToMethod.get(method.getName());
         assertCoverageNode(expected, method);
         CoverageTestBase.assertEquals(expected.getFirstLine(), method.getFirstLine());
         CoverageTestBase.assertEquals(expected.getLastLine(), method.getLastLine());

      }
   }

   public void validate(CoverageClass... classes) {
      for (CoverageClass actual : classes) {
         IClassCoverage expected = nameToClass.get(actual.getName());
         assertCoverageNode(expected, actual);
         assertEquals(expected.getPackageName(), actual.getPackageName());
         assertEquals(expected.getSourceFileName(),
               actual.getSourceFileName());
         assertTrue(Arrays.equals(expected.getInterfaceNames(),
               actual.getInterfaces()));
         assertEquals(expected.getSuperName(), actual.getSuperClass());
         assertEquals(expected.getFirstLine(), actual.getFirstLine());
         assertEquals(expected.getLastLine(), actual.getLastLine());
         assertEquals(expected.getLineCounter().getStatus(), actual.getLineCoverage().getStatus());
         for (CoverageMethod method : actual.getMethodCounter()) {
            validate(method);
         }
         for (CoverageClass nestedClass : actual.getClassCounter()) {
            validate(nestedClass);
         }
         for (int i = expected.getFirstLine(); i < expected.getLastLine(); i++) {
            int ourLine = i - expected.getFirstLine();
            ILine expectedLine = expected.getLine(i);
            assertLine(expectedLine, actual.getLine(ourLine));
         }
      }

   }

   public void validate(CoveragePackage... packages) {
      for (CoveragePackage actual : packages) {
         IPackageCoverage expected = nameToPackage.get(actual.getName());
         assertCoverageNode(expected, actual);
       //  validate(actual.getClassCoverageInfo()
       //        .toArray(new CoverageClass[0]));
      }

   }


   private void assertLine(ILine expected, CoverageLine actual) {
      assertCounter(expected.getBranchCounter(), actual.getBranchCounter());
      CoverageTestBase.assertEquals(expected.getStatus(), actual.getStatus());
   }

   private void assertCoverageNode(ICoverageNode expected, Coverage actual) {
      CoverageTestBase.assertNotNull(expected);
      assertCounter(expected.getBranchCounter(), actual.getBranchCoverage());
      assertCounter(expected.getClassCounter(), actual.getClassCoverage());
      assertCounter(expected.getLineCounter(), actual.getLineCoverage());
      assertCounter(expected.getMethodCounter(), actual.getMethodCoverage());
   }

   private void assertCounter(ICounter expected, CoverageCounter actual) {
      CoverageTestBase.assertEquals(expected.getCoveredCount(), actual.getCovered());
      CoverageTestBase.assertEquals(expected.getCoveredRatio(), actual.getCoveredRatio());
      CoverageTestBase.assertEquals(expected.getMissedCount(), actual.getMissed());
      CoverageTestBase.assertEquals(expected.getMissedRatio(), actual.getMissedRatio());
      CoverageTestBase.assertEquals(expected.getTotalCount(), actual.getTotal());
      CoverageTestBase.assertEquals(expected.getStatus(), actual.getStatus());
   }
}