package bluej.codecoverage.ui.ext;

import bluej.codecoverage.utils.serial.CoverageLine;

/**
 * Represents the text shown when a line in a source file is hovered over for a period of time.
 * @author ikingsbu
 *
 */
public interface LineToolTip {
   /**
    * Gets the text to show when the specified line is hovered over.
    * @param lineNum line number hovered on
    * @return text to show
    */
   String getToolTip(int lineNum);

   /**
    * Registers coverage information for a line so the correct text can be shown.
    * @param line line number the coverage information is for
    * @param data coverage information for that line
    */
   void registerLine(int line, CoverageLine data);
}
