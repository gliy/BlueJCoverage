package bluej.codecoverage.utils.serial;

import lombok.Getter;

/**
 * Serializable representation of <a href=
 * "http://www.eclemma.org/jacoco/trunk/doc/api/org/jacoco/core/analysis/ICounter.CounterValue.html"
 * >CounterValue</a>.
 * 
 * This class can be used by BlueJ extensions that are using a different
 * ClassLoader through Serialization.
 * 
 * @author Ian
 * 
 */
@Getter
public enum CoverageCounterValue {
   FULLY_COVERED(2), NOT_COVERED(1), EMPTY(0), PARTLY_COVERED(1 | 2), UNKNOWN(
         -1);
   private int intValue;

   private CoverageCounterValue(int intValue) {
      this.intValue = intValue;
   }

   public static CoverageCounterValue from(int value) {
      for (CoverageCounterValue val : values()) {
         if (val.intValue == value) {
            return val;
         }
      }
      return UNKNOWN;
   }

}
