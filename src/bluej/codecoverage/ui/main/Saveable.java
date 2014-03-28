package bluej.codecoverage.ui.main;

import java.util.Map;


public interface Saveable {
   Map<String, String> save();
   void load(Map<String, String> info);
}
