package bluej.codecoverage.utils.join;

import bluej.codecoverage.utils.serial.CoverageCounter;

public interface BCoverageInformation
{
    public CoverageCounter getObjectCoverage();
    public String getName();
}
