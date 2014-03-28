package bluej.codecoverage.utils.join;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.PriorityQueue;

import lombok.Getter;
import bluej.extensions.BPackage;

public class CoverageBundleManager {
   private PriorityQueue<CoverageBundle> allSessions;
   private static final int MAX_SIZE = 5;
   private int coverageNumber;

   public CoverageBundleManager() {
      allSessions = new PriorityQueue<CoverageBundle>();
      coverageNumber = 0;
   }

   public CoverageBundle createBundle(List<BCoveragePackage> allPackages) {
      if (allSessions.size() >= MAX_SIZE) {
         allSessions.remove();
      }
      coverageNumber++;
      CoverageBundle newBundle = new CoverageBundle(allPackages, "Session "
            + coverageNumber);
      allSessions.add(newBundle);
      return newBundle;

   }
   
   public List<CoverageBundle> getAllBundles() {
      List<CoverageBundle> bundles = Arrays.asList(allSessions.toArray(new CoverageBundle[0]));
      Collections.reverse(bundles);
      return bundles;
   }

   @Getter
   public class CoverageBundle implements Comparable<CoverageBundle> {

      private String name;
      private List<BCoveragePackage> allPackages;
      private Date recorded;

      private CoverageBundle(List<BCoveragePackage> allPackages, String name) {
         this.allPackages = allPackages;
         this.name = name;
         this.recorded = new Date();
      }

      @Override
      public int compareTo(CoverageBundle other) {
         if (other == CoverageBundle.this) {
            return 0;
         }
         return recorded.compareTo(other.recorded);
      }

   }

}
