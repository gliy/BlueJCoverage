package bluej.codecoverage.utils.join;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.IPackageCoverage;
import org.jacoco.core.analysis.ISourceFileCoverage;

import base.CoverageTestBase;
import base.CoverageTestBuilder;
import bluej.codecoverage.utils.serial.CoverageBridge;
import bluej.codecoverage.utils.serial.CoverageClass;
import bluej.codecoverage.utils.serial.CoveragePackage;

public class BCoverageBridgeTest extends CoverageTestBase {
   private CoverageTestBuilder tester;
   @Override
   protected void setUp() throws Exception {
      tester = create();
      super.setUp();
   }
   public void testSuccess() throws Exception {
      
      File found = new File("found.java");
      found.createNewFile();
      found.deleteOnExit();
      
      List<IClassCoverage> classes = Arrays.asList(tester.classCoverage("found", "", "found"), tester.classCoverage("notfound","","notfound"));
      List<ISourceFileCoverage> sourceClasses = Arrays.asList(tester.sourceFile("found", ""), tester.sourceFile("notfound", ""));
      
      IPackageCoverage pkg = tester.packageCoverage(sourceClasses, classes);
      CoveragePackage coveragePkg = CoverageBridge.toSerializable(pkg);
      
      CoverageClass expected = null;
      Iterator<CoverageClass> coverageIter = coveragePkg.getClassCoverageInfo().iterator();
      while(expected == null && coverageIter.hasNext()) {
         CoverageClass next = coverageIter.next();
         if(next.getName().startsWith("found")) {
            expected = next;
         }
      }
      
      BCoveragePackage result = BCoverageBridge.toBCoverage(Arrays.asList(coveragePkg), found.getAbsoluteFile().getParentFile()).get(0);
      assertEquals(1, result.getNodes().size());
      BCoverage<CoverageClass> actual = result.getNodes().get(0);
      
      tester.bValidator().validate(actual);
   }
}
