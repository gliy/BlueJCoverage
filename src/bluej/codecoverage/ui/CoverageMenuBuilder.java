package bluej.codecoverage.ui;

import java.awt.Component;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import bluej.codecoverage.CoverageAction;
import bluej.codecoverage.CoverageInvokationListener;
import bluej.extensions.BClass;
import bluej.extensions.BlueJ;
import bluej.extensions.MenuGenerator;

/**
 * Generates an item on a Class's popup menu when that class is right clicked.
 * 
 * @author Ian
 * 
 */
public class CoverageMenuBuilder extends MenuGenerator
{

    private BlueJ bluej;

    public CoverageMenuBuilder(BlueJ bluej)
    {
        this.bluej = bluej;
    }

    /**
     * Generates the menu for attachment if there are valid targets.
     * 
     * @param aClass
     *            the class the popupmenu is for
     * @return the generated menu or null if there are no targets
     * @see bluej.extensions.MenuGenerator#getClassMenuItem(bluej.extensions.BClass)
     */
    @Override
    public JMenuItem getClassMenuItem(final BClass aClass)
    {
        return new JMenu("Run Coverage");// new CoverageAction(aClass)
    }

    @Override
    public void notifyPostClassMenu(BClass bc, JMenuItem jmi)
    {
        JMenu menu = (JMenu) jmi;
        int count = 0;
        int seperatorCount = 0;
        Component[] comps = jmi.getParent()
            .getComponents();
        while (count < comps.length && seperatorCount < 2)
        {
            Component cur = comps[count++];
            if (cur instanceof JSeparator)
            {
                seperatorCount++;
            }
            else if (cur instanceof JMenuItem)
            {
                menu.add(clone(bc, (JMenuItem) cur));
            }
        }
        
        super.notifyPostClassMenu(bc, jmi);
    }

    private JMenuItem clone(BClass clz, JMenuItem toClone)
    {
        JMenuItem item = new JMenuItem();
        CoverageAction action = new CoverageAction(toClone.getAction(),
            toClone.getText(), clz, bluej);
        item.setAction(action);
        item.setActionCommand(toClone.getActionCommand());
        item.setActionMap(toClone.getActionMap());
       
        return item;
    }

}