package bluej.codecoverage.utils.serial;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.analysis.ICoverageNode;
import org.jacoco.core.analysis.ILine;
import org.jacoco.core.analysis.IMethodCoverage;
import org.jacoco.core.analysis.IPackageCoverage;
import org.jacoco.core.analysis.ISourceFileCoverage;
import org.jacoco.report.JavaNames;

/**
 * Extracts needed information from Jacoco's coverage, and turns it into a
 * serializable representation.
 * 
 * @author ikingsbu
 * 
 */
public class CoverageBridge {

   /**
    * Turn each class in the package into our representation and return it in a
    * package.
    * <p>
    * First it converts all Jacoco's ClassCoverage objects into
    * {@link CoverageClass}, then performs a merge on all of the gathered class
    * level coverage information.</br>
    * 
    * The merge is required because Jacoco returns a different ClassCoverage for
    * every class, included nested classes. </br> While Jacoco's
    * ISourceFileCoverage returns only 1 element per file, like we want, it
    * lacks information like method locations.
    * 
    * @param pkg
    *           the Jacoco object to transform
    * @return our representation of the Jacoco object.
    */
   public static CoveragePackage toSerializable(IPackageCoverage pkg) {
      List<CoverageClass> classes = new ArrayList<CoverageClass>();
      for (IClassCoverage coverage : pkg.getClasses()) {
         classes.add(toSerializable(coverage));
      }
      Map<String, ISourceFileCoverage> sourceCoverage = new HashMap<String, ISourceFileCoverage>();
      for (ISourceFileCoverage source : pkg.getSourceFiles()) {
         sourceCoverage.put(source.getPackageName() + source.getName(), source);
      }
      classes = merge(classes, sourceCoverage);
      CoveragePackage pkgCoverage = new CoveragePackage(classes);
      fillBase(pkg, pkgCoverage);
      return pkgCoverage;
   }

   /**
    * Merges all class level coverage information, so there is 1 CoverageClass
    * per source file.
    * <p>
    * This is done by finding the CoverageClass objects whose class name matches
    * the name of the source file.</br> Once those are filtered all remaining
    * classes are added as children to the CoverageClass that has the same
    * source file name.
    * 
    * @param classes
    *           the list of CoverageClasses to merge
    * @param sourceCoverage
    *           Map of package + source file name to the coverage for a source
    *           file.
    * @return reduced list of CoverageClasses, with 1 per source file.
    */
   private static List<CoverageClass> merge(List<CoverageClass> classes,
         Map<String, ISourceFileCoverage> sourceCoverage) {
      List<CoverageClass> innerClasses = new ArrayList<CoverageClass>();
      Map<String, CoverageClass> byFileName = new HashMap<String, CoverageClass>();
      for (CoverageClass clz : classes) {
         String key = clz.getPackageName() + clz.getSourceFileName();
         if (clz.getName().equals(
               clz.getSourceFileName().substring(0,
                     clz.getSourceFileName().indexOf(".java")))) {
            // use the source file coverage if there is more then 1 class in the
            // file
            ISourceFileCoverage srcCov = sourceCoverage.get(key);
            String curName = clz.getName();
            fillBase(srcCov, clz);
            clz.setName(curName);
            byFileName.put(key, clz);
         } else {
            innerClasses.add(clz);
         }

      }
      for (CoverageClass inner : innerClasses) {
         inner.setName(findName(inner));
         String key = inner.getPackageName() + inner.getSourceFileName();
         CoverageClass parent = byFileName.get(key);
         // failsafe
         if (parent == null) {

            byFileName.put(key, inner);
         } else {
            parent.addClass(inner);
         }
      }
      return new ArrayList<CoverageClass>(byFileName.values());
   }

   /**
    * Finds the class name of an inner class.
    * <p>
    * By Default the class name for an inner class is ParentClass$1 for
    * Anonymous classes, and ParentClass$ChildClass for nested classes.
    * 
    * @param inner
    *           the class to find the name for
    * @return the correct name.
    */
   private static String findName(CoverageClass inner) {
      String name = new JavaNames().getClassName(inner.getName(), null,
            inner.getSuperClass(), inner.getInterfaces());
      if (name.contains(".")) {
         name = name.substring(name.indexOf(".") + 1);
      }
      return name;
   }

   /**
    * Converts the Jacoco IClassCoverage into CoverageClass.
    * <p>
    * CoverageClass contains coverage information for the class, as well as a
    * list of methods, and a list of CoverageClasses, in the case of nested
    * classes.
    * 
    * @param clz
    * @return
    */
   private static CoverageClass toSerializable(IClassCoverage clz) {
      List<CoverageLine> lines = new ArrayList<CoverageLine>();
      int first = clz.getFirstLine();
      int last = clz.getLastLine();
      for (int lineNum = first; lineNum <= last; lineNum++) {
         ILine iline = clz.getLine(lineNum);
         lines.add(toLine(iline));

      }

      CoverageClass rtn = new CoverageClass();
      rtn.setLineCounter(lines);
      rtn.setFirstLine(first);
      rtn.setLastLine(last);
      rtn.setSourceFileName(clz.getSourceFileName());
      rtn.setPackageName(clz.getPackageName());
      rtn.setMethodCounter(toMethods(clz.getMethods()));
      rtn.setInterfaces(clz.getInterfaceNames());
      rtn.setSuperClass(clz.getSuperName());
      fillBase(clz, rtn);

      return rtn;
   }

   /**
    * Fills in the coverage information that is common to all types.
    * 
    * @param src
    *           the Jacoco object
    * @param dest
    *           our representation of the jacoco object.
    */
   private static void fillBase(ICoverageNode src, Coverage dest) {
      dest.setName(src.getName());
      dest.setLineCoverage(toCounter(src.getLineCounter()));
      dest.setBranchCoverage(toCounter(src.getBranchCounter()));
      dest.setMethodCoverage(toCounter(src.getMethodCounter()));
      dest.setClassCoverage(toCounter(src.getClassCounter()));
   }

   private static List<CoverageMethod> toMethods(
         Collection<IMethodCoverage> methods) {
      ArrayList<CoverageMethod> rtn = new ArrayList<CoverageMethod>();
      for (IMethodCoverage method : methods) {
         CoverageMethod coverageMethod = new CoverageMethod(
               method.getFirstLine());
         fillBase(method, coverageMethod);
         rtn.add(coverageMethod);

      }
      return rtn;

   }

   private static CoverageLine toLine(ILine line) {
      ICounter branch = line.getBranchCounter();
      int status = line.getStatus();

      return new CoverageLine(status, toCounter(line.getBranchCounter()));
   }

   private static CoverageCounter toCounter(ICounter line) {
      return new CoverageCounter(line.getCoveredCount(), line.getMissedCount(),
            line.getTotalCount(), line.getStatus(), line.getCoveredRatio(),
            line.getMissedRatio());
   }
}
