package bluej.codecoverage.utils.serial;

import java.io.Serializable;

public class CoverageLine implements Serializable
{


    private static final long serialVersionUID = -3807095603496099416L;
    private int status;
    private CoverageCounter branchCounter;
    public CoverageLine(int status, CoverageCounter branchCounter)
    {
        this.status = status;
        this.branchCounter = branchCounter;
    }
    public int getStatus()
    {
        return status;
    }
    public CoverageCounter getBranchCounter()
    {
        return branchCounter;
    }
    public CoverageLine()
    {
    }
    public void setStatus(int status)
    {
        this.status = status;
    }
    public void setBranchCounter(CoverageCounter branchCounter)
    {
        this.branchCounter = branchCounter;
    }
    
    
    
}
