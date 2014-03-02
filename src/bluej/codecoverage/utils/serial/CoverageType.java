package bluej.codecoverage.utils.serial;

public enum CoverageType {
   PACKAGE, CLASS, METHOD, ENUM;

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
