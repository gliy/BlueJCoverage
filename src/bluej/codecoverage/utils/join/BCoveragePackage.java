package bluej.codecoverage.utils.join;

import java.util.ArrayList;
import java.util.List;

import bluej.codecoverage.utils.serial.CoverageCounter;
import bluej.codecoverage.utils.serial.CoveragePackage;
import bluej.extensions.BPackage;

public class BCoveragePackage implements BCoverageInformation
{

    private BPackage bpackage;
    private CoveragePackage packageCoverage;
    private List<BCoverageClass> children;
    public BCoveragePackage(BPackage bpackage, CoveragePackage packageCoverage)
    {
        this.bpackage = bpackage;
        children = new ArrayList<BCoverageClass>();
        this.packageCoverage = packageCoverage;
    }
    public BPackage getBpackage()
    {
        return bpackage;
    }
    public CoveragePackage getPackageCoverage()
    {
        return packageCoverage;
    }
    public List<BCoverageClass> getChildren()
    {
        return children;
    }
    public void addChild(BCoverageClass child)
    {
        this.children.add(child);
    }
    @Override
    public CoverageCounter getObjectCoverage()
    {
        return packageCoverage.getClassCoverage();
    }
    @Override
    public String getName()
    {
        return packageCoverage.getName();
    }
    
    
    
    
}
