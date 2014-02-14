package bluej.codecoverage.utils;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import javax.swing.JOptionPane;

/**
 * Loads the jars of all the files that will need to be accessed when collecting code
 * coverage metrics.
 * 
 * Required due to {@link FirewallLoader}
 * 
 * @author ikingsbu
 * 
 */
public class BreakoutClassloader extends URLClassLoader
{
    
    private static final String JAR_EXT = ".jar";
    private static final String[] REQUIRED_JARS = new String[]{"asm-all", "org.jacoco.core","org.jacoco.report"};

    public BreakoutClassloader(File userLib) throws Exception
    {
        super(loadJars(userLib), BreakoutClassloader.class.getClassLoader()
            .getParent());
     
    }

    private static URL[] loadJars2(File userLib) throws Exception
    {
        URL base = BreakoutClassloader.class.getProtectionDomain()
            .getCodeSource()
            .getLocation();
        Manifest con = new JarFile(base.getFile()).getManifest();
        String[] all = con.getMainAttributes()
            .getValue("Class-Path")
            .split(" ");
        URL u = new URL("jar", "", base + "!/");
        List<URL> rtn = new ArrayList<URL>();
        rtn.add(u);
        for (String s : all)
        {
            File file = new File(new URL("jar", "", base + "!/" + s).getFile());

            rtn.add(new URL("jar", "", base + "!/" + s));
        }
  
        return rtn.toArray(new URL[0]);

    }
    private static URL[] loadJars(File userLib) throws Exception
    {
        List<URL> jarsToLoad = new ArrayList<URL>();
        List<String> found = new ArrayList<String>(Arrays.asList(REQUIRED_JARS));
        for (File fi : userLib.listFiles())
        {
            boolean reqFile = false;
            Iterator<String> filePrefixs = found.iterator();
            while (!reqFile && filePrefixs.hasNext())
            {
                reqFile = fi.getName()
                    .startsWith(filePrefixs.next());
            }
            if (reqFile)
            {
                filePrefixs.remove();
                jarsToLoad.add(fi.toURI()
                    .toURL());
            }
        }
        if (!found.isEmpty())
        {
            JOptionPane.showMessageDialog(null,
                "Required jar(s) for code coverage not found: " + found,
                "Code Coverage", JOptionPane.ERROR_MESSAGE);
        }
  
        jarsToLoad.addAll(Arrays.asList(((URLClassLoader) BreakoutClassloader.class.getClassLoader()).getURLs()));
        return jarsToLoad.toArray(new URL[0]);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException
    {
     //   System.out.println("\tloading " + name);
        return super.loadClass(name);
    }

}
