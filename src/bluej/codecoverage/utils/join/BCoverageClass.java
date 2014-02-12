package bluej.codecoverage.utils.join;

import java.util.UUID;

import bluej.codecoverage.utils.serial.CoverageClass;
import bluej.codecoverage.utils.serial.CoverageCounter;
import bluej.extensions.BClass;

public class BCoverageClass implements BCoverageInformation
{
    private ClassInfo classinfo;
    private CoverageClass classCoverage;
    private BCoveragePackage parent;
    private String id;
    public BCoverageClass(ClassInfo bclass, CoverageClass classCoverage, BCoveragePackage parent)
    {
        
        this.classinfo = bclass;
        this.parent = parent;
        this.classCoverage = classCoverage;
        parent.addChild(this);
        id = UUID.randomUUID().toString();
    }
    public BCoveragePackage getParent() {
        return parent;
    }
    public ClassInfo getClassInfo()
    {
        return classinfo;
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
        return classinfo.getName();
    }
    @Override
    public String getId()
    {
        return id;
    }

    
    
    
    
}
