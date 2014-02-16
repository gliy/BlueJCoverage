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
    
    public BreakoutClassloader(File userLib) throws Exception
    {
        super(loadAllClasses(), BreakoutClassloader.class.getClassLoader()
            .getParent());
     
    }

    private static URL[] loadClasses() throws Exception
    {
        return new URL[]{BreakoutClassloader.class.getClassLoader().getResource("lib/")};

    }
    private static URL[] loadAllClasses() throws Exception
    {
        List<URL> jarsToLoad = new ArrayList<URL>();
        jarsToLoad .addAll(Arrays.asList(loadClasses()));
        jarsToLoad.addAll(Arrays.asList(((URLClassLoader) BreakoutClassloader.class.getClassLoader()).getURLs()));
        
        return jarsToLoad.toArray(new URL[0]);
    }
}
