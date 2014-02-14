package bluej.codecoverage.ui;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.ToolTipManager;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import bluej.codecoverage.pref.StandardGUIPrefs;
import bluej.codecoverage.ui.ext.LineAttributes;
import bluej.codecoverage.ui.ext.LineToolTip;
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
class CoverageSourceDisplay extends JScrollPane
{
    private BCoverageClass coverage;
    private List<LineAttributes> lineAttributes;
    private Map<Integer, CoverageLine> lineStats;
    private LineToolTip tooltip;
    private JTextPane source;

    public CoverageSourceDisplay(BCoverageClass coverage)
        throws ProjectNotOpenException, PackageNotFoundException,
        BadLocationException
    {
        this(coverage, new StandardGUIPrefs().getAttributes(),
            new StandardGUIPrefs().getTooltip());
    }

    public CoverageSourceDisplay(BCoverageClass coverage,
        List<LineAttributes> lineAttributes, LineToolTip tooltip)
        throws ProjectNotOpenException, PackageNotFoundException,
        BadLocationException
    {
        super();
        this.source = new CustomJTextPane();
        this.coverage = coverage;
        this.lineAttributes = lineAttributes;
        this.tooltip = tooltip;
        this.lineStats = mapLineToCoverage(coverage.getClassCoverage());
        generateDisplay();
    }

    private void generateDisplay() throws BadLocationException,
        ProjectNotOpenException, PackageNotFoundException
    {

        CoverageClass clz = coverage.getClassCoverage();
        ClassInfo bclz = coverage.getClassInfo();

        StyledDocument doc = source.getStyledDocument();
        Map<Integer, AttributeSet> lineToStyle = createStyleMap(clz);

        JPanel lineNumbers = new JPanel();
        lineNumbers.setLayout(new BoxLayout(lineNumbers, BoxLayout.Y_AXIS));
        
       // lineNumbers.setBackground(Color.GRAY);
        //lineNumbers.setOpaque(false);
        for (int line = 0; line < bclz.getNumberOfLines(); line++)
        {

            String sourceCode = bclz.getLine(line);
            AttributeSet style = lineToStyle.get(line);
            doc.insertString(doc.getLength(), sourceCode + "\n", style);
            JLabel lineLabel = new JLabel("" + (line + 1));
           
            if (style != null &&  style.getAttribute(StyleConstants.Background) != null)
            {
                Object background = style.getAttribute(StyleConstants.Background);

                lineLabel.setBackground((Color) background);
                lineLabel.setOpaque(true);
            }
            

            lineNumbers.add(lineLabel);

        }
        JViewport viewPort = new JViewport();
        viewPort.add(new TextLineNumber(source));
        setRowHeader(viewPort);
        source.setCaretPosition(0);
        ToolTipManager.sharedInstance()
            .registerComponent(this);
        source.setEditable(false);
        setViewportView(source);

    }

    private JViewport buildRowNumbers()
    {
        JPanel counter = new JPanel();
        counter.setLayout(new BoxLayout(counter, BoxLayout.Y_AXIS));
        for (int i = 0; i < 100; i++)
        {
            counter.add(new JLabel("" + i));

        }

        return new JScrollPane(counter).getViewport();
    }

    private Map<Integer, CoverageLine> mapLineToCoverage(CoverageClass clz)
    {
        int base = clz.getFirstLine();
        Map<Integer, CoverageLine> rtn = new HashMap<Integer, CoverageLine>();
        for (int i = 0; i < (clz.getLastLine() - base); i++)
        {
            CoverageLine covLine = clz.getLine(i);
            CoverageCounterValue lineStatus = CoverageCounterValue.from(covLine.getStatus());
            if (lineStatus != CoverageCounterValue.EMPTY)
            {
                rtn.put(base + i - 1, clz.getLine(i));
            }
        }
        return rtn;
    }

    private Map<Integer, AttributeSet> createStyleMap(CoverageClass clz)
    {
        Map<CoverageCounterValue, MutableAttributeSet> cache = new HashMap<CoverageCounterValue, MutableAttributeSet>();
        Map<Integer, AttributeSet> lineToStyle = new HashMap<Integer, AttributeSet>();
        for (Entry<Integer, CoverageLine> lineStat : lineStats.entrySet())
        {
            CoverageCounterValue lineStatus = CoverageCounterValue.from(lineStat.getValue()
                .getStatus());
            MutableAttributeSet styleToUse = cache.get(lineStatus);
            if (styleToUse == null)
            {

                styleToUse = new SimpleAttributeSet();
                for (LineAttributes attr : lineAttributes)
                {
                    attr.setStyle(styleToUse, lineStat.getValue());
                }
                cache.put(lineStatus, styleToUse);
            }
            lineToStyle.put(lineStat.getKey(), styleToUse);
        }
        return lineToStyle;
    }

    private class CustomJTextPane extends JTextPane
    {
        @Override
        public String getToolTipText(MouseEvent arg0)
        {

            int modelPoint = viewToModel(new Point(arg0.getX(), arg0.getY()));
            Element map = getDocument().getDefaultRootElement();
            modelPoint = map.getElementIndex(modelPoint);

            String rtn = null;
            if (lineStats.containsKey(modelPoint))
            {
                CoverageLine li = lineStats.get(modelPoint);
                rtn = tooltip.getToolTip(modelPoint, li);
            }
            return rtn;
        }
    }
}