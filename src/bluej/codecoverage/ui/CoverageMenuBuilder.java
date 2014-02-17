package bluej.codecoverage.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;

import bluej.codecoverage.CoverageAction;
import bluej.extensions.BPackage;
import bluej.extensions.BlueJ;

/**
 * Generates an item on a Class's popup menu when that class is right clicked.
 * 
 * @author Ian
 * 
 */
public class CoverageMenuBuilder
{
    private JToggleButton coverageButton;

    public CoverageMenuBuilder(BlueJ bluej, BPackage pack) throws Exception
    {

        JFrame frame = (JFrame) pack.getFrame();
        Container content = frame.getContentPane();
        BorderLayout layout = (BorderLayout) content.getLayout();
        JSplitPane comp = (JSplitPane) layout.getLayoutComponent(BorderLayout.CENTER);
        JComponent leftBar = (JComponent) ((BorderLayout) ((JComponent) comp
            .getLeftComponent()).getLayout()).getLayoutComponent(BorderLayout.WEST);

        JPanel coverage = new JPanel();

        coverageButton = new JToggleButton(getText("Start"),
            false);
        coverageButton.addItemListener(new ItemListener()
        {

            @Override
            public void itemStateChanged(ItemEvent e)
            {
                
                if (coverageButton.isSelected())
                {
                    coverageButton.setText(getText("End"));
                }
                else
                {
                    coverageButton.setText(getText("Start"));
                }
            }
        });
        coverage.setLayout(new BoxLayout(coverage, BoxLayout.Y_AXIS));
        coverage.setAlignmentX(0.5f);
        coverage.add(coverageButton);
        
        leftBar.add(coverage, 1);

    }
    private static String getText(String state) {
        return "<html>" + state + "</br>Coverage</html>";
    }
    public JToggleButton getButton() {
        return coverageButton;
    }

}
