package bluej.codecoverage.utils.serial;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import junit.framework.TestCase;

import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.analysis.ICoverageNode;
import org.jacoco.core.analysis.ILine;
import org.jacoco.core.analysis.IMethodCoverage;
import org.jacoco.core.analysis.IPackageCoverage;
import org.jacoco.core.analysis.ISourceFileCoverage;
import org.jacoco.core.analysis.ISourceNode;
import org.jacoco.core.internal.analysis.SourceFileCoverageImpl;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class CoverageTestCase extends TestCase {
   private static final Random RANDOM = new Random();

   protected static int getRandomInt() {
      return RANDOM.nextInt(5000);
   }

   protected static String getRandomString() {
      StringBuilder rngString = new StringBuilder();
      Random rnd = new Random();
      for(int i = 0; i < 14; i++) {
         rngString.append((char)('a' + rnd.nextInt(25)));
      }
      return rngString.toString();
   }

   protected static TestBuilder create() {
      return new TestBuilder();
   }

   public static class TestBuilder {

      private Validator validator;

      private TestBuilder() {
         validator = new Validator();
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

      private void setupCoverageNode(ICoverageNode node) {
         String name =getRandomString();
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
         String name = getRandomString();
         return classCoverage(name, name + ".java");
      }

      private IClassCoverage classCoverage(String name, String packageName) {
         // class name must have no periods for lookup to work
         IClassCoverage clz = mock(IClassCoverage.class);
         String interfaceName = getRandomString();
         String superName = getRandomString();
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
         when(clz.getSourceFileName()).thenReturn(name + ".java");
         when(clz.getName()).thenReturn(name);

         validator.add(clz);
         return clz;
      }

      public IPackageCoverage packageCoverage(int numClasses,
            int numInnerClasses) {
         List<ISourceFileCoverage> sourceClasses = new ArrayList<ISourceFileCoverage>();
         List<IClassCoverage> allClasses = new ArrayList<IClassCoverage>();
         for (int i = 0; i < numClasses; i++) {
            String name = getRandomString();
            String pkg = getRandomString();
            IClassCoverage clz = classCoverage(name, pkg);
            allClasses.add(clz);

            if (i < numInnerClasses) {
               allClasses.add(classCoverage(getRandomString(),
                     clz.getSourceFileName()));
            } else {
               sourceClasses.add(sourceFile(name, pkg));
            }

         }

         IPackageCoverage pkg = mock(IPackageCoverage.class);
         setupCoverageNode(pkg);
         String name = getRandomString();
         when(pkg.getName()).thenReturn(name);
         when(pkg.getClasses()).thenReturn(allClasses);
         when(pkg.getSourceFiles()).thenReturn(sourceClasses);
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

   public static class Validator {
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
            assertEquals(expected.getFirstLine(), method.getFirstLine());
            assertEquals(expected.getLastLine(), method.getLastLine());

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
         assertEquals(expected.getStatus(), actual.getStatus());
      }

      private void assertCoverageNode(ICoverageNode expected, Coverage actual) {
         assertNotNull(expected);
         assertCounter(expected.getBranchCounter(), actual.getBranchCoverage());
         assertCounter(expected.getClassCounter(), actual.getClassCoverage());
         assertCounter(expected.getLineCounter(), actual.getLineCoverage());
         assertCounter(expected.getMethodCounter(), actual.getMethodCoverage());
      }

      private void assertCounter(ICounter expected, CoverageCounter actual) {
         assertEquals(expected.getCoveredCount(), actual.getCovered());
         assertEquals(expected.getCoveredRatio(), actual.getCoveredRatio());
         assertEquals(expected.getMissedCount(), actual.getMissed());
         assertEquals(expected.getMissedRatio(), actual.getMissedRatio());
         assertEquals(expected.getTotalCount(), actual.getTotal());
         assertEquals(expected.getStatus(), actual.getStatus());
      }
   }
}
