package bluej.codecoverage.utils.join;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import bluej.codecoverage.utils.serial.CoverageClass;
import bluej.codecoverage.utils.serial.CoveragePackage;

public class BCoveragePackage extends BCoverage<CoveragePackage> {

   private List<BCoverageClass> children;

   public BCoveragePackage(CoveragePackage packageCoverage,
         Collection<BCoverageClass> childrenToAdd) {
      super(packageCoverage);
      this.children = new ArrayList<BCoverageClass>();

      addChildren(childrenToAdd);
   }

   @Override
   public List<? extends BCoverage<CoverageClass>> getNodes() {
      return children;
   }

   public final void addChild(BCoverageClass child) {
      child.setParent(this);
      this.children.add(child);
   }

   public final void addChildren(Collection<BCoverageClass> children) {
      for (BCoverageClass child : children) {
         addChild(child);
      }
   }

   @Override
   public String getName() {
      String name = super.getName();
      if (name.isEmpty()) {
         name = "<default>";
      }
      return name;
   }

   @Override
   public String getId() {
      return id;
   }

}
