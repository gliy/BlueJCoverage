package bluej.codecoverage.utils.serial;

import java.io.Serializable;
import java.util.List;

public class CoverageClass implements Serializable
{

    /**
     * 
     */
    private static final long serialVersionUID = -1459075392091370960L;
    private String packageName;
    private String name;

    private List<CoverageLine> lineCounter;
    private int firstLine;
    private int lastLine;

    public CoverageClass()
    {
        
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

    public void setName(String name)
    {
        this.name = name;
    }

    public void setFirstLine(int firstLine)
    {
        this.firstLine = firstLine;
    }

    public void setLastLine(int lastLine)
    {
        this.lastLine = lastLine;
    }

    public CoverageClass(String packageName, String name,
        List<CoverageLine> lineCounter, int firstLine, int lastLine)
    {
        this.packageName = packageName;
        this.name = name;
        this.lineCounter = lineCounter;
        this.firstLine = firstLine;
        this.lastLine = lastLine;
    }

    public String getPackageName()
    {
        return packageName;
    }

    public String getName()
    {
        return name;
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

}
