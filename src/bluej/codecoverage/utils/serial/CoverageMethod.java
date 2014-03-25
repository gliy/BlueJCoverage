package bluej.codecoverage.utils.serial;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents Code coverage for a method.
 * 
 * @author Ian
 *
 */
@Setter
@Getter
public class CoverageMethod extends Coverage implements Serializable {

   private static final long serialVersionUID = -8818413595750521145L;
   private int startLine;
   private int lastLine;
   public CoverageMethod(int startLine, int lastLine) {
      super();
      this.startLine = startLine;
      this.lastLine = lastLine;
   }

}
