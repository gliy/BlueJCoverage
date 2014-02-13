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

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

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
        coverage.setLayout(new GridLayout(3,1));
        coverage.setPreferredSize(new Dimension(leftBar.getWidth(), 50));
        startCoverage = new JButton("Start Coverage");
        startCoverage.addActionListener(ON_START);

        endCoverage = new JButton("End Coverage");
        endCoverage.addActionListener(ON_END);
        endCoverage.setEnabled(false);
        coverage.add(startCoverage);
        coverage.add(endCoverage);
        leftBar.setPreferredSize(new Dimension(startCoverage.getPreferredSize().width, leftBar.getHeight()));
        leftBar.add(coverage, 1);
    }

    private final ActionListener ON_START = new ActionListener()
    {

        @Override
        public void actionPerformed(ActionEvent e)
        {
            startCoverage.setEnabled(false);
            endCoverage.setEnabled(true);
            CoverageUtilities utils = CoverageUtilities.get();
            utils.clearResults();

        }
    };
    private final ActionListener ON_END = new ActionListener()
    {

        @Override
        public void actionPerformed(ActionEvent e)
        {
            startCoverage.setEnabled(true);
            endCoverage.setEnabled(false);
            try
            {
                List<CoveragePackage> coverage = new ArrayList<CoveragePackage>(
                    CoverageUtilities.get()
                        .getResults(pack.getProject().getDir()));
                List<BCoveragePackage> bcoverage = BCoverageBridge.toBCoverage(
                    coverage, bluej);
                JFrame report = new CoverageReportFrame(bcoverage);
                report.setLocationRelativeTo(bluej.getCurrentFrame());

                report.setVisible(true);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }

        }
    };

}
