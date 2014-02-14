package bluej.codecoverage.ui.ext;

import bluej.codecoverage.utils.serial.CoverageCounter;
import bluej.codecoverage.utils.serial.CoverageLine;

public class DefaultLineToolTip implements LineToolTip
{

    @Override
    public String getToolTip(int lineNum, CoverageLine line)
    {
        String rtn = null;
        CoverageCounter branches = line.getBranchCounter();
        if(branches.getTotal() != 0) {
            if(branches.getMissed() > 0) {
                rtn = String.format("%d of %d branches missed", branches.getMissed(), branches.getTotal());
            }
            else {
                rtn = "All " + branches.getTotal() + " covered";
            }
            
        }
        return rtn;
    }

}
