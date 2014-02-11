package bluej.codecoverage.utils.join;

import bluej.codecoverage.utils.serial.CoverageClass;
import bluej.codecoverage.utils.serial.CoverageCounter;
import bluej.extensions.BClass;

public class BCoverageClass implements BCoverageInformation
{
    private BClass bclass;
    private CoverageClass classCoverage;
    private BCoveragePackage parent;
    public BCoverageClass(BClass bclass, CoverageClass classCoverage, BCoveragePackage parent)
    {
        this.bclass = bclass;
        this.parent = parent;
        this.classCoverage = classCoverage;
        parent.addChild(this);
    }
    public BCoveragePackage getParent() {
        return parent;
    }
    public BClass getBclass()
    {
        return bclass;
    }

    public CoverageClass getClassCoverage()
    {
        return classCoverage;
    }
    @Override
    public CoverageCounter getObjectCoverage()
    {
       return classCoverage.getTotalCoverage();
    }
    @Override
    public String getName()
    {
        return bclass.getName();
    }

    
    
    
    
}
