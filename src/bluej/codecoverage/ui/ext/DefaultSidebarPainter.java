package bluej.codecoverage.ui.ext;

import java.awt.Graphics;
import java.util.HashMap;
import java.util.Map;

import bluej.codecoverage.utils.serial.CoverageBranch;
import bluej.codecoverage.utils.serial.CoverageLine;

public class DefaultSidebarPainter implements SidebarPainter {

   private Map<Integer, CoverageLine> lineData;

   public DefaultSidebarPainter() {
      this.lineData = new HashMap<Integer, CoverageLine>();
   }

   @Override
   public void paint(Graphics g, int row, int x, int y, int width, int height) {
      CoverageLine line = lineData.get(row);
      if (line.getBranchCoverageType() != null
               && line.getBranchCoverageType() != CoverageBranch.NONE) {
         g.drawImage(line.getBranchCoverageType().getImg().getImage(), x, y,width,height,
                   null);
      }
   }

   @Override
   public void registerLine(int line, CoverageLine data) {
      this.lineData.put(line, data);

   }

}
