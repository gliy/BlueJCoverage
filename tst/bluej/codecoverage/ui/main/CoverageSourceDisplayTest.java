package bluej.codecoverage.ui.main;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.IPackageCoverage;
import org.jacoco.core.analysis.ISourceFileCoverage;
import org.mockito.Mockito;

import base.CoverageTestBase;
import base.CoverageTestBuilder;
import base.MockCodeCoverageModule;
import bluej.codecoverage.main.CodeCoverageModule;
import bluej.codecoverage.utils.join.BCoverage;
import bluej.codecoverage.utils.join.BCoverageBridge;
import bluej.codecoverage.utils.join.BCoverageClass;
import bluej.codecoverage.utils.join.BCoveragePackage;
import bluej.codecoverage.utils.serial.CoverageBridge;
import bluej.codecoverage.utils.serial.CoverageClass;
import bluej.codecoverage.utils.serial.CoveragePackage;

public class CoverageSourceDisplayTest extends CoverageTestBase {
   private CoverageTestBuilder tester;
   private MockCodeCoverageModule module;
   @Override
   protected void setUp() throws Exception {
      super.setUp();
      tester = create();
      module = new MockCodeCoverageModule();
      module.setupManager();
   }

   public void testCreateStyleMap() throws Exception {
     
   }
   
   private BCoverageClass getCoverageClass() throws Exception{
      
      File found = new File("found.java");
      found.createNewFile();
      found.deleteOnExit();
      
      List<IClassCoverage> classes = Arrays.asList(
               tester.classCoverage("found", "", "found"),
               tester.classCoverage("notfound", "", "notfound"));
      List<ISourceFileCoverage> sourceClasses = Arrays.asList(
               tester.sourceFile("found", ""),
               tester.sourceFile("notfound", ""));

      IPackageCoverage pkg = tester.packageCoverage(sourceClasses, classes);
      CoveragePackage coveragePkg = CoverageBridge.toSerializable(pkg);

      BCoveragePackage result = BCoverageBridge.toBCoverage(
               Arrays.asList(coveragePkg),
               found.getAbsoluteFile().getParentFile()).get(0);
      //end
      BCoverageClass clz = (BCoverageClass) result.getNodes().get(0);
      return clz;
   }

}
