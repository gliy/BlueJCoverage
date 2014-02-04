package com.bluej.codecoverage;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bluej.extensions.BProject;

/**
 * Loads the jars of all the files that will need to be accessed when
 * collecting code coverage metrics.
 * 
 * Required due to {@link FirewallLoader}
 * @author ikingsbu
 *
 */
public class BreakoutClassloader extends URLClassLoader {

   public BreakoutClassloader(BProject project) throws Exception {
      super(loadJars(project), BreakoutClassloader.class.getClassLoader()
            .getParent().getParent());

   }

   private static URL[] loadJars(BProject project) throws Exception {
      List<URL> jarsToLoad = new ArrayList<URL>();
      for (File fi : new File("/media/PENDRIVE/workspace/BlueJCoverage/lib")
            .listFiles()) {
         System.out.println("Adding " + fi.getName());
         jarsToLoad.add(fi.toURI().toURL());
      }
      for (File fi : new File("/usr/local/bluej/lib/").listFiles()) {
         System.out.println("Adding " + fi.getName());
         jarsToLoad.add(fi.toURI().toURL());
      }
      jarsToLoad.addAll(Arrays.asList(project.getClassLoader().getURLs()));
      jarsToLoad.addAll(Arrays
            .asList(((URLClassLoader) BreakoutClassloader.class
                  .getClassLoader()).getURLs()));
      return jarsToLoad.toArray(new URL[0]);
   }
   
   @Override
   public Class<?> loadClass(String name) throws ClassNotFoundException {
      System.out.println("\tloading "+name);
      return super.loadClass(name);
   }

}
