package bluej.codecoverage.ui.ext;

import java.awt.Graphics;

public class DefaultSidebarPainter implements SidebarPainter{

   @Override
   public void paint(Graphics g, int row, int x, int y, int width, int height) {
     g.fillRect(x, y, width, height);
   }

}
