package bluej.codecoverage.utils.join;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import bluej.codecoverage.utils.serial.CoverageCounter;
import bluej.codecoverage.utils.serial.CoveragePackage;

public class BCoveragePackage implements BCoverageInformation
{


    private CoveragePackage packageCoverage;
    private List<BCoverageClass> children;
    private String id;
    public BCoveragePackage(CoveragePackage packageCoverage, Collection<BCoverageClass> childrenToAdd)
    {
        this.children = new ArrayList<BCoverageClass>();
        this.packageCoverage = packageCoverage;
        id = UUID.randomUUID().toString();
        addChildren(childrenToAdd);
    }

    public CoveragePackage getPackageCoverage()
    {
        return packageCoverage;
    }
    @Override
    public List<? extends BCoverageInformation> getNodes()
    {
        return children;
    }
    public final void addChild(BCoverageClass child)
    {
        child.setParent(this);
        this.children.add(child);
    }
    public final void addChildren(Collection<BCoverageClass> children)
    {
        for(BCoverageClass child : children) {
            addChild(child);
        }
    }
    @Override
    public CoverageCounter getObjectCoverage()
    {
        return packageCoverage.getClassCoverage();
    }
    @Override
    public String getName()
    {
        String name = packageCoverage.getName();
        if(name.isEmpty()) {
            name = "<default>";
        }
        return name;
    }
    @Override
    public String getId()
    {
        return id;
    }
    
    
    
    
}
