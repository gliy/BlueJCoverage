package bluej.codecoverage.utils.join;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import lombok.Getter;
import bluej.codecoverage.ui.main.Saveable;
import bluej.extensions.BPackage;

public class CoverageBundleManager {
   private PriorityQueue<CoverageBundle> allSessions;
   private static final int MAX_SIZE = 5;
   private int coverageNumber;
   private List<Saveable> stateSavers;
   public CoverageBundleManager() {
      allSessions = new PriorityQueue<CoverageBundle>();
      coverageNumber = 0;
      this.stateSavers = new ArrayList<Saveable>();
   }

   public void addSaver(Saveable saver) {
      stateSavers.add(saver);
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
   public void save(CoverageBundle bundle) {
      for (Saveable saver : stateSavers) {
         saver.save(bundle);
      }
   }
   public void load(CoverageBundle bundle) {
      for (Saveable saver : stateSavers) {
         saver.load(bundle);
      }
   }

   @Getter
   public class CoverageBundle implements Comparable<CoverageBundle> {

      private String name;
      private List<BCoveragePackage> allPackages;
      private Date recorded;
      private BundleState bundleState;
      
      private CoverageBundle(List<BCoveragePackage> allPackages, String name) {
         this.allPackages = allPackages;
         this.name = name;
         this.recorded = new Date();
         bundleState = new BundleState();
      }

      @Override
      public int compareTo(CoverageBundle other) {
         if (other == CoverageBundle.this) {
            return 0;
         }
         return recorded.compareTo(other.recorded);
      }
      public BundleState getState() {
         return bundleState;
      }

   }
   
   public static class BundleState {
      private Map<String, Object> state = new HashMap<String, Object>();
      public void save(String key, Object val) {
         state.put(key, val);
      }
      public <E> E load(String key) {
         return load(key, null);
      }
      public <E> E load(String key, E def) {
         E rtn = (E)state.get(key);
         return rtn != null ? rtn : def;
      }
   }

}
