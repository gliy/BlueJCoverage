package bluej.codecoverage.utils.join;

import java.util.List;
import java.util.UUID;

import bluej.codecoverage.utils.serial.Coverage;
import bluej.codecoverage.utils.serial.CoverageCounter;

/**
 * Basic interface for coverage information that allows users to treat package coverage
 * and class coverage the same.
 * 
 * @author ikingsbu
 * 
 */
public abstract class BCoverage<E extends Coverage>
{
    protected final String id;
    protected E src;

    protected BCoverage(E src)
    {
        this.id = UUID.randomUUID().toString();
        this.src = src;
    }

    public String getName()
    {
        return src.getName();
    }

    public CoverageCounter getLineCoverage()
    {
        return src.getLineCoverage();
    }

    public CoverageCounter getMethodCoverage()
    {
        return src.getMethodCoverage();
    }

    public CoverageCounter getBranchCoverage()
    {
        return src.getBranchCoverage();
    }

    public CoverageCounter getClassCoverage()
    {
        return src.getClassCoverage();
    }

    public String getId()
    {
        return id;
    }
    public E getSource() {
        return src;
    }
    public abstract List<? extends BCoverage<?>> getNodes();
}
