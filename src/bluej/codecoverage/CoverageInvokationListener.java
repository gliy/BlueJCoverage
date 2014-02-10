package bluej.codecoverage;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import bluej.codecoverage.utils.CoverageUtilities;
import bluej.codecoverage.utils.serial.CoverageClass;
import bluej.codecoverage.utils.serial.CoverageCounterValue;
import bluej.codecoverage.utils.serial.CoverageLine;
import bluej.extensions.BClass;
import bluej.extensions.BlueJ;
import bluej.extensions.editor.TextLocation;
import bluej.extensions.event.InvocationEvent;
import bluej.extensions.event.InvocationListener;

public class CoverageInvokationListener implements InvocationListener
{
    private BClass bClass;
    private BlueJ bluej;
    private static CoverageInvokationListener listener;
    private CoverageInvokationListener(BClass bClass, BlueJ bluej)
    {
        super();
        this.bClass = bClass;
        this.bluej = bluej;
        
    }
    
    public static CoverageInvokationListener setup(BClass bClass, BlueJ bluej) {
        if(listener == null) {
            listener = new CoverageInvokationListener(bClass, bluej);
            bluej.addInvocationListener(listener);
        } else {
            listener.bClass = bClass;
        }
        return listener;
    }

    @Override
    public void invocationFinished(InvocationEvent event)
    {
       
        try
        {
            if(!bClass.getJavaClass().getName().equals(event.getClassName())){
                return;
            }
            for (CoverageClass clz : CoverageUtilities.get()
                .getResults(bClass.getClassFile()))
            {
                System.out.println(clz.getName());
                System.out.println("\t" + clz.getFirstLine() + ","
                    + clz.getLastLine());
                Vector<String> data = new Vector<String>();
                int base = clz.getFirstLine();
                int i = 0;
                JTextPane textPane = new JTextPane();
                StyledDocument doc = textPane.getStyledDocument();
                bluej.extensions.editor.Editor editor = bClass.getEditor();
                Map<Integer, CoverageCounterValue> vals = new HashMap<Integer, CoverageCounterValue> ();
                for (CoverageLine li : clz.getLineCounter())
                {

                    String display = (base + i++) + ": ";
                    display += CoverageCounterValue.from(li.getStatus())
                        + " > Branchs: " + li.getBranchCounter()
                            .getCovered() + "/" + li.getBranchCounter()
                            .getTotal();
                    System.out.println(display);
                    data.add(display);
                    
                    
                    vals.put(base + i, CoverageCounterValue.from(li.getStatus()));
                }
                for (int line = 0; line < editor.getLineCount(); line++)
                {
                    Style style = textPane.addStyle("Style line " + i, null);
                    CoverageCounterValue lineStatus = vals.get(line);
                    if (lineStatus != null)
                    {
                        getStyle(style, lineStatus);
                    }
                    doc.insertString(doc.getLength(), editor.getText(
                        new TextLocation(line, 0), new TextLocation(line,
                            editor.getLineLength(line) - 1)) + "\n", style);
                }
                JFrame display = new JFrame();

              //  JList<String> list = new JList<String>(data);
               // display.add(new JScrollPane(list));
              //  list.setVisibleRowCount(30);
                display.add(textPane);
                display.pack();
                
               
                
                display.setTitle("Coverage for " + bClass.getName());
                display.setLocationRelativeTo(bClass.getPackage()
                    .getFrame());
                display.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                display.setVisible(true);
            }
           /* Field editorFrame = bClass.getEditor().getClass().getDeclaredField("bjEditor");
            editorFrame.setAccessible(true);
            JFrame frame = (JFrame)editorFrame.get(bClass.getEditor());
            BorderLayout border = (BorderLayout)(frame.getContentPane().getLayout());
            JScrollPane src = (JScrollPane)border.getLayoutComponent(BorderLayout.CENTER);
            JEditorPane editorPane = (JEditorPane) src.getViewport().getView();
            editorPane.getGraphics().fillRect(0, 0, editorPane.getWidth(), editorPane.getHeight());*/
           
           
            
        }
        catch (Exception ex)
        {
            JOptionPane.showMessageDialog(null, ex.getMessage());
            ex.printStackTrace();
        }
    }
    private static void getStyle(Style style, CoverageCounterValue value) {
        switch(value) {
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
