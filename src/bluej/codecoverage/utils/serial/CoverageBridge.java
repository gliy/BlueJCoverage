package bluej.codecoverage.utils.serial;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.analysis.ICoverageNode;
import org.jacoco.core.analysis.ILine;
import org.jacoco.core.analysis.IMethodCoverage;
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
        classes = merge(classes);
        CoveragePackage pkgCoverage = new CoveragePackage(classes);
        fillBase(pkg, pkgCoverage);
        return pkgCoverage;
    }

    private static List<CoverageClass> merge(List<CoverageClass> classes)
    {
        List<CoverageClass> innerClasses = new ArrayList<CoverageClass>();
        Map<String, CoverageClass> byFileName = new HashMap<String, CoverageClass>();
        for (CoverageClass clz : classes)
        {
            if(clz.getName().equals(clz.getSourceFileName().substring(0, clz.getSourceFileName().indexOf(".java")))) {
                byFileName.put(clz.getSourceFileName(), clz);
            } else {
                innerClasses.add(clz);
            }
                
        }
        for(CoverageClass inner : innerClasses) {
            inner.setName(findName(inner));
            CoverageClass parent = byFileName.get(inner.getSourceFileName());
            if(parent == null) {
                byFileName.put(inner.getSourceFileName(), inner);
            } else {
                parent.addClass(inner);
            }
        }
        return new ArrayList<CoverageClass>(byFileName.values());
    }
    private static String findName(CoverageClass inner) {
        String newName = inner.getName();
        if(!inner.getSuperClass().equals("java/lang/Object")) {
            newName = inner.getSuperClass();
        } else if(inner.getInterfaces().length > 0) {
            newName = inner.getInterfaces()[0];
        }
       return newName;
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
  
        }
        CoverageClass rtn = new CoverageClass();
        rtn.setLineCounter(lines);
        rtn.setFirstLine(first);
        System.out.println(clz.getLineCounter().getCoveredCount());
        rtn.setLastLine(last);
        rtn.setSourceFileName(clz.getSourceFileName());
        rtn.setPackageName(clz.getPackageName());
        rtn.setMethodCounter(toMethods(clz.getMethods()));
        rtn.setInterfaces(clz.getInterfaceNames());
        rtn.setSuperClass(clz.getSuperName());
        fillBase(clz, rtn);
       
        return rtn;
    }

    private static void fillBase(ICoverageNode src, Coverage dest) {
        dest.setName(src.getName());      
        dest.setLineCoverage(toCounter(src.getLineCounter()));
        dest.setBranchCoverage(toCounter(src.getBranchCounter()));
        dest.setMethodCoverage(toCounter(src.getMethodCounter()));
        dest.setClassCoverage(toCounter(src.getClassCounter()));
    }
    private static List<CoverageMethod> toMethods(Collection<IMethodCoverage> methods)
    {
        ArrayList<CoverageMethod> rtn = new ArrayList<CoverageMethod>();
        for (IMethodCoverage method : methods)
        {
            CoverageMethod coverageMethod = new CoverageMethod(
                method.getFirstLine());
            fillBase(method, coverageMethod);
            rtn.add(coverageMethod);

        }
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
            line.getTotalCount(), line.getStatus(), line.getCoveredRatio(), line.getMissedRatio());
    }
}
