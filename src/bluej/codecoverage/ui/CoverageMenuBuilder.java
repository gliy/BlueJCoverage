package bluej.codecoverage.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import bluej.codecoverage.CoverageAction;
import bluej.codecoverage.utils.CoverageUtilities;
import bluej.codecoverage.utils.join.BCoverageBridge;
import bluej.codecoverage.utils.join.BCoveragePackage;
import bluej.codecoverage.utils.serial.CoveragePackage;
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

    private BlueJ bluej;
    JButton startCoverage;
    JButton endCoverage;
    private BPackage pack;
    
    public CoverageMenuBuilder(BlueJ bluej, BPackage pack) throws Exception
    {
        this.bluej = bluej;
        this.pack = pack;
        JFrame frame = (JFrame) pack.getFrame();
        Container content = frame.getContentPane();
        BorderLayout layout = (BorderLayout) content.getLayout();
        JSplitPane comp = (JSplitPane) layout.getLayoutComponent(BorderLayout.CENTER);
        JComponent leftBar = (JComponent) ((BorderLayout) ((JComponent) comp.getLeftComponent()).getLayout()).getLayoutComponent(BorderLayout.WEST);

        JPanel coverage = new JPanel();
        coverage.setLayout(new BoxLayout(coverage, BoxLayout.Y_AXIS));
       // coverage.setPreferredSize(new Dimension(leftBar.getWidth(), 50));
        
        setupButtons();
        coverage.setAlignmentX(0.5f);
        coverage.add(startCoverage);
        coverage.add(endCoverage);
        //leftBar.setPreferredSize(new Dimension(startCoverage.getPreferredSize().width, leftBar.getHeight()));
        leftBar.add(coverage, 1);
        
    }
    private void setupButtons() throws Exception {
        CoverageAction action = new CoverageAction(pack.getProject(), pack.getFrame());
        startCoverage = new JButton("Start Coverage");
        startCoverage.addActionListener(ON_START);
        startCoverage.setActionCommand(CoverageAction.START);
        startCoverage.addActionListener(action);
        endCoverage = new JButton("End Coverage  ");
        endCoverage.addActionListener(ON_END);
        endCoverage.addActionListener(action);
        endCoverage.setActionCommand(CoverageAction.STOP);
        endCoverage.setEnabled(false);
        
       // startCoverage.setAlignmentX(Component.CENTER_ALIGNMENT);
       // endCoverage.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private final ActionListener ON_START = new ActionListener()
    {

        @Override
        public void actionPerformed(ActionEvent e)
        {
            startCoverage.setEnabled(false);
            endCoverage.setEnabled(true);
        }
    };
    private final ActionListener ON_END = new ActionListener()
    {

        @Override
        public void actionPerformed(ActionEvent e)
        {
            startCoverage.setEnabled(true);
            endCoverage.setEnabled(false);
        }
    };

}
