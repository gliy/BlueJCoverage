/*
 * 
 */
package com.bluej.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import bluej.extensions.BClass;
import bluej.extensions.BPackage;
import bluej.extensions.BProject;
import bluej.extensions.BlueJ;
import bluej.extensions.ExtensionException;
import bluej.extensions.PackageNotFoundException;
import bluej.extensions.ProjectNotOpenException;
import bluej.extensions.event.PackageEvent;
import bluej.extensions.event.PackageListener;

/**
 * Utiltiy Class that controls all interaction with the properties file.
 * This handles what classes are valid targets, setting targets, reloading,
 * and saving of the properties.
 * 
 * @author Ian
 */
public final class TestAttacherUtilities implements PackageListener
{
    
    /** Standard Java package separator. */
    private static final String PACKAGE_SEP = ".";

    /** The number of targets. */
    private static final String TARGET_COUNT = "package.numTargets";
    
    /** package properties file name. */
    private static final String OLD_PKG = "bluej.pkg";
    
    /** Running instance of bluej. */
    private static BlueJ bluej;
    
    /** Singleton to hook into events. */
    private static final TestAttacherUtilities UTILS = new TestAttacherUtilities();
    
    /** Current information about attachments. */
    private final Map<String, BClassInfo> info = new HashMap<String, BClassInfo>();
    
    /** offset for targets */
    private static final Integer OFFSET = 30;
    /** Active package. */
    private BPackage activePackage;
    
    /**
     * Instantiates a new test attacher utilities.
     */
    private TestAttacherUtilities()
    {

    }
  

