package bluej.codecoverage.utils.join;

import java.util.ArrayList;
import java.util.List;

import bluej.codecoverage.utils.serial.CoverageClass;
import bluej.codecoverage.utils.serial.CoverageMethod;

public class BCoverageClass extends BCoverage<CoverageClass> implements
      Locatable {
   private ClassInfo classinfo;
   private BCoveragePackage parent;

   private List<BCoverageMethod> methods;
   private List<BCoverageClass> classes;

   public BCoverageClass(ClassInfo bclass, CoverageClass classCoverage) {
      super(classCoverage);
      this.classinfo = bclass;
      this.methods = new ArrayList<BCoverageMethod>();
      this.classes = new ArrayList<BCoverageClass>();
      for (CoverageMethod method : classCoverage.getMethodCounter()) {
         methods.add(new BCoverageMethod(method));
      }
      for (CoverageClass clz : classCoverage.getClassCounter()) {
         classes.add(new BCoverageClass(bclass, clz));
      }
   }

   public void setParent(BCoveragePackage parent) {
      this.parent = parent;
   }

   public BCoveragePackage getParent() {
      return parent;
   }

   public ClassInfo getClassInfo() {
      return classinfo;
   }

   @Override
   public String getName() {
      String name = src.getName();
      if (name.contains("/")) {
         name = name.substring(name.lastIndexOf("/") + 1);
      }
      return name;
   }

   @Override
   public List<? extends BCoverage<?>> getNodes() {
      List<BCoverage<?>> allNodes = new ArrayList<BCoverage<?>>();
      allNodes.addAll(methods);
      allNodes.addAll(classes);
      return allNodes;
   }

   public class BCoverageMethod extends BCoverage<CoverageMethod> implements
         Locatable {

      protected BCoverageMethod(CoverageMethod src) {
         super(src);
      }

      @Override
      public List<? extends BCoverage<?>> getNodes() {
         return new ArrayList<BCoverage<?>>();
      }

      @Override
      public int getFirstLine() {
         return src.getStartLine();
      }

   }

   @Override
   public int getFirstLine() {
      return src.getFirstLine();
   }
}
