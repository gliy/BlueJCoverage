package bluej.codecoverage;

import java.util.Observable;

import javax.swing.JOptionPane;

import bluej.codecoverage.utils.CoverageUtilities;
import bluej.extensions.BClass;
import bluej.extensions.BlueJ;
import bluej.extensions.event.InvocationEvent;
import bluej.extensions.event.InvocationListener;

public class CoverageInvokationListener extends Observable implements InvocationListener
{
    private BClass bClass;
    private BlueJ bluej;
    private static CoverageInvokationListener listener;

    private CoverageInvokationListener(BClass bClass, BlueJ bluej)
    {
        super();
        this.bClass = bClass;
        this.bluej = bluej;

    }

    public static CoverageInvokationListener setup(BClass bClass, BlueJ bluej)
    {
        if (listener == null)
        {
            listener = new CoverageInvokationListener(bClass, bluej);
            bluej.addInvocationListener(listener);
        }
        else
        {
            listener.bClass = bClass;
        }
        return listener;
    }

    @Override
    public void invocationFinished(InvocationEvent event)
    {

        try
        {
            if (!bClass.getJavaClass()
                .getName()
                .equals(event.getClassName()))
            {
                return;
            }
            setChanged();
            notifyObservers(CoverageUtilities.get().getResults(bClass.getClassFile()));

        }
        catch (Exception ex)
        {
            JOptionPane.showMessageDialog(null, ex.getMessage());
            ex.printStackTrace();
        }
    }

}
