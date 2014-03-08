package bluej.codecoverage.utils.serial;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * Serializable representation of {@linkplain http
 * ://www.eclemma.org/jacoco/trunk
 * /doc/api/org/jacoco/core/analysis/ICounter.html}.
 * 
 * Can be used by BlueJ extensions that are using the provided ClassLoader.
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

   public CoverageCounter(int covered, int missed, int total, int status,
         double coveredRatio, double missedRation) {
      this.covered = covered;
      this.missed = missed;
      this.total = total;
      this.status = status;
      this.coveredRatio = coveredRatio;
      coveredRatio = missedRation;
   }

}
