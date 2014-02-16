package bluej.codecoverage.main;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;

import javax.swing.JToggleButton;

import bluej.codecoverage.CoverageAction;
import bluej.codecoverage.pref.CoveragePrefManager;
import bluej.codecoverage.ui.CoverageMenuBuilder;
import bluej.codecoverage.ui.CoveragePreferences;
import bluej.codecoverage.utils.CoverageUtilities;
import bluej.extensions.BlueJ;
import bluej.extensions.Extension;
import bluej.extensions.event.PackageEvent;
import bluej.extensions.event.PackageListener;

/**
 * TestAttacherExtension serves as an entrance point for the Extension, allowing BlueJ to
 * load it.
 * 
 * @author Ian Kingsbury
 */
public class CodeCoverageExtension extends Extension
{

    /** The Constant NAME. */
    private static final String NAME = "BCoverage";

    /** The Constant VERSION. */
    private static final String VERSION = "1.0";

    /**
     * When this method is called, the extension may start its work.
     * 
     * @param bluej
     *            the bluej application
     */
    @Override
    public void startup(final BlueJ bluej)
    {

        try
        {
            CoveragePrefManager.getPrefs(bluej);
            CoverageUtilities.create(bluej);
            // test(bluej);
            // System.exit(1);

            final List<JToggleButton> coverageButtons = new ArrayList<JToggleButton>();
            CoverageAction.init(bluej);
            bluej.setPreferenceGenerator(new CoveragePreferences(bluej));
            bluej.addPackageListener(new PackageListener()
            {

                @Override
                public void packageOpened(PackageEvent event)
                {
                    try
                    {
                        CoverageMenuBuilder builder = new CoverageMenuBuilder(bluej, event.getPackage());
                        boolean selected = false;
                        if(!coverageButtons.isEmpty()) {
                            selected = coverageButtons.get(0).isSelected();
                        } else {
                            builder.getButton().addItemListener(CoverageAction.get());
                        }
                        coverageButtons.add(builder.getButton());
                        builder.getButton().setSelected(selected);
                        builder.getButton().addItemListener(new ItemListener()
                        {
                            
                            @Override
                            public void itemStateChanged(ItemEvent e)
                            {
                                for (JToggleButton button : coverageButtons)
                                {
                                    button.setSelected(e.getStateChange() == ItemEvent.SELECTED);
                                }
                                
                            }
                        });
                        
                    }
                    catch (Exception e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                @Override
                public void packageClosing(PackageEvent event)
                {
                    // TODO Auto-generated method stub

                }
            });

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        // ClassAttachAction(aClass.getPackage().getProject()).actionPerformed(null);

    }

    private void test(BlueJ bluej) throws Exception
    {
        JarInputStream is = new JarInputStream(getClass().getClassLoader()
            .getResourceAsStream("jacocoagent.jar"));
        JarOutputStream out = new JarOutputStream(
            new FileOutputStream(new File(bluej.getUserConfigDir(), "jacoco2agent.jar")));

        copyFiles(is, out);

    }

    private static void copyFiles(JarInputStream in, JarOutputStream out)
        throws IOException
    {
        JarEntry inEntry;
        byte[] buffer = new byte[4096];

        while ((inEntry = (JarEntry) in.getNextEntry()) != null)
        {

            out.putNextEntry(new JarEntry(inEntry));
            int size = (int) inEntry.getSize();
            int len;

            int off = 0;
            while (size > 0 && (len = in.read(buffer, off, size)) >= 0)
            {
                out.write(buffer, 0, len);
                off += len;
                size -= len;
            }
            out.flush();
        }
        out.close();
    }

    @Override
    public boolean isCompatible()
    {
        return true;
    }

    @Override
    public String getVersion()
    {
        return VERSION;
    }

    @Override
    public String getName()
    {
        return NAME;
    }

    @Override
    public String getDescription()
    {
        return ("Adds Jacoco Code Coverage to BlueJ");
    }

}
