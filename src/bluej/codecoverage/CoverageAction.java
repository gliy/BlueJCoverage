package bluej.codecoverage;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import bluej.codecoverage.utils.CoverageUtilities;
import bluej.codecoverage.utils.serial.CoverageClass;
import bluej.codecoverage.utils.serial.CoverageCounterValue;
import bluej.codecoverage.utils.serial.CoverageLine;
import bluej.extensions.BClass;
import bluej.extensions.ExtensionException;

/**
 * Performs the attach action when a choice is selected.
 * 
 * @author Ian
 * 
 */
public class CoverageAction extends AbstractAction
{

    private BClass bClass;

    /**
     * Constructs the action to be executed when the menu item is pressed. Sets the text
     * for the action in the menu to be the class name of the unit test provided.
     * 
     * @param source
     *            class that the menu appeared on
     * @param unitTestClass
     *            class that should be attached to source
     * 
     * @throws ExtensionException
     *             if there is an error getting the editor throws an ExtensionException
     */
    public CoverageAction(BClass clz)
    {
        super("Show coverage");
        this.bClass = clz;
    }

    /**
     * When a class is selected for attachment, attempts to attach the class prompting
     * the user if the class is already attached.
     * 
     * @param anEvent
     *            the trigger event
     */
    @Override
    public void actionPerformed(ActionEvent anEvent)
    {

        try
        {
            for (CoverageClass clz : CoverageUtilities.get()
                .getResults(bClass.getClassFile()))
            {
                System.out.println(clz.getName());
                System.out.println("\t" + clz.getFirstLine() + ","
                    + clz.getLastLine());
                Vector<String> data = new Vector<String>();
                int base = clz.getFirstLine();
                int i = 0;
                for (CoverageLine li : clz.getLineCounter())
                {

                    String display = (base + i++) + ": ";
                    display += CoverageCounterValue.from(li.getStatus())
                        + " > Branchs: " + li.getBranchCounter()
                            .getCovered() + "/" + li.getBranchCounter()
                            .getTotal();
                    data.add(display);
                }
                JFrame display = new JFrame();
                
                JList<String> list = new JList<String>(data);
                display.add(new JScrollPane(list ));
                list.setVisibleRowCount(30);
                display.pack();
                
                display.setTitle("Coverage for " + bClass.getName());
                display.setLocationRelativeTo(bClass.getPackage().getFrame());
                display.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                display.setVisible(true);
            }
        }
        catch (Exception ex)
        {
            JOptionPane.showMessageDialog(null, ex.getMessage());
            ex.printStackTrace();
        }

    }

}
