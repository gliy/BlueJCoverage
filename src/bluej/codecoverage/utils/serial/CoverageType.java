package bluej.codecoverage.utils.serial;

import javax.swing.ImageIcon;

import bluej.codecoverage.pref.PreferenceManager;
import bluej.codecoverage.ui.pref.CoveragePreferencePane;

/**
 * Joins Coverage information for Java Object's with their respective icon.
 * 
 * @author Ian
 * 
 */
public enum CoverageType {
   PACKAGE, CLASS, METHOD, ENUM;

   /** Icon that signifies the class type */
   private ImageIcon img;

   private CoverageType() {
      img = PreferenceManager.getImage(this, "png");
   }

   public ImageIcon getImage() {
      return img;
   }

   /**
    * Determines the CoverageType by checking if we have parsed the information into  a
    * {@link CoveragePackage}, {@link CoverageMethod}, or a {@link CoverageClass}.
    * @param node the information to find the type of.
    * @return the type and image for the information.
    */
   public static CoverageType findType(Coverage node) {

      if (node instanceof CoveragePackage) {
         return PACKAGE;
      } else if (node instanceof CoverageMethod) {
         return METHOD;
      } else {
         CoverageClass clz = (CoverageClass) node;
         if (clz.getSuperClass() != null
               && clz.getSuperClass().equals("java/lang/Enum")) {
            return ENUM;
         }
         return CLASS;
      }
   }
}
