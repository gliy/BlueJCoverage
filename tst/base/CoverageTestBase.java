package base;

import java.util.Random;

import junit.framework.TestCase;

public abstract class CoverageTestBase extends TestCase {
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

   protected static CoverageTestBuilder create() {
      return new CoverageTestBuilder();
   }
}
