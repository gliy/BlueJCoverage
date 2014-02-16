package bluej.codecoverage.utils.serial;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**
 * Serializable representation of 
 * {@linkplain http://www.eclemma.org/jacoco/trunk/doc/api/org/jacoco/core/analysis/IClassCoverage.html}.
 * 
 * Can be used by BlueJ extensions that are using the provided ClassLoader.
 * 
 * @author Ian
 * 
 */
public class CoverageClass extends Coverage implements Serializable
{
    private static final long serialVersionUID = 5343197807669888739L;
    private String packageName;
    private String sourceFileName;
    private List<CoverageLine> lineCounter;
    private List<CoverageMethod> methodCounter;
    private List<CoverageClass> classCounter;
    
    private int firstLine;
    private int lastLine;
    
    private String[] interfaces;
    private String superClass;

    public CoverageClass(String packageName, String name,
        List<CoverageLine> lineCounter, int firstLine, int lastLine)
    {
        this();
        this.packageName = packageName;
        this.lineCounter = lineCounter;
        this.firstLine = firstLine;
        this.lastLine = lastLine;
        
        setName(name);
    }
    public CoverageClass() {
        this.interfaces = new String[0];
        this.superClass = "";
        this.methodCounter = new ArrayList<CoverageMethod>();
        this.classCounter = new ArrayList<CoverageClass>();
    }
    public List<CoverageMethod> getMethodCounter()
    {
        return methodCounter;
    }

    public void setMethodCounter(List<CoverageMethod> methodCounter)
    {
        this.methodCounter = methodCounter;
    }


    public List<CoverageLine> getLineCounter()
    {
        return lineCounter;
    }

    public void setLineCounter(List<CoverageLine> lineCounter)
    {
        this.lineCounter = lineCounter;
    }

    public void setPackageName(String packageName)
    {
        this.packageName = packageName;
    }


    public void setFirstLine(int firstLine)
    {
        this.firstLine = firstLine;
    }

    public void setLastLine(int lastLine)
    {
        this.lastLine = lastLine;
    }

    public List<CoverageClass> getclassCounter()
    {
        return classCounter;
    }
    public void addClass(CoverageClass clz)
    {
        classCounter.add(clz);
    }

    public String getPackageName()
    {
        return packageName;
    }

   
    public CoverageLine getLine(int lineNum)
    {
        return lineCounter.get(lineNum);
    }

    public int getFirstLine()
    {
        return firstLine;
    }

    public int getLastLine()
    {
        return lastLine;
    }

    public String getSourceFileName()
    {
        return sourceFileName;
    }

    public void setSourceFileName(String sourceFileName)
    {
        this.sourceFileName = sourceFileName;
    }
    public String[] getInterfaces()
    {
        return interfaces;
    }
    public void setInterfaces(String[] interfaces)
    {
        this.interfaces = interfaces;
    }
    public String getSuperClass()
    {
        return superClass;
    }
    public void setSuperClass(String superClass)
    {
        this.superClass = superClass;
    }

}
