package bluej.codecoverage.utils.serial;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents Code coverage for a method.
 * <p>
 * This class must be contained in a {@link CoverageClass}, and cannot exist as
 * a top level element.
 * 
 * @author Ian
 * 
 */
@Setter
@Getter
public class CoverageMethod extends Coverage implements Serializable {

   private static final long serialVersionUID = -8818413595750521145L;
   private int firstLine;
   private int lastLine;

   /**
    * Constructs a new object containing the code coverage results for a method.
    * @param firstLine the line that the method starts on
    * @param lastLine the last line of coverage information collected for this method.
    */
   public CoverageMethod(int startLine, int lastLine) {
      super();
      this.firstLine = startLine;
      this.lastLine = lastLine;
   }

}
