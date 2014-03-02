package bluej.codecoverage.utils.serial;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Coverage implements Serializable {

   private static final long serialVersionUID = 8636473635240557385L;
   private String name;
   private CoverageCounter lineCoverage;
   private CoverageCounter methodCoverage;
   private CoverageCounter branchCoverage;
   private CoverageCounter classCoverage;
   private CoverageType type;

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
