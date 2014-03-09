package bluej.codecoverage.ui.ext;

import bluej.codecoverage.utils.serial.CoverageLine;

public interface LineToolTip {
   public String getToolTip(int lineNum);

   void registerLine(int line, CoverageLine data);
}
