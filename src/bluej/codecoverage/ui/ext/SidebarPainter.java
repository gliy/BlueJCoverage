package bluej.codecoverage.ui.ext;

import java.awt.Graphics;

import bluej.codecoverage.utils.serial.CoverageLine;

public interface SidebarPainter {
   void paint(Graphics g, int row, int x, int y, int width, int height);
<<<<<<< HEAD
   void registerLine(int line, CoverageLine data);
=======
   
   void register(Integer line, CoverageLine coverage);
>>>>>>> Bug fixs and icon addition
}
