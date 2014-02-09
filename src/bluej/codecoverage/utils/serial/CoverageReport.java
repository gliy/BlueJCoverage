package bluej.codecoverage.utils.serial;

import org.jacoco.core.analysis.IClassCoverage;

import bluej.extensions.BClass;
/**
 * This class joins coverage information with the bluej class.
 * It is used by classes to display the coverage information.
 * @author Ian
 *
 */
public class CoverageReport
{

    private String className;
    private BClass bClass;        
    private IClassCoverage coverage;
    
    
    public CoverageReport(String className, IClassCoverage coverage)
    {
        this.className = className;
        this.coverage = coverage;
        // find bclass
    }
    public String getClassName()
    {
        return className;
    }
    public BClass getbClass()
    {
        return bClass;
    }
    public IClassCoverage getCoverage()
    {
        return coverage;
    }
    
    
}
