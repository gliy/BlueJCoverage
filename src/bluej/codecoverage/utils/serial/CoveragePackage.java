package bluej.codecoverage.utils.serial;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;

/**
 * Represents coverage information for an entire package, with an individual
 * {@link CoverageClass} for each class in the package. Also includes the sum
 * coverage of all classes in the package.
 * 
 * @author ikingsbu
 * 
 */
@Getter
public class CoveragePackage extends Coverage implements Serializable {

   private static final long serialVersionUID = 8070078380080374190L;
   /** List of classes contained in this package */
   private List<CoverageClass> classCoverageInfo;

   /**
    * Constructs a new CoveragePackage with a list of the classes and their
    * coverage information contained inside of it.
    * <p>
    * 
    * @param classCoverageInfo
    *           classes contained in this package.
    */
   public CoveragePackage(List<CoverageClass> classCoverageInfo) {
      this.classCoverageInfo = classCoverageInfo;

   }

}
