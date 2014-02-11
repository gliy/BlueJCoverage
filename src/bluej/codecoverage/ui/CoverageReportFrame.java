package bluej.codecoverage.ui;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import bluej.codecoverage.utils.serial.CoverageClass;
import bluej.codecoverage.utils.serial.CoverageCounterValue;
import bluej.extensions.BClass;
import bluej.extensions.PackageNotFoundException;
import bluej.extensions.ProjectNotOpenException;
import bluej.extensions.editor.Editor;
import bluej.extensions.editor.TextLocation;

public class CoverageReportFrame extends JFrame
{

    public CoverageReportFrame(Map<BClass, CoverageClass> classesCovered) throws ProjectNotOpenException, PackageNotFoundException, BadLocationException {



        generateDisplay(classesCovered);
        pack();
        setTitle("Coverage Report");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    private void generateDisplay(Map<BClass, CoverageClass> classesCovered) throws BadLocationException, ProjectNotOpenException, PackageNotFoundException {
        JTabbedPane allCoverageClasses = new JTabbedPane();
        
        for (Entry<BClass, CoverageClass> coverageInfo : classesCovered.entrySet())
        {
            CoverageClass clz =coverageInfo.getValue();
            BClass bclz = coverageInfo.getKey();
            System.out.println(clz.getName());
            System.out.println("\t" + clz.getFirstLine() + ","
                + clz.getLastLine());
            
            JTextPane textPane = new JTextPane();
            StyledDocument doc = textPane.getStyledDocument();
            Editor editor = bclz.getEditor();
            Map<Integer, AttributeSet> lineToStyle = createStyleMap(clz);

            for (int line = 0; line < editor.getLineCount(); line++)
            {


                String sourceCode = editor.getText(
                    new TextLocation(line, 0), new TextLocation(line,
                        editor.getLineLength(line) - 1));
                AttributeSet style = lineToStyle.get(line);
                doc.insertString(doc.getLength(), line  + ": "+ sourceCode + "\n",
                    style);

            }
            allCoverageClasses.add(bclz.getName(), new JScrollPane(textPane));
            textPane.setEditable(false);
            /*
             * //testing Field editorFrame =
             * bClass.getEditor().getClass().getDeclaredField("bjEditor");
             * editorFrame.setAccessible(true); JFrame frame =
             * (JFrame)editorFrame.get(bClass.getEditor()); BorderLayout border =
             * (BorderLayout)(frame.getContentPane().getLayout()); JScrollPane src =
             * (JScrollPane)border.getLayoutComponent(BorderLayout.CENTER);
             * JEditorPane editorPane = (JEditorPane) src.getViewport().getView();
             * Document moeDoc = editorPane.getDocument();
             */
        }
        add(allCoverageClasses);
    }

    private Map<Integer, AttributeSet> createStyleMap(CoverageClass clz)
    {
        int base = clz.getFirstLine();
        Map<CoverageCounterValue, MutableAttributeSet> cache = new HashMap<CoverageCounterValue, MutableAttributeSet>();
        Map<Integer, AttributeSet> lineToStyle = new HashMap<Integer, AttributeSet>();
        for (int i = 0; i < (clz.getLastLine() - base); i++)
        {
            CoverageCounterValue lineStatus = CoverageCounterValue.from(clz.getLine(
                i)
                .getStatus());
            MutableAttributeSet styleToUse = cache.get(lineStatus);
            if (styleToUse == null)
            {
                styleToUse = new SimpleAttributeSet();
                getStyle(styleToUse, lineStatus);
                cache.put(lineStatus, styleToUse);
            }
            lineToStyle.put(base + i - 1, styleToUse);
        }
        return lineToStyle;
    }

    private static void getStyle(MutableAttributeSet style, CoverageCounterValue value)
    {
        switch (value) {
            case FULLY_COVERED:
                StyleConstants.setBackground(style, Color.green);
                break;
            case NOT_COVERED:
                StyleConstants.setBackground(style, Color.RED);
                break;
            case EMPTY:
                break;
            case PARTLY_COVERED:
                StyleConstants.setBackground(style, Color.YELLOW);
                break;
            case UNKNOWN:
                break;
        }
    }
}
