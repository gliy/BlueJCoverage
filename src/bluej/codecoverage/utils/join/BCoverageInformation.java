package bluej.codecoverage.utils.join;

import bluej.codecoverage.utils.serial.CoverageCounter;

/**
 * Basic interface for coverage information that allows users
 * to treat package coverage and class coverage the same.
 * @author ikingsbu
 *
 */
public interface BCoverageInformation
{
    public CoverageCounter getObjectCoverage();
    public String getName();
}
