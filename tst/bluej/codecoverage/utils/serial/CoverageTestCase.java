package bluej.codecoverage.utils.serial;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.annotation.ElementType;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import junit.framework.TestCase;

import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.analysis.ICoverageNode;
import org.jacoco.core.analysis.ILine;
import org.jacoco.core.analysis.IMethodCoverage;
import org.jacoco.core.analysis.ISourceNode;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class CoverageTestCase extends TestCase {
   private static final Random RANDOM = new Random();

   protected static int getRandomInt() {
      return RANDOM.nextInt(5000);
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
    	  String name = UUID.randomUUID().toString();
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
    //  private void setupSourceNode(ILine)
      public IClassCoverage classCoverage() {
    	  return null;
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

      private void setupSourceNode(ISourceNode node) {
         int firstLine = 1;
         int lastLine = 50;
         final ILine[] lines = new ILine[lastLine + firstLine];
         for (int lineNum = firstLine; lineNum < lastLine; lineNum++) {
            lines[lineNum] = line();
         }
         when(node.getLine(Mockito.anyInt())).then(new Answer<ILine>() {

            @Override
            public ILine answer(InvocationOnMock invocation) throws Throwable {
               return lines[(int) invocation.getArguments()[0]];
            }
         });
         when(node.getFirstLine()).thenReturn(firstLine);
         when(node.getLastLine()).thenReturn(lastLine);
         setupCoverageNode(node);
      }

   }

   public static class Validator {
      private Map<String, IMethodCoverage> nameToMethod = new HashMap<String, IMethodCoverage>();

      public void add(IMethodCoverage method) {
         nameToMethod.put(method.getName(), method);
      }

      public void validate(CoverageMethod... methods) {
         for (CoverageMethod method : methods) {
            IMethodCoverage expected = nameToMethod.get(method.getName());
            assertNotNull(expected);
            assertCounter(expected.getBranchCounter(),
                  method.getBranchCoverage());
            assertCounter(expected.getClassCounter(), method.getClassCoverage());
            assertCounter(expected.getLineCounter(), method.getLineCoverage());
            assertCounter(expected.getMethodCounter(),
                  method.getMethodCoverage());
            assertEquals(expected.getFirstLine(), method.getStartLine());
            assertEquals(expected.getLastLine(), method.getLastLine());
            for (int i = expected.getFirstLine(); i < expected.getLastLine(); i++) {
               int ourLine = i - expected.getFirstLine();
               ILine expectedLine = expected.getLine(i);

            }
         }
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

