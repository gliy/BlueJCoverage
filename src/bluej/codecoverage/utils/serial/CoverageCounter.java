package bluej.codecoverage.utils.serial;

import java.io.Serializable;

public class CoverageCounter implements Serializable
{

    /**
     * 
     */
    private static final long serialVersionUID = 8372346998202557372L;
    private int covered;
    private int missed;
    private int total;
    private int status;
    
    
    public CoverageCounter()
    {

    }
    public CoverageCounter(int covered, int missed, int total, int status)
    {
        this.covered = covered;
        this.missed = missed;
        this.total = total;
        this.status = status;
    }
    public int getCovered()
    {
        return covered;
    }
    public int getMissed()
    {
        return missed;
    }
    public int getTotal()
    {
        return total;
    }
    public int getStatus()
    {
        return status;
    }
    public void setCovered(int covered)
    {
        this.covered = covered;
    }
    public void setMissed(int missed)
    {
        this.missed = missed;
    }
    public void setTotal(int total)
    {
        this.total = total;
    }
    public void setStatus(int status)
    {
        this.status = status;
    }
    
    
    
    
    
}
