package com.bluej.prefs;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.bluej.main.CoverageUtilities.ClassLocation;
import com.bluej.main.CoverageUtilities.Keys;

import bluej.extensions.BlueJ;

public class CoveragePrefManager {
   private static CoveragePrefManager prefs;
   private BlueJ bluej;
   private static final String FILE_NAME = "coverageprefs.properties";
   private CoveragePrefManager() {
      
   }
   
   public static CoveragePrefManager getPrefs() {
      if(prefs == null) {
         prefs = new CoveragePrefManager();
      }
      return prefs;
   }
   
   public void init(BlueJ bluej) {
      this.bluej = bluej;
   }
   
   public CurrentPreferences load() {
      File configDir = bluej.getUserConfigDir();
      for(File inDir : configDir.listFiles()) {
         if(inDir.getName().equals(FILE_NAME)) {
            return loadFromFile(inDir);
         }
      }
      return new DefaultPreferences();
   }
   
   private CurrentPreferences loadFromFile(File file) {
      Properties props = new Properties();
      
      List<ClassLocation> locs = new ArrayList<ClassLocation>();
      InputStream fis = null;
      CurrentPreferences loadedPrefs = null;
      try {
         fis = new FileInputStream(file);
         props.load(fis);

            String name = props.getProperty(Keys.NAME.make(index));
            Integer x =  Integer.parseInt(props.getProperty(Keys.X.make(index)));
            Integer y =  Integer.parseInt(props.getProperty(Keys.Y.make(index)));
            locs.add(new ClassLocation(name, x, y));
         
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
      if(loadedPrefs == null) {
         return new DefaultPreferences();
      }
      return loadedPrefs;
   }
   
   public static class CurrentPreferences {
      private Color notCovered;
      private Color paritallyCovered;
      private Color totallyCovered;
      private CurrentPreferences(Color notCovered, Color paritallyCovered,
            Color totallyCovered) {
         this.notCovered = notCovered;
         this.paritallyCovered = paritallyCovered;
         this.totallyCovered = totallyCovered;
      }
      private Color getNotCovered() {
         return notCovered;
      }
      private Color getParitallyCovered() {
         return paritallyCovered;
      }
      private Color getTotallyCovered() {
         return totallyCovered;
      }
   }
   
   private static class DefaultPreferences extends CurrentPreferences {
      private DefaultPreferences() {
         super(Color.RED, Color.YELLOW, Color.GREEN);
      }
   }
   
   private enum PrefKey {
      NOT_COVERED, PARTIALLY_COVERED, TOTALLY_COVERED;
   }
}
