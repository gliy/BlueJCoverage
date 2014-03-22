package bluej.codecoverage.utils;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Loads the jars of all the files that will need to be accessed when collecting
 * code coverage metrics.
 * 
 * Required due to {@link FirewallLoader}
 * 
 * @author ikingsbu
 * 
 */
public class BreakoutClassLoader extends URLClassLoader {

   /**
    * Creates a new ClassLoader to use for all communication with Jacoco.
    * <p>
    * The Custom Jacoco classes that are loaded are contained in the jar under
    * the lib directory.
    * 
    * @throws Exception
    */
   public BreakoutClassLoader() throws Exception {
      super(loadAllClasses(), BreakoutClassLoader.class.getClassLoader()
            .getParent());

   }

   /**
    * Returns an array of URLs of class directories to load.
    * 
    * @return URLs to load classes from.
    * @throws Exception
    */
   private static URL[] loadClasses() throws Exception {
      return new URL[] { BreakoutClassLoader.class.getClassLoader()
            .getResource("lib/") };

   }

   /**
    * Returns an array of URLs to include in our ClassLoader.
    * <p>
    * This includes both {@link #loadClasses()} and all URLs defined in the
    * parent ClassLoader.
    * 
    * @return An array of all URLs that will be included in our ClassLoader.
    * @throws Exception
    */
   private static URL[] loadAllClasses() throws Exception {
      List<URL> jarsToLoad = new ArrayList<URL>();
      jarsToLoad.addAll(Arrays.asList(loadClasses()));
      jarsToLoad.addAll(Arrays
            .asList(((URLClassLoader) BreakoutClassLoader.class
                  .getClassLoader()).getURLs()));

      return jarsToLoad.toArray(new URL[0]);
   }
}
