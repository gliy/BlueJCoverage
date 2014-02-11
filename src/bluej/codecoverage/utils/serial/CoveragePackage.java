package bluej.codecoverage.utils.serial;

import java.io.Serializable;
import java.util.List;

/**
 * Represents coverage information for an entire package, with an individual
 * {@link CoverageClass} for each class in the package. Also includes the sum coverage of
 * all classes in the package.
 * 
 * @author ikingsbu
 * 
 */
public class CoveragePackage implements Serializable
{

    private static final long serialVersionUID = 8070078380080374190L;
    private CoverageCounter classCoverage;
    private List<CoverageClass> classCoverageInfo;
    private String name;

    public CoveragePackage(CoverageCounter classCoverage,
        List<CoverageClass> classCoverageInfo, String name)
    {
        this.name = name;
        this.classCoverage = classCoverage;
        this.classCoverageInfo = classCoverageInfo;
    }

    public CoverageCounter getClassCoverage()
    {
        return classCoverage;
    }

    public List<CoverageClass> getClassCoverageInfo()
    {
        return classCoverageInfo;
    }

    public String getName()
    {
        return name;
    }

}
