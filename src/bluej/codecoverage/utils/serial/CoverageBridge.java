package bluej.codecoverage.utils.serial;

import java.util.ArrayList;
import java.util.List;

import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.analysis.ILine;

public class CoverageBridge
{

    public static CoverageClass toSerializable(IClassCoverage clz) {
        List<CoverageLine> lines = new ArrayList<CoverageLine>();
        int first = clz.getFirstLine();
        int last = clz.getLastLine();
        for(int lineNum = first; lineNum < last; lineNum++) {
            ILine iline = clz.getLine(lineNum);
            
            lines.add(toLine(iline));
        }
        
        CoverageClass rtn = new CoverageClass();
        rtn.setLineCounter(lines);
        rtn.setFirstLine(first);
        rtn.setLastLine(last);
        rtn.setName(clz.getName());
        rtn.setPackageName(clz.getPackageName());
        
        return rtn;
    }

    private static CoverageLine toLine(ILine line)
    {
        ICounter branch = line.getBranchCounter();
        int status = line.getStatus();
        return new CoverageLine(status, new CoverageCounter(
            branch.getCoveredCount(), branch.getMissedCount(),
            branch.getTotalCount(), branch.getStatus()));
    }
}
