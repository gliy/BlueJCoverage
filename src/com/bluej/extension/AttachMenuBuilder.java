package com.bluej.extension;

import static com.bluej.main.TestAttacherUtilities.get;
import static com.bluej.main.TestAttacherUtilities.getValidTargets;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import bluej.extensions.BClass;
import bluej.extensions.ExtensionException;
import bluej.extensions.MenuGenerator;

import com.bluej.codecoverage.BreakoutClassloader;
import com.bluej.main.TestAttacherUtilities.BClassInfo;

/**
 * Generates an item on a Class's popup menu when that class is right clicked.
 * 
 * @author Ian
 * 
 */
public class AttachMenuBuilder extends MenuGenerator
{

    /** The Constant EMPTY. */
    private static final int EMPTY = 0;

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

        // menu to add to the popup
        JMenu menu = null;

        try
        {
            ClassLoader loader = new BreakoutClassloader(aClass.getPackage()
                .getProject());
            Object o = loader.loadClass("com.bluej.extension.CoverageRunner")
                .newInstance();
            Class<?> loaderBClass = loader.loadClass(aClass.getName());
            o.getClass()
                .getMethod("runCoverage", Class.class)
                .invoke(o, aClass.getJavaClass());
            // ((CoverageRunner) o).runCoverage(aClass);
            System.out.println("trying load");
            Class.forName("org.jacoco.core.analysis.Analyzer", true, loader);
            Class.forName("org.jacoco.core.analysis.ICoverageVisitor", true, loader);
            System.out.println("ddd");
            // If it is not a unit test add it to the list
            if (!get(aClass).isUnitTest())
            {
              /*  Thread tr = new Thread(new Runnable()
                {
                    
                    @Override
                    public void run()
                    {
                        try
                        {
                           // new ClassAttachAction(source.getPackage()
                           //     .getProject()))
                            Object o = loader.loadClass("com.bluej.extension.ClassAttachAction").getConstructor(BProject.class)
                            .newInstance(aClass.getPackage().getProject());
                            ((ActionListener)o).actionPerformed(null);
                        }
                        catch (Exception e)
                        {
                            JOptionPane.showMessageDialog(null, ":(" + e);
                        }
                    }
                });
                tr.setContextClassLoader(loader);
                tr.start();*/
               // new ClassAttachAction(aClass.getPackage().getProject()).actionPerformed(null);
            }
        }
        catch (Throwable e)
        {
            JOptionPane.showMessageDialog(null, e);
            e.printStackTrace();
        }
        // If there are valid attachments, display the menu item,
        // otherwise hide it
        if (menu != null && menu.getItemCount() == EMPTY)
        {
            menu = null;
        }
        return menu;
    }

    /**
     * Generates a menu of all valid class targets that can be attached to the class.
     * 
     * @param source
     *            the selected class to find targets for.
     * @return the newly created menu
     * @throws ExtensionException
     *             If any error happens while generating the menu.
     */
    private JMenu attachToClass(BClass source) throws ExtensionException
    {
        // base menu
        JMenu menu = new JMenu("Attach Test to Class");

        // Add each valid target to the menu
        for (BClassInfo valid : getValidTargets(source))
        {

        }
        menu.add(new JMenuItem(new ClassAttachAction(source.getPackage()
            .getProject())));
        return menu;
    }

}
