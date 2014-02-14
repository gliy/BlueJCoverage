package bluej.codecoverage.utils.join;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import bluej.codecoverage.utils.serial.CoverageClass;
import bluej.codecoverage.utils.serial.CoverageCounter;

public class BCoverageClass implements BCoverageInformation
{
    private ClassInfo classinfo;
    private CoverageClass classCoverage;
    private BCoveragePackage parent;
    private String id;
    public BCoverageClass(ClassInfo bclass, CoverageClass classCoverage)
    {
        
        this.classinfo = bclass;
        this.classCoverage = classCoverage;
        id = UUID.randomUUID().toString();
    }
    public void setParent(BCoveragePackage parent) {
        this.parent = parent;
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
        String name = classinfo.getName();
        if(name.contains("/")) {
            name = name.substring(name.lastIndexOf("/") + 1);
        }
        return name;
    }
    @Override
    public String getId()
    {
        return id;
    }
    @Override
    public List<? extends BCoverageInformation> getNodes()
    {
        return new ArrayList<BCoverageInformation>();
    }

    
    
    
    
}
