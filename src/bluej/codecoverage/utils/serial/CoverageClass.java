package bluej.codecoverage.utils.serial;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Serializable representation of <a href=
 * "http://www.eclemma.org/jacoco/trunk/doc/api/org/jacoco/core/analysis/IClassCoverage.html"
 * >IClassCoverage</a>
 * 
 * This class can be used by BlueJ extensions that are using a different
 * ClassLoader through Serialization.
 * 
 * @author Ian
 * 
 */
@EqualsAndHashCode(callSuper=true)
@Getter
@Setter
public class CoverageClass extends Coverage implements Serializable {
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
         List<CoverageLine> lineCounter, int firstLine, int lastLine) {
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

   public void addClass(CoverageClass clz) {
      classCounter.add(clz);
   }

   public CoverageLine getLine(int lineNum) {
      return lineCounter.get(lineNum);
   }

}
