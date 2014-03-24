package bluej.codecoverage.utils.join;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bluej.codecoverage.utils.serial.CoverageClass;
import bluej.codecoverage.utils.serial.CoveragePackage;

/**
 * Connects the coverage information generated by
 * {@link bluej.codecoverage.utils.serial.CoverageBridge} with their source
 * code.
 * 
 * @author ikingsbu
 * 
 */
public class BCoverageBridge {

   /**
    * Goes through all the coverage information, and searches the local
    * filesystem. This is because BlueJ does not provide BPackages for packages
    * that are not currently open in the GUI.
    * 
    * If the Filestystem search is unable to locate the file, then the coverage
    * information is excluded.
    * 
    * @param packages
    *           all collected coverage information.
    * @param baseDir
    *           the base directory to search from
    * @return the mapped coverage information
    * @throws Exception
    */
   public static List<BCoveragePackage> toBCoverage(
         List<CoveragePackage> packages, File baseDir) throws Exception {
      List<BCoveragePackage> bcoverage = new ArrayList<BCoveragePackage>();

      // go through each package first
      for (CoveragePackage coveragePkg : packages) {
         List<BCoverageClass> foundClasses = new ArrayList<BCoverageClass>();

         // go through all the classes
         for (CoverageClass coverageClass : coveragePkg.getClassCoverageInfo()) {
            File src = findFile(baseDir, coverageClass);
            // only include it in our report if we find the file under the base
            // directory of the project.
            if (src != null) {
               ClassInfo classInfo = new FileClassInfo(src,
                     coverageClass.getName());
               foundClasses.add(new BCoverageClass(classInfo, coverageClass));
            }
         }
         // if the package is not empty, add it to the list of packages to show.
         if (!foundClasses.isEmpty()) {
            BCoveragePackage bpkg = new BCoveragePackage(coveragePkg,
                  foundClasses);
            bcoverage.add(bpkg);
         }
      }

      return bcoverage;
   }

   /**
    * Tries to find a class file using base as the root directory to start
    * searching from.
    * <p>
    * If no file is found null is returned.
    * 
    * @param base
    *           the base directory for the search
    * @param clz
    *           Information about the class to find, like name, and package.
    * @return File containing the source code for the class defined in clz.
    * @throws Exception
    */
   private static File findFile(File base, CoverageClass clz) throws Exception {
      String sep = "";
      // if there is a package name we need to add a / to the end of it.
      if (clz.getPackageName() != null || !clz.getPackageName().isEmpty()) {
         sep = File.separator;
      }
      File toGet = new File(base + File.separator + clz.getPackageName() + sep
            + clz.getSourceFileName());

      if (!toGet.exists()) {
         return null;
      }

      return toGet;
   }

   //TODO: Not working
   public static final Comparator<? super BCoverage<?>> SORT_BY_COVERAGE = new Comparator<BCoverage<?>>() {

      @Override
      public int compare(BCoverage<?> o1, BCoverage<?> o2) {
         int order = o1.getLineCoverage().getMissed()
               - o2.getLineCoverage().getMissed();
         if (!o1.getNodes().isEmpty()) {
            Collections.sort(o1.getNodes(), SORT_BY_COVERAGE);
         }
         return order;
      }
   };
}