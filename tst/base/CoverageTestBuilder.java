package base;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.analysis.ICoverageNode;
import org.jacoco.core.analysis.ILine;
import org.jacoco.core.analysis.IMethodCoverage;
import org.jacoco.core.analysis.IPackageCoverage;
import org.jacoco.core.analysis.ISourceFileCoverage;
import org.jacoco.core.analysis.ISourceNode;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class CoverageTestBuilder {

   private Validator validator;
   private BValidator bvalidator;

   CoverageTestBuilder() {
      validator = new Validator();
      bvalidator = new BValidator();
   }

   public ICounter counter() {
      ICounter counter = mock(ICounter.class);
      int covered = CoverageTestBase.getRandomInt();
      int missed = CoverageTestBase.getRandomInt();
      int total = CoverageTestBase.getRandomInt();
      double missedRatio = CoverageTestBase.getRandomInt();
      double coveredRatio = CoverageTestBase.getRandomInt();
      when(counter.getCoveredCount()).thenReturn(covered);
      when(counter.getCoveredRatio()).thenReturn(coveredRatio);
      when(counter.getMissedCount()).thenReturn(missed);
      when(counter.getTotalCount()).thenReturn(total);
      when(counter.getMissedRatio()).thenReturn(missedRatio);
      when(counter.getStatus()).thenReturn(ICounter.PARTLY_COVERED);
      return counter;
   }

   private void setupCoverageNode(ICoverageNode node) {
      String name =CoverageTestBase.getRandomString();
      ICounter line = counter();
      ICounter method = counter();
      ICounter clazz = counter();
      ICounter branch = counter();
      when(node.getBranchCounter()).thenReturn(branch);
      when(node.getClassCounter()).thenReturn(clazz);
      when(node.getLineCounter()).thenReturn(line);
      when(node.getMethodCounter()).thenReturn(method);
      when(node.getName()).thenReturn(name);
   }

   // private void setupSourceNode(ILine)
   public IClassCoverage classCoverage() {
      String name = CoverageTestBase.getRandomString();
      return classCoverage(name, name,name + ".java");
   }

   public IClassCoverage classCoverage(String name, String packageName, String sourceFile) {
      // class name must have no periods for lookup to work
      IClassCoverage clz = mock(IClassCoverage.class);
      String interfaceName = CoverageTestBase.getRandomString();
      String superName = CoverageTestBase.getRandomString();
      setupSourceNode(clz);
      ArrayList<IMethodCoverage> methods = new ArrayList<IMethodCoverage>();
      for (int i = 0; i < 5; i++) {
         methods.add(methodCoverage());
      }

      when(clz.getMethods()).thenReturn(methods);

      when(clz.getInterfaceNames()).thenReturn(
            new String[] { interfaceName });

      when(clz.getSuperName()).thenReturn(superName);
      when(clz.getPackageName()).thenReturn(packageName);
      
      String fileType = sourceFile.endsWith(".java") ? "" :  ".java";
      when(clz.getSourceFileName()).thenReturn(sourceFile + fileType);
      when(clz.getName()).thenReturn(name);

      validator.add(clz);
      return clz;
   }

   public IPackageCoverage packageCoverage(int numClasses,
         int numInnerClasses) {
      List<ISourceFileCoverage> sourceClasses = new ArrayList<ISourceFileCoverage>();
      List<IClassCoverage> allClasses = new ArrayList<IClassCoverage>();
      for (int i = 0; i < numClasses; i++) {
         String name = CoverageTestBase.getRandomString();
         String pkg = CoverageTestBase.getRandomString();
         IClassCoverage clz = classCoverage(name, pkg,name);
         allClasses.add(clz);
         sourceClasses.add(sourceFile(name, pkg));
         if (i < numInnerClasses) {
            allClasses.add(classCoverage(CoverageTestBase.getRandomString(),clz.getPackageName(),
                  clz.getSourceFileName()));
         }

      }
      return packageCoverage(sourceClasses, allClasses);
   }
   public IPackageCoverage packageCoverage(List<ISourceFileCoverage> sourceFileCoverage, List<IClassCoverage> classCoverage) {
     

      IPackageCoverage pkg = mock(IPackageCoverage.class);
      setupCoverageNode(pkg);
      String name = CoverageTestBase.getRandomString();
      when(pkg.getName()).thenReturn(name);
      when(pkg.getClasses()).thenReturn(classCoverage);
      when(pkg.getSourceFiles()).thenReturn(sourceFileCoverage);
      validator.add(pkg);
      return pkg;
   }

   public IMethodCoverage methodCoverage() {
      IMethodCoverage method = mock(IMethodCoverage.class);
      setupSourceNode(method);
      validator.add(method);
      return method;
   }

   public Validator validator() {
      return validator;
   }
   public BValidator bValidator() {
      return bvalidator;
   }
   public ILine line() {
      ICounter branch = counter();
      ILine line = mock(ILine.class);
      when(line.getBranchCounter()).thenReturn(branch);
      when(line.getStatus()).thenReturn(ICounter.FULLY_COVERED);
      return line;
   }

   public ISourceFileCoverage sourceFile(String name, String packageName) {

      ISourceFileCoverage sourceFile = mock(ISourceFileCoverage.class);
      setupSourceNode(sourceFile);

      
      when(sourceFile.getPackageName()).thenReturn(packageName);
      when(sourceFile.getName()).thenReturn(name + ".java");

      
      return sourceFile;
   }

   private void setupSourceNode(ISourceNode node) {
      int firstLine = 1;
      int lastLine = 50;
      final ILine[] lines = new ILine[lastLine + firstLine];
      for (int lineNum = firstLine; lineNum <= lastLine; lineNum++) {
         lines[lineNum] = line();
      }
      when(node.getLine(Mockito.anyInt())).then(new Answer<ILine>() {

         @Override
         public ILine answer(InvocationOnMock invocation) throws Throwable {
            return lines[(Integer) invocation.getArguments()[0]];
         }
      });
      when(node.getFirstLine()).thenReturn(firstLine);
      when(node.getLastLine()).thenReturn(lastLine);
      setupCoverageNode(node);
   }

}