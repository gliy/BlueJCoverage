package com.bluej.codecoverage;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bluej.extensions.BProject;

public class BreakoutClassloader extends URLClassLoader
{

    public BreakoutClassloader(BProject project) throws Exception
    {
        super(loadJars(project), BreakoutClassloader.class.getClassLoader()
            .getParent()
            .getParent());
        
    }

    private static URL[] loadJars(BProject project) throws Exception
    {
        List<URL> jarsToLoad = new ArrayList<URL>();
        for (File fi : new File(
            "F:\\Users\\Ian\\workspace\\AttacherExtension\\lib").listFiles())
        {
            jarsToLoad.add(fi.toURI()
                .toURL());
        }
        for (File fi : new File(
            "F:\\Users\\Ian\\Documents\\BluejTest\\BlueJ\\lib").listFiles())
        {
            jarsToLoad.add(fi.toURI()
                .toURL());
        }
        jarsToLoad.addAll(Arrays.asList( project.getClassLoader().getURLs()));
        jarsToLoad.addAll(Arrays.asList(((URLClassLoader) BreakoutClassloader.class.getClassLoader()).getURLs()));
        return jarsToLoad.toArray(new URL[0]);
    }

}
