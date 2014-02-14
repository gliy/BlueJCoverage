package bluej.codecoverage.pref;

import java.util.Arrays;
import java.util.List;

import bluej.codecoverage.ui.ext.DefaultLineAttributes;
import bluej.codecoverage.ui.ext.DefaultLineToolTip;
import bluej.codecoverage.ui.ext.LineAttributes;
import bluej.codecoverage.ui.ext.LineToolTip;

public class StandardGUIPrefs
{

    private LineAttributes attributes;
    private LineToolTip tooltip;

    public StandardGUIPrefs()
    {
        attributes = new DefaultLineAttributes();
        tooltip = new DefaultLineToolTip();
    }

    public List<LineAttributes> getAttributes()
    {
        return Arrays.asList(attributes);
    }

    public LineToolTip getTooltip()
    {
        return tooltip;
    }

}
