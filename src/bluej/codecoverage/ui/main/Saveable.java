package bluej.codecoverage.ui.main;

import bluej.codecoverage.utils.join.CoverageBundleManager.CoverageBundle;



public interface Saveable {
   void save(CoverageBundle state);
   
   
   void load(CoverageBundle state);
}
