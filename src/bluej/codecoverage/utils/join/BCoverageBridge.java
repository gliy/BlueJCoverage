package bluej.codecoverage.utils.join;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import bluej.codecoverage.utils.serial.CoverageClass;
import bluej.codecoverage.utils.serial.CoveragePackage;
import bluej.extensions.BClass;
import bluej.extensions.BPackage;
import bluej.extensions.BProject;
import bluej.extensions.BlueJ;

/**
 * Connects the coverage information generated by
 * {@link bluej.codecoverage.utils.serial.CoverageBridge} with their source code.
 * 
 * @author ikingsbu
 * 
 */
public class BCoverageBridge
{

    /**
     * Goes through all open BlueJ projects, and attempts to join coverage information
     * with the BlueJ objects. If a mapping to a BlueJ object
     *  is not found for the coverage information, then the local filesystem is searched.
     * This is because BlueJ does not provide BPackages for packages that are not currently open in the GUI.
     * 
     * If the Filestystem search is unable to locate the file, then the coverage information is excluded.
     * @param packages all collected coverage information.
     * @param bluej the current bluej application
     * @return the mapped coverage information
     * @throws Exception
     */
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
                        String name = coverageClz.getName();
                        if(name.contains("/")) {
                            name = name.substring(name.lastIndexOf("/"));
                        }
                        System.out.println("looking for " + name);
                        BClass bclz = bpack.getBClass(name);
                        if (bclz != null)
                        {
                            // creates the mapping class, which adds itself to its parent.
                            new BCoverageClass(new BClassInfo(bclz), coverageClz, found);
                        }
                        else
                        {
                            System.err.println("No BClass found for "
                                + coverageClz.getName());
                        }
                    }
                    bcoverage.add(found);
                    break;
                } else {
                    for (CoverageClass coverageClz : coveragePkg.getClassCoverageInfo())
                    {
                        File sourceFile = findFile(project, coverageClz.getName());
                        System.out.println(sourceFile.getAbsolutePath());
                        if(sourceFile != null) {
                            if(found == null) {
                                found = new BCoveragePackage(null, coveragePkg);
                                bcoverage.add(found);
                            }
                            System.out.println("FOUND");

                            new BCoverageClass(
                                new FileClassInfo(sourceFile, sourceFile.getName()), coverageClz, 
                               found);
                            
                        }
                    }
                   
                  
                   
                }
            }
        }
        return bcoverage;
    }
    
    private static File findFile(BProject base, String name) throws Exception {
        File toGet = new File(base.getDir().getAbsolutePath() +"/"+ name + ".java");
        if(!toGet.exists()) {
            return null;
        }
        return toGet;
    }
}
