/*
 * 
 */
package com.bluej.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import bluej.extensions.BPackage;
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
public final class CoverageUtilities implements PackageListener
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
    private static final CoverageUtilities UTILS = new CoverageUtilities();
    
    /** offset for targets */
    private static final Integer OFFSET = 30;
    /** Active package. */
    private BPackage activePackage;
    
    /**
     * Instantiates a new test attacher utilities.
     */
    private CoverageUtilities()
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
            CoverageUtilities.bluej = bluejApp;
            bluej.addPackageListener(UTILS);
            if (bluej.getCurrentPackage() != null)
            {
                UTILS.setActive(bluej.getCurrentPackage());
            }

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
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
            setActive(event.getPackage());
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
    
    public List<ClassLocation> getPackageClassLocations() {
       Properties props = new Properties();
       List<ClassLocation> locs = new ArrayList<ClassLocation>();
       InputStream fis = null;
       try {
          fis = new FileInputStream(getFile());
          props.load(fis);
          int targetCount = Integer.parseInt(props.getProperty(TARGET_COUNT));
          for(int index = 1; index <= targetCount; index++) {
             String name = props.getProperty(Keys.NAME.make(index));
             Integer x =  Integer.parseInt(props.getProperty(Keys.X.make(index)));
             Integer y =  Integer.parseInt(props.getProperty(Keys.Y.make(index)));
             locs.add(new ClassLocation(name, x, y));
          }
       }catch(Exception e) {
          e.printStackTrace();
       } finally {
          if(fis != null) {
             try {
                fis.close();
            } catch (IOException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }
          }
       }
       return locs;
       
    }

    
    /**
     * Keys that represent the information we care about for classes'
     */
    private enum Keys
    {
       NAME, X, Y;
        
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
    public static class ClassLocation {
       private String name;
       private int x;
       private int y;
      public ClassLocation(String name, int x, int y) {
         this.name = name;
         this.x = x;
         this.y = y;
      }
      private String getName() {
         return name;
      }
      private void setName(String name) {
         this.name = name;
      }
      private int getX() {
         return x;
      }
      private void setX(int x) {
         this.x = x;
      }
      private int getY() {
         return y;
      }
      private void setY(int y) {
         this.y = y;
      }
      
       
    }
    

}