    /**
     * Sets up the Utilites class so it can properly manage associations.
     * 
     * @param bluejApp
     *            the running blueJ application
     */
    public static void setup(BlueJ bluejApp)
    {
        try
        {
            TestAttacherUtilities.bluej = bluejApp;
            bluej.addPackageListener(UTILS);
            if (bluej.getCurrentPackage() != null)
            {
                UTILS.setActive(bluej.getCurrentPackage());
                UTILS.load();
            }

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Links the 2 classes together.
     * 
     * @param src
     *            the class that the other class will be attached to
     * @param dest
     *            the class to attach
     */
    public static void setAssociation(BClassInfo src, BClassInfo dest)
    {
        UTILS.save(src, dest);
    }

    /**
     * Returns all valid attachment targets for the class.
     * 
     * @param info
     *            the class to find valid attachments for
     * @return all possible classes that can be attached
     * @throws ExtensionException
     *             any error occurs while performing this method, throws an
     *             ExtensionException
     */
    public static List<BClassInfo> getValidTargets(BClass info)
        throws ExtensionException
    {
        System.out.println("looking " + info);
        List<BClassInfo> valid = new ArrayList<BClassInfo>();
        for (BClassInfo cur : UTILS.info.values())
        {
            if (cur.isUnitTest
                && (cur.targetOf == null || !inChain(get(info), cur)))
            {
                valid.add(cur);
            }
        }
        return valid;
    }

    /**
     * Returns the BClassInfo that represents the specified class.
     * 
     * @param name
     *            BClass to get ClassInfo for
     * @return the specified BClass's ClassInfo
     * @throws ExtensionException
     *             any error occurs while performing this method, throws an
     *             ExtensionException
     */
    public static BClassInfo get(BClass name) throws ExtensionException
    {
        return UTILS.info.get(getName(name));
    }

    /**
     * Gets the name.
     * 
     * @param clz
     *            the clz
     * @return the name
     */
    private static String getName(BClass clz)
    {
        String qual = clz.getName();
        if(qual.contains(PACKAGE_SEP)) 
        {
            qual = qual.substring(qual.lastIndexOf(PACKAGE_SEP));
        }
        return qual;
    }

    /**
     * reloads all information about classes when a package is closed
     * 
     * @param event the triggering event
     */
    @Override
    public void packageOpened(PackageEvent event)
    {
        try
        {
            UTILS.setActive(event.getPackage());
            load();
        }
        catch (ExtensionException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Clears all information about classes when a package is closed
     * 
     * @param event the triggering event
     */
    @Override
    public void packageClosing(PackageEvent event)
    {
        info.clear();
    }

    
    /**
     * Sets the active.
     * 
     * @param pack
     *            the new active
     * @throws ProjectNotOpenException
     *             the project not open exception
     * @throws PackageNotFoundException
     *             the package not found exception
     */
    private void setActive(BPackage pack) throws ProjectNotOpenException,
        PackageNotFoundException
    {
        activePackage = pack;
    }
    

    /**
     * Gets the file.
     * 
     * @return the file
     * @throws ProjectNotOpenException
     *             the project not open exception
     * @throws PackageNotFoundException
     *             the package not found exception
     */
    private static File getFile() throws ProjectNotOpenException,
    PackageNotFoundException 
    {
        return new File(UTILS.activePackage.getDir() + File.separator + OLD_PKG);
    }

    /**
     * Load the class information from the propeties file,
     * and update the internal map.
     */
    private void load()
    {
        try
        {
            info.clear();
            Properties props = new Properties(); // properties to load into
            props.load(new FileInputStream(getFile()));
            // number of targets in the properties file
            int max = Integer.parseInt(props.getProperty(TARGET_COUNT)); 
            // temp map for classes who are out of order
            Map<String, String> targetTmp = new HashMap<String, String>();
            
            // Go through each target in the properties file
            for (int index = 1; index <= max; index++)
            {
                // the name of the class
                String name = props.getProperty(Keys.NAME.make(index)); 
                // the type of the class (unittest, classtarget)
                String type = props.getProperty(Keys.TYPE.make(index));
                // the class that is targeted by this class
                String assoc = props.getProperty(Keys.ASSOCIATION.make(index));
                BClassInfo newInfo = new BClassInfo(index, name, type);
                
                // If this class targets another class
                if (assoc != null)
                {
                    // If we have already loaded this class, set the target.
                    if (info.containsKey(assoc))
                    {
                        BClassInfo toTarget = info.get(assoc);
                        newInfo.setTarget(toTarget);
                        toTarget.setTargetOf(newInfo);
                    }
                    // Otherwise wait until we load the targeted class
                    // before setting the association
                    else
                    {
                        targetTmp.put(assoc, name);
                    }
                }
                
                info.put(name, newInfo);
            }
            // If the class we just loaded is targeted by someone else
            // set the assocation
            for (Entry<String, String> ent : targetTmp.entrySet())
            {
                BClassInfo target = info.get(ent.getKey());
                BClassInfo dest = info.get(ent.getValue());
                target.setTargetOf(dest);
                dest.setTarget(target);
            }
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Set the association of src so that it will target dest.
     * 
     * @param src
     *            the src the class to add a target to
     * @param dest
     *            the dest the class that will be targeted.
     */
    private void save(BClassInfo src, BClassInfo dest)
    {
        String key = Keys.ASSOCIATION.make(src.index); // key to add for targeting
        String value = null;
        Integer destIdx = null; // target number for destination
        // If we want to clear a target dest will be null
        if (dest != null)
        {
            value = dest.name;
            destIdx = dest.index;
        }
        reload(key, value, src.index, destIdx);
    }

    /**
     * Reloads the properties file and adds the target key, and updates the x/y locations
     * @param key the src key value
     * @param value the dest class name
     * @param srcIdx the target index of the src
     * @param destIdx the target index of the dest
     */
    private void reload(String key, String value, int srcIdx, Integer destIdx)
    {
        try
        {

            File packagePropFile = getFile(); // properties file
            BProject project = activePackage.getProject(); // current project
            File projectFile = project.getDir(); // current project's dir

            project.close();
            
            // If key is null just reload the properties, dont save anything.
            if (key != null)
            {
                Properties props = new Properties();
                props.load(new FileInputStream(packagePropFile));
                // If there is not value to set remove the key
                if (value == null)
                {
                    props.remove(key);
                }
                // Otherwise set the target and update the x and y values
                else
                {
                    props.setProperty(key, value);
                    props.setProperty(
                        Keys.X.make(destIdx),
                        "" + (Integer.parseInt(
                            props.getProperty(Keys.X.make(srcIdx))) + OFFSET));
                    props.setProperty(
                        Keys.Y.make(destIdx),
                        "" + (Integer.parseInt(
                            props.getProperty(Keys.Y.make(srcIdx))) - OFFSET));
                }
                // output to save the new properties
                OutputStream output = new FileOutputStream(packagePropFile);

                props.store(output, "");
                output.close();
            }
            bluej.openProject(projectFile);
            
            load();
            activePackage.reload();
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }
    
    /**
     * Checks to see if lookFor contains testClass in its association chain.
     * 
     * @param lookFor the class whos target's to check
     * @param testClass the class to check for
     * @return true if testClass was found in the chain, otherwise false.
     */
    private static boolean inChain(BClassInfo lookFor, BClassInfo testClass) 
    {
        // if a match is found is set to true
        boolean found = false;
        
        // While there are still elements in the target and a match
        // has not been found
        while(!found && lookFor.getTarget() != null) 
        {
            found |= lookFor.getTarget().equals(testClass);
            lookFor = lookFor.getTarget();
        }
        return found;
    }
    
    /**
     * Keys that represent the information we care about for classes'
     */
    private enum Keys
    {
       NAME, ASSOCIATION, TYPE, X, Y;
        
        /**
         * Creates a key as represented in the properties file.
         * 
         * @param index
         *            the index of the target
         * @return the properties string
         */
        public String make(int index)
        {
            return "target" + index + "." + name().toLowerCase();
        }
    }
    
    /**
     * The Class BClassInfo.
     */
    public static class BClassInfo
    {
        
        /** The is unit test. */
        private boolean isUnitTest;
        
        /** The index. */
        private int index;
        
        /** The name. */
        private String name;
        
        /** The target. */
        private BClassInfo target;
        
        /** The target of. */
        private BClassInfo targetOf;
        
        /** The Constant UNIT_TEST. */
        private static final String UNIT_TEST = "UnitTestTarget";

        /**
         * Instantiates a new BClassInfo with information from the properties
         * file.
         * 
         * @param index
         *            the index as specified in the properties
         * @param name
         *            the unique name of this class
         * @param target
         *            the type of this class, ie unittest, classtarget ...
         */
        public BClassInfo(int index, String name, String target)
        {
            super();
            this.isUnitTest = UNIT_TEST.equals(target);
            this.name = name;
            this.index = index;
        }

        /**
         * Checks if is unit test.
         * 
         * @return true, if is unit test
         */
        public boolean isUnitTest()
        {
            return isUnitTest;
        }

        /**
         * Gets the class name of this class.
         * 
         * @return the name of this class.
         */
        public String getName()
        {
            return name;
        }

        /**
         * Gets the target of this class.
         * 
         * @return the target of this class or
         *  null if one does not exist.
         */
        public BClassInfo getTarget()
        {
            return target;
        }

        /**
         * Gets the class that targets this class.
         * 
         * @return the class that targets this class, or null if
         *  one does not exist.
         */
        public BClassInfo getTargetOf()
        {
            return targetOf;
        }

        /**
         * Sets the target of this class.
         * 
         * @param target
         *            the new target of this class
         */
        private void setTarget(BClassInfo target)
        {
            this.target = target;
        }

        /**
         * Sets the class that targets this class.
         * 
         * @param targetOf
         *            the new target 
         */
        private void setTargetOf(BClassInfo targetOf)
        {
            this.targetOf = targetOf;
        }

       
        @Override
        public int hashCode()
        {
            return name.hashCode();
        }
        @Override
        public boolean equals(Object obj)
        {
            return obj != null && getClass() == obj.getClass()
                && name.equals(((BClassInfo) obj).name);

        }

    }

}
