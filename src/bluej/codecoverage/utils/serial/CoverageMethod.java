package bluej.codecoverage.utils.serial;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CoverageMethod extends Coverage implements Serializable
{
 
    private static final long serialVersionUID = -8818413595750521145L;
    private int startLine;

    public CoverageMethod(int startLine)
    {
        super();
        this.startLine = startLine;
    }

    
}
