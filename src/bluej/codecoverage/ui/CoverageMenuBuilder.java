package bluej.codecoverage.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import bluej.codecoverage.CoverageAction;
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
     * Generates a base menu for the coverage plugin, the option list
     * is populated when the context menu is shown.
     * 
     * @param aClass class that the menu is for.
     * @return skeleton menu
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
