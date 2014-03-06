package bluej.codecoverage.utils.serial;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * Serializable representation of {@linkplain http 
 * ://www.eclemma.org/jacoco/trunk/doc/api/org/jacoco/core/analysis/ILine.html}.
 * 
 * Can be used by BlueJ extensions that are using the provided ClassLoader.
 * 
 * @author Ian
 * 
 */
@Getter
@Setter
public class CoverageLine implements Serializable {

   private static final long serialVersionUID = -3807095603496099416L;
   private int status;
   private CoverageCounter branchCounter;
   private CoverageBranch branchCoverageType;
   public CoverageLine(int status, CoverageCounter branchCounter) {
      this.status = status;
      this.branchCounter = branchCounter;
      this.branchCoverageType = CoverageBranch.fromCounter(branchCounter);
   }

   @Override
   public String toString() {
      return "CoverageLine[status=" + CoverageCounterValue.from(status) + "]";
   }

}
