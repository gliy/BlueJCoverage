package bluej.codecoverage.utils.serial;

import java.awt.Image;

import javax.swing.ImageIcon;

import bluej.codecoverage.pref.CoveragePrefManager.CurrentPreferences;

public enum CoverageType {
   PACKAGE, CLASS, METHOD, ENUM;

   private ImageIcon img;
   
   private CoverageType() {
      img = CurrentPreferences.getImage(this, "png");
   }
   public ImageIcon getImage() {
      return img;
   }
   
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
