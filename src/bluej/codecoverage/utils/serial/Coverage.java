package bluej.codecoverage.utils.serial;

import java.io.Serializable;

public abstract class Coverage implements Serializable
{
    
    private static final long serialVersionUID = 8636473635240557385L;
    private String name;
    private CoverageCounter lineCoverage;
    private CoverageCounter methodCoverage;
    private CoverageCounter branchCoverage;
    private CoverageCounter classCoverage;
    protected Coverage(String name, CoverageCounter lineCoverage,
        CoverageCounter methodCoverage, CoverageCounter branchCoverage,
        CoverageCounter classCoverage)
    {
        super();
        this.name = name;
        this.lineCoverage = lineCoverage;
        this.methodCoverage = methodCoverage;
        this.branchCoverage = branchCoverage;
        this.classCoverage = classCoverage;
    }
    protected Coverage() {
        
    }
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public CoverageCounter getLineCoverage()
    {
        return lineCoverage;
    }
    public void setLineCoverage(CoverageCounter lineCoverage)
    {
        this.lineCoverage = lineCoverage;
    }
    public CoverageCounter getMethodCoverage()
    {
        return methodCoverage;
    }
    public void setMethodCoverage(CoverageCounter methodCoverage)
    {
        this.methodCoverage = methodCoverage;
    }
    public CoverageCounter getBranchCoverage()
    {
        return branchCoverage;
    }
    public void setBranchCoverage(CoverageCounter branchCoverage)
    {
        this.branchCoverage = branchCoverage;
    }
    public CoverageCounter getClassCoverage()
    {
        return classCoverage;
    }
    public void setClassCoverage(CoverageCounter classCoverage)
    {
        this.classCoverage = classCoverage;
    }
    
    
 
    
}
