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
public class CoveragePackage extends Coverage implements Serializable
{

    private static final long serialVersionUID = 8070078380080374190L;
    private List<CoverageClass> classCoverageInfo;

    public CoveragePackage(
        List<CoverageClass> classCoverageInfo)
    {
        this.classCoverageInfo = classCoverageInfo;

    }

  
    public List<CoverageClass> getClassCoverageInfo()
    {
        return classCoverageInfo;
    }


}
