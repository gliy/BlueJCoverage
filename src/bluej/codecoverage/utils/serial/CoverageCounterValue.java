package bluej.codecoverage.utils.serial;

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
