package bluej.codecoverage.utils.join;

import java.util.ArrayList;
import java.util.List;

import bluej.codecoverage.utils.serial.CoverageClass;
import bluej.codecoverage.utils.serial.CoveragePackage;
import bluej.extensions.BClass;
import bluej.extensions.BPackage;
import bluej.extensions.BProject;
import bluej.extensions.BlueJ;

public class BCoverageBridge
{

    public static List<BCoveragePackage> toBCoverage(List<CoveragePackage> packages,
        BlueJ bluej) throws Exception
    {
        List<BCoveragePackage> bcoverage = new ArrayList<BCoveragePackage>();

        BProject[] allProjects = bluej.getOpenProjects();
        for (CoveragePackage coveragePkg : packages)
        {
            BCoveragePackage found = null;
            for (BProject project : allProjects)
            {

                BPackage bpack = project.getPackage(coveragePkg.getName());
                if (bpack != null)
                {
                    found = new BCoveragePackage(bpack, coveragePkg);
                    for (CoverageClass coverageClz : coveragePkg.getClassCoverageInfo())
                    {
                        BClass bclz = bpack.getBClass(coverageClz.getName());
                        if (bclz != null)
                        {
                            new BCoverageClass(bclz, coverageClz, found);
                        }
                        else
                        {
                            System.err.println("No BClass found for "
                                + coverageClz.getName());
                        }
                    }
                    bcoverage.add(found);
                    break;
                }
            }
        }
        return bcoverage;
    }
}
