package bluej.codecoverage.utils.serial;

import java.util.ArrayList;
import java.util.List;

import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.analysis.ILine;
import org.jacoco.core.analysis.IPackageCoverage;

/**
 * Extracts needed information from Jacoco's coverage, and turns it into 
 * a serializable representation.
 * @author ikingsbu
 *
 */
public class CoverageBridge
{

    public static CoveragePackage toSerializable(IPackageCoverage pkg)
    {
        List<CoverageClass> classes = new ArrayList<CoverageClass>();
        for (IClassCoverage coverage : pkg.getClasses())
        {
            classes.add(toSerializable(coverage));
        }

        CoveragePackage pkgCoverage = new CoveragePackage(
            toCounter(pkg.getLineCounter()), classes, pkg.getName());
        return pkgCoverage;
    }

    private static CoverageClass toSerializable(IClassCoverage clz)
    {
        List<CoverageLine> lines = new ArrayList<CoverageLine>();
        int first = clz.getFirstLine();
        int last = clz.getLastLine();
        for (int lineNum = first; lineNum < last; lineNum++)
        {
            ILine iline = clz.getLine(lineNum);
            lines.add(toLine(iline));
            System.out.println(lineNum + ": " + CoverageCounterValue.from(iline.getStatus()));
        }

        CoverageClass rtn = new CoverageClass();
        rtn.setLineCounter(lines);
        rtn.setFirstLine(first);
        rtn.setLastLine(last);
        rtn.setName(clz.getName());
        rtn.setPackageName(clz.getPackageName());
        rtn.setTotalCoverage(toCounter(clz.getLineCounter()));
        return rtn;
    }

    private static CoverageLine toLine(ILine line)
    {
        ICounter branch = line.getBranchCounter();
        int status = line.getStatus();

        return new CoverageLine(status, toCounter(line.getBranchCounter()));
    }

    private static CoverageCounter toCounter(ICounter line)
    {
        return new CoverageCounter(line.getCoveredCount(), line.getMissedCount(),
            line.getTotalCount(), line.getStatus());
    }
}
