package bluej.codecoverage.ui.main;

import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.ToolTipManager;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;

import bluej.codecoverage.pref.PreferenceManager;
import bluej.codecoverage.pref.SourceDisplayGUIPrefs;
import bluej.codecoverage.ui.ext.LineAttributes;
import bluej.codecoverage.ui.ext.LineToolTip;
import bluej.codecoverage.ui.ext.SidebarPainter;
import bluej.codecoverage.utils.join.BCoverageClass;
import bluej.codecoverage.utils.join.ClassInfo;
import bluej.codecoverage.utils.serial.CoverageClass;
import bluej.codecoverage.utils.serial.CoverageCounterValue;
import bluej.codecoverage.utils.serial.CoverageLine;
import bluej.extensions.PackageNotFoundException;
import bluej.extensions.ProjectNotOpenException;

/**
 * Displays the source code for a file with the coverage information overlayed.
 * 
 * @author ikingsbu
 * 
 */
class CoverageSourceDisplay extends JScrollPane {
   private BCoverageClass coverage;
   private List<LineAttributes> lineAttributes;
   private Map<Integer, CoverageLine> lineStats;
   private LineToolTip tooltip;
   private JTextPane source;
   private SidebarPainter painter;
   private TextLineNumber textLineNumber;

   public CoverageSourceDisplay(PreferenceManager prefManager,
         BCoverageClass coverage) throws ProjectNotOpenException,
         PackageNotFoundException, BadLocationException {
      super();
      SourceDisplayGUIPrefs prefs = new SourceDisplayGUIPrefs(prefManager);
      this.source = new CustomJTextPane();
      this.coverage = coverage;
      this.lineAttributes = prefs.getAttributes();
      this.tooltip = prefs.getTooltip();
      this.painter = prefs.getPainter();
      this.lineStats = mapLineToCoverage(coverage.getSource());
      generateDisplay();
   }

   public JTextPane getSource() {
      return source;
   }

   public String getId() {
      return coverage.getClassInfo().getId();
   }

   private void generateDisplay() throws BadLocationException,
         ProjectNotOpenException, PackageNotFoundException {

      CoverageClass clz = coverage.getSource();
      ClassInfo sourceFile = coverage.getClassInfo();

      StyledDocument doc = source.getStyledDocument();
      Map<Integer, AttributeSet> lineToStyle = createStyleMap(clz);


      for (int line = 0; line < sourceFile.getNumberOfLines(); line++) {

         String sourceCode = sourceFile.getLine(line);
         AttributeSet style = lineToStyle.get(line);
         doc.insertString(doc.getLength(), sourceCode + "\n", style);
      }
      JViewport viewPort = new JViewport();
      textLineNumber = new TextLineNumber(source);
      textLineNumber.setPainter(painter);
      textLineNumber.setTooltip(tooltip);
      viewPort.add(textLineNumber);
      setRowHeader(viewPort);
      source.setCaretPosition(0);
      ToolTipManager.sharedInstance().registerComponent(source);
      source.setEditable(false);
      setViewportView(source);

   }

   Map<Integer, CoverageLine> mapLineToCoverage(CoverageClass clz) {
      int base = clz.getFirstLine();
      Map<Integer, CoverageLine> rtn = new HashMap<Integer, CoverageLine>();
      for (int i = 0; i <= (clz.getLastLine() - base); i++) {
         CoverageLine covLine = clz.getLine(i);
         CoverageCounterValue lineStatus = CoverageCounterValue.from(covLine
               .getStatus());
         if (lineStatus != CoverageCounterValue.EMPTY) {
            rtn.put(base + i - 1, covLine);
            painter.registerLine(base + i - 1, covLine);
            tooltip.registerLine(base + i - 1, covLine);
         }
      }
      return rtn;
   }

   Map<Integer, AttributeSet> createStyleMap(CoverageClass clz) {
      Map<CoverageCounterValue, MutableAttributeSet> cache = new HashMap<CoverageCounterValue, MutableAttributeSet>();
      Map<Integer, AttributeSet> lineToStyle = new HashMap<Integer, AttributeSet>();
      for (Entry<Integer, CoverageLine> lineStat : lineStats.entrySet()) {
         CoverageCounterValue lineStatus = CoverageCounterValue.from(lineStat
               .getValue().getStatus());
         MutableAttributeSet styleToUse = cache.get(lineStatus);
         if (styleToUse == null) {

            styleToUse = new SimpleAttributeSet();
            for (LineAttributes attr : lineAttributes) {
               attr.setStyle(styleToUse, lineStat.getValue());
            }
            cache.put(lineStatus, styleToUse);
         }
         lineToStyle.put(lineStat.getKey(), styleToUse);
      }
      return lineToStyle;
   }

   private class CustomJTextPane extends JTextPane {

      public CustomJTextPane() {
         setFont(new Font("Monospaced", 0, 11));
      }

      @Override
      public String getToolTipText(MouseEvent arg0) {

         int modelPoint = viewToModel(new Point(arg0.getX(), arg0.getY()));
         Element map = getDocument().getDefaultRootElement();
         modelPoint = map.getElementIndex(modelPoint);

         return tooltip.getToolTip(modelPoint);
      }
   }

   public void moveCaret(int firstLine, int lastLine) {

      StyledDocument document = source.getStyledDocument();
      Element root = document.getDefaultRootElement();
      int visibleLines = source.getVisibleRect().height
            / source.getFontMetrics(source.getFont()).getHeight();

      int location = root.getElement(firstLine)
            .getStartOffset() - 1;
      int maxLines = root.getEndOffset();
      /*
      int newCaret = maxLines;
      int additionalLines = firstLine+visibleLines/2;
      if(root.getElementCount() > additionalLines) {
         newCaret = root.getElement(additionalLines).getStartOffset();
      }
      */
      source.setCaretPosition(location);

      source.moveCaretPosition(location);
   }
}