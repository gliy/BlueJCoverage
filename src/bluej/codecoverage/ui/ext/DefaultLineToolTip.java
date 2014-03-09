package bluej.codecoverage.ui.ext;

import java.util.HashMap;
import java.util.Map;

import bluej.codecoverage.utils.serial.CoverageCounter;
import bluej.codecoverage.utils.serial.CoverageLine;

public class DefaultLineToolTip implements LineToolTip {
   private Map<Integer, CoverageLine> lineData;

   public DefaultLineToolTip() {
      this.lineData = new HashMap<Integer, CoverageLine>();
   }

   @Override
   public String getToolTip(int lineNum) {
      String rtn = null;
      CoverageLine line = lineData.get(lineNum);
      if (line != null) {
         CoverageCounter branches = line.getBranchCounter();
         if (branches.getTotal() != 0) {
            if (branches.getMissed() > 0) {
               rtn = String.format("%d/%d branches missed",
                     branches.getMissed(), branches.getTotal());
            } else {
               rtn = "All " + branches.getTotal() + " covered";
            }

         }
      }
      return rtn;
   }

   @Override
   public void registerLine(int line, CoverageLine data) {
      lineData.put(line, data);

   }
}
