package bluej.codecoverage.utils.serial;

import java.io.Serializable;

import org.jacoco.core.analysis.ICounter;

import lombok.Getter;
import lombok.Setter;

/**
 * Serializable representation of <a href=
 * "http://www.eclemma.org/jacoco/trunk/doc/api/org/jacoco/core/analysis/ICounter.html"
 * >ICounter</a>
 * 
 * 
 * This class can be used by BlueJ extensions that are using a different
 * ClassLoader through Serialization.
 * 
 * @author Ian
 * 
 */
@Getter
@Setter
public class CoverageCounter implements Serializable {

   private static final long serialVersionUID = 8372346998202557372L;
   private int covered;
   private int missed;
   private int total;
   private int status;

   private double coveredRatio;
   private double missedRatio;

   public CoverageCounter() {

   }
   
   public CoverageCounter(ICounter line) {
      this(line.getCoveredCount(), line.getMissedCount(),
            line.getTotalCount(), line.getStatus(), line.getCoveredRatio(),
            line.getMissedRatio());
   }

   public CoverageCounter(int covered, int missed, int total, int status,
         double coveredRatio, double missedRation) {
      this.covered = covered;
      this.missed = missed;
      this.total = total;
      this.status = status;
      this.coveredRatio = coveredRatio;
      this.missedRatio = missedRation;
   }

}
