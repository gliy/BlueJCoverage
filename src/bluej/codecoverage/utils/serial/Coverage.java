package bluej.codecoverage.utils.serial;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents the common elements of all code coverage elements.
 * <p>
 * A default Constructor is provided due to the large number of parameters
 * required, as well as serialization.
 * 
 * @author Ian
 * 
 */
@Getter
@Setter
public abstract class Coverage implements Serializable {

   private static final long serialVersionUID = 8636473635240557385L;
   /**
    * Identifies the Class that the coverage information is for. This should be
    * the class name
    */
   private String name;
   /** Counter with the coverage metrics for lines in a class file */
   private CoverageCounter lineCoverage;

   /** Counter with the coverage metrics for methods in a class file */
   private CoverageCounter methodCoverage;

   /** Counter with the coverage metrics for branchs in a class file */
   private CoverageCounter branchCoverage;

   /** Counter with the coverage metrics for classes in a class file */
   private CoverageCounter classCoverage;
   /** Enum representing what Java Object Type this information is for. */
   private CoverageType type;

   /**
    * Fills in all the coverage information that is shared between the different
    * types.
    * 
    * @param name
    * @param lineCoverage
    * @param methodCoverage
    * @param branchCoverage
    * @param classCoverage
    */
   protected Coverage(String name, CoverageCounter lineCoverage,
         CoverageCounter methodCoverage, CoverageCounter branchCoverage,
         CoverageCounter classCoverage) {
      super();
      this.name = name;
      this.lineCoverage = lineCoverage;
      this.methodCoverage = methodCoverage;
      this.branchCoverage = branchCoverage;
      this.classCoverage = classCoverage;

   }

   protected Coverage() {

   }

}
