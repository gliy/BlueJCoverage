package bluej.codecoverage.utils.serial;
/**
 * Serializable representation of 
 * {@linkplain http://www.eclemma.org/jacoco/trunk/doc/api/org/jacoco/core/analysis/ICounter.CounterValue.html}.
 * 
 * Can be used by BlueJ extensions that are using the provided ClassLoader.
 * 
 * @author Ian
 * 
 */
public enum CoverageCounterValue
{
    FULLY_COVERED(2), NOT_COVERED(1), EMPTY(0), PARTLY_COVERED(1 | 2), UNKNOWN(-1);
    private int intValue;

    private CoverageCounterValue(int intValue)
    {
        this.intValue = intValue;
    }

    public static CoverageCounterValue from(int value)
    {
        for (CoverageCounterValue val : values())
        {
            if (val.intValue == value)
            {
                return val;
            }
        }
        return UNKNOWN;
    }

}
