package bluej.codecoverage.utils.serial;

import java.io.Serializable;
/**
 * Serializable representation of 
 * {@linkplain http://www.eclemma.org/jacoco/trunk/doc/api/org/jacoco/core/analysis/ICounter.html}.
 * 
 * Can be used by BlueJ extensions that are using the provided ClassLoader.
 * 
 * @author Ian
 * 
 */
public class CoverageCounter implements Serializable
{

    private static final long serialVersionUID = 8372346998202557372L;
    private int covered;
    private int missed;
    private int total;
    private int status;
    
    private double coveredRatio;
    private double missedRatio;
    
    public CoverageCounter()
    {

    }
    public CoverageCounter(int covered, int missed, int total, int status, double coveredRatio, double missedRation)
    {
        this.covered = covered;
        this.missed = missed;
        this.total = total;
        this.status = status;
        this.coveredRatio = coveredRatio;
        coveredRatio = missedRation;
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
    public double getCoveredRatio()
    {
        return coveredRatio;
    }
    public void setCoveredRatio(double coveredRatio)
    {
        this.coveredRatio = coveredRatio;
    }
    public double getMissedRatio()
    {
        return missedRatio;
    }
    public void setMissedRatio(double missedRatio)
    {
        this.missedRatio = missedRatio;
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
